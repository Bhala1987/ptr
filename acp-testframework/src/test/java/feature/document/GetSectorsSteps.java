package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.SectorsDao;
import com.hybris.easyjet.database.hybris.models.SectorsModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.SectorQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.refdata.SectorsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.refdata.SectorsService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static com.hybris.easyjet.exceptions.EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INSUFFICIENT_DATA;

/**
 * Created by giuseppe on 31/01/2017.
 */
@ContextConfiguration(classes = TestApplication.class)

public class GetSectorsSteps {

    private SectorsService sectorsService;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private SectorsDao sectorsDao;
    private List<SectorsModel> activeSectors;
    private List<SectorsModel> activeSectorsforApi;
    @Autowired
    private SerenityFacade testData;

    @Given("^there are active sectors in the database$")
    public void thereAreActiveSectorsInTheDatabase() throws Throwable {
        activeSectors = sectorsDao.returnActiveSectors();
        if (activeSectors.size() < 1) {
            throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
        }
    }

    @When("^I call the getSectors service for the ([^\\s]*)$")
    public void iCallTheGetSectorsServiceForTheChannel(String channel) throws Throwable {
        sectorsService = serviceFactory.getSectors(new SectorsRequest(HybrisHeaders.getValid(channel).build(), null));
        sectorsService.invoke();
    }

    @Then("^I will return all the active sectors to the channel$")
    public void iWillReturnAllTheActiveSectorsToTheChannel() throws Throwable {
        sectorsService.assertThat()
                .thereWereSectorsReturned()
                .allActiveSectorsWereReturned(activeSectors);
    }


    @Given("^there are active sectors in the database for (.*)$")
    public void thereAreActiveSectorsInTheDatabaseForOriginAirportCode(String origin) throws Throwable {
        activeSectors = sectorsDao.returnActiveSectorsForOriginAirport(origin);
        if (activeSectors.size() < 1) {
            throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
        }
    }

    @When("^I call the getSectors service for the ([^\\s]*) for ([^\\s]*)$")
    public void iCallTheGetSectorsServiceForTheChannelForOriginAirportCode(String channel, String origin) throws Throwable {
        SectorQueryParams queryParam = SectorQueryParams.builder().originAirportCode(origin).build();
        sectorsService = serviceFactory.getSectors(new SectorsRequest(HybrisHeaders.getValid(channel).build(), queryParam));
        sectorsService.invoke();
    }


    @And("^I have sectors set in backoffice with APIs required (.*)$")
    public void iHaveSectorsSetInBackofficeWithAPISRequired(boolean condition) throws Throwable {
        activeSectorsforApi = sectorsDao.returnSectorsWithApiTrue(condition);
        // once date issues are fixed we can uncomment the below lines and remove the hardcoded sectors info
        // testData.setOrigin(activeSectorsforApi.get(0).getDepartureAirport());
        // testData.setDestination(activeSectorsforApi.get(0).getArrivalAirport());
//        testData.setOrigin("ALC");
//        testData.setDestination("FAO");
        if (activeSectorsforApi.isEmpty()) {
            throw new EasyjetCompromisedException(INSUFFICIENT_DATA);

        }

    }
}