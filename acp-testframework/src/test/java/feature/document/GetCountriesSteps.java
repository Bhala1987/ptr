package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.CountriesDao;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.refdata.CountriesRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.refdata.CountriesService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Map;

import static com.hybris.easyjet.exceptions.EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INSUFFICIENT_DATA;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by daniel on 21/09/2016.
 */
@ContextConfiguration(classes = TestApplication.class)

public class GetCountriesSteps {

    private static Logger LOG = LogManager.getLogger(GetCountriesSteps.class);
    @Autowired
    private CountriesDao countriesDao;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private List<String> activeCountriesInHybrisDatabase;
    private List<String> inactiveCountriesInTheHybrisDatabase;

    private Map<String, String> diallingCodeForCountryCode;

    private CountriesService countriesService;

    @Autowired
    private SerenityFacade testData;

    @When("^I call the get countries service$")
    public void iCallTheGetCountriesService() throws Throwable {
        countriesService = serviceFactory.getCountries(new CountriesRequest(HybrisHeaders.getValid(testData.getChannel()).build()));
        countriesService.invoke();
    }

    @Given("^there are active countries in the database$")
    public void thereAreCountriesInTheDatabase() throws Throwable {
        activeCountriesInHybrisDatabase = countriesDao.getCountries(true);
        assertThat(activeCountriesInHybrisDatabase).size().isGreaterThan(0);
    }

    @Then("^there are countries returned$")
    public void thereAreCountriesReturned() throws Throwable {
        countriesService.assertThat().thereWereCountriesReturned();
    }

    @Then("^all active countries are returned$")
    public void allCountriesAreReturned() throws Throwable {
        countriesService.assertThat().theseCountriesWereAllReturned(activeCountriesInHybrisDatabase);
    }

    @And("^the country information includes the country international dialling code$")
    public void theCountryInformationIncludesTheCountryInternationalDiallingCode() throws Throwable {

        diallingCodeForCountryCode = countriesDao.getCountriesAndDiallingCode(true);

        countriesService.assertThat().theCountryContainsTheInternationalDiallingCodeIfStoredInDb(diallingCodeForCountryCode);
    }

    @Given("^there are inactive countries in the database$")
    public void thereAreInactiveCountriesInTheDatabase() throws Throwable {
        inactiveCountriesInTheHybrisDatabase = countriesDao.getCountries(false);
        if (inactiveCountriesInTheHybrisDatabase.size() < 1) {
            throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
        }
        assertThat(inactiveCountriesInTheHybrisDatabase).size().isGreaterThan(0);
    }

    @Then("^the inactive countries are not returned$")
    public void theInactiveCountriesAreNotReturned() throws Throwable {
        countriesService.assertThat().theseCountriesWereNotReturned(inactiveCountriesInTheHybrisDatabase);
    }

}
