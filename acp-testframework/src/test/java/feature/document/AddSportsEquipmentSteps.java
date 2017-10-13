package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.fixture.hybris.helpers.*;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.AddHoldItemsRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AddHoldItemsToBasketRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.AddHoldBagToBasketService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BASKET_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BOOKING_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.PASSENGER_CODES;
import static com.hybris.easyjet.config.constants.CommonConstants.SINGLE;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.SPORT_EQUIP;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by giuseppedimartino on 31/03/17.
 */

@ContextConfiguration(classes = TestApplication.class)
public class AddSportsEquipmentSteps {

    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BasketHoldItemsHelper basketHoldItemsHelper;
    private AddHoldBagToBasketService addHoldBagToBasketService;
    @Autowired
    private CheckInHelper checkInHelper;
    @Autowired
    private BookingHelper bookingHelper;
    @Autowired
    private SetAPIHelper setAPIHelper;
    @Autowired
    private PurchasedSeatHelper purchasedSeatHelper;
    @Autowired
    private BoardingPassHelper boardingPassHelper;

    @Given("^I added a sports equipment to first passenger$")
    public void iAddedASportsEquipmentToFirstPassenger() throws Throwable {
        Basket basket = bookingHelper.getBasketHelper().getBasketService().getResponse().getBasket();
        BasketPathParams pathParams = BasketPathParams.builder().basketId(basket.getCode()).path(SPORT_EQUIP).build();
        AddHoldItemsRequestBody body =
                AddHoldItemsRequestBody.builder()
                        .productCode("Snowboard")
                        .quantity(1)
                        .passengerCode(basket.getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getCode())
                        .flightKey(basket.getOutbounds().get(0).getFlights().get(0).getFlightKey())
                        .override(false)
                        .build();

        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
        addHoldBagToBasketService = serviceFactory.addHoldBagToBasket(new AddHoldItemsToBasketRequest(headers.build(), pathParams, body));
        addHoldBagToBasketService.invoke();
        basket = bookingHelper.getBasketHelper().getBasket(basket.getCode(), testData.getChannel());
        addHoldBagToBasketService.assertThat().holdBagAddedForEachPassenger(bookingHelper.getBasketHelper().getBasketService().getResponse());
        testData.setData(SerenityFacade.DataKeys.OUTBOUND_FLIGHT,basket.getOutbounds().get(0).getFlights().get(0));

    }

    @Given("^I added a sport equipment to all passengers$")
    public void iAddedASportsEquipmentToAllPassengers() throws Throwable {

        Basket basket = bookingHelper.getBasketHelper().getBasketService().getResponse().getBasket();
        BasketPathParams pathParams = BasketPathParams.builder().basketId(basket.getCode()).path(SPORT_EQUIP).build();
                    AddHoldItemsRequestBody body =
                            AddHoldItemsRequestBody.builder()
                                    .productCode("Snowboard")
                                    .quantity(1)
                                    .override(false)
                                    .build();

                    HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
                    addHoldBagToBasketService = serviceFactory.addHoldBagToBasket(new AddHoldItemsToBasketRequest(headers.build(), pathParams, body));
                    addHoldBagToBasketService.invoke();

        basket = bookingHelper.getBasketHelper().getBasket(basket.getCode(), testData.getChannel());
        addHoldBagToBasketService.assertThat().holdBagAddedForEachPassenger(bookingHelper.getBasketHelper().getBasketService().getResponse());
        testData.setData(SerenityFacade.DataKeys.OUTBOUND_FLIGHT,basket.getOutbounds().get(0).getFlights().get(0));
        testData.setFlightKey(basket.getOutbounds().get(0).getFlights().get(0).getFlightKey());
    }

    @When("^I commit an amendable basket containing (\\d+) flight (Standard|Flexi) and (.*) after add sport equipment (.*) to (first|second|all) passenger on (first|second|all) flight$")
    public void commitAmendableBasketDifferentFlightAndPassenger(int numOfFlight, String fare, String passengerMix, String sportEq, String passengerWhereAdd, String passengerOnFlight) throws Throwable {
        // add multiple flight to basket
        bookingHelper.getBasketHelper().myBasketContainsManyFlightWithPassengerMix(numOfFlight, passengerMix, testData.getChannel(), fare, SINGLE);
        testData.setData(BASKET_ID, bookingHelper.getBasketHelper().getBasketService().getResponse().getBasket().getCode());
        // commit booking and get amendable basket
        testData.setData(BOOKING_ID, bookingHelper.createBasicBookingRequestWithEJPlusDetailsAndCommitIt(false, false).getBookingConfirmation().getBookingReference());
        testData.setData(BASKET_ID, bookingHelper.getAmendableBasket(testData.getData(BOOKING_ID)));
        GetBookingResponse booking = bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel());
        List<GetBookingResponse.Flight> flightsOnBooking = booking.getBookingContext().getBooking().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).collect(Collectors.toList());
        // in the booking I have at least 3 flights
        if(flightsOnBooking.size() < numOfFlight)
            throw new IllegalArgumentException("The number of flights in the booking are not the expected one");
        // get number of flight in booking
        List<GetBookingResponse.Flight> desiredFlights  = getDesiredFlight(flightsOnBooking, passengerOnFlight);
        // get passengers from flight
        List<String> passengerToAddSport = getPassengerListForFlight(desiredFlights, passengerWhereAdd);
        // add sport eq
        basketHoldItemsHelper.addSportEqOnDifferentPassengers(sportEq, passengerToAddSport);
        BasketsResponse basketsResponse = bookingHelper.getBasketHelper().getBasketResponse(testData.getData(BASKET_ID), testData.getChannel());
        // commit amend basket
        bookingHelper.commitBookingFromBasket(basketsResponse);
    }

    private List<GetBookingResponse.Flight> getDesiredFlight(List<GetBookingResponse.Flight> flightsOnBooking, String passengerOnFlight) {
        List<GetBookingResponse.Flight> desiredFlights  = new ArrayList<>();
        switch (passengerOnFlight) {
            case "first":
                desiredFlights.add(flightsOnBooking.get(0));
                break;
            case "second":
                desiredFlights.add(flightsOnBooking.get(1));
                break;
            case "all":
                desiredFlights.add(flightsOnBooking.get(0));
                desiredFlights.add(flightsOnBooking.get(1));
                break;
            default:
                throw new IllegalArgumentException("At least 2 flight are currently allow from the logic");
        }
        return desiredFlights;
    }

    private List<String> getPassengerListForFlight(List<GetBookingResponse.Flight> flights, String passengerWhereAdd) {
        List<String> passengerOnFlight = new ArrayList<>();
        switch (passengerWhereAdd) {
            case "first":
                flights.forEach(flight -> {
                    passengerOnFlight.add(flight.getPassengers().get(0).getCode());
                });
                break;
            case "second":
                flights.forEach(flight -> {
                    passengerOnFlight.add(flight.getPassengers().get(1).getCode());
                });
                break;
            case "all":
                passengerOnFlight.addAll(flights.stream().flatMap(p -> p.getPassengers().stream()).map(passenger -> passenger.getCode()).collect(Collectors.toList()));
                break;
            default:
                throw new IllegalArgumentException("At least 2 passenger for flight are currently allow from the logic");
        }
        return passengerOnFlight;
    }

    @When("^I commit an amendable basket after add add sport equipment (.*) to the passenger$")
    public void iCommitAnAmendableBasketAfterAddAddSportEquipmentToThePassenger(String productCode) throws Throwable {
        // commit booking for one passenger
        testData.setData(BASKET_ID, bookingHelper.createBookingWithMultipleFlightAndGetAmendable(CommonConstants.ONE_ADULT, CommonConstants.STANDARD, 1));
        GetBookingResponse booking = bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel());
        List<String> passengerToAddSport = booking.getBookingContext().getBooking().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(p -> p.getPassengers().stream()).map(pass -> pass.getCode()).collect(Collectors.toList());
        // add sport eq
        basketHoldItemsHelper.addSportEqOnDifferentPassengers(productCode, passengerToAddSport);
        BasketsResponse basketsResponse = bookingHelper.getBasketHelper().getBasketResponse(testData.getData(BASKET_ID), testData.getChannel());
        // commit amend basket
        testData.setData(BOOKING_ID, bookingHelper.commitBookingFromBasket(basketsResponse).getOperationConfirmation().getBookingReference());
    }

    @And("^I have a basket with (.*) on (\\d+) flight$")
    public void iHaveABasketWithAdultOnFlight(String passengerMix, int numOfFlight) throws Throwable {
        // add multiple flight to basket
        bookingHelper.getBasketHelper().myBasketContainsManyFlightWithPassengerMix(numOfFlight, passengerMix, testData.getChannel(), CommonConstants.STANDARD, SINGLE);
        testData.setData(BASKET_ID, bookingHelper.getBasketHelper().getBasketService().getResponse().getBasket().getCode());
    }

    @And("^the first passenger on the first flight has purchased seat and already requested boarding pass$")
    public void addPurchasedSeatGetAmendableAndCheckin() throws Throwable {
        // add purchased seat on first passenger of first outbound flight (current logic implemented from the test)
        purchasedSeatHelper.addPurchasedSeatToBasket(null);
        // commit booking and get amendable basket
        testData.setData(BOOKING_ID, bookingHelper.createBasicBookingRequestWithEJPlusDetailsAndCommitIt(false, false).getBookingConfirmation().getBookingReference());
        pollingLoop().untilAsserted(() -> {
            assertThat(bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel()).getBookingContext().getBooking().getBookingStatus())
                    .isEqualTo("COMPLETED");
        });
        GetBookingResponse.Passenger passenger = testData.getBookingResponse().getBookingContext().getBooking().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(p -> p.getPassengers().stream()).filter(pass -> Objects.nonNull(pass.getSeat())).findFirst().orElseThrow(() -> new IllegalArgumentException("No passenger on booking"));
        // update identity document
        testData.setBookingResponse(bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel()));
        testData.setPassengerId(passenger.getCode());
        testData.setData(PASSENGER_CODES, passenger.getCode());
        setAPIHelper.invokeUpdateIdentityDocument(false, "");
        // checkin
        String flightKey = passenger.getCode().split("_")[1];
        testData.setFlightKey(flightKey);
        checkInHelper.checkInAFlight(Arrays.asList(passenger.getCode()));
        checkInHelper.getCheckInFlightService().getResponse();
        // boarding pass
        boardingPassHelper.generateBoardingPassForFlightKey(flightKey, Arrays.asList(passenger));
        // get amendable basket
        testData.setData(BASKET_ID, bookingHelper.getAmendableBasket(testData.getData(BOOKING_ID)));
    }

    @When("^I commit the amendable basket after add sport equipment (.*) to first passenger on first flight$")
    public void addSportItemAndGetAmendable(String sportProduct) throws Throwable {
        // add sport eq
        basketHoldItemsHelper.addSportEqOnDifferentPassengers(sportProduct, Arrays.asList(testData.getPassengerId()));
        BasketsResponse basketsResponse = bookingHelper.getBasketHelper().getBasketResponse(testData.getData(BASKET_ID), testData.getChannel());
        // commit amend basket
        testData.setData(BOOKING_ID, bookingHelper.commitBookingFromBasket(basketsResponse).getOperationConfirmation().getBookingReference());
        bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel());
    }

    @Then("^I expect status of booking (NEED_TO_RERETRIEVE|NEVER_RETRIEVED) for the passenger on (first|second) flight$")
    public void iCheckBoardingPassStatus(String statusOfBoarding, String numOfFlight) throws Throwable {
        bookingHelper.getGetBookingService().assertThat().verifyBoardingPassStatusForPassenger(numOfFlight, statusOfBoarding);
    }
}
