package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BookingHelper;
import com.hybris.easyjet.fixture.hybris.helpers.FlightHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BoardingPassParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.GenerateBoardingPassRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.BasketRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.GenerateBoardingPassRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.CommitBookingRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket.Passenger;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.operationconfirmation.BookingConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.GenerateBoardingPassService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.CommitBookingService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BoardingPassParams.GenerateBoardingPassPaths.DEFAULT;
import static org.assertj.core.api.Java6Assertions.assertThat;


/**
 * Created by albertowork on 5/24/17.
 */
@DirtiesContext
@ContextConfiguration(classes = TestApplication.class)
public class GenerateBoardingPassSteps {
    protected static Logger LOG = LogManager.getLogger(GenerateBoardingPassSteps.class);

    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private BookingHelper commitBookingHelper;
    @Autowired
    private FlightHelper flightHelper;
    private GenerateBoardingPassService generateBoardingPassService;
    private BookingConfirmationResponse bookingResponse;
    private FlightsService flightsService;
    private BasketsResponse basketsResponse;
    private List<String> passengerCodes;
    private BasketService basketService;
    private String flightKey;


    @When("^the ([^\\s]*) initiates a generate boarding pass request$")
    public void theChannelInitiatesAGenerateBoardingPassRequest(String channel)
            throws Throwable {
        testData.setChannel(channel);
        String bookingIdWrong = "1111111";
        BoardingPassParams pathParams = BoardingPassParams.builder().bookingId(bookingIdWrong).path(DEFAULT).build();
        GenerateBoardingPassRequestBody body =
                GenerateBoardingPassRequestBody.builder()
                        .language("eng")
                        .flights(null)
                        .build();
        generateBoardingPassService = serviceFactory.getBoardingPassService(
                new GenerateBoardingPassRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, body));
        generateBoardingPassService.invoke();
    }

    @Then("^I will receive a error message (SVC_\\d+_\\d+)$")
    public void iWillReceiveAErrorMessageSVC__(String errorCode)
            throws Throwable {
        generateBoardingPassService.assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }

    @Given("^I have found a valid flight for the boarding pass via the ([^\"]*) and ([^\"]*)$")
    public void iHaveFoundAValidFlightForTheBoardingPassViaTheChannel(String channel, String passengerTypes) throws Throwable {
        testData.setChannel(channel);
        createAFlight(passengerTypes);
        createBookingFromChannel();
    }

    @When("^the requested flight ID not been found in the booking$")
    public void theRequestedFlightIDIsUnableToBeFoundOnTheBooking()
            throws Throwable {
        String flightKeyWrong = "20170531AMSCPH9334449";
        GenerateBoardingPassRequestBody requestBody = createBoardingPassRequestBody(flightKeyWrong);

        BoardingPassParams pathParams = BoardingPassParams.builder()
                .bookingId(bookingResponse.getBookingConfirmation().getBookingReference())
                .path(DEFAULT)
                .build();

        generateBoardingPassService = serviceFactory.getBoardingPassService(
                new GenerateBoardingPassRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, requestBody));
        generateBoardingPassService.invoke();
    }

    @Then("^I will generate a error message to inform unable to generate boarding pass$")
    public void iWillGenerateAErrorMessageToInformUnableToGenerateBoardingPass()
            throws Throwable {
        generateBoardingPassService.assertThat().additionalInformationReturned("SVC_100173_001");
    }

    @Then("^I will receive the boarding pass for all requested <passenger>$")
    public void iWillReceiveTheBoardingPassForAllRequestedPassenger()
            throws Throwable {
        generateBoardingPassService.assertThat().allPassengersHaveABoardingPass("20170615AMSCPH9339", passengerCodes);
    }

    @Then("^I will receive the boarding pass for the requested passenger$")
    public void iWillReceiveTheBoardingPassForTheRequestedPassenger() throws Throwable {
        generateBoardingPassService.assertThat().selectedPassengersHaveBoardingPass(testData.getFlightKey(), testData.getPassengerId(), "errorMessage");
    }


    @When("^I create a request to generate the boarding pass$")
    public void iCreateARequestToGenerateTheBoardingPass() throws Throwable {
        if (flightKey == null)
            flightKey = testData.getFlightKey().replace(" ", "");
        GenerateBoardingPassRequestBody requestBody = createBoardingPassRequestBody(flightKey);
        BoardingPassParams pathParams = BoardingPassParams.builder()
                .bookingId(testData.getData(SerenityFacade.DataKeys.BOOKING_ID))
                .path(DEFAULT)
                .build();

        generateBoardingPassService = serviceFactory.getBoardingPassService(
                new GenerateBoardingPassRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, requestBody));
        generateBoardingPass();
    }

    @And("^I will receive the URL to the location of the boarding pass$")
    public void iWillReceiveTheURLToTheLocationOfTheBoardingPass()
            throws Throwable {
        generateBoardingPassService.assertThat().allPassengersHaveAPdfLink(flightKey);
    }


    private void createAFlight(String passengerMix) throws Throwable {
        String requiredFare = "Standard";
        findFlight(passengerMix);
        FindFlightsResponse.Flight flight = flightsService.getOutboundFlight();

        basketHelper.addFlightToBasketAsChannelUsingFlightCurrencyWithPassengerMix(
                flightsService.getOutboundFlight(), passengerMix, testData.getChannel(), flightsService.getResponse().getCurrency(), requiredFare);

        getBasket();

        assertThat(basketService.getResponse().getBasket().getOutbounds()).isNotEmpty();
        basketService.assertThat().theBasketContainsTheFlight(flight);

        basketsResponse = basketHelper.getBasketService().getResponse();
        passengerCodes = getPassengerCode();
    }

    public BasketService getBasket() throws Throwable {
        String basketId = basketHelper.getBasketService().getResponse().getBasket().getCode();

        BasketPathParams pathParams = BasketPathParams.builder().basketId(basketId).build();
        basketService = serviceFactory.getBasket(new BasketRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams));

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

    public FindFlightsResponse.Flight findFlight(String passengerMix) throws Throwable {
        flightsService = flightHelper.getFlights(testData.getChannel(), passengerMix, testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate());
        FindFlightsResponse.Flight flight = flightsService.getOutboundFlight();
        flightKey = flight.getFlightKey();
        return flight;
    }

    private void createBookingFromChannel() throws Throwable {
        CommitBookingRequest commitBookingRequest = setCommitBookingRequest();
        CommitBookingService commitBookingService = serviceFactory.commitBooking(commitBookingRequest);
        commitBookingService.invoke();
        commitBookingService.assertThat().gotAValidResponse();
        bookingResponse = commitBookingService.getResponse();
    }

    private CommitBookingRequest setCommitBookingRequest() throws Throwable {
        return commitBookingHelper.createNewBookingRequestForChannelBasedOnBasket(basketsResponse, testData.getChannel());
    }

    private GenerateBoardingPassRequestBody createBoardingPassRequestBody(String flightKey) {

        GenerateBoardingPassRequestBody.Flight flight = GenerateBoardingPassRequestBody.Flight.builder().build();
        flight.setFlightKey(flightKey);

        List<GenerateBoardingPassRequestBody.Flight.Passenger> passengers = getPassengerCodeBoardingPassRequest();
        flight.setPassengers(passengers);

        List<GenerateBoardingPassRequestBody.Flight> flights = new ArrayList<>();
        flights.add(flight);

        return GenerateBoardingPassRequestBody.builder()
                .language("fr")
                .flights(flights)
                .build();
    }

    private GenerateBoardingPassRequestBody createBoardingPassForFlightRequestBody(String flightKey) {
        GenerateBoardingPassRequestBody.Flight flight = GenerateBoardingPassRequestBody.Flight.builder().build();
        flight.setFlightKey(flightKey);
        GetBookingResponse getBookingResponse = testData.getData(SERVICE);
        List<GenerateBoardingPassRequestBody.Flight.Passenger> boardingPassPassengers = new ArrayList<>();

        getBookingResponse.getBookingContext().getBooking()
                .getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream()
                        .filter(ff -> ff.getFlightKey().equalsIgnoreCase(flightKey))
                        .flatMap(i -> i.getPassengers().stream()))
                .forEach(psg -> {
                    GenerateBoardingPassRequestBody.Flight.Passenger passenger = GenerateBoardingPassRequestBody.Flight.Passenger.builder().build();
                    passenger.setPassengerCode(psg.getCode());
                    passenger.setAdditionalSeatsOnly(false);
                    boardingPassPassengers.add(passenger);
                });
        flight.setPassengers(boardingPassPassengers);

        List<GenerateBoardingPassRequestBody.Flight> flights = new ArrayList<>();
        flights.add(flight);

        return GenerateBoardingPassRequestBody.builder()
                .language("fr")
                .flights(flights)
                .build();
    }

    private GenerateBoardingPassRequestBody createBoardingPassForGetBookingRequestBody(String flightKey) {
        GenerateBoardingPassRequestBody.Flight flight = GenerateBoardingPassRequestBody.Flight.builder().build();
        flight.setFlightKey(flightKey);
        GetBookingResponse getBookingResponse = testData.getData(GET_BOOKING_RESPONSE);
        List<GenerateBoardingPassRequestBody.Flight.Passenger> boardingPassPassengers = new ArrayList<>();

        getBookingResponse.getBookingContext().getBooking()
                .getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream()
                        .filter(ff -> ff.getFlightKey().equalsIgnoreCase(flightKey))
                        .flatMap(i -> i.getPassengers().stream()))
                .forEach(psg -> {
                    GenerateBoardingPassRequestBody.Flight.Passenger passenger = GenerateBoardingPassRequestBody.Flight.Passenger.builder().build();
                    passenger.setPassengerCode(psg.getCode());
                    passenger.setAdditionalSeatsOnly(false);
                    boardingPassPassengers.add(passenger);
                });
        flight.setPassengers(boardingPassPassengers);

        List<GenerateBoardingPassRequestBody.Flight> flights = new ArrayList<>();
        flights.add(flight);

        return GenerateBoardingPassRequestBody.builder()
                .language("fr")
                .flights(flights)
                .build();
    }

    private List<String> getPassengerCode() {
        List<String> passengerCode = new ArrayList<>();
        BasketService basketService = testData.getData(BASKET_SERVICE);
        List<Passenger> passengerList = basketService.getResponse()
                .getBasket()
                .getOutbounds().get(0)
                .getFlights().get(0)
                .getPassengers();

        for (Passenger p : passengerList) {
            passengerCode.add(p.getCode());
        }

        return passengerCode;
    }

    private List<String> getPassengerCode(String flightKey) {
        BasketService basketService = testData.getData(BASKET_SERVICE);
        return basketService.getResponse()
                .getBasket()
                .getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream()
                        .filter(ff -> ff.getFlightKey().equalsIgnoreCase(flightKey))
                        .flatMap(i -> i.getPassengers().stream()
                                .map(p -> p.getCode())))
                .collect(Collectors.toList());
    }

    private List<GenerateBoardingPassRequestBody.Flight.Passenger> getPassengerCodeBoardingPassRequest() {
        List<GenerateBoardingPassRequestBody.Flight.Passenger> passengerList = new ArrayList<>();
        passengerCodes = getPassengerCode();
        for (String passengerCode : passengerCodes) {
            GenerateBoardingPassRequestBody.Flight.Passenger passenger = GenerateBoardingPassRequestBody.Flight.Passenger.builder().build();
            passenger.setPassengerCode(passengerCode);
            passenger.setAdditionalSeatsOnly(false);
            passengerList.add(passenger);
        }

        return passengerList;
    }

    private List<GenerateBoardingPassRequestBody.Flight.Passenger> getPassengerCodeBoardingPassRequest(String flightKey) {
        List<GenerateBoardingPassRequestBody.Flight.Passenger> passengerList = new ArrayList<>();
        passengerCodes = getPassengerCode(flightKey);
        for (String passengerCode : passengerCodes) {
            GenerateBoardingPassRequestBody.Flight.Passenger passenger = GenerateBoardingPassRequestBody.Flight.Passenger.builder().build();
            passenger.setPassengerCode(passengerCode);
            passenger.setAdditionalSeatsOnly(false);
            passengerList.add(passenger);
        }
        return passengerList;
    }

    @When("^I create new boarding pass for updated customer$")
    public void iCreateNewBoardingPassForUpdatedCustomer() throws Throwable {
        String newFlightKey = testData.getData(NEW_FLIGHT_KEY);
        testData.setFlightKey(newFlightKey);
        GenerateBoardingPassRequestBody requestBody = createBoardingPassForFlightRequestBody(newFlightKey);
        BoardingPassParams pathParams = BoardingPassParams.builder()
                .bookingId(testData.getData(SerenityFacade.DataKeys.BOOKING_ID))
                .path(DEFAULT)
                .build();

        generateBoardingPassService = serviceFactory.getBoardingPassService(
                new GenerateBoardingPassRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, requestBody));
        generateBoardingPass();
    }

    @When("^I send a boarding pass request for updated customer$")
    public void iCreateNewBoardingPassForUpdatedCustomerWithoutPolling() throws Throwable {
        GetBookingResponse getBookingResponse = testData.getData(GET_BOOKING_RESPONSE);
        String newFlightKey = getBookingResponse.getBookingContext().getBooking().getOutbounds().get(0).getFlights().get(0).getFlightKey();
        testData.setFlightKey(newFlightKey);
        GenerateBoardingPassRequestBody requestBody = createBoardingPassForGetBookingRequestBody(newFlightKey);
        BoardingPassParams pathParams = BoardingPassParams.builder()
                .bookingId(testData.getData(SerenityFacade.DataKeys.BOOKING_ID))
                .path(DEFAULT)
                .build();

        generateBoardingPassService = serviceFactory.getBoardingPassService(
                new GenerateBoardingPassRequest(HybrisHeaders.getValid(testData.getData(CHANNEL)).build(), pathParams, requestBody));
        generateBoardingPassService.invoke();

    }

    private void generateBoardingPass() {
        pollingLoop().untilAsserted(() -> {
            generateBoardingPassService.invoke();
            assertThat(generateBoardingPassService.getRestResponse().getStatusCode()).isEqualTo(200);
        });
        generateBoardingPassService.getResponse();
    }
}
