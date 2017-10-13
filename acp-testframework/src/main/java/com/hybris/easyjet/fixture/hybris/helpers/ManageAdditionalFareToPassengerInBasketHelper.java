package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.database.hybris.dao.FeesAndTaxesDao;
import com.hybris.easyjet.database.hybris.models.FeesAndTaxesModel;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.BasketQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.AddAdditionalFareToPassengerInBasketRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AddAdditionalFareRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.BasketRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ManageAdditionalFareToPassengerInBasketRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.Currency;
import com.hybris.easyjet.fixture.hybris.invoke.services.AddAdditionalFareToPassengerInBasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.ManageAdditionalFareToPassengerInBasketService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BASKET_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.PASSENGER_ID;
import static com.hybris.easyjet.config.constants.CommonConstants.STANDARD;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.ADD_ADDITIONAL_FARE_TO_PASSENGER;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.MANAGE_ADDITIONAL_SEAT_TO_PASSENGER;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 13/04/2017.
 */
@Component
public class ManageAdditionalFareToPassengerInBasketHelper {

    @Getter
    @Setter
    private FlightsService flightsService;
    @Getter
    @Setter
    private AddAdditionalFareToPassengerInBasketService addAdditionalFareToPassengerInBasketService;
    @Getter
    @Setter
    private Integer numberOfProductRelated;
    @Getter
    @Setter
    private String passengerCode;
    @Setter
    @Getter
    private PricingHelper basketTotalPrice = new PricingHelper();
    @Setter
    @Getter
    private PricingHelper seatTotalPrice = new PricingHelper();
    @Setter
    @Getter
    private BasketsResponse basketAfterAddingPurchasedSeat;
    @Setter
    private ManageAdditionalFareToPassengerInBasketService manageAdditionalFareToPassengerInBasketService;

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private FeesAndTaxesDao feesAndTaxesDao;
    @Autowired
    private FlightHelper flightHelper;

    private BasketService basketService;
    private BasketPathParams pathParams;
    private BasketQueryParams queryParam;
    private String basketId;
    private ArrayList<String> passengerList;

    private PricingHelper passengerTotalPrice = new PricingHelper();

    public void findAndAddFlight(String passengerMix, String channel) throws Throwable {
        String requiredFare = STANDARD;
        findFlight(passengerMix, channel, requiredFare);

        basketHelper.addFlightToBasketAsChannelUsingFlightCurrencyWithPassengerMix(
                flightsService.getOutboundFlight(), passengerMix, channel, flightsService.getResponse().getCurrency(), requiredFare);

        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(),testData.getChannel());

        assertThat(basketHelper.getBasketService().getResponse().getBasket().getOutbounds()).isNotEmpty();
        basketHelper.getBasketService().assertThat().theBasketContainsTheFlight(flightsService.getOutboundFlight());
    }

    public FindFlightsResponse.Flight findFlight(String passengerMix, String channel, String fare) throws Exception {
        testData.setChannel(channel);
        flightsService = flightHelper.getFlights(testData.getChannel(), passengerMix, testData.getOrigin(), testData.getDestination(),fare,testData.getCurrency());
        testData.setOutboundFlight(flightsService.getOutboundFlight());
        return flightsService.getOutboundFlight();
    }

    public void additionalFareToPassengerInBasketHelperMultiplePassengers(){
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());
        Basket basket = basketHelper.getBasketService().getResponse().getBasket();
        basket.getOutbounds().forEach(journey -> {
            for (Basket.Flight flight : journey.getFlights()){
                List<Basket.Passenger> passengers = flight.getPassengers().stream()
                        .filter(passenger -> !"INFANTONLAP".equalsIgnoreCase(passenger.getFareProduct().getCode()))
                        .collect(Collectors.toList());
                for (Basket.Passenger passenger : passengers) {

                    addAdditionalFareToPassengerNew(passenger.getCode());
                }
            }
        });

    }

    public void addAdditionalFareToPassengerNew(String passengerId){

        basketId = testData.getBasketId();
        AddAdditionalFareToPassengerInBasketRequestBody addAdditionalFareToPassengerInBasketRequestBody;
        pathParams = BasketPathParams.builder().basketId(basketId).path(MANAGE_ADDITIONAL_SEAT_TO_PASSENGER).passengerId(passengerId).build();
        addAdditionalFareToPassengerInBasketRequestBody = AddAdditionalFareToPassengerInBasketRequestBody.builder().numberOfFares(1).additionalSeatReason("COMFORT").build();
        manageAdditionalFareToPassengerInBasketService = serviceFactory.manageAdditionalFareToPassengerInBasket(new ManageAdditionalFareToPassengerInBasketRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, addAdditionalFareToPassengerInBasketRequestBody));
        manageAdditionalFareToPassengerInBasketService.invoke();
    }

    public ManageAdditionalFareToPassengerInBasketService getManageAdditionalFareToPassengerInBasketService(){

        return  manageAdditionalFareToPassengerInBasketService;
    }

    public List<FindFlightsResponse.Flight> findFlights(String passengerMix, String channel, String fare) throws Exception {
        testData.setChannel(channel);
        flightsService = flightHelper.getFlights(testData.getChannel(), passengerMix, testData.getOrigin(), testData.getDestination(),fare,testData.getCurrency());
        testData.setOutboundFlight(flightsService.getOutboundFlight());
        return flightsService.getOutboundFlights();
    }

    public void addAdditionalFareToPassenger(String additionalSeatMix, String channel, String parameter) {
        testData.setChannel(channel);
        FlightPassengers paxAdditionalSeats = new FlightPassengers(additionalSeatMix);
        int paxTypes = additionalSeatMix.split(",").length;
        basketService = getBasket();
        basketId = basketService.getResponse().getBasket().getCode();
        List<Basket.Flight> basketOutboundFlights = basketService.getResponse().getBasket()
                .getOutbounds()
                .stream()
                .flatMap(f -> f.getFlights().stream())
                .collect(Collectors.toList());
        passengerList = new ArrayList<>();
        for (Basket.Flight flight : basketOutboundFlights) {

            List<Basket.Passenger> basketPassengers = flight.getPassengers().stream().collect(Collectors.toList());
            for (int i = 0; i < paxTypes; i++) {

                for (Basket.Passenger traveller : basketPassengers) {

                    Integer position = Integer.valueOf(additionalSeatMix.split(",")[i].trim().split(" ")[2]);
                    if (traveller.getPassengerDetails().getPassengerType().equalsIgnoreCase(paxAdditionalSeats.getPassengers().get(i).getPassengerType())) {
                        Integer numberOfFares = paxAdditionalSeats.getPassengers().get(i).getQuantity();
                        passengerList.add(basketPassengers.get(position - 1).getCode());
                        if ("basketId".equalsIgnoreCase(parameter)) {
                            pathParams = BasketPathParams.builder().basketId("invalid").path(MANAGE_ADDITIONAL_SEAT_TO_PASSENGER).passengerId(basketPassengers.get(position - 1).getCode()).build();
                        } else if ("passengerId".equalsIgnoreCase(parameter)) {
                            pathParams = BasketPathParams.builder().basketId(basketId).path(MANAGE_ADDITIONAL_SEAT_TO_PASSENGER).passengerId("invalid").build();
                        } else {
                            pathParams = BasketPathParams.builder().basketId(basketId).path(MANAGE_ADDITIONAL_SEAT_TO_PASSENGER).passengerId(basketPassengers.get(position - 1).getCode()).build();
                        }
                        AddAdditionalFareToPassengerInBasketRequestBody addAdditionalFareToPassengerInBasketRequestBody;
                        if ("zeroFare".equalsIgnoreCase(parameter)) {
                            addAdditionalFareToPassengerInBasketRequestBody = AddAdditionalFareToPassengerInBasketRequestBody.builder().numberOfFares(0).build();
                        } else if ("requestBody".equalsIgnoreCase(parameter)) {
                            addAdditionalFareToPassengerInBasketRequestBody = AddAdditionalFareToPassengerInBasketRequestBody.builder().build();
                        } else {
                            addAdditionalFareToPassengerInBasketRequestBody = AddAdditionalFareToPassengerInBasketRequestBody.builder().numberOfFares(numberOfFares).build();
                        }
                        addAdditionalFareToPassengerInBasketRequestBody.setAdditionalSeatReason("COMFORT");
                        manageAdditionalFareToPassengerInBasketService = serviceFactory.manageAdditionalFareToPassengerInBasket(new ManageAdditionalFareToPassengerInBasketRequest(HybrisHeaders.getValid(channel).build(), pathParams, addAdditionalFareToPassengerInBasketRequestBody));
                        manageAdditionalFareToPassengerInBasketService.invoke();
                        break;
                    }
                }
            }
        }
    }

    public BasketService getBasket() {
        basketId = basketHelper.getBasketService().getResponse().getBasket().getCode();

        pathParams = BasketPathParams.builder().basketId(basketId).build();
        basketService = serviceFactory.getBasket(new BasketRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams));
        pollingLoop().ignoreExceptions().untilAsserted(() -> {
            basketService.invoke();
            basketService.assertThat().gotAValidResponse();
        });
        return basketService;
    }

    public void addAdditionalFareToPassengerInBasketSuccess() {
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(),testData.getChannel());
        basketService=basketHelper.getBasketService();
        Currency currency = basketHelper.getBasketService().getResponse().getBasket().getCurrency();
        FeesAndTaxesModel fee = feesAndTaxesDao.getAdminFees(currency.getCode()).get(0);
        basketService.assertThat().priceCalculationAreRight(Integer.valueOf(currency.getDecimalPlaces()), fee.getFeeValue());
        for (String paxCode : passengerList) {
            basketHelper.getBasketService().assertThat().additionalSeatAddedAtPassengerLevel(basketHelper.getBasketService().getResponse(), paxCode);
        }
    }

    private String getFirstPassengerAdultFromFirstOutboundFlightInBasket() {
        if (Objects.isNull(passengerCode)) {
            passengerCode = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().stream().filter(f -> "adult".equalsIgnoreCase(f.getPassengerDetails().getPassengerType())).findFirst().orElseThrow(() -> new IllegalArgumentException("No desired passenger type ADULT is available")).getCode();
        }
        return passengerCode;
    }

    private String getPassengerMap(){
        return basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getPassengerMap().get(0);
    }

    public void prepareStatementToRemoveAdditionalSeat(String flightKey, Integer number) {
        passengerCode = null;
        basketId = testData.getData(SerenityFacade.DataKeys.BASKET_ID);
        if (basketId == null) {
            basketId = basketHelper.getBasketService().getResponse().getBasket().getCode();
        }
        queryParam = BasketQueryParams.builder().
                numberOfFare(String.valueOf(number)).build();
        pathParams = BasketPathParams.builder().basketId(basketId).path(MANAGE_ADDITIONAL_SEAT_TO_PASSENGER).passengerId(getFirstPassengerAdultFromFirstOutboundFlightInBasket()).build();
        manageAdditionalFareToPassengerInBasketService = serviceFactory.manageAdditionalFareToPassengerInBasket(new ManageAdditionalFareToPassengerInBasketRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, queryParam));
        // init price for basket
        basketAfterAddingPurchasedSeat = getBasket().getResponse();
        initPriceBeforeRemovingSeat(basketAfterAddingPurchasedSeat, flightKey);
        // init number related fare product
        numberOfProductRelated = basketAfterAddingPurchasedSeat.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).filter(g -> g.getFlightKey().equalsIgnoreCase(flightKey)).flatMap(h -> h.getPassengers().stream()).filter(i -> i.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null).getCabinItems().size();
    }

    public void removeFieldFromRequestBody(String field) {
        if ("basketId".equals(field)) {
            pathParams = BasketPathParams.builder().basketId("invalidBasketID").path(MANAGE_ADDITIONAL_SEAT_TO_PASSENGER).passengerId(getFirstPassengerAdultFromFirstOutboundFlightInBasket()).build();
        } else if ("passengerId".equals(field)) {
            pathParams = BasketPathParams.builder().basketId(testData.getBasketId()).path(MANAGE_ADDITIONAL_SEAT_TO_PASSENGER).passengerId("invalidPassengerID").build();
        }
        manageAdditionalFareToPassengerInBasketService = serviceFactory.manageAdditionalFareToPassengerInBasket(new ManageAdditionalFareToPassengerInBasketRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, queryParam));
    }

    public void invokeRemoveAdditionalSeat() {
        manageAdditionalFareToPassengerInBasketService.invoke();
    }

    public void quantityExceedsThreshold(int quantity) {
        queryParam = BasketQueryParams.builder().numberOfFare(String.valueOf(quantity + 1)).build();
        pathParams = BasketPathParams.builder().basketId(basketId).path(MANAGE_ADDITIONAL_SEAT_TO_PASSENGER).passengerId(getFirstPassengerAdultFromFirstOutboundFlightInBasket()).build();
        manageAdditionalFareToPassengerInBasketService = serviceFactory.manageAdditionalFareToPassengerInBasket(new ManageAdditionalFareToPassengerInBasketRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, queryParam));
    }

    private void initPriceBeforeRemovingSeat(BasketsResponse basketsResponse, String flightKey) {
        initBasketPriceBeforeRemovingSeat(basketsResponse);
        initPassengerPriceBeforeRemovingSeat(basketsResponse, flightKey, getFirstPassengerAdultFromFirstOutboundFlightInBasket());
        initSeatTotalPriceBeforeRemovingSeat(basketsResponse, flightKey, getFirstPassengerAdultFromFirstOutboundFlightInBasket());
    }

    private void initBasketPriceBeforeRemovingSeat(BasketsResponse basketsResponse) {
        basketTotalPrice.setSubtotalAmountWithCreditCard(basketsResponse.getBasket().getSubtotalAmountWithCreditCard());
        basketTotalPrice.setSubtotalAmountWithDebitCard(basketsResponse.getBasket().getSubtotalAmountWithDebitCard());
        basketTotalPrice.setTotalAmountWithCreditCard(basketsResponse.getBasket().getTotalAmountWithCreditCard());
        basketTotalPrice.setTotalAmountWithDebitCard(basketsResponse.getBasket().getTotalAmountWithDebitCard());
    }

    private void initPassengerPriceBeforeRemovingSeat(BasketsResponse basketsResponse, String flightKey, String passCode) {
        passengerTotalPrice.setTotalAmountWithCreditCard(basketsResponse.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).filter(l -> l.getFlightKey().equalsIgnoreCase(flightKey)).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passCode)).findFirst().orElseThrow(() -> new IllegalArgumentException("No passenger with desired code " + passCode)).getPassengerTotalWithCreditCard());
        passengerTotalPrice.setTotalAmountWithDebitCard(basketsResponse.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).filter(l -> l.getFlightKey().equalsIgnoreCase(flightKey)).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passCode)).findFirst().orElseThrow(() -> new IllegalArgumentException("No passenger with desired code " + passCode)).getPassengerTotalWithDebitCard());
    }

    private void initSeatTotalPriceBeforeRemovingSeat(BasketsResponse basketsResponse, String flightKey, String passCode) {
        List<AbstractPassenger.AdditionalSeat> seat = basketsResponse.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).filter(l -> l.getFlightKey().equalsIgnoreCase(flightKey)).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passCode)).map(l -> l.getAdditionalSeats()).findFirst().orElseThrow(() -> new IllegalArgumentException("Seat is not available for the desired passenger " + passCode));
        seatTotalPrice.setTotalAmountWithCreditCard(Objects.isNull(seat) || seat.isEmpty() ? 0 : seat.get(0).getFareProduct().getPricing().getTotalAmountWithCreditCard());
        seatTotalPrice.setTotalAmountWithDebitCard(Objects.isNull(seat) || seat.isEmpty() ? 0 : seat.get(0).getFareProduct().getPricing().getTotalAmountWithDebitCard());
    }

    public void addAdditionalFareExceedsAllowableQuantity(int quantity) {

        AddAdditionalFareToPassengerInBasketRequestBody addAdditionalFareToPassengerInBasketRequestBody =
                AddAdditionalFareToPassengerInBasketRequestBody.builder().numberOfFares(quantity).build();

        pathParams = BasketPathParams.builder().basketId(testData.getBasketId()).
                passengerMap(getPassengerMap()).path(ADD_ADDITIONAL_FARE_TO_PASSENGER).build();

        addAdditionalFareToPassengerInBasketService = serviceFactory.addAdditionalFareToPassengerInBasketService(new AddAdditionalFareRequest(HybrisHeaders.getValid(testData.getChannel()).build(),
                pathParams, addAdditionalFareToPassengerInBasketRequestBody));
        addAdditionalFareToPassengerInBasketService.invoke();
    }

    public void calculateBasketAllTotals() {
        basketService = getBasket();
        Currency currency = basketService.getResponse().getBasket().getCurrency();
        FeesAndTaxesModel fee = feesAndTaxesDao.getAdminFees(currency.getCode()).get(0);
        basketService.assertThat().priceCalculationAreRight(Integer.valueOf(currency.getDecimalPlaces()), fee.getFeeValue());
    }


    public void removeAdditionalSeats(int quantity) {
        if (quantity > 1)
            queryParam = BasketQueryParams.builder().numberOfFare(String.valueOf(quantity)).build();
        else
            queryParam = BasketQueryParams.builder().build();

        pathParams = BasketPathParams.builder().basketId(testData.getBasketId()).path(MANAGE_ADDITIONAL_SEAT_TO_PASSENGER).passengerId(getAdultPassengerWithAdditionalSeat(quantity)).build();
        manageAdditionalFareToPassengerInBasketService = serviceFactory.manageAdditionalFareToPassengerInBasket(new ManageAdditionalFareToPassengerInBasketRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, queryParam));
        manageAdditionalFareToPassengerInBasketService.invoke();
    }

    private String getAdultPassengerWithAdditionalSeat(int quantity) {
        String passengerId = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().stream()
                .filter(f -> (CommonConstants.ADULT).equalsIgnoreCase(f.getPassengerDetails().getPassengerType()))
                .filter(f -> null != f.getAdditionalSeats() && f.getAdditionalSeats().size() >= quantity)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("No desired passenger type ADULT with additional seat is available")).getCode();
        testData.setData(PASSENGER_ID, passengerId);
        return passengerId;
    }

    public void addAdditionalFareToAllPassengerOnAllFlight(Integer number) {
        Basket basket = basketHelper.getBasket(testData.getData(BASKET_ID), testData.getChannel());
        AddAdditionalFareToPassengerInBasketRequestBody addAdditionalFareToPassengerInBasketRequestBody = AddAdditionalFareToPassengerInBasketRequestBody.builder().numberOfFares(number).additionalSeatReason("COMFORT").build();
        List<String> passengersToAdd = basket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(p -> p.getPassengers().stream()).map(pax -> pax.getCode()).collect(Collectors.toList());
        for(String passengerCode : passengersToAdd) {
            BasketPathParams pathParams = BasketPathParams.builder().basketId(basket.getCode()).path(ADD_ADDITIONAL_FARE_TO_PASSENGER).passengerMap(passengerCode).build();
            addAdditionalFareToPassengerInBasketService = serviceFactory.addAdditionalFareToPassengerInBasketService(new AddAdditionalFareRequest(HybrisHeaders.getValid(testData.getChannel()).build(),
                    pathParams, addAdditionalFareToPassengerInBasketRequestBody));
            addAdditionalFareToPassengerInBasketService.invoke();
            addAdditionalFareToPassengerInBasketService.getResponse();
        }
    }

    public void removeAdditionalFareFromPassengers(List<String> passengersToRemove, Integer number) {
        Basket basket = basketHelper.getBasket(testData.getData(BASKET_ID), testData.getChannel());
        BasketQueryParams queryParam = BasketQueryParams.builder().
                numberOfFare(String.valueOf(number)).build();
        for(String passengerCode : passengersToRemove) {
            BasketPathParams pathParams = BasketPathParams.builder().basketId(basket.getCode()).path(MANAGE_ADDITIONAL_SEAT_TO_PASSENGER).passengerId(passengerCode).build();
            manageAdditionalFareToPassengerInBasketService = serviceFactory.manageAdditionalFareToPassengerInBasket(new ManageAdditionalFareToPassengerInBasketRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, queryParam));
            manageAdditionalFareToPassengerInBasketService.invoke();
            manageAdditionalFareToPassengerInBasketService.getResponse();
        }
    }
}
