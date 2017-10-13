package com.hybris.easyjet.fixture.alei.invokers.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.alei.asserters.ErrorAssertion;
import com.hybris.easyjet.fixture.alei.invokers.requests.AbstractRequest;
import com.hybris.easyjet.fixture.alei.invokers.responses.Errors;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;

import java.net.SocketTimeoutException;
import java.util.Map;

import static com.hybris.easyjet.config.constants.HttpMethods.*;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static net.serenitybdd.rest.RestRequests.given;

/**
 * Created by daniel on 26/11/2016.
 * the hybris service superclass provides all of the common functionality for any service
 */
public abstract class AbstractService implements IService {

    private static final Logger LOGGER = LogManager.getLogger(AbstractService.class);
    @Getter
    protected final AbstractRequest request;
    private RequestSpecification restRequest;
    protected final String endPoint;
    protected Response restResponse;
    protected boolean successful;
    private Errors errors;
    private long startTime;
    private static final String CONTENT_TYPE = "application/json";

    /**
     * a service comprises a request, a client and an endpoint
     *
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    protected AbstractService(IRequest request, String endPoint) {
        this.request = (AbstractRequest) request;
        this.endPoint = endPoint;
    }

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
        restRequest = given()
                .log().all()
                .baseUri(endPoint);
        if (request.getQueryParameters() != null) {
            addQueryParams();
        }
        if (request.getPathParameters() != null) {
            addPathParams();
        }
        try {
            restResponse = addHeadersAndExecuteRequest()
                    .then().log().all()
                    .extract().response();
            stopTheClock();
        } catch (Exception pe) {
            if (pe.getCause().getClass().equals(SocketTimeoutException.class)) {
                throw new AssertionError("The AL service did not respond within the specified timeout.");
            } else {
                throw pe;
            }
        }
        setSuccessState();
        if (successful) {
            mapResponse();
            checkThatResponseBodyIsPopulated();
        } else {
            mapErrors();
        }
    }

    private void stopTheClock() {
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        LOGGER.info("The service " + this.getClass().toString() + " responded in " + elapsedTime + "ms." + "Call was to " + restResponse.toString());
    }

    /**
     * provides a wrapper for asserting the contents of any errors
     *
     * @return the error assertion object which provides fluent reusable assertions
     */
    @Override
    public ErrorAssertion assertThatErrors() {
        assertThatServiceCallWasNotSuccessful();
        return new ErrorAssertion(errors);
    }

    /**
     * @return the errors object model
     */
    @Override
    public Errors getErrors() {
        return errors;
    }

    protected void assertThatServiceCallWasSuccessful() {
        Assert.assertNotNull(restResponse);
        Assert.assertTrue("The service was not called successfully.  Response type was: " +
                        restResponse.getStatusCode() + " Endpoint: " + restResponse.toString(),
                successful);
    }

    protected abstract void checkThatResponseBodyIsPopulated();

    protected void checkThatResponseBodyIsPopulated(Object expectedResponseContent) {
        Assert.assertNotNull("The message body was not populated but the service reported a " + restResponse.getStatusCode()
                + " " + restResponse.getStatusLine(), expectedResponseContent);
    }

    protected abstract void mapResponse();

    private void addPathParams() {
        restRequest.basePath(request.getPathParameters().get());
    }

    private void addQueryParams() {
        restRequest.queryParams(request.getQueryParameters().getParameters());
    }

    private Response addHeadersAndExecuteRequest() { //NOSONAR

        for (Map.Entry<String, Object> entry : request.getHeaders().get().entrySet()) {
            restRequest.header(entry.getKey(), entry.getValue());
        }
        if (request.getHttpMethod().equals(GET)) {
            startTheClock();
            return restRequest.when().get();
        } else if (request.getHttpMethod().equals(POST)) {
            startTheClock();
            restRequest.contentType(CONTENT_TYPE).body(request.getRequestBody());
            return restRequest.when().post();
        } else if (request.getHttpMethod().equals(PUT)) {
            pollingLoop().untilAsserted(() -> {
                startTheClock();
                restRequest.contentType(CONTENT_TYPE).body(request.getRequestBody());
            });
            return restRequest.when().put();
        } else if (request.getHttpMethod().equals(DELETE)) {
            pollingLoop().untilAsserted(() -> {
                startTheClock();
                restRequest.contentType(CONTENT_TYPE).body(request.getRequestBody());
            });
            return restRequest.when().delete();
        } else {
            startTheClock();
            return restRequest.when().get();
        }
    }

    private void startTheClock() {
        startTime = System.currentTimeMillis();
    }

    private void setSuccessState() {
        this.successful = restResponse.getStatusCode() == 200;
    }

    private void mapErrors() {
        try {
            errors = restResponse.as(Errors.class);
        } catch (Exception e) {
            LOGGER.info(e);
        }
    }

    private void assertThatServiceCallWasNotSuccessful() {
        Assert.assertFalse("The service returned a: " +
                restResponse.getStatusCode() + ":" + restResponse.getStatusLine(), successful);
    }

}