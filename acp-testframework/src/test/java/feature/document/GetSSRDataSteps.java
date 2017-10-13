package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.database.hybris.dao.EnumerationValuesDao;
import com.hybris.easyjet.database.hybris.dao.SSRDataDao;
import com.hybris.easyjet.database.hybris.dao.SectorsDao;
import com.hybris.easyjet.database.hybris.models.SSRDataModel;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.SSRDataQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.refdata.SSRDataRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.refdata.SSRDataService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

/**
 * Created by giuseppecioce on 09/02/2017.
 */
@ContextConfiguration(classes = TestApplication.class)

public class GetSSRDataSteps {

    List<SSRDataModel> expectedSSRData;
    private String channel;
    private String sector;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    private SSRDataService ssrDataService;
    @Autowired
    private SSRDataDao ssrDataDao;
    @Autowired
    private EnumerationValuesDao enumerationValuesDao;
    @Autowired
    private SectorsDao sectorDao;

    @Given("^I have access to SSR for \"([^\"]*)\" for the channel$")
    public void iHaveAccessToSSRForForTheChannel(String channel) throws Throwable {
        this.channel = channel;
    }

    @And("^The sector passed in the request is a valid \"([^\"]*)\" sector$")
    public void theSectorPassedInTheRequestIsAValidSector(String sector) throws Throwable {
        this.sector = sector;
        String PKsector = sectorDao.returnPKSectorFromCode(sector);
        String pkChannel = enumerationValuesDao.getPCodeForChannel(channel);
        expectedSSRData = ssrDataDao.getSSRDataForValidSector(true, pkChannel, PKsector);
    }

    @And("^The sector passed in the request is empty$")
    public void theSectorPassedInTheRequestIsEmpty() throws Throwable {
        sector = "";
        String pkChannel = enumerationValuesDao.getPCodeForChannel(channel);
        expectedSSRData = ssrDataDao.getSSRDataForEmptySector(true, pkChannel);
    }

    @When("^I select SSR$")
    public void iSelectSSR() throws Throwable {
        ssrDataService = serviceFactory.getSSRData(new SSRDataRequest(HybrisHeaders.getValid(channel).build(), SSRDataQueryParams.builder().sector(sector).build()));
        ssrDataService.invoke();
    }

    @Then("^I will return a list of active SSR for the requesting channel and sectors$")
    public void iWillReturnAListOfActiveSSRForTheRequestingChannelAndSectors() throws Throwable {
        ssrDataService.assertThat().verifySSRDataAreReturned(expectedSSRData);
    }

    @And("^The sector passed in the request is an invalid sector$")
    public void theSectorPassedInTheRequestIsAnInvalidSector() throws Throwable {
        this.sector = "INVALID";
    }

    @Then("^I will return the \"([^\"]*)\" error$")
    public void iWillReturnTheError(String errorCode) throws Throwable {
        ssrDataService.assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }

    @And("^I will show which SSR require T&C to be accepted$")
    public void iWillShowWhichSSRRequireTCToBeAccepted() throws Throwable {
        ssrDataService.assertThat().eachSSRContainsTAndC();
    }
}
