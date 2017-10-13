package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.FlightFinder;
import com.hybris.easyjet.database.hybris.dao.BookingDao;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BookingHelper;
import com.hybris.easyjet.fixture.hybris.helpers.FlightHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.FlightQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.AddFlightRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.AddFlightRequestBodyFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.BasketRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.FlightsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.CommitBookingRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.GenerateBoardingPassResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket.Passenger;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.operationconfirmation.BookingConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.GenerateBoardingPassService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.CommitBookingService;
import cucumber.api.java.en.Given;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by albertowork on 5/24/17.
 */
@DirtiesContext
@ContextConfiguration(classes = TestApplication.class)
public class FlexiCommitProveStep {
    protected static Logger LOG = LogManager.getLogger(GenerateBoardingPassSteps.class);

    private GenerateBoardingPassService generateBoardingPassService;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    //    @Autowired
//    private TestData testData;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private BookingHelper commitBookingHelper;
    @Autowired
    private BookingDao bookingDao;
    @Autowired
    private FlightFinder flightFinder;
    @Autowired
    private AddFlightRequestBodyFactory addFlightRequestBodyFactory;
    @Autowired
    private FlightHelper flightHelper;

    private FindFlightsResponse.Flight flight;
    private CommitBookingService commitBookingService;
    private BookingConfirmationResponse bookingResponse;
    private GenerateBoardingPassResponse boardingPassResponseresponse;
    private FlightsService flightsService;
    private BasketsResponse basketsResponse;
    private BookingConfirmationResponse commitBookingResponse;
    private AddFlightRequestBody addFlight;
    private GenerateBoardingPassResponse boardingPassResponse;
    private List<String> passengerCodes;
    private String basketId;
    private BasketPathParams pathParams;
    private BasketService basketService;
    private String flightKey;
    private String defaultPassengerMix = "1 Adult";
    private String defaultChannel = "ADAirport";
    int plusDays = 5;

    @Given("^I have added a flight with \"([^\"]*)\" bundle to the basket to prove flexi$")
    public void iHaveAddedAFlightWithBundleToTheBasketToProveFlexi(String bundle) throws Throwable {
        if (bundle.equalsIgnoreCase("staff") || bundle.equalsIgnoreCase("staffstandard")
                || bundle.equalsIgnoreCase("standby"))
            addFlight = addFlightToBasketAsStaff(bundle, "1");
        else addFlight = basketHelper
                .myBasketContainsAFlightWithPassengerMixAndBundle(defaultPassengerMix, defaultChannel, bundle);

        flightKey = addFlight.getFlights().get(0).getFlightKey();
        testData.setCurrency(addFlight.getCurrency());

        basketHelper.addFlightToBasketAsChannel(addFlight, defaultChannel);
        getBasket();

        assertThat(basketService.getResponse().getBasket().getOutbounds()).isNotEmpty();

        basketsResponse = basketHelper.getBasketService().getResponse();
        passengerCodes = getPassengerCode();


        createBookingFromChannel(defaultChannel);
    }

    private AddFlightRequestBody addFlightToBasketAsStaff(String bundle, String adults) throws Throwable {
        return addStaffLTNCDGFlightToBasket(null, bundle, adults);
    }

    private AddFlightRequestBody addStaffLTNCDGFlightToBasket(String fk, String bundle, String numberOfAdults) throws Throwable {
        LocalDate today = LocalDate.now().plusDays(plusDays);
        plusDays++;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-yyy");
        String todayDate = today.format(df);

        FlightQueryParams params = FlightQueryParams.builder().origin("LTN").destination("CDG").outboundDate(todayDate)
                .inboundDate(todayDate).adult(numberOfAdults).build();

        FlightsService flightsService = serviceFactory
                .findFlight((new FlightsRequest(HybrisHeaders.getValid(testData.getChannel()).build(), params)));
        flightsService.invoke();
        if (testData.getCurrency() == null) {
            testData.setCurrency(flightsService.getResponse().getCurrency());
        }
        addFlightRequestBodyFactory = new AddFlightRequestBodyFactory(serviceFactory, flightFinder);
        addFlightRequestBodyFactory = new AddFlightRequestBodyFactory(serviceFactory, flightFinder);
        AddFlightRequestBody addFlight = addFlightRequestBodyFactory
                .buildFlightRequestForStaff(flightsService.getOutboundFlight(), testData.getCurrency(), "Staff");
        flightKey = addFlight.getFlights().get(0).getFlightKey();


        if (bundle.equalsIgnoreCase("staff") || bundle.equalsIgnoreCase("staffstandard")
                || bundle.equalsIgnoreCase("standby")) {
            addFlight.setBookingType("STAFF");
        }
        addFlight.setFareType(bundle);

        if (fk != null) addFlight.getFlights().get(0).setFlightKey(fk);

        basketHelper.addFlightToBasketAsChannel(addFlight, testData.getChannel());

        return addFlight;
    }

    public BasketService getBasket() throws Throwable {
        basketId = basketHelper.getBasketService().getResponse().getBasket().getCode();

        pathParams = BasketPathParams.builder().basketId(basketId).build();
        basketService = serviceFactory.getBasket(
                new BasketRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams));

        pollingLoop().untilAsserted(() -> {
            try {
                basketService.invoke();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            basketService.assertThat().gotAValidResponse();
        });
        return basketService;
    }

    private List<String> getPassengerCode() {
        List<String> passengerCode = new ArrayList<>();
        List<Passenger> passengerList = basketsResponse
                .getBasket()
                .getOutbounds().get(0)
                .getFlights().get(0)
                .getPassengers();

        for (Passenger p : passengerList) {
            passengerCode.add(p.getCode());
        }

        return passengerCode;
    }

    public void createBookingFromChannel(String channel) throws Throwable {
        CommitBookingRequest commitBookingRequest = setCommitBookingRequest(channel);

        commitBookingService = serviceFactory.commitBooking(commitBookingRequest);
        commitBookingService.invoke();
        commitBookingService.assertThat().gotAValidResponse();
        bookingResponse = commitBookingService.getResponse();
    }

    private CommitBookingRequest setCommitBookingRequest(String channel) throws Throwable {
        CommitBookingRequest commitBookingRequest =
                commitBookingHelper.createNewBookingRequestForChannelBasedOnBasket(basketsResponse, channel);

        return commitBookingRequest;
    }
}
