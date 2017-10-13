package feature.document.steps.services.createbasketservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.BookingDao;
import com.hybris.easyjet.database.hybris.dao.CartDao;
import com.hybris.easyjet.database.hybris.dao.CurrenciesDao;
import com.hybris.easyjet.database.hybris.dao.FeesAndTaxesDao;
import com.hybris.easyjet.database.hybris.models.CurrencyModel;
import com.hybris.easyjet.database.hybris.models.FeesAndTaxesModel;
import com.hybris.easyjet.fixture.hybris.asserters.AddHoldBagToBasketAssertion;
import com.hybris.easyjet.fixture.hybris.asserters.BasketsAssertion;
import com.hybris.easyjet.fixture.hybris.asserters.BookingAssertion;
import com.hybris.easyjet.fixture.hybris.asserters.ConvertBasketCurrencyAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.UpdateCurrencyRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ConvertBasketCurrencyRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.ConvertBasketCurrencyService;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import net.thucydides.core.annotations.Steps;
import org.apache.commons.collections.CollectionUtils;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static org.assertj.core.api.Java6Assertions.fail;

/**
 * ConvertBasketCurrencySteps handle the communication with the convertBasketCurrency service (aka change currency, update currency).
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 *
 * @author gd <g.dimartino@reply.it>
 */
@ContextConfiguration(classes = TestApplication.class)
public class ConvertBasketCurrencySteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Autowired
    private CurrenciesDao currenciesDao;
    @Autowired
    private FeesAndTaxesDao feesAndTaxesDao;
    @Autowired
    private BookingDao bookingDao;
    @Autowired
    private CartDao cartDao;

    @Steps
    private GetBasketSteps getBasketSteps;
    @Steps
    private ConvertBasketCurrencyAssertion convertBasketCurrencyAssertion;
    @Steps
    private BasketsAssertion basketsAssertion;
    @Steps
    private BookingAssertion bookingAssertion;

    private ConvertBasketCurrencyService convertBasketCurrencyService;
    private BasketPathParams.BasketPathParamsBuilder basketPathParams;
    private UpdateCurrencyRequestBody.UpdateCurrencyRequestBodyBuilder updateCurrencyRequestBody;

    private CurrencyModel newCurrencyModel;
    private String newCurrency;
    private Integer newCurrencyDecimalPlaces;
    private String basketCurrency;
    private Basket originalBasket;
    private CurrencyModel originalCurrencyModel;
    private CurrencyModel baseCurrency;
    private Double totalPriceAfterCurrencyConversion;

    private void setPathParameter() {
        basketPathParams = BasketPathParams.builder()
                .basketId(testData.getData(BASKET_ID))
                .path(BasketPathParams.BasketPaths.CURRENCY);
    }

    private void setRequestBody() {
        updateCurrencyRequestBody = UpdateCurrencyRequestBody.builder()
                .newCurrencyCode(newCurrency);
    }

    private void invokeConvertBasketCurrencyService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        convertBasketCurrencyService = serviceFactory.updateCurrency(new ConvertBasketCurrencyRequest(headers.build(), basketPathParams.build(), updateCurrencyRequestBody.build()));
        testData.setData(SERVICE, convertBasketCurrencyService);
        convertBasketCurrencyService.invoke();
    }

    private void sendConvertBasketCurrencyRequest() {
        setPathParameter();
        setRequestBody();
        invokeConvertBasketCurrencyService();
    }

    private List<CurrencyModel> getValidCurrencies() {
        setOriginalBasketValues();
        List<CurrencyModel> currencies = currenciesDao.getCurrencies(true);

        currencies.removeIf(currency -> currency.getCode().equals(basketCurrency));

        //TODO This currency doesn't have prices for products defined
        currencies.removeIf(currency -> currency.getCode().equals("CZK"));

        return currencies;
    }

    private void setOriginalBasketValues(){
        getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
        BasketService basketService = testData.getData(BASKET_SERVICE);
        List<CurrencyModel> currencies = currenciesDao.getCurrencies(true);
        originalCurrencyModel = currencies.stream().filter(
                currency -> currency.getCode()
                        .equals(basketService.getResponse().getBasket().getCurrency().getCode())
        ).findFirst().get();
        baseCurrency = currencies.stream().filter(
                CurrencyModel::isBaseCurrency
        ).findFirst().get();
        basketCurrency = basketService.getResponse().getBasket().getCurrency().getCode();
        originalBasket = basketService.getResponse().getBasket();
    }

    private void setRandomCurrency() {
        List<CurrencyModel> currencies = getValidCurrencies();
        newCurrencyModel = currencies.get(new Random().nextInt(currencies.size()));
        newCurrency = newCurrencyModel.getCode();
        newCurrencyDecimalPlaces = newCurrencyModel.getDecimalPlaces();
    }

    @Given("^I changed the currency$")
    public void convertBasketCurrencySteps() {
        setRandomCurrency();
        testData.setData(CURRENCY, basketCurrency);
        sendConvertBasketCurrencyRequest();
        convertBasketCurrencyService.assertThat().currencyIsUpdated(testData.getData(BASKET_ID));

        getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
        BasketService basketService = testData.getData(BASKET_SERVICE);
        totalPriceAfterCurrencyConversion = basketService.getResponse().getBasket().getTotalAmountWithDebitCard();
    }

    @Then("^the original currency is stored against the booking$")
    public void theOriginalCurrencyIsStoredAgainstTheBooking() {
        convertBasketCurrencyAssertion
                .originalCurrencyIsStoredAgainstTheBooking(testData.getData(BOOKING_ID), testData.getData(CURRENCY));
    }

    @And("^the original currency is stored against the basket$")
    public void theOriginalCurrencyIsStored() throws Throwable {
        basketsAssertion.originalCurrencyIsStored(cartDao, originalCurrencyModel.getCode());
    }

    @And("^the (Tax|Internet Discount) value is converted from the old currency to the new currency value$")
    public void theTaxValueIsConvertedFromTheOldCurrencyToTheNewCurrencyValue(String type) throws Throwable {
        Double fee;
        List<FeesAndTaxesModel> fees = feesAndTaxesDao.getAdminFees(newCurrency, testData.getData(FARE_TYPE));

        if (CollectionUtils.isNotEmpty(fees)) {
            fee = fees.get(0).getFeeValue();
        } else {
            fee = 0.0;
        }

        BigDecimal margin = currenciesDao.getCurrencyConversionMargin();
        getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
        BasketService basketService = testData.getData(BASKET_SERVICE);
        basketsAssertion.setResponse(basketService.getResponse());


        try {
                getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
                if (newCurrency.equalsIgnoreCase(testData.getData(CURRENCY))) {
                    basketsAssertion
                            .currencyIsRight(newCurrency)
                            .priceAreRevertedToOriginalCurrency(originalBasket)
                            .priceCalculationAreRight(newCurrencyDecimalPlaces, fee);
                } else {
                    if(type.equalsIgnoreCase("tax")) {
                        basketsAssertion
                                .currencyIsRight(updateCurrencyRequestBody.build().getNewCurrencyCode())
                                .taxPricesAreUpdatedWithNewCurrency(
                                        testData.getData(CHANNEL),
                                        originalBasket,
                                        originalCurrencyModel,
                                        newCurrencyModel,
                                        baseCurrency,
                                        feesAndTaxesDao,
                                        margin)
                                .priceCalculationAreRight(newCurrencyDecimalPlaces, fee);
                    }
                    else if(type.equalsIgnoreCase("internet discount")){
                        basketsAssertion
                                .currencyIsRight(updateCurrencyRequestBody.build().getNewCurrencyCode())
                                .discountPricesAreUpdatedWithNewCurrency(
                                        testData.getData(CHANNEL),
                                        originalBasket,
                                        originalCurrencyModel,
                                        newCurrencyModel,
                                        baseCurrency,
                                        feesAndTaxesDao,
                                        margin)
                                .priceCalculationAreRight(newCurrencyDecimalPlaces, fee);
                    }
                }
        } catch (ConditionTimeoutException ignored) {
            fail("Basket after currency conversion is not right");
        }
    }

    @And("^the new price in the booking is same as in the basket$")
    public void theNewPriceInTheBookingIsSameAsInTheBasket() {
        bookingAssertion.setResponse(testData.getData(GET_BOOKING_RESPONSE));
        bookingAssertion.verifyBasketTotalWithBookingTotal(totalPriceAfterCurrencyConversion);
    }
}