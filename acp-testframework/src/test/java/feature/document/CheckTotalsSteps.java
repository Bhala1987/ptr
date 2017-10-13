package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.CurrenciesDao;
import com.hybris.easyjet.database.hybris.dao.FeesAndTaxesDao;
import com.hybris.easyjet.database.hybris.models.CurrencyModel;
import com.hybris.easyjet.database.hybris.models.FeesAndTaxesModel;
import com.hybris.easyjet.database.hybris.models.HybrisFlightDbModel;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.FlightQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CURRENCY;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.GET_FLIGHT_SERVICE;

/**
 * Created by giuseppedimartino on 28/03/17.
 */

@ContextConfiguration(classes = TestApplication.class)
public class CheckTotalsSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private CurrenciesDao currenciesDao;
    @Autowired
    private FeesAndTaxesDao feesAndTaxesDao;
    private int decimalPlaces;

    @And("^all the calculation in the getFlights response are right$")
    public void allTheCalculationInTheGetFlightsResponseAreRight() throws Throwable {
        FlightsService flightsService = testData.getData(GET_FLIGHT_SERVICE);
        List<CurrencyModel> currencies = currenciesDao.getCurrencies(true);
        CurrencyModel requestedCurrency = currencies.stream()
                .filter(currency -> currency.getCode().equals(flightsService.getResponse().getCurrency()))
                .findFirst()
                .get();
        flightsService.assertThat().priceCalculationAreRight(requestedCurrency.getDecimalPlaces());
        testData.setCurrency(requestedCurrency.getCode());
        testData.setData(CURRENCY, requestedCurrency.getCode());
        decimalPlaces = requestedCurrency.getDecimalPlaces();
    }

    @Then("^all the calculation in the basket are right$")
    public void allTheCalculationInTheBasketAreRight() throws Throwable {
        FeesAndTaxesModel fee = feesAndTaxesDao.getAdminFees(testData.getCurrency()).get(0);
        basketHelper.getBasketService().assertThat().priceCalculationAreRight(decimalPlaces, fee.getFeeValue());
    }
}