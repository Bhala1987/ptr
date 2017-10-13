package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.FlightFinder;
import com.hybris.easyjet.database.hybris.dao.CurrenciesDao;
import com.hybris.easyjet.database.hybris.models.CurrencyModel;
import com.hybris.easyjet.fixture.hybris.helpers.*;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.SeatMapPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.SeatMapQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.AddFlightRequestBodyFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.GetSeatMapRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.PreferencesService;
import com.hybris.easyjet.fixture.wiremock.WireMockHelper;
import cucumber.api.PendingException;
import cucumber.api.Scenario;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BUNDLE;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SCENARIO;
import static com.hybris.easyjet.fixture.hybris.helpers.PurchasedSeatHelper.SEATPRODUCTS.EXTRA_LEGROOM;
import static com.hybris.easyjet.fixture.hybris.helpers.PurchasedSeatHelper.SEATPRODUCTS.STANDARD;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.SeatMapPathParams.FlightPaths.GET_SEAT_MAP;
import static com.hybris.easyjet.fixture.wiremock.WireMockHelper.updateWireMockTimeout;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jamie on 20/03/2017.
 */


@ContextConfiguration(classes = TestApplication.class)

public class GetSeatingPlanSteps {

    @Autowired
    ChannelPropertiesHelper channelPropertiesHelper;
    @Autowired
    AddFlightRequestBodyFactory addFlightRequestBodyFactory;
    @Autowired
    StaffMembertoCustomerProfileAssociationHelper staffMembertoCustomerProfileAssociationHelper;
    @Autowired
    CustomerHelper customerHelper;
    @Autowired
    PurchasedSeatHelper purchasedSeatHelper;
    @Autowired
    FlightFinder flightFinder;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    private PreferencesService preferencesService;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private CurrenciesDao currenciesDao;
    @Autowired
    private FlightHelper flightHelper;
    private FlightsService flightService;
    private PurchasedSeatHelper.SEATPRODUCTS seatProductToCheck;
    private Integer maxSeatPassengerConfig;

    private void getPublicAPISeatMap(String aCurrency, String aFlightKey, String aBundle) {
        testData.setFlightKey(aFlightKey);
        testData.setCurrency(aCurrency);
        testData.setData(BUNDLE, aBundle);

        SeatMapPathParams pathParams = SeatMapPathParams.builder()
                .flightId(aFlightKey)
                .path(GET_SEAT_MAP)
                .build();

        SeatMapQueryParams queryParams = SeatMapQueryParams.builder()
                .bundleId(aBundle)
                .currency(aCurrency)
                .build();

        purchasedSeatHelper.setSeatMapService(serviceFactory.getSeatMapService(new GetSeatMapRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, queryParams)));
        purchasedSeatHelper.getSeatMapService().invoke();
        Scenario scenario = testData.getData(SCENARIO);
        if (!scenario.getSourceTagNames().contains("@negative")) {
            purchasedSeatHelper.getSeatMapService().getResponse();
            testData.setSeatingServiceHelper(purchasedSeatHelper.getSeatMapService());
        }
    }

    @When("^I make a request to retrieve seating plan and priced inventory$")
    public void iMakeARequestToRetrieveSeatingPlanAndPricedInventory() throws Throwable {
        testData.setFlightKey(getFirstOutboundFlightKeyFromBasket());
        testData.setCurrency(getCurrencyFromBasket());

        SeatMapPathParams pathParams = SeatMapPathParams.builder()
                .flightId(getFirstOutboundFlightKeyFromBasket())
                .path(GET_SEAT_MAP)
                .build();

        SeatMapQueryParams queryParams = SeatMapQueryParams.builder()
                .basketId(getBasketCode())
                .build();

        purchasedSeatHelper.setSeatMapService(serviceFactory.getSeatMapService(new GetSeatMapRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, queryParams)));
        purchasedSeatHelper.getSeatMapService().invoke();
        Scenario scenario = testData.getData(SCENARIO);
        if (!scenario.getSourceTagNames().contains("@negative")) {
            purchasedSeatHelper.getSeatMapService().getResponse();
            testData.setSeatingServiceHelper(purchasedSeatHelper.getSeatMapService());
        }
    }

    @And("^will not return the seat map$")
    public void willNotReturnTheSeatMap() throws Throwable {
        purchasedSeatHelper.getSeatMapService().assertThatErrors();
    }

    @When("^I make a request to retrieve the seat map with an invalid flight key$")
    public void iMakeARequestToRetrieveTheSeatMapWithAnInvalidFlightKey() throws Throwable {
        SeatMapPathParams pathParams = SeatMapPathParams.builder().flightId("incorrectflightid").path(GET_SEAT_MAP).build();
        SeatMapQueryParams queryParams = SeatMapQueryParams.builder().basketId(basketHelper.getBasketService().getResponse().getBasket().getCode()).build();
        purchasedSeatHelper.setSeatMapService(serviceFactory.getSeatMapService(new GetSeatMapRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, queryParams)));
        purchasedSeatHelper.getSeatMapService().invoke();
        Scenario scenario = testData.getData(SCENARIO);
        if (!scenario.getSourceTagNames().contains("@negative")) {
            purchasedSeatHelper.getSeatMapService().getResponse();
            testData.setSeatingServiceHelper(purchasedSeatHelper.getSeatMapService());
        }
    }

    private String getFirstOutboundFlightKeyFromBasket() {
        return basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getFlightKey();
    }

    private String getCurrencyFromBasket() {
        return basketHelper.getBasketService().getResponse().getBasket().getCurrency().getCode();
    }

    private String getBasketCode() {
        return basketHelper.getBasketService().getResponse().getBasket().getCode();
    }

    @And("^the chosen bundle contains the same level seating band$")
    public void theChosenBundleContainsTheSameLevelSeatBand() throws Throwable {
        // Reporting only
    }

    @Then("^I will set the final offer price as zero$")
    public void biWillSetTheFinalOfferPriceAsZero() throws Throwable {
        purchasedSeatHelper.getSeatMapService().assertThat().seatOfferPriceIsZero(purchasedSeatHelper.getProductFromSeat(seatProductToCheck));
    }

    @And("^the seating band is lower than the bundle seat$")
    public void theSeatingBandIsLowerThanTheBundleSeat() throws Throwable {
        // Reporting only
        //Using flexi with extra legroom, check the standard seating product
        seatProductToCheck = STANDARD;
        testData.setSeatProductInBasket(EXTRA_LEGROOM);
    }

    @And("^the basket contains a fare type Bundle that does not includes a seat band product$")
    public void theBasketContainsAFareTypeBundleThatDoesNotIncludesASeatBandProduct() throws Throwable {
        // reporting only.
    }

    @Then("^I will set the final offer price as the price received from the seating service$")
    public void iWillSetTheFinalOfferPriceAsThePriceReceivedFromTheSeatingService() throws Throwable {
        purchasedSeatHelper.getSeatMapService().assertThat().offerPriceIsBasePriceForAllSeatProducts();
    }

    @And("^the seating band is higher than the bundle seat$")
    public void theSeatingBandIsHigherThanTheBundleSeat() throws Throwable {
        //using standard with SPC-2, check the extra leg room product (SPC-1)
        seatProductToCheck = EXTRA_LEGROOM;
        testData.setSeatProductInBasket(STANDARD);
    }

    @Then("^I will set the final offer price as the price difference between the two bands$")
    public void iWillSetTheFinalOfferPriceAsThePriceDifferenceBetweenTheTwoBands() throws Throwable {
        CurrencyModel currency = currenciesDao.getCurrency(purchasedSeatHelper.getSeatMapService().getResponse().getCurrencyCode());

        purchasedSeatHelper.getSeatMapService().assertThat().seatFinalOfferIsDifferenceBetweenSeatProducts(
                currency,
                purchasedSeatHelper.getProductFromSeat(seatProductToCheck),
                purchasedSeatHelper.getProductFromSeat(testData.getSeatProductInBasket())
        );
    }

    @When("^I make a request to retrieve the seat map with an invalid currency$")
    public void iMakeARequestToRetrieveTheSeatMapWithAnInvalidCurrency() throws Throwable {
        //cannot test this scenario. Currency is validated before adding seatmap to basket.
    }

    @Then("^I should receive the seat map$")
    public void iShouldReceiveTheSeatMap() throws Throwable {
        assertThat(purchasedSeatHelper.getSeatMapService().getResponse().getProducts()).isNotNull();
    }

    @And("^the seat map Flight Key should be correct$")
    public void theSeatMapFlightKeyShouldBeCorrect() throws Throwable {
        purchasedSeatHelper.getSeatMapService().assertThat().flightKeyIsCorrect(testData.getFlightKey());
    }

    @And("^the seat map Currency should be correct$")
    public void theSeatMapCurrencyShouldBeCorrect() throws Throwable {
        purchasedSeatHelper.getSeatMapService().assertThat().currencyIsCorrect(testData.getCurrency());
    }

    @And("^the seating band is the same level as the bundle seat$")
    public void theSeatingBandIsTheSameLevelAsTheBundleSeat() throws Throwable {
        // Reporting only
        //using standard fare check the standard product price
        seatProductToCheck = STANDARD;
        testData.setSeatProductInBasket(STANDARD);
    }

    @Then("^I will receive the final offer price based on the bundle in the basket$")
    public void iWillReceiveTheFinalOfferPriceBasedOnTheBundleInTheBasket() throws Throwable {
        // Write type here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^I will return the associated aircraft seat map$")
    public void iWillReturnTheAssociatedAircraftSeatMap() throws Throwable {
        String myAircraftCode = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getCarrier();
        purchasedSeatHelper.getSeatMapService().assertThat().seatMapContainsAircraftCode(myAircraftCode);
    }

    @And("^I will return seat bands and seat pricing$")
    public void iWillReturnSeatBandsAndSeatPricing() throws Throwable {
        // Write type here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^I will return facility locations, available seats and restricted seats$")
    public void iWillReturnFacilityLocationsAvailableSeatsAndRestrictedSeats() throws Throwable {
        // Write type here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^I will return both debit and credit card final offer price$")
    public void iWillReturnBothDebitAndCreditCardFinalOfferPrice() throws Throwable {
        // Write type here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Given("^that I have received a response from the Seating Service$")
    public void thatIHaveReceivedAResponseFromTheSeatingService() throws Throwable {
        // reporting only
    }

    @When("^the Seating Service is down or no seat map is returned for PublicApi$")
    public void theSeatingServiceIsDownOrNoSeatMapIsReturnedPublicAPi() throws Throwable {

        //turn off wiremock
        updateWireMockTimeout(500000);

        getPublicAPISeatMap("GBP", "666", "Standard");

        //turn on wiremock
        updateWireMockTimeout(500);
    }


    @Then("^I will return an error message to the channel$")
    public void iWillReturnAnErrorMessageToTheChannel() throws Throwable {
        // Write type here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^the seat map is not returned$")
    public void theSeatMapIsNotReturned() throws Throwable {
        // reporting only
    }

    @Then("^the seat map and prices service should return the invalid flight key error:(.*)$")
    public void theSeatMapAndPricesServiceShouldReturnTheInvalidFlightKeyErrorErrorCode(String aErrorCode) throws Throwable {
        purchasedSeatHelper.getSeatMapService().assertThatErrors().containedTheCorrectErrorMessage(aErrorCode.trim());
    }

    @Then("^the seat map and prices service should return the invalid currency error:(.*)$")
    public void theSeatMapAndPricesServiceShouldReturnTheInvalidCurrencyErrorErrorCode(String aErrorCode) throws Throwable {
        purchasedSeatHelper.getSeatMapService().assertThatErrors().containedTheCorrectErrorMessage(aErrorCode.trim());
    }

    @Then("^the seat map and prices service should return the passenger limit error:(.*)$")
    public void theSeatMapAndPricesServiceShouldReturnThePassengerLimitError(String aErrorCode) throws Throwable {
        purchasedSeatHelper.getSeatMapService().assertThatErrors().containedTheCorrectErrorMessage(aErrorCode.trim());
    }

    @And("^the max seat passenger limit is configured for channel:(.*)$")
    public void thePassengerLimitIsConfiguredForChannelChannel(String aChannel) throws Throwable {
        testData.setChannel(aChannel.trim());
        maxSeatPassengerConfig = Integer.valueOf(channelPropertiesHelper.getPropertyValueByChannelAndKey(testData.getChannel(), "maxSeatPassengers"));
        assertThat(maxSeatPassengerConfig).isNotNull();
    }


    @Given("^my basket contains flight with 1 more passenger than the config added via \"(.*)\"$")
    public void myBasketContainsFlightWithMorePassengerThanTheConfigAddedVia(String aChannel) throws Throwable {
        testData.setChannel(aChannel);
        basketHelper.addFlightToBasketAsChannel(maxSeatPassengerConfig + 1, aChannel);
    }

    @Given("^I have a flight in my basket with \"(.*)\" fare via channel:(.*)$")
    public void iHaveAFlightInMyBasketWithInclusiveFareViaChannelChannel(String fareBundle, String aChannel) throws Throwable {
        testData.setChannel(aChannel);

        basketHelper.myBasketContainsAFlightWithPassengerMixForStaff(testData.getPassengerMix(), testData.getChannel(), fareBundle);
        basketHelper.getBasketService().getResponse();
    }

    @Then("^the seat map service should return error the \"(.*)\" error message(?:.*)$")
    public void theSeatMapServiceShouldReturnErrorTheErrorMessageStatingUnableToReturnSeatMapForStandbyBundles(String err) throws Throwable {
        purchasedSeatHelper.getSeatMapService().assertThatErrors().containedTheCorrectErrorMessage(err.trim());
    }

    @When("^I make a request to retrieve seating plan and priced inventory for PublicAPI using bundle:(Flexi|Standard|Staff|Standby|StaffStandard)$")
    public void iMakeARequestToRetrieveSeatingPlanAndPricedInventoryForPublicAPIUsingBundle(String aBundle) throws Throwable {
        flightHelper.setSectors();
        flightService = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), testData.getFareType(), testData.getCurrency());
        getPublicAPISeatMap("GBP", flightService.getInboundFlight().getFlightKey(), aBundle);
    }

    @When("^I make a request to retrieve seating plan and priced inventory for PublicAPI$")
    public void iMakeARequestToRetrieveSeatingPlanAndPricedInventoryForPublicAPI() throws Throwable {
        iMakeARequestToRetrieveSeatingPlanAndPricedInventoryForPublicAPIUsingBundle("Standard");
    }


    @When("^I make a request to retrieve seating plan and priced inventory with invalid currency for PublicAPI:(.*)$")
    public void iMakeARequestToRetrieveSeatingPlanAndPricedInventoryWithInvalidCurrencyForPublicAPIInvalidCurrency(String anInvalidCurrency) throws Throwable {
        flightService = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), testData.getFareType(), testData.getCurrency());
        getPublicAPISeatMap(anInvalidCurrency, flightService.getOutboundFlight().getFlightKey(), "Standard");
    }

    @When("^I make a request to retrieve seating plan and priced inventory with invalid flight key for PublicAPI:(.*)$")
    public void iMakeARequestToRetrieveSeatingPlanAndPricedInventoryWithInvalidFlightKeyForPublicAPIInvalidFlightKey(String anInvalidFlightKey) throws Throwable {
        getPublicAPISeatMap("GBP", anInvalidFlightKey, "Standard");
    }


    @When("^I make a request to retrieve seating plan and priced inventory with invalid bundle for PublicAPI:(.*)$")
    public void iMakeARequestToRetrieveSeatingPlanAndPricedInventoryWithInvalidBundleForPublicAPIInvalidBundle(String aBundle) throws Throwable {
        flightService = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), testData.getFareType(), testData.getCurrency());
        getPublicAPISeatMap("EUR", flightService.getOutboundFlight().getFlightKey(), aBundle);
    }

    @Then("^the seat map service should return the \"(.*)\" value based error message stating bundle is invalid$")
    public void theSeatMapServiceShouldReturnTheValueBasedErrorMessageStatingBundleIsInvalid(String errorCode) throws Throwable {
        purchasedSeatHelper.getSeatMapService().assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }

    @Then("^the seat map service should return error the \"([^\"]*)\" value based error message stating currency is invalid$")
    public void theSeatMapServiceShouldReturnErrorTheValueBasedErrorMessageStatingCurrencyIsInvalid(String errorCode) throws Throwable {
        purchasedSeatHelper.getSeatMapService().assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }

    @Then("^the seat map and prices service should return the seat map unavailable error:(.*)$")
    public void theSeatMapAndPricesServiceShouldReturnTheSeatMapUnavailableErrorString(String error) throws Throwable {
        purchasedSeatHelper.getSeatMapService().assertThatErrors().containedTheCorrectErrorMessage(error.trim());
    }


    @When("^the Seating Service is down or no seat map is returned$")
    public void theSeatingServiceIsDownOrNoSeatMapIsReturned() throws Throwable {

        WireMockHelper.updateWireMockTimeout(5000000);
        iMakeARequestToRetrieveSeatingPlanAndPricedInventory();
        WireMockHelper.updateWireMockTimeout(500);
    }
}
