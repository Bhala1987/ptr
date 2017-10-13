package feature.document.steps.services.managecustomerservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.PaymentDetailsPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.PaymentMethodsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.customer.managepaymentdetails.SavedPaymentMethodService;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;

/**
 * GetPaymentDetailsSteps handle the communication with the getPaymentDetails service.
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class GetPaymentDetailsSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private SavedPaymentMethodService getPaymentDetailsService;
    private PaymentDetailsPathParams.PaymentDetailsPathParamsBuilder paymentDetailsPathParams;

    private void setPathParameter() {
        paymentDetailsPathParams = PaymentDetailsPathParams.builder()
                .customerId(testData.getData(CUSTOMER_ID));
    }

    private void invokeGetPaymentDetailsService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        getPaymentDetailsService = serviceFactory.addSavedPaymentMethod(new PaymentMethodsRequest(headers.build(), paymentDetailsPathParams.build()));
        testData.setData(SERVICE, getPaymentDetailsService);
        getPaymentDetailsService.invoke();
    }

    private void sendGetPaymentDetailsRequest() {
        setPathParameter();
        invokeGetPaymentDetailsService();
    }

    @When("^I send the request to getPaymentDetails service$")
    public void getPaymentDetails() {
        sendGetPaymentDetailsRequest();
    }

}