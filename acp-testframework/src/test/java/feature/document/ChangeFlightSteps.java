package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.HoldItemsDao;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.*;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.ChangeFlightRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ChangeFlightRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.operationconfirmation.BookingConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.ChangeFlightService;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.assertj.core.api.Java6Assertions;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.exceptions.EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INSUFFICIENT_DATA;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.CHANGE_FLIGHT;
import static org.assertj.core.api.Java6Assertions.assertThat;


/**
 * Created by robertadigiorgio on 11/07/2017.
 */

@ContextConfiguration(classes = TestApplication.class)

public class ChangeFlightSteps {
    private static final String SINGLE_JOURNEY = "SINGLE";
    private static final String OLD_BASKET = "oldBasket";
    private static final String OLD_FLIGHT_KEY = "oldFlightKey";
    private static final String NEW_BASKET = "newBasket";
    private static final String NEW_FLIGHT_KEY = "newFlightKey";
    private static final String NEW_FLIGHT_PRICE = "newFlightPrice";
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private FlightHelper flightHelper;
    private FlightsService flightService;
    private ChangeFlightRequestBody changeFlightRequestBody;
    private BasketPathParams changeFlightPathParams;
    private ChangeFlightService changeFlightService;
    @Autowired
    private HoldItemsDao holdItemsDao;
    @Autowired
    private BasketHoldItemsHelper basketHoldItemsHelper;
    @Autowired
    private BookingHelper commitBookingHelper;
    private GetBookingResponse bookingContext;
    private BookingConfirmationResponse bookingConfirmationResponse;

    @And("^I have (\\d+) flights (.*) fare on (.*) journey in the basket with (.*)$")
    public void iHaveFlightsFareOnJourneyInTheBasketWith(int numberOfFlight, String fareType, String journeyType, String passengerMix) throws Throwable {
        testData.setPassengerMix(passengerMix);
        testData.setFareType(fareType);
        if (SINGLE_JOURNEY.equalsIgnoreCase(journeyType)) {
            basketHelper.myBasketContainsManyFlightWithPassengerMix(numberOfFlight, passengerMix, testData.getChannel(), fareType, journeyType);
        } else {
            flightService = flightHelper.getFlights(testData.getChannel(), passengerMix, testData.getOrigin(), testData.getDestination(), journeyType, testData.getOutboundDate(), testData.getInboundDate(), fareType, testData.getCurrency());
            flightService.assertThat().atLeastOneOutboundFlightWasReturned().theFlightHasAFlightKey();
            basketHelper.addReturnFlightWithTaxToBasketAsChannelJourneyType(flightService, passengerMix, testData.getCurrency(), testData.getChannel(), fareType, journeyType);
        }

        Basket basket = basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());
        testData.setData(OLD_BASKET, basket);
    }

    @When("^I request an invalid change flight with (.*) with (.*)$")
    public void iRequestAnInvalidChangeFlightMissingFields(String fields, String value) throws Throwable {
        buildRequestForChangeFlight();
        invokeInvalidChangeFlightService(fields, value);
    }

    @Then("^I expect error (.*)$")
    public void iExpectError(String error) throws Throwable {
        changeFlightService.assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @When("^I request a valid change flight request$")
    public void iRequestAValidChangeFlightRequest() throws Throwable {
        buildRequestForChangeFlight();
        invokeChangeFlightService();
        changeFlightService.getResponse();

        Basket basket = basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());
        testData.setData(NEW_BASKET, basket);
    }

    @And("^I want to see new flight in the basket with new offer price using the current base price$")
    public void iWantToSeeNewFlightInTheBasketWithNewOfferPriceUsingTheCurrentBasePrice() throws Throwable {
        changeFlightService.assertThat().verifyFlightPriceHasBeenUpdated((Basket) testData.getData(NEW_BASKET), (String) testData.getData(NEW_FLIGHT_KEY), (Double) testData.getData(NEW_FLIGHT_PRICE));
    }

    @And("^I want to see the fees and taxes that are applicable to the new flight$")
    public void iWantToSeeTheFeesAndTaxesThatAreApplicableToTheNewFlight() throws Throwable {
        changeFlightService.assertThat().verifyFeeAndTaxes((Basket) testData.getData(OLD_BASKET), (String) testData.getData(OLD_FLIGHT_KEY), (Basket) testData.getData(NEW_BASKET), (String) testData.getData(NEW_FLIGHT_KEY));
    }

    @And("^I want to see any associated products for each passenger$")
    public void iWantToSeeAnyAssociatedProductsForEachPassenger() throws Throwable {
        changeFlightService.assertThat().verifyProductAssociatedToPassenger((Basket) testData.getData(OLD_BASKET), (String) testData.getData(OLD_FLIGHT_KEY), (Basket) testData.getData(NEW_BASKET), (String) testData.getData(NEW_FLIGHT_KEY));
    }

    @And("^I do not want to see the old flight in the basket$")
    public void iDoNotWantToSeeTheOldFlightInTheBasket() throws Throwable {
        changeFlightService.assertThat().verifyOldFlightKeyHasBeenRemoved((Basket) testData.getData(NEW_BASKET), (String) testData.getData(OLD_FLIGHT_KEY));
    }


    @And("^I (.*) apportion any admin fee apportioned to the flight being changed$")
    public void iApportionAnyAdminFeeApportionedToTheFlightBeingChanged(boolean should) throws Throwable {
        if (should) {
            changeFlightService.assertThat().verifyApportionedAdminFee((Basket) testData.getData(OLD_BASKET), (String) testData.getData(OLD_FLIGHT_KEY), (Basket) testData.getData(NEW_BASKET), (String) testData.getData(NEW_FLIGHT_KEY));
        }
    }

    private void buildRequestForChangeFlight() throws EasyjetCompromisedException, ParseException {
        Basket basket = basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());
        String oldFlightKey = (String) testData.getData(OLD_FLIGHT_KEY);
        List<String> flightInBasket = basket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).map(g -> g.getFlightKey()).collect(Collectors.toList());
        flightInBasket.addAll(basket.getInbounds().stream().flatMap(f -> f.getFlights().stream()).map(g -> g.getFlightKey()).collect(Collectors.toList()));
        List<FindFlightsResponse.Flight> flights = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate(), testData.getFareType(), testData.getCurrency())
                .getOutboundFlights()
                .stream().collect(Collectors.toList());
        flights.removeIf(h -> {
            return flightInBasket.contains(h.getFlightKey());
        });

        FindFlightsResponse.Flight flight = flights.stream().findFirst().orElseThrow(() -> new IllegalArgumentException("No flights returned with different flight key"));
        String newFlightKey = flight.getFlightKey();
        testData.setData(NEW_FLIGHT_KEY, newFlightKey);
        Double priceFlight = flight.getFareTypes().stream().filter(f -> f.getFareTypeCode().equalsIgnoreCase(testData.getFareType())).flatMap(g -> g.getPassengers().stream()).findFirst().orElseThrow(() -> new IllegalArgumentException("No flight found for the desired fare")).getBasePrice();
        testData.setData(NEW_FLIGHT_PRICE, priceFlight);

        changeFlightPathParams = BasketPathParams.builder().basketId(basket.getCode()).path(CHANGE_FLIGHT).flightKey(oldFlightKey).build();
        changeFlightRequestBody = ChangeFlightRequestBody.builder().newFlightKey(newFlightKey).price(priceFlight.toString()).build();
    }

    private void invokeInvalidChangeFlightService(String fields, String value) {
        switch (fields) {
            case "basket-id":
                changeFlightPathParams.setBasketId("WRONG");
                break;
            case "old-flight-key":
                if ("not-present".equalsIgnoreCase(value)) {
                    changeFlightPathParams.setFlightKey("NOT_PRESENT");
                }
                break;
            case "new-flight-key":
                if ("missing".equalsIgnoreCase(value)) {
                    changeFlightRequestBody.setNewFlightKey("");
                } else if ("already-present".equalsIgnoreCase(value)) {
                    String flightKeyAlreadyPresent = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).filter(g -> !g.getFlightKey().equalsIgnoreCase((String) testData.getData(OLD_FLIGHT_KEY))).findFirst().orElseThrow(() -> new IllegalArgumentException("No flight found in the basket")).getFlightKey();
                    changeFlightRequestBody.setNewFlightKey(flightKeyAlreadyPresent);
                }
                break;
            case "new-base-price":
                changeFlightRequestBody.setPrice("");
                break;
            default:
                break;
        }

        invokeChangeFlightService();
    }

    private void invokeChangeFlightService() {
        changeFlightService = serviceFactory.changeFlight(new ChangeFlightRequest(HybrisHeaders.getValid(testData.getChannel()).build(), changeFlightPathParams, changeFlightRequestBody));
        changeFlightService.invoke();
    }

    @And("^I change the flight for amendable basket$")
    public void iChangeTheFlightForAmendableBasket() throws Throwable {
        changeFlightInAmendableBasket();
    }

    private void changeFlightInAmendableBasket() throws EasyjetCompromisedException, ParseException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = LocalDate.now().format(dateTimeFormatter);
        ArrayList<String> passengers = new ArrayList<>();
        testData.setData(BASKET_ID, testData.getBasketId());
        HybrisHeaders.HybrisHeadersBuilder builder = HybrisHeaders.getValid(testData.getChannel());
        testData.setData(HEADERS, builder);
        basketHelper.getBasket(testData.getBasketId());
        passengers.add(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                .flatMap(c -> c.getFlights().stream().map(f -> f.getPassengers())).findFirst().get().stream().map(k -> k.getCode()).findFirst().get());
        FlightsService flights = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate(), "Standard", testData.getCurrency());
        String flightKey = flights.getResponse().getOutbound().getJourneys().stream().flatMap(c -> c.getFlights().stream()
                .filter(k -> !k.getFlightKey().equals(testData.getFlightKey()) && !k.getFlightKey().contains(date))).map(k -> k.getFlightKey()).findFirst().orElseThrow(() -> new EasyjetCompromisedException(INSUFFICIENT_DATA));
        String price = flights.getResponse().getOutbound().getJourneys().stream().flatMap(c -> c.getFlights().stream().filter(a -> a.getAvailableStatus().equals("AVAILABLE")).flatMap(p -> p.getFareTypes().stream().map(t -> t.getTotalFare().getWithDebitCardFee()))).findFirst().get().toString();


        BasketPathParams.BasketPathParamsBuilder basketPathParams = BasketPathParams.builder()
                .basketId(testData.getData(BASKET_ID))
                .flightKey(testData.getData(FLIGHT_KEY))
                .path(CHANGE_FLIGHT);


        ChangeFlightRequestBody.ChangeFlightRequestBodyBuilder changeFlightRequestBody = ChangeFlightRequestBody.builder()
                .newFlightKey(flightKey)
                .price(price)
                .passengers(passengers);

        //testData.setData(FLIGHT_KEY, flightKey);
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        changeFlightService = serviceFactory.changeFlight(new ChangeFlightRequest(headers.build(), basketPathParams.build(), changeFlightRequestBody.build()));
        testData.setData(SERVICE, changeFlightService);
        invokeWithRetry(changeFlightService);
        changeFlightService.getResponse();
    }

    @And("^I change the flight to (.*) passenger in amendable basket$")
    public void iChangeTheFlightToPassengerInAmendableBasket(String nthPassenger) throws Throwable {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = LocalDate.now().format(dateTimeFormatter);
        ArrayList<String> passengers = new ArrayList<>();
        testData.setData(BASKET_ID, testData.getBasketId());
        HybrisHeaders.HybrisHeadersBuilder builder = HybrisHeaders.getValid(testData.getChannel());
        testData.setData(HEADERS, builder);
        basketHelper.getBasket(testData.getBasketId());
        String passengerCode=null;
        if(nthPassenger.equalsIgnoreCase("first")){
            passengerCode = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                    .flatMap(c -> c.getFlights().stream().map(f -> f.getPassengers())).findFirst().get().stream().map(k -> k.getCode()).findFirst().get();
        }else{
            passengerCode=basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                    .flatMap(c -> c.getFlights().stream().map(f -> f.getPassengers().stream().reduce((a,b)->b).get())).findFirst().orElse(null).getCode();
        }
        passengers.add(passengerCode);
        FlightsService flights = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate(), "Standard", testData.getCurrency());
        String flightKey = flights.getResponse().getOutbound().getJourneys().stream().flatMap(c -> c.getFlights().stream()
                .filter(k -> !k.getFlightKey().equals(testData.getFlightKey()) && !k.getFlightKey().contains(date))).map(k -> k.getFlightKey()).findFirst().orElseThrow(() -> new EasyjetCompromisedException(INSUFFICIENT_DATA));
        String price = flights.getResponse().getOutbound().getJourneys().stream().flatMap(c -> c.getFlights().stream().filter(a -> a.getAvailableStatus().equals("AVAILABLE")).flatMap(p -> p.getFareTypes().stream().map(t -> t.getTotalFare().getWithDebitCardFee()))).findFirst().get().toString();

        testData.setData(SerenityFacade.DataKeys.NEW_FLIGHT_KEY,flightKey);
        BasketPathParams.BasketPathParamsBuilder basketPathParams = BasketPathParams.builder()
                .basketId(testData.getData(BASKET_ID))
                .flightKey(testData.getData(FLIGHT_KEY))
                .path(CHANGE_FLIGHT);


        ChangeFlightRequestBody.ChangeFlightRequestBodyBuilder changeFlightRequestBody = ChangeFlightRequestBody.builder()
                .newFlightKey(flightKey)
                .price(price)
                .passengers(passengers);


        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        changeFlightService = serviceFactory.changeFlight(new ChangeFlightRequest(headers.build(), basketPathParams.build(), changeFlightRequestBody.build()));
        testData.setData(SERVICE, changeFlightService);
        invokeWithRetry(changeFlightService);
    }

    private void invokeWithRetry(ChangeFlightService changeFlightService){
        int[] noOfRetry = {5};

        try {
            pollingLoop().until(() -> {
                changeFlightService.invoke();
                noOfRetry[0]--;
                return changeFlightService.getStatusCode() == 200 || noOfRetry[0] == 0;

            });
         }catch (ConditionTimeoutException ct){
            changeFlightService.getRestResponse();
    }
    }

    @When("^I change the flight with (.*) with a new flight")
    public void iChangeFlightInAmendableBasketWithHoldItems(String product) throws Throwable {
        //Check Stock levels before changing flight
        if (product.equals("hold bag")) {
            testData.setData(STOCK_BEFORE_CHANGE_FLIGHT, Integer.parseInt(holdItemsDao.getReservedStockLevelForFlight(testData.getData(FLIGHT_KEY), "20kgbag").get(0)));
        } else if (product.equals("sport equipment")) {
            testData.setData(STOCK_BEFORE_CHANGE_FLIGHT, Integer.parseInt(holdItemsDao.getReservedStockLevelForFlight(testData.getData(FLIGHT_KEY), "Snowboard").get(0)));
        }

        //Change the flight in the basket
        changeFlightInAmendableBasket();

        //Commit Booking
        commitBookingHelper.commitBookingFromBasket(basketHelper.getBasketResponse(testData.getData(BASKET_ID), testData.getChannel()));

        pollingLoop().untilAsserted(() -> {
            bookingContext = getBookingContext();
            assertThat(bookingContext.getBookingContext().getBooking().getBookingStatus().equalsIgnoreCase("COMPLETED")).withFailMessage("Booking status is not 'COMPLETED").isTrue();
            assertThat(bookingContext.getBookingContext().getBooking().getBookingReference()).withFailMessage("Booking reference is not generated or empty").isNotEmpty();
        });
    }

    @Then("^I verify the stock level for the added (.*) has been released")
    public void theStockLevelShouldBeDecreasedForProduct(String product) throws Throwable {
        if (product.equals("hold bag")) {
            testData.setData(STOCK_AFTER_CHANGE_FLIGHT, Integer.parseInt(holdItemsDao.getReservedStockLevelForFlight(testData.getData(FLIGHT_KEY), "20kgbag").get(0)));
        } else if (product.equals("sport equipment")) {
            testData.setData(STOCK_AFTER_CHANGE_FLIGHT, Integer.parseInt(holdItemsDao.getReservedStockLevelForFlight(testData.getData(FLIGHT_KEY), "Snowboard").get(0)));
        }
        int beforeStock = testData.getData(SerenityFacade.DataKeys.STOCK_BEFORE_CHANGE_FLIGHT);
        int afterStock =  testData.getData(SerenityFacade.DataKeys.STOCK_AFTER_CHANGE_FLIGHT);
        assertThat(beforeStock).isGreaterThan(afterStock).withFailMessage("No change in stock level");
    }
    private GetBookingResponse getBookingContext() {
        bookingConfirmationResponse = testData.getData(GET_BOOKING_RESPONSE);
        bookingContext = commitBookingHelper.getBookingDetails(
              bookingConfirmationResponse.getConfirmation().getBookingReference(),
              testData.getChannel());
        testData.setBookingResponse(bookingContext);
        return bookingContext;
    }
}
