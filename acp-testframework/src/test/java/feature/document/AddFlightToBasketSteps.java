package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.FlightFinder;
import com.hybris.easyjet.database.hybris.dao.CartDao;
import com.hybris.easyjet.database.hybris.dao.FlightsDao;
import com.hybris.easyjet.database.hybris.dao.HoldItemsDao;
import com.hybris.easyjet.database.hybris.dao.IndirectFlightRoutesDao;
import com.hybris.easyjet.database.hybris.models.BasketPassengerBasicInfoModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.*;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.FlightQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.factory.FlightQueryParamsFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.AddFlightRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.AddFlightRequestBodyFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.FlightsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.Scenario;
import cucumber.api.java.en.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Dan on 01/11/2016.
 */
@ContextConfiguration(classes = TestApplication.class)

public class AddFlightToBasketSteps {

    private static final Logger LOG = LogManager.getLogger(AddFlightToBasketSteps.class);
    private AddFlightRequestBody addFlight;
    int plusDays = 5;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private FlightFinder flightFinder;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private CartDao cartdao;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private IndirectFlightRoutesDao indirectFlightRoutesDao;
    @Autowired
    private FlightHelper flightHelper;
    @Autowired
    private FlightsDao flightsDao;
    private String expectedFare;

    private String basketCode;
    private FlightsService flightsService;
    private FindFlightsResponse foundFlights;
    private List<AddFlightRequestBody> addFlights;
    private String channelUsed;
    private String flightKey;
    private String defaultPassengerMix = "1 Adult";
    private String defaultChannel = "Digital";
    private FindFlightsResponse.Flight flight;
    private AddFlightRequestBodyFactory addFlightRequestBodyFactory;
    private String fareType;
    private String oldBasketId;
    private String newBasketId;
    @Autowired
    private HoldItemsDao holdItemsDao;
    @Autowired
    private BookingHelper bookingHelper;
    private List<Double> oldListOfPrices;
    private List<Double> newListOfPrices;


    @When("^I add the flight to my basket via the (.*)$")
    public void iAddTheFlightToMyBasketViaTheChannel(String channel) throws Throwable {
        testData.setOutboundFlight(flightsService.getOutboundFlight());
        basketHelper.addFlightToBasketAsChannelUsingFlightCurrency(
                testData.getOutboundFlight(), channel, flightsService.getResponse().getCurrency());
        testData.setData(GET_FLIGHT_SERVICE, flightsService);
    }

    @When("^call empty basket service$")
    public void callEmptyBasketService() throws Throwable {
        basketCode = basketHelper.getBasketService().getResponse().getBasket().getCode();
        basketHelper.emptyBasket(basketCode, "Digital");
    }

    @Given("^my basket contains flight with passengerMix \"([^\"]*)\"$")
    public void myBasketContainsFlightWithPassengerMix(String passengerMix) throws Throwable {
        basketHelper.myBasketContainsAFlightWithPassengerMix(passengerMix);
        testData.setBasketId(basketHelper.getBasketService().getResponse().getBasket().getCode());
    }

    @Then("^the flight is added to the basket via the (.*)$")
    public void theFlightIsAddedToTheBasketViaThe(String channel) throws Throwable {
        assertThat(basketHelper.getBasketService().getResponse().getBasket().getOutbounds()).isNotEmpty();

        // Make sure that basket is empty before adding stuff in.
        basketCode = basketHelper.getBasketService().getResponse().getBasket().getCode();
    }

    @Then("^the basket flights should be linked together using the linkedFlights attribute$")
    public void theBasketFlightsShouldBeLinkedTogetherUsingTheLinkedFlightsAttribute() {
        Basket basket = basketHelper.getBasketService().getResponse().getBasket();

        assertThat(basket.getOutbounds()).isNotEmpty();
        assertThat(basket.getInbounds()).isNotEmpty();

        basket.getInbounds().stream()
                .flatMap(inboundFlights -> inboundFlights.getFlights().stream())
                .forEach(inboundFlight -> assertThat(
                        basket.getOutbounds().stream()
                                .flatMap(outboundFlights -> outboundFlights.getFlights().stream())
                                .anyMatch(outboundFlight -> outboundFlight.getLinkedFlights().contains(
                                        inboundFlight.getFlightKey()
                                ))
                        ).isTrue()
                );
    }

    @Given("^my basket contains flight with passengerMix \"([^\"]*)\" added via \"([^\"]*)\"$")
    public void myBasketContainsFlightWithPassengerMixAddedVia(String passengerMix, String channel) throws Throwable {
        testData.setChannel(channel);
        expectedFare = "Standard";
        basketHelper.myBasketContainsAFlightWithPassengerMix(passengerMix, channel, expectedFare, false);
    }

    @Then("^the base price is returned$")
    public void theBasePriceIsReturned() throws Throwable {
        basketHelper.getBasketService().assertThat().theBasePriceIsReturnedForEachPassenger();
    }

    @Then("^the selected bundle is added to each passenger$")
    public void theSelectedBundleIsAddedToEachPassenger() throws Throwable {
        basketHelper.getBasketService().assertThat().theFareBundleIsAddedToEachPassenger(expectedFare);
    }

    @Given("^there are multiple valid flights with different departure airports that have different default currencies$")
    public void thereAreMultipleValidFlightsWithDifferentDepartureAirportsThatHaveDifferentDefaultCurrencies() throws Throwable {
        addFlights = basketHelper.findMultipleFlightsWithDifferentBaseCurrencies("Digital");
    }

    @When("^I add the flights to the basket$")
    public void iAddTheFlightsToTheBasket() throws Throwable {
        for (AddFlightRequestBody flight : addFlights) {
            basketHelper.addFlightToBasketAsChannel(flight, "Digital");
        }
    }

    @Then("^the default currency is defined by first departure airport$")
    public void theDefaultCurrencyIsDefinedByFirstDepartureAirport() throws Throwable {
        basketHelper.getBasketService().assertThat().theCurrencyOfTheBasketIsDefinedAsTheFirstFlight(addFlights);
    }

    @Given("^I have found a valid flight via the (.*)$")
    public void iHaveFoundAValidFlightViaTheChannel(String channel) throws Throwable {
        flightsService = flightHelper.getFlights(testData.getChannel(), "1 adult", testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate());
        foundFlights = flightsService.getResponse();
        assertThat(foundFlights.getOutbound().getJourneys()).isNotEmpty();
    }

    @And("^I search a flight for \"([^\"]*)\" from \"([^\"]*)\" to \"([^\"]*)\"$")
    public void i_search_a_flight_for_something_from_something_to_something(String passengerMix, String origin, String destination) throws Throwable {
        if (testData.getFareType() == null) {
            flightsService = flightHelper.getFlights(testData.getChannel(), passengerMix, origin, destination, null, testData.getOutboundDate(), testData.getInboundDate());
        } else {
            flightsService = flightHelper.getFlights(testData.getChannel(), passengerMix, origin, destination, null, testData.getOutboundDate(), testData.getInboundDate(), testData.getFareType());
        }
        foundFlights = flightsService.getResponse();
        assertThat(foundFlights.getOutbound().getJourneys()).isNotEmpty();
    }

    @Then("^the base price and associated taxes are the same in the basket$")
    public void theBasepriceAndAssociatedTaxesAreTheSameInTheBasket() throws Throwable {
        basketHelper.getBasketService().assertThat().flightBasePriceAndFeesAreTheSameForASinglePassenger(
                testData.getOutboundFlight());
//                foundFlights.getOutbound().getJourneys().get(0).getFlights().get(0));


    }

    @Then("^the infant is autoallocated to first Adult$")
    public void theInfantIsAutoallocatedToFirstAdult() throws Throwable {
        basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        basketHelper.getBasketService().assertThat().infantIsNowOnLapOfFirstAdult();
    }

    @Then("^the infant is autoallocated to only Adult$")
    public void theInfantIsAutoallocatedToOnlyAdult() throws Throwable {
        basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        basketHelper.getBasketService().assertThat().infantIsNowOnLapOfFirstAdult();
    }

    @Given("^my basket contains flight with multiple passengers for a \"([^\"]*)\" bundle$")
    public void myBasketContainsFlightWithMultiplePassengersForABundle(String bundles) throws Throwable {
        expectedFare = bundles;
        FlightPassengers passengers = new FlightPassengers("1 Adult");
        FlightQueryParams flightQueryParams = FlightQueryParamsFactory.generateFlightSearchCriteria(passengers, testData.getOrigin(), testData.getDestination(), testData.getOutboundDate(), testData.getInboundDate(), null);
        flightQueryParams.setFareTypes(bundles);
        flightsService = serviceFactory.findFlight(new FlightsRequest(HybrisHeaders.getValid("Digital").build(), flightQueryParams));
        flightsService.invoke();
        basketHelper.addFlightToBasketAsChannelUsingFareCode(flightsService.getOutboundFlight("Flexi"), "Digital", "Flexi");
    }

    @Then("^I should see basketType as \"([^\"]*)\"$")
    public void iShouldSeeBasketTypeAs(String BasketType) throws Throwable {
        basketHelper.getBasketService().assertThat().basketType(BasketType);
    }

    @Then("^the deal is applied to basket$")
    public void theDealIsAppliedToBasket() throws Throwable {
        basketHelper.getBasketService().assertThat().basketTypeAsBusiness("BUSINESS");
        basketHelper.getBasketService().assertThat().bookingReasonAsBusiness("BUSINESS");
    }

    @Then("^the booking type value is \"([^\"]*)\"$")
    public void theBookingTypeValueIs(String dealType) throws Throwable {
        basketHelper.getBasketService().assertThat().basketTypeAsBusiness(dealType);
    }

    @Then("^the booking reason value is \"([^\"]*)\"$")
    public void theBookingReasonValueIs(String dealType) throws Throwable {
        basketHelper.getBasketService().assertThat().bookingReasonAsBusiness(dealType);
    }

    @Then("^Discount Tier and POS fee are applied at passenger level$")
    public void discountTierAndPOSFeeAreAppliedAtPassengerLevel() throws Throwable {
        basketHelper.getBasketService().assertThat().discountAndPOSFeeAppliedAtPassengerLevel();
    }

    @Then("^the basket total amount is calculated$")
    public void theBasketTotalAmountIsCalculated() throws Throwable {
        basketHelper.getBasketService().assertThat().appliedDiscountAndPOS();
    }

    @When("^My basket contains deal with passengerMix \"([^\"]*)\"$")
    public void myBasketContainsDealWithPassengerMix(String passengerMix) throws Throwable {
        basketHelper.myBasketContainsWithPassengerMixWithDeal("Digital", "ApplicationId,OfficeId,CorporateId", passengerMix);
    }

    @When("^My basket contains deal based on \"([^\"]*)\" and \"([^\"]*)\" and passengerMix \"([^\"]*)\"$")
    public void myBasketContainsDealBasedOnAndAndPassengerMix(String channel, String dealParameters, String passengerMix) throws Throwable {
        basketHelper.myBasketContainsWithPassengerMixWithDeal(channel, dealParameters, passengerMix);
    }

    @When("^My basket contains deal with PosFee and with passengerMix \"([^\"]*)\"$")
    public void myBasketContainsDealWithPosFeeAndWithPassengerMix(String passengerMix) throws Throwable {
        basketHelper.myBasketContainsWithPassengerMixWithDealWithPos("Digital", "ApplicationId,OfficeId,CorporateId", passengerMix);
    }

    @And("^the logged in customer is a staff customer  with credential \"([^\"]*)\" and \"([^\"]*)\"$")
    public void theLoggedInCustomerIsAStaffCustomerWithCredentialAnd(String user, String pwd) throws Throwable {
        customerHelper.loginWithValidCredentials(StringUtils.EMPTY, user, pwd, false);
    }

    @And("^the booking type is (.*)$")
    public void theBookingTypeIsStaff(String fare) throws Throwable {
        this.fareType = fare;
        testData.setFareType(fare);
    }

    @When("^I add the flight to the basket with passenger \"([^\"]*)\" using channel \"([^\"]*)\"$")
    public void iAddTheFlightToTheBasketWithPassengerUsingChannel(String mix, String channel) throws Throwable {
        basketHelper.myBasketContainsAFlightWithPassengerMixForStaff(mix, channel, "Staff");
    }

    @And("^the logged in customer is not a staff customer using channel \"([^\"]*)\"$")
    public void theLoggedInCustomerIsNotAStaffCustomerUsingChannel(String channel) throws Throwable {
        testData.setChannel(channel);
        customerHelper.createRandomCustomer(channel);
        customerHelper.createValidLoginRequest();
    }

    @Then("^I will return a error message \"([^\"]*)\" to the channel$")
    public void iWillReturnAErrorMessageToTheChannel(String error) throws Throwable {
        basketHelper.getBasketService().assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @When("^I add the flight with route \"([^\"]*)\" to the basket with passenger \"([^\"]*)\" using channel \"([^\"]*)\"$")
    public void iAddTheFlightWithRouteToTheBasketWithPassengerUsingChannel(String route, String mix, String channel) throws Throwable {
        testData.setOrigin(route.substring(0, 3));
        testData.setDestination(route.substring(3, 6));
        String flightKey = indirectFlightRoutesDao.getFlightKeyForRoute(route).get(0);
        basketHelper.addCustomFlightToTheBasket(flightKey, route, mix, channel, fareType);
    }

    @Then("^I should see the selected flight bundle is added per passenger$")
    public void iShouldSeeTheSelectedFlightBundleIsAddedPerPassenger() throws Throwable {
        basketHelper.getBasketService().assertThat().verifyStaffBundleForEachPassenger();
    }

    @And("^no Credit card, admin fees be applied$")
    public void noCreditCardAdminFeesBeApplied() throws Throwable {
        basketHelper.getBasketService().assertThat().noCCRFeeAndAdminFeeAreApliedForStaffBundle();
    }

    @And("^Flight Tax \"([^\"]*)\" is included in the Fare price per passenger if applicable route$")
    public void flightTaxIsIncludedInTheFarePricePerPassengerIfApplicableRoute(String tax) throws Throwable {
        basketHelper.getBasketService().assertThat().taxesAreApplied(tax);
    }

    @Given("^I have added a flight with \"([^\"]*)\" bundle to the basket$")
    public void iHaveAddedAFlightWithBundleToTheBasket(String bundle) throws Throwable {
        if (bundle.equalsIgnoreCase("staff") || bundle.equalsIgnoreCase("staffstandard") || bundle.equalsIgnoreCase("standby"))
            addFlight = addFlightToBasketAsStaff(bundle, "1 Adult");
        else
            addFlight = basketHelper.myBasketContainsAFlightWithPassengerMixAndBundle(defaultPassengerMix, defaultChannel, bundle);
        Scenario scenario = testData.getData(SCENARIO);
        if (!scenario.getSourceTagNames().contains("@negative")) {
            flightKey = addFlight.getFlights().get(0).getFlightKey();
            testData.setCurrency(addFlight.getCurrency());
        }
    }

    private AddFlightRequestBody addFlightToBasketAsStaff(String bundle, String adults) throws Throwable {
        return addStaffLTNCDGFlightToBasket(null, bundle, adults);
    }

    private AddFlightRequestBody addStaffLTNCDGFlightToBasket(String fk, String bundle, String numberOfAdults) throws Throwable {
//        if condition for different flights for different destination as some scenarios require this
        if (testData.getDestination().equalsIgnoreCase("CDG")) {
            testData.setDestination("ALC");
        } else {
            testData.setDestination("CDG");
        }
        flightsService = flightHelper.getFlights(testData.getChannel(), numberOfAdults, testData.getOrigin(), testData.getDestination(), "outbound");
        if (testData.getCurrency() == null) {
            testData.setCurrency(flightsService.getResponse().getCurrency());
        }
        addFlightRequestBodyFactory = new AddFlightRequestBodyFactory(serviceFactory, flightFinder);
        addFlightRequestBodyFactory = new AddFlightRequestBodyFactory(serviceFactory, flightFinder);
        AddFlightRequestBody addFlight = addFlightRequestBodyFactory.buildFlightRequestForStaff(flightsService.getOutboundFlight(), testData.getCurrency(), "Staff");
        flightKey = addFlight.getFlights().get(0).getFlightKey();


        if (bundle.equalsIgnoreCase("staff") || bundle.equalsIgnoreCase("staffstandard") || bundle.equalsIgnoreCase("standby")) {
            addFlight.setBookingType("STAFF");
        }
        addFlight.setFareType(bundle);

        if (fk != null) addFlight.getFlights().get(0).setFlightKey(fk);

        basketHelper.addFlightToBasketAsChannel(addFlight, testData.getChannel());

        return addFlight;
    }

    public FindFlightsResponse.Flight getSameFlight() {
        FindFlightsResponse.Flight flight = flightsService.getResponse().getOutbound().getJourneys().stream().flatMap(journey -> journey.getFlights().stream().filter(flight1 -> flight1.getFlightKey().equalsIgnoreCase(flightKey))).findAny().get();
        return flight;
    }

    @When("^I attempt to add the \"([^\"]*)\" flight with a \"([^\"]*)\" bundle$")
    public void iAttemptToAddTheFlightWithABundle(String newOrSameFlight, String bundle) throws Throwable {
        if (newOrSameFlight.equalsIgnoreCase("new")) iHaveAddedAFlightWithBundleToTheBasket(bundle);
        else if (newOrSameFlight.equalsIgnoreCase("same")) {
            String previousJSession = HybrisService.theJSessionCookie.get();
            addFlight.setFareType(bundle);
            basketHelper.addFlightToSameBasketAsChannel(addFlight, previousJSession, testData.getChannel());
        }
    }


    @Given("^I am using \"([^\"]*)\" to add flight to the basket$")
    public void iAmUsingToAddFlightToTheBasket(String channel) throws Throwable {
        channelUsed = channel;
        testData.setData(SerenityFacade.DataKeys.CHANNEL,channel);
    }

    @When("^I attempt to add \"([^\"]*)\"$")
    public void iAttemptToAdd(String pax) throws Throwable {
        addFlight = basketHelper.createAddFlightRequest(flight);
        basketHelper.addFlightToBasketAsChannel(addFlight, testData.getData(CHANNEL), pax);
    }

    @When("^I attempt to add \"([^\"]*)\" to the same basket$")
    public void iAttemptToAddToTheSameBasket(String pax) throws Throwable {
        basketHelper.addFlightToBasketAsChannel(addFlight,  testData.getData(CHANNEL), pax);
    }

    @Then("^I should see passengers successfully added$")
    public void iShouldSeePassengersSuccessfullyAdded() throws Throwable {
        basketHelper.getBasketService().assertThat();
    }

    @Given("^I logged in as a staff customer with credential \"([^\"]*)\" and \"([^\"]*)\"$")
    public void iLoggedInAsAStaffCustomerWithCredentialAnd(String user, String pwd) throws Throwable {
        customerHelper.loginWithValidCredentials(StringUtils.EMPTY, user, pwd, false);
    }

    @When("^I add the flight to the basket as staff with \"([^\"]*)\"$")
    public void iAddTheFlightToTheBasketAsStaffWithUsing(String mix) throws Throwable {
        addFlight = basketHelper.createAddFlightRequestForStaff(flight);
        basketHelper.addFlightToBasketAsChannel(addFlight,  testData.getData(CHANNEL), mix);
    }

    @When("^I add \"([^\"]*)\" to the basket as non-staff$")
    public void iAddToTheBasketAsNonStaff(String passengers) throws Throwable {
        basketHelper.myBasketContainsAFlightWithPassengerMix(passengers,  testData.getData(CHANNEL), "Standard",false);
    }

    @Given("^a flight exists for \"([^\"]*)\"$")
    public void aFlightExistsFor(String passengerMix) throws Throwable {
        testData.setPassengerMix(passengerMix);
        findFlight();
        flight = flightsService.getOutboundFlight();
    }

    private void findFlight() throws EasyjetCompromisedException {
        try {
            flightsService = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), testData.getFareType(), testData.getCurrency());
        } catch (Exception e) {
            throw new EasyjetCompromisedException("==== NO FLIGHTS FOUND ===");
        }
    }

    @When("^I add a flight for \"([^\"]*)\"$")
    public void iAddAFlightFor(String pax) throws Throwable {
        basketHelper.myBasketContainsAFlightWithPassengerMix(pax,  testData.getData(CHANNEL), "Standard", false);
    }

    @Given("^I am using \"([^\"]*)\" to search for flights \"([^\"]*)\"$")
    public void iAmUsingToSearchForFlights(String channel, String passengerMix) throws Throwable {
        testData.setData(SerenityFacade.DataKeys.CHANNEL,channel);
        findFlight();
        flight = flightsService.getOutboundFlight();
        testData.setData(GET_FLIGHT_SERVICE, flightsService);
    }

    @Then("^I will return a warning message \"([^\"]*)\" to the channel$")
    public void iWillReturnAWarningMessageToTheChannel(String warning) throws Throwable {
        basketHelper.getBasketService().assertThat().additionalInformationReturned(warning);
    }

    @Then("^I added it successfully$")
    public void iAddedItSuccessfully() throws Throwable {
        basketHelper.getBasketService().assertThat();
    }

    @And("^I have added a \"([^\"]*)\" bundle to the basket$")
    public void iHaveAddedABundleToTheBasket(String bundle) throws Throwable {
        basketHelper.addCustomFlightToTheBasket(flight.getFlightKey(), flight.getFlightKey().substring(8, 14), defaultPassengerMix, defaultChannel, bundle);
    }

    @And("^I have a flightKey$")
    public void iHaveAFlightKey() throws Throwable {
        iAmUsingToSearchForFlights(testData.getChannel(), defaultPassengerMix);
        testData.setActualFlightKey(flight.getFlightKey());
        testData.setActualFareType("Staff");
        FlightsService flightsService = testData.getData(GET_FLIGHT_SERVICE);
        testData.setActualCurrency(flightsService.getResponse().getCurrency());
    }

    @And("^I add (.*) flight to my basket with (.*),fare type as (.*) and booking type as (.*)$")
    public void iAddFlightToMyBasketWithFareTypeAsAndBookingTypeAs(String journeyType, String passengerMix, String faretype, String bookingtype) throws Throwable {
        FlightsService flightsService = testData.getData(GET_FLIGHT_SERVICE);
        basketHelper.createBasketWithPassengerMix(flightsService, journeyType, faretype, bookingtype, testData.getChannel(), false);
        testData.setData(GET_FLIGHT_SERVICE, null);
    }


    @And("^I add flight to my basket with fare type as (.*)$")
    public void iAddFlightToMyBasketWithFareTypeAs(String fareType) throws Throwable {
        FlightsService flightsService = testData.getData(GET_FLIGHT_SERVICE);
        basketHelper.createBasketWithPassengerMix(flightsService, testData.getJourneyType(), fareType, null, testData.getChannel(), false);
        testData.setData(GET_FLIGHT_SERVICE, null);
    }

    @Then("^the basket should store against the profile$")
    public void theBasketShouldStoreAgainstTheProfile() throws Throwable {
        String expectedCustomer = testData.getData(CUSTOMER_ID);
        String actualCustomer = cartdao.getCustomerForBasket(testData.getBasketId());
        Assert.assertTrue("Expected user is: " + expectedCustomer + ", but actual user is: " + actualCustomer, expectedCustomer.equals(actualCustomer));
    }

    @But("^not the personal details$")
    public void notThePersonalDetails() throws Throwable {
        Assert.assertTrue("API details still exist.", cartdao.getDocumentCountForBasket(testData.getBasketId()) == 0);
        Assert.assertTrue("Passenger information is still exists.", basicInfoIsNotStored(cartdao.getPassengerInfoForAllPassengersInBasket(testData.getBasketId())));
    }

    private boolean basicInfoIsNotStored(List<BasketPassengerBasicInfoModel> basicInfoForAllPassengers) {
        for (BasketPassengerBasicInfoModel basicInfoForAllPassenger : basicInfoForAllPassengers) {
            if (!(basicInfoForAllPassenger.getFirstName().equals("") &&
                    basicInfoForAllPassenger.getLastName().equals("") &&
                    basicInfoForAllPassenger.getEmail().equals("") &&
                    basicInfoForAllPassenger.getPhone().equals("") &&
                    basicInfoForAllPassenger.getAge() == 0 &&
                    basicInfoForAllPassenger.getEjNumber().equals("") &&
                    basicInfoForAllPassenger.getTitle() == null))
                return false;
        }
        return true;
    }

    @Given("^I created a basket as a logged in user$")
    public void iCreatedABasketAsALoggedInUser(Map<String, String> data) throws Throwable {
        createBasketAs(data, true);
        oldBasketId = testData.getBasketId();
    }

    @And("^I created a basket as an anonymous user$")
    public void iCreatedABasketAsAnAnonymousUser(Map<String, String> data) throws Throwable {
        createBasketAs(data, false);
        newBasketId = testData.getBasketId();
    }

    private void createBasketAs(Map<String, String> data, boolean loggedInUser) throws Throwable {
        testData.setPassengerMix(data.get("passengerMix"));
        testData.setJourneyType(data.get("journey"));
        testData.setOrigin(data.get("origin"));
        testData.setDestination(data.get("destination"));
        if (loggedInUser) {
            customerHelper.createRandomCustomer(testData.getChannel());
        }
        if (testData.getJourneyType().equalsIgnoreCase("return")) {
            basketHelper.addInboundOutboundFlights(testData.getPassengerMix(), "Standard");
        } else {
            basketHelper.myBasketContainsAFlightWithPassengerMix(testData.getPassengerMix(), testData.getChannel(), "Standard", false);
        }
        oldListOfPrices = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                .flatMap(bounds -> bounds.getFlights().stream())
                .flatMap(flight1 -> flight1.getPassengers().stream())
                .map(passenger -> passenger.getFareProduct().getPricing().getBasePrice())
                .collect(Collectors.toList());
    }

    @Then("^the new basket is rendered$")
    public void theNewBasketIsRendered() throws Throwable {
        basketHelper.getBasket(newBasketId, testData.getChannel());
        assertThat(basketHelper.getBasketService().getResponse().getBasket().getInbounds().size()).isEqualTo(0);
        assertThat(
                basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                        .flatMap(bounds -> bounds.getFlights().stream()
                                .filter(
                                        flight -> flight.getSector().getDeparture().getCode().equals(testData.getOrigin()) &&
                                                flight.getSector().getArrival().getCode().equalsIgnoreCase(testData.getDestination()))
                        ).collect(Collectors.toList())
                        .size()
        ).isEqualTo(1);
    }

    @And("^old basket is deleted$")
    public void oldBasketIsDeleted() throws Throwable {
        basketHelper.invokeGetBasket(oldBasketId, testData.getChannel());
        basketHelper.getBasketService().assertThatErrors().containedTheCorrectErrorMessage("SVC_100013_1001");

    }

    @And("^I change the outbound flight departure date to the past$")
    public void iChangeTheOutboundFlightDepartureDateToThePast() throws Throwable {
        flightsDao.updateTheDepartureDateForFlight(testData.getFlightKey(), getYesterdayDateString());
    }

    @And("^I change the outbound inbound flight departure date to the future$")
    public void iChangeTheOutboundInboundFlightDepartureDateToTheFuture() throws Throwable {
        testData.setOutboundDate(new com.hybris.easyjet.fixture.hybris.helpers.DateFormat().today().addDay(5));
        testData.setInboundDate(new com.hybris.easyjet.fixture.hybris.helpers.DateFormat().today().addDay(5));
    }

    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    private String getYesterdayDateString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(yesterday());
    }

    @Then("^the flight is removed$")
    public void theFlightIsRemoved() throws Throwable {
        basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        assertThat(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().size()).isEqualTo(0);
    }

    @Then("^the whole journey is removed$")
    public void theWholeJourneyIsRemoved() throws Throwable {
        basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        assertThat(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().size()).isEqualTo(0);
        assertThat(basketHelper.getBasketService().getResponse().getBasket().getInbounds().size()).isEqualTo(0);
    }

    @Then("^the seat is removed$")
    public void theSeatIsRemoved() throws Throwable {
        basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        assertThat(basketHelper.getBasketService().assertThat().noSeatsArePurchasedForAnyPassengerInTheBasket());
    }

    @Then("^the basket should update with the new price$")
    public void theBasketShouldUpdateWithTheNewPrice() throws Throwable {
        basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        newListOfPrices = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                .flatMap(bounds -> bounds.getFlights().stream())
                .flatMap(flight1 -> flight1.getPassengers().stream())
                .map(passenger -> passenger.getFareProduct().getPricing().getBasePrice())
                .collect(Collectors.toList());
        assertThat(newListOfPrices.
                equals(oldListOfPrices.stream().map(price -> price - 1.0).collect(Collectors.toList()))).isTrue();
    }

    @Then("^the hold bag should be removed from the basket$")
    public void theHoldBagShouldBeRemovedFromTheBasket() throws Throwable {
        basketHelper.getBasket(oldBasketId, testData.getChannel());
        assertThat(
                basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                        .flatMap(bounds -> bounds.getFlights().stream()
                                .flatMap(flight -> flight.getPassengers().stream()
                                        .filter(passenger -> passenger.getHoldItems().size() != 0
                                        )
                                )
                        )
                        .collect(Collectors.toList())
                        .size()
        ).isEqualTo(0);
        holdItemsDao.updateTheStock(testData.getFlightKey(), "20kgbag", "0");
    }

    @When("^I add the flight to the basket with passenger (.*)$")
    public void iAddTheFlightToTheBasketWithPassenger(String passengerMix) throws Throwable {
        flightsService = flightHelper.getFlights(testData.getChannel(), passengerMix, testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate());
        basketHelper.addReturnFlightWithTaxToBasketAsChannelJourneyType(flightsService, passengerMix, testData.getCurrency(), testData.getChannel(), "Standard", "SINGLE");
    }

    @When("^add flight to the basket with passenger \"([^\"]*)\"$")
    public void addFlightToTheBasketWithPassengerUsingChannel(String passengerMix) throws Throwable {
        basketHelper.addInboundOutboundFlights(passengerMix, "Standard");
    }

    @And("^add flight to the basket with passenger \"([^\"]*)\" with \"([^\"]*)\"$")
    public void addFlightToTheBasketWithPassengerWith(String passengerMix, String fare) throws Throwable {
        basketHelper.myBasketContainsAFlightWithPassengerMix(passengerMix, testData.getChannel(), fare, false);
    }

    @When("^I add the flight to the basket with (.*) with currency (.*)$")
    public void iAddTheFlightToTheBasketWithPassengersWithCurrencyCurrency(String passengers, String currency) throws Throwable {
        testData.setCurrency(currency);
        flightsService = flightHelper.getFlights(testData.getChannel(), passengers, testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate());
        basketHelper.addReturnFlightWithTaxToBasketAsChannelJourneyType(flightsService, passengers, currency, testData.getChannel(), "Standard", "RETURN");
    }

    @When("^I add the flight to the basket with (.*)  with booking type (.*)$")
    public void iAddTheFlightToTheBasketWithPassengersWithBookingType(String passenger, String bookingType) throws Throwable {
        try {
            flightsService = flightHelper.getFlights(testData.getChannel(), passenger, testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate());
            basketHelper.addReturnFlightWithTaxToBasketAsChannelJourneyType(flightsService, passenger, testData.getCurrency(), testData.getChannel(), "Standard", "RETURN");
        } catch (AssertionError error) {
            handelError(error);
        }
    }

    private void handelError(AssertionError error) throws EasyjetCompromisedException {
        if (error.getMessage().contains("SVC_100012_20017") || error.getMessage().contains("SVC_100012_3036")) {
            throw new EasyjetCompromisedException(EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INSUFFICIENT_DATA);
        } else {
            Assert.fail(error.getMessage());
        }
    }

    @And("^add flight to the basket with passenger (.*) with (.*) bla$")
    public void addFlightToTheBasketWithPassengerWithBla(String passengerMix, String fare) throws Throwable {
        bookingHelper.createBookingWithMultipleFlightAndGetAmendable(passengerMix, fareType, 1);

    }

    @And("^my basket contains flights with departure date (\\d+) days ahead with passengerMix \"([^\"]*)\"$")
    public void myBasketContainsFlightsWithDepartureDateDaysAheadWithPassengerMix(int Days,String passengerMix) throws Throwable {

        testData.setOutboundDate(new com.hybris.easyjet.fixture.hybris.helpers.DateFormat().today().addDay(Days));
        testData.setInboundDate(new com.hybris.easyjet.fixture.hybris.helpers.DateFormat().today().addDay(Days));

        try {
            flightsService = flightHelper.getFlights(testData.getChannel(), passengerMix, testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate());
            basketHelper.addReturnFlightWithTaxToBasketAsChannelJourneyType(flightsService, passengerMix, testData.getCurrency(), testData.getChannel(), "Standard", "SINGLE");
        } catch (AssertionError error) {
            handelError(error);
        }

        String flightKey = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                                    .flatMap(p -> p.getFlights().stream())
                                    .findFirst()
                                    .get().getFlightKey();
        testData.setData(FLIGHT_KEY,flightKey);
    }
}