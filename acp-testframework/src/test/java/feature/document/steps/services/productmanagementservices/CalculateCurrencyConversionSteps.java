package feature.document.steps.services.productmanagementservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.CurrenciesDao;
import com.hybris.easyjet.database.hybris.dao.PropertyValueConfigurationDao;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.asserters.CurrencyConversionAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.CurrencyConversionRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.CurrencyConversionRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.CurrencyConversionService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.Objects;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.HEADERS;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;

/**
 * CalculateCurrencyConversionSteps handle the communication with the calculateCurrencyConversion service.
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 *
 * @author vijayapalkayyam
 */
@ContextConfiguration(classes = TestApplication.class)
public class CalculateCurrencyConversionSteps {

    private static final String FLIGHT_PRICE_CURRENCY_CONVERSION_MARGIN = "flightPriceCurrencyConversionMargin";

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Autowired
    private CurrenciesDao currenciesDao;
    @Autowired
    private PropertyValueConfigurationDao propertyValueConfigurationDao;

    @Steps
    private CurrencyConversionAssertion currencyConversionAssertion;

    private CurrencyConversionService calculateCurrencyConversionService;
    private CurrencyConversionRequestBody.CurrencyConversionRequestBodyBuilder currencyConversionRequestBody;

    private String fromCurrencyCode;
    private String toCurrencyCode;
    private BigDecimal amountToConvert;

    private void setRequestBody() {
        currencyConversionRequestBody = CurrencyConversionRequestBody.builder()
                .fromCurrencyCode(fromCurrencyCode)
                .toCurrencyCode(toCurrencyCode);
        if (!Objects.isNull(amountToConvert)) {
            currencyConversionRequestBody.amount(amountToConvert.doubleValue());
        }
    }

    private void invokeCalculateCurrencyConversionService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        calculateCurrencyConversionService = serviceFactory.currencyConversionService(new CurrencyConversionRequest(headers.build(), currencyConversionRequestBody.build()));
        testData.setData(SERVICE, calculateCurrencyConversionService);
        calculateCurrencyConversionService.invoke();
    }

    private void sendCalculateCurrencyConversionRequest() {
        setRequestBody();
        invokeCalculateCurrencyConversionService();
    }

    @And("^I want to convert from (\\w{3})? to (\\w{3})? for an amount of (-?\\d+(?:\\.\\d+)?)?$")
    public void setCalculateCurrencyConversionParameters(String fromCurrencyCode, String toCurrencyCode, String amountToConvert) {
        this.fromCurrencyCode = fromCurrencyCode;
        this.toCurrencyCode = toCurrencyCode;
        this.amountToConvert = Objects.isNull(amountToConvert) ? null : new BigDecimal(amountToConvert);
    }

    @When("^I send the request to calculateCurrencyConversion service$")
    public void iSendCalculateCurrencyConversion() {
        sendCalculateCurrencyConversionRequest();
        currencyConversionAssertion.setResponse(calculateCurrencyConversionService.getResponse());
    }

    @When("^I send the wrong request to calculateCurrencyConversion service$")
    public void iSendWrongCalculateCurrencyConversion() {
        sendCalculateCurrencyConversionRequest();
    }

    @Then("^it should deduct the margin from total converted amount value$")
    public void itShouldDeductTheMarginFromTotalConvertedAmountValue() throws EasyjetCompromisedException {
        BigDecimal margin = new BigDecimal(propertyValueConfigurationDao.getPropertyValueBasedOnName(FLIGHT_PRICE_CURRENCY_CONVERSION_MARGIN)).multiply(new BigDecimal("0.01"));

        currencyConversionAssertion
                .amountCalculatedIsRight(fromCurrencyCode, toCurrencyCode, amountToConvert, margin, currenciesDao);
    }

}