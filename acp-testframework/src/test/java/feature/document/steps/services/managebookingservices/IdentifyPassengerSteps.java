package feature.document.steps.services.managebookingservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.asserters.IdentifyPassengerAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.IdentifyPassengerRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.IdentifyPassengerRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractFlights;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.IdentifyPassengerService;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collection;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.GET_BOOKING_RESPONSE;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.HEADERS;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;
import static feature.document.GlobalHooks.clearCookiesInClient;

/**
 * Created by rajakm on 11/10/2017.
 */
/**
 * IdentifyPassengerSteps handle the communication from find the passenger from the booking.
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 */
@ContextConfiguration(classes = TestApplication.class)
public class IdentifyPassengerSteps {
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private IdentifyPassengerService identifyPassengerService;
    private IdentifyPassengerRequestBody.IdentifyPassengerRequestBodyBuilder identifyPassengerRequestBody;
    @Steps
    private IdentifyPassengerAssertion identifyPassengerAssertion;

    private String passengerName;
    private String passengerId;

    private void setRequestBody() {
        identifyPassengerRequestBody = IdentifyPassengerRequestBody.builder()
                .bookingReference(testData.getData(SerenityFacade.DataKeys.BOOKING_ID))
                .lastName(passengerName)
                .passengerOnFlightId(passengerId);
    }

    private void invokeIdentifyPassengerService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        identifyPassengerService = serviceFactory.identifyPassengerService(new IdentifyPassengerRequest(headers.build(), identifyPassengerRequestBody.build()));
        testData.setData(SERVICE, identifyPassengerService);
        identifyPassengerService.invoke();
    }

    private void sendIdentifyPassengerRequest() {
        setRequestBody();
        invokeIdentifyPassengerService();
    }

    @And("^I want to search by passenger (id|surname) which is (exist|not exist) in the booking$")
    public void iWantToSearchByPassengerSurnameOrIDInTheBooking(String type, String value) {
        if(value.equalsIgnoreCase("not exist")) {
            passengerName = "nonExist";
        }
        else if(value.equalsIgnoreCase("exist")){
            GetBookingResponse bookingResponse = testData.getData(GET_BOOKING_RESPONSE);
            if(type.equalsIgnoreCase("surname")) {
                passengerName = bookingResponse.getBookingContext().getBooking().getOutbounds().stream()
                        .map(AbstractFlights::getFlights)
                        .flatMap(Collection::stream)
                        .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                        .flatMap(Collection::stream)
                        .findFirst().get()
                        .getPassengerDetails().getName().getLastName();
            }
            else if(type.equalsIgnoreCase("id")){
                passengerId = bookingResponse.getBookingContext().getBooking().getOutbounds().stream()
                        .map(AbstractFlights::getFlights)
                        .flatMap(Collection::stream)
                        .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                        .flatMap(Collection::stream)
                        .findFirst().get()
                        .getCode();
            }
        }
        clearCookiesInClient();
    }

    @When("^I send the identifyPassenger request$")
    public void iSendTheIdentifyPassengerRequest() {
        sendIdentifyPassengerRequest();
    }

    @Then("^I will get the authentication details in the response$")
    public void iWillGetThePassengerDetailsInTheResponse() {
        identifyPassengerAssertion.setResponse(identifyPassengerService.getResponse());
        identifyPassengerService.assertThat().theSearchWasSuccesful();
    }

    @And("^I want to search by passenger surname which is matching more than one passenger in the booking$")
    public void iWantToSearchByPassengerSurnameWhichIsMatchingMoreThanOnePassengerInTheBooking() {
        iWantToSearchByPassengerSurnameOrIDInTheBooking("surname", "exist");
    }

    @Then("^I will get all the passenger details matching the surname in the response$")
    public void iWillGetAllThePassengerDetailsMatchingTheSurnameInTheResponse() {
        identifyPassengerAssertion.setResponse(identifyPassengerService.getResponse());
        identifyPassengerService.assertThat().theResultListContainsValidSearch(passengerName);
    }
}
