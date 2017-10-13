package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.ErrorsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.HybrisRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.Errors;
import cucumber.api.Scenario;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SCENARIO;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.TRANSACTION_ID;
import static com.hybris.easyjet.config.SerenityFacade.getTestDataFromSpring;
import static com.hybris.easyjet.config.constants.MockTransactionIdentifiers.customXClientTransactionIds;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static net.serenitybdd.rest.RestRequests.given;

/**
 * Created by daniel on 26/11/2016.
 * the hybris service superclass provides all of the common functionality for any service
 */
public abstract class HybrisService implements IService {
    private static final Logger LOG = LogManager.getLogger(HybrisService.class);

    private SerenityFacade testData = SerenityFacade.getTestDataFromSpring();

    public static final String[] ALLOWABLE_XML_CHANNELS = new String[]{
            "PublicApiMobile",
            "PublicApiB2B"
    };

    public static final ThreadLocal<String> theJSessionCookie = new ThreadLocal<>();

    public static final ThreadLocal<String> theRememberMeCookie = new ThreadLocal<>();

    protected final HybrisRequest request;

    protected final String endPoint;

    protected RequestSpecification restRequest;

    @Getter
    @Setter
    protected Response restResponse;

    protected boolean successful;

    private Errors errors;

    private long startTime;

    private static final String JSESSIONID = "JSESSIONID";

    private static final String REMEMBER_ME = "REMEMBER_ME";

    /**
     * a service comprises a request, a client and an endpoint
     *
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    protected HybrisService(IRequest request, String endPoint) {
        this.request = (HybrisRequest) request;
        this.endPoint = endPoint;
    }

    protected abstract void checkThatResponseBodyIsPopulated();

    protected abstract void mapResponse();

    /**
     * adds the query parameters
     * adds the path parameters
     * adds the headers
     * invokes the service call
     * sets the success state based upon the return type
     * maps the response to either errors or the valid response domain model and verifies mapping has been successful
     */
    @Override
    public void invoke() {
        restRequest = given().config(
                RestAssured.config().encoderConfig(
                        encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)
                )
        ).log().all().baseUri(endPoint);

        if (request.getQueryParameters() != null) {
            restRequest.queryParams(request.getQueryParameters().getParameters());
        }
        if (request.getPathParameters() != null) {
            restRequest.basePath(request.getPathParameters().get());
        }
        restResponse = addHeadersAndExecuteRequest().then().log().all().extract().response();

        if (Objects.nonNull(restResponse.getCookie(JSESSIONID))) {
            theJSessionCookie.set(restResponse.getCookie(JSESSIONID));
        }

        if (Objects.nonNull(restResponse.getCookie(REMEMBER_ME))) {
            theRememberMeCookie.set(restResponse.getCookie(REMEMBER_ME));
        }

        stopTheClock();
        setSuccessState();
        if (successful) {
            mapResponse();
            checkThatResponseBodyIsPopulated();
        } else {
            mapErrors();
        }
    }

    protected void checkThatResponseBodyIsPopulated(Object expectedResponseContent) {
        Assert.assertNotNull(
                "The message body was not populated but the service reported a " +
                        restResponse.getStatusCode() +
                        " " +
                        restResponse.getStatusLine(),
                expectedResponseContent
        );
    }

    private void startTheClock() {
        startTime = System.currentTimeMillis();
    }

    private void stopTheClock() {
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        LOG.info("The service " + this.getClass().toString() + " responded in " + elapsedTime + "ms." + "Call was to " + restResponse.toString());
    }

    /**
     * Add headers and execute the request
     *
     * @return The response from the request.
     */
    private Response addHeadersAndExecuteRequest() {

        //Update the xClientTransactionId with a new dynamic value
        if (!customXClientTransactionIds.contains(request.getHeaders().getXClientTransactionId())) {
            request.getHeaders().setXClientTransactionId(HybrisHeaders.getNewClientTransactionIdHeaderValue());
        }

        //Set xClientTransactionId if provided to work with mock
        if (testData.keyExist(TRANSACTION_ID)) {
            request.getHeaders().setXClientTransactionId(testData.getData(TRANSACTION_ID));
        }

        // Add all the headers.
        request.getHeaders().get().forEach((key, value) -> restRequest.header(key, value));

        // Set the cookie and default content type.
        if (Objects.nonNull(theJSessionCookie.get())) {
            restRequest.cookie(JSESSIONID, theJSessionCookie.get());
        }

        // If we're sending XML and we're on an allowable channel
        // then set the content type appropriately otherwise use JSON.
        if ("true".equals(System.getProperty("AsXml", "false"))
                && Arrays.asList(ALLOWABLE_XML_CHANNELS).contains(request.getHeaders().getXPosId())
                ) {
            restRequest.contentType(ContentType.XML);
        } else {
            // Set the content type to be JSON.
            restRequest.contentType(ContentType.JSON);
        }

        // Set the request body if it's relevant.
        if (request.getRequestBody() != null) {
            restRequest.body(request.getRequestBody());
        }

        // Prepare a response (cyclomatic complexity)
        Response response;

        startTheClock();
        // Rest Assured 3 could do this much more succinctly.
        switch (request.getHttpMethod()) {
            case GET:
                response = restRequest.when().get();
                break;
            case PUT:
                response = restRequest.when().put();
                break;
            case POST:
                response = restRequest.when().post();
                break;
            case DELETE:
                response = restRequest.when().delete();
                break;
            default:
                response = restRequest.when().get().then().log().all().extract().response();
                break;
        }

        // Return the response.
        return response;
    }

    /**
     * Map the errors to Error objects.
     *
     * @see Errors
     */
    private void mapErrors() {
        try {
            errors = restResponse.as(Errors.class);
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    /**
     * provides a wrapper for asserting the contents of any errors
     *
     * @return the error assertion object which provides fluent reusable assertions
     */
    @Override
    public ErrorsAssertion assertThatErrors() {
        assertThatServiceCallWasNotSuccessful();
        return new ErrorsAssertion(errors);
    }

    /**
     * Asserts the successful property to see if the request was a success.
     */
    protected void assertThatServiceCallWasSuccessful() {

        Scenario scenario = getTestDataFromSpring().getData(SCENARIO);
        if (!scenario.getSourceTagNames().contains("@negative")) {
            Assert.assertNotNull(restResponse);
            Assert.assertTrue(
                    "The service was not called successfully.  Response type was: " +
                            restResponse.getStatusCode() +
                            ". The error reported was: " +
                            (Objects.nonNull(errors) ? getMessageError() : "") +
                            ". Endpoint: " + restResponse.getStatusLine(),
                    successful
            );
        }
    }

    /**
     * Asserts the successful property to see if the request was NOT a success.
     */
    protected void assertThatServiceCallWasNotSuccessful() {
        Assert.assertFalse(
                "The service returned a: " +
                        restResponse.getStatusCode() +
                        ":" +
                        restResponse.getStatusLine(),
                successful
        );
    }

    /**
     * Get all the errors as one string.
     *
     * @return The error string.
     */
    private String getMessageError() {
        final List<String> messageError = new ArrayList<>();

        errors.getErrors().forEach(
                error -> messageError.add("(type: " + error.getCode() + ", message: " + error.getMessage() + ")\n")
        );

        return Arrays.toString(messageError.toArray());
    }

    /**
     * @return the errors object model
     */
    @Override
    public Errors getErrors() {
        return errors;
    }

    /**
     * If a 200 was returned set successful to be true.
     */
    private void setSuccessState() {
        this.successful = restResponse.getStatusCode() == 200;
    }
}
