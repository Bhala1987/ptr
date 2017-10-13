package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.FlightFinder;
import com.hybris.easyjet.database.hybris.dao.PropertyValueConfigurationDao;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.FlightHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.AddFlightRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by AndrewGr on 09/11/2016.
 */
@ContextConfiguration(classes = TestApplication.class)

public class AddFlightToBasketValidationSteps {

    protected static Logger LOG = LogManager.getLogger(AddFlightToBasketValidationSteps.class);
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private FlightFinder flightFinder;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private FlightHelper flightHelper;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private PropertyValueConfigurationDao propertyValueConfigurationDao;
    private FindFlightsResponse.Flight flight;
    private AddFlightRequestBody addFlight;
    private FlightsService flightsService;

    @And("^I have added (\\d+) flights to my basket$")
    public void iHaveAddedFlightsToMyBasket(int numberOfFlights) throws Throwable {
        basketHelper.addNumberOfFlightsToBasketForDigital(numberOfFlights);

    }

    @When("^I add the flight to my basket$")
    public void iAddTheFlightToMyBasket() throws Throwable {
        basketHelper.myBasketContainsAFlightWithPassengerMix("1 Adult");
    }

    @Given("^I have a valid flight that exceeds the infant on own seat limit$")
    public void iHaveAValidFlightThatExceedsTheInfantOnOwnSeatLimit() throws Throwable {
        String passengerMix = "2 Adult, 2 Infant OOS";
        findFlights(passengerMix);
        flight = flightsService.getOutboundFlight();
        int maxSearchResultForFlight = Integer.valueOf(propertyValueConfigurationDao.getPropertyValueBasedOnName(testData.getChannel(), "adultInfantOwnSeatRatio"));
        flight = flightHelper.updateInfantQuantity(maxSearchResultForFlight + 3, flight, true);

    }
    @Given("^I have a valid flight that exceeds the infant on own seat limit for flight$")
    public void iHaveAValidFlightThatExceedsTheInfantOnOwnSeatLimitForFlights() throws Throwable {
        int maxSearchResultForFlight = Integer.valueOf(propertyValueConfigurationDao.getPropertyValueBasedOnName(testData.getChannel(), "maxInfantsOnBooking"));
        String passengerMix = maxSearchResultForFlight + " Adult," + maxSearchResultForFlight + " Infant OOS";
        findFlights(passengerMix);
        flight = flightsService.getOutboundFlight();
        flight = flightHelper.updateInfantQuantity(maxSearchResultForFlight + 2, flight, true);
    }

    @When("^I try to add the flight to my basket$")
    public void iTryToAddTheFlightToMyBasket() throws Throwable {
        basketHelper.addAFlightToMyBasket(flight,testData.getChannel(),false);
    }
    @When("^I try to add the flight to my basket with override warning as (.*)$")
    public void iTryToAddTheFlightToMyBasket(boolean isOverrideWarning) throws Throwable {
        basketHelper.addAFlightToMyBasket(flight,testData.getChannel(),isOverrideWarning);
    }

    @Then("^the \"([^\"]*)\" error should be returned$")
    public void theErrorShouldBeReturned(String parameter) throws Throwable {
        basketHelper.getBasketService().assertThatErrors().containedTheCorrectErrorMessage(parameter);
    }

    @Given("^I have a flight fare that has different price now to what it was when first received$")
    public void iHaveAFlightFareThatHasDifferentPriceNowToWhatItWasWhenFirstReceived() throws Throwable {
        findFlights("1 adult");
        flight = flightsService.getOutboundFlight();
        flight.getFareTypes().get(0).getPassengers().get(0).setBasePrice(299.13);
    }

    private void findFlights(String passenger) throws Throwable {
        flightsService = flightHelper.getFlights(testData.getChannel(), passenger, testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate());
    }

    @Then("^the \"([^\"]*)\" warning should be returned$")
    public void theWarningShouldBeReturned(String error) throws Throwable {
        if (error.equals("seatmap fare"))
            basketHelper.getBasketService().assertThat().additionalInformationReturned("SVC_100012_3008");
        else if (error.equals("cancelled seatmap"))
            basketHelper.getBasketService().assertThat().additionalInformationReturned("SVC_100012_3008");
    }

    @And("^the flight is added to the basket$")
    public void theFlightIsAddedToTheBasket() throws Throwable {
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());
        basketHelper.getBasketService().assertThat().theBasketContainsTheFlight(flight);
    }

    @When("^I attempt to add the flight to my basket$")
    public void iAttemptToAddTheFlightToMyBasket() throws Throwable {
        basketHelper.attemptToAddFlightToBasket(HybrisHeaders.getValid("Digital").build(), addFlight);
    }

    @Given("^I have a flight request with (invalid|missing) \"([^\"]*)\"$")
    public void iHaveAFlightRequestWithInvalid(String criteria, String fieldKey) throws Throwable {
        findFlights("1 adult");
        flight = flightsService.getOutboundFlight();
        if (criteria.equalsIgnoreCase("missing")) {
            addFlight = flightHelper.setFieldAsMissing(fieldKey, basketHelper.createAddFlightRequest(flight));
        } else {
            addFlight = FlightHelper.setFieldAsInvalid(fieldKey, basketHelper.createAddFlightRequest(flight));
        }
    }

    @Given("^a valid flight exists with ([^\"]*) seats available$")
    public void aValidFlightExistsWithSeatsAvailable(String passengerMix) throws Throwable {
        findFlights(passengerMix);
        flight = flightsService.getOutboundFlight();
    }

    @When("^I try to add the flight to my basket with the passenger mix ([^\"]*)$")
    public void iTryToAddTheFlightToMyBasketWithThePasengerMix(String passengerMix) throws Throwable {
        flight = flightHelper.updateInfantQuantity(2, flight, false);
        addFlight = basketHelper.createAddFlightRequest(flight);
        basketHelper.attemptToAddFlightToBasket(HybrisHeaders.getValid(testData.getChannel()).build(), addFlight);
    }


    @Given("^\"([^\"]*)\" sends an invalid add to basket request$")
    public void sendsAnInvalidAddToBasketRequest(String channel) throws Throwable {
        iHaveAFlightRequestWithInvalid("missing", "flightKey");
        basketHelper.addFlightToBasketAsChannel(addFlight, channel);
    }

    @Then("^an error is returned to the channel$")
    public void anErrorIsReturnedToTheChannel() throws Throwable {
        theErrorShouldBeReturned("SVC_100012_2002");
    }
    @When("^I add flight to basket with different price and (.*)$")
    public void iAddFlightToBasketWithDifferentPrice(String passengerMix) throws Throwable {
        findFlights(passengerMix);
        flight = flightsService.getOutboundFlight();
        basketHelper.addAFlightToMyBasketWithDifferentPrice(flight,testData.getChannel(),false);
    }
    @Then("^I should get the waring with message as (.*)$")
    public void iShouldGetTheWaringWithCodeAsSVC(String code) throws Throwable {
        basketHelper.getBasketService().assertThat().additionalInformationContainsMessage(code);
    }


}
