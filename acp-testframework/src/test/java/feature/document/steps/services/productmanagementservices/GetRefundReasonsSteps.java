package feature.document.steps.services.productmanagementservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.asserters.GetRefundReasonsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.refdata.GetRefundReasonsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.refdata.GetRefundReasonsService;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.HEADERS;

/**
 * GetRefundReasonsSteps handle the communication with the getRefundReasons service.
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 *  and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class GetRefundReasonsSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Steps
    private GetRefundReasonsAssertion getRefundReasonsAssertion;

    private GetRefundReasonsService getRefundReasonsService;

    private void invokeGetRefundReasonsService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        getRefundReasonsService = serviceFactory.getRefundReasons(new GetRefundReasonsRequest(headers.build()));
        getRefundReasonsService.invoke();
    }

    @When("^I send the request to getRefundReasons service$")
    public void iSendTheRequestToGetRefundReasonsService() {
        invokeGetRefundReasonsService();
        getRefundReasonsAssertion.setResponse(getRefundReasonsService.getResponse());
    }

    @Then("^I should receive the list of reasons code$")
    public void iShouldReceiveTheListOfReasonsCode() {
        getRefundReasonsAssertion
                .primaryReasonsReturned()
                .secondaryReasonsContainAllowedBooking();
    }
}