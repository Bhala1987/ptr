package feature.document.steps.services.managecustomerservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.GetAPIsForCustomerRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.GetCustomerAPIsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.CustomerPathParams.CustomerPaths.APIS;

/**
 * GetAdvancedPassengerInformationSteps handle the communication with the getAdvancedPassengerInformation service (aka API, identity document).
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class GetAdvancedPassengerInformationSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private GetCustomerAPIsService getCustomerAPIsService;
    private CustomerPathParams.CustomerPathParamsBuilder customerPathParamsBuilder;

    private void setPathParameter() {
        customerPathParamsBuilder = CustomerPathParams.builder()
                .customerId(testData.getData(CUSTOMER_ID))
                .path(APIS);
    }

    private void invokeGetAdvancedPassengerInformationService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        getCustomerAPIsService = serviceFactory.getCustomerAPIs(new GetAPIsForCustomerRequest(headers.build(), customerPathParamsBuilder.build()));
        testData.setData(SERVICE, getCustomerAPIsService);
        getCustomerAPIsService.invoke();
    }

    private void sendGetAdvancedPassengerInformationRequest() {
        setPathParameter();
        invokeGetAdvancedPassengerInformationService();
    }

    @When("^I send the request to getAdvancedPassengerInformation service$")
    public void getAdvancedPassengerInformation() {
        sendGetAdvancedPassengerInformationRequest();
    }

}