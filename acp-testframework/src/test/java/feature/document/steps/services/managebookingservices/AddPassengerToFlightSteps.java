package feature.document.steps.services.managebookingservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.AddPassengerToFlightRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AddPassengerToFlightRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.AddPassengerToFlightService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.And;
import feature.document.steps.constants.StepsRegex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.ADD_PASSENGER_TO_FLIGHT;

/**
 * AddPassengerToFlightSteps handle the communication with the add Passenger To Flight service.
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 */
@ContextConfiguration(classes = TestApplication.class)
public class AddPassengerToFlightSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private AddPassengerToFlightService addPassengerToFlightService;
    private BasketPathParams.BasketPathParamsBuilder basketPathParams;
    private AddPassengerToFlightRequestBody.AddPassengerToFlightRequestBodyBuilder addPassengerToFlightRequestBody;

    private String passengerType;

    private void setRequestPathParameter() {
        basketPathParams = BasketPathParams.builder()
                .basketId(testData.getData(BASKET_ID))
                .flightKey(testData.getData(FLIGHT_KEY))
                .path(ADD_PASSENGER_TO_FLIGHT);
    }

    private void setRequestBody() {
        addPassengerToFlightRequestBody = AddPassengerToFlightRequestBody.builder()
                .bundleCode(testData.getData(BUNDLE))
                .flightKeys(Collections.singletonList(testData.getData(FLIGHT_KEY)))
                .passengerType(passengerType);
    }

    private void invokeAddPassengerToFlightService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        addPassengerToFlightService = serviceFactory.getAddPassengerToFlight(new AddPassengerToFlightRequest(headers.build(), basketPathParams.build(), addPassengerToFlightRequestBody.build()));
        testData.setData(SERVICE, addPassengerToFlightService);
        addPassengerToFlightService.invoke();
    }

    private void sendAddPassengerToFlightRequest() {
        setRequestPathParameter();
        setRequestBody();
        invokeAddPassengerToFlightService();
    }

    @And("^I want to add an? " + StepsRegex.PASSENGER_TYPES + " to a flight$")
    public void iWantToAddAnPassengerTypeToAFlight(String paxType) throws Throwable {
        passengerType = paxType;
    }

    @And("^I sen[d|t] the addPassenger request$")
    public void iSendTheAddPassengerRequest() {
        sendAddPassengerToFlightRequest();
    }

}
