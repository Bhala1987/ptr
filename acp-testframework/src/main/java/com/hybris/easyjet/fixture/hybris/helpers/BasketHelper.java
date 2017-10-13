package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.database.FlightFinder;
import com.hybris.easyjet.database.hybris.dao.DealDao;
import com.hybris.easyjet.database.hybris.dao.HoldItemsDao;
import com.hybris.easyjet.database.hybris.models.DealModel;
import com.hybris.easyjet.database.hybris.models.HybrisFlightDbModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Passengers;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.BasketQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.FlightQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.factory.FlightQueryParamsFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.AddInfantOnLapRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.AssociateInfantRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.GetAmendableBookingRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.AddFlightRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.AddHoldItemsRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.AddFlightRequestBodyFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.AddInfantOnLapFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.CreateAmendableBasketBodyFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.*;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.GetAmendableBookingRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse.Flight;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractFlights;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AugmentedPriceItem;
import com.hybris.easyjet.fixture.hybris.invoke.services.*;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.*;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.GetAmendableBookingService;
import cucumber.api.Scenario;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.config.constants.CommonConstants.*;
import static com.hybris.easyjet.exceptions.EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INSUFFICIENT_DATA;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoopForSearchBooking;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.*;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams.BookingPaths.AMENDABLE_BOOKING_REQUEST;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.PROFILE;
import static java.lang.Boolean.FALSE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by dwebb on 11/25/2016.
 */
@Component

public class BasketHelper {

    private static final Logger LOG = LogManager.getLogger(BasketHelper.class);
    private static final String OLD_FLIGHT_KEY = "oldFlightKey";

    @Autowired
    private final AddFlightRequestBodyFactory addFlightRequestBodyFactory;
    @Autowired
    private TravellerHelper travellerHelper;
    @Autowired
    private FlightHelper flightHelper;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HoldItemsDao holdItemsDao;

    private final FlightFinder flightFinder;
    private final HybrisServiceFactory serviceFactory;
    private final DealDao dealDao;
    private List<DealModel> deals;
    private FlightsService flightsService;
    private BasketService basketService;
    private DeleteBasketService deleteBasketService;
    private BasketTravellerService basketTravellerService;
    private String fareType;
    private Flight outboundFlight;
    private Flight inboundFlight;
    private AddFlightRequestBody addedFlight;
    private String defaultCurrency = GBP;
    private String flightKey;
    private AddHoldBagToBasketService addHoldBagToBasketService;

    @Getter
    private GetAmendableBookingService getAmendableBookingService;

    /**
     * @param addFlightRequestBodyFactory autowired factory class for generating seatmap request body
     * @param flightFinder                autowired class to find flights that can be used for testing
     * @param serviceFactory              autowired class that returns service objects, bridges the gap from spring to non spring managed classes
     */
    @Autowired
    public BasketHelper(AddFlightRequestBodyFactory addFlightRequestBodyFactory, FlightFinder flightFinder, HybrisServiceFactory serviceFactory, DealDao dealDao) {

        this.addFlightRequestBodyFactory = addFlightRequestBodyFactory;
        this.flightFinder = flightFinder;
        this.serviceFactory = serviceFactory;
        this.dealDao = dealDao;
    }

    /**
     * @return request body representing the seatmap you wish to add to the basket
     */
    AddFlightRequestBody getAddedFlight() {
        return addedFlight;
    }

    /**
     * @return seatmap object returned fom findFlights
     */
    public Flight getOutboundFlight() {
        return outboundFlight;
    }

    /**
     * @param flight       seatmap to add to the basket
     * @param channel      channel to use to call the service
     * @param requiredFare the fare type to be used
     * @throws Exception
     */
    public void addFlightToBasketAsChannelUsingFareCode(Flight flight, String channel, String requiredFare) throws Throwable {
        AddFlightRequestBody aFlight = addFlightRequestBodyFactory.buildFlightRequestWithRequiredFare(flight, testData.getCurrency(), requiredFare);
        addFlightToBasket(HybrisHeaders.getValid(channel).build(), aFlight);
    }

    /**
     * @param flight  seatmap to add to the basket
     * @param channel channel to use to call the service
     * @throws Exception
     */
    public void addFlightToBasketAsChannelUsingFlightCurrency(Flight flight, String channel, String currency) throws Throwable {

        AddFlightRequestBody aFlight = addFlightRequestBodyFactory.buildFlightRequest(flight, currency);
        addFlightToBasket(HybrisHeaders.getValid(channel).build(), aFlight);
    }

    /**
     * @param flight seatmap to add to the basket
     * @throws Exception
     */
    public void addFlightToBasketAsChannel(Flight flight) throws Throwable {
        AddFlightRequestBody aFlight = addFlightRequestBodyFactory.buildFlightRequestWithFareType(flight, testData.getData(BUNDLE));
        addFlightToBasket(HybrisHeaders.getValid(testData.getChannel()).build(), aFlight);
    }

    public void addFlightToBasketAsChannelUsingFlightCurrencyWithPassengerMix(Flight flight, String passengerMix, String channel, String currency, String fare) throws Throwable {

        AddFlightRequestBody aFlight = addFlightRequestBodyFactory.buildFlightRequest(flight, passengerMix, currency);
        aFlight.setFareType(fare);
        addFlightToBasket(HybrisHeaders.getValid(channel).build(), aFlight);
    }

    public void addFlightToBasketAsChannelUsingFlightCurrencyWithPassengerMixAndFareType(Flight flight, String passengerMix, String channel, String currency, String fareType) throws Throwable {
        AddFlightRequestBody aFlight = addFlightRequestBodyFactory.buildFlightRequest(flight, passengerMix, currency);
        aFlight.setFareType(fareType);
        addFlightToBasket(HybrisHeaders.getValid(channel).build(), aFlight);
    }

    public String addFlightToBasketAsChannelUsingFlightCurrencyWithPassengerMixAndFaretypeAndJourney(Flight flight, String passengerMix, String channel, String currency, String fareType, String journey) throws Throwable {

        AddFlightRequestBody aFlight = addFlightRequestBodyFactory.buildFlightRequest(flight, passengerMix, currency);
        aFlight.setFareType(fareType);
        aFlight.setJourneyType(journey);
        addFlightToBasket(HybrisHeaders.getValid(channel).build(), aFlight);
        return aFlight.getFlights().get(0).getFlightKey();
    }

    public void addFlightToBasketAsChannelUsingFlightCurrencyWithPassengerMix(String journeyType, Flight flight, String passengerMix, String channel, String currency) throws Throwable {
        AddFlightRequestBody aFlight;
        if (INBOUND.equalsIgnoreCase(journeyType)) {
            aFlight = addFlightRequestBodyFactory.buildFlightRequest(flight, passengerMix, currency, INBOUND);
        } else {
            aFlight = addFlightRequestBodyFactory.buildFlightRequest(flight, passengerMix, currency, OUTBOUND);
        }
        addFlightToBasket(HybrisHeaders.getValid(channel).build(), aFlight);
    }

    public BasketsResponse addInDirectFlightToBasket(List<FindFlightsResponse.Journey> journeys, String channel, String currency, String journeyType) throws Throwable {
        AddFlightRequestBody aFlight = addFlightRequestBodyFactory.buildFlightRequestForInDirectFlights(journeys.get(0), currency, journeyType);
        addFlightToBasket(HybrisHeaders.getValid(channel).build(), aFlight);
        return getBasketService().getResponse();
    }

    public String addFlightToBasketWithAdditionalSeat(List<Flight> flight, String currency, String passengerMix, String journey, String fareType) throws Throwable {
        return addFlightToBasketWithAdditionalSeat(flight, currency, passengerMix, journey, fareType, false);
    }

    private String addFlightToBasketWithAdditionalSeat(List<Flight> flights, String currency, String passengerMix, String journey, String fareType, Boolean staff) throws Throwable {

        String chosenFlightKey = "";
        Scenario scenario = testData.getData(SCENARIO);
        for (Flight f : flights) {

            AddFlightRequestBody aFlight = addFlightRequestBodyFactory.buildFlightRequestWithAdditionalSeat(f, passengerMix, currency, journey, fareType);
            if (staff) {
                aFlight.setFareType(STAFF);
                aFlight.setBookingType(BookingType.STAFF);
            }
            addFlightToBasket(HybrisHeaders.getValid(testData.getChannel()).build(), aFlight);
            if (!scenario.getSourceTagNames().contains(NEGATIVE_SCENARIO)) {
                AlreadyAllocationSeatHelper alreadyAllocationSeatHelper = new AlreadyAllocationSeatHelper(serviceFactory, this, testData);
                boolean result = alreadyAllocationSeatHelper.verifyAllocationForSeatType(aFlight, basketService.getResponse().getBasket().getCode(), testData.getTypeOfSeat());
                if (result) {
                    chosenFlightKey = aFlight.getFlights().get(0).getFlightKey();
                    break;
                } else {
                    HybrisService.theJSessionCookie.remove();
                }
            } else {
                break;
            }
        }

        if (!scenario.getSourceTagNames().contains(NEGATIVE_SCENARIO) && "".equalsIgnoreCase(chosenFlightKey)) {
            throw new EasyjetCompromisedException(NO_SEAT_AVAILABLE + testData.getTypeOfSeat().name());
        } else {
            return chosenFlightKey;
        }
    }

    public BasketsResponse addOutboundInDirectFlightToBasketAsChannelUsingFlightCurrency(List<Flight> flights, String channel, String currency) throws Throwable {
        AddFlightRequestBody aFlight = addFlightRequestBodyFactory.buildFlightRequestForMultipleFlights(flights, currency, OUTBOUND);
        addFlightToBasket(HybrisHeaders.getValid(channel).build(), aFlight);
        return getBasketService().getResponse();
    }

    public void attemptToAddInboundInDirectFlightToBasketAsChannelUsingFlightCurrency(List<Flight> flights, String channel, String currency) throws Throwable {
        AddFlightRequestBody aFlight = addFlightRequestBodyFactory.buildFlightRequestForMultipleFlights(flights, currency, INBOUND);
        attemptToAddFlightToBasket(HybrisHeaders.getValid(channel).build(), aFlight);
    }

    public void attemptToAddInDirectFlightToBasketAsChannelUsingFlightCurrency(List<FindFlightsResponse.Journey> journeys, String channel, String currency) throws Throwable {
        AddFlightRequestBody aFlight = addFlightRequestBodyFactory.buildFlightRequestForInDirectFlights(journeys.stream().findAny().get(), currency, "single");
        attemptToAddFlightToBasket(HybrisHeaders.getValid(channel).build(), aFlight);
    }

    public void attemptToAddFlightToBasket(HybrisHeaders headers, AddFlightRequestBody aFlight) throws Throwable { //NOSONAR
        basketService = serviceFactory.addFlight(new BasketRequest(headers, aFlight));
        basketService.invoke();
        Scenario scenario = testData.getData(SCENARIO);
        if (!scenario.getSourceTagNames().contains("@negative")) {
            testData.setData(BASKET_SERVICE, basketService);
            testData.setBasketId(basketService.getResponse().getBasket().getCode());
        }
    }

    private void attemptToAddFlightToBasketWithPassengerMix(HybrisHeaders headers, AddFlightRequestBody aFlight, String PassengerMix) throws Throwable { //NOSONAR

        FlightPassengers passengers = new FlightPassengers(PassengerMix);
        aFlight.setPassengers(passengers.getPassengers());

        basketService = serviceFactory.addFlight(new BasketRequest(headers, aFlight));
        basketService.invoke();
    }

    /**
     * @param numberOfFlights    number of flights to add to the basket
     * @param numberOfPassengers number of passengers to be booked on each seatmap
     * @param channel            channel to use to call the service
     * @throws Throwable
     */
    private void addFlightToBasketAsChannel(int numberOfFlights, int numberOfPassengers, String channel) throws Throwable {

        addFlightToBasketAsChannel(numberOfFlights, numberOfPassengers, channel, false, null);
    }

    public void addFlightToBasketAsChannel(int numberOfFlights, int numberOfPassengers, String channel, boolean withFlightTax, String bundles) throws Throwable {

        for (AddFlightRequestBody flightToAdd : addFlightRequestBodyFactory.flightsToAdd(numberOfFlights, numberOfPassengers, withFlightTax, bundles, channel)) {
            addFlightToBasket(HybrisHeaders.getValid(channel).build(), flightToAdd);
        }
    }

    public void addFlightToBasketAsChannel(int numberOfPassengers, String channel) throws Throwable {

        addFlightToBasketAsChannel(1, numberOfPassengers, channel);
    }

    /**
     * @param flight  the fight to add to the basket
     * @param channel the channel to use to call the service
     * @throws Exception
     */
    public void addFlightToBasketAsChannel(AddFlightRequestBody flight, String channel) throws Throwable {
        attemptToAddFlightToBasket(HybrisHeaders.getValid(channel).build(), flight);
        testData.setData(BASKET_SERVICE, basketService);
    }

    /**
     * @param flight  the fight to add to the basket
     * @param channel the channel to use to call the service
     * @throws Exception
     */
    public void addFlightToSameBasketAsChannel(AddFlightRequestBody flight, String jSessionId, String channel) throws Throwable {
        HybrisHeaders headers = HybrisHeaders.getValid(channel).build();
        headers.setCookie(jSessionId);
        attemptToAddFlightToBasket(headers, flight);
    }

    /**
     * @param flight  the fight to add to the basket
     * @param channel the channel to use to call the service
     * @throws Exception
     */
    public void addFlightToBasketAsChannel(AddFlightRequestBody flight, String channel, String passengerMix) throws Throwable {
        attemptToAddFlightToBasketWithPassengerMix(HybrisHeaders.getValid(channel).build(), flight, passengerMix);
    }

    /**
     * @param flight the seatmap to add to the basket
     * @return the request body for the seatmap
     */
    public AddFlightRequestBody createAddFlightRequest(Flight flight) {
        return addFlightRequestBodyFactory.buildFlightRequest(flight);
    }

    /**
     * @param flight the seatmap to add to the basket
     * @return the request body for the seatmap
     */
    public AddFlightRequestBody createAddFlightRequestForStaff(Flight flight) {
        return addFlightRequestBodyFactory.buildFlightRequestForStaff(flight);
    }

    /**
     * @param flight      the seatmap to add to the basket
     * @param bookingType
     * @param fareType
     * @return the request body for the seatmap
     */
    public AddFlightRequestBody createAddFlightRequestWithBookingTypeAndFareType(Flight flight, String bookingType, String fareType, String passangerMix) {
        return addFlightRequestBodyFactory.buildFlightRequestWithBookingTypeAndFareType(flight, bookingType, fareType, passangerMix);
    }

    /**
     * @return the basket service object
     */
    public BasketService getBasketService() {
        return basketService;
    }

    /**
     * @return the bastket traveller service object
     */
    public BasketTravellerService getBasketPassengerService() {
        return basketTravellerService;
    }

    /**
     * calls the update traveller service with channel Digital
     *
     * @param passenger the traveller to update
     * @param channel   the channel to call the service with
     * @param basketId  the basket Id for the basket you wish to update
     */
    public void updatePassengersForChannel(Passengers passenger, String channel, String basketId) {
        BasketPathParams pathParams = BasketPathParams.builder()
                .basketId(basketId)
                .path(PASSENGER)
                .build();
        basketTravellerService = serviceFactory.updatePassengers(
                new BasketTravellerRequest(
                        HybrisHeaders.getValid(channel).build(),
                        pathParams,
                        passenger
                )
        );
        basketTravellerService.invoke();
        testData.setData(SERVICE, basketTravellerService);
    }

    /**
     * calls the get basket service for the basket id provided for the DIGITAL channel
     *
     * @param basketId the basket Id for the basket you want to get
     */

    public void getBasket(String basketId) {
        if (testData.getChannel() == null) {
            getBasket(basketId, DIGITAL_CHANNEL);
        } else {
            getBasket(basketId, testData.getChannel());
        }
    }

    /**
     * calls the get basket service for the basket id provided for the channel provided
     *
     * @param basketId the basket Id for the basket you want to get
     * @param channel  the channel to be used in the header
     */
    public Basket getBasket(String basketId, String channel) {

        BasketPathParams pathParams = BasketPathParams.builder().basketId(basketId).build();
        //TODO this is a known
        basketService = serviceFactory.getBasket(new BasketRequest(HybrisHeaders.getValid(channel)
                .build(), pathParams));
        invokeWithRetry();
        return basketService.getResponse().getBasket();
    }

    /**
     * calls the get basket service for the basket id provided for the channel provided
     *
     * @param basketId the basket Id for the basket you want to get
     * @param channel  the channel to be used in the header
     */
    public void invokeGetBasket(String basketId, String channel) {

        BasketPathParams pathParams = BasketPathParams.builder().basketId(basketId).build();
        basketService = serviceFactory.getBasket(new BasketRequest(HybrisHeaders.getValid(channel)
                .build(), pathParams));
        basketService.invoke();
    }

    /**
     * calls the get basket service for the basket id provided for the channel provided
     *
     * @param basketId                     the basket Id for the basket you want to get
     * @param channel                      the channel to be used in the header
     * @param checkPassengerDetailsUpdated if this is true we are checking at least one passenger has last name isn't null and get basket
     */
    public Basket getBasket(String basketId, String channel, boolean checkPassengerDetailsUpdated) {

        if (checkPassengerDetailsUpdated) {
            BasketPathParams pathParams = BasketPathParams.builder().basketId(basketId).build();
            //TODO this is a known
            basketService = serviceFactory.getBasket(new BasketRequest(HybrisHeaders.getValid(channel)
                    .build(), pathParams));
            pollingLoop().until(() -> {
                final int[] count = {0};
                basketService.invoke();
                basketService.getResponse().getBasket().getOutbounds().forEach(
                        outbound ->
                                outbound.getFlights().forEach(
                                        flight ->
                                                flight.getPassengers().forEach(
                                                        passenger -> {
                                                            if (passenger.getPassengerDetails().getName().getLastName() != null) {
                                                                count[0]++;
                                                            }
                                                        }
                                                )));
                return count[0] > 0;
            });
            return basketService.getResponse().getBasket();
        } else return getBasket(basketId, channel);
    }

    /**
     * @param numberOfFlights the number of flights you want to add to the basket
     * @throws Throwable
     */
    public void addNumberOfFlightsToBasketForDigital(int numberOfFlights) throws Throwable {

        for (AddFlightRequestBody flightToAdd : addFlightRequestBodyFactory.flightsToAdd(numberOfFlights, DIGITAL_CHANNEL)) {
            addFlightToBasket(flightToAdd);
        }
    }

    /**
     * @param numberOfFlights the number of flights to add to the basket
     * @param channel         the channel to call the service with
     * @return a list of flights that have been added to the basket
     * @throws Throwable
     */
    public List<AddFlightRequestBodyFactory.multiFlightData> addNumberOfFlightsToBasket(int numberOfFlights, String channel) throws Throwable {
        for (AddFlightRequestBody flightToAdd : addFlightRequestBodyFactory.flightsToAdd(numberOfFlights, channel)) {
            addFlightToBasketAsChannel(flightToAdd, channel);
        }
        return addFlightRequestBodyFactory.getMultiFlightAvailableData();
    }

    /**
     * @param addFlights list of flight request body that should be added to the basket
     * @param channel    the channel to call the service with
     * @return a list of flights that have been added to the basket
     * @throws Throwable
     */
    List<AddFlightRequestBodyFactory.multiFlightData> addNumberOfFlightsToBasket(List<AddFlightRequestBody> addFlights, String channel) throws Throwable {

        for (AddFlightRequestBody flightToAdd : addFlights) {
            addFlightToBasketAsChannel(flightToAdd, channel);
        }
        return addFlightRequestBodyFactory.getMultiFlightAvailableData();
    }

    /**
     * @return list of flights
     * @throws Throwable
     */
    public List<AddFlightRequestBody> findMultipleFlightsWithDifferentBaseCurrencies(String channel) throws Throwable {
        return addFlightRequestBodyFactory.flightsToAdd(2, true, channel);
    }

    /**
     * @param basketId the basket id for the basket you want
     * @param channel  the channel to use to call the service
     * @return the basket response
     */
    public void emptyBasket(String basketId, String channel) {
        BasketQueryParams queryParam = BasketQueryParams.builder().actionType("empty").basketId(basketId).build();
        deleteBasketService = serviceFactory.deleteBasket(new BasketRequest(HybrisHeaders.getValid(channel)
                .build(), null, queryParam));
        deleteBasketService.invoke();
        deleteBasketService.assertThat();
    }

    /**
     * @param passengerMix the passenger mix
     * @throws Throwable
     */
    public void myBasketContainsAFlightWithPassengerMix(String passengerMix) throws Throwable {
        myBasketContainsAFlightWithPassengerMix(
                passengerMix,
                (testData.getChannel() == null ? DIGITAL_CHANNEL : testData.getChannel()),
                STANDARD, false
        );
        testData.setData(BUNDLE, STANDARD);
    }

    public void myBasketContainsAReturnFlightWithPassengerMix(String passengerMix, String channel) throws Throwable {
        testData.setFareType(STANDARD);
        addReturnFlightWithPaxMixToBasket(passengerMix, channel, testData.getFareType());
    }

    /**
     * The method add the flight to basket. The method may choose a flight considering the availability for a specific seat type.
     * To use this utility you HAVE TO SPECIFY throw SerenityFacade: -setTypeOfSeat(SEAT_PRODUCT), -setVerifySeatAllocation(true)
     * It is also possible do not specify any seat, in this case a random seat will be picked up from the available list
     * If you do not specify the setVerifySeatAllocation, the method simply add a generic flight without checking for the availability
     *
     * @param passengerMix passenger in the basket
     * @param channel      channel to use
     * @param fare         fare to use
     * @return flight key added in the basket
     * @throws Throwable
     */
    public String myBasketContainsAFlightWithPassengerMix(String passengerMix, String channel, String fare, boolean Apis) throws Throwable {
        return myBasketContainsAFlightWithPassengerMix(passengerMix, channel, false, fare, Apis);
    }

    /**
     * The method add the flight to basket for STAFF customer.
     * NB: in order to use this method you need a staff customer in the session
     * The method may choose a flight considering the availability for a specific seat type.
     * To use this utility you HAVE TO SPECIFY throw SerenityFacade: -setTypeOfSeat(SEAT_PRODUCT), -setVerifySeatAllocation(true)
     * It is also possible do not specify any seat, in this case a random seat will be picked up from the available list
     * If you do not specify the setVerifySeatAllocation, the method simply add a generic flight without checking for the availability
     *
     * @param passengerMix
     * @param channel
     * @param fareType
     * @throws Throwable
     */
    public void myBasketContainsAFlightWithPassengerMixForStaff(String passengerMix, String channel, String fareType) throws Throwable {
        this.myBasketContainsAFlightWithPassengerMix(passengerMix, channel, true, fareType, false);
    }

    private String myBasketContainsAFlightWithPassengerMix(String passengerMix, String channel, Boolean staff, String fare, boolean Apis) throws Throwable {
        testData.setPassengerMix(passengerMix);
        AddFlightRequestBody flight;
        List<Flight> flightToCheck;

        try {
            if (testData.getOrigin() == null && testData.getDestination() == null) {
                flightHelper.setSectors();
            }
            if (Apis) {
                flightsService = flightHelper.getFlights(testData.getChannel(), passengerMix, testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate(), fare, testData.getCurrency());
                flightToCheck = flightsService.getOutboundFlights();
            } else {
                flightsService = flightHelper.getFlights(testData.getChannel(), passengerMix, testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate(), fare, testData.getCurrency());
                flightToCheck = flightsService.getOutboundFlights();
            }
            defaultCurrency = flightsService.getResponse().getCurrency();
            // retrieve valid flight for availability

            String chosenFlightKey = "";
            for (Flight f : flightToCheck) {
                outboundFlight = f;
//               Please do not remove below written testData.setData() as it is used for flight keys with spaces in it
//               It is used in check in step where ACP is expecting original flight key with space in it
                testData.setFlightKey(outboundFlight.getFlightKey().replace(" ",""));
                testData.setData(FLIGHT_KEY, outboundFlight.getFlightKey().replace(" ",""));

                if (staff) {
                    flight = addFlightRequestBodyFactory.buildFlightRequestForStaff(outboundFlight, defaultCurrency, fare);
                } else {
                    flight = addFlightRequestBodyFactory.buildFlightRequestWithRequiredFareAndPassengerMix(outboundFlight, defaultCurrency, fare, passengerMix);
                }
                testData.setData(BOOKING_TYPE, flight.getBookingType());

                fareType = flight.getFareType();
                addFlightToBasketAsChannel(flight, channel);

                AlreadyAllocationSeatHelper alreadyAllocationSeatHelper = new AlreadyAllocationSeatHelper(serviceFactory, this, testData);
                boolean result = alreadyAllocationSeatHelper.verifyAllocationForSeatType(flight, basketService.getResponse().getBasket().getCode(), testData.getTypeOfSeat());
                if (result) {
                    chosenFlightKey = flight.getFlights().get(0).getFlightKey();
                    break;
                } else {
                    HybrisService.theJSessionCookie.remove();
                }
            }

            if ("".equalsIgnoreCase(chosenFlightKey)) {
                throw new EasyjetCompromisedException(NO_SEAT_AVAILABLE + testData.getTypeOfSeat().name());
            } else {
                testData.setData(FLIGHT_KEY, chosenFlightKey);
                return chosenFlightKey;
            }

        } catch (Exception e) {
            LOG.error(SEATMAP_ERROR, e);
            throw e;
        }
    }

    /**
     * @param passengerMix the passenger mix
     * @throws Throwable
     */
    public String addFlightToBasket(String passengerMix, String src, String dest, boolean staff, String fare, String bookingType) throws Throwable {
        return addFlightToBasketInternal(passengerMix, src, dest, staff, fare, bookingType);
    }

    private String addFlightToBasketInternal(String passengerMix, String src, String dest, boolean staff, String fare, String bookingType) throws Throwable {

        AddFlightRequestBody aFlight;

        try {
            flightHelper.setSectors();
            if ("present".equalsIgnoreCase(testData.getPeriod())) {
                flightsService = flightHelper.getFlights(testData.getChannel(), passengerMix, src, dest, null, testData.getOutboundDate(), testData.getInboundDate(), fare, testData.getCurrency());
            } else {
                flightsService = flightHelper.getFlights(testData.getChannel(), passengerMix, src, dest, fare, testData.getCurrency());
            }
            testData.setData(GET_FLIGHT_SERVICE, flightsService);

            outboundFlight = flightsService.getOutboundFlight();
            testData.setFlightKey(outboundFlight.getFlightKey());
            testData.setData(FLIGHT_KEY, outboundFlight.getFlightKey());

            if (staff) {
                aFlight = addFlightRequestBodyFactory.buildFlightRequestForStaff(outboundFlight, flightsService.getResponse().getCurrency(), fare);
            } else {
                aFlight = addFlightRequestBodyFactory.buildFlightRequestWithRequiredFare(outboundFlight, flightsService.getResponse().getCurrency(), fare, bookingType);
            }
            aFlight.setCurrency(testData.getCurrency());
            addFlightToBasketAsChannel(aFlight, testData.getChannel());

            return aFlight.getFlights().get(0).getFlightKey();

        } catch (Exception e) {
            LOG.error(SEATMAP_ERROR, e);
            throw e;
        }
    }

    public String createBasketWithPassengerMix(FlightsService flightsService, String journeyType, String fareType, String bookingType, String channel, Boolean staff) throws Throwable {
        AddFlightRequestBody aFlight;
        try {
            defaultCurrency = flightsService.getResponse().getCurrency();
            outboundFlight = flightsService.getOutboundFlight();

            if (staff) {
                aFlight = addFlightRequestBodyFactory.buildFlightRequestForStaff(outboundFlight, defaultCurrency, fareType);
            } else {
                aFlight = addFlightRequestBodyFactory.buildFlightRequestWithSpefiedJourney(outboundFlight, defaultCurrency, fareType, journeyType, bookingType);
            }
            addedFlight = aFlight;
            addFlightToBasketAsChannel(aFlight, channel);
            return addedFlight.getFlights().get(0).getFlightKey();
        } catch (Exception e) {
            LOG.error(SEATMAP_ERROR, e);
            throw e;
        }

    }

    void myBasketContainsAFlightWithPassengerMixBasedOnDates(String passengerMix, String channel) throws Throwable {
        AddFlightRequestBody aFlight;
        try {
            flightsService = flightHelper.getFlights(testData.getChannel(), passengerMix, testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate());
            defaultCurrency = flightsService.getResponse().getCurrency();
            outboundFlight = flightsService.getOutboundFlight();
            aFlight = addFlightRequestBodyFactory.buildFlightRequest(outboundFlight, defaultCurrency);
            fareType = aFlight.getFareType();
            addedFlight = aFlight;
            addFlightToBasketAsChannel(aFlight, channel);
        } catch (Exception e) {
            LOG.error(SEATMAP_ERROR, e);
            throw e;
        }
    }

    public void addCustomFlightToTheBasket(String flightKey, String route, String passengerMix, String channel, String bundle) throws Throwable {

        try {
            flightsService = flightHelper.getFlights(testData.getChannel(), passengerMix, testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate());
            outboundFlight = flightsService.getOutboundFlight();
            defaultCurrency = flightsService.getResponse().getCurrency();
            AddFlightRequestBody aFlight;
            if (bundle.toLowerCase().contains(STAFF.toLowerCase()) || "standby".equalsIgnoreCase(bundle)) {
                aFlight = addFlightRequestBodyFactory.buildFlightRequestForStaff(outboundFlight, defaultCurrency, bundle);
            } else if ("flexi".equalsIgnoreCase(bundle)) {
                aFlight = addFlightRequestBodyFactory.buildFlightRequestForFlexi(outboundFlight, defaultCurrency);
            } else {
                aFlight = addFlightRequestBodyFactory.buildFlightRequest(outboundFlight, defaultCurrency);
            }

            fareType = aFlight.getFareType();
            addedFlight = aFlight;
            addedFlight.getFlights().get(0).setFlightKey(flightKey);
            addedFlight.setRouteCode(route);
            addFlightToBasket(aFlight, channel);
        } catch (Exception e) {
            LOG.error(SEATMAP_ERROR, e);
            throw e;
        }
    }

    void myBasketContainsAFlightWithPassengerMixWithDeal(String passengerMix, String channel, DealModel dealsModel) throws Throwable {
        FlightPassengers passengers = new FlightPassengers(passengerMix);
        try {
            FlightQueryParams flightQueryParams =
                    FlightQueryParamsFactory.generateFlightSearchCriteria(flightFinder.findAValidFlight(passengers.getTotalNumberOfPassengers()), passengers);
            FlightsService flightService;
            if (dealsModel == null) {
                flightService = serviceFactory.findFlight(
                        new FlightsRequest(HybrisHeaders.getValidWithDealInfo(channel,
                                "test", "test", "test").build(), flightQueryParams));
            } else {

                flightService = serviceFactory.findFlight(
                        new FlightsRequest(HybrisHeaders.getValidWithDealInfo(channel,
                                dealsModel.getSystemName(), dealsModel.getOfficeId(), dealsModel.getCorporateId())
                                .build(), flightQueryParams));
            }
            flightService.invoke();
            outboundFlight = flightService.getOutboundFlight();
            AddFlightRequestBody aFlight = addFlightRequestBodyFactory.buildFlightRequest(outboundFlight);
            fareType = aFlight.getFareType();
            addedFlight = aFlight;
            addFlightToBasketWithDeal(aFlight, dealsModel, channel);
        } catch (Exception e) {
            LOG.error(SEATMAP_ERROR, e);
            throw e;
        }

    }

    public void associateCustomerProfileToBasket(String aChannel, String aCustomerUUID) throws Throwable { //NOSONAR

        CustomerPathParams profilePathParams = CustomerPathParams.builder()
                .customerId(aCustomerUUID)
                .path(PROFILE)
                .build();
        CustomerProfileService customerProfileService = serviceFactory.getCustomerProfile(new ProfileRequest(HybrisHeaders.getValid(aChannel)
                .build(), profilePathParams));
        customerProfileService.invoke();
    }

    /**
     * @param flight the seatmap to add to the basket
     */
    public void addAFlightToMyBasket(Flight flight, String channel, boolean overrideWarning) throws Throwable {
        AddFlightRequestBody addFlight = addFlightRequestBodyFactory.buildFlightRequest(flight);
        addFlight.setOverrideWarning(overrideWarning);
        addFlightToBasket(addFlight, channel);
    }

    public void addAFlightToMyBasketWithDifferentPrice(Flight flight, String channel, boolean overrideWarning) throws Throwable {
        AddFlightRequestBody addFlight = addFlightRequestBodyFactory.buildFlightRequest(flight);
        addFlight.getFlights().stream().forEach(
                flight1 -> {
                    flight1.setFlightPrice(flight1.getFlightPrice() + 10);
                }
        );
        addFlightToBasket(addFlight, channel);
    }

    private void addFlightToBasket(AddFlightRequestBody addFlight, String channel) throws Throwable {

        addFlightToBasket(HybrisHeaders.getValid(channel).build(), addFlight);
    }

    private void addFlightToBasket(AddFlightRequestBody addFlight) throws Throwable {

        addFlightToBasket(addFlight, DIGITAL_CHANNEL);
    }

    private void addFlightToBasketWithDeal(AddFlightRequestBody addFlight, DealModel dealsModel, String channel) throws Throwable {

        if (dealsModel != null) {
            addFlightToBasket(HybrisHeaders.getValidWithDealInfo(channel, dealsModel.getSystemName(), dealsModel.getOfficeId(), dealsModel
                    .getCorporateId()).build(), addFlight);
        } else {
            addFlightToBasket(HybrisHeaders.getValidWithDealInfo(channel, "test", "test", "test").build(), addFlight);
        }
    }

    private void addFlightToBasket(HybrisHeaders headers, AddFlightRequestBody addFlight) throws Throwable { //NOSONAR
        basketService = serviceFactory.addFlight(new BasketRequest(headers, addFlight));
        basketService.invoke();
        Scenario scenario = testData.getData(SCENARIO);
        if (!scenario.getSourceTagNames().contains("@negative")) {
            testData.setData(BASKET_SERVICE, basketService);
            if (!Optional.ofNullable(basketService.getErrors()).isPresent()) {
                testData.setBasketId(basketService.getResponse().getBasket().getCode());
                testData.setData(BASKET_ID,  basketService.getResponse().getBasket().getCode());
                testData.setData(OUTBOUND_FLIGHT, basketService.getResponse().getBasket().getOutbounds());
            }
        }
    }

    void addFlightToMyBasket(AddFlightRequestBody addFlight, String channel) throws Throwable {

        addFlightToBasket(HybrisHeaders.getValid(channel).build(), addFlight);
    }

    public void addReturnFlightWithTaxToBasketAsChannelJourneyType(FlightsService flightsService, String passengerMix, String currency, String channel, String bundles, String journeyType) throws Throwable {

        //add Flights seatmap to basket for Single journey
        if ("SINGLE".equalsIgnoreCase(journeyType)) {
            Flight flight = flightsService.getResponse().getOutbound().getJourneys().stream()
                    .flatMap(p -> p.getFlights().stream())
                    .filter(f -> f.getAvailableStatus().equals(AVAILABLE) && f.getFareTypes().stream()
                            .anyMatch(ff -> ff.getFareTypeCode().equalsIgnoreCase(bundles)))
                    .findFirst().orElse(null);
            if (flight == null) {
                throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
            }
            testData.setData(OLD_FLIGHT_KEY, flight.getFlightKey());
            AddFlightRequestBody addFlight = addFlightRequestBodyFactory.buildFlightRequest(flight, passengerMix, currency, "OUTBOUND", bundles, null);
            addFlightToBasket(HybrisHeaders.getValid(channel).build(), addFlight);
            String outboundArrivalDate = flight.getArrival().getDate();
            testData.setData(OUTBOUND_DATE, outboundArrivalDate);

        } else {
            //add Flights and Flights seatmap to basket for journey
            Flight outbound = flightsService.getResponse().getOutbound().getJourneys().stream()
                    .flatMap(p -> p.getFlights().stream())
                    .filter(f -> f.getAvailableStatus().equals(AVAILABLE) && f.getFareTypes().stream()
                            .anyMatch(ff -> ff.getFareTypeCode().equalsIgnoreCase(bundles)))
                    .findFirst().orElse(null);
            if (outbound == null) {
                throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
            }
            AddFlightRequestBody addFlight = addFlightRequestBodyFactory.buildFlightRequest(outbound, passengerMix, currency, "OUTBOUND", bundles, null);
            addFlightToBasket(HybrisHeaders.getValid(channel).build(), addFlight);
            String outboundArrivalDate = outbound.getArrival().getDate();
            testData.setData(OUTBOUND_DATE, outboundArrivalDate);
            Flight inbound = flightsService.getResponse().getInbound().getJourneys().stream()
                    .flatMap(p -> p.getFlights().stream())
                    .filter(f -> f.getAvailableStatus().equals(AVAILABLE) && f.getFareTypes().stream()
                            .anyMatch(ff -> ff.getFareTypeCode().equalsIgnoreCase(bundles)) && getDate(f.getArrival().getDate()).after(getAddedDepDate(outboundArrivalDate)))
                    .findFirst().orElse(null);
            if (inbound == null) {
                LOG.info("INBOUND IS NULL !!!");
            }
            AddFlightRequestBody inboundFlight = addFlightRequestBodyFactory.buildFlightRequest(inbound, passengerMix, currency, "INBOUND", bundles, null);
            addFlightToBasket(HybrisHeaders.getValid(channel).build(), inboundFlight);
            if (inbound != null) {
                testData.setData(INBOUND_DATE, inbound.getDeparture().getDate());
            }
        }
    }

    public void addReturnFlightWithTaxToBasketAsChannel(int numberOfPassengers, String channel, boolean withFlightTax, String bundles) throws Throwable {

        HashMap<String, HybrisFlightDbModel> availableFlight = flightFinder.findAValidFlightWithReturn(numberOfPassengers, withFlightTax, bundles);

        FlightQueryParams params = FlightQueryParamsFactory.generateReturnFlightSearchCriteria(availableFlight)
                .adult(String.valueOf(numberOfPassengers))
                .build();
        flightsService = serviceFactory.findFlight(new FlightsRequest(HybrisHeaders.getValid(channel).build(), params));
        flightsService.invoke();
        //add Flights seatmap to basket
        AddFlightRequestBody addFlight = addFlightRequestBodyFactory.buildFlightRequest(flightsService.getResponse()
                        .getOutbound()
                        .getJourneys()
                        .stream()
                        .flatMap(p -> p.getFlights().stream())
                        .filter(f -> f.getAvailableStatus().equals(AVAILABLE))
                        .findFirst()
                        .orElse(null),
                numberOfPassengers, "OUTBOUND");
        addFlightToBasket(HybrisHeaders.getValid(channel).build(), addFlight);
        //Add Flight seatmap to basket
        AddFlightRequestBody inboundFlight = addFlightRequestBodyFactory.buildFlightRequest(flightsService.getResponse()
                        .getInbound()
                        .getJourneys()
                        .stream()
                        .flatMap(p -> p.getFlights().stream())
                        .filter(f -> f.getAvailableStatus().equals(AVAILABLE))
                        .findFirst()
                        .orElse(null),
                numberOfPassengers, "INBOUND");
        addFlightToBasket(HybrisHeaders.getValid(channel).build(), inboundFlight);
    }

    public void addInboundOutboundFlights(String passengerMix, String fare) throws Throwable {
        addReturnFlightWithPaxMixToBasket(passengerMix, testData.getChannel(), fare);

    }

    public void addInboundOutboundFlightsForStaff(String passengerMix, String fare) throws Throwable {
        addReturnFlightWithPaxMixToBasketAsStaff(passengerMix, testData.getChannel(), fare);
    }

    private void addReturnFlightWithPaxMixToBasket(String passengerMix, String channel, String fare) throws Throwable {
        AddFlightRequestBody aFlight;
        flightHelper.setSectors();
        flightsService = flightHelper.getFlights(testData.getChannel(), passengerMix, testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate(), fare);
        defaultCurrency = flightsService.getResponse().getCurrency();
        try {
            AlreadyAllocationSeatHelper alreadyAllocationSeatHelper = new AlreadyAllocationSeatHelper(serviceFactory, this, testData);
            // retrieve valid outbound and inbound flight for availability
            List<Flight> outboundFlightToCheck = flightsService.getOutboundFlights();
            List<Flight> inboundFlightToCheck = flightsService.getInboundFlights();

            String chosenOutboundFlightKey = "";
            String chosenInboundFlightKey = "";
            for (Flight f : outboundFlightToCheck) {
                outboundFlight = f;

                testData.setJourneyType(OUTBOUND);
                aFlight = addFlightRequestBodyFactory.buildFlightRequestWithRequiredFare(outboundFlight, defaultCurrency, fare);
                testData.setJourneyType(null);

                fareType = aFlight.getFareType();
                addFlightToBasketAsChannel(aFlight, channel);

                //selecting the first available flight, hence using get(0)
                boolean result = alreadyAllocationSeatHelper.verifyAllocationForSeatType(aFlight, basketService.getResponse().getBasket().getCode(), testData.getTypeOfSeat());
                if (result) {
                    chosenOutboundFlightKey = aFlight.getFlights().get(0).getFlightKey();
                    break;
                } else {
                    HybrisService.theJSessionCookie.remove();
                }
            }
            for (Flight f : inboundFlightToCheck) {
                inboundFlight = f;

                testData.setJourneyType(INBOUND);
                aFlight = addFlightRequestBodyFactory.buildFlightRequestWithRequiredFare(inboundFlight, defaultCurrency, fare);
                testData.setJourneyType(null);

                fareType = aFlight.getFareType();
                addFlightToBasketAsChannel(aFlight, channel);

                //selecting the first available flight, hence using get(0)
                boolean result = alreadyAllocationSeatHelper.verifyAllocationForSeatType(aFlight, basketService.getResponse().getBasket().getCode(), testData.getTypeOfSeat());
                if (result) {
                    chosenInboundFlightKey = aFlight.getFlights().get(0).getFlightKey();
                    break;
                } else {
                    HybrisService.theJSessionCookie.remove();
                }
            }

            if (("".equalsIgnoreCase(chosenOutboundFlightKey)) && ("".equalsIgnoreCase(chosenInboundFlightKey))) {
                throw new EasyjetCompromisedException("No seat are available from seating service for the desired type " + testData.getTypeOfSeat().name());
            }

        } catch (Exception e) {
            LOG.error(SEATMAP_ERROR, e);
            throw e;
        }
    }

    private void addReturnFlightWithPaxMixToBasketAsStaff(String passengerMix, String channel, String fare) throws Throwable {
        AddFlightRequestBody aFlight;
        flightsService = flightHelper.getFlights(testData.getChannel(), passengerMix, testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate(), fare);
        defaultCurrency = flightsService.getResponse().getCurrency();
        try {
            AlreadyAllocationSeatHelper alreadyAllocationSeatHelper = new AlreadyAllocationSeatHelper(serviceFactory, this, testData);
            // retrieve valid outbound and inbound flight for availability
            List<Flight> outboundFlightToCheck = flightsService.getOutboundFlights();
            List<Flight> inboundFlightToCheck = flightsService.getInboundFlights();

            String chosenOutboundFlightKey = "";
            String chosenInboundFlightKey = "";
            String outBoundArrivalTime = "";
            for (Flight f : outboundFlightToCheck) {
                outboundFlight = f;

                testData.setJourneyType(OUTBOUND);
                aFlight = addFlightRequestBodyFactory.buildFlightRequestWithRequiredFare(outboundFlight, defaultCurrency, fare);
                aFlight.setBookingType("STAFF");
                aFlight.setFareType(STAFF);
                testData.setJourneyType(null);
                fareType = aFlight.getFareType();
                addFlightToBasketAsChannel(aFlight, channel);
                outBoundArrivalTime = outboundFlight.getArrival().getDate();
                testData.setData(OUTBOUND_DATE, outBoundArrivalTime);
                //selecting the first available flight, hence using get(0)
                boolean result = alreadyAllocationSeatHelper.verifyAllocationForSeatType(aFlight, basketService.getResponse().getBasket().getCode(), testData.getTypeOfSeat());
                if (result) {
                    chosenOutboundFlightKey = aFlight.getFlights().get(0).getFlightKey();
                    break;
                } else {
                    HybrisService.theJSessionCookie.remove();
                }
            }
            for (Flight f : inboundFlightToCheck) {
                inboundFlight = f;
                if (getDate(inboundFlight.getArrival().getDate()).after(getAddedDepDate(outBoundArrivalTime))) {
                    testData.setJourneyType(INBOUND);
                    aFlight = addFlightRequestBodyFactory.buildFlightRequestWithRequiredFare(inboundFlight, defaultCurrency, fare);
                    testData.setJourneyType(null);
                    aFlight.setBookingType("STAFF");
                    aFlight.setFareType(STAFF);
                    fareType = aFlight.getFareType();
                    addFlightToBasketAsChannel(aFlight, channel);
                    testData.setData(INBOUND_DATE, inboundFlight.getDeparture().getDate());
                    //selecting the first available flight, hence using get(0)
                    boolean result = alreadyAllocationSeatHelper.verifyAllocationForSeatType(aFlight, basketService.getResponse().getBasket().getCode(), testData.getTypeOfSeat());
                    if (result) {
                        chosenInboundFlightKey = inboundFlight.getFlightKey();
                        break;
                    } else {
                        HybrisService.theJSessionCookie.remove();
                    }
                }
            }

            if (("".equalsIgnoreCase(chosenOutboundFlightKey)) && ("".equalsIgnoreCase(chosenInboundFlightKey))) {
                throw new EasyjetCompromisedException("No seat are available from seating service for the desired type " + testData.getTypeOfSeat().name());
            }

        } catch (Exception e) {
            LOG.error(SEATMAP_ERROR, e);
            throw e;
        }
    }

    public void myBasketContainsWithPassengerMixWithDeal(String channel, String dealParameters, String passengerMix) throws Throwable {

        if (StringUtils.isNotEmpty(dealParameters)) {
            if ("ApplicationId,OfficeId".equalsIgnoreCase(dealParameters)) {
                deals = dealDao.getDeals(false, null, null);
            } else {
                deals = dealDao.getDeals(true, null, null);
            }
        }
        addDealInTheBasket(channel, passengerMix);
    }

    public void myBasketContainsWithPassengerMixWithDealWithPos(String channel, String dealParameters, String passengerMix) throws Throwable {

        if (StringUtils.isNotEmpty(dealParameters)) {
            if ("ApplicationId,OfficeId".equalsIgnoreCase(dealParameters)) {
                deals = dealDao.getDeals(false, true, true);
            } else {
                deals = dealDao.getDeals(true, true, true);
            }
        }
        addDealInTheBasket(channel, passengerMix);
    }

    private void addDealInTheBasket(String channel, String passengerMix) throws Throwable {

        String applicationId = deals.get(0).getSystemName();
        String officeId = deals.get(0).getOfficeId();
        String corporateId = deals.get(0).getCorporateId();

        FlightPassengers passengers = new FlightPassengers(passengerMix);
        fareType = "avalon".equalsIgnoreCase(applicationId) ? "Inclusive" : "Standard,Flexi";
        try {
            FlightQueryParams flightQueryParams =
                    FlightQueryParamsFactory.generateFlightSearchCriteria(flightFinder.findAValidFlight(passengers.getTotalNumberOfPassengers()), passengers, fareType);
            FlightsService flightService = serviceFactory.findFlight(
                    new FlightsRequest(HybrisHeaders.getValidWithDealInfo(channel, applicationId, officeId,
                            corporateId).build(), flightQueryParams));
            flightService.invoke();
            AddFlightRequestBody aFlight = "Inclusive".equalsIgnoreCase(fareType) ? addFlightRequestBodyFactory.buildFlightRequestWithFareType(flightService.getOutboundFlight(), fareType) : addFlightRequestBodyFactory.buildFlightRequest(flightService.getOutboundFlight());
            addFlightToBasket(HybrisHeaders.getValidWithDealInfo(channel, applicationId, officeId, corporateId)
                    .build(), aFlight);

        } catch (Exception e) {
            LOG.error(SEATMAP_ERROR, e);
            throw e;
        }
    }

    public String getFlightKeyForTheFlightWhichIsInBasketFor(String passengerMix, String channel) throws Throwable {

        return getFlightKeyForTheFlightWhichIsInBasketFor(passengerMix, channel, false);
    }

    private String getFlightKeyForTheFlightWhichIsInBasketFor(String passengerMix, String channel, Boolean staff) throws Throwable {
        AddFlightRequestBody aFlight;

        try {
            flightsService = flightHelper.getFlights(testData.getChannel(), passengerMix, testData.getOrigin(), testData.getDestination(), OUTBOUND);
            testData.setData(GET_FLIGHT_SERVICE, flightsService);
            defaultCurrency = flightsService.getResponse().getCurrency();
            outboundFlight = flightsService.getOutboundFlight();

            if (staff) {
                aFlight = addFlightRequestBodyFactory.buildFlightRequestForStaff(outboundFlight, defaultCurrency, STAFF);
            } else {
                aFlight = addFlightRequestBodyFactory.buildFlightRequest(outboundFlight, defaultCurrency);
            }

            fareType = aFlight.getFareType();
            addedFlight = aFlight;
            addFlightToBasketAsChannel(aFlight, channel);
            return aFlight.getFlights().get(0).getFlightKey();
        } catch (Exception e) {
            LOG.error("Error getting a valid flight", e);
            throw e;
        }
    }

    public Passengers createRequestToUpdateFirstPassenger(String passengerMix, String channel) throws Throwable {
        flightKey = myBasketContainsAFlightWithPassengerMix(passengerMix, channel, STANDARD, false);
        return travellerHelper.updateInformationForFirstPassenger(getBasketService().getResponse().getBasket(), flightKey);
    }

    public void invokeUpdatePassengerService(Passengers passenger, String channel) {
        updatePassengersForChannel(passenger, channel, getBasketService().getResponse().getBasket().getCode());
    }

    public Passengers createRequestToUpdatePassengerExcludeCode(String passengerCode) throws EasyjetCompromisedException {
        return travellerHelper.updateInformationForFirstPassengerExcludeCode(getBasketService().getResponse().getBasket(), passengerCode, flightKey);
    }

    public Passengers createRequestToUpdateAllPassengerOnSameFlight() throws EasyjetCompromisedException {
        return travellerHelper.updateInformationForAllPassengerOnSameFlight(getBasketService().getResponse().getBasket(), flightKey);
    }

    public Passengers createRequestToUpdateAllPassenger(boolean required) throws EasyjetCompromisedException {
        return travellerHelper.updateInformationForAllPassenger(getBasketService().getResponse().getBasket(),required);
    }

    public void addMultipleFlight(int numFlight, String passengerMix, String channel) throws Throwable {
        for (int i = 0; i < numFlight; i++) {
            myBasketContainsAFlightWithPassengerMix(passengerMix, channel, STANDARD, false);
            testData.setOrigin("LTN");
            testData.setDestination("CDG");
        }
    }

    public AddFlightRequestBody myBasketContainsAFlightWithPassengerMixAndBundle(String passengerMix, String channel, String bundle) throws Throwable {
        boolean staff = false;
        if (bundle.toLowerCase().contains(STAFF) || "standby".equalsIgnoreCase(bundle)) {
            staff = true;
        }
        AddFlightRequestBody aFlight;
        try {
            flightsService = flightHelper.getFlights(testData.getChannel(), passengerMix, testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate());
            if (testData.getCurrency() == null) {
                testData.setCurrency(flightsService.getResponse().getCurrency());
            }
            outboundFlight = flightsService.getOutboundFlight();

            if ("flexi".equalsIgnoreCase(bundle)) {
                aFlight = addFlightRequestBodyFactory.buildFlightRequestForFlexi(outboundFlight, testData.getCurrency());
            } else if (staff) {
                aFlight = addFlightRequestBodyFactory.buildFlightRequestForStaff(outboundFlight, testData.getCurrency(), bundle);
            } else {
                aFlight = addFlightRequestBodyFactory.buildFlightRequest(outboundFlight, testData.getCurrency());
            }

            fareType = aFlight.getFareType();
            addedFlight = aFlight;
            addFlightToBasketAsChannel(aFlight, channel);
            return addedFlight;
        } catch (Exception e) {
            LOG.error(SEATMAP_ERROR, e);
            throw e;
        }
    }

    public void addFlightsToBasket(String fareType, String journeyType) throws Throwable {
        List<Flight> flights;
        FlightsService flightsService = testData.getData(GET_FLIGHT_SERVICE);
        switch (journeyType.toLowerCase()) {
            case OUTBOUND:
                flights = flightsService.getOutboundFlights();
                break;
            case INBOUND:
                flights = flightsService.getInboundFlights();
                break;
            default:
                flights = flightsService.getOutboundFlights();
                break;
        }

        boolean staff = false;
        if (CommonConstants.STAFF
                .equalsIgnoreCase(fareType) || CommonConstants.STANDBY
                .equalsIgnoreCase(fareType)) {
            staff = true;
        }

        String actualFlightKey =
                addFlightToBasketWithAdditionalSeat(
                        flights
                        , flightsService.getResponse().getCurrency()
                        , testData.getPassengerMix()
                        , journeyType
                        , fareType
                        , staff
                );
        testData.setActualFlightKey(actualFlightKey);
    }

    void addSportEquipmentForAllPassengers(int quantity) {
        if (quantity > 0) {
            Basket basket = getBasketService().getResponse().getBasket();
            String flightKey = basket.getOutbounds().get(0).getFlights().get(0).getFlightKey();
            BasketPathParams pathParams = BasketPathParams.builder().basketId(basket.getCode()).path(SPORT_EQUIP).build();
            basket.getOutbounds().get(0).getFlights().get(0).getPassengers().forEach(
                    passenger -> {
                        if (!INFANT.equalsIgnoreCase(passenger.getPassengerDetails().getPassengerType())) {
                            AddHoldItemsRequestBody body =
                                    AddHoldItemsRequestBody.builder()
                                            .productCode("Snowboard")
                                            .quantity(quantity)
                                            .passengerCode(passenger.getCode())
                                            .flightKey(flightKey)
                                            .price(getAppropriatePrice("Snowboard"))
                                            .override(false)
                                            .build();

                            addHoldBagToBasketService = serviceFactory.addHoldBagToBasket(new AddHoldItemsToBasketRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, body));
                            addHoldBagToBasketService.invoke();
                        }
                    }
            );

            getBasket(basket.getCode(), testData.getChannel());

            addHoldBagToBasketService.assertThat().holdBagAddedForEachPassenger(getBasketService().getResponse());
        }
    }

    public void addHoldAndExcessWeightBagsForAllPassengers(int holdItemCount, int excessWeightCount) {
        if (holdItemCount > 0) {
            Basket basket = getBasketService().getResponse().getBasket();
            String flightKey = basket.getOutbounds().get(0).getFlights().get(0).getFlightKey();
            BasketPathParams pathParams = BasketPathParams.builder().basketId(basket.getCode()).path(HOLD_BAG).build();
            basket.getOutbounds().get(0).getFlights().get(0).getPassengers().forEach(
                    passenger -> {
                        if (!"infant".equalsIgnoreCase(passenger.getPassengerDetails().getPassengerType())) {
                            AddHoldItemsRequestBody body =
                                    AddHoldItemsRequestBody.builder()
                                            .productCode("20kgbag")
                                            .quantity(holdItemCount)
                                            .passengerCode(passenger.getCode())
                                            .flightKey(flightKey)
                                            //commented as we can not add excess weight if there are more than one hold bag
//                                            .excessWeightProductCode("3kgextraweight")
//                                            .excessWeightQuantity(excessWeightCount)
                                            .price(getAppropriatePrice("20kgbag"))
//                                            .excessWeightPrice(getAppropriatePrice("3kgextraweight"))
                                            .override(false)
                                            .build();

                            addHoldBagToBasketService = serviceFactory.addHoldBagToBasket(new AddHoldItemsToBasketRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, body));
                            addHoldBagToBasketService.invoke();
                        }
                    }
            );

            getBasket(basket.getCode(), testData.getChannel());
            addHoldBagToBasketService.assertThat().holdBagAddedForEachPassenger(getBasketService().getResponse());
        }
    }

    private Double getAppropriatePrice(String productCode) {
        Map<String, List<HashMap<String, Double>>> activeProducts = holdItemsDao.returnActiveProducts(testData.getChannel(), "GBP");
        List<HashMap<String, Double>> price = activeProducts.get(productCode);
        // returns two in the list, price can be fetched from any of these two, using the first in the list to fetch
        return price.get(0).get("price");
    }

    public String getPassenger(final String paxType) {

        List<Basket.Passenger> passengers = basketService.getResponse().getBasket().getOutbounds().stream()
                .flatMap(flights -> flights.getFlights().stream().flatMap(flight -> flight.getPassengers().stream()))
                .collect(Collectors.toList());
        return passengers.stream().filter(passenger -> passenger.getPassengerDetails().getPassengerType().equalsIgnoreCase(paxType) &&
                passenger.getInfantsOnLap().isEmpty()).findFirst().orElse(null).getCode();


    }

    /**
     * calls the get basket service and returns response for the basket id provided for the channel provided
     *
     * @param basketId the basket Id for the basket you want to get
     * @param channel  the channel to be used in the header
     */
    public BasketsResponse getBasketResponse(String basketId, String channel) {

        BasketPathParams pathParams = BasketPathParams.builder().basketId(basketId).build();
        basketService = serviceFactory.getBasket(new BasketRequest(HybrisHeaders.getValid(channel)
                .build(), pathParams));
        basketService.invoke();
        return basketService.getResponse();
    }

    public String createAmendableBasket(String bookingRef) {
        BookingPathParams params = BookingPathParams.builder().bookingId(bookingRef).path(AMENDABLE_BOOKING_REQUEST).build();
        GetAmendableBookingRequestBody getAmendableBookingRequestBody = CreateAmendableBasketBodyFactory.createABodyForBookingLevelAmendableBasket(FALSE);
        getAmendableBookingService = serviceFactory.getAmendableBooking(new GetAmendableBookingRequest(HybrisHeaders.getValid(testData.getChannel()).build(), params, getAmendableBookingRequestBody));
        final int[] attempts = {3};
        try {
            pollingLoopForSearchBooking().until(() -> {
                getAmendableBookingService.invoke();
                attempts[0]--;
                return getAmendableBookingService.getStatusCode() == 200 || attempts[0] == 0;
            });
        } catch(Exception e) {}
        testData.setData(BASKET_ID, getAmendableBookingService.getResponse().getOperationConfirmation().getBasketCode());
        return getAmendableBookingService.getResponse().getOperationConfirmation().getBasketCode();
    }

    public List<Basket.Passenger> getPassengersBasedOnType(List<Basket.Flights> journey, String type) {
        return journey.stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                .filter(passenger1 -> passenger1.getPassengerDetails().getPassengerType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    public List<Basket.Passenger> getAllPassengerRecordsFromAllFlights(Basket basket, List<String> passengerCodes) {
        return Stream.concat(
                basket.getInbounds().stream(),
                basket.getOutbounds().stream()
        ).flatMap(flights -> flights.getFlights().stream())
                .flatMap(flight -> flight.getPassengers().stream())
                .filter(passenger -> passengerCodes.contains(passenger.getCode()))
                .collect(Collectors.toList());
    }

    List<Basket.Passenger> getAllPassengerRecordsFromAllFlights(Basket basket) {
        return Stream.concat(
                basket.getInbounds().stream(),
                basket.getOutbounds().stream()
        ).flatMap(flights -> flights.getFlights().stream())
                .flatMap(flight -> flight.getPassengers().stream())
                .collect(Collectors.toList());
    }

    public AssociateInfantService changeAssociationInfantAdult(String passengerAdultCode, String passengerInfantCode) {
        BasketPathParams pathParams = BasketPathParams.builder().basketId(basketService.getResponse().getBasket().getCode()).path(ASSOCIATE_INFANT).passengerId(passengerAdultCode).build();
        AssociateInfantRequestBody associateInfantRequestBody = AssociateInfantRequestBody.builder().infantPassengerId(passengerInfantCode).build();
        AssociateInfantService associateInfantService = serviceFactory.associateInfant(new AssociateInfantRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, associateInfantRequestBody));
        testData.setData(SERVICE, associateInfantService);
        associateInfantService.invoke();

        return associateInfantService;
    }

    /**
     * @param passengerMix the passenger mix
     * @param channel      the channel to use to call the service
     * @throws Throwable
     */
    public void myBasketContainsManyFlightWithPassengerMix(int numberOfFlight, String passengerMix, String channel, String fare, String journey) throws Throwable {
        aBasketWitManyFlightWithPassengerMix(numberOfFlight, passengerMix, channel, fare, journey);
    }

    private void aBasketWitManyFlightWithPassengerMix(int numberOfFlight, String passengerMix, String channel, String fare, String journey) throws Throwable {
        AddFlightRequestBody aFlight;
        flightsService = flightHelper.getFlights(testData.getChannel(), passengerMix, testData.getOrigin(), testData.getDestination(), journey, testData.getOutboundDate(), testData.getInboundDate(), fare, testData.getCurrency());
        defaultCurrency = flightsService.getResponse().getCurrency();
        flightsService.assertThat().atLeastOneOutboundFlightWasReturned().theFlightHasAFlightKey();
        // retrieve valid flight for availability
        List<Flight> flightToCheck = flightsService.getOutboundFlights();
        List<Flight> addedFlights = new ArrayList<>();
        int countAddedFlight = 0;
        for (Flight f : flightToCheck) {
            if (!addedFlights.contains(f)) {
                outboundFlight = f;
                if (countAddedFlight == 0) {
                    testData.setData(OLD_FLIGHT_KEY, outboundFlight.getFlightKey());
                }

                aFlight = addFlightRequestBodyFactory.buildFlightRequestWithRequiredFareAndPassengerMix(outboundFlight, defaultCurrency, fare, passengerMix);

                fareType = aFlight.getFareType();
                addFlightToBasketAsChannel(aFlight, channel);

                AlreadyAllocationSeatHelper alreadyAllocationSeatHelper = new AlreadyAllocationSeatHelper(serviceFactory, this, testData);
                boolean result = alreadyAllocationSeatHelper.verifyAllocationForSeatType(aFlight, basketService.getResponse().getBasket().getCode(), testData.getTypeOfSeat());
                if (result) {
                    addedFlights.add(f);
                    countAddedFlight++;
                    if (addedFlights.size() >= numberOfFlight) {
                        return;
                    }
                } else {
                    HybrisService.theJSessionCookie.remove();
                    /**
                     * In case the flight has not the availability for the seat type,
                     * we need to remove deleting cache. This operation delete also the other so is required add again the previously flight where there are enough availability
                     */
                    for (Flight reAddFlight : addedFlights) {
                        addFlightToBasketAsChannel(addFlightRequestBodyFactory.buildFlightRequestWithRequiredFareAndPassengerMix(reAddFlight, defaultCurrency, fare, passengerMix), channel);
                    }
                }
            }
        }
    }

    private static Calendar getAddedDepDate(String depDate) {
        Calendar date = getDate(depDate);
        date.add(Calendar.DATE, 1);
        return date;

    }

    private static Calendar getDate(String dateTime) {
        Calendar calender = Calendar.getInstance();
        try {
            calender.setTime(getDateFormatterWithDay().parse(dateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calender;
    }

    private static SimpleDateFormat getDateFormatterWithDay() {
        return new SimpleDateFormat("EEE d-MMM-yyyy HH:mm:ss");
    }

    public void clearBasket(String basketId, String channel) {
        BasketQueryParams queryParam;
        if(Objects.nonNull(basketId))
         queryParam = BasketQueryParams.builder().actionType("empty").basketId(basketId).build();
        else
            queryParam = BasketQueryParams.builder().actionType("empty").build();
        deleteBasketService = serviceFactory.deleteBasket(new BasketRequest(HybrisHeaders.getValid(channel)
                .build(), null, queryParam));
        deleteBasketService.invoke();
    }

    public List<Basket.Passenger> getPassengerWithInfantOnLap(Basket basket) {
        return basket.getOutbounds().stream()
                .flatMap(flights -> flights.getFlights().stream())
                .flatMap(p -> p.getPassengers().stream())
                .filter(p -> p.getFareProduct().getOrderEntryNumber().contains("0"))
                .collect(Collectors.toList());
    }

    public List<Basket.Passenger> getInfantOnLapOnFlight(Basket basket){
        return basket.getOutbounds().stream()
                .flatMap(flights -> flights.getFlights().stream())
                .flatMap(p -> p.getPassengers().stream())
                .filter(p -> "infant".equalsIgnoreCase(p.getPassengerDetails().getPassengerType()))
                .collect(Collectors.toList());
    }

    private void invokeWithRetry() {
        int[] noOfRetry = {5};
        try {

            pollingLoop().until(() -> {
                basketService.invoke();
                noOfRetry[0]--;
                return basketService.getStatusCode() == 200 || noOfRetry[0] == 0;

            });
        }catch (ConditionTimeoutException ct){
            LOG.error(ct);
            basketService.getResponse();
        }
    }

    public BigDecimal getRefundAmtForBookingLessThan24Hr(GetBookingResponse getBookingResponse,Basket basket, String passengerCode){
        // If the cancellation is pre 24 hrs of the booking then the refund amount is the original passenger booking amount minus cancel fee

        List<GetBookingResponse.Passenger> passengers = getBookingResponse.getBookingContext().getBooking().getOutbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList());
        passengers.addAll(getBookingResponse.getBookingContext().getBooking().getInbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList()));
        for (GetBookingResponse.Passenger passenger : passengers) {
            if (passenger.getCode().equals(passengerCode)) {
                int currencyDigits = Integer.valueOf(basket.getCurrency().getDecimalPlaces());
                AugmentedPriceItem cancelFeeItem = basket.getFees().getItems().stream().filter(feeItem -> feeItem.getCode().contains("CancelFee_")).findFirst().orElse(null);
                return new BigDecimal((passenger.getTotalAmount())).setScale(currencyDigits, RoundingMode.UP).subtract(BigDecimal.valueOf(cancelFeeItem.getAmount()).setScale(currencyDigits,RoundingMode.UP)).setScale(currencyDigits,RoundingMode.UP);
            }
        }

        return BigDecimal.ZERO;
    }

    public BigDecimal getCancelAmtForBookingLessThan24Hr(GetBookingResponse getBookingResponse,Basket basket, String passengerCode){
        // If the cancellation is pre 24 hrs of the booking then the cancel fee is the cancellation fees charged ex: 14

        List<GetBookingResponse.Passenger> passengers = getBookingResponse.getBookingContext().getBooking().getOutbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList());
        passengers.addAll(getBookingResponse.getBookingContext().getBooking().getInbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList()));
        for (GetBookingResponse.Passenger passenger : passengers) {
            if (passenger.getCode().equals(passengerCode)) {
                int currencyDigits = Integer.valueOf(basket.getCurrency().getDecimalPlaces());
                AugmentedPriceItem cancelFeeItem = basket.getFees().getItems().stream().filter(feeItem -> feeItem.getCode().contains("CancelFee_")).findFirst().orElse(null);
                return (BigDecimal.valueOf(cancelFeeItem.getAmount()).setScale(currencyDigits,RoundingMode.UP)).setScale(currencyDigits,RoundingMode.UP);
            }
        }

        return BigDecimal.ZERO;
    }

    public BigDecimal getRefundAmtForBookingGreaterThan24Hr(GetBookingResponse getBookingResponse,Basket basket, String passengerCode){
        // If the cancellation is post 24 hrs of the booking then the refund amount is zero

        List<GetBookingResponse.Passenger> passengers = getBookingResponse.getBookingContext().getBooking().getOutbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList());
        passengers.addAll(getBookingResponse.getBookingContext().getBooking().getInbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList()));
        for (GetBookingResponse.Passenger passenger : passengers) {
            if (passenger.getCode().equals(passengerCode)) {
                int currencyDigits = Integer.valueOf(basket.getCurrency().getDecimalPlaces());
                AugmentedPriceItem cancelFeeItem = basket.getFees().getItems().stream().filter(feeItem -> feeItem.getCode().contains("CancelFee_")).findFirst().orElse(null);
                return BigDecimal.ZERO;
            }
        }

        return BigDecimal.ZERO;
    }

    public BigDecimal getCancelAmtForBookingGreaterThan24Hr(GetBookingResponse getBookingResponse,Basket basket, String passengerCode){
        // If the cancellation is post 24 hrs of the booking then the cancel fee is same as the original passenger amount
        List<GetBookingResponse.Passenger> passengers = getBookingResponse.getBookingContext().getBooking().getOutbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList());
        passengers.addAll(getBookingResponse.getBookingContext().getBooking().getInbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList()));
        for (GetBookingResponse.Passenger passenger : passengers) {
            if (passenger.getCode().equals(passengerCode)) {
                int currencyDigits = Integer.valueOf(basket.getCurrency().getDecimalPlaces());
                return new BigDecimal((passenger.getTotalAmount())).setScale(currencyDigits, RoundingMode.UP).setScale(currencyDigits,RoundingMode.UP);
            }
        }

        return BigDecimal.ZERO;
    }

    /**
     * This method is used to add infant on lap
     * @param channel     the channel to use to call the service
     * @param basketID    the basket Id for the basket you wish to update
     * @param passengerID the adult traveller to associate to an infant
     */
    public void invokeAddInfantOnLap(String channel, String basketID, String passengerID) {
        BasketPathParams basketPathParams = BasketPathParams.builder()
                .basketId(basketID)
                .passengerId(passengerID)
                .path(BasketPathParams.BasketPaths.ADD_INFANT_ON_LAP)
                .build();

        AddInfantOnLapRequestBody addInfantOnLapRequestBody = AddInfantOnLapFactory.getAddInfantOnLapBody();
        AddInfantOnLapService addInfantOnLapService = serviceFactory
                .addInfantOnLap(new AddInfantOnLapRequest(HybrisHeaders.getValid(channel).build(), basketPathParams, addInfantOnLapRequestBody));

        addInfantOnLapService.invoke();
        testData.setData(SerenityFacade.DataKeys.INFANT_ON_LAP_ID, addInfantOnLapService.getResponse().getOperationConfirmation().getInfantOnLapPassengerCode());
    }

    public BigDecimal getCancellationFeesForCancellingInLessThan24Hr(Basket basket) {
        int currencyDigits = Integer.valueOf(basket.getCurrency().getDecimalPlaces());
        AugmentedPriceItem cancelFeeItem = basket.getFees().getItems().stream().filter(feeItem -> feeItem.getCode().contains("CancelFee_")).findFirst().orElse(null);
        return (cancelFeeItem != null) ? (BigDecimal.valueOf(cancelFeeItem.getAmount()).setScale(currencyDigits, RoundingMode.UP)).setScale(currencyDigits, RoundingMode.UP) : BigDecimal.ZERO;
    }

    public int checkRemovedAdditionalSeatStatusCount(Basket basket) {
        Optional<Basket.Passenger> passenger = basket.getOutbounds().get(0).getFlights().get(0).getPassengers().stream()
                .filter(p -> (testData.getData(PASSENGER_ID)).equals(p.getCode()))
                .findFirst();
        if(passenger.isPresent())
            return passenger.get().getAdditionalSeats().stream()
                    .filter(seat -> !seat.getFareProduct().getActive() && seat.getFareProduct().getEntryStatus().equals("CHANGED"))
                    .collect(Collectors.toList()).size();

        return 0;
    }

    public void checkBasketTotalAfterRemovalOfAdditionalSeats(Basket originalBasket, Basket amendedBasket, double cancellationFees, boolean purchasedSeats) {
        double creditCardFees = getCreditCardFees(amendedBasket);
        double sumOfFareOfAllCancelledAddlSeats = checkRemovedAdditionalSeatFare(amendedBasket, purchasedSeats);
        double totalDebitCardAmountOnAmendedBasket = checkTotalDebitCardAmount(amendedBasket);
        Double totalCreditCardAmountOnAmendedBasket = checkTotalCreditCardAmount(amendedBasket);
        Double totalDebitCardAmountOnOriginalBasket = checkTotalDebitCardAmount(originalBasket);

        DecimalFormat df = new DecimalFormat("#.##");

        Double totalDebitCardAmount_AmendedBasket = Double.valueOf(df.format(totalDebitCardAmountOnAmendedBasket + sumOfFareOfAllCancelledAddlSeats - cancellationFees));
        Double totalCC_Amt = Double.valueOf(df.format(totalDebitCardAmountOnAmendedBasket + creditCardFees));

        assertThat(totalDebitCardAmountOnOriginalBasket.compareTo(totalDebitCardAmount_AmendedBasket)).isEqualTo(0);
        assertThat(totalCreditCardAmountOnAmendedBasket.compareTo(totalCC_Amt)).isEqualTo(0);
    }

    private double checkTotalDebitCardAmount(Basket amendedBasket) {
        return amendedBasket.getTotalAmountWithDebitCard();
    }

    private double checkTotalCreditCardAmount(Basket amendedBasket) {
        return amendedBasket.getTotalAmountWithCreditCard();
    }


    private double getCreditCardFees(Basket basket) {
        AugmentedPriceItem cancelFeeItem = basket.getFees().getItems().stream().filter(feeItem -> feeItem.getCode().contains("CRCardFee")).findFirst().orElse(null);
        return cancelFeeItem != null ? cancelFeeItem.getAmount() : 0;
    }

    public double checkRemovedAdditionalSeatFare(Basket basket, boolean purchasedSeats) {
        Optional<Basket.Passenger> passenger = basket.getOutbounds().get(0).getFlights().get(0).getPassengers().stream()
                .filter(p -> (testData.getData(PASSENGER_ID)).equals(p.getCode()))
                .findFirst();
        Double totalAmount = 0.0;
        if (passenger.isPresent()) {
            Double sumOfFareOfAllCancelledAddlSeats = passenger.get().getAdditionalSeats().stream()
                    .filter(seat -> !seat.getFareProduct().getActive() && seat.getFareProduct().getEntryStatus().equals("CHANGED"))
                    .map(additionalSeat -> additionalSeat.getFareProduct().getPricing().getTotalAmountWithDebitCard()).mapToDouble(Double::doubleValue).sum();

            if (purchasedSeats) {
                Double sumOfFareOfAlladdlnCancelledPurchasedSeats = passenger.get().getAdditionalSeats().stream()
                        .filter(seat -> !seat.getSeat().getActive() && seat.getSeat().getEntryStatus().equals("CHANGED"))
                        .map(additionalSeat -> additionalSeat.getSeat().getPricing().getTotalAmountWithDebitCard()).mapToDouble(Double::doubleValue).sum();
                totalAmount = sumOfFareOfAllCancelledAddlSeats + sumOfFareOfAlladdlnCancelledPurchasedSeats;
            } else
                totalAmount = sumOfFareOfAllCancelledAddlSeats;

        }
        return totalAmount;
    }

    public int checkRemovedPurchasedSeatStatusCount(Basket amendedBasket) {
        Optional<Basket.Passenger> passenger = amendedBasket.getOutbounds().get(0).getFlights().get(0).getPassengers().stream()
                .filter(p -> (testData.getData(PASSENGER_ID)).equals(p.getCode()))
                .findFirst();
        if (passenger.isPresent())
            return passenger.get().getAdditionalSeats().stream()
                    .filter(seat -> !seat.getSeat().getActive() && seat.getSeat().getEntryStatus().equals("CHANGED"))
                    .collect(Collectors.toList()).size();

        return 0;
    }
    public void removeFlightFromBasket(String basketId, String flightKey){
        BasketPathParams basketPathParams = BasketPathParams.builder().basketId(basketId).path(BasketPathParams.BasketPaths.REMOVE_FLIGHT).flightKey(flightKey).build();
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
        RemoveFlightFromBasketService removeFlightService = serviceFactory.removeFlightFromBasket(new RemoveFlightFromBasketRequest(Objects.nonNull(headers) ? headers.build() : HybrisHeaders.getValid(testData.getChannel()).build(), basketPathParams));

        removeFlightService.invoke();
    }
}
