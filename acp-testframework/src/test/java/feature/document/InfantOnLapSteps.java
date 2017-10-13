package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.FlightFinder;
import com.hybris.easyjet.fixture.hybris.helpers.*;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BoardingPassParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.GenerateBoardingPassRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.GenerateBoardingPassRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.operationconfirmation.BookingConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.GenerateBoardingPassService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.CommitBookingService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BOOKING_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.PASSENGER_CODES;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BoardingPassParams.GenerateBoardingPassPaths.DEFAULT;

/**
 * Created by albertowork on 7/3/17.
 */
@ContextConfiguration(classes = TestApplication.class)
public class InfantOnLapSteps {

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
    @Autowired
    private BookingHelper bookingHelper;
    @Autowired
    private CheckInHelper checkInHelper;
    @Getter
    private CommitBookingService commitBookingService;
    @Autowired
    private SetAPIHelper setAPIHelper = new SetAPIHelper();
    @Autowired
    private FlightFinder flightFinder;
    private GenerateBoardingPassService generateBoardingPassService;
    private BookingConfirmationResponse bookingResponse;
    private BasketsResponse basketsResponse;
    private List<String> allPassengerCodes;
    private BasketService basketService;
    private String flightKey;
    private List<Basket.Passenger> passengerList;
    private List<GenerateBoardingPassRequestBody.Flight.Passenger> passengersResponse;
    private GenerateBoardingPassRequestBody.Flight flightResponse;
    private String passengerCodeInfant;
    private String passengerCodeAdult;
    private String passengerTypes;


    @Given("^I have valid basket with via the ([^\"]*) with ([^\"]*)$")
    public void aFlightIsAddtocartViaTheChannel(String channel, String passengerTypes)
            throws Throwable {
        testData.setChannel(channel);
        this.passengerTypes = passengerTypes;
    }

    @And("^from ([^\"]*) to ([^\"]*)$")
    public void fromDepartureAirportToDestinationAirport(String departureAirport, String destinationAirport) throws Throwable {
        testData.setOrigin(departureAirport);
        testData.setDestination(destinationAirport);
        theChannelHasInitiatedACheckInForFlightForOnFlight(passengerTypes);
    }

    @When("^the channel initiates a generate boarding pass request for adult with infant on lap$")
    public void theChannelInitiatesAGenerateBoardingPassRequestForAdultWithInfant() throws Throwable {
        GenerateBoardingPassRequestBody requestBody = createBoardingPassRequestBody(flightKey);

        BoardingPassParams pathParams = BoardingPassParams.builder()
                .bookingId(bookingResponse.getBookingConfirmation().getBookingReference())
                .path(DEFAULT)
                .build();

        generateBoardingPassService = serviceFactory.getBoardingPassService(
                new GenerateBoardingPassRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, requestBody));
        generateBoardingPassService.invoke();
    }

    @Then("^I should get error (SVC_\\d+_\\d+)$")
    public void iWillReceiveAErrorMessageSVC__(String errorCode) throws Throwable {
        generateBoardingPassService.assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }

    @And("^the adult passenger has status check-in$")
    public void theAdultPassengerHasCheckInStatus() throws Throwable {
        passengerStatusShouldChangeToCheckedInOnTheFlight();
        channelSendGetbookingReequest();
        updatedPassengerStatusShouldReturnCheckedIn();
    }

    @When("^the requesting passenger has not a status of checked in$")
    public void theRequestingPassengerHasNotAStatusOfCheckedIn() throws Throwable {

    }

    @When("^I create a request to generate the boarding pass for adult and infant$")
    public void iCreateARequestToGenerateTheBoardingPassForAdultAndInfant() throws Throwable {
        GenerateBoardingPassRequestBody requestBody = createBoardingPassRequestBody(flightKey);

        BoardingPassParams pathParams = BoardingPassParams.builder()
                .bookingId(bookingResponse.getBookingConfirmation().getBookingReference())
                .path(DEFAULT)
                .build();

        generateBoardingPassService = serviceFactory.getBoardingPassService(
                new GenerateBoardingPassRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, requestBody));
        generateBoardingPassService.invoke();
        generateBoardingPassService.getResponse();
    }

    @And("^the departureAirport requires a separate boarding pass for the infant$")
    public void theDepartureAirportRequiresASeparateBoardingPassForTheInfant() throws Throwable {
        if(!testData.getOrigin().equals("AMS"))
            throw new Exception("The departure airport does NOT require a separeted boarding pass for infant");
    }

    @Then("^I will receive a boarding pass for the adult$")
    public void iWillReceiveABoardingPassForTheAdult() throws Throwable {
        String errorMessage = "THE ADULT PASSENGER DOESN'T HAVE BOARDING PASS";
        generateBoardingPassService.assertThat().selectedPassengersHaveBoardingPass(flightKey, passengerCodeAdult, errorMessage);
    }

    @And("^I will receive a boarding pass for the infant$")
    public void iWillReceiveABoardingPassForTheInfant() throws Throwable {
        String errorMessage = "THE INFANT PASSENGER DOESN'T HAVE BOARDING PASS";
        generateBoardingPassService.assertThat().selectedPassengersHaveBoardingPass(flightKey, passengerCodeInfant, errorMessage);
    }

    @And("^the departing airport requires a separate boarding pass for the infant$")
    public void theDepartingAirportRequiresASeparateBoardingPassForTheInfant() throws Throwable {
    }

    @And("^the departureAirport does NOT require a separate boarding pass for the infant$")
    public void theDepartureAirportDoesnTRequireASeparateBoardingPassForTheInfant() throws Throwable {
        if(testData.getOrigin().equals("AMS"))
            throw new Exception("The departure airport does NOT require a separeted boarding pass for infant");
    }

    @Then("^I will receive a boarding pass for the adult with plus infant next to adult name$")
    public void iWillReceiveABoardingPassForTheAdultWithPlusInfantNextToAdultName() throws Throwable {
        String errorMessage = "THE ADULT PASSENGER DOESN'T HAVE BOARDING PASS";
        generateBoardingPassService.assertThat().selectedPassengersHaveBoardingPass(flightKey, passengerCodeAdult, errorMessage);
    }

    @Then("^associate infant on seat to (.*) adult passenger until ratio is exceeded$")
    public void associateInfantOnSeatToFirstAdultPassengerUntilRatioIsExceeded(String passengerPosition) throws Throwable {
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());
        basketHelper.getBasketService().assertThat().checkThatRatioInfantToAdultsDoesNotExceedAllowed(basketHelper.getBasketService().getResponse().getBasket(), passengerPosition);
        testData.setData("PassengersOnFlight",getAllPassengers(basketHelper.getBasketService().getResponse().getBasket()));
    }

    public void theChannelHasInitiatedACheckInForFlightForOnFlight(String passengerMix) throws Throwable {

        testData.setData(BOOKING_ID, bookingHelper.createNewBooking(testData.getChannel(), passengerMix).getBookingConfirmation().getBookingReference());
        testData.setPassengerId(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0)
                .getFlights().get(0).getPassengers().get(0).getPassengerMap().get(0));

        bookingResponse = bookingHelper.getCommitBookingService().getResponse();
        basketsResponse = basketHelper.getBasketService().getResponse();
        flightKey = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getFlightKey();
        allPassengerCodes = getPassengerCode();
        passengerCodeAdult = getPassengerCodeByType("Adult");
        passengerCodeInfant = getPassengerCodeByType("Infant");
    }

    public void passengerStatusShouldChangeToCheckedInOnTheFlight() throws Throwable {
        setAPIHelper.invokeUpdateIdentityDocument(true);

        pollingLoop().untilAsserted(() -> {
            checkInHelper.checkInAFlight(Arrays.asList(testData.getPassengerId()));
            Assertions.assertThat(checkInHelper.checkInFlightService.getResponse());
        });
    }

    public void channelSendGetbookingReequest() throws Throwable {
        bookingHelper.getBookingDetails(testData.getData(SerenityFacade.DataKeys.BOOKING_ID), testData.getChannel());
    }

    public void updatedPassengerStatusShouldReturnCheckedIn() throws Throwable {
        pollingLoop().ignoreExceptions().untilAsserted(
                () -> {
                    bookingHelper.getBookingDetails(testData.getData(SerenityFacade.DataKeys.BOOKING_ID), testData.getChannel());

                    bookingHelper.getGetBookingService().assertThat().thePassengerCheckedin(
                            basketHelper.getBasketService().getResponse(),"CHECKED_IN");
                }
        );
    }

    private String getPassengerCodeByType(String type){
        if(type.equals("Adult")){
            return basketsResponse.getBasket().getOutbounds()
                    .get(0).getFlights().get(0).getPassengers().stream()
                    .filter(p -> CollectionUtils.isNotEmpty(p.getInfantsOnLap())).findFirst().get().getCode();
        }
        else if(type.equals("Infant")){
            return basketsResponse.getBasket().getOutbounds()
                    .get(0).getFlights().get(0).getPassengers().stream()
                    .filter(p -> CollectionUtils.isEmpty(p.getInfantsOnLap())).findFirst().get().getCode();
        }
        return null;
    }

    private List<String> getPassengerCode() {
        List<String> passengerCode = new ArrayList<>();
        passengerList = basketsResponse
                .getBasket()
                .getOutbounds().get(0)
                .getFlights().get(0)
                .getPassengers();

        for (Basket.Passenger p : passengerList) {
            passengerCode.add(p.getCode());
        }

        return passengerCode;
    }

    private List<Basket.Passenger> getAllPassengers(Basket basket){
        return basket.getOutbounds().stream()
                .flatMap(flights -> flights.getFlights().stream())
                .flatMap(p -> p.getPassengers().stream())
                .filter(p -> p.getActive().equals(true))
                .collect(Collectors.toList());
    }

    private GenerateBoardingPassRequestBody createBoardingPassRequestBody(String flightKey) {

        flightResponse = GenerateBoardingPassRequestBody.Flight.builder().build();
        flightResponse.setFlightKey(flightKey);

        passengersResponse = getPassengerCodeBoardingPassRequest();
        flightResponse.setPassengers(passengersResponse);

        List<GenerateBoardingPassRequestBody.Flight> flights = new ArrayList<>();
        flights.add(flightResponse);

        return GenerateBoardingPassRequestBody.builder()
                .language("fr")
                .flights(flights)
                .build();
    }

    private List<GenerateBoardingPassRequestBody.Flight.Passenger> getPassengerCodeBoardingPassRequest() {
        List<GenerateBoardingPassRequestBody.Flight.Passenger> passengerListResponse = new ArrayList<>();

        for (Basket.Passenger passenger : passengerList) {

            if(CollectionUtils.isNotEmpty(passenger.getInfantsOnLap())){
                GenerateBoardingPassRequestBody.Flight.Passenger passengerResponse = GenerateBoardingPassRequestBody.Flight.Passenger.builder().build();
                passengerResponse.setPassengerCode(passenger.getCode());
                passengerResponse.setAdditionalSeatsOnly(false);
                passengerListResponse.add(passengerResponse);
            }
        }
        return passengerListResponse;
    }
}
