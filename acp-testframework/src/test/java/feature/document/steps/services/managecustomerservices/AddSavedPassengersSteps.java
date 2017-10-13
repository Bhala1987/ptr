package feature.document.steps.services.managecustomerservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.SavedPassengerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.savedpassenger.AddUpdateSavedPassengerRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.savedpassenger.SavedPassengerRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.UpdateSavedPassengerService;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Random;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;

/**
 * AddSavedPassengersSteps handle the communication with the addSavedPassengers service.
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class AddSavedPassengersSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private UpdateSavedPassengerService updateSavedPassengerService;
    private SavedPassengerPathParams.SavedPassengerPathParamsBuilder savedPassengerPathParams;
    private AddUpdateSavedPassengerRequestBody.AddUpdateSavedPassengerRequestBodyBuilder addSavedPassengerRequestBody;

    private void setPathParameter() {
        savedPassengerPathParams = SavedPassengerPathParams.builder()
                .customerId(testData.getData(CUSTOMER_ID))
                .passengerId(testData.getData(PASSENGER_ID));
    }

    private void setRequestBody() {
        testData.dataFactory.randomize(new Random(System.currentTimeMillis()).nextInt());
        String[] fullname = testData.dataFactory.getName().split(" ");

        addSavedPassengerRequestBody = AddUpdateSavedPassengerRequestBody.builder()
                .type("adult")
                .title("mr")
                .firstName(fullname[0])
                .lastName(fullname[1])
                .age(26)
                .phoneNumber(testData.dataFactory.getNumberText(12))
                .email("success" + fullname[0] + "_" + testData.dataFactory.getNumberText(10) + "@abctest.com")
                .ejPlusCardNumber("")
                .nifNumber("")
                .flightClubId("")
                .flightClubExpiryDate("");
    }

    private void invokeAddSavedPassengersService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        updateSavedPassengerService = serviceFactory.updateSavedPassenger(new SavedPassengerRequest(headers.build(), savedPassengerPathParams.build(), addSavedPassengerRequestBody.build()));
        testData.setData(SERVICE, updateSavedPassengerService);
        updateSavedPassengerService.invoke();
    }

    private void sendAddSavedPassengersRequest() {
        setPathParameter();
        setRequestBody();
        invokeAddSavedPassengersService();
    }

    @When("^I send the request to addSavedPassengers service$")
    public void addSavedPassengers() {
        sendAddSavedPassengersRequest();
    }

}