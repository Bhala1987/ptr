package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.data.FlightSearch;
import com.hybris.easyjet.database.FlightFinder;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.FlightQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.factory.FlightQueryParamsFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.AddFlightRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.Flight;
import com.hybris.easyjet.fixture.hybris.invoke.requests.FlightsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import lombok.Getter;
import org.fluttercode.datafactory.impl.DataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;


/**
 * Created by dwebb on 12/12/2016.
 */
@Component
public class FlightHelper {

    @Autowired
    @Getter
    private FlightFinder flightFinder;
    @Autowired
    SerenityFacade testData;

    private HybrisServiceFactory serviceFactory;
    private FlightsService flightService;
    FlightQueryParams params;
    private static final Random rand = new Random(System.currentTimeMillis());
    private String flexibleDays = "2";

    @Autowired
    public FlightHelper(HybrisServiceFactory serviceFactory, FlightFinder flightFinder) {
        this.flightFinder = flightFinder;
        this.serviceFactory = serviceFactory;
    }

    public static AddFlightRequestBody setFieldAsInvalid(String key, AddFlightRequestBody flight) {
        DataFactory df = new DataFactory();
        switch (key) {
            case "flightKeyInvalid":
                flight.setFlights(populateFlightsWithInvalidKey(df.getRandomChars(8)));
                break;
            case "fareTypeInvalid":
                flight.setFareType(df.getRandomChars(8));
                break;
            case "journeyTypeInvalid":
                flight.setJourneyType(df.getRandomChars(8));
                break;
            case "priceInvalid":
                flight.setRoutePrice(-100.00);
                break;
            case "TOEICodeInvalid":
                flight.setToeiCode(df.getRandomChars(8));
                break;
            default:
                break;
        }

        return flight;

    }

    private static List<Flight> populateFlightsWithInvalidKey(String key) {
        List<com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.Flight> flightList = new ArrayList<>();
        flightList.add(com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.Flight.builder().flightKey(key)
                .build());
        return flightList;
    }

    public FindFlightsResponse.Flight updateInfantQuantity(int quantity, FindFlightsResponse.Flight flight, boolean infantOnSeat) {
        flight.getFareTypes().forEach(
                f -> f.getPassengers().forEach(
                        p -> {
                            if ("infant".equalsIgnoreCase(p.getType().toLowerCase())) {
                                p.setQuantity(quantity);
                                p.setAdditionalSeats(0);
                                p.setInfantOnSeat(infantOnSeat);
                            }
                        }
                )
        );

        return flight;

    }

    public AddFlightRequestBody setFieldAsMissing(String key, AddFlightRequestBody flight) {
        switch (key) {
            case "flightKey":
                flight.getFlights().get(0).setFlightKey(null);
                break;
            case "fareType":
                flight.setFareType(null);
                break;
            case "journeyType":
                flight.setJourneyType(null);
                break;
            case "price":
                flight.getFlights().get(0).setFlightPrice(null);
                flight.setRoutePrice(null);
                break;
            case "TOEICode":
                flight.setToeiCode(null);
                break;
            case "passengers":
                flight.setPassengers(null);
                break;
            case "currency":
                flight.setCurrency(null);
                break;
            case "routeCode":
                flight.setRouteCode(null);
                break;
            default:
                break;
        }

        return flight;

    }

    /**
     * The method look for a flight with parameter as:
     *
     * @param channel
     * @param passengerMix
     * @param origin
     * @param destination
     * @param journeyType
     * @return
     * @throws Exception
     */
    public FlightsService getFlights(String channel, String passengerMix, String origin, String destination, String journeyType) throws NullPointerException, ParseException {
        FlightPassengers passengers = new FlightPassengers(passengerMix);
        params = FlightQueryParamsFactory.generateFlightSearchCriteria(passengers, origin, destination, new DateFormat().today().addDay(2), new DateFormat().today().addDay(15), null);
        flightService = serviceFactory.findFlight(new FlightsRequest(HybrisHeaders.getValid(channel).build(), params));
        flightService.invoke();
        return flightService;
    }

    /**
     * The method look for a flight with parameter as:
     *
     * @param channel
     * @param passengerMix
     * @param origin
     * @param destination
     * @param currency
     * @return
     * @throws Exception
     */
    public FlightsService getFlights(String channel, String passengerMix, String origin, String destination, String fare, String currency) throws Exception {
        FlightPassengers passengers = new FlightPassengers(passengerMix);
        if (testData.getData(SerenityFacade.DataKeys.FLEXIBLE_DAYS) != null) {
            flexibleDays = testData.getData(SerenityFacade.DataKeys.FLEXIBLE_DAYS);
        }
        if ("elv".equalsIgnoreCase(testData.getPaymentCode())) {
            testData.setData(OUTBOUND_DATE, getDateFromToday(15));
            testData.setData(INBOUND_DATE, getDateFromToday(17));
            flexibleDays = null;
        } else {
            testData.setData(OUTBOUND_DATE, getDateFromToday(2));
            testData.setData(INBOUND_DATE, getDateFromToday(10));
        }
        params = FlightQueryParamsFactory.generateFlightSearchCriteria(passengers, origin, destination, testData.getData(OUTBOUND_DATE), testData.getData(INBOUND_DATE), flexibleDays);
        params.setFareTypes(fare);
        params.setCurrency(currency);
        flightService = serviceFactory.findFlight(new FlightsRequest(HybrisHeaders.getValid(channel).build(), params));
        flightService.invoke();
        return flightService;
    }

    private String getDateFromToday(int days) throws ParseException {
        return new DateFormat().today().addDay(days);
    }

    /**
     * The method look for a flight with parameter as:
     *
     * @param channel
     * @param passengerMix
     * @param origin
     * @param destination
     * @param journeyType
     * @param outboundDate
     * @param inboundDate
     * @return
     * @throws Throwable
     */
    public FlightsService getFlights(String channel, String passengerMix, String origin, String destination, String journeyType, String outboundDate, String inboundDate) throws Throwable { //NOSONAR
        FlightPassengers passengers = new FlightPassengers(passengerMix);
        params = FlightQueryParamsFactory.generateFlightSearchCriteria(passengers, origin, destination, outboundDate, inboundDate, "2");
        flightService = serviceFactory.findFlight(new FlightsRequest(HybrisHeaders.getValid(channel).build(), params));
        flightService.invoke();
        return flightService;
    }

    /**
     * The method look for a flight with parameter as:
     *
     * @param channel
     * @param passengerMix
     * @param origin
     * @param destination
     * @param journeyType
     * @param outboundDate
     * @param inboundDate
     * @param fare
     * @return
     * @throws NullPointerException
     */
    public FlightsService getFlights(String channel, String passengerMix, String origin, String destination, String journeyType, String outboundDate, String inboundDate, String fare) throws NullPointerException { //NOSONAR
        FlightPassengers passengers = new FlightPassengers(passengerMix);
        params = FlightQueryParamsFactory.generateFlightSearchCriteria(passengers, origin, destination, outboundDate, inboundDate, "2");
        params.setFareTypes(fare);
        flightService = serviceFactory.findFlight(new FlightsRequest(HybrisHeaders.getValid(channel).build(), params));
        flightService.invoke();
        return flightService;
    }

    /**
     * The method look for a flight with parameter as:
     *
     * @param channel
     * @param passengerMix
     * @param origin
     * @param destination
     * @param journeyType
     * @param outboundDate
     * @param inboundDate
     * @param fare
     * @param currency
     * @return
     * @throws NullPointerException
     */
    public FlightsService getFlights(String channel, String passengerMix, String origin, String destination, String journeyType, String outboundDate, String inboundDate, String fare, String currency) throws NullPointerException, ParseException { //NOSONAR
        FlightPassengers passengers = new FlightPassengers(passengerMix);
        if ("elv".equalsIgnoreCase(testData.getPaymentCode())) {
            testData.setData(OUTBOUND_DATE, getDateFromToday(15));
            testData.setData(INBOUND_DATE, getDateFromToday(17));
            flexibleDays = null;
            params = FlightQueryParamsFactory.generateFlightSearchCriteria(passengers, origin, destination, testData.getData(OUTBOUND_DATE), testData.getData(INBOUND_DATE), flexibleDays);
        } else {
            flexibleDays = "2";
            params = FlightQueryParamsFactory.generateFlightSearchCriteria(passengers, origin, destination, getValidOutboundDate(outboundDate, flexibleDays), inboundDate, flexibleDays);
        }
        params.setFareTypes(fare);
        params.setCurrency(currency);
        flightService = serviceFactory.findFlight(new FlightsRequest(HybrisHeaders.getValid(channel).build(), params));
        flightService.invoke();
        return flightService;
    }

    private String getValidOutboundDate(String outboundDate, String flexibleDays) {
//        This logic for Outbound date to exclude todays flights
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        int i = Integer.parseInt(flexibleDays.trim());
        Calendar cal2 = Calendar.getInstance();
        try {

            Calendar cal1 = Calendar.getInstance();
            Date outBoundDate = sdf.parse(outboundDate);
            cal1.setTime(outBoundDate);
            cal2.setTime(new Date());
            cal1.add(Calendar.DATE,-i);
            if(cal1.getTime().compareTo(new Date())<=0){
                cal2.add(Calendar.DATE,i+1);
            }

        } catch (ParseException e) {
            e.printStackTrace();

        }
        return sdf.format(cal2.getTime());
    }

    /**
     * The method look for a flight with parameter as:
     *
     * @param channel
     * @param passengerMix
     * @param origin
     * @param destination
     * @param outboundDate
     * @param fare
     * @param flexiDays
     * @param applicationId
     * @param officeId
     * @param corporateId
     * @return
     * @throws Exception
     */
    public FlightsService getFlights(String channel, String passengerMix, String origin, String destination, String outboundDate, String fare, String flexiDays, String applicationId, String officeId, String corporateId) throws ParseException {
        FlightPassengers passengers = new FlightPassengers(passengerMix);
        params = FlightQueryParamsFactory.generateFlightSearchCriteria(passengers, origin, destination, new DateFormat().today().addDay(1), new DateFormat().today().addDay(4), flexiDays);
        params.setFareTypes(fare);
        flightService = serviceFactory.findFlight(new FlightsRequest(HybrisHeaders.getValidWithDeal(channel, applicationId, officeId, corporateId).build(), params));
        flightService.invoke();
        return flightService;
    }

    public FlightQueryParams getParam() {
        testData.setData(FLIGHT_QUERY_PARAMS, params);
        return params;
    }

    public void setSectors() {
        String sector = getSectors();
        testData.setOrigin(sector.substring(0, 3));
        testData.setDestination(sector.substring(3, 6));
    }

    public String getSectors() {
        return getRandomSector(Arrays.asList(FlightSearch.availableSectors));
    }

    private String getRandomSector(List<String> sectors) {

        return sectors.get(rand.nextInt(sectors.size()));
    }

}
