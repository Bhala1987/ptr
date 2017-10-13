package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.LocalisedLanguagesDao;
import com.hybris.easyjet.database.hybris.dao.TitleDao;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.refdata.PassengerTitlesRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.refdata.PassengerTitlesService;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;


/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 10/11/2016.
 */
@ContextConfiguration(classes = TestApplication.class)

public class GetPassengerTitlesSteps {

    private List<String> expectedTitlesFromDatabase;
    @Autowired
    private TitleDao _hybrisTitleDao;
    @Autowired
    private LocalisedLanguagesDao localisedLanguagesDao;
    private PassengerTitlesService passengerTitlesService;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Autowired
    private SerenityFacade testData;

    @Given("^there are active passenger titles available$")
    public void thereArePassengerTitlesAvailable() {
        // Check in the DB for passenger titles
        expectedTitlesFromDatabase = _hybrisTitleDao.findTitlesWhichAreActive(true);
    }

    @When("^I request passenger title reference data$")
    public void iRequestPassengerTitleReferenceData() throws Throwable {
        passengerTitlesService = serviceFactory.getPassengerTitles(new PassengerTitlesRequest(HybrisHeaders.getValid(testData.getChannel()).build()));
        passengerTitlesService.invoke();
    }

    @Then("^all applicable passenger titles are returned$")
    public void allApplicablePassengerTitlesAreReturned() throws Throwable {
        passengerTitlesService.assertThat().theNumberOfPassengerTitlesReturnedWas(expectedTitlesFromDatabase.size())
                .titlesAreAsExpected(expectedTitlesFromDatabase);
    }

    @Then("^all localisation data is present$")
    public void allLocalisationDataIsPresent() throws Throwable {
        List<String> expectedLocales = localisedLanguagesDao.getLocales(null);
        passengerTitlesService.assertThat().allLocalisationDataIsPresent(expectedLocales);
    }

    @When("^I request passenger title reference data for a language$")
    public void iRequestPassengerTitleReferenceDataForALanguage() throws Throwable {
        HybrisHeaders header = HybrisHeaders.getValid("Digital").build();
        header.setAcceptLanguage("en");
        passengerTitlesService = serviceFactory.getPassengerTitles(new PassengerTitlesRequest(header));
        passengerTitlesService.invoke();
    }

    @Then("^only language specific reference data is returned$")
    public void onlyLanguageSpecificReferenceDataIsReturned() throws Throwable {
        List<String> expectedLocales = localisedLanguagesDao.getLocales("en");
        passengerTitlesService.assertThat().allLocalisationDataIsPresentForLanguage("en", expectedLocales);
    }
}