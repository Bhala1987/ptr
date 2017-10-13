package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.FlightFinder;
import com.hybris.easyjet.database.hybris.dao.CartDao;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.FlightHelper;
import com.hybris.easyjet.fixture.hybris.helpers.PurchasedSeatHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.FlightQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.AddFlightRequestBodyFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.FlightsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BUNDLE;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.FARE_TYPE;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.GET_FLIGHT_SERVICE;
import static feature.document.steps.constants.StepsRegex.CHANNELS;

@ContextConfiguration(classes = TestApplication.class)

public class ClearBasketSteps {

    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private FlightFinder flightFinder;
    @Autowired
    private FlightHelper flightHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private PurchasedSeatHelper purchasedSeatHelper;
    @Autowired
    private SerenityFacade testData;
    private String basketCode;
    private FlightQueryParams params;
    private FlightsService flightsService;
    private List<AddFlightRequestBodyFactory.multiFlightData> multiFlightParams;
    private FindFlightsResponse.Flight flight;
    @Autowired
    private CartDao cartdao;

    @When("^I clear the basket$")
    public void iClearTheBasket() throws Throwable {
        basketCode = basketHelper.getBasketService().getResponse().getBasket().getCode();
        basketHelper.emptyBasket(basketCode, "Digital");
    }

    @Then("^the basket is emptied$")
    public void theBasketIsEmptied() throws Throwable {
        basketHelper.getBasketService().assertThat().isEmptied(basketCode,cartdao);
    }

    @Then("^the flights are de-allocated via \"([^\"]*)\"$")
    public void theFlightsAreDeallocated(String channel) throws Throwable {
        flightsService = serviceFactory.findFlight(new FlightsRequest(HybrisHeaders.getValid(channel).build(), params));
        flightsService.invoke();
    }

    @Given("^I have a basket with a valid flight with ([^\"]*) adult added via "+CHANNELS+"$")
    public void iHaveABasketWithAValidFlightAddedVia(int numberOfAdults, String channel) throws Throwable {
        testData.setData(BUNDLE, "Standard");
        testData.setChannel(channel);
        flightsService = flightHelper.getFlights(testData.getChannel(), numberOfAdults + " Adult", testData.getOrigin(), testData.getDestination(),"SINGLE");
        testData.setData(GET_FLIGHT_SERVICE, flightsService);
        flight = flightsService.getOutboundFlight();
        basketHelper.addFlightToBasketAsChannel(flight);
    }

    @Then("^the flights are not further de-allocated for (.*)$")
    public void theFlightsAreNotFurtherDeallocated(String channel) throws Throwable {
        flightsService = serviceFactory.findFlight(new FlightsRequest(HybrisHeaders.getValid(channel).build(), params));
        flightsService.invoke();
    }

    @Given("^I have a basket with valid multiple flights$")
    public void iHaveABasketWithValidMultipleFlights() throws Throwable {
        basketHelper.addNumberOfFlightsToBasketForDigital(2);
    }

    @When("^I clear the basket via \"([^\"]*)\"$")
    public void iClearTheBasketVia(String channel) throws Throwable {
        basketCode = basketHelper.getBasketService().getResponse().getBasket().getCode();
        basketHelper.emptyBasket(basketCode, channel);
    }

    @Given("^I have a basket with valid multiple flights added via \"([^\"]*)\"$")
    public void iHaveABasketWithValidMultipleFlightsAddedVia(String channel) throws Throwable {
        multiFlightParams = basketHelper.addNumberOfFlightsToBasket(2, channel);
    }

    @Then("^all the flights are de-allocated via \"([^\"]*)\"$")
    public void allTheFlightsAreDeallocated(String channel) throws Throwable {
        for (AddFlightRequestBodyFactory.multiFlightData mfParams : multiFlightParams) {
            flightsService = serviceFactory.findFlight(new FlightsRequest(HybrisHeaders.getValid(channel).build(), mfParams.getFlightQueryParams()));
            flightsService.invoke();
        }
    }

    @Given("^I have a basket with two valid flight with one adult added via ([^\"]*)$")
    public void iHaveABasketWithTwoValidFlightWithAdultAddedViaChannel(String channel) throws Throwable {
        testData.setData(BUNDLE, "Standard");
        testData.setChannel(channel);
        flightsService = flightHelper.getFlights(
                testData.getChannel(),  "1 Adult", testData.getOrigin(), testData.getDestination(),null);
        testData.setData(GET_FLIGHT_SERVICE, flightsService);
        flight = flightsService.getOutboundFlight();
        if(testData.getChannel().equalsIgnoreCase("ADAirport")) {
            basketHelper.addFlightToBasketWithAdditionalSeat(
                    flightsService.getOutboundFlights(),
                    flightsService.getResponse().getCurrency(),
                    "1,1 Adult",
                    "single",
                    testData.getData(BUNDLE));
        }
        else {
            basketHelper.addFlightToBasketAsChannel(flight);
        }

        flightsService = flightHelper.getFlights(
                testData.getChannel(),  "1 Adult", testData.getOrigin(), testData.getDestination(),null);
        testData.setData(GET_FLIGHT_SERVICE, flightsService);
        flight = flightsService.getOutboundFlight();
        if(testData.getChannel().equalsIgnoreCase("ADAirport")) {
            basketHelper.addFlightToBasketWithAdditionalSeat(
                    flightsService.getOutboundFlights(),
                    flightsService.getResponse().getCurrency(),
                    "1,1 Adult",
                    "single",
                    testData.getData(BUNDLE));
        }
        else {
            basketHelper.addFlightToBasketAsChannel(flight);
        }
    }

    @And("^I add a \"(EXTRA_LEGROOM|STANDARD)\" seat for each passenger$")
    public void iAddASeatForEachPassenger(PurchasedSeatHelper.SEATPRODUCTS aSeatProduct) throws Throwable {
        testData.setSeatProductInBasket(aSeatProduct);
        purchasedSeatHelper.addPurchasedSeatToBasketForEachPassengerAndFlight(aSeatProduct, false);
    }

    @And("^I have a basket with (\\d+) flights$")
    public void iHaveABasketWithFlights(int numberOfFlights) throws Throwable {
        basketHelper.addNumberOfFlightsToBasketForDigital(numberOfFlights);
        testData.setData(FARE_TYPE, "Standard");
    }
}
