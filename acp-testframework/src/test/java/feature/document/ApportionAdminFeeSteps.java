package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.FeesAndTaxesDao;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.FlightHelper;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BUNDLE;

/**
 * Created by AndyGr on 12/20/2016.
 */
@ContextConfiguration(classes = TestApplication.class)

public class ApportionAdminFeeSteps {

    private static Logger LOG = LogManager.getLogger(ApportionAdminFeeSteps.class);
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private FeesAndTaxesDao feesAndTaxesDao;
    @Autowired
    private FlightHelper flightHelper;
    private FlightsService flightsService;
    private FindFlightsResponse.Flight flight;


    @Then("^the admin fee should be apportioned per passenger and rounded to the nearest pence for the first two sectors only$")
    public void theAdminFeeShouldBeApportionedPerPassengerAndRoundedToTheNearestPenceForTheFirstTwoSectors() throws Throwable {
        basketHelper.getBasketService().assertThat().theAdminFeeShouldBeApportionedPerPassengerAndRoundedToTheNearestPenceForTheFirstTwoSectors(feesAndTaxesDao);
    }

    @Then("^the admin fee should be apportioned per passenger per sector and wrapped up in the flight fare of the first flight Only$")
    public void theAdminFeeShouldBeApportionedPerPassengerPerSectorAndWrappedUpInTheFlightFare() throws Throwable {
        basketHelper.getBasketService().assertThat().theAdministrationFeeDividedAcrossPassengersandOfTheFirstFlightOnly(feesAndTaxesDao);
    }

    @Given("^my basket contains return flight that has a flight tax for \"([^\"]*)\" passengers added via the \"([^\"]*)\" channel$")
    public void myBasketContainsReturnFlightwithTaxForPassengersAddedViaTheChannel(int numberOfPassengers, String channel) throws Throwable {
        basketHelper.addReturnFlightWithTaxToBasketAsChannel(numberOfPassengers, channel, true, null);
    }

    @When("^I add another \"([^\"]*)\" flight that has a flight tax with \"([^\"]*)\" passengers to the basket via the \"([^\"]*)\" channel$")
    public void iAddAnotherFlightWithTaxWithPassengersToTheBasketViaTheChannel(int numberOfFlights, int numberOfPassengers, String channel) throws Throwable {
        basketHelper.addFlightToBasketAsChannel(numberOfFlights, numberOfPassengers, channel, true, null);
    }

    @Given("^my basket contains \"([^\"]*)\" flights for \"([^\"]*)\" passengers with \"([^\"]*)\" fare added via the \"([^\"]*)\" channel$")
    public void myBasketContainsFlightsForPassengersAddedViaTheChannel(int numberOfFlights, int numberOfPassengers, String bundles, String channel) throws Throwable {
        basketHelper.myBasketContainsManyFlightWithPassengerMix(numberOfFlights, numberOfPassengers + " adult", testData.getChannel(), bundles, "single");
    }

    @Given("^my basket contains a flight with \"([^\"]*)\" fare added via the \"([^\"]*)\" channel$")
    public void myBasketContainsaFlightForBundleAddedViaTheChannel(String bundles, String channel) throws Throwable {
        testData.setChannel(channel.trim());
        testData.setData(BUNDLE, bundles);
        flightsService = flightHelper.getFlights(testData.getChannel(), "1 Adult", testData.getOrigin(), testData.getDestination(), bundles, testData.getCurrency());
        testData.setOutboundFlight(flightsService.getOutboundFlight());
        basketHelper.addFlightToBasketAsChannel(testData.getOutboundFlight());
    }

    @When("^I add the \"([^\"]*)\" flights with \"([^\"]*)\" passengers with \"([^\"]*)\" bundle to my basket via \"([^\"]*)\"$")
    public void iAddTheFlightsWithPassengersToMyBasketVia(int numberOfFlights, int numberOfPassengers, String bundles, String channel) throws Throwable {
        basketHelper.myBasketContainsManyFlightWithPassengerMix(numberOfFlights, numberOfPassengers + " adult", testData.getChannel(), bundles, "single");
    }

    @Then("^the credit card fee for each passenger is correct for the \"([^\"]*)\"$")
    public void theCreditCardFeeForEachPassengerIsCorrectForChannel(String channel) throws Throwable {
        basketHelper.getBasketService().assertThat().theCreditCardFeeForEachPassengerIsCorrect(feesAndTaxesDao);
    }

    @And("^the administration tax is at booking level$")
    public void theAdministrationTaxIsAtBookingLevel() throws Throwable {
        basketHelper.getBasketService().assertThat().theAdministrationTaxIsAtBookingLevel(feesAndTaxesDao);
    }

    @Then("^the Flight Tax should be in the Fare Price per passenger$")
    public void theFlightTaxShouldBeInTheFarePricePerPassenger() throws Throwable {
        basketHelper.getBasketService().assertThat().theFlightTaxIsAtPassengerLevel(feesAndTaxesDao);
    }

    @Given("^my basket contains return flight for \"([^\"]*)\" passengers with \"([^\"]*)\" fare added via the \"([^\"]*)\" channel$")
    public void myBasketContainsReturnFlightForPassengersAddedViaTheChannel(int numberOfPassengers, String bundles, String aChannel) throws Throwable {
        testData.setChannel(aChannel);
        basketHelper.addReturnFlightWithTaxToBasketAsChannel(numberOfPassengers, testData.getChannel(), false, bundles);
    }

    @When("^I add another \"([^\"]*)\" flight with \"([^\"]*)\" passengers to the basket via the \"([^\"]*)\" channel$")
    public void iAddAnotherFlightWithPassengersToTheBasketViaTheChannel(int numberOfFlights, int numberOfPassengers, String channel) throws Throwable {
        basketHelper.addFlightToBasketAsChannel(numberOfFlights, numberOfPassengers, channel, false, null);
    }

}
