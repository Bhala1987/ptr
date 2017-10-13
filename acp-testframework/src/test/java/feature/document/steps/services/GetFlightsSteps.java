package feature.document.steps.services;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.DealDao;
import com.hybris.easyjet.database.hybris.dao.FeesAndTaxesDao;
import com.hybris.easyjet.database.hybris.dao.FlightsDao;
import com.hybris.easyjet.database.hybris.models.DealModel;
import com.hybris.easyjet.database.hybris.models.ItemModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.asserters.FlightsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.FlightQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.FlightsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.SectorResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractFlights;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import com.hybris.easyjet.models.PassengerMix;
import cucumber.api.java.en.*;
import feature.document.steps.constants.StepsRegex;
import feature.document.steps.services.createbasketservices.GetBasketSteps;
import feature.document.steps.services.productmanagementservices.GetSectorsSteps;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import org.apache.commons.lang.StringUtils;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static feature.document.steps.helpers.DateHelper.getDate;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * GetFlightsSteps handle the communication with the getFlights service (aka findFlights).
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class GetFlightsSteps {

    private static final String STAFF = "Staff";
    private static final String STANDBY = "Standby";
    private static final String APIS = "apis";
    private static final String DCS = "DCS";

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Autowired
    private FlightsDao flightsDao;
    @Autowired
    private DealDao dealDao;
    @Autowired
    private FeesAndTaxesDao feesAndTaxesDao;

    @Steps
    private GetSectorsSteps getSectorsSteps;
    @Steps
    private GetBasketSteps getBasketSteps;
    @Steps
    private FlightsAssertion flightsAssertion;

    private FlightsService getFlightsService;
    private FlightQueryParams.FlightQueryParamsBuilder getFlightsQueryParams = FlightQueryParams.builder();

    private boolean withReturn;
    private String origin;
    private String destination;
    private String fareType;
    private String passengerMix;
    private String outboundDate;
    private String inboundDate;
    private String flexibleDays;
    private String applicationId;
    private String officeId;
    private String corporateId;
    private String currency;
    private HashMap<String, ItemModel> deal;
    private String flightKey;
    private List<String> passengerIds = new ArrayList<>();
    private HashMap<String, List<String>> passengerList = new HashMap<>();
    private String bookingRef;

    private void saveData(String withReturn, String origin, String destination, String fareType, String passengerMix, String outboundDate, String inboundDate, String flexibleDays, String applicationId, String officeId, String corporateId, String currency) {
        this.withReturn = StringUtils.isNotBlank(withReturn);
        this.origin = origin;
        this.destination = destination;
        if (!Objects.isNull(origin) && origin.equals("same")) {
            this.origin = testData.getData(ORIGIN);
            this.destination = testData.getData(DESTINATION);
        } else {
            testData.setData(ORIGIN, null);
            testData.setData(DESTINATION, null);
        }
        this.flexibleDays = flexibleDays;
        if (!Objects.isNull(flexibleDays)) {
            testData.setData(FLEXIBLE_DAYS, null);
        }
        this.fareType = fareType;
        if (!Objects.isNull(fareType)) {
            testData.setData(FARE_TYPE, null);
        }
        this.passengerMix = passengerMix;
        if (!Objects.isNull(passengerMix)) {
            testData.setData(PASSENGER_MIX, null);
        }
        this.outboundDate = outboundDate;
        if (!Objects.isNull(outboundDate)) {
            testData.setData(OUTBOUND_DATE, null);
        }
        this.inboundDate = inboundDate;
        if (!Objects.isNull(inboundDate)) {
            testData.setData(INBOUND_DATE, null);
        }
        this.applicationId = applicationId;
        if (!Objects.isNull(applicationId)) {
            testData.setData(APPLICATION_ID, null);
        }
        this.officeId = officeId;
        if (!Objects.isNull(officeId)) {
            testData.setData(OFFICE_ID, null);
        }
        this.corporateId = corporateId;
        if (!Objects.isNull(corporateId)) {
            testData.setData(CORPORATE_ID, null);
        }
        this.currency = currency;
        if (!Objects.isNull(currency)) {
            testData.setData(CURRENCY, null);
        }
    }

    private void checkSessionData() {
        if (testData.keyExist(ORIGIN)) {
            this.origin = testData.getData(ORIGIN);
        }
        if (testData.keyExist(DESTINATION)) {
            this.destination = testData.getData(DESTINATION);
        }
        if (testData.keyExist(FARE_TYPE)) {
            this.fareType = testData.getData(FARE_TYPE);
        }
        if (testData.keyExist(PASSENGER_MIX)) {
            this.passengerMix = testData.getData(PASSENGER_MIX);
        }
        if (testData.keyExist(OUTBOUND_DATE)) {
            this.outboundDate = testData.getData(OUTBOUND_DATE);
        }
        if (testData.keyExist(INBOUND_DATE)) {
            this.withReturn = true;
            this.inboundDate = testData.getData(INBOUND_DATE);
        }
        if (testData.dataExist(FLEXIBLE_DAYS)) {
            this.flexibleDays = testData.getData(FLEXIBLE_DAYS);
        }
        if (testData.keyExist(APPLICATION_ID)) {
            this.applicationId = testData.getData(APPLICATION_ID);
            this.officeId = testData.getData(OFFICE_ID);
            this.corporateId = testData.getData(CORPORATE_ID);
        }
        if (testData.keyExist(CURRENCY)) {
            this.currency = testData.getData(CURRENCY);
        }
    }

    private void storeTestData() throws EasyjetCompromisedException {
        // Mandatory parameters for the getFlights service
        if (Objects.isNull(origin) && Objects.isNull(destination)) {
            getSectorsSteps.setRandomSector();
        } else {
            if (!Objects.isNull(origin)) {
                testData.setData(ORIGIN, origin);
            }
            if (!Objects.isNull(destination)) {
                testData.setData(DESTINATION, destination);
            }
        }
        if (!Objects.isNull(outboundDate)) {
            testData.setData(OUTBOUND_DATE, getDate(outboundDate));
        } else if (testData.keyNotExist(OUTBOUND_DATE)) {
            testData.setData(OUTBOUND_DATE, getDate("1"));
        }

        if (!Objects.isNull(passengerMix))
            testData.setData(PASSENGER_MIX, passengerMix);
        else testData.setData(PASSENGER_MIX, "1 adult");
        testData.setData(PASSENGERS, new PassengerMix(testData.getData(PASSENGER_MIX)));

        // Optional parameters
        if (!Objects.isNull(fareType)) testData.setData(FARE_TYPE, fareType);
        if (!Objects.isNull(inboundDate)) {
            testData.setData(INBOUND_DATE, getDate(inboundDate));
        } else if (testData.keyNotExist(INBOUND_DATE) && withReturn) {
            testData.setData(INBOUND_DATE, getDate("3"));
        }
        if (!Objects.isNull(flexibleDays)) {
            testData.setData(FLEXIBLE_DAYS, flexibleDays);
        }
        if (!Objects.isNull(applicationId) && !Objects.isNull(officeId)) {
            testData.setData(APPLICATION_ID, applicationId);
            testData.setData(OFFICE_ID, officeId);
        }
        if (!Objects.isNull(corporateId)) testData.setData(CORPORATE_ID, corporateId);
        if (!Objects.isNull(currency)) testData.setData(CURRENCY, currency);
    }

    private void setPassengerQueryParam() {
        PassengerMix passengers = testData.getData(PASSENGERS);
        String adults = passengers.getAdult() + "," + passengers.getAdditionalAdult();
        String childs = passengers.getChild() + "," + passengers.getAdditionalChild();
        String infants = passengers.getInfantOnLap() + passengers.getInfantOnSeat() + "," + passengers.getInfantOnSeat();

        if (!adults.equals("0,0")) getFlightsQueryParams.adult(adults);
        if (!childs.equals("0,0")) getFlightsQueryParams.child(childs);
        if (!infants.equals("0,0")) getFlightsQueryParams.infant(infants);
    }

    private void setQueryParameter() {
        setPassengerQueryParam();
        getFlightsQueryParams
                .origin(testData.getData(ORIGIN))
                .destination(testData.getData(DESTINATION))
                .outboundDate(testData.getData(OUTBOUND_DATE));

        if (testData.keyExist(FARE_TYPE) && !testData.getData(FARE_TYPE).equals(STANDBY)) {
            getFlightsQueryParams.fareTypes(testData.getData(FARE_TYPE));
        }
        if (withReturn) getFlightsQueryParams.inboundDate(testData.getData(INBOUND_DATE));
        if (testData.dataExist(FLEXIBLE_DAYS)) {
            if (!testData.getData(FLEXIBLE_DAYS).equals("0")) {
                getFlightsQueryParams.flexibleDays(testData.getData(FLEXIBLE_DAYS));
            }
        } else {
            getFlightsQueryParams.flexibleDays("60");
        }
        if (testData.keyExist(CURRENCY))
            getFlightsQueryParams.currency(testData.getData(CURRENCY));
        if (!Objects.isNull(flightKey)) getFlightsQueryParams.flightKey(flightKey);
        if (!Objects.isNull(bookingRef)) getFlightsQueryParams.bookingRef(bookingRef);
        if (!passengerIds.isEmpty()) getFlightsQueryParams.passenderIds(passengerIds);
    }

    private void invokeGetFlightService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        getFlightsService = serviceFactory.findFlight(new FlightsRequest(headers.build(), getFlightsQueryParams.build()));
        testData.setData(GET_FLIGHT_SERVICE, getFlightsService);
        testData.setData(SERVICE, getFlightsService);
        getFlightsService.invoke();
        //TODO move this initialization at the end of the execution of the test!!
        bookingRef = null;
        passengerIds = new ArrayList<>();
        flightKey = null;
        getFlightsQueryParams = FlightQueryParams.builder();
    }

    private boolean checkAvailability() {
        if (testData.keyExist(CURRENCY)) {
            if (!getFlightsService.getResponse().getCurrency().equals(testData.getData(CURRENCY))) {
                return false;
            }
        }

        List<DealModel> deals = testData.keyExist(DEALS) ? testData.getData(DEALS) : new ArrayList<>();
        if (!deals.isEmpty()) {
            deal = dealDao.getDeal(testData.getData(APPLICATION_ID), testData.getData(OFFICE_ID), testData.getData(CORPORATE_ID), getFlightsService.getResponse().getCurrency());
        }

        String outboundSector = testData.getData(ORIGIN).toString() + testData.getData(DESTINATION).toString();
        List<FindFlightsResponse.Journey> usableOutbounds = checkJourney(
                getFlightsService.getResponse().getOutbound().getJourneys().stream()
                        .filter(journey -> journey.getFlights().stream()
                                .anyMatch(flight -> flight.getAvailableStatus().equals("AVAILABLE")
                                        && flight.getFlightKey().substring(8, 14).equals(outboundSector)))
                , "outbound");

        boolean usableOutbound = usableOutbounds.size() > 0;

        boolean usableInbound = true;
        if (withReturn) {
            String inboundSector = testData.getData(DESTINATION).toString() + testData.getData(ORIGIN).toString();
            if (!Objects.isNull(getFlightsService.getResponse().getInbound())) {
                List<FindFlightsResponse.Journey> usableInbounds = checkJourney(
                        getFlightsService.getResponse().getInbound().getJourneys().stream()
                                .filter(journey -> journey.getFlights().stream()
                                        .anyMatch(flight -> flight.getAvailableStatus().equals("AVAILABLE")
                                                && flight.getFlightKey().substring(8, 14).equals(inboundSector)))
                        , "inbound");
                if (!usableInbounds.isEmpty()) {
                    testData.setData(INBOUND_JOURNEY, usableInbounds.get(0));
                    testData.setData(INBOUND_FLIGHT, usableInbounds.get(0).getFlights().get(0));
                } else {
                    usableInbound = false;
                }
            } else {
                usableInbound = false;
            }
        }

        if (!usableOutbounds.isEmpty()) {
            testData.setData(OUTBOUND_JOURNEY, usableOutbounds.get(0));
            testData.setData(OUTBOUND_FLIGHT, usableOutbounds.get(0).getFlights().get(0));
            if (testData.dataNotExist(FARE_TYPE)) {
                testData.setData(FARE_TYPE, usableOutbounds.get(0).getFlights().get(0).getFareTypes().get(0).getFareTypeCode());
            }
            if (testData.getData(FARE_TYPE).equals(STAFF) || testData.getData(FARE_TYPE).equals(STANDBY)) {
                testData.setData(BOOKING_TYPE, "STAFF");
            } else {
                testData.setData(BOOKING_TYPE, "STANDARD_CUSTOMER");
            }
        }

        return usableOutbound && usableInbound;
    }

    private List<FindFlightsResponse.Journey> checkJourney(Stream<FindFlightsResponse.Journey> journeyStream, String journeyType) {

        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd");
        String flightOutboundDate = "";
        String flightInboundDate = "";
        try {
            flightOutboundDate = outputFormat.format(inputFormat.parse(testData.getData(OUTBOUND_DATE)));
            if (withReturn) {
                flightInboundDate = outputFormat.format(inputFormat.parse(testData.getData(INBOUND_DATE)));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Stream<FindFlightsResponse.Journey> usableJourney = journeyStream;
        PassengerMix passengers = testData.getData(PASSENGERS);

        String validDate;
        if (testData.dataExist(FLEXIBLE_DAYS) && testData.getData(FLEXIBLE_DAYS).equals("0")) {
            switch (journeyType) {
                case "outbound":
                    validDate = flightOutboundDate;
                    break;
                case "inbound":
                    validDate = flightInboundDate;
                    break;
                default:
                    throw new IllegalArgumentException("no journey type defined for find flight");
            }
            usableJourney = usableJourney
                    .filter(journey -> journey.getFlights().stream()
                            .anyMatch(flight -> flight.getFlightKey().substring(0, 8).equals(validDate)));
        } else {
            switch (journeyType) {
                case "outbound":
                    validDate = outputFormat.format(Calendar.getInstance().getTime());
                    break;
                case "inbound":
                    validDate = flightOutboundDate;
                    break;
                default:
                    throw new IllegalArgumentException("no journey type defined for find flight");
            }
            usableJourney = usableJourney
                    .filter(journey -> journey.getFlights().stream()
                            .anyMatch(flight -> flight.getFlightKey().substring(0, 8).compareTo(validDate) > 0));
        }

        if (testData.keyExist(FARE_TYPE) && testData.getData(FARE_TYPE).equals(STANDBY)) {
            usableJourney = usableJourney
                    .filter(journey -> journey.getFlights().stream()
                            .anyMatch(flight -> {
                                int available = flightsDao.getAvailableStockLevelForFlight(flight.getFlightKey(), "STANDBY");
                                int reserved = flightsDao.getReservedStockLevelForFlight(flight.getFlightKey(), "STANDBY");
                                return ((available - reserved) >= passengers.getTotalSeats())
                                        && flight.getFareTypes().stream()
                                        .anyMatch(fareType -> fareType.getFareTypeCode().equals(testData.getData(FARE_TYPE)));
                            }));
        } else {
            if (testData.keyExist(FARE_TYPE)) {
                usableJourney = usableJourney
                        .filter(journey -> journey.getFlights().stream()
                                .anyMatch(flight -> flight.getFareTypes().stream()
                                        .anyMatch(fareType -> fareType.getFareTypeCode().equals(testData.getData(FARE_TYPE)))));
            }
            usableJourney = usableJourney
                    .filter(journey -> journey.getFlights().stream()
                            .anyMatch(flight -> flight.getInventory().getAvailable() >= passengers.getTotalSeats()));
            if ((passengers.getInfantOnSeat() + passengers.getInfantOnLap()) > 0) {
                usableJourney = usableJourney
                        .filter(journey -> journey.getFlights().stream()
                                .anyMatch(flight -> {
                                    HashMap<String, Integer> infantsLimitsAndConsumed = new HashMap<>(flightsDao.getInfantsLimitAndConsumed(flight.getFlightKey()));
                                    int infantsLimit = infantsLimitsAndConsumed.get("InfantsLimit");
                                    int infantsConsumed = infantsLimitsAndConsumed.get("InfantsConsumed");
                                    int infantsOnSeatLimit = infantsLimitsAndConsumed.get("InfantsOnSeatLimit");
                                    int infantsOnSeatConsumed = infantsLimitsAndConsumed.get("InfantsOnSeatConsumed");
                                    return (((infantsLimit - infantsConsumed) >= (passengers.getInfantOnSeat() + passengers.getInfantOnLap())) &&
                                            ((infantsOnSeatLimit - infantsOnSeatConsumed) >= passengers.getInfantOnSeat()) &&
                                            ((infantsLimit - infantsConsumed) >= passengers.getInfantOnSeat()));
                                }));
            }
        }

        if (Objects.nonNull(deal)) {
            if (Objects.nonNull(deal.get("discount"))) {
                usableJourney = usableJourney
                        .filter(journey -> journey.getFlights().stream()
                                .anyMatch(flight -> flight.getFareTypes().stream()
                                        .anyMatch(fareType -> fareType.getPassengers().stream()
                                                .anyMatch(passenger -> passenger.getDiscounts().stream()
                                                        .anyMatch(discount -> discount.getCode().equals(deal.get("discount").getCode()))))));
            }

            if (Objects.nonNull(deal.get("fee"))) {
                usableJourney = usableJourney
                        .filter(journey -> journey.getFlights().stream()
                                .anyMatch(flight -> flight.getFareTypes().stream()
                                        .anyMatch(fareType -> fareType.getPassengers().stream()
                                                .anyMatch(passenger -> passenger.getFees().stream()
                                                        .anyMatch(fee -> fee.getCode().equals(deal.get("fee").getCode()))))));
            }
        }

        return usableJourney.collect(Collectors.toList());
    }

    @Step("Set getFlight request parameter: origin {1}, destination {2}, fare type {3}, passenger mix {4}, outbound date {5}, inbound date {6}")
    @Given("^I want to search a flight" + StepsRegex.RETURN + StepsRegex.SECTOR + StepsRegex.FARE_TYPE + StepsRegex.PASSENGER_MIX + StepsRegex.DATES + StepsRegex.FLEXIBLE_DAYS + StepsRegex.DEAL + StepsRegex.CURRENCY + "$")
    public void setGetFlightsParameters(String withReturn, String origin, String destination, String fareType, String passengerMix, String outboundDate, String inboundDate, String flexibleDays, String applicationId, String officeId, String corporateId, String currency) {
        if (origin == null && destination == null) {
            getSectorsSteps.getSectors(StringUtils.isNotBlank(withReturn), null, null);
        } else if (StringUtils.isNotBlank(origin) && origin.contains(APIS)) {
            getSectorsSteps.getSectors(StringUtils.isNotBlank(withReturn), origin.equals(APIS), null);
            origin = null;
            destination = null;
        } else if (StringUtils.isNotBlank(origin) && origin.contains(DCS)) {
            getSectorsSteps.getSectors(StringUtils.isNotBlank(withReturn), null, origin.equals(DCS));
            origin = null;
            destination = null;
        }
        saveData(withReturn, origin, destination, fareType, passengerMix, outboundDate, inboundDate, flexibleDays, applicationId, officeId, corporateId, currency);
    }

    @And("^I want to search a flight to change an existing one" + StepsRegex.SECTOR + StepsRegex.FARE_TYPE + StepsRegex.PASSENGER_MIX + StepsRegex.DATES + StepsRegex.FLEXIBLE_DAYS + StepsRegex.DEAL + StepsRegex.CURRENCY + "$")
    public void setChangeFlightsParameters(String origin, String destination, String fareType, String passengerMix, String outboundDate, String inboundDate, String flexibleDays, String applicationId, String officeId, String corporateId, String currency) throws EasyjetCompromisedException {
        if (Objects.isNull(origin) && Objects.isNull(destination)) {
            getSectorsSteps.getSectors(false, null, null);
        } else if (StringUtils.isNotBlank(origin) && origin.equals("different")) {
            getSectorsSteps.getSectors(false, null, null);
            List<SectorResponse.Sector> sectors = testData.getData(USABLE_SECTORS);
            testData.setData(USABLE_SECTORS,
                    sectors.stream().filter(
                            sector -> !sector.getDepartureAirport().equals(testData.getData(ORIGIN))
                                    && !sector.getArrivalAirport().equals(testData.getData(DESTINATION))
                    ).collect(Collectors.toList())
            );
            origin = null;
            destination = null;
        } else if (StringUtils.isNotBlank(origin) && origin.contains(APIS)) {
            getSectorsSteps.getSectors(false, origin.equals(APIS), null);
            origin = null;
            destination = null;
        }else if (StringUtils.isNotBlank(origin) && origin.contains(DCS)) {
            getSectorsSteps.getSectors(false, null, origin.equals(DCS));
            origin = null;
            destination = null;
        }
        if (Objects.isNull(passengerMix)) {
            passengerMix = testData.getData(PASSENGER_MIX);
        }
        saveData(null, origin, destination, fareType, passengerMix, outboundDate, inboundDate, flexibleDays, applicationId, officeId, corporateId, currency);

        getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
        BasketService basketService = testData.getData(BASKET_SERVICE);
        Basket basket = basketService.getResponse().getBasket();

        flightKey = basket.getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("No outbound flight in the basket"))
                .getFlightKey();

        testData.setData(FLIGHT_KEY, flightKey);

        bookingRef = testData.getData(BOOKING_ID);

        passengerList.put("adult", new ArrayList<>());
        passengerList.put("child", new ArrayList<>());
        passengerList.put("infantOnSeat", new ArrayList<>());
        passengerList.put("infantOnLap", new ArrayList<>());

        basket.getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .filter(flight -> flight.getFlightKey().equals(flightKey))
                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                .forEach(
                        passenger -> {
                            if (passenger.getPassengerDetails().getPassengerType().equals("infant")) {
                                if (passenger.getFareProduct().getBundleCode().equals("InfantOnLap")) {
                                    passengerList.get("infantOnLap").add(passenger.getCode());
                                } else {
                                    passengerList.get("infantOnSeat").add(passenger.getCode());
                                }
                            } else {
                                passengerList.get(passenger.getPassengerDetails().getPassengerType()).add(passenger.getCode());
                            }
                        }
                );
        passengerIds.addAll(passengerList.get("adult"));
        passengerIds.addAll(passengerList.get("child"));
        passengerIds.addAll(passengerList.get("infantOnSeat"));
        passengerIds.addAll(passengerList.get("infantOnLap"));

        testData.setData(PASSENGER_LIST, passengerIds);
    }

    @And("^I have specified the exclude-admin-fee parameter as (true|false)$")
    public void iHaveSpecifiedTheExcludeAdminFeeParameterAsAdminFee(String excludeAdminFee) {
        getFlightsQueryParams.excludeAdminFee(excludeAdminFee);
    }

    @But("^the queryParam (flightKey|bookingRef|passengerIds|passengerMix) is (.*)$")
    public void theQueryParamValue(String queryParam, String value) {
        String param = null;
        switch (value) {
            case "empty":
                param = "";
                break;
            case "invalid":
                param = "INVALID";
                break;
            case "contains invalid value":
                passengerIds.add("INVALID");
                break;
            case "adult does not match passenger mix":
                passengerIds.removeAll(passengerList.get("adult"));
                break;
            case "child does not match passenger mix":
                passengerIds.removeAll(passengerList.get("child"));
                break;
            case "infant on seat does not match passenger mix":
                passengerIds.removeAll(passengerList.get("infantOnSeat"));
                break;
            case "infant on lap does not match passenger mix":
                passengerIds.removeAll(passengerList.get("infantOnLap"));
                break;
            case "does not match original passenger mix":
                PassengerMix passengers = testData.getData(PASSENGERS);
                String adults = passengers.getAdult() + 1 + "," + passengers.getAdditionalAdult();
                String childs = passengers.getChild() + "," + passengers.getAdditionalChild();
                String infants = passengers.getInfantOnLap() + passengers.getInfantOnSeat() + "," + passengers.getInfantOnSeat();

                passengerIds = new ArrayList<>();
                testData.setData(PASSENGER_MIX, adults + " adult; " + childs + " child; " + infants + " infant");
                break;
            default:
                param = value;
                break;
        }

        if (!Objects.isNull(param)) {
            try {
                this.getClass().getDeclaredField(queryParam).set(this, param);
            } catch (IllegalAccessException | NoSuchFieldException ignored) {
            }
        }
    }

    @And("^I want to do a group booking" + StepsRegex.RETURN + StepsRegex.SECTOR + StepsRegex.FARE_TYPE + StepsRegex.PASSENGER_MIX + StepsRegex.DATES + StepsRegex.FLEXIBLE_DAYS + StepsRegex.DEAL + StepsRegex.CURRENCY + "$")
    public void setGetFlightsParametersForGroupBooking(String withReturn, String origin, String destination, String fareType, String passengerMix, String outboundDate, String inboundDate, String flexibleDays, String applicationId, String officeId, String corporateId, String currency) {
        setGetFlightsParameters(withReturn, origin, destination, fareType, passengerMix, outboundDate, inboundDate, flexibleDays, applicationId, officeId, corporateId, currency);
        getFlightsQueryParams.groupBooking("true");
    }

    @Step("Search flight")
    @Given("^I searched a flights" + StepsRegex.RETURN + StepsRegex.SECTOR + StepsRegex.FARE_TYPE + StepsRegex.PASSENGER_MIX + StepsRegex.DATES + StepsRegex.FLEXIBLE_DAYS + StepsRegex.DEAL + StepsRegex.CURRENCY + "$")
    public void searchFlights(String withReturn, String origin, String destination, String fareType, String passengerMix, String outboundDate, String inboundDate, String flexibleDays, String applicationId, String officeId, String corporateId, String currency) throws EasyjetCompromisedException {
        setGetFlightsParameters(withReturn, origin, destination, fareType, passengerMix, outboundDate, inboundDate, flexibleDays, applicationId, officeId, corporateId, currency);
        sentGetFlightsRequest();
    }

    @Step("Get flight")
    @Given("^I sent the request to getFlights service$")
    public void sentGetFlightsRequest() {
        checkSessionData();
        try {
            pollingLoop().untilAsserted(() -> {
                storeTestData();
                setQueryParameter();
                invokeGetFlightService();
                assertThat(checkAvailability())
                        .withFailMessage("No flight match the requested parameters")
                        .isTrue();
                testData.setData(CURRENCY, getFlightsService.getResponse().getCurrency());
            });
        } catch (ConditionTimeoutException conditionTimeOutException) {
            conditionTimeOutException.printStackTrace();
        }
        flightsAssertion.setResponse(getFlightsService.getResponse());
    }

    @When("^I send the request to getFlights service$")
    public void sendGetFlightsRequest() throws EasyjetCompromisedException {
        checkSessionData();
        storeTestData();
        setQueryParameter();
        invokeGetFlightService();
    }

    @Then("^the channel receive a list of flights$")
    public void checkGetFlightsResponse() {
        getFlightsService.assertThat().atLeastOneOutboundFlightWasReturned();
    }

    @Then("^each flight has a flightkey$")
    public void checkGetFlightsResponseContainsFlightKeys() {
        //TODO refactor assertion to check all the flights in the response
        getFlightsService.assertThat().theFlightHasAFlightKey();
    }

    @Then("^the (AdminFee|GRPBKGFee) (is|is not) showed in the results$")
    public void feeInResultsCheck(String feeCode, String show) {
        flightsAssertion
                .feeIsDisplayed(feeCode, show);
    }

    @Then("^the (InternetDisc) (is|is not) be showed in the results$")
    public void discountInResultsCheck(String discountCode, String show) {
        flightsAssertion
                .discountIsDisplayed(discountCode, show);
    }

    @Then("^list of available flight is returned" + StepsRegex.FARE_TYPE_LIST + "$")
    public void bundleInResultsCheck(String bundle) {
        flightsAssertion
                .flightsBundleIs(Arrays.asList(bundle.split(",\\s")));
    }

    @Then("^list of available flight for group booking is returned$")
    public void groupBookingResultsCheck() {
        bundleInResultsCheck("Group");
        feeInResultsCheck("AdminFee", "is not");
        feeInResultsCheck("GRPBKGFee", "is");
        discountInResultsCheck("GroupBookingInternetDisc", "is");
    }

    @Then("^list of available flight for change is returned" + StepsRegex.FARE_TYPE_LIST + "$")
    public void changeFlightResultsCheck(String bundle) {
        getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
        BasketService basketService = testData.getData(BASKET_SERVICE);
        Basket basket = basketService.getResponse().getBasket();

        flightsAssertion
                .flightsBundleIs(Arrays.asList(bundle.split(",\\s")))
                .flightsPriceIsTheDeltaWithChangedFlight(basket)
                .feeIsDisplayed("AdminFee", "is not")
                .taxesAreAppliedToTheFlights(feesAndTaxesDao);
    }

    @Then("^list of available flight for change is returned (with|without) deal$")
    public void listOfAvailableFlightForChangeIsReturnedExistDeal(String dealApplication) {
        flightsAssertion.setResponse(getFlightsService.getResponse());
        List<DealModel> dealModels = dealDao.getDeals(true, true, true);
        List<HashMap<String, ItemModel>> dealList = new ArrayList<>();
        dealModels.forEach(
                dealModel -> dealList.add(dealDao.getDeal(dealModel.getSystemName(), dealModel.getOfficeId(), dealModel.getCorporateId(), getFlightsService.getResponse().getCurrency()))
        );

        if (dealApplication.equals("with")) {
            flightsAssertion
                    .flightsHaveDealApplied(deal);
        } else {
            flightsAssertion
                    .flightsHaveNoDealApplied(dealList);
        }
    }

    @Then("^Standby flight is( not)? returned$")
    public void standbyFlightIsNotReturned(String isReturned) {
        flightsAssertion.setResponse(getFlightsService.getResponse());
        flightsAssertion
                .fareIsReturned("Standby", StringUtils.isBlank(isReturned));
    }

}