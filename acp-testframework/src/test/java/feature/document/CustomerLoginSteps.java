package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.CustomerHelper;
import cucumber.api.PendingException;
import cucumber.api.Scenario;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SCENARIO;
import static com.hybris.easyjet.config.constants.MockTransactionIdentifiers.*;

/**
 * Created by dwebb on 12/16/2016.
 */
@ContextConfiguration(classes = TestApplication.class)

public class CustomerLoginSteps {

    @Autowired
    private CustomerHelper customerHelper;

    @Autowired
    private SerenityFacade testData;

    private int maxAttempts = 6;

    @When("^I login with valid credentials$")
    public void iLoginWithValidCredentials() throws Throwable {
        customerHelper.loginWithValidCredentials(testData.getChannel());
    }

    @When("^I login with valid credentials and remember me$")
    public void iLoginWithValidCredentialsAndRememberMe() throws Throwable {
        customerHelper.loginWithValidCredentialsAndRememberMe();
    }

    @Given("^a customer account exists with a known password$")
    public void aCustomerAccountExistsWithAKnownPassword() throws Throwable {
        customerHelper.customerAccountExistsWithAKnownPassword();
    }

    @Then("^I should be successfully logged in$")
    public void iShouldBeSuccessfullyLoggedIn() throws Throwable {
        testData.setData(CUSTOMER_ID, customerHelper.getLoginDetailsService().getResponse().getAuthenticationConfirmation().getCustomerId());
        customerHelper.getLoginDetailsService().assertThat().theLoginWasSuccesful();
    }

    @When("^I login with valid credentials using the \"([^\"]*)\" channel$")
    public void iLoginWithValidCredentialsUsingTheChannel(String channel) throws Throwable {
        testData.setChannel(channel);
        customerHelper.loginWithValidCredentials(channel);
    }

    @Given("^staff Customer is logged on \"([^\"]*)\" channel and using the \"([^\"]*)\" and \"([^\"]*)\" credentials$")
    public void staffcustomerisloggedon(String channel,String email, String pwd) throws Throwable {
        customerHelper.loginWithValidCredentials(testData.getChannel(), email, pwd, false);
    }

    @Then("^I should not be logged in$")
    public void iShouldNotBeLoggedIn() throws Throwable {
        customerHelper.getLoginDetailsService().assertThatErrors().containedTheCorrectErrorMessage("SVC_100046_2001");
    }

    @When("^I login with invalid credentials$")
    public void iLoginWithInvalidCredentials() throws Throwable {
        customerHelper.loginWithInvalidCredentials();
    }

    @When("^I provide a different email address$")
    public void iProvideADifferentEmailAddress() throws Throwable {
        customerHelper.loginWithDifferentEmail("thisisnotthe@emailforpassword.com");
    }

    @And("^I should have a remember me cookie$")
    public void iShouldHaveARememberMeCookie() throws Throwable {
        customerHelper.getLoginDetailsService().assertThat().theRememberMeCookieIsSet();
    }

    @And("^configuration is in place for maximum number of failed attempts to get lock$")
    public void configurationIsInPlaceForMaximumNumberOfFailedAttemptsToGetLock() throws Throwable {
        //assume it is six
        maxAttempts = 5;
    }

    @When("^I breach the maximum login attempts in a single session$")
    public void iBreachTheMaximumLoginAttemptsInASingleSession() throws Throwable {
        //attempt login 7 times
        for (int i = 0; i < maxAttempts; i++) {
            customerHelper.loginWithInvalidPassword("");
            customerHelper.getLoginDetailsService().assertThatErrors().containedTheCorrectErrorMessage("SVC_100046_2001");
        }
        if (maxAttempts == 6) {
            customerHelper.getLoginDetailsService().assertThatErrors().containedTheCorrectErrorMessage("SVC_100012_2071");
        }
        customerHelper.loginWithInvalidPassword("");
    }

    @Then("^the account is locked$")
    public void theAccountIsLocked() throws Throwable {
        customerHelper.getLoginDetailsService().assertThatErrors().containedTheCorrectErrorMessage("SVC_100046_2002");
    }

    @When("^I breach the maximum login attempts over multiple sessions$")
    public void iBreachTheMaximumLoginAttemptsOverMultipleSessions() throws Throwable {
        for (int i = 0; i < maxAttempts; i++) {
            customerHelper.loginWithInvalidPassword("");
            customerHelper.getLoginDetailsService().assertThatErrors().containedTheCorrectErrorMessage("SVC_100046_2001");
        }
        customerHelper.loginWithInvalidPassword("");
    }

    @And("^the account has been disabled$")
    public void theAccountHasBeenDisabled() throws Throwable {
        for (int i = 0; i < maxAttempts; i++) {
            customerHelper.loginWithInvalidPassword("");
        }
    }

    @Then("^I am informed that the account is disabled$")
    public void iAmInformedThatTheAccountIsDisabled() throws Throwable {
        customerHelper.getLoginDetailsService().assertThatErrors().containedTheCorrectErrorMessage("SVC_100046_2002");
    }

    @Then("^I am informed that only Digital can access this channel$")
    public void iAmInformedThatOnlyDigitalCanAccessThisChannel() throws Throwable {
        customerHelper.getLoginDetailsService().assertThatErrors().containedTheCorrectErrorMessage("SVC_100046_2005");
    }

    @When("^I login with missing \"([^\"]*)\"$")
    public void iLoginWithMissing(String parameter) throws Throwable {
        customerHelper.loginWithMissingParametersInBody(parameter);
    }

    @Then("^error for \"([^\"]*)\" is returned$")
    public void errorForIsReturned(String error) throws Throwable {
        customerHelper.getLoginDetailsService().assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @And("^the customer (.*) and (.*) is logged in$")
    public void theCustomerAndIsLoggedIn(String email, String password) throws Throwable {
        testData.setEmail(email);
        testData.setPassword(password);
        String channelForThisTest = testData.getChannel();
        Scenario scenario = testData.getData(SCENARIO);
        if (scenario.getSourceTagNames().contains("@OnlyToLoginUseDigital")) {
            testData.setChannel("Digital");
        }

        customerHelper.loginWithValidCredentials(testData.getChannel(), email, password, false);

        if (scenario.getSourceTagNames().contains("@OnlyToLoginUseDigital")) {
            testData.setChannel(channelForThisTest);
        }
    }

    @When("^I login as same user as before$")
    public void iLoginAsSameUserAsBefore() throws Throwable {
        customerHelper.loginWithValidCredentials(testData.getChannel(), testData.getEmail(),testData.getPassword(), false);
    }

    @When("^I login back while (.*)$")
    public void iLoginBack(String unAvailability) throws Throwable {
        switch (unAvailability) {
            case "flight inventory unavailable":
                customerHelper.loginWithCustomXClientTransactionId(FLIGHT_UNAVAILABLE.getTransactionId());
                break;
            case "flight price has changed":
                customerHelper.loginWithCustomXClientTransactionId(FLIGHT_PRICE_CHANGE.getTransactionId());
                break;
            case "seat inventory unavailable":
                customerHelper.loginWithCustomXClientTransactionId(SEAT_UNAVAILABLE.getTransactionId());
                break;
            case "seat price has changed":
                customerHelper.loginWithCustomXClientTransactionId(SEAT_PRICE_CHANGE.getTransactionId());
                break;
            default:
                break;
        }
    }

    @And("^I get customer profile (.*)$")
    public void iGetCustomerProfileProfile(String profile) throws Throwable {
        customerHelper.getCustomerProfile(profile);
    }
}
