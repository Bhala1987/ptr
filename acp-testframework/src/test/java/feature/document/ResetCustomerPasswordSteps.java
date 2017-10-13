package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.fixture.hybris.helpers.AccountPasswordHelper;
import cucumber.api.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by giuseppecioce on 01/03/2017.
 */
@ContextConfiguration(classes = TestApplication.class)

public class ResetCustomerPasswordSteps {

    @Autowired
    private AccountPasswordHelper accountPasswordHelper;


    @Given("^I have valid customer profile$")
    public void iHaveValidCustomerProfile() throws Throwable {
        accountPasswordHelper.createNewAccountForCustomer();
    }

    @And("^I have received a valid password reset request$")
    public void IHaveReceivedAValidPasswordResetRequest() throws Throwable {
        accountPasswordHelper.buildRequestForResetPassword();

    }

    @But("^the email ID is not associated to the customer$")
    public void theEmailIDIsNotAssociatedToTheCustomer() throws Throwable {
        accountPasswordHelper.updateResetPasswordEmail("wrongvalue@something.it");
    }

    @When("^I process the request for resetPassword$")
    public void iProcessTheRequestForResetPassword() throws Throwable {
        accountPasswordHelper.callServiceResetPassword();
    }

    @Then("^I should return an error \"([^\"]*)\" message$")
    public void iShouldReturnAnErrorMessage(String error) throws Throwable {
        accountPasswordHelper.getResetPasswordService().assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @And("^I set the profile status to locked$")
    public void iSetTheProfileStatusToLocked() throws Throwable {
        /**
         * NOTHING TO DO, this step is implicit done in the reset request service
         */
    }

    @Given("^I have received a valid password reset request for anonymous customer$")
    public void iHaveReceivedAValidPasswordResetRequestForAnonymousCustomer() throws Throwable {
        accountPasswordHelper.buildRequestForResetPasswordForAnonymous("somevalue@something.it");
    }

    @Then("^I will generate a temporary Token$")
    public void iWillGenerateATemporaryToken() throws Throwable {
        accountPasswordHelper.getResetPasswordService().assertThat().verifyTheTokenHasBeenCreated(accountPasswordHelper.getTokenForCustomer());
    }

    @When("^I process the request for resetPassword for anonymous customer$")
    public void iProcessTheRequestForResetPasswordForAnonymousCustomer() throws Throwable {
        accountPasswordHelper.callServiceResetPasswordForAnonymous();
    }
}
