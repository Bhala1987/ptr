package feature.document.steps.services.checkinservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.CheckInFlightRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.CheckInFlightRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractFlights;
import com.hybris.easyjet.fixture.hybris.invoke.services.CheckInFlightService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.Given;
import feature.document.steps.constants.StepsRegex;
import feature.document.steps.services.createbookingservices.CommitBookingSteps;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams.BookingPaths.CHECKIN;

/**
 * CheckInForFlightSteps handle the communication with the checkinForFlight service (aka check-in).
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class CheckInForFlightSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Steps
    private CommitBookingSteps commitBookingSteps;

    private CheckInFlightService checkInFlightService;
    private BookingPathParams.BookingPathParamsBuilder bookingPathParams;
    private CheckInFlightRequestBody.CheckInFlightRequestBodyBuilder checkInFlightRequestBody;
    private List<CheckInFlightRequestBody.CheckFlight> flights;

    private void setPathParameter() {
        bookingPathParams = BookingPathParams.builder()
                .bookingId(testData.getData(BOOKING_ID))
                .path(CHECKIN);
    }

    private void setRequestBody() {
        checkInFlightRequestBody = CheckInFlightRequestBody.builder()
                .isCheckInAll(true)
                .flights(flights)
                .isDangerousGoodsAccepted(true);
    }

    private void invokeCheckInForFlightService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        checkInFlightService = serviceFactory.checkInFlightService(new CheckInFlightRequest(headers.build(), bookingPathParams.build(), checkInFlightRequestBody.build()));
        testData.setData(SERVICE, checkInFlightService);
        checkInFlightService.invoke();
    }

    private void sendCheckInForFlightRequest() {
        setPathParameter();
        setRequestBody();
        invokeCheckInForFlightService();
    }

    private void setFlightsList(){
        flights = new ArrayList<>();
        GetBookingResponse getBookingResponse = testData.getData(GET_BOOKING_RESPONSE);
        getBookingResponse.getBookingContext().getBooking().getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .forEach(
                        flight -> {
                            CheckInFlightRequestBody.CheckFlight.CheckFlightBuilder theFlight = CheckInFlightRequestBody.CheckFlight.builder();
                            theFlight.flightKey(flight.getFlightKey());
                            List<String> passengers = new ArrayList<>();
                            flight.getPassengers().forEach(
                                    passenger -> passengers.add(passenger.getCode())
                            );
                            theFlight.passengers(passengers);
                            flights.add(theFlight.build());
                        }
                );
    }

    @Step("Check-in")
    public void checkin() {
        setFlightsList();
        sendCheckInForFlightRequest();
    }

    @Step("Create check-in")
    @Given("^I have a check-in" + StepsRegex.RETURN + StepsRegex.HOLD_ITEMS + StepsRegex.SPORTS_ITEMS + StepsRegex.SECTOR + StepsRegex.FARE_TYPE + StepsRegex.PASSENGER_MIX_APIS + StepsRegex.DATES + StepsRegex.FLEXIBLE_DAYS + StepsRegex.DEAL + StepsRegex.CURRENCY + "$")
    public void createCheckIn(String withReturn, String addHoldBag, Integer holdBag, Integer excessWeightQuantity, String excessWeightType, String passengerWithHolddBag, String addsportsItem, Integer sportsItemQty, String passengerWithSportsItem, String origin, String destination, String fareType, String passengerMix, String apisData, String outboundDate, String inboundDate, String flexibleDays, String applicationId, String officeId, String corporateId, String currency) throws EasyjetCompromisedException {
        commitBookingSteps.createBooking(withReturn, addHoldBag, holdBag, excessWeightQuantity, excessWeightType, passengerWithHolddBag, addsportsItem, sportsItemQty, passengerWithSportsItem, origin, destination, fareType, passengerMix, apisData, outboundDate, inboundDate, flexibleDays, applicationId, officeId, corporateId, currency);
        checkin();
    }

}