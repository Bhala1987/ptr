package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.LanguagesDao;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.asserters.models.ExpectedLanguage;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.refdata.LanguagesRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.refdata.LanguagesService;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.serenitybdd.junit.spring.integration.SpringIntegrationMethodRule;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static com.hybris.easyjet.exceptions.EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INSUFFICIENT_DATA;

/**
 * Created by daniel on 21/09/2016.
 */
@ContextConfiguration(classes = TestApplication.class)
//(hierarchyMode = DirtiesContext.HierarchyMode.CURRENT_LEVEL)
public class GetLanguagesSteps {
    @Rule
    public SpringIntegrationMethodRule springIntegration = new SpringIntegrationMethodRule();
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private LanguagesDao languagesDao;
    private LanguagesService languagesService;
    private List<ExpectedLanguage> activeLanguages;
    private List<ExpectedLanguage> inactiveLanguages;

    @Autowired
    private SerenityFacade testData;

    @When("^I call the get languages service$")
    public void iCallTheGetLanguagesService() throws Throwable {
        languagesService = serviceFactory.getLanguages(new LanguagesRequest(HybrisHeaders.getValid(testData.getChannel()).build()));
        languagesService.invoke();
    }

    @Given("^there are active languages$")
    public void thereAreActiveLanguages() throws Throwable {
        activeLanguages = languagesDao.getLanguages(true);
        if (activeLanguages.size() < 1) {
            throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
        }
    }

    @Given("^there are inactive languages$")
    public void thereAreInactiveLanguages() throws Throwable {
        inactiveLanguages = languagesDao.getLanguages(false);
        if (inactiveLanguages.size() < 1) {
            throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
        }
    }

    @Then("^the active languages are returned$")
    public void theActiveLanguagesAreReturned() throws Throwable {
        languagesService.assertThat().onlyTheseLanguagesWereReturned(activeLanguages);
    }

    @Then("^the inactive languages are not returned$")
    public void theInactiveLanguagesAreNotReturned() throws Throwable {
        languagesService.assertThat().theseLanguagesWereNotReturned(inactiveLanguages);
    }
}
