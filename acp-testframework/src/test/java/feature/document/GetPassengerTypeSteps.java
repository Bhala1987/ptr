package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.database.hybris.dao.PassengerTypeDao;
import com.hybris.easyjet.database.hybris.models.PassengerTypeDbModel;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.refdata.PassengerTypesRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.refdata.PassengerTypesService;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

/**
 * Created by daniel on 21/09/2016.
 */
@ContextConfiguration(classes = TestApplication.class)

public class GetPassengerTypeSteps {

    private PassengerTypesService passengerTypesService;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    private List<PassengerTypeDbModel> paxTypes;
    @Autowired
    private PassengerTypeDao dbPaxTypes;

    @When("^I call the get passenger types service$")
    public void iCallTheGetPassengerTypesService() throws Throwable {
        passengerTypesService = serviceFactory.getPassengerTypes(new PassengerTypesRequest(HybrisHeaders.getValid("Digital").build()));
        passengerTypesService.invoke();
    }

    @Given("^there are passenger types$")
    public void thereArePasssengerTypes() throws Throwable {
        paxTypes = dbPaxTypes.getPassengerTypes();
    }

    @Then("^the passenger types are returned$")
    public void thePassengerTypesAreReturned() throws Throwable {
        passengerTypesService.assertThat().thePassengerTypesWereReturned(paxTypes);
    }


    @Then("^the passenger rules associating infant on seat to it adult is returned$")
    public void thePassengerRulesAreReturned() throws Throwable {
        passengerTypesService.assertThat().thesePassengerRulesAreReturned();
    }
}