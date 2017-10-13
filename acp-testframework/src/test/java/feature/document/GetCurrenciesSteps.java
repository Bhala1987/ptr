package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.CurrenciesDao;
import com.hybris.easyjet.database.hybris.models.CurrencyModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.refdata.CurrenciesRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.refdata.CurrenciesService;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static com.hybris.easyjet.exceptions.EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INSUFFICIENT_DATA;

/**
 * Created by daniel on 21/09/2016.
 */
@ContextConfiguration(classes = TestApplication.class)

public class GetCurrenciesSteps {

    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private CurrenciesDao currenciesDao;
    private CurrenciesService currenciesService;
    private List<CurrencyModel> activeCurrencies;
    private List<CurrencyModel> inactiveCurrencies;

    @Autowired
    private SerenityFacade testData;

    @Given("^there are active currencies$")
    public void thereAreActiveCurrencies() throws Throwable {
        activeCurrencies = currenciesDao.getCurrencies(true);
        if (activeCurrencies.size() < 1) {
            throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
        }
    }

    @When("^I call the get currencies service$")
    public void iCallTheGetCurrenciesService() throws Throwable {
        currenciesService = serviceFactory.getCurrencies(new CurrenciesRequest(HybrisHeaders.getValid(testData.getChannel()).build()));
        currenciesService.invoke();
    }

    @Then("^the active currencies are returned$")
    public void theActiveCurrenciesAreReturned() throws Throwable {
        currenciesService.assertThat().onlyTheseCurrenciesWereReturned(activeCurrencies);
    }

    @Given("^there are inactive currencies$")
    public void thereAreInactiveCurrencies() throws Throwable {
        inactiveCurrencies = currenciesDao.getCurrencies(false);
        if (inactiveCurrencies.size() < 1) {
            throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
        }
    }

    @Then("^the inactive currencies are not returned$")
    public void theInactiveCurrenciesAreNotReturned() throws Throwable {
        currenciesService.assertThat().theseCurrenciesWereNotReturned(inactiveCurrencies);
    }
}