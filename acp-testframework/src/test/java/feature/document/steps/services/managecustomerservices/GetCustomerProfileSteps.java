package feature.document.steps.services.managecustomerservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ProfileRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.CustomerProfileService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.CustomerPathParams.CustomerPaths.PROFILE;

/**
 * GetCustomerProfileSteps handle the communication with the getCustomerProfile service.
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class GetCustomerProfileSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private CustomerProfileService getCustomerProfileService;
    private CustomerPathParams.CustomerPathParamsBuilder customerPathParamsBuilder;

    private void setPathParameter() {
        customerPathParamsBuilder = CustomerPathParams.builder()
                .customerId(testData.getData(CUSTOMER_ID))
                .path(PROFILE);
    }

    private void invokeGetCustomerProfileService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        String channel = testData.getData(CHANNEL);
        if (channel.startsWith("AD") && testData.dataExist(AGENT_ACCESS_TOKEN)) {
            headers.authorization("Bearer " + testData.getData(AGENT_ACCESS_TOKEN));
        } else if (testData.dataExist(CUSTOMER_ACCESS_TOKEN)) {
            headers.authorization("Bearer " + testData.getData(CUSTOMER_ACCESS_TOKEN));
        }
        getCustomerProfileService = serviceFactory.getCustomerProfile(new ProfileRequest(headers.build(), customerPathParamsBuilder.build()));
        testData.setData(SERVICE, getCustomerProfileService);
        getCustomerProfileService.invoke();
    }

    private void sendGetCustomerProfileRequest() {
        setPathParameter();
        invokeGetCustomerProfileService();
    }

    @Step("Get customer profile")
    @When("^I send the request to getCustomerProfile service$")
    public void getCustomerProfile() {
        sendGetCustomerProfileRequest();
    }

}