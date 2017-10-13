package feature.document.steps.services.createbookingservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.GroupBookingQuoteRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.registercustomer.RegisterCustomerRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.GroupBookingQuoteRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.GroupBookingQuoteService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.GENERATE_QUOTE;

@ContextConfiguration(classes = TestApplication.class)
public class GenerateQuoteSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private GroupBookingQuoteService groupBookingQuoteService;
    private BasketPathParams.BasketPathParamsBuilder generateQuotePath;
    private GroupBookingQuoteRequestBody requestBody;
    private String basketId;
    private String email;

    private void setPathParameter() {
        generateQuotePath = BasketPathParams.builder().basketId(basketId).path(GENERATE_QUOTE);
    }

    private void invokeGroupBookingQuoteService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
        groupBookingQuoteService = serviceFactory.groupBookingQuoteService(new GroupBookingQuoteRequest(headers.build(), generateQuotePath.build(), requestBody));
        testData.setData(SERVICE, groupBookingQuoteService);
        groupBookingQuoteService.invoke();
    }

    private void sendGenerateQuoteRequest() {
        setPathParameter();
        setRequestBody();
        invokeGroupBookingQuoteService();
    }

    private void setRequestBody() {
        RegisterCustomerRequestBody customerRequestBody = testData.getData(REGISTER_CUSTOMER_REQUEST);
        assert customerRequestBody != null;
        email = customerRequestBody.getPersonalDetails().getEmail();
        requestBody = GroupBookingQuoteRequestBody.builder().build();
        requestBody.setEmailAddress(email);
    }

    @When("^I send a request to Group Quote Email service$")
    public void generateQuote() {
        basketId = testData.getData(BASKET_ID);
        sendGenerateQuoteRequest();
    }

    @When("^I send a Group Quote Email request with (.*) parameter$")
    public void iSendAGroupQuoteEmailRequestWithInvalidParameter(String param) throws Throwable {
        basketId = testData.getData(BASKET_ID);
        setRequestBody();

        switch (param) {
            case "Invalid email":
                requestBody.setEmailAddress("INVALID_EMAIL");
                break;
            case "invalid basket id":
                basketId = "INVALID_BASKET_ID";
                break;
            default:
                break;
        }
        setPathParameter();
        invokeGroupBookingQuoteService();
    }

    @Then("^the response is correct$")
    public void theResponseIsCorrect() throws Throwable {
        groupBookingQuoteService.assertThat().checkTheResponse(email);
    }
}
