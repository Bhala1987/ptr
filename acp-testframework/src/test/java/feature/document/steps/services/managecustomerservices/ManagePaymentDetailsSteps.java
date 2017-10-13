package feature.document.steps.services.managecustomerservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.PaymentDetailsPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.managepaymentdetails.Card;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.managepaymentdetails.SavedPaymentMethodRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.PaymentMethodsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.customer.managepaymentdetails.SavedPaymentMethodService;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.PaymentDetailsPathParams.PaymentDetailsPaths.CREDIT_CARD;

/**
 * ManagePaymentDetailsSteps handle the communication with the managePaymentDetails service.
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class ManagePaymentDetailsSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private SavedPaymentMethodService addPaymentDetailsService;
    private PaymentDetailsPathParams.PaymentDetailsPathParamsBuilder paymentDetailsPathParams;
    private SavedPaymentMethodRequestBody.SavedPaymentMethodRequestBodyBuilder savedPaymentMethodRequestBody;

    private void setPathParameter() {
        paymentDetailsPathParams = PaymentDetailsPathParams.builder()
                .customerId(testData.getData(CUSTOMER_ID))
                .path(CREDIT_CARD);
    }

    private void setRequestBody() {
        Card card = Card.builder()
                .cardToken("4212345678901237")
                .cardIssueNumber("737")
                .cardHolderName("Testing card")
                .cardValidFromMonth("09")
                .cardValidFromYear("2015")
                .cardExpiryMonth("12")
                .cardExpiryYear("9999")
                .isDefault(false)
                .build();

        savedPaymentMethodRequestBody = SavedPaymentMethodRequestBody.builder()
                .paymentMethod("card")
                .paymentCode("VI")
                .paymentMethodId("71")
                .card(card);
    }

    private void invokeManagePaymentDetailsService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        addPaymentDetailsService = serviceFactory.addSavedPaymentMethod(new PaymentMethodsRequest(headers.build(), paymentDetailsPathParams.build(), savedPaymentMethodRequestBody.build()));
        testData.setData(SERVICE, addPaymentDetailsService);
        addPaymentDetailsService.invoke();
    }

    private void sendManagePaymentDetailsRequest() {
        setPathParameter();
        setRequestBody();
        invokeManagePaymentDetailsService();
    }

    @When("^I send the request to managePaymentDetails service$")
    public void managePaymentDetails() {
        sendManagePaymentDetailsRequest();
    }

}