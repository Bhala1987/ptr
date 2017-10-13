package feature.document.steps.services.createbasketservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.asserters.BasketsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.AddFlightRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.Flight;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.Passenger;
import com.hybris.easyjet.fixture.hybris.invoke.requests.BasketRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import com.hybris.easyjet.models.PassengerMix;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import feature.document.steps.constants.StepsRegex;
import feature.document.steps.services.GetFlightsSteps;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;

/**
 * AddFlightSteps handle the communication with the addFlight service (aka addFlightToBasket).
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 *
 * @author gd <g.dimartino@reply.it>
 */
@ContextConfiguration(classes = TestApplication.class)
public class AddFlightSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Steps
    private GetFlightsSteps getFlightsSteps;
    @Steps
    private AddHoldBagProductSteps addHoldBagProductSteps;
    @Steps
    private AddSportsEquipProductSteps addSportsEquipProductSteps;
    @Steps
    private BasketsAssertion basketAssertion;
    @Steps
    private GetBasketSteps getBasketSteps;

    private BasketService basketService;
    private AddFlightRequestBody addFlightRequestBody;

    /**
     * build the passenger list object to use for the addFlight request body from the Passengers object stored in testData
     *
     * @return a List of Passenger
     */
    private List<Passenger> buildPassengerList() {
        PassengerMix passengerMix = testData.getData(PASSENGERS);
        List<Passenger> passengerList = new ArrayList<>();
        Passenger.PassengerBuilder passengerBuilder = Passenger.builder();

        if (passengerMix.getAdult() != 0) {
            passengerBuilder.passengerType("adult");
            passengerBuilder.quantity(passengerMix.getAdult());
            passengerBuilder.additionalSeats(passengerMix.getAdditionalAdult());
            passengerBuilder.infantOnSeat(false);
            passengerList.add(passengerBuilder.build());
        }
        if (passengerMix.getChild() != 0) {
            passengerBuilder.passengerType("child");
            passengerBuilder.quantity(passengerMix.getChild());
            passengerBuilder.additionalSeats(passengerMix.getAdditionalChild());
            passengerBuilder.infantOnSeat(false);
            passengerList.add(passengerBuilder.build());
        }
        if (passengerMix.getInfantOnLap() != 0) {
            passengerBuilder.passengerType("infant");
            passengerBuilder.quantity(passengerMix.getInfantOnLap());
            passengerBuilder.additionalSeats(0);
            passengerBuilder.infantOnSeat(false);
            passengerList.add(passengerBuilder.build());
        }
        if (passengerMix.getInfantOnSeat() != 0) {
            passengerBuilder.passengerType("infant");
            passengerBuilder.quantity(passengerMix.getInfantOnSeat());
            passengerBuilder.additionalSeats(passengerMix.getAdditionalInfant());
            passengerBuilder.infantOnSeat(true);
            passengerList.add(passengerBuilder.build());
        }

        return passengerList;
    }

    /**
     * Prepare the builder for the body request for addFlight getting data from testData values
     * It will set overrideWarning field in the request body as false
     *
     * @param journeyType it can be outbound, inbound or single; it can be null, in which case it will be set to single
     */
    private void setRequestBody(String journeyType) {
        setRequestBody(journeyType, false);
    }

    /**
     * Prepare the builder for the body request for addFlight getting data from testData values
     *
     * @param journeyType     it can be outbound, inbound or single; it can be null, in which case it will be set to single
     * @param overrideWarning it can be true or false
     */
    private void setRequestBody(String journeyType, boolean overrideWarning) {
        FindFlightsResponse.Flight flight;
        if (StringUtils.isBlank(journeyType)) {
            journeyType = "single";
        }
        switch (journeyType) {
            case "outbound":
                flight = testData.getData(OUTBOUND_FLIGHT);
                break;
            case "inbound":
                flight = testData.getData(INBOUND_FLIGHT);
                break;
            default:
                flight = testData.getData(OUTBOUND_FLIGHT);
                break;
        }
        testData.setData(FLIGHT_KEY, flight.getFlightKey());

        List<Passenger> passengerList = buildPassengerList();

        //The presence of the fareType was checked during the findFlight; the price is the same for all the passengers
        Double price = flight.getFareTypes().stream()
                .filter(fareType -> fareType.getFareTypeCode().equals(testData.getData(FARE_TYPE)))
                .findFirst().get()
                .getPassengers().get(0)
                .getBasePrice();

        String routeCode = flight.getDeparture().getAirportCode() + flight.getArrival().getAirportCode();

        List<Flight> flightList = new ArrayList<>();
        Flight flightRequest = Flight.builder()
                .flightKey(flight.getFlightKey())
                .flightPrice(price)
                .sector(routeCode)
                .build();
        flightList.add(flightRequest);

        FlightsService flightsService = testData.getData(GET_FLIGHT_SERVICE);
        addFlightRequestBody = AddFlightRequestBody.builder()
                .flights(flightList)
                .toeiCode(UUID.randomUUID().toString())
                .currency(flightsService.getResponse().getCurrency())
                .routeCode(routeCode)
                .fareType(testData.getData(FARE_TYPE))
                .journeyType(journeyType)
                .overrideWarning(overrideWarning)
                .passengers(passengerList)
                .routePrice(price)
                .bookingType(testData.getData(BOOKING_TYPE))
                .build();

        testData.setData(BUNDLE, testData.getData(FARE_TYPE));
    }

    /**
     * Create the request object, getting headers from testData and body from the previous method, and invoke the service;
     * it store the service created into testData SERVICE
     */
    private void invokeBasketService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        basketService = serviceFactory.addFlight(new BasketRequest(headers.build(), addFlightRequestBody));
        testData.setData(SERVICE, basketService);
        testData.setData(BASKET_SERVICE, basketService);
        basketService.invoke();
    }

    /**
     * Call the findFlight and add a flight to the basket as specified in the arguments
     *
     * @param journeyType   it can be outbound, inbound, single or outbound/inbound; it can be null, in which case will be set to single
     * @param origin        departure airport code (\w{3}); it can be null, in which case the getSector will be called and a random sector will be selected
     * @param destination   destination airport (\w{3}); it can be null, in which case the getSector will be called and a random sector will be selected
     * @param fareType      bundle code; it can be null
     * @param passengerMix  passenger mix (\d(,\d)? passengerType;); it can be null, in which case will be set to 1 adult
     * @param outboundDate  departure date in meaningful english (i.e. today, in \d+ days); it can be null, in which case will be set to tomorrow
     * @param inboundDate   return date in meaningful english (i.e. in \d+ days); it can be null, in which case, if withReturn is true, will be set to 3 days from today
     * @param applicationId application id of the deal; it can be null
     * @param officeId      office id of the deal; it can be null
     * @param corporateId   corporate id of the deal; it can be null
     * @param currency      currency ; it can be null
     * @throws EasyjetCompromisedException if the date argument doesn't include a number of day to add from today and is not today
     */
    @Step("Add flights to basket")
    @Given("^I added a flight to the basket" + StepsRegex.JOURNEY + StepsRegex.HOLD_ITEMS + StepsRegex.SPORTS_ITEMS + StepsRegex.SECTOR + StepsRegex.FARE_TYPE + StepsRegex.PASSENGER_MIX + StepsRegex.DATES + StepsRegex.FLEXIBLE_DAYS + StepsRegex.DEAL + StepsRegex.CURRENCY + "$")
    public void sendAddFlightRequest(String journeyType, String addHoldBag, Integer holdBag, Integer excessWeightQuantity, String excessWeightType, String passengerWithHolddBag, String addSportItem, Integer sportItemQty, String passengerWithSportItem, String origin, String destination, String fareType, String passengerMix, String outboundDate, String inboundDate, String flexibleDays, String applicationId, String officeId, String corporateId, String currency) throws EasyjetCompromisedException {
        String withReturn = null;
        if (StringUtils.isBlank(journeyType)) {
            journeyType = "single";
        } else if (journeyType.equals("outbound/inbound")) {
            withReturn = "with return";
        }
        getFlightsSteps.searchFlights(withReturn, origin, destination, fareType, passengerMix, outboundDate, inboundDate, flexibleDays, applicationId, officeId, corporateId, currency);

        if (journeyType.equals("outbound/inbound")) {
            setRequestBody("outbound");
            invokeBasketService();
            setRequestBody("inbound");
            invokeBasketService();
        } else {
            setRequestBody(journeyType);
            invokeBasketService();
        }
        testData.setData(BASKET_ID, basketService.getResponse().getBasket().getCode());

        if (StringUtils.isNotBlank(addHoldBag)) {
            addHoldBagProductSteps.addHoldBag(holdBag, excessWeightType, excessWeightQuantity, passengerWithHolddBag);
        }
        if (StringUtils.isNotBlank(addSportItem)) {
            addSportsEquipProductSteps.addSportItem(sportItemQty, passengerWithSportItem);
        }
    }

    @When("^I add a flight to the basket" + StepsRegex.JOURNEY + StepsRegex.HOLD_ITEMS + StepsRegex.SECTOR + StepsRegex.FARE_TYPE + StepsRegex.PASSENGER_MIX + StepsRegex.DATES + StepsRegex.DEAL + StepsRegex.CURRENCY + "$")
    public void setAddFlightParameters(String journeyType, String addHoldBag, Integer holdBag, Integer excessWeightQuantity, String excessWeightType, String passengerWithHolddBag, String origin, String destination, String fareType, String passengerMix, String outboundDate, String inboundDate, String applicationId, String officeId, String corporateId, String currency) {
        if (StringUtils.isNotBlank(origin)) {
            testData.setData(ORIGIN, origin);
        }
        if (StringUtils.isNotBlank(destination)) {
            testData.setData(DESTINATION, destination);
        }
        if (StringUtils.isNotBlank(fareType)) {
            testData.setData(FARE_TYPE, fareType);
        }
        if (StringUtils.isNotBlank(passengerMix)) {
            testData.setData(PASSENGERS, new PassengerMix(passengerMix));
        }
        if (StringUtils.isNotBlank(outboundDate)) {
            testData.setData(OUTBOUND_DATE, outboundDate);
        }
        if (StringUtils.isNotBlank(inboundDate)) {
            testData.setData(INBOUND_DATE, inboundDate);
        }
        if (StringUtils.isNotBlank(applicationId)) {
            testData.setData(APPLICATION_ID, applicationId);
        }
        if (StringUtils.isNotBlank(officeId)) {
            testData.setData(OFFICE_ID, officeId);
        }
        if (StringUtils.isNotBlank(corporateId)) {
            testData.setData(CORPORATE_ID, corporateId);
        }
        if (StringUtils.isNotBlank(currency)) {
            testData.setData(CURRENCY, currency);
        }
        if (StringUtils.isNotBlank(journeyType)) {
            if (journeyType.equals("outbound/inbound")) {
                sendAddFlightRequest();
            }
        } else {
            sendAddFlightRequest(journeyType);
        }
        if (StringUtils.isNotBlank(addHoldBag)) {
            addHoldBagProductSteps.addHoldBag(holdBag, excessWeightType, excessWeightQuantity, passengerWithHolddBag);
        }
    }

    /**
     * Add a flight with return to the basket getting data from testData values;
     * it stores the basket in the response in testData
     */
    @Step("Add flight: Journey type outbound/inbound")
    @When("^I add the flight to the basket as an outbound/inbound journey$")
    public void sendAddFlightRequest() {
        setRequestBody("outbound");
        invokeBasketService();
        setRequestBody("inbound");
        invokeBasketService();
    }

    /**
     * Add a flight to the basket getting data from testData values with the specified journy type;
     * it stores the basket in the response in testData
     *
     * @param journeyType it can be outbound, inbound or single
     */
    @Step("Add flight: Journey type {0}")
    @When("^I add the flight to the basket" + StepsRegex.JOURNEY + "$")
    public void sendAddFlightRequest(String journeyType) {
        setRequestBody(journeyType);
        invokeBasketService();
    }

    @Step("Add flight: Journey type {0}, overrideWarning {1}")
    @When("^I added the flight to the basket" + StepsRegex.JOURNEY + "(?: with override (true|false))?$")
    public void sendAddFlightRequest(String journeyType, String overrideWarning) {
        setRequestBody(journeyType, Boolean.valueOf(overrideWarning));
        invokeBasketService();
        testData.setData(BASKET_ID, basketService.getResponse().getBasket().getCode());
    }

    @Step("Add flights to basket as group booking")
    @Given("^I added a flight to the basket" + StepsRegex.JOURNEY + StepsRegex.HOLD_ITEMS + StepsRegex.SPORTS_ITEMS + StepsRegex.SECTOR + StepsRegex.FARE_TYPE + StepsRegex.PASSENGER_MIX + StepsRegex.DATES + StepsRegex.FLEXIBLE_DAYS + StepsRegex.DEAL + StepsRegex.CURRENCY + " as group booking$")
    public void sendAddFlightForGroupRequest(String journeyType, String addHoldBag, Integer holdBag, Integer excessWeightQuantity, String excessWeightType, String passengerWithHolddBag, String addSportItem, Integer sportItemQty, String passengerWithSportItem, String origin, String destination, String fareType, String passengerMix, String outboundDate, String inboundDate, String flexibleDays, String applicationId, String officeId, String corporateId, String currency) throws EasyjetCompromisedException {
        String withReturn = null;
        if (StringUtils.isBlank(journeyType)) {
            journeyType = "single";
        } else if (journeyType.equals("outbound/inbound")) {
            withReturn = "with return";
        }
        getFlightsSteps.setGetFlightsParametersForGroupBooking(withReturn, origin, destination, fareType, passengerMix, outboundDate, inboundDate, flexibleDays, applicationId, officeId, corporateId, currency);
        getFlightsSteps.sentGetFlightsRequest();

        if (journeyType.equals("outbound/inbound")) {
            sendAddFlightRequest();
        } else {
            sendAddFlightRequest(journeyType);
        }

        testData.setData(BASKET_ID, basketService.getResponse().getBasket().getCode());

        if (StringUtils.isNotBlank(addHoldBag)) {
            addHoldBagProductSteps.addHoldBag(holdBag, excessWeightType, excessWeightQuantity, passengerWithHolddBag);
        }
        if (StringUtils.isNotBlank(addSportItem)) {
            addSportsEquipProductSteps.addSportItem(sportItemQty, passengerWithSportItem);
        }
    }

    @Then("^the basket with added flight is returned$")
    public void theBasketWithAddedFlightIsReturned() {
        basketAssertion.setResponse(basketService.getResponse());
        basketAssertion.flightIsAddedToTheBasket(testData.getData(FLIGHT_KEY));
    }

    @Then("^the flight standby stock level is (reserved|released)$")
    public void checkStandbyStockLevel(String check) {
        switch (check) {
            case "reserved":
                basketAssertion
                        .standbyStockLevelIsReserved(testData.getData(FLIGHT_KEY));
                break;
            case "released":
                basketAssertion
                        .standbyStockLevelIsReleased(testData.getData(FLIGHT_KEY));
                break;
        }
    }

    @Then("^I verify the admin fee is not added$")
    public void iVerifyTheAdminFeeIsNotAdded() {
        getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
        basketAssertion.setResponse(basketService.getResponse());
        basketAssertion.verifyAdminFeeIsNotAdded();
    }

    @And("^I verify the (group booking internet discount|group booking fee) is applied$")
    public void iVerifyTheGroupBookingInternetDiscountIsApplied(String type) {
        basketAssertion.setResponse(basketService.getResponse());
        basketAssertion.verifyGroupBookingFeesDiscountsAdded(type);
    }
}