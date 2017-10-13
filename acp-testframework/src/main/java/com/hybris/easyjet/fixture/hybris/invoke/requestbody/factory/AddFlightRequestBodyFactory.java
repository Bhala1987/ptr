package com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.FlightFinder;
import com.hybris.easyjet.database.hybris.models.HybrisFlightDbModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.FlightPassengers;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.FlightQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.factory.FlightQueryParamsFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.AddFlightRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.FlightsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.constants.CommonConstants.INFANT;
import static com.hybris.easyjet.exceptions.EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INSUFFICIENT_DATA;
import static com.hybris.easyjet.exceptions.EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INSUFFICIENT_UNIQUE_CURRENCY_FLIGHTS;

/**
 * Created by daniel on 30/11/2016.
 */
@ToString
@Component
public class AddFlightRequestBodyFactory {

    private static final Logger LOG = LogManager.getLogger(AddFlightRequestBody.class);

    @Autowired
    private SerenityFacade testData;
    private final FlightFinder flightFinder;
    private final HybrisServiceFactory serviceFactory;
    private final String defaultPassengerMix = "1 Adult";
    private final String defaultCurrency = "GBP";
    private final String toeiCode = "ABC";
    private final String minPassengerMix = "1 Adult";
    private String passengerMix;
    @Getter
    private List<multiFlightData> multiFlightAvailableData;

    @Autowired
    public AddFlightRequestBodyFactory(HybrisServiceFactory serviceFactory, FlightFinder flightFinder) {
        this.flightFinder = flightFinder;
        this.serviceFactory = serviceFactory;
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public AddFlightRequestBody buildFlightRequestWithRequiredFare(FindFlightsResponse.Flight flight, String currency, String requiredFare, String bookingType) {
        return buildFlightRequest(flight, defaultPassengerMix, currency, "SINGLE", requiredFare, bookingType);
    }

    public AddFlightRequestBody buildFlightRequestWithRequiredFare(FindFlightsResponse.Flight flight, String currency, String requiredFare) {
        return buildFlightRequest(
            flight,
            defaultPassengerMix,
            currency,
            testData.getJourneyType() == null ? "SINGLE" : testData.getJourneyType(),
            requiredFare,
            testData.getBookingType()
        );
    }

    public AddFlightRequestBody buildFlightRequestWithRequiredFareAndPassengerMix(FindFlightsResponse.Flight flight, String currency, String requiredFare, String passengerMix) {
        return buildFlightRequestWithPassengerMixSingleJourney(flight, passengerMix, currency, requiredFare);
    }

    public AddFlightRequestBody buildFlightRequest(FindFlightsResponse.Flight flight) {
        if (testData.getCurrency() == null) {
            return buildFlightRequest(flight, defaultPassengerMix, defaultCurrency, "SINGLE");
        } else {
            return buildFlightRequest(flight, defaultPassengerMix, testData.getCurrency(), "SINGLE");
        }
    }
    public AddFlightRequestBody buildFlightRequestForStaff(FindFlightsResponse.Flight flight, String currency, String fareType) {
        AddFlightRequestBody myFlight = buildFlightRequest(flight, defaultPassengerMix, currency, "SINGLE");
        myFlight.setFareType(fareType);
        myFlight.setBookingType("STAFF");
        return myFlight;
    }

    public AddFlightRequestBody buildFlightRequestForStaff(FindFlightsResponse.Flight flight, String currency) {
        return buildFlightRequestForStaff(flight, currency, "Staff");
    }

    public AddFlightRequestBody buildFlightRequestForFlexi(FindFlightsResponse.Flight flight, String currency) {
        AddFlightRequestBody myFlight = buildFlightRequest(flight, defaultPassengerMix, currency, "SINGLE");
        myFlight.setFareType("Flexi");
        return myFlight;
    }

    public AddFlightRequestBody buildFlightRequestForStaff(FindFlightsResponse.Flight flight) {
        return buildFlightRequestForStaff(flight, defaultCurrency, "Staff");
    }

    public AddFlightRequestBody buildFlightRequestWithFareType(FindFlightsResponse.Flight flight, String fareType) {
        return buildFlightRequest(flight, defaultPassengerMix, defaultCurrency, "SINGLE", fareType, null);
    }

    public AddFlightRequestBody buildFlightRequest(FindFlightsResponse.Flight flight, String currency) {
        return buildFlightRequest(flight, defaultPassengerMix, currency, "SINGLE");
    }

    public AddFlightRequestBody buildFlightRequestWithSpefiedJourney(FindFlightsResponse.Flight flight, String currency, String fareType, String journeyType, String bookingType) {
        return buildFlightRequest(flight, defaultPassengerMix, currency, journeyType, fareType, bookingType);
    }
    public AddFlightRequestBody buildFlightRequestForInDirectFlights(FindFlightsResponse.Journey journey, String currency,String journeyType) {
        return buildFlightRequestIndirectFlights(journey, defaultPassengerMix, currency, journeyType, null);
    }

    public AddFlightRequestBody buildFlightRequestForMultipleFlights(List<FindFlightsResponse.Flight> flight, String currency, String journeyType) {
        return buildFlightRequestWithMultipleFlights(flight, defaultPassengerMix, currency, journeyType, null);
    }

    public AddFlightRequestBody buildFlightRequest(FindFlightsResponse.Flight flight, String passengerMix, String currency, String journeyType) {
        return buildFlightRequest(flight, passengerMix, currency, journeyType, null, null);
    }

    public AddFlightRequestBody buildFlightRequest(FindFlightsResponse.Flight flight, String passengerMix, String currency) {
        return buildFlightRequestWithPassengerMixSingleJourney(flight, passengerMix, currency, "Standard");
    }

    public AddFlightRequestBody buildFlightRequest(FindFlightsResponse.Flight flight, int numberOfPassengers, String journeyType) {
        if (numberOfPassengers > 0) {
            passengerMix = numberOfPassengers + " Adult";
        } else {
            numberOfPassengers = 1;//NOSONAR
            passengerMix = minPassengerMix;
        }

        return buildFlightRequest(flight, passengerMix, defaultCurrency, journeyType, null, null);
    }

    private AddFlightRequestBody buildFlightRequestWithPassengerMixSingleJourney(FindFlightsResponse.Flight flight, String passengerMix, String currency, String fare) {
        FlightPassengers passengers = new FlightPassengers(passengerMix);

        String routeCode = flight.getDeparture().getAirportCode() + flight.getArrival().getAirportCode();
        com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.Flight flightRequest = com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.Flight.builder().build();
        String fareType = fare;
        List<faresAndPrice> faresAndPrices = getBasePriceForPassengerMix(flight);
        Double price = totalBasePrice(faresAndPrices);

        return AddFlightRequestBody.builder()
                .flights(populateFlightData(flight.getFlightKey(), price, routeCode))
                .toeiCode("ABC")
                .currency(currency)
                .routeCode(routeCode)
                .fareType(fareType)
                .journeyType("SINGLE")
                .overrideWarning(false)
                .passengers(passengers.getPassengers())
                .routePrice(price)
                .bookingType(testData.getBookingType())
                .build();

    }

    public AddFlightRequestBody buildFlightRequestWithBookingTypeAndFareType(FindFlightsResponse.Flight flight, String bookingType, String fareType, String aPassengerMix) {
        FlightPassengers passengers = new FlightPassengers(aPassengerMix);

        String routeCode = flight.getDeparture().getAirportCode() + flight.getArrival().getAirportCode();
        String currency = "GBP";
        com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.Flight flightRequest = com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.Flight.builder().build();
        List<faresAndPrice> faresAndPrices = getBasePriceForPassengerMix(flight);
        Double price = totalBasePrice(faresAndPrices);

        return AddFlightRequestBody.builder()
                .flights(populateFlightData(flight.getFlightKey(), price, routeCode))
                .toeiCode("ABC")
                .currency(currency)
                .routeCode(routeCode)
                .fareType(fareType)
                .journeyType("SINGLE")
                .overrideWarning(false)
                .passengers(passengers.getPassengers())
                .routePrice(price)
                .bookingType(bookingType)
                .build();

    }

    private AddFlightRequestBody buildFlightRequestWithMultipleFlights(List<FindFlightsResponse.Flight> flights, String passengerMix, String currency, String journeyType, String requiredFare) {
        FlightPassengers passengers = new FlightPassengers(passengerMix);
        List<com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.Flight> addFlights = new ArrayList<>();
//        FlightPassengers passengers = null;
        Double routePrice = 0.0;
        String fareType = "STANDARD";
        List<faresAndPrice> faresAndPrices = new ArrayList<>();
        for (FindFlightsResponse.Flight flight : flights
                ) {
            faresAndPrices = getBasePriceForPassengerMix(flight);
            Double price = totalBasePrice(faresAndPrices);
            if (requiredFare == null) {
                requiredFare = "Standard";//NOSONAR
            } else {
                String finalRequiredFare = requiredFare;
                faresAndPrice fp = faresAndPrices.stream().filter(g -> g.getFareType().equals(finalRequiredFare)).findFirst().orElse(null);
                fareType = fp.getFareType();
                price = fp.getTotalBasePriceForFare();
                passengers = fp.getPassengers();
            }
            routePrice = routePrice + price;
            addFlights.add(com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.Flight.builder()
                    .flightKey(flight.getFlightKey())
                    .flightPrice(price)
                    .sector(flight.getDeparture().getAirportCode() + "" + flight.getArrival().getAirportCode())
                    .build());
        }
        // List<faresAndPrice> faresAndPrices = getBasePriceForPassengerMix(flight);

        // get and create the routeCode for entire journey (origin departure + destination arrival codes)
        String routeCode = flights.get(0).getDeparture().getAirportCode()
                + flights.get(flights.size() - 1).getArrival().getAirportCode();


        return AddFlightRequestBody.builder()
                .flights(addFlights)
                .toeiCode("ABC")
                .currency(currency)
                .fareType(fareType)
                .routeCode(routeCode)
                .journeyType(journeyType)
                .overrideWarning(false)
                .passengers(passengers.getPassengers())
                .routePrice(routePrice)
                .bookingType("STANDARD_CUSTOMER")
                .build();
        // .passengers(passengers.getPassengers()).execute();
    }
    private AddFlightRequestBody buildFlightRequestIndirectFlights(FindFlightsResponse.Journey journey, String passengerMix, String currency, String journeyType, String requiredFare) {
        FlightPassengers passengers = new FlightPassengers(passengerMix);
        List<com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.Flight> addFlights = new ArrayList<>();
        Double routePrice = 0.0;
        String fareType = "STANDARD";
        List<faresAndPrice> faresAndPrices;
        for (FindFlightsResponse.Flight flight : journey.getFlights()) {
            faresAndPrices = getBasePriceForPassengerMix(flight);
            Double price = totalBasePrice(faresAndPrices);
            if (requiredFare == null) {
                requiredFare = "Standard"; //NOSONAR
            } else {
                String finalRequiredFare = requiredFare;
                faresAndPrice fp = faresAndPrices.stream().filter(g -> g.getFareType().equals(finalRequiredFare)).findFirst().orElse(null);
                fareType = fp.getFareType();
                price = fp.getTotalBasePriceForFare();
                passengers = fp.getPassengers();
            }
            routePrice = routePrice + price;
            addFlights.add(com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.Flight.builder()
                    .flightKey(flight.getFlightKey())
                    .flightPrice(price)
                    .sector(flight.getDeparture().getAirportCode() + "" + flight.getArrival().getAirportCode())
                    .build());

        }
        testData.setOutboundFlights(journey.getFlights());
        // List<faresAndPrice> faresAndPrices = getBasePriceForPassengerMix(flight);

        // get and create the routeCode for entire journey (origin departure + destination arrival codes)
        String routeCode = journey.getFlights().get(0).getDeparture().getAirportCode()
                + journey.getFlights().get(journey.getFlights().size() - 1).getArrival().getAirportCode();


        return AddFlightRequestBody.builder()
                .flights(addFlights)
                .toeiCode("ABC")
                .currency(currency)
                .fareType(fareType)
                .routeCode(routeCode)
                .journeyType(journeyType)
                .overrideWarning(false)
                .passengers(passengers.getPassengers())
                .routePrice(routePrice)
                .bookingType("STANDARD_CUSTOMER")
                .build();
        // .passengers(passengers.getPassengers()).execute();
    }



    public AddFlightRequestBody buildFlightRequestWithAdditionalSeat(FindFlightsResponse.Flight flight, String passengerMix, String currency, String journeyType, String requiredFare) throws EasyjetCompromisedException {

        List<com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.Passenger> passengerList = new ArrayList<>();
        com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.Passenger.PassengerBuilder builder = com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.Passenger
                .builder();

        String[] passengers = passengerMix.split("\\s*;\\s*");

        for (String passenger : passengers) {

            String[] passengerData = passenger.split("\\s+");
            String seats = passengerData[0];
            String type = passengerData[1];
            String additionalSeat = "0";

            if (INFANT.equals(type)) {

                if (seats.contains("-")) {
                    String[] additionalSeats = seats.split("-");
                    seats = additionalSeats[0];
                    additionalSeat = additionalSeats[1];
                }

                builder.passengerType(type.toLowerCase());

                String[] infant = seats.split(",");

                builder.quantity(Integer.valueOf(infant[0]) - Integer.valueOf(infant[1]));
                builder.additionalSeats(0);
                builder.infantOnSeat(false);
                passengerList.add(builder.build());

                if (Integer.valueOf(infant[1]) > 0) {
                    builder.quantity(Integer.valueOf(infant[1]));
                    builder.additionalSeats(Integer.valueOf(additionalSeat));
                    builder.additionalSeats(0);
                    builder.infantOnSeat(true);
                    passengerList.add(builder.build());
                }

            } else {

                builder.passengerType(type.toLowerCase());
                builder.infantOnSeat(false);
                if (seats.contains(",")) {
                    builder.quantity(Integer.valueOf(seats.split(",")[0]));
                    builder.additionalSeats(Integer.valueOf(seats.split(",")[1]));
                } else {
                    builder.quantity(Integer.valueOf(seats));
                    builder.additionalSeats(0);
                }

                passengerList.add(builder.build());

            }

        }
        List<faresAndPrice> faresAndPrices = getBasePriceForPassengerMix(flight);

        Double price = totalBasePrice(faresAndPrices);

        String routeCode = flight.getDeparture().getAirportCode() + flight.getArrival().getAirportCode();

        return AddFlightRequestBody.builder()
                .flights(populateFlightData(flight.getFlightKey(), price, routeCode))
                .toeiCode("ABC")
                .currency(currency)
                .routeCode(routeCode)
                .fareType(requiredFare)
                .journeyType(journeyType)
                .overrideWarning(false)
                .passengers(passengerList)
                .routePrice(price)
                .bookingType("STANDARD_CUSTOMER")
                .build();
    }

    public AddFlightRequestBody buildFlightRequest(FindFlightsResponse.Flight flight, String passengerMix, String currency, String journeyType, String requiredFare, String bookingType) {
        FlightPassengers passengers = new FlightPassengers(passengerMix);
        List<faresAndPrice> faresAndPrices = getBasePriceForPassengerMix(flight);
        String fareType = "STANDARD";
        Double price = totalBasePrice(faresAndPrices);

        if (requiredFare == null) {
            for (faresAndPrice fp : faresAndPrices) {
                if ("Standard".equalsIgnoreCase(fp.getFareType()) && fp.getNumberOfSeatsAvailableInClass() >= fp.passengers.getTotalNumberOfPassengers()) {
                    fareType = fp.getFareType();
                    price = fp.getTotalBasePriceForFare();
                    passengers = fp.getPassengers();
                    break;  // will get out once fare for all passengers is found
                }
            }
        } else {
            faresAndPrice fp = faresAndPrices.stream().filter(
                g -> g.getFareType().equals(requiredFare)
            ).findFirst().orElse(null);

            fareType = fp.getFareType();
            price = fp.getTotalBasePriceForFare();
            passengers = fp.getPassengers();
        }

        String routeCode = flight.getDeparture().getAirportCode() + flight.getArrival().getAirportCode();
        if (bookingType == null) {
            bookingType = testData.getBookingType();
        }
        return AddFlightRequestBody.builder()
                .flights(populateFlightData(flight.getFlightKey(), price, routeCode))
                .toeiCode("ABC")
                .currency(currency)
                .routeCode(routeCode)
                .fareType(fareType)
                .journeyType(journeyType)
                .overrideWarning(false)
                .passengers(passengers.getPassengers())
                .routePrice(price)
                .bookingType(bookingType)
                .build();
    }

    private List<com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.Flight> populateFlightData(String flightKey, Double prize, String sector) {
        com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.Flight flightRequest = com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.Flight.builder().build();
        List<com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.Flight> flightList = new ArrayList<>();
        flightRequest.setFlightKey(flightKey);
        flightRequest.setFlightPrice(prize);
        flightRequest.setSector(sector);
        flightList.add(flightRequest);
        return flightList;
    }

    public List<AddFlightRequestBody> flightsToAdd(int numberOfFlightsToAdd, String channel) throws Throwable {
        return flightsToAdd(numberOfFlightsToAdd, 1, false, false, null, channel);
    }

    public List<AddFlightRequestBody> flightsToAdd(int numberOfFlightsToAdd, boolean departureAirportCurrencyMustBeUnique, String channel) throws Throwable {
        return flightsToAdd(numberOfFlightsToAdd, 1, departureAirportCurrencyMustBeUnique, false, null, channel);
    }

    public List<AddFlightRequestBody> flightsToAdd(int numberOfFlightsToAdd, int numberOfPassengersPerFlight, boolean withFlightTax, String bundles, String channel) throws Throwable {
        return flightsToAdd(numberOfFlightsToAdd, numberOfPassengersPerFlight, false, withFlightTax, bundles, channel);
    }

    private List<AddFlightRequestBody> flightsToAdd(int numberOfFlightsToAdd, int numberOfPassengersPerFlight, boolean departureAirportCurrencyMustBeUnique, boolean withFlightTax, String bundles, String channel) throws Throwable {

        if (numberOfPassengersPerFlight > 0) {
            passengerMix = numberOfPassengersPerFlight + " Adult";
        } else {
            numberOfPassengersPerFlight = 1;//NOSONAR
            passengerMix = minPassengerMix;
        }

        List<AddFlightRequestBody> flights = new ArrayList<>();
        LocalDate previousDepartureDate = LocalDate.now();
        List<HybrisFlightDbModel> validFlights = flightFinder.findValidFlights(numberOfPassengersPerFlight, bundles, withFlightTax);

        if (departureAirportCurrencyMustBeUnique) {
            validFlights = validFlights.stream().filter(distinctByKey(HybrisFlightDbModel::getCurrency)).collect(Collectors.toList());
            if (validFlights.size() < 2) {
                throw new EasyjetCompromisedException(INSUFFICIENT_UNIQUE_CURRENCY_FLIGHTS);
            }
        } else {
            //Establish first Flight Currency   -this bit of type need to be remove once we can select multiple currencies into basket
            Random randomGenerator = new Random();
            String firstFlightCurrency = validFlights.get(randomGenerator.nextInt(validFlights.size())).getCurrency();
            validFlights = validFlights.stream().filter(t -> t.getCurrency().equals(firstFlightCurrency)).collect(Collectors.toList());
            if (validFlights.size() < 2) {
                throw new EasyjetCompromisedException(INSUFFICIENT_UNIQUE_CURRENCY_FLIGHTS);
            }
        }

        multiFlightAvailableData = new ArrayList<>();
        //for (int i = 0; i < numberOfFlightsToAdd; i++) {
        DateTimeFormatter format;
        if (validFlights.get(0).getPlannedDepartureTime().contains("."))
            format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.s");
        else
            format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        int foundFlights = 0;
        for (HybrisFlightDbModel validFlight : validFlights) {

            LocalDate nextDepartureDate = LocalDate.parse(validFlight.getPlannedDepartureTime(), format);

            if (nextDepartureDate.isEqual(previousDepartureDate) || nextDepartureDate.isAfter(previousDepartureDate)) {

                FlightQueryParams criteria =
                        FlightQueryParamsFactory.generateFlightSearchCriteria(validFlight).adult(String.valueOf(numberOfPassengersPerFlight)).build();
                criteria.setFareTypes(bundles);

                FlightsService flightsService = serviceFactory.findFlight(new FlightsRequest(HybrisHeaders.getValid(channel).build(), criteria));
                flightsService.invoke();

                FindFlightsResponse.Flight available = flightsService.getResponse().getOutbound().getJourneys().stream().flatMap(p -> p.getFlights().stream()).filter(f -> "AVAILABLE".equals(f.getAvailableStatus())).findAny().orElse(null);
                multiFlightData data = new multiFlightData();
                data.flightQueryParams = criteria;
                int attempt = 0;
                while (available == null && attempt < 3) {
                    try {
                        attempt += 1;
                        flightsService.invoke();
                        available = flightsService.getResponse().getOutbound().getJourneys().stream().flatMap(p -> p.getFlights().stream()).filter(f -> "AVAILABLE".equals(f.getAvailableStatus())).findAny().orElse(null);

                    } catch (NullPointerException e) {
                        LOG.error(e);
                        flightsService.invoke();
                        available = flightsService.getResponse().getOutbound().getJourneys().stream().flatMap(p -> p.getFlights().stream()).filter(f -> "AVAILABLE".equals(f.getAvailableStatus())).findAny().orElse(null);

                    }
                }

                Optional<FindFlightsResponse.Flight> nullableAvailable = Optional.ofNullable(available);
                if (nullableAvailable.isPresent()){
                    data.availableSeats = nullableAvailable.get().getInventory().getAvailable();
                }else{
                    throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
                }

                multiFlightAvailableData.add(data);

                AddFlightRequestBody flightToAdd = buildFlightRequest(
                        available,
                        passengerMix,
                        flightsService.getResponse().getCurrency(), "SINGLE", bundles, null);

                flights.add(flightToAdd);
                previousDepartureDate = nextDepartureDate;
                foundFlights++;
            }

            if (foundFlights >= numberOfFlightsToAdd) {
                break;
            }

        }

        if (departureAirportCurrencyMustBeUnique && flights.size() < numberOfFlightsToAdd) {
            throw new EasyjetCompromisedException(INSUFFICIENT_UNIQUE_CURRENCY_FLIGHTS);
        }

        return flights;
    }

    private Double totalBasePrice(List<faresAndPrice> fps) {
        Double total = 0.0;
        for (faresAndPrice fp : fps) {
            total += fp.getTotalBasePriceForFare();
        }
        return total;
    }

    private List<faresAndPrice> getBasePriceForPassengerMix(FindFlightsResponse.Flight flight) {
        List<faresAndPrice> fpList = new ArrayList<>();

        if (Objects.nonNull(flight.getFareTypes())) {
            for (FindFlightsResponse.FareType fType : flight.getFareTypes()) {
                faresAndPrice fp = new faresAndPrice();
                fp.setTotalBasePriceForFare(0.0);
                fp.setNumberOfSeatsAvailableInClass(0);
                fp.setFareType(fType.getFareTypeCode());
                String paxMix = "";

                for (FindFlightsResponse.Passenger pax : fType.getPassengers()) {
                    if ("adult".equalsIgnoreCase(pax.getType())|| "child".equalsIgnoreCase(pax.getType())) {
                        fp.setTotalBasePriceForFare(fp.getTotalBasePriceForFare() + pax.getBasePrice());
                        break;
                    }
                }

                for (FindFlightsResponse.Passenger pax : fType.getPassengers()) {
                    fp.setNumberOfSeatsAvailableInClass(fp.getNumberOfSeatsAvailableInClass() + pax.getQuantity());
                    if (!Objects.equals(paxMix, "")) {
                        paxMix += ", ";
                    }
                    paxMix += pax.getQuantity() + " " + pax.getType();
                    if (INFANT.equalsIgnoreCase(pax.getType())) {
                        if (pax.getInfantOnSeat()) {
                            paxMix += " OOS";
                        } else {
                            paxMix += " OL";
                        }
                    }
                }

                fp.passengers = new FlightPassengers(paxMix);
                fpList.add(fp);
            }
        }

        return fpList;
    }

    @Getter
    @Setter
    public class multiFlightData {
        private FlightQueryParams flightQueryParams;
        private int availableSeats;
    }

    @Getter
    @Setter
    private class PassengerMix {

        private int num;
        private String type;

    }

    @Getter
    @Setter
    private class faresAndPrice {

        private String fareType;
        private Double totalBasePriceForFare;
        private int numberOfSeatsAvailableInClass;
        private FlightPassengers passengers;
    }

}
