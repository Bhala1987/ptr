package feature.document;

import com.headius.invokebinder.transform.Collect;
import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.fixture.hybris.helpers.*;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.CommitBookingService;
import cucumber.api.PendingException;
import cucumber.api.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.config.constants.CommonConstants.OUTBOUND;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by giuseppecioce on 04/05/2017.
 */

@ContextConfiguration(classes = TestApplication.class)
public class RemoveAdditionalSeatSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BookingHelper bookingHelper;
    @Autowired
    private ManageAdditionalFareToPassengerInBasketHelper manageAdditionalFareToPassengerInBasketHelper;
    @Autowired
    private PurchasedSeatHelper purchasedSeatHelper;

    @Given("^I am using \"([^\"]*)\" as passenger mix with (\\d+) additional seat for each and \"([^\"]*)\" fare$")
    public void iAmUsingAsPassengerMixWithAdditionalSeatForEachAndFare(String passengerMix, int additionalSeatQuantity, String fareType) throws Throwable {
        testData.setPassengerMix(passengerMix);
        manageAdditionalFareToPassengerInBasketHelper.setPassengerCode(null);
        testData.setData(SerenityFacade.DataKeys.QUANTITY, additionalSeatQuantity);
        testData.setFareType(fareType);
    }

    @And("^I am removing a seat for passenger via \"([^\"]*)\"$")
    public void iAmRemovingASeatForPassengerVia(String channel) throws Throwable {
        // add flight to basket with passenger mix and additional seat
        String journeyType = CommonConstants.SINGLE;
        // find valid flight
        manageAdditionalFareToPassengerInBasketHelper.findFlight(testData.getPassengerMix(), channel, testData.getFareType());
        List<FindFlightsResponse.Flight> flightsToCheck = manageAdditionalFareToPassengerInBasketHelper.findFlights(testData.getPassengerMix(), channel, testData.getFareType());
        String passengerMixWitAdditionalSeat = FlightPassengers.managePassengerMixWithAdditionalSeat(testData.getPassengerMix(), testData.getData(SerenityFacade.DataKeys.QUANTITY));
        testData.setData(SerenityFacade.DataKeys.FLIGHT_KEY, bookingHelper.getBasketHelper().addFlightToBasketWithAdditionalSeat(
                flightsToCheck,
                manageAdditionalFareToPassengerInBasketHelper.getFlightsService().getResponse().getCurrency(),
                passengerMixWitAdditionalSeat,
                journeyType, testData.getFareType()));
        // prepare statement to invoke service
        manageAdditionalFareToPassengerInBasketHelper.prepareStatementToRemoveAdditionalSeat(testData.getData(SerenityFacade.DataKeys.FLIGHT_KEY), testData.getData(SerenityFacade.DataKeys.QUANTITY));
    }

    @But("^the request miss the mandatory field (.*)$")
    public void theRequestMissTheMandatoryField(String field) throws Throwable {
        manageAdditionalFareToPassengerInBasketHelper.removeFieldFromRequestBody(field);
    }

    @When("^I send the remove additional fare request$")
    public void iSendTheRemoveAdditionalFareRequest() throws Throwable {
        manageAdditionalFareToPassengerInBasketHelper.invokeRemoveAdditionalSeat();
    }

    @Then("^I will verify the error (.*) has been returned$")
    public void iWillVerifyTheErrorHasBeenReturned(String error) throws Throwable {
        manageAdditionalFareToPassengerInBasketHelper.getManageAdditionalFareToPassengerInBasketService().assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @When("^I call the find flight$")
    public void iCallTheFindFlight() throws Throwable {
        manageAdditionalFareToPassengerInBasketHelper.invokeRemoveAdditionalSeat();
    }

    @Then("^I will verify the seat has been deallocated properly$")
    public void iWillVerifyTheSeatHasBeenDeallocatedProperly() throws Throwable {
        // step already verified in step @When iCallTheFindFlight :: invokeFlightFinderAndVerifyAvailabilityHasBeenIncreased
    }

    @Then("^I will verify the additional seat has been removed$")
    public void iWillVerifyTheAdditionalSeatHasBeenRemoved() throws Throwable {
        manageAdditionalFareToPassengerInBasketHelper.getManageAdditionalFareToPassengerInBasketService().assertThat().verifySeatHasBeenRemovedFromPassenger(manageAdditionalFareToPassengerInBasketHelper.getBasket().getResponse().getBasket(),
                manageAdditionalFareToPassengerInBasketHelper.getPassengerCode());
    }

    @Then("^I will verify a successful remove additional fare response has been returned$")
    public void iWillVerifyASuccessfulRemoveAdditionalFareResponseHasBeenReturned() throws Throwable {

        manageAdditionalFareToPassengerInBasketHelper.getManageAdditionalFareToPassengerInBasketService().assertThat().basketOperationConfirmation(manageAdditionalFareToPassengerInBasketHelper.getBasket().getResponse().getBasket().getCode());
    }

    @But("^the quantity is greater than the number for the passenger$")
    public void theQuantityIsGreaterThanTheNumberForThePassenger() throws Throwable {
        manageAdditionalFareToPassengerInBasketHelper.quantityExceedsThreshold(testData.getData(SerenityFacade.DataKeys.QUANTITY));
    }

    @But("^the passenger does not have any additional seat$")
    public void thePassengerDoesNotHaveAnyAdditionalSeat() throws Throwable {
        int additionalQuantity = (testData.getData(SerenityFacade.DataKeys.QUANTITY));
        manageAdditionalFareToPassengerInBasketHelper.prepareStatementToRemoveAdditionalSeat(testData.getData(SerenityFacade.DataKeys.FLIGHT_KEY),  additionalQuantity+ 1);
    }

    @And("^I will verify the passenger totals has been updated$")
    public void iWillVerifyThePassengerTotalsHasBeenUpdated() throws Throwable {
        manageAdditionalFareToPassengerInBasketHelper.getManageAdditionalFareToPassengerInBasketService().assertThat().verifyPriceForPassengerHasBeenUpdateAfterRemoving(manageAdditionalFareToPassengerInBasketHelper.getBasketAfterAddingPurchasedSeat(), manageAdditionalFareToPassengerInBasketHelper.getBasket().getResponse(), manageAdditionalFareToPassengerInBasketHelper.getSeatTotalPrice(), manageAdditionalFareToPassengerInBasketHelper.getPassengerCode());
    }

    @And("^I will verify basket totals has been updated$")
    public void iWillVerifyBasketTotalsHasBeenUpdated() throws Throwable {
        manageAdditionalFareToPassengerInBasketHelper.getManageAdditionalFareToPassengerInBasketService().assertThat().verifyPriceForBasketHasBeenUpdateAfterRemoving(manageAdditionalFareToPassengerInBasketHelper.getBasketAfterAddingPurchasedSeat(), manageAdditionalFareToPassengerInBasketHelper.getBasket().getResponse(), manageAdditionalFareToPassengerInBasketHelper.getSeatTotalPrice());
    }

    @And("^I remove an additional seat for passenger$")
    public void i_remove_an_additional_seat_for_passenger() throws Throwable {
        manageAdditionalFareToPassengerInBasketHelper.prepareStatementToRemoveAdditionalSeat(testData.getFlightKey(), testData.getData(SerenityFacade.DataKeys.QUANTITY));
    }

    @And("^I remove (\\d+) additional seat from the basket$")
    public void i_remove_additional_seat_from_the_basket(int additionalSeatQuantity) throws Throwable {
        manageAdditionalFareToPassengerInBasketHelper.prepareStatementToRemoveAdditionalSeat(testData.getFlightKey(), additionalSeatQuantity);
        manageAdditionalFareToPassengerInBasketHelper.invokeRemoveAdditionalSeat();
    }

    @And("^I will verify the passenger and basket totals has been updated$")
    public void iWillVerifyThePassengerAndBasketTotalsHasBeenUpdated() throws Throwable {
        manageAdditionalFareToPassengerInBasketHelper.calculateBasketAllTotals();
    }

    @Then("^I should receive additional seat removal successful response$")
    public void iShouldReceiveAdditionalSeatRemovalSuccessfulResponse() throws Throwable {
        manageAdditionalFareToPassengerInBasketHelper.getManageAdditionalFareToPassengerInBasketService().assertThat().basketOperationConfirmation(manageAdditionalFareToPassengerInBasketHelper.getBasket().getResponse().getBasket().getCode());
        manageAdditionalFareToPassengerInBasketHelper.getManageAdditionalFareToPassengerInBasketService().assertThat().verifySeatHasBeenRemovedFromPassenger(manageAdditionalFareToPassengerInBasketHelper.getBasket().getResponse().getBasket(),
                manageAdditionalFareToPassengerInBasketHelper.getPassengerCode());
    }

    @When("^I have amendable basket for (.*) fare and (.*) passenger with additional seat (.*) and (.*) purchasedSeats$")
    public void iHaveAmendableBasketForFareTypeFareAndPassengerPassengerWithPurchasedSeat(String fare, String passenger, int additionalSeats, boolean purchasedSeats) throws Throwable {
        String amendableBasket = bookingHelper.createBookingWithPurchasedSeatAndGetAmendable(passenger, fare, purchasedSeats, false, null, false);
        testData.setData(ORIGINAL_BASKET, bookingHelper.getBasketHelper().getBasketService().getResponse().getBasket());
        testData.setBasketId(amendableBasket);
    }

    @And("^I remove (.*) additional seat$")
    public void iRemoveAdditionalSeatAdditionalSeat(int additionalSeats) throws Throwable {
        manageAdditionalFareToPassengerInBasketHelper.removeAdditionalSeats(additionalSeats);
    }

    @Then("^I will receive the cancellation fees ([^\"]*)$")
    public void iWillReceiveTheCancellationFeesCancellationFees(Double cancelFee) throws Throwable {
        Basket amendedBasket = bookingHelper.getBasketHelper().getBasket(testData.getBasketId(), testData.getChannel());
        testData.setData(AMENDED_BASKET, amendedBasket);
        assertThat(bookingHelper.getBasketHelper().getCancellationFeesForCancellingInLessThan24Hr(amendedBasket).compareTo(BigDecimal.valueOf(cancelFee)) == 0)
                .isTrue()
                .withFailMessage("incorrect cancel fee");
    }

    @And("^I get (.*) additional seats deactivated$")
    public void iSetTheAdditionalSeatLineItemStatusToChanged(int additionalSeatsRemoved) throws Throwable {
        Basket amendedBasket = testData.getData(AMENDED_BASKET);
        assertThat(bookingHelper.getBasketHelper().checkRemovedAdditionalSeatStatusCount(amendedBasket))
                .withFailMessage("Status not changed")
                .isEqualTo(additionalSeatsRemoved);
    }

    @And("^I recalculate the basket total with cancellation fees (.*) and purchased Seats (.*)$")
    public void iRecalculateTheBasketTotal(Double cancellationFees, boolean purchasedSeats) throws Throwable {
        Basket originalBasket = testData.getData(ORIGINAL_BASKET);
        Basket amendableBasket = testData.getData(AMENDED_BASKET);
        bookingHelper.getBasketHelper().checkBasketTotalAfterRemovalOfAdditionalSeats(originalBasket, amendableBasket, cancellationFees, purchasedSeats);
    }

    @And("^I should receive successful response$")
    public void iReceiveSuccessfulResponse() throws Throwable {
        manageAdditionalFareToPassengerInBasketHelper.getManageAdditionalFareToPassengerInBasketService().getResponse();
    }

    @And("^I remove (.*) additional seat and the cancellation time is more than 24 hrs$")
    public void iRemoveAdditionalSeatAdditionalSeatAndTheCancellationTimeIsMoreThanHrs(int additionalSeats) throws Throwable {
        throw new PendingException();
    }

    @And("^I get (.*) purchased seats deactivated for additional seats (.*)$")
    public void iGetPurchasedSeatPurchasedSeatsDeactivated(boolean purchasedSeats, int aditionalSeats) throws Throwable {
        if (purchasedSeats) {
            Basket amendedBasket = testData.getData(AMENDED_BASKET);
            assertThat(bookingHelper.getBasketHelper().checkRemovedPurchasedSeatStatusCount(amendedBasket))
                    .withFailMessage("Status not changed")
                    .isEqualTo(aditionalSeats);
        }
    }

    @When("^I commit an amendable basket containing (\\d+) flight and (.*) for each after delete additional fare on (first|second|third) flight for (first|second|first/second) passenger$")
    public void iCommitAmendableBasketAfterRemoveAdditionalFare(int numOfFlight, String passengerMix, String flightWhereRemove, String passengerWhereRemove) throws Throwable {
        // add multiple flight to basket
        bookingHelper.getBasketHelper().myBasketContainsManyFlightWithPassengerMix(numOfFlight, passengerMix, testData.getChannel(), CommonConstants.STANDARD, OUTBOUND);
        testData.setData(BASKET_ID, bookingHelper.getBasketHelper().getBasketService().getResponse().getBasket().getCode());
        // add additional seat for each passenger on flight
        manageAdditionalFareToPassengerInBasketHelper.addAdditionalFareToAllPassengerOnAllFlight(2);
        // commit booking and get amendable basket
        testData.setData(BOOKING_ID, bookingHelper.createBasicBookingRequestWithEJPlusDetailsAndCommitIt(false, false).getBookingConfirmation().getBookingReference());
        String amendBasket = bookingHelper.getAmendableBasket(testData.getData(BOOKING_ID));
        testData.setData(BASKET_ID, amendBasket);
        GetBookingResponse booking = bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel());
        List<GetBookingResponse.Flight> flights = booking.getBookingContext().getBooking().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).collect(Collectors.toList());
        // in the booking I have at least 3 flights
        if(flights.size() < numOfFlight)
            throw new IllegalArgumentException("The number of flights in the booking are not the expected one");

        GetBookingResponse.Flight flight;
        switch (flightWhereRemove) {
            case "first":
                flight = flights.get(0);
                break;
            case "second":
                flight = flights.get(1);
                break;
            case "third":
                flight = flights.get(2);
                break;
            default:
                throw new IllegalArgumentException("At least 3 flight are currently allow from the logic");
        }
        List<String> passengerToRemove = getPassengerListForFlight(flight, passengerWhereRemove);
        manageAdditionalFareToPassengerInBasketHelper.removeAdditionalFareFromPassengers(passengerToRemove, 1);
        BasketsResponse basketsResponse = bookingHelper.getBasketHelper().getBasketResponse(testData.getData(BASKET_ID), testData.getChannel());
        Double amountPriceDifference = Math.abs(basketsResponse.getBasket().getPriceDifference().getAmountWithDebitCard());
        String originalPaymentMethodContext = booking.getBookingContext().getBooking().getPayments().stream().findFirst().orElseThrow(() -> new IllegalArgumentException("No payments found in the booking")).getTransactionId();
        bookingHelper.commitBookingWithPartialRefund(basketsResponse, amountPriceDifference, "24_HOUR_CANCELLATION", originalPaymentMethodContext, "ACTIVE_EUR", false);
    }

    private List<String> getPassengerListForFlight(GetBookingResponse.Flight flight, String passengerWhereRemove) {
        List<String> passengerOnFlight = new ArrayList<>();
        switch (passengerWhereRemove) {
            case "first":
                passengerOnFlight.add(flight.getPassengers().get(0).getCode());
                break;
            case "second":
                passengerOnFlight.add(flight.getPassengers().get(1).getCode());
                break;
            case "first/second":
                passengerOnFlight.addAll(flight.getPassengers().stream().map(p -> p.getCode()).collect(Collectors.toList()));
                break;
            default:
                throw new IllegalArgumentException("At least 2 passenger for flight are currently allow from the logic");
        }
        return passengerOnFlight;
    }

    @Then("^the amendment lock on the booking should be released$")
    public void theAmendmentLockOnTheBookingShouldBeReleased() throws Throwable {
        String amendBasket = bookingHelper.getAmendableBasket(testData.getData(BOOKING_ID));
        assertThat(amendBasket)
                .withFailMessage("Impossible create amendable basket, the lock has not been removed")
                .isNotNull();
        assertThat(amendBasket)
                .withFailMessage("Impossible create amendable basket, the lock has not been removed")
                .isNotEmpty();
    }

    @Then("^I want to check successful response$")
    public void iWantToCheckSuccessfulResponse() throws Throwable {
        CommitBookingService commitBookingService = testData.getData(SERVICE);
        commitBookingService.getResponse();
    }

    @When("^I commit an amendable basket containing (.*) after delete additional fare$")
    public void iCommitAnAmendableBasketContainingAfterDeleteAdditionalFareFromThePassenger(String passengerMix) throws Throwable {
        // add multiple flight to basket
        bookingHelper.getBasketHelper().myBasketContainsAFlightWithPassengerMix(passengerMix, testData.getChannel(), CommonConstants.STANDARD, false);
        testData.setData(BASKET_ID, bookingHelper.getBasketHelper().getBasketService().getResponse().getBasket().getCode());
        // commit booking and get amendable basket
        testData.setData(BOOKING_ID, bookingHelper.createBasicBookingRequestWithEJPlusDetailsAndCommitIt(false, false).getBookingConfirmation().getBookingReference());
        String amendBasket = bookingHelper.getAmendableBasket(testData.getData(BOOKING_ID));
        testData.setData(BASKET_ID, amendBasket);
        // get booking
        GetBookingResponse booking = bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel());
        String passengerToRemove = booking.getBookingContext().getBooking().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).findFirst().orElseThrow(() -> new IllegalArgumentException("No flights found in the booking")).getPassengers().stream().findFirst().orElseThrow(() -> new IllegalArgumentException("No passengers found in the flight")).getCode();
        manageAdditionalFareToPassengerInBasketHelper.removeAdditionalFareFromPassengers(Arrays.asList(passengerToRemove), 1);
        BasketsResponse basketsResponse = bookingHelper.getBasketHelper().getBasketResponse(testData.getData(BASKET_ID), testData.getChannel());
        Double amountPriceDifference = Math.abs(basketsResponse.getBasket().getPriceDifference().getAmountWithDebitCard());
        String originalPaymentMethodContext = booking.getBookingContext().getBooking().getPayments().stream().findFirst().orElseThrow(() -> new IllegalArgumentException("No payments found in the booking")).getTransactionId();
        bookingHelper.commitBookingWithPartialRefund(basketsResponse, amountPriceDifference, "24_HOUR_CANCELLATION", originalPaymentMethodContext, "ACTIVE_EUR", false);
    }

    @And("^I want to check status for additional seat in the booking is INACTIVE$")
    public void iWantToCheckStatusForAdditionalSeatInTheBookingIsINACTIVE() throws Throwable {
        bookingHelper.getGetBookingService().assertThat().checkAdditionalFareActiveStatus();
    }

    @And("^I want to check amend entry status for additional seat in the booking is CHANGED$")
    public void iWantToCheckAmendEntryStatusForAdditionalSeatInTheBookingIsCHANGED() throws Throwable {
        bookingHelper.getGetBookingService().assertThat().checkAdditionalFareEntryStatus();
    }
}
