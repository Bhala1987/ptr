package feature.document.steps.services.managecustomerservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.SavedPassengerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.savedpassenger.SavedPassengerRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.GetSavedPassengerService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;

/**
 * GetSavedPassengersSteps handle the communication with the getSavedPassengers service.
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class GetSavedPassengersSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private GetSavedPassengerService getSavedPassengerService;
    private SavedPassengerPathParams.SavedPassengerPathParamsBuilder savedPassengerPathParams;

    private void setPathParameter() {
        savedPassengerPathParams = SavedPassengerPathParams.builder()
                .customerId(testData.getData(CUSTOMER_ID));
    }

    private void invokeGetSavedPassengersService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        getSavedPassengerService = serviceFactory.getSavedPassenger(new SavedPassengerRequest(headers.build(), savedPassengerPathParams.build(), "get"));
        testData.setData(SERVICE, getSavedPassengerService);
        getSavedPassengerService.invoke();
    }

    private void sendGetSavedPassengersRequest() {
        setPathParameter();
        invokeGetSavedPassengersService();
    }

    @When("^I send the request to getSavedPassengers service$")
    public void getSavedPassengers() {
        sendGetSavedPassengersRequest();
    }

}