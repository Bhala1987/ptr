package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.*;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Passengers;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.AddInfantOnLapRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.AddInfantOnLapFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AddInfantOnLapRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.AddInfantOnLapService;
import cucumber.api.PendingException;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.CommitBookingService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.maven.surefire.util.internal.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.builder;
import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = TestApplication.class)
public class AddInfantOnLapWithPurchaseSeatSteps {

    @Autowired
    private SerenityFacade testData;

    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Autowired
    private BookingHelper bookingHelper;

    private AddInfantOnLapService addInfantOnLapService;

    private AddInfantOnLapRequestBody addInfantOnLapRequestBody;

    @Autowired
    private AmendableBasketHelper amendableBasketHelper;

    @Autowired
    private CheckInHelper checkInHelper;

    @Autowired
    private ManageBookingHelper manageBookingHelper;

    @Autowired
    BasketHelper basketHelper;
    @Autowired
    TravellerHelper travellerHelper;


    @When("^I send a request to add an Infant OL to an adult who has a purchase seat$")
    public void addingInfantSuccessfully() throws Throwable {
        createAmendableBasketAndSetParameters(false);
        invokeInfantOnLap();
        checkForException();
    }

    @Then("^I receive a successful response$")
    public void iReceiveASuccessfulResponse() throws Throwable {
        addInfantOnLapService.getResponse();
    }

    @When("^I send the request to add Infant OL to an adult who has a purchase seat and passenger status is BOOKED$")
    public void addIOLToBookedPassenger() throws Throwable {
        createAmendableBasketAndSetParameters(false);
        String bookingRef = testData.getData(SerenityFacade.DataKeys.BOOKING_ID);

        GetBookingResponse bookingDetails = bookingHelper.getBookingDetails(bookingRef, testData.getChannel());

        invokeInfantOnLap();

        checkForException();

        testData.setBookingResponse(bookingDetails);
    }

    private void checkForException() throws EasyjetCompromisedException {
        if (Objects.nonNull(addInfantOnLapService.getErrors())) {
            List<String> errorCodeDynamicRule = addInfantOnLapService.getErrors().getErrors().stream().map(e -> e.getCode()).collect(Collectors.toList());
            if (errorCodeDynamicRule.contains("SVC_100022_3012")) {
                throw new EasyjetCompromisedException("(type: SVC_100022_3012, message: Seat not available (restricted by dynamic rule))");
            } else if (errorCodeDynamicRule.contains("SVC_100500_5041")) {
                throw new EasyjetCompromisedException("(type: SVC_100500_5041, message: Seat not available (restricted by dynamic rule))");
            } else if (errorCodeDynamicRule.contains("SVC_100600_1013")) {
                throw new EasyjetCompromisedException("(type: SVC_100600_1013, message: Seat not available (restricted by dynamic rule))");
            }
        }
    }

    @And("^I see an Infant on Lap product in the basket$")
    public void iWillSeeAnInfantOnLapProductInTheBasket() throws Throwable {
        String code = amendableBasketHelper.getBasketHelper().getBasket(testData.getBasketId(), testData.getChannel()).getOutbounds().stream().flatMap(f -> f.getFlights().stream().flatMap(p -> p.getPassengers().stream().filter(k -> k.getPassengerDetails().getPassengerType().equalsIgnoreCase(CommonConstants.INFANT)))).findFirst().orElse(null).getFareProduct().getCode();
        assertThat(code.equalsIgnoreCase("INFANTONLAP"));
    }

    @And("^I see an infant passenger in the basket$")
    public void iSeeAnInfantPassengerInTheBasket() throws Throwable {
        assertThat(ObjectUtils.nonNull(amendableBasketHelper.getBasketHelper().getBasket(testData.getBasketId(), testData.getChannel()).getOutbounds().iterator().next().getFlights().iterator().next().getPassengers().stream().anyMatch(passenger -> passenger.getPassengerDetails().getPassengerType().equalsIgnoreCase(CommonConstants.INFANT))));
    }

    @When("^I send the request to add Infant OL to an adult who has a purchase seat and passenger status is CHECKED_IN$")
    public void addIOLToCheckedInPassenger() throws Throwable {
        createAmendableBasketAndSetParameters(false);
        String bookingRef = testData.getData(SerenityFacade.DataKeys.BOOKING_ID);

        GetBookingResponse bookingDetails = bookingHelper.getBookingDetails(bookingRef, testData.getChannel());

        System.setProperty("mocked", "true");
        checkInHelper.buildCheckInRequestForSpecificPassenger(Arrays.asList(testData.getPassengerId()));
        checkInHelper.invokeCheckInService();

        invokeInfantOnLap();

        checkForException();
        testData.setBookingResponse(bookingDetails);
    }

    @And("^I see the Infant on Lap assigned to the passenger$")
    public void iSeeTheIOLAssignedToThePassenger() throws Throwable {
        final Basket.Passenger adultPassenger = amendableBasketHelper.getBasketHelper().getBasket(testData.getBasketId(), testData.getChannel()).getOutbounds().stream().flatMap(outbound -> outbound.getFlights().stream().flatMap(flight -> flight.getPassengers().stream().filter(passenger -> passenger.getCode().equals(testData.getPassengerId())))).findFirst().get();
        assertThat(ObjectUtils.nonNull(adultPassenger.getInfantsOnLap()));
    }

    @And("^The adult passenger's APIS status remains the same$")
    public void theAdultPassengerSAPISStatusRemainsTheSame() throws Throwable {
        final Basket.Passenger adultPassenger = amendableBasketHelper.getBasketHelper().getBasket(testData.getBasketId(), testData.getChannel()).getOutbounds().stream().flatMap(outbound -> outbound.getFlights().stream().flatMap(flight -> flight.getPassengers().stream().filter(passenger -> passenger.getCode().equals(testData.getPassengerId())))).findFirst().get();
        String bookingApisStatus = adultPassenger.getApisStatus();
        String bookingRef = testData.getData(SerenityFacade.DataKeys.BOOKING_ID);
        GetBookingResponse bookingDetails = bookingHelper.getBookingDetails(bookingRef, testData.getChannel());
        String apisStatus = bookingDetails.getBookingContext().getBooking().getOutbounds().stream().flatMap(outbound -> outbound.getFlights().stream().flatMap(flight -> flight.getPassengers().stream().filter(passenger -> passenger.getCode().equals(testData.getPassengerId())))).findFirst().get().getApisStatus();
        assertThat(bookingApisStatus.equals(apisStatus));
    }

    @When("^I send the request to add Infant OL to an adult who has an invalid purchase seat$")
    public void addIOLToInalidSeatPassenger() throws Throwable {
        createAmendableBasketAndSetParameters(true);
        invokeInfantOnLap();
        testData.setData(SerenityFacade.DataKeys.SERVICE, addInfantOnLapService);
    }

    @Then("^I receive an updated basket$")
    public void iReceiveAnUpdatedBasket() throws Throwable {
        String bookingRef = testData.getData(SerenityFacade.DataKeys.BOOKING_ID);
        GetBookingResponse bookingDetails = bookingHelper.getBookingDetails(bookingRef, testData.getChannel());
        Double bookingTotal = bookingDetails.getBookingContext().getBooking().getPriceSummary().getTotalAmount();
        Double basketTotal = amendableBasketHelper.getBasketHelper().getBasket(testData.getBasketId(), testData.getChannel()).getTotalAmountWithDebitCard();
        assertThat(bookingTotal < basketTotal);
    }

    @And("^I see basket totals recalculated$")
    public void iSeeBasketTotalsRecalculated() throws Throwable {
        addInfantOnLapService.getResponse();
        manageBookingHelper.verifyBasketTotalsMoreAfterAddingInfant();
    }

    @And("^The Passenger Status is BOOKED$")
    public void thePassengerStatusIsBOOKED() throws Throwable {
        Basket basket = amendableBasketHelper.getBasketHelper().getBasket(testData.getBasketId(), testData.getChannel());
        Passengers requestToUpdateAllPassenger = amendableBasketHelper.getBasketHelper().createRequestToUpdateAllPassenger(false);
        bookingHelper.getBasketHelper().updatePassengersForChannel(requestToUpdateAllPassenger, testData.getChannel(), testData.getBasketId());
        bookingHelper.commitBookingFromBasket(amendableBasketHelper.getBasketHelper().getBasketResponse(testData.getBasketId(), testData.getChannel()));
        GetBookingResponse bookingDetails = bookingHelper.getBookingDetails(testData.getData(SerenityFacade.DataKeys.BOOKING_ID), testData.getChannel());
        GetBookingResponse.Passenger adultPassengerInTheBooking = bookingDetails.getBookingContext().getBooking().getOutbounds().stream().flatMap(outBound -> outBound.getFlights().stream().flatMap(flight -> flight.getPassengers().stream().filter(passenger -> passenger.getCode().equals(testData.getPassengerId())))).findFirst().get();
        assertThat(adultPassengerInTheBooking.getPassengerStatus().equals("BOOKED"));
    }


    @And("^I have a basket with (\\d+) flights with infant on lap on one flight with ([^\"]*)$")
    public void iVeAmendableBasketWithWithCreditFundCardAsPaymentType(int numberOfFlights, String paymentType) throws Throwable {
        basketHelper.myBasketContainsManyFlightWithPassengerMix(numberOfFlights, "1 Adult", testData.getChannel(), "Standard", "Single");
        BasketService basketService = testData.getData(BASKET_SERVICE);
        String basketCode = basketService.getResponse().getBasket().getCode();
        testData.setBasketId(basketCode);
        basketHelper.getBasket(basketService.getResponse().getBasket().getCode(), testData.getChannel());

        String passengerCode = basketService.getResponse().getBasket().getOutbounds().stream()
                .findFirst().orElse(null)
                .getFlights().stream()
                .findFirst().orElse(null)
                .getPassengers().stream()
                .findFirst().orElse(null)
                .getCode();

        testData.setPassengerId(passengerCode);
        invokeInfantOnLap();
        basketHelper.getBasket(basketService.getResponse().getBasket().getCode(), testData.getChannel());
        bookingHelper.basicBookingwithCardDetailsAndCommitIt(paymentType);
        CommitBookingService commitBookingService = testData.getData(SERVICE);

        testData.setData(BOOKING_ID, commitBookingService.getResponse().getBookingConfirmation().getBookingReference());
    }

    private void createAmendableBasketAndSetParameters(boolean isEmergencyExit) throws Throwable {
        String amendableBasket = bookingHelper.createBookingWithPurchasedSeatAndGetAmendable("1 Adult", "Standard", true, isEmergencyExit, null, false);

        String passengerCode = amendableBasketHelper.getBasketHelper().getBasketService().getResponse().getBasket().getOutbounds().iterator().next().getFlights().iterator().next().getPassengers().iterator().next().getCode();

        testData.setBasketId(amendableBasket);
        testData.setPassengerId(passengerCode);
    }

    private void invokeInfantOnLap() {
        BasketPathParams basketPathParams = builder()
                .basketId(testData.getBasketId())
                .passengerId(testData.getPassengerId())
                .path(BasketPathParams.BasketPaths.ADD_INFANT_ON_LAP)
                .build();

        addInfantOnLapRequestBody = AddInfantOnLapFactory.getAddInfantOnLapBody();
        addInfantOnLapService = serviceFactory
                .addInfantOnLap(new AddInfantOnLapRequest(HybrisHeaders.getValid(testData.getChannel()).build(), basketPathParams, addInfantOnLapRequestBody));

        addInfantOnLapService.invoke();
    }

    @When("^I send the request to add Infant OL to an adult$")
    public void iSendTheRequestToAddInfantOLToAnAdult() throws Throwable
    {
        basketHelper.getBasket(testData.getBasketId());
        String passengerCode = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream().findFirst().orElse(null).getFlights().stream()
              .findFirst().orElse(null).getPassengers().stream().findFirst().orElse(null).getCode();
        testData.setPassengerId(passengerCode);
        invokeInfantOnLap();

        checkForException();

    }
}
