package feature.document.steps.services.agentservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.asserters.AgentLoginAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.login.LoginDetails;
import com.hybris.easyjet.fixture.hybris.invoke.requests.LoginRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.AgentLoginService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;

/**
 * LoginSteps handle the communication with the login service for the agent.
 * It makes use of testData to store parameters that can be used by other steps.
 *
 * @author gd <g.dimartino@reply.it>
 */
@ContextConfiguration(classes = TestApplication.class)
public class LoginSteps {

    private static final String INVALID_USER_NAME = "testtest";
    private static final String INVALID_PASSWORD = "00000000";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Steps
    private AgentLoginAssertion agentLoginAssertion;

    private AgentLoginService agentLoginService;
    private LoginDetails.LoginDetailsBuilder loginRequest;

    // This is the only known agent
    private String email = "rachel";
    private String password = "12341234";

    private void setRequestBody() {
        loginRequest = LoginDetails.builder()
                .email(email)
                .password(password)
                .rememberme(false);
    }

    private void invokeLoginService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
        agentLoginService = serviceFactory.loginAgent(new LoginRequest(headers.build(), loginRequest.build()));
        testData.setData(SERVICE, agentLoginService);
        agentLoginService.invoke();
    }

    private void sendLoginRequest() {
        setRequestBody();
        invokeLoginService();
    }

    @Given("^I have an invalid agent (username|password)$")
    public void iHaveAnInvalidUsername(String type) {
        if (type.equalsIgnoreCase(USERNAME)) {
            email = INVALID_USER_NAME;
            testData.setData(AGENT_ID, email);
        } else if (type.equalsIgnoreCase(PASSWORD)) {
            password = INVALID_PASSWORD;
        }
    }

    @Step("Login as agent")
    @When("^I logged in as agent$")
    public void succesfulLogin() {
        sendLoginRequest();
        testData.setData(AGENT_ID, email);
        testData.setData(AGENT_ACCESS_TOKEN, agentLoginService.getResponse().getAgentAuthenticationConfirmation().getAuthentication().getAccessToken());
    }

    @When("^I send the request to agent login service$")
    public void login() {
        sendLoginRequest();
        testData.setData(AGENT_ID, email);
    }

    @Then("^the channel will receive the successful response for agent login$")
    public void theChannelWillReceiveTheSuccessfulResponseForAgentLogin() {
        agentLoginService.assertThat().theLoginWasSuccesful();
    }

    @Then("^the message of the days are returned$")
    public void theMessageOfTheDaysAreReturned() throws Throwable {
        agentLoginAssertion.setResponse(agentLoginService.getResponse());
        agentLoginAssertion
                .theMessageIsReturned()
                .noMorethenFiveMessage();
    }

}
