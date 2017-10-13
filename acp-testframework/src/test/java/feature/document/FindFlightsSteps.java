package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.database.FlightFinder;
import com.hybris.easyjet.database.hybris.dao.DealDao;
import com.hybris.easyjet.database.hybris.dao.FeesAndTaxesDao;
import com.hybris.easyjet.database.hybris.dao.FlightInterestDao;
import com.hybris.easyjet.database.hybris.dao.PropertyValueConfigurationDao;
import com.hybris.easyjet.database.hybris.models.ChannelPropertiesModel;
import com.hybris.easyjet.database.hybris.models.DealModel;
import com.hybris.easyjet.database.hybris.models.HybrisFlightDbModel;
import com.hybris.easyjet.database.hybris.models.ItemModel;
import com.hybris.easyjet.fixture.hybris.helpers.*;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.FlightQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.factory.FlightQueryParamsFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.FlightsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by daniel on 21/09/2016.
 */
@ContextConfiguration(classes = TestApplication.class)

public class FindFlightsSteps {

    protected static Logger LOG = LogManager.getLogger(FindFlightsSteps.class);
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private FlightHelper flightHelper;
    @Autowired
    private FlightFinder flightFinder;
    @Autowired
    private DealDao dealdao;
    @Autowired
    private ChannelPropertiesHelper channelPropertiesHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private StaffMembertoCustomerProfileAssociationHelper staffHelper;
    @Autowired
    private FeesAndTaxesDao feesAndTaxesDao;
    @Autowired
    private FlightInterestDao flightInterestDao;
    @Autowired
    private PropertyValueConfigurationDao propertyValueConfigurationDao;
    private String channel;
    private List<DealModel> deals;
    private HybrisFlightDbModel availableFlight;
    private FlightsService flightService;
    private FlightQueryParams flightQueryParams;
    private long hours;
    private String origin = "LTN";
    private String destination = "ALC";
    private String flexiDays = "4";
    private List<ItemModel> discounts;
    FlightQueryParams params;
    @Steps
    private CommonSteps commonSteps;


    @Given("^a flight exists with \"([^\"]*)\" CheckInWindow closed for (.*)$")
    public void aFlightExistsWithCheckInWindowClosed(String CheckInWindow, String PassengerMix, Map<String, String> data) throws Throwable {

        DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date today = Calendar.getInstance().getTime();
        String date = sdf.format(today);
        testData.setPassengerMix(PassengerMix);
        testData.setOrigin(data.get("origin"));
        testData.setDestination(data.get("destination"));
        FlightsService flightsService = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), null, date, null);
        testData.setData(GET_FLIGHT_SERVICE, flightsService);

        if (CheckInWindow.equalsIgnoreCase("Airport")) {
            hours = flightInterestDao.getAirportGateClosureCheckinTime(testData.getOrigin());
        } else if (CheckInWindow.equalsIgnoreCase("Online")) {
            hours = flightInterestDao.getOnlineClosureCheckinTime(testData.getOrigin());
        }

    }

    @And("^I am using channel (.*)$")
    public void iAmUsingChannelChannel(String channel) throws Throwable {
        this.channel = channel.trim();
        testData.setChannel(channel);
        testData.setData(CHANNEL, channel);
        if (channel.trim().equals(CommonConstants.AD_CHANNEL) || channel.trim().equals(CommonConstants.AD_CUSTOMER_SERVICE)) {
            commonSteps.iLoginAsAgentWithUsernameAndPassword("rachel", "12341234");
            customerHelper.createRandomCustomer(channel);
            customerHelper.getCustomerProfile(testData.getData(CUSTOMER_ID));
        }
    }

    @Then("^flight is returned$")
    public void flightIsReturned() throws Throwable {
        flightService.assertThat().atLeastOneOutboundFlightWasReturned();
    }

    @Given("^the maximum number of passengers is (.*)$")
    public void theMaximumNumberOfPassengersIs(String numberOfPassengers) throws Throwable {

        List<ChannelPropertiesModel> properties = channelPropertiesHelper.getChannelProperties("Digital");
        for (ChannelPropertiesModel model : properties) {
            if (model.getP_propertyname().equals("maxPassengers")) {
                assertThat(model.getP_propertyvalue()).isEqualTo(numberOfPassengers);
            }
        }

    }

    @When("^I search for flight with \"([^\"]*)\"$")
    public void iSearchForFlightWith(String params) throws Throwable {

        FlightQueryParams queryParams = FlightQueryParamsFactory.InvalidFlightParams(flightFinder, params);
        flightService = serviceFactory.findFlight(new FlightsRequest(HybrisHeaders.getValid("Digital")
                .build(), queryParams));
        flightService.invoke();
    }

    @When("^I search for flight with \"([^\"]*)\" exceeding maximum$")
    public void iSearchForFlightWithExceedingMaximum(String passengerMix) throws Throwable {
        flightService = flightHelper.getFlights(testData.getChannel(), passengerMix, testData.getOrigin(), testData.getDestination(), "outbound");
    }

    @Then("^multiple error messages returned$")
    public void multipleErrorMessagesReturned() throws Throwable {
        flightService.assertThatErrors().containedTheCorrectErrorMessage("SVC_100148_3001", "SVC_100148_3005");
    }

    @Then("^the flight has a flight key$")
    public void theFlightHasAFlightKey() throws Throwable {
        flightService.assertThat().atLeastOneOutboundFlightWasReturned().theFlightHasAFlightKey();
    }

    @When("^I call the find flights service for a valid flight via \"([^\"]*)\"$")
    public void findAValidFlight(String channel) throws Throwable {
        testData.setData(CHANNEL, channel);
        flightService = flightHelper.getFlights(testData.getData(CHANNEL), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), testData.getFareType(), testData.getCurrency());
    }

    @Then("^\"([^\"]*)\" error is returned$")
    public void errorIsReturned(String error) throws Throwable {
        flightService.assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @When("^I call the find flights service with deal headers (.*), (.*), (.*) via (.*)$")
    public void iCallTheFindFlightsServiceWithDealHeaders(String App, String Off, String Corp, String channel) throws Throwable {
        String system = null;
        String office = null;
        String corporate = null;
        if (App.equals("Valid")) {
            system = deals.get(0).getSystemName();
        } else if (App.equals("Invalid")) system = "invalid";
        if (Off.equals("Valid")) {
            office = deals.get(0).getOfficeId();
        } else if (Off.equals("Invalid")) office = "invalid";
        if (Corp.equals("Valid")) {
            corporate = deals.get(0).getCorporateId();
        } else if (Corp.equals("Invalid")) corporate = "invalid";
        testData.setData(CHANNEL, channel);
        flightService = flightHelper.getFlights(testData.getData(CHANNEL), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), testData.getOutboundDate(), testData.getFareType(), "100", system, office, corporate);
    }

    @And("^I (should|should not) see discounts for flight search$")
    public void iShouldSeeDiscounts(String should_or_shouldnot) throws Throwable {
        discounts = dealdao.getDiscount(deals.get(0).getDiscounts(), flightService.getResponse()
                .getCurrency());
        if (discounts.size() != 0) {
            flightService.assertThat().discountsreturned(should_or_shouldnot, discounts.get(0));
        }
    }

    @And("^I (should|should not) see POS Fees for flight search$")
    public void iShouldSeePOSFees(String should_or_shouldnot) throws Throwable {

        List<ItemModel> fees = dealdao.getFee(deals.get(0).getPosFees(), flightService.getResponse().getCurrency());
        if (fees.size() != 0) {
            flightService.assertThat().posFeereturned(should_or_shouldnot, fees.get(0));
        }
    }

    @And("^I (should|should not) see total discounts for flight search$")
    public void iShouldSeeTotalDiscounts(String should_or_shouldnot) throws Throwable {
        flightService.assertThat().totalDiscounts(should_or_shouldnot, discounts.get(0));
    }

    @Then("^flights deal (.*) should be returned$")
    public void flightsDealWarningMessageShouldBeReturned(String warningMessage) throws Throwable {
        flightService.assertThat().additionalInformationReturned(warningMessage);
    }

    @And("^I should see total fare with discounts, POS fee$")
    public void iShouldSeeTotalFareWithDiscountsPOSFee() throws Throwable {
        flightService.assertThat().augmentedprice();
    }

    @Given("^I found a deal with (.*), (.*), (.*)$")
    public void iFoundADealWithApplicationIdOfficeIdCorpId(String App, String Off, String Corp) throws Throwable {
        deals = dealdao.getDeals(true, true, true);
    }

    @Given("^I call the flight search with \"([^\"]*)\"$")
    public void iCallTheFlightSearchWithInfantOnOwnSeats(String passengerMix) throws Throwable {
        testData.setData(PASSENGER_MIX, passengerMix);
        flightService = flightHelper.getFlights(testData.getData(CHANNEL), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), testData.getFareType(), testData.getCurrency());
    }

    @Given("^I call flight search with passenger mix (.*) and flexible days outside max range")
    public void flightSearch(String passengerMix) throws Throwable {
        int maxSearchResultForFlight = Integer.valueOf(propertyValueConfigurationDao.getPropertyValueBasedOnName(testData.getChannel(), "maxFlightSearchDayRange"));
        testData.setData(FLEXIBLE_DAYS, String.valueOf(maxSearchResultForFlight + 1));
        testData.setData(PASSENGER_MIX, passengerMix);
        flightService = flightHelper.getFlights(testData.getData(CHANNEL), testData.getData(PASSENGER_MIX), testData.getOrigin(), testData.getDestination(), testData.getFareType(), testData.getCurrency());

        flexiDays = String.valueOf(maxSearchResultForFlight);
    }

    @Given("^a valid flight exists with maximum number of infants on own seat \"([^\"]*)\"$")
    public void aValidFlightExistsWithMaximumNumberOfInfantsOnOwnSeatInfantsOnOwnSeat(String passengerMix) throws Throwable {
        flightService = flightHelper.getFlights(testData.getChannel(), passengerMix, testData.getOrigin(), testData.getDestination(), "outbound");
    }

    @Then("^flight is returned with \"([^\"]*)\"$")
    public void flightIsReturnedWith(String passengerMix) throws Throwable {
        flightService.assertThat().infantIsOnOwnSeat(passengerMix);
    }

    @Given("^there is a valid flight for (.*)$")
    public void thereIsAValidFlightForPassengerMix(String passengerMix) throws Throwable {
        testData.setPassengerMix(passengerMix);
        flightService = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), testData.getFareType(), testData.getCurrency());
    }

    @When("^I send the getFlight request$")
    public void iSendTheGetFlightRequestForChannel() throws Throwable {
        flightService = serviceFactory.findFlight(new FlightsRequest(HybrisHeaders.getValid(channel)
                .build(), flightQueryParams));
        flightService.invoke();
    }

    @And("^the availableStatus should be Unavailable$")
    public void theAvailableStatusShouldBeUnavailable() throws Throwable {
        FlightsService flightsService = testData.getData(GET_FLIGHT_SERVICE);
        flightsService.assertThat().unavailableStatus(hours);
    }

    @And("^I am going to send a request for staff member$")
    public void iAmGoingToSendARequestForStaffMember() throws Throwable {
        FlightPassengers passengers = new FlightPassengers("1 Adult");
        flightQueryParams = FlightQueryParamsFactory.generateFlightSearchCriteria(passengers, origin, destination, testData.getOutboundDate(), testData.getInboundDate(), null);
        flightQueryParams.setStaffBooking("true");
        flightQueryParams.setAdult("1");
        availableFlight = flightFinder.findAValidFlight(1);
    }

    @When("^I send a getFlight request for (.*) through (.*)$")
    public void iSendAGetFlightRequestForPassengerMix(String passengerMix, String channel) throws Throwable {
        testData.setPassengerMix(passengerMix);
        testData.setChannel(channel);
        flightService = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), testData.getFareType(), testData.getCurrency());
    }

    @And("^I see response includes additional seats for the requested (.*)$")
    public void responseToIncludeSeats(String passengerMix) throws Throwable {
        flightService.assertThat().passengerMixIsAddedWithAdditionalSeat(passengerMix);
    }

    @And("^fees and taxes will be applied for (.*) including credit card fee$")
    public void feesAndTaxesWillBeAppliedForPassengerMixIncludingCreditCardFee(String passengerMix) throws Throwable {
        flightService.assertThat().feesAndTaxesAreAppliedToTheAdditionalSeat(passengerMix);
    }

    @And("^I am not logged in$")
    public void iAmNotLoggedIn() throws Throwable {
        // Do nothing: by default the user is not logged in
    }

    @Then("^I will return a (SVC_\\d+_\\d+) error message to the channel$")
    public void iWillReturnASVC__ErrorMessageToTheChannel(String errorCode) throws Throwable {

        flightService.assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }

    @And("^I am logged in as a standard customer$")
    public void iAmLoggedInAsAStandardCustomer() throws Throwable {
        customerHelper.createRandomCustomer(testData.getChannel());
    }

    @And("^I am logged in as a new customer$")
    public void iAmLoggedInAsANewCustomer() throws Throwable {
        iAmLoggedInAsAStandardCustomer();
    }

    @And("^I am logged in as a staff member$")
    public void iAmLoggedInAsAStaffMember() throws Throwable {
        customerHelper.createRandomCustomer(testData.getChannel());
        staffHelper.associateCustomerProfileWithStaffMemberFromRequest(testData.getChannel(), false);
    }

    @And("^request contains only a child passenger type$")
    public void theRequestContainsOnlyAChildPassengerType() throws Throwable {
        FlightPassengers passengers = new FlightPassengers("1 child");
        flightQueryParams = FlightQueryParamsFactory.generateFlightSearchCriteria(passengers, origin, destination, testData.getOutboundDate(), testData.getInboundDate(), null);
        flightQueryParams.setStaffBooking("true");
    }

    @And("^request contains more than (\\d+) passengers excluding infant on lap$")
    public void theRequestContainsMoreThanPassengersExcludingInfantOnLap(int passengers) throws Throwable {
        FlightPassengers passenger = new FlightPassengers(String.valueOf(passengers + 1) + " adult");
        flightQueryParams = FlightQueryParamsFactory.generateFlightSearchCriteria(passenger, origin, destination, testData.getOutboundDate(), testData.getInboundDate(), null);
        //getFlightParams.adult(String.valueOf(passengers + 1));
    }

    @Then("^I will not see the standby bundles in the results$")
    public void iWillNotSeeTheStandbyBundlesInTheResults() throws Throwable {

        flightService.assertThat().standbyBundlesAreNotDisplayed();
    }

    @Then("^I will return bundles for the channel$")
    public void iWillReturnBundlesForTheChannel() throws Throwable {
        flightService.assertThat().staffBundlesAreReturned();
    }

    @And("^no Credit card or Admin fees are applied$")
    public void noCreditCardOrAdminFeesAreApplied() throws Throwable {
        flightService.assertThat().staffBundlesDontHaveAdminFeeOrCRFee();
    }

    @And("^relevant taxes are applied$")
    public void relevantTaxesAreApplied() throws Throwable {

        HashMap<String, Double> taxes = feesAndTaxesDao.getTaxesForPassenger(availableFlight.getDeparts()
                .concat(availableFlight.getArrives()), flightService.getResponse().getCurrency(), "adult");
        flightService.assertThat().staffBundlesIncludeTaxes(taxes);
    }

    @Given("^I call the flight search with (.*) and flexible days (.*)$")
    public void iCallTheFlightSearchWithPaxAndFlexibleDay(String passengerMix, String flexibleDays) throws Exception {
        testData.setData(PASSENGER_MIX, passengerMix);
        testData.setData(FLEXIBLE_DAYS, flexibleDays);
        flightService = flightHelper.getFlights(testData.getData(CHANNEL), testData.getData(PASSENGER_MIX), testData.getOrigin(), testData.getDestination(), testData.getFareType(), testData.getCurrency());
    }

    @Then("^I get a warning message with (.*)$")
    public void iGetAWarningMessageWithWarning(String warning) {
        flightService.assertThat().additionalInformationReturned(warning);
    }

    @Then("^I will get flight search result within the plus and minus range of flexible days from the travel date$")
    public void iWillGetFlightSearchResultWithinThePlusAndMinusRangeOfFlexiDaysFromTheOutboudDate() throws ParseException {
        flightService.assertThat().allFlightsReturnWithinRange(testData.getData(OUTBOUND_DATE).toString(), testData.getData(INBOUND_DATE).toString(), flexiDays);
    }

    @And("^the search result are not outside the range of (.*)$")
    public void theSearchResultAreNotOutsideTheRangeOfFlexiDays(String flexibleDays) throws ParseException {
        flightService.assertThat().allFlightsReturnOutSideRange(testData.getData(OUTBOUND_DATE).toString(), testData.getData(INBOUND_DATE).toString(), flexibleDays);
    }

    @And("^travelling from (.*) to (.*)$")
    public void travellingFromTo(String origin, String destination) throws Throwable {
        testData.setOrigin(origin);
        testData.setDestination(destination);
    }
}