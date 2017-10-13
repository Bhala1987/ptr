package feature.document.steps.services.agentservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.asserters.BasketsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.AgentPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.LogoutRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.AgentLogoutService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;

/**
 * LogoutSteps handle the communication with the logout service for the agent.
 * It makes use of testData to store parameters that can be used by other steps.
 *
 * @author gd <g.dimartino@reply.it>
 */
@ContextConfiguration(classes = TestApplication.class)
public class LogoutSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Steps
    private BasketsAssertion basketAssertion;

    private AgentLogoutService agentLogoutService;
    private AgentPathParams.AgentPathParamsBuilder agentPathParams;

    private String agentId;

    private void setPathParameter() {
        agentPathParams = AgentPathParams.builder()
                .agentId(agentId);
    }

    private void invokeLogoutService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
        agentLogoutService = serviceFactory.logoutAgent(new LogoutRequest(headers.build(), agentPathParams.build()));
        testData.setData(SERVICE, agentLogoutService);
        agentLogoutService.invoke();
    }

    private void sendLogoutRequest() {
        setPathParameter();
        invokeLogoutService();
    }

    @When("^I send a request to agent logout service$")
    public void logout() {
        agentId = testData.getData(AGENT_ID);
        sendLogoutRequest();
    }

    @Then("^the basket is removed$")
    public void iVerifyTheBasketIsRemoved() {
        basketAssertion.verifyTheBasketNotExist(testData.getData(BASKET_ID));
    }

}