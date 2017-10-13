package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.FeesAndTaxesDao;
import com.hybris.easyjet.database.hybris.models.FeesAndTaxesModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.ALInventoryManagementHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.FlightHelper;
import com.hybris.easyjet.fixture.hybris.helpers.RepriceBasketHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.RecalculatePricesRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.BasketContentFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.BasketRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.Currency;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.RecalculatePricesService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.But;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;

/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 08/05/2017.
 */
@ContextConfiguration(classes = TestApplication.class)
@DirtiesContext
public class RecalculatePricesSteps {

    protected static Logger LOG = LogManager.getLogger(RecalculatePricesSteps.class);
    @Autowired
    ALInventoryManagementHelper alInventoryManagementHelper;
    @Autowired
    FlightHelper flightHelper;
    @Autowired
    private RepriceBasketHelper repriceBasketHelper;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private FeesAndTaxesDao feesAndTaxesDao;
    private RecalculatePricesService recalculatePricesService;
    private RecalculatePricesRequestBody recalculatePricesRequestBody;
    private String basketCode;
    private BasketPathParams basketPathParams;
    private BasketService basketService;
    private Basket newBasket;
    private Basket oldBasket;
    private int quantity;

    @When("^I trigger the recalculatePrices service$")
    public void iTriggerTheRecalculatePricesService() throws Throwable {
        if (testData.getChannel().equalsIgnoreCase("PublicAPiB2B"))
            recalculatePricesService =
                    repriceBasketHelper.invokeRepriceBasket(testData.getChannel(), null, testData.getBasketContent());
        else
            recalculatePricesService = repriceBasketHelper.invokeRepriceBasket(testData.getChannel(), basketHelper.getBasketService().getResponse().getBasket().getCode(), null);
    }

    @Then("^I should get the updated basket for a Flight price change$")
    public void iShouldGetTheUpdatedBasketForAFlightPriceChange() throws Throwable {
        BasketService basketService = testData.getData(BASKET_SERVICE);
        oldBasket = basketService.getResponse().getBasket();

        basketPathParams = BasketPathParams.builder().basketId(basketHelper.getBasketService().getResponse().getBasket().getCode()).build();
        basketService = serviceFactory.getBasket(new BasketRequest(HybrisHeaders.getValid(testData.getChannel()).build(), basketPathParams));
        basketService.invoke();

        newBasket = basketService.getResponse().getBasket();

        Currency oldBasketCurrency = oldBasket.getCurrency();
        FeesAndTaxesModel oldBasketFee = feesAndTaxesDao.getAdminFees(oldBasketCurrency.getCode()).get(0);
        basketHelper.getBasketService().assertThat().priceCalculationAreRight(Integer.valueOf(oldBasketCurrency.getDecimalPlaces()), oldBasketFee.getFeeValue());

        Currency newBasketCurrency = oldBasket.getCurrency();
        FeesAndTaxesModel newBasketFee = feesAndTaxesDao.getAdminFees(newBasketCurrency.getCode()).get(0);
        basketHelper.getBasketService().assertThat().priceCalculationAreRight(Integer.valueOf(newBasketCurrency.getDecimalPlaces()), newBasketFee.getFeeValue());

        recalculatePricesService.assertThat().affectedDataNameReturned("/basket/outbounds/0/flights/", "/fare/0/pricing/basePrice");
    }

    @Then("^I should get the success response$")
    public void iShouldGetTheSuccessResponse() throws Throwable {
        repriceBasketHelper.getRecalculatePricesService().assertThat().basketOperationConfirmation(basketHelper.getBasketService().getResponse().getBasket().getCode());
    }

    @Then("^I should get the success response along with basket$")
    public void iShouldGetTheSuccessResponseWithBasket() throws Throwable {
        repriceBasketHelper.getRecalculatePricesService().assertThat().basketOperationConfirmation(null);
    }

    @When("^I trigger the recalculatePrices service with invalid request (.*)$")
    public void iTriggerTheRecalculatePricesServiceWithInvalidBasketCodntent(String parameter) throws Throwable {
        repriceBasketHelper.invokeRepriceBasketServiceWithInvalidParam(testData.getChannel(), testData.getBasketContent());
        testData.setData(SERVICE, repriceBasketHelper.getRecalculatePricesService());
    }

    @When("^I trigger the recalculatePrices service with invalid basket code")
    public void iTriggerTheRecalculatePricesServiceWithInvalidBasketCode() throws Throwable {
        recalculatePricesRequestBody = RecalculatePricesRequestBody.builder().basketCode("invalid").build();
        repriceBasketHelper.invokeRepriceBasketServiceWithInvalidBasketCode(recalculatePricesRequestBody);
    }

    @And("^I should get the same basket if there are no price changes$")
    public void iShouldGetTheSameBasketIfThereAreNoPriceChanges() throws Throwable {
        BasketService basketService = testData.getData(BASKET_SERVICE);
        oldBasket = basketService.getResponse().getBasket();

        basketPathParams = BasketPathParams.builder().basketId(basketHelper.getBasketService().getResponse().getBasket().getCode()).build();
        basketService = serviceFactory.getBasket(new BasketRequest(HybrisHeaders.getValid(testData.getChannel()).build(), basketPathParams));
        basketService.invoke();

        newBasket = basketService.getResponse().getBasket();

        basketHelper.getBasketService().assertThat().basketComparison(newBasket, oldBasket);
    }

    @Then("^I should get the success response with \"([^\"]*)\"$")
    public void iShouldGetTheSuccessResponseWithAnd(String successCode) throws Throwable {
        iShouldGetTheSuccessResponse();
        recalculatePricesService.assertThat().additionalInformationReturned(successCode);
    }

    @And("^I should get the updated basket for a Seat price change$")
    public void iShouldGetTheUpdatedBasketForASeatPriceChange() throws Throwable {
        BasketService basketService = testData.getData(BASKET_SERVICE);
        oldBasket = basketService.getResponse().getBasket();

        basketPathParams = BasketPathParams.builder().basketId(basketHelper.getBasketService().getResponse().getBasket().getCode()).build();
        basketService = serviceFactory.getBasket(new BasketRequest(HybrisHeaders.getValid(testData.getChannel()).build(), basketPathParams));
        basketService.invoke();

        newBasket = basketService.getResponse().getBasket();

        Currency oldBasketCurrency = oldBasket.getCurrency();
        FeesAndTaxesModel oldBasketFee = feesAndTaxesDao.getAdminFees(oldBasketCurrency.getCode()).get(0);
        basketHelper.getBasketService().assertThat().priceCalculationAreRight(Integer.valueOf(oldBasketCurrency.getDecimalPlaces()), oldBasketFee.getFeeValue());

        Currency newBasketCurrency = oldBasket.getCurrency();
        FeesAndTaxesModel newBasketFee = feesAndTaxesDao.getAdminFees(newBasketCurrency.getCode()).get(0);
        basketHelper.getBasketService().assertThat().priceCalculationAreRight(Integer.valueOf(newBasketCurrency.getDecimalPlaces()), newBasketFee.getFeeValue());

        recalculatePricesService.assertThat().affectedDataNameReturned("/seats/0/pricing/totalAmountWithDebitCart", "/seats/0/pricing/totalAmountWithCreditCart");
    }

    @And("^I should get the affected data with (.*)$")
    public void iShouldGetTheAffectedDataWith(String affectedDataCode) throws Throwable {
        recalculatePricesService.assertThat().additionalInformationReturned(affectedDataCode);
    }

    @And("^I should get the message in additional information informing that there is no change$")
    public void iShouldGetTheMessageInAdditionalInformationInformingThatThereIsNoChange() throws Throwable {
        repriceBasketHelper.getRecalculatePricesService().assertThat().additionalInformationReturned("SVC_100187_1000");
    }

    @And("^I should get the message in additional information informing about change$")
    public void iShouldGetTheMessageInAdditionalInformationInformingAboutChangeBasedOnCriteria() throws Throwable {
        repriceBasketHelper.getRecalculatePricesService().assertThat().additionalInformationReturned("SVC_100187_1001");
    }

    @And("^I will verify that basket has no changes$")
    public void iWillVerifyThatBasketHasNoChnages() throws Throwable {
        BasketService basketService = testData.getData(BASKET_SERVICE);
        repriceBasketHelper.getRecalculatePricesService().assertThat().compairBasketContent(basketService.getResponse().getBasket(), repriceBasketHelper.getRecalculatePricesService().getResponse().getBasket());
    }

    @And("^I will verify that basket has changes$")
    public void iWillVerifyThatBasketHasChnages() throws Throwable {
        BasketService basketService = testData.getData(BASKET_SERVICE);
        repriceBasketHelper.getRecalculatePricesService().assertThat().compairBasketContent(basketService.getResponse().getBasket(), repriceBasketHelper.getRecalculatePricesService().getResponse().getBasket());
    }

    @When("^I request recalculate price for basket content with (.*) flight not available for (.*) journey$")
    public void iRequestRecalculatePriceForBasketContentWithIndexFlightNotAvailableForOutboundJourney(int index, String journey) throws Throwable {
        if (journey.contains("outbound") && journey.contains("inbound")) {
            alInventoryManagementHelper.allocateAllInventory(getFlightKey(index, "inbound"));
            alInventoryManagementHelper.allocateAllInventory(getFlightKey(index, "outbound"));
        } else
            alInventoryManagementHelper.allocateAllInventory(getFlightKey(index, journey));
        recalculatePricesService = repriceBasketHelper.invokeRepriceBasket(testData.getChannel(), null, testData.getBasketContent());
    }

    private String getFlightKey(int index, String journey) {
        List<Basket.Flight> journeyFlights;
        BasketService basketService = testData.getData(BASKET_SERVICE);
        if (journey.equalsIgnoreCase("outbound")) {
            journeyFlights = basketService.getResponse().getBasket().getOutbounds().stream().flatMap(flights -> flights.getFlights().stream()).collect(Collectors.toList());
            return journeyFlights.get(index - 1).getFlightKey();
        } else
            journeyFlights = basketService.getResponse().getBasket().getInbounds().stream().flatMap(flights -> flights.getFlights().stream()).collect(Collectors.toList());
        return journeyFlights.get(index - 1).getFlightKey();
    }

    @Then("^I will receive an error code as \"([^\"]*)\"$")
    public void iWillReceiveAnErrorCodeAsSomething(String errorCode) throws Throwable {
        repriceBasketHelper.getRecalculatePricesService().assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }

    @And("^I will verify that the (.*) flight with journey as (.*) is removed from basket$")
    public void iWillVerifyThatTheIndexFlightWithJourneyAsJourneyIsRemovedFromBasket(int index, String journey) throws Throwable {

        repriceBasketHelper.getRecalculatePricesService().assertThat().verifyFlightIsRemoved(getFlightKey(index, journey), journey);

    }

    @But("^the base price should be changed$")
    public void theBasePriceShouldBeChanged() throws Throwable {


        String previousJSession = HybrisService.theJSessionCookie.get();
        BasketsResponse previousBasket = basketHelper.getBasketService().getResponse();

        FlightsService flightsService = testData.getData(GET_FLIGHT_SERVICE);
        FindFlightsResponse.Flight flight = flightsService.getResponse().getOutbound().getJourneys()
                .stream().filter(
                        journey -> journey.getFlights().get(0).getFlightKey().equals(testData.getFlightKey())
                )
                .map(FindFlightsResponse.Journey::getFlights).flatMap(Collection::stream)
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("The flight key " + testData.getFlightKey() + " was not returned from find flight"));

        HybrisService.theJSessionCookie.remove();
        basketHelper.getBasketService().setBasketsResponse(null);

        quantity = 0;

        if (CollectionUtils.isNotEmpty(flight.getFareTypes())) {

            Optional<FindFlightsResponse.FareType> actualFare = flight.getFareTypes().stream().filter(fareType -> fareType.getFareTypeCode().equalsIgnoreCase(testData.getActualFareType())).findFirst();

            if (actualFare.isPresent() && actualFare.get().getFareClass() != null) {

                quantity = actualFare.get().getFareClass().getAvailableUnits();
                if (testData.getChannel().equalsIgnoreCase("ADAirport") || testData.getChannel().equalsIgnoreCase("ADCustomerService")) {
                    quantity = quantity - 1;
                }
            }

            do {
                basketHelper.addFlightToBasketAsChannelUsingFlightCurrencyWithPassengerMixAndFaretypeAndJourney(flight,
                        quantity + " adult",
                        "ADAirport",
                        flightsService.getResponse().getCurrency(),
                        testData.getActualFareType(),
                        "single");
            }
            while (basketHelper.getBasketService().getResponse().getAdditionalInformations().stream().noneMatch(info -> info.getCode().equalsIgnoreCase("SVC_100012_3008")));

            HybrisService.theJSessionCookie.set(previousJSession);
            basketHelper.getBasketService().setBasketsResponse(previousBasket);

        }
    }

    @And("^I searched a valid flight for (.*)$")
    public void iSearchedAValidFlightForAdult(String passengerMix) throws Throwable {
        flightHelper.getFlights(testData.getChannel(), passengerMix, testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate());
    }

    @And("^I will verify that the basket price is updated$")
    public void iWillVerifyThatTheBasketPriceIsUpdated() throws Throwable {
        newBasket = repriceBasketHelper.getRecalculatePricesService().getResponse().getBasket();
        Currency oldBasketCurrency = newBasket.getCurrency();
        FeesAndTaxesModel oldBasketFee = feesAndTaxesDao.getAdminFees(oldBasketCurrency.getCode()).get(0);
        basketHelper.getBasketService().assertThat().priceCalculationAreRight(Integer.valueOf(oldBasketCurrency.getDecimalPlaces()), oldBasketFee.getFeeValue(), newBasket);
    }


    @And("^I updated the price of (.*) in my basket content$")
    public void iUpdatedThePriceOfProductTypeInMyBasketContent(List<String> productType) throws Throwable {
     testData.setBasketContent(BasketContentFactory.getBasketContentWithUpdatedPrice(testData.getBasketContent(),productType));
    }

    @And("^I should get the notification of (.*) change in response$")
    public void iShouldGetTheNotificationOfProductTypeChangeInResponse(List<String> productTypes) throws Throwable {
        recalculatePricesService.assertThat().verifyInformationInAffectedData(productTypes);
    }


    @And("^I updated the price of (.*) in my basket content for (.*) passenger$")
    public void iUpdatedThePriceOfProductInMyBasketContentForPassengerIndexPassenger(String productType,String passengerIndex) throws Throwable {
        String[] paxIndex = passengerIndex.split("'");
        testData.setBasketContent(BasketContentFactory.getBasketContentWithUpdatedSeatPrice(testData.getBasketContent(),Integer.valueOf(paxIndex[0])-1));
    }
}
