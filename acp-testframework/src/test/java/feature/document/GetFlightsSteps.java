package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.database.FlightFinder;
import com.hybris.easyjet.database.hybris.dao.PropertyValueConfigurationDao;
import com.hybris.easyjet.fixture.hybris.helpers.DateFormat;
import com.hybris.easyjet.fixture.hybris.helpers.FlightHelper;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.FlightQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.regex.Pattern;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.GET_FLIGHT_SERVICE;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.PASSENGERS_TOTAL;
import static com.hybris.easyjet.config.constants.CommonConstants.DEPARTURE_THRESHOLD_NAME_CHANGE;

/**
 * Created by giuseppedimartino on 31/03/17.
 */

@ContextConfiguration(classes = TestApplication.class)
public class GetFlightsSteps {
    public static final String BEFORE = "before";
    public static final int NUMBER_OF_DAYS = 2;
    @Autowired
    FlightHelper flightHelper;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private FlightFinder flightFinder;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    private FlightQueryParams.FlightQueryParamsBuilder findFlightQueryParams = FlightQueryParams.builder();
    private FlightsService flightsService;
    @Autowired
    private PropertyValueConfigurationDao propertyValueConfigurationDao;

    private int setPassengerQueryParam() {
        String passengerMix = testData.getPassengerMix();

        String[] passengers = passengerMix.split("\\s*;\\s*");
        int totalSeats = 0;
        for (String passenger : passengers) {

            String seats = passenger.split("\\s+")[0];
            String type = passenger.split("\\s+")[1];

            if (type.equals("infant")) {
                totalSeats += Integer.valueOf(seats.split(",")[0]) - Integer.valueOf(seats.split(",")[1]);
            } else {
                totalSeats += Pattern.compile(",")
                        .splitAsStream(seats)
                        .mapToInt(Integer::parseInt)
                        .sum();
            }

            try {
                findFlightQueryParams.getClass().getMethod(type, String.class).invoke(findFlightQueryParams, seats);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return totalSeats;
    }

    @Given("^I searched a flight for (\\d(?:,\\d)?\\s+\\w+(?:\\s*;\\s*\\d(?:,\\d)?\\s+\\w+)*)$")
    public void iSearchedAFlightForPassengerMix(String passengerMix) throws Throwable {
        testData.setPassengerMix(passengerMix);
        flightHelper.setSectors();
        flightsService = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate());
        testData.setData(GET_FLIGHT_SERVICE, flightsService);
        testData.setFlightKey(flightsService.getOutboundFlight().getFlightKey());
    }

    @Given("^I searched a '(Standard|Flexi|Staff|Standby)' flight with return for (.*)$")
    public void iSearchedAFareTypeFlightWithReturnForPassengerMix(String fareType, String passengerMix) throws Throwable {
        testData.setPassengerMix(passengerMix);
        int totalSeats = setPassengerQueryParam();
        testData.setData(PASSENGERS_TOTAL, totalSeats);
        if(!(fareType.equalsIgnoreCase(CommonConstants.STAFF)||fareType.equalsIgnoreCase(CommonConstants.STANDARD)))
        flightHelper.setSectors();
        flightsService = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), testData.getJourneyType(), testData.getOutboundDate(), testData.getInboundDate(), fareType, testData.getCurrency());
        testData.setData(GET_FLIGHT_SERVICE, flightsService);
    }


    @Given("^I searched a flight for '(.+)' with stock level for '(.+)' '(.+)'$")
    public void iSearchedAFlightForPassengerMixWithStockLevelForQuantityProduct(String passengerMix, int quantity, String product) throws Throwable {
        testData.setPassengerMix(passengerMix);
        testData.setData(SerenityFacade.DataKeys.QUANTITY,quantity);
        flightHelper.setSectors();
        flightsService = flightHelper.getFlights(testData.getChannel(), passengerMix, testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate());
        testData.setData(GET_FLIGHT_SERVICE, flightsService);
        testData.setFlightKey(flightsService.getOutboundFlight().getFlightKey());
    }


    @And("^I search for (.*) flight from (.*) to (.*) for (.*) via (.*)$")
    public void iSearchForFlightFromToForVia(String journey, String origin, String destination, String passengerMix, String channel) throws Throwable {
        testData.setPassengerMix(passengerMix);
        testData.setJourneyType(journey);
        flightsService = flightHelper.getFlights(channel, passengerMix, origin, destination, journey);
        testData.setData(GET_FLIGHT_SERVICE, flightsService);
        testData.setChannel(channel);
    }

    @And("^I search for flight with following details via (.*)$")
    public void iSearchForFlightWithFollowingDetailsVia(String channel, Map<String, String> data) throws Throwable {
        testData.setPassengerMix(data.get("passengerMix"));
        testData.setJourneyType(data.get("journey"));
        testData.setOrigin(data.get("origin"));
        testData.setDestination(data.get("destination"));
        flightsService = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), testData.getJourneyType(), testData.getOutboundDate(), testData.getInboundDate(), testData.getFareType());
        testData.setData(GET_FLIGHT_SERVICE, flightsService);
        testData.setChannel(channel);
    }

    @And("^I search for flight with (.*) departing '(before|after)' today plus the configure days with following details$")
    public void iSearchForFlightWithFollowingDetailsViaWithX(String passengerMix, String param, Map<String, String> data) throws Throwable {
        //Need to fetch from database once defined
        //TODO add name once confirmed
        int xValue = Integer.parseInt(propertyValueConfigurationDao.getPropertyValueBasedOnName(DEPARTURE_THRESHOLD_NAME_CHANGE));
        //int xValue=5;
        testData.setPassengerMix(passengerMix);
        testData.setJourneyType(data.get("journey"));
        testData.setOrigin(data.get("origin"));
        testData.setDestination(data.get("destination"));
        if (BEFORE.equalsIgnoreCase(param)) {
            testData.setOutboundDate(new DateFormat().today().addDay(xValue - 1));
            testData.setInboundDate(new DateFormat().today().addDay(xValue));
        } else {
            testData.setOutboundDate(new DateFormat().today().addDay(xValue + NUMBER_OF_DAYS));
        }
        flightsService = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), testData.getJourneyType(), testData.getOutboundDate(), testData.getInboundDate(), testData.getFareType());
        testData.setData(GET_FLIGHT_SERVICE, flightsService);
    }

    @And("^I search for flight with passenger mix (.*) and details as$")
    public void iSearchForFlightFollowingDetailsVia(String passengerMix, Map<String, String> data) throws Throwable {
        testData.setPassengerMix(passengerMix);
        testData.setJourneyType(data.get("journey"));
        testData.setOrigin(data.get("origin"));
        testData.setDestination(data.get("destination"));
        flightsService = flightHelper.getFlights(testData.getChannel(), passengerMix, data.get("origin"), data.get("destination"), data.get("journey"));
        testData.setData(GET_FLIGHT_SERVICE, flightsService);
    }


}
