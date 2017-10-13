package feature.document.steps.services.managecustomerservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.PreferencesPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.UpdateCustomerDetailsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.UpdateCustomerDetailsService;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;

/**
 * GetPreferencesSteps handle the communication with the getPreferences service.
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class GetPreferencesSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private UpdateCustomerDetailsService getPreferencesService;
    private PreferencesPathParams.PreferencesPathParamsBuilder preferencesPathParams;

    private void setPathParameter() {
        preferencesPathParams = PreferencesPathParams.builder()
                .customerId(testData.getData(CUSTOMER_ID));
    }

    private void invokeGetPreferencesService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        getPreferencesService = serviceFactory.updateCustomerDetails(new UpdateCustomerDetailsRequest(headers.build(), preferencesPathParams.build()));
        testData.setData(SERVICE, getPreferencesService);
        getPreferencesService.invoke();
    }

    private void sendGetPreferencesRequest() {
        setPathParameter();
        invokeGetPreferencesService();
    }

    @When("^I send the request to getPreferences service$")
    public void getPreferences() {
        sendGetPreferencesRequest();
    }

}