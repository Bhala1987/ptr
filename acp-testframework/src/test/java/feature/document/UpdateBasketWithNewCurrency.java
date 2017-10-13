package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.*;
import com.hybris.easyjet.database.hybris.models.CurrencyModel;
import com.hybris.easyjet.database.hybris.models.FeesAndTaxesModel;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.FlightHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.SeatMapPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.FlightQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.SeatMapQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.UpdateCurrencyRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ConvertBasketCurrencyRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.FlightsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.GetSeatMapRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.GetSeatMapResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractFlights;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.GetSeatMapService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.ConvertBasketCurrencyService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.collections.CollectionUtils;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.GET_FLIGHT_SERVICE;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.CURRENCY;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.SeatMapPathParams.FlightPaths.GET_SEAT_MAP;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;

/**
 * Created by giuseppedimartino on 27/03/17.
 */
@ContextConfiguration(classes = TestApplication.class)
public class UpdateBasketWithNewCurrency {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private CurrenciesDao currenciesDao;
    @Autowired
    private CartDao cartDao;
    @Autowired
    private FeesAndTaxesDao feesAndTaxesDao;
    @Autowired
    private HoldItemsDao holdItemDao;
    @Autowired
    private BundleTemplateDao bundleDao;
    private FlightsService flightsService;
    private GetSeatMapService seatMapService;
    private ConvertBasketCurrencyService convertBasketCurrencyService;
    private BasketPathParams.BasketPathParamsBuilder basketPathParams = BasketPathParams.builder();
    private UpdateCurrencyRequestBody.UpdateCurrencyRequestBodyBuilder updateCurrencyRequestBody = UpdateCurrencyRequestBody.builder();
    private Basket originalBasket;
    private String originalCurrency;

    private String basketCode;
    @Autowired
    private FlightHelper flightHelper;

    @And("^I am using an invalid basketId$")
    public void iAmUsingAnInvalidBasketId() throws Throwable {

        basketPathParams.basketId("A").path(CURRENCY);
        updateCurrencyRequestBody.newCurrencyCode("EUR");
    }

    @And("^I have a basket with a valid flight with (.*) faretype$")
    public void iHaveABasketWithAValidFlight(String fareType) throws Throwable {
        testData.setActualFareType(fareType);

        flightsService = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), testData.getFareType(), testData.getCurrency());

        testData.setData(GET_FLIGHT_SERVICE, flightsService);
        testData.setFlightKey(flightsService.getOutboundFlight().getFlightKey());
        basketHelper.addFlightsToBasket(fareType, "single");

        basketCode = basketHelper.getBasketService().getResponse().getBasket().getCode();
        originalCurrency = basketHelper.getBasketService().getResponse().getBasket().getCurrency().getCode();
        basketPathParams.basketId(basketCode).path(CURRENCY);

    }

    @And("^I am using '(.*)' currency$")
    public void iAmUsingTypeCurrency(String currencyType) throws Throwable {

        Basket currentBasket = basketHelper.getBasket(basketCode, testData.getChannel());
        if (originalBasket == null) originalBasket = currentBasket;

        String basketCurrency = currentBasket
                .getCurrency()
                .getCode();

        List<CurrencyModel> currencies = currenciesDao.getCurrencies(true);
        currencies.removeIf(currency -> currency.getCode().equals(basketCurrency));
        // This currency doesn't have prices for products defined
        currencies.removeIf(currency -> currency.getCode().equals("CZK"));

        switch (currencyType) {
            case "blank":
                updateCurrencyRequestBody.newCurrencyCode("");
                break;
            case "invalid":
                updateCurrencyRequestBody.newCurrencyCode("KFC");
                break;
            case "same":
                updateCurrencyRequestBody.newCurrencyCode(basketCurrency);
                break;
            case "original":
                updateCurrencyRequestBody.newCurrencyCode(originalCurrency);
                break;
            case "not original":
                currencies.removeIf(currency -> currency.getCode().equalsIgnoreCase(originalCurrency));
                updateCurrencyRequestBody.newCurrencyCode(
                        currencies.get((new Random()).nextInt(currencies.size())).getCode()
                );
                break;
            case "valid":
                updateCurrencyRequestBody.newCurrencyCode(
                        currencies.get((new Random()).nextInt(currencies.size())).getCode()
                );
                break;
        }
    }

    @And("^I have converted the basket currency$")
    public void iHaveConvertedTheBasketCurrency() throws Throwable {
        iAmUsingTypeCurrency("valid");
        iSendARequestToUpdateBasketCurrency();
    }

    @When("^I sen[d|t] a request to update basket currency$")
    public void iSendARequestToUpdateBasketCurrency() throws Throwable {

        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
        convertBasketCurrencyService = serviceFactory.updateCurrency(new ConvertBasketCurrencyRequest(
                headers.build()
                , basketPathParams.build()
                , updateCurrencyRequestBody.build()
        ));
        testData.setData(SERVICE, convertBasketCurrencyService);
        convertBasketCurrencyService.invoke();
    }

    @Then("^I will receive a confirmation for the update$")
    public void iWillReceiveAConfirmation() throws Throwable {
        convertBasketCurrencyService.assertThat().currencyIsUpdated(basketCode);
        basketHelper.getBasket(basketCode, testData.getChannel());
    }

    @And("^I received a confirmation for the update$")
    public void iReceivedAConfirmationForTheUpdate() throws Throwable {
        iWillReceiveAConfirmation();
    }

    @Given("^I have received a confirmation$")
    public void iHaveReceivedAConfirmation() throws Throwable {
        iWillReceiveAConfirmation();
    }

    @And("^the fare product price is converted from the old currency to the request currency at the current exchange rates(?: and the margin are applied on top of the conversion,)? and the products price are update with the new currency values$")
    public void allPricesInTheBasketAreConvertedFromTheOldCurrencyToTheRequestCurrency() throws Throwable {

        List<CurrencyModel> currencies = currenciesDao.getCurrencies(true);
        CurrencyModel oldCurrency = currencies.stream().filter(
                currency -> currency.getCode()
                        .equals(originalBasket.getCurrency().getCode())
        ).findFirst().get();

        CurrencyModel newCurrency = currencies.stream().filter(
                currency -> currency.getCode()
                        .equals(basketHelper.getBasketService().getResponse().getBasket().getCurrency().getCode())
        ).findFirst().get();

        CurrencyModel baseCurrency = currencies.stream().filter(
                CurrencyModel::isBaseCurrency
        ).findFirst().get();

        Double fee;

        List<FeesAndTaxesModel> fees = feesAndTaxesDao.getAdminFees(newCurrency.getCode(), testData.getActualFareType());

        if (CollectionUtils.isNotEmpty(fees)) {
            fee = fees.get(0).getFeeValue();
        } else {
            fee = 0.0;
        }

        BigDecimal margin = currenciesDao.getCurrencyConversionMargin();

        GetSeatMapResponse seatMapResponse;

        if (basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getSeat() != null) {

            SeatMapPathParams pathParams = SeatMapPathParams.builder()
                    .flightId(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getFlightKey())
                    .path(GET_SEAT_MAP)
                    .build();

            SeatMapQueryParams queryParams = SeatMapQueryParams.builder()
                    .basketId(basketCode)
                    .build();

            seatMapService = serviceFactory.getSeatMapService(new GetSeatMapRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, queryParams));

            try {
                pollingLoop().untilAsserted(() -> {
                    seatMapService.invoke();
                    assertThat(seatMapService.getResponse().getCurrencyCode()).isEqualTo(newCurrency.getCode());
                });
            } catch (ConditionTimeoutException ignored) {
                fail("The seating service doesn't returned the results in the new currency");
            }

            seatMapResponse = seatMapService.getResponse();
        } else {
            seatMapResponse = null;
        }

        try {
            pollingLoop().untilAsserted(() -> {
                basketHelper.getBasket(basketCode, testData.getChannel());
                if (newCurrency.getCode().equalsIgnoreCase(originalCurrency)) {
                    basketHelper.getBasketService().assertThat()
                            .currencyIsRight(newCurrency.getCode())
                            .priceAreRevertedToOriginalCurrency(originalBasket)
                            .priceCalculationAreRight(newCurrency.getDecimalPlaces(), fee);
                } else {
                    basketHelper.getBasketService().assertThat()
                            .currencyIsRight(updateCurrencyRequestBody.build().getNewCurrencyCode())
                            .priceAreUpdatedWithNewCurrency(
                                    testData.getChannel(),
                                    originalBasket,
                                    oldCurrency,
                                    newCurrency,
                                    baseCurrency,
                                    feesAndTaxesDao,
                                    holdItemDao,
                                    bundleDao,
                                    seatMapResponse,
                                    margin
                            ).priceCalculationAreRight(newCurrency.getDecimalPlaces(), fee);
                }
            });
        } catch (ConditionTimeoutException ignored) {
            fail("Basket after currency conversion is not right");
        }
    }

    @And("^the original currency is stored$")
    public void theOriginalCurrencyIsStored() throws Throwable {

        List<CurrencyModel> currencies = currenciesDao.getCurrencies(true);
        CurrencyModel oldCurrency = currencies.stream().filter(
                currency -> currency.getCode()
                        .equals(originalCurrency)
        ).findFirst().get();

        basketHelper.getBasketService().assertThat().originalCurrencyIsStored(cartDao, oldCurrency.getCode());
    }

    @And("^the price in the find flight response are updated with currency of the basket$")
    public void thePriceInTheFindFlightResponseAreUpdatedWithCurrencyOfTheBasket() throws Throwable {
        String previousJSession = HybrisService.theJSessionCookie.get();

        List<CurrencyModel> currencies = currenciesDao.getCurrencies(true);
        CurrencyModel oldCurrency = currencies.stream().filter(
                currency -> currency.getCode()
                        .equals(originalBasket.getCurrency().getCode())
        ).findFirst().get();

        CurrencyModel newCurrency = currencies.stream().filter(
                currency -> currency.getCode()
                        .equals(basketHelper.getBasketService().getResponse().getBasket().getCurrency().getCode())
        ).findFirst().get();

        CurrencyModel baseCurrency = currencies.stream().filter(
                CurrencyModel::isBaseCurrency
        ).findFirst().get();

        BigDecimal margin = currenciesDao.getCurrencyConversionMargin();

        HybrisService.theJSessionCookie.set("");
        FlightQueryParams params = FlightQueryParams.builder().build();
        params.setOutboundDate(testData.getOutboundDate());
        params.setInboundDate(testData.getInboundDate());
        params.setOrigin(testData.getOrigin());
        params.setDestination(testData.getDestination());
        params.setAdult("1");
        params.setCurrency(originalCurrency);
        flightsService = serviceFactory.findFlight(new FlightsRequest(HybrisHeaders.getValid(testData.getChannel()).build(), params));
        flightsService.invoke();

        flightsService.assertThat().priceAreUpdatedWithBasketCurrency(
                flightsService.getResponse().getOutbound().getJourneys().stream()
                        .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                        .map(FindFlightsResponse.Flight::getFareTypes).flatMap(Collection::stream)
                        .map(FindFlightsResponse.FareType::getPassengers).flatMap(Collection::stream)
                        .map(FindFlightsResponse.Passenger::getBasePrice)
                        .collect(Collectors.toList()),
                oldCurrency,
                newCurrency,
                baseCurrency,
                margin
        );

        HybrisService.theJSessionCookie.set(previousJSession);
    }

    @Then("^the flight price in the basket is in the request currency at the current exchange rates$")
    public void theFlightPriceInTheBasketIsInTheRequestCurrencyAtTheCurrentExchangeRates() throws Throwable {
        CurrencyModel newCurrency = currenciesDao.getCurrencies(true).stream().filter(
                currency -> currency.getCode()
                        .equals(basketHelper.getBasketService().getResponse().getBasket().getCurrency().getCode())
        ).findFirst().get();

        Double fee;

        List<FeesAndTaxesModel> fees = feesAndTaxesDao.getAdminFees(newCurrency.getCode(), testData.getActualFareType());
        if (CollectionUtils.isNotEmpty(fees))
            fee = fees.get(0).getFeeValue();
        else
            fee = 0.0;

        FlightsService flightsService = testData.getData(GET_FLIGHT_SERVICE);
        try {
            pollingLoop().untilAsserted(() -> {
                basketHelper.getBasket(basketCode, testData.getChannel());
                basketHelper.getBasketService().assertThat()
                        .flightPriceIsInNewCurrency(
                                flightsService.getOutboundFlight(),
                                "Standard",
                                "adult"
                        )
                        .priceCalculationAreRight(newCurrency.getDecimalPlaces(), fee);
            });
        } catch (ConditionTimeoutException ignored) {
            fail("Basket after currency conversion is not right");
        }
    }
}