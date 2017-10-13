package feature.document.steps.services.productmanagementservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.refdata.GetBulkTransferReasonsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.refdata.BulkTransferReasonsService;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.HEADERS;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;

@ContextConfiguration(classes = TestApplication.class)
public class GetBulkTransferReasonsSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private BulkTransferReasonsService bulkTransferReasonsService;

    private void invokeGetBulkTransferReasonsService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        bulkTransferReasonsService = serviceFactory.bulkTransferReasonsService(new GetBulkTransferReasonsRequest(headers.build()));
        testData.setData(SERVICE, bulkTransferReasonsService);
        bulkTransferReasonsService.invoke();
    }

    @When("^I send the request to getBulkTransferReasons service")
    public void getBulkTransferReasons() {
        invokeGetBulkTransferReasonsService();
    }

    @Then("^the list of requested bulk transfer is returned$")
    public void iExpectToSeeAllOfTheBulkTransferReasons() {
        bulkTransferReasonsService.assertThat()
                .theReturnedListIsRight();
    }

}
