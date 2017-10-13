package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.BookingHelper;
import com.hybris.easyjet.fixture.hybris.helpers.CheckInHelper;
import com.hybris.easyjet.fixture.hybris.helpers.PurchasedSeatHelper;
import com.hybris.easyjet.fixture.hybris.helpers.SetAPIHelper;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.CustomerProfileService;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.CommitBookingService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.IllformedLocaleException;
import java.util.List;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.config.constants.CommonConstants.ONE_ADULT;
import static com.hybris.easyjet.config.constants.CommonConstants.STANDARD;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = TestApplication.class)
public class CheckInFlightSteps {
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private CheckInHelper checkInHelper;
    @Autowired
    private BookingHelper bookingHelper;
    @Autowired
    private SetAPIHelper setAPIHelper = new SetAPIHelper();
    @Autowired
    private PurchasedSeatHelper purchasedSeatHelper;



    @When("^the channel has initiated a CheckInForFlight for \"([^\"]*)\"$")
    public void theChannelHasInitiatedACheckInForFlightForOnFlight(String passengerMix) throws Throwable {
        testData.setData(BOOKING_ID, bookingHelper.createNewBooking(testData.getChannel(), passengerMix).getBookingConfirmation().getBookingReference());
        testData.setBookingResponse(bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel()));
        testData.setData(PASSENGER_ID, bookingHelper.getBasketHelper().getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getPassengerMap().get(0));
        testData.setPassengerId(bookingHelper.getBasketHelper().getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getPassengerMap().get(0));
    }

    @Then("^Passenger status should change to checked-in on the flight$")
    public void passengerStatusShouldChangeToCheckedInOnTheFlight() throws Throwable {
        if (testData.getPassengerId() == null && testData.getData(PASSENGER_ID) != null) {
            testData.setPassengerId(testData.getData(PASSENGER_ID));
        }
        List<Basket.Passenger> passengers = (List<Basket.Passenger>) testData.getData("PassengersOnFlight");
        setAPIHelper.invokeUpdateIdentityDocumentForEachPassenger(true, passengers);
//        setAPIHelper.invokeUpdateIdentityDocumentForEachPassenger(,true);
        checkInHelper.checkInAFlight(Arrays.asList(testData.getPassengerId()));
        pollingLoop().untilAsserted(() -> {
            assertThat(checkInHelper.checkInFlightService.getResponse());
        });
    }

    @Then("^Passenger status should change to checked-in on the flight for each passenger$")
    public void passengerStatusShouldChangeToCheckedInOnTheFlightForEachPassenger() throws Throwable {
        List<Basket.Passenger> passengers = (List<Basket.Passenger>) testData.getData("PassengersOnFlight");
        setAPIHelper.invokeUpdateIdentityDocumentForEachPassenger(true, passengers);
        pollingLoop().untilAsserted(() -> {
            checkInHelper.checkInAFlight(Arrays.asList(testData.getData(SerenityFacade.DataKeys.PASSENGER_ID).toString()));
            assertThat(checkInHelper.checkInFlightService.getResponse());
        });
    }
    @Then("^Passenger status should change to checked-in on the flight for all passenger$")
    public void passengerStatusShouldChangeToCheckedInOnTheFlightForAllPassenger() throws Throwable {
        List<Basket.Passenger> passengers = (List<Basket.Passenger>) testData.getData("PassengersOnFlight");
        setAPIHelper.invokeUpdateIdentityDocumentForEachPassenger(true, passengers);

        pollingLoop().untilAsserted(() -> {
            checkInHelper.checkInAllFlight(true);
            assertThat(checkInHelper.checkInFlightService.getResponse());
        });
    }

    @And("^channel send getbooking request$")
    public void channelSendGetbookingReequest() throws Throwable {
        bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel());
    }

    @When("^intiated a check for \"([^\"]*)\" with APIs not provided$")
    public void intiatedACheckForWithAPIsNotProvided(String passengerMix) throws Throwable {
        bookingHelper.createNewBooking(testData.getChannel(), passengerMix);
        testData.setData(BOOKING_ID, bookingHelper.createNewBooking(testData.getChannel(), passengerMix).getBookingConfirmation().getBookingReference());
        testData.setPassengerId(bookingHelper.getBasketHelper().getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getPassengerMap().get(0));
        checkInHelper.checkInAFlight(Arrays.asList(testData.getPassengerId()));
    }

    @Then("^response returns errorcode \"([^\"]*)\"$")
    public void responseReturnsErrocode(String errorcode) throws Throwable {
        checkInHelper.getCheckInFlightService().assertThatErrors().containedTheCorrectErrorMessage(errorcode);
    }

    @And("^I am using \"([^\"]*)\" channel$")
    public void iAmUsingChannel(String channel) throws Throwable {
        testData.setChannel(channel);
    }

    @When("^the channel has initiated a CheckInForFlight for (\\d+) Adult on single flight with an Invalid booking Reference$")
    public void theChannelHasInitiatedACheckInForFlightForAdultOnSingleFlight(int noOfPassenger) throws Throwable {
        testData.setData(BOOKING_ID, "EZJ1498744089407");
        checkInHelper.checkInFlightWithDangerousGoodSetToFalse();
    }

    @And("^the isDangerousGoodsAccepted is set to false$")
    public void theIsDangerousGoodsAcceptedIsSetToFalse() throws Throwable {
        setAPIHelper.invokeUpdateIdentityDocument(true);
        checkInHelper.checkInFlightWithDangerousGoodSetToFalse();
    }

    @Then("^I create a booking with following \"([^\"]*)\"$")
    public void iCreateABookingWithFollowing(String criteria) throws Throwable {
        bookingHelper.createNewBookingForPublicChannelWithProducts();

    }

    @And("^update APIs information before checkin$")
    public void updateAPIsInformationBeforeCheckin() throws Throwable {
        CommitBookingService commitBookingService = testData.getData(SERVICE);
        testData.setData(BOOKING_ID, commitBookingService.getResponse().getBookingConfirmation().getBookingReference());
        testData.setBookingResponse(bookingHelper.getAllWithBookingReference().getResponse());
        testData.setPassengerId(testData.getBookingResponse().getBookingContext().getBooking().getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getPassengerMap().get(0));
        setAPIHelper.invokeUpdateIdentityDocument(true);
    }

    @Then("^do checkIn for booking$")
    public void doCheckInForBooking() throws Throwable {
        checkInHelper.checkInAFlight(Arrays.asList(testData.getPassengerId()));
    }


    @Then("^updated passenger status should return \"([^\"]*)\"$")
    public void updatedPassengerStatusShouldReturn(String passengerStatus) throws Throwable {
        pollingLoop().ignoreExceptions().untilAsserted(() -> {
                    bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel());
                    bookingHelper.getGetBookingService().assertThat().thePassengerCheckedin(bookingHelper.getBasketHelper().getBasketService().getResponse(), passengerStatus);
                }
        );
    }


    @And("^the updated passenger status is ([^\"]*)$")
    public void theUpdatedPassengerStatusIsStatus(String passengerStatus) throws Throwable {
        final GetBookingResponse[] response = new GetBookingResponse[1];
        final int[] attempts = {3};
 //       pollingLoop().ignoreExceptions().untilAsserted
        pollingLoop().until(() -> {
            response[0] = bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel());
 //         bookingHelper.getGetBookingService().assertThat().thePassengerCheckedin(response[0], passengerStatus);

            return response[0].getBookingContext().getBooking().getOutbounds().stream()
                    .flatMap(f -> f.getFlights().stream())
                    .flatMap(p -> p.getPassengers().stream())
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No passenger found in the booking"))
                    .getPassengerStatus().equalsIgnoreCase(passengerStatus) || attempts[0] == 0;
        });
        testData.setFlightKey(response[0].getBookingContext().getBooking().getOutbounds().get(0).getFlights().get(0).getFlightKey());
        testData.setPassengerId(response[0].getBookingContext().getBooking().getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getCode());
    }

    @Then("^the passenger is checkedIn$")
    public void thePassengerIsCheckedIn() throws Throwable {
        setAPIHelper.invokeUpdateIdentityDocument(true);

        pollingLoop().untilAsserted(() -> {
            checkInHelper.checkInAFlight(Arrays.asList(testData.getPassengerId()));
            checkInHelper.checkInFlightService.getResponse();
        });
    }

    @Then("^check in passenger with custom \"([^\"]*)\" transaction id$")
    public void checkInPassengerWithCustomTransactionId(String customerClient) throws Throwable {
        setAPIHelper.invokeUpdateIdentityDocument(true);
        checkInHelper.checkInAFlightWithCustomerClientTransactionId(customerClient);
    }

    @When("^checkin a passenger$")
    public void checkinAPassenger() throws Throwable {
        setAPIHelper.invokeUpdateIdentityDocument(true);
    }

    @When("^I send a check in request for (.*) passenger without specifying the infant$")
    public void iSendACheckInRequestForPassengerWithoutSpecifyingTheInfant(String passenger) throws Throwable {
        bookingHelper.createBookingAndGetAmendable(passenger, STANDARD, true);
        List<Basket.Passenger> passengersOnBasket = (List<Basket.Passenger>) testData.getData("PassengersOnFlight");
        setAPIHelper.invokeUpdateIdentityDocumentForEachPassenger(true, passengersOnBasket);
        testData.setPassengerId(passengersOnBasket.stream().filter(p -> "adult".equalsIgnoreCase(p.getPassengerDetails().getPassengerType())).findFirst().orElseThrow(() -> new IllformedLocaleException("No passenger with type adult")).getCode());
        checkInHelper.buildCheckInRequestForSpecificPassenger(Arrays.asList(testData.getPassengerId()));
        checkInHelper.invokeCheckInService();
    }

    @When("^I send a check in request for (.*) passenger for APIS route without submit identity document for both passengers$")
    public void iSendACheckInRequestForPassengerForAPISRouteWithoutSubmitIdentityDocumentForBothPassengers(String passengers) throws Throwable {
        bookingHelper.createBookingAndGetAmendable(passengers, STANDARD, true);
        List<Basket.Passenger> passengersOnBasket = (List<Basket.Passenger>) testData.getData("PassengersOnFlight");
        setAPIHelper.invokeUpdateIdentityDocument(true);
        checkInHelper.checkInAFlight(passengersOnBasket.stream().map(p -> p.getCode()).collect(Collectors.toList()));
    }

    @And("^all passenger status should change to checked-in on the flight$")
    public void allPassengerStatusShouldChangeToCheckedInOnTheFlight() throws Throwable {
        testData.setBookingResponse(bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel()));
        testData.setData(PASSENGER_CODES,  bookingHelper.getBasketHelper().getBasketService().getResponse().getBasket().getOutbounds().stream().
                flatMap(f -> f.getFlights().stream().flatMap(s -> s.getPassengers().stream().map(i->i.getCode()))).collect(Collectors.toList()));
        checkInHelper.checkInAFlight(testData.getData(PASSENGER_CODES));
            checkInHelper.checkInFlightService.getResponse();
    }

    @And("^I've checked in all passengers outbound flight$")
    public void iVeCheckedInAllPassengersOutboundFlight() throws Throwable {
        testData.setBookingResponse(bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel()));
        testData.setData(PASSENGER_CODES,  bookingHelper.getBasketHelper().getBasketService().getResponse().getBasket().getOutbounds().stream().
                flatMap(f -> f.getFlights().stream().flatMap(s -> s.getPassengers().stream().filter(p->p.getActive()==true).map(i->i.getCode()))).collect(Collectors.toList()));
        checkInHelper.checkInAFlight(testData.getData(PASSENGER_CODES));
        pollingLoop().untilAsserted(() -> {
            assertThat(checkInHelper.checkInFlightService.getResponse());
        });
    }

    @And("^I've checked in again for (.*) passenger outbound flight$")
    public void iVeCheckedInAgainForChangeFlightPassengerPassengerOutboundFlight(String nthPassenger) throws Throwable {
        GetBookingResponse bookingResponse = testData.getData(SERVICE);
        String newFlightKey = testData.getData(NEW_FLIGHT_KEY);
        testData.setFlightKey(newFlightKey);
        List<String> passengerCodes = bookingResponse.getBookingContext().getBooking().getOutbounds().stream().flatMap(f -> f.getFlights().stream()
                .filter(i -> i.getFlightKey().equalsIgnoreCase(newFlightKey)).flatMap(j -> j.getPassengers().stream().filter(fp -> fp.getActive() == true).map(p -> p.getCode()))).collect(Collectors.toList());
        testData.setBookingResponse(bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel()));
        checkInHelper.checkInAFlight(passengerCodes);
        pollingLoop().untilAsserted(() -> {
            assertThat(checkInHelper.checkInFlightService.getResponse());
        });
    }

    @And("^I've checked in updated passenger outbound flight$")
    public void iVeCheckedInUpdatedPassengerOutboundFlight() throws Throwable {
        testData.setBookingResponse(bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel()));
        String newFlightKey = testData.getData(SerenityFacade.DataKeys.NEW_FLIGHT_KEY);
        testData.setFlightKey(newFlightKey);
        testData.setData(PASSENGER_CODES,  bookingHelper.getBasketHelper().getBasketService().getResponse().getBasket().getOutbounds().stream().
                flatMap(f -> f.getFlights().stream().filter(ff->ff.getFlightKey().equalsIgnoreCase(newFlightKey)).flatMap(s -> s.getPassengers().stream().filter(p->p.getActive()==true).map(i->i.getCode()))).collect(Collectors.toList()));

        checkInHelper.checkInAFlight(testData.getData(PASSENGER_CODES));
        pollingLoop().untilAsserted(() -> {
            assertThat(checkInHelper.checkInFlightService.getResponse());
        });
    }

    @When("^I request to change the purchased seat on a passenger already checked in$")
    public void iRequestToAddThePurchasedSeatOnAPassengerAlreadyCheckedIn() throws Throwable {
        testData.setPassengerMix(ONE_ADULT);
        testData.setFareType(STANDARD);
        bookingHelper.getBasketHelper().myBasketContainsAFlightWithPassengerMix(testData.getPassengerMix(), testData.getChannel(), testData.getFareType(), false);
        bookingHelper.createBasicBookingRequestWithEJPlusDetailsAndCommitIt(false, false);
        pollingLoop().untilAsserted(() -> {
            assertThat(bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel()).getBookingContext().getBooking().getBookingStatus())
                    .isEqualTo("COMPLETED");
        });
        testData.setBookingResponse(bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel()));
        testData.setPassengerId(testData.getBookingResponse().getBookingContext().getBooking().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(p -> p.getPassengers().stream()).findFirst().orElseThrow(() -> new IllegalArgumentException("No passenger on booking")).getCode());
        setAPIHelper.invokeUpdateIdentityDocument(false, "");
        checkInHelper.checkInAllFlight(true);
        checkInHelper.getCheckInFlightService().getResponse();
        pollingLoop().untilAsserted(() -> {
            List<GetBookingResponse.Passenger> passengerOnFlight = bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel()).getBookingContext().getBooking().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(p -> p.getPassengers().stream()).collect(Collectors.toList());
            passengerOnFlight.forEach(pass -> {
                assertThat(pass.getPassengerStatus())
                        .isEqualTo("CHECKED_IN");
            });
        });
        bookingHelper.getAmendableBasket(testData.getData(BOOKING_ID));
        purchasedSeatHelper.getBasketHelper().getBasket(testData.getBasketId(), testData.getChannel());
        purchasedSeatHelper.getSeatMap(null);
    }

    @Then("^I (true|false) receive an error (.*) based on the channel$")
    public void iShouldReceiveAnErrorBasedOnTheChannel(boolean receiveError, String error) throws Throwable {
        if(receiveError) {
            purchasedSeatHelper.getSeatMapService().assertThatErrors().containedTheCorrectErrorMessage(error);
        } else {
            purchasedSeatHelper.getSeatMapService().getResponse();
        }
    }
}
