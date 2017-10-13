package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.database.hybris.dao.TravelDocumentTypesDao;
import com.hybris.easyjet.database.hybris.models.TravelDocumentTypesModel;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.refdata.TravelDocumentTypesRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.refdata.TravelDocumentTypesService;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

/**
 * Created by giuseppecioce on 08/02/2017.
 */
@ContextConfiguration(classes = TestApplication.class)

public class GetTravelDocumentTypeSteps {

    @Autowired
    private HybrisServiceFactory serviceFactory;
    private TravelDocumentTypesService travelDocumentTypesService;
    @Autowired
    private TravelDocumentTypesDao travelDocumentTypesDao;
    private List<TravelDocumentTypesModel> travelDocumentTypesModels;

    @Given("^I have access to APIS$")
    public void iHaveAccessToAPIS() {
        travelDocumentTypesService = serviceFactory.getTravelDocumentTypes(new TravelDocumentTypesRequest(HybrisHeaders.getValid("Digital").build()));
        travelDocumentTypesModels = travelDocumentTypesDao.getTravelDocumentType(true);
    }

    @When("^I select document types$")
    public void iSelectDocumentTypes() throws Throwable {
        travelDocumentTypesService.invoke();
    }

    @Then("^I will receive a list of available document types$")
    public void iWillReceiveAListOfAvailableDocumentTypes() throws Throwable {
        travelDocumentTypesService.assertThat().verifyTravelDocumentTypesAreReturned(travelDocumentTypesModels);
    }

}
