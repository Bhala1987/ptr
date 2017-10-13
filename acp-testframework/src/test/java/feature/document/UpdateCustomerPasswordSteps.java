package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.AccountPasswordHelper;
import cucumber.api.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;


/**
 * Created by robertadigiorgio on 09/02/2017.
 */
@ContextConfiguration(classes = TestApplication.class)

public class UpdateCustomerPasswordSteps {

    @Autowired
    private AccountPasswordHelper accountPasswordHelper;

    @Autowired
    private SerenityFacade testData;

    @Given("^I create a new customer and execute the login$")
    public void iCreateANewCustomerAndExecuteTheLogin() throws Throwable {
        accountPasswordHelper.createNewAccountForCustomerAndLoginIt();
    }

    @When("^I send a request to update password from \"([^\"]*)\" with \"([^\"]*)\"$")
    public void iSendARequestToUpdatePasswordFromWith(String channel, String newPassword) throws Throwable {
        accountPasswordHelper.updatePassword(channel, newPassword);
    }

    @Then("^I will return a error message to the channel \"([^\"]*)\"$")
    public void iWillReturnAErrorMessageToTheChannel(String code) throws Throwable {
        accountPasswordHelper.getUpdatePasswordService().assertThatErrors().containedTheCorrectErrorMessage(code);
    }

    @And("^I should calculate the password \"([^\"]*)\" score$")
    public void iShouldCalculateThePasswordScore(String strength) throws Throwable {
        accountPasswordHelper.getUpdatePasswordService().assertThat().strengthIsTheExpected(strength);
    }

    @Then("^I can login with the new \"([^\"]*)\"$")
    public void iCanLoginWithTheNew(String password) throws Throwable {
        accountPasswordHelper.getLogin(password);
    }

    @Given("^I create a new customer profile$")
    public void iCreateANewCustomerProfile() throws Throwable {
        accountPasswordHelper.createNewAccountForCustomer();
    }

    @And("^I receive the request to updatePassword of the customer$")
    public void iReceiveTheRequestToUpdatePasswordOfTheCustomer() throws Throwable {
        accountPasswordHelper.createRequestToUpdatePassword();
    }

    @But("^the customer account status is locked$")
    public void theCustomerStatusIsLocked() throws Throwable {
        accountPasswordHelper.buildRequestForResetPassword();
        accountPasswordHelper.callServiceResetPassword();
        accountPasswordHelper.updatePasswordFieldWithValue("passwordResetToken", accountPasswordHelper.getTokenForCustomer());
    }

    @And("^the request contains a token$")
    public void theRequestContainsAToken() throws Throwable {
        // DO NOTHING, step already done in the builder of the requestBody
    }

    @And("^the request does not contain the field \"([^\"]*)\"$")
    public void theRequestDoesNotContainTheField(String field) throws Throwable {
        accountPasswordHelper.updatePasswordRequestBodyMissingField(field);
    }

    @When("^I validate the request to updatePassword$")
    public void iValidateTheRequestToUpdatePassword() throws Throwable {
        accountPasswordHelper.callServiceUpdatePassword();
    }

    @And("^the request contains a new password$")
    public void theRequestContainsANewPassword() throws Throwable {
        // DO NOTHING, step already done in the builder of the requestBody
    }

    @But("^the new field \"([^\"]*)\" has lenght \"([^\"]*)\"$")
    public void theNewFieldHasLenght(String field, int length) throws Throwable {
        accountPasswordHelper.updatePasswordRequestWithFieldLength(field, length);
    }

    @But("^the field \"([^\"]*)\" contains space$")
    public void theFieldContainsSpace(String field) throws Throwable {
        accountPasswordHelper.updatePasswordFieldWithSpace(field);
    }

    @But("^the field \"([^\"]*)\" is part of the guessable word list with value \"([^\"]*)\"$")
    public void theFieldIsPartOfTheGuessableWordListWithValue(String field, String value) throws Throwable {
        accountPasswordHelper.updatePasswordFieldWithValue(field, value);
    }

    @But("^the field \"([^\"]*)\" contains a symbol \"([^\"]*)\" which is not allowed$")
    public void theFieldContainsASymbolWhichIsNotAllowed(String field, String symbol) throws Throwable {
        accountPasswordHelper.setUpdatePasswordRequestFieldWithSymbol(field, symbol);
    }

    @Then("^I should store the password against the profile$")
    public void iShouldStoreThePasswordAgainstTheProfile() throws Throwable {
        accountPasswordHelper.verifyCustomerProfileIsStored();
    }

    @And("^I will verify any Saved APIS, Saved Payment methods, SSR and Saved Passengers details from the Customer's profile are present$")
    public void iWillVerifyAnySavedAPISSavedPaymentMethodsSSRAndSavedPassengersDetailsFromTheCustomerSProfileArePresent() throws Throwable {
        accountPasswordHelper.verifyAllDataRelatedCustomerAreClear();
    }

    @And("^set the account status to Active$")
    public void setTheAccountStatusToActive() throws Throwable {
        // DO NOTHING, step already done in login with new credential
    }

    @And("^I will return confirmation to channel$")
    public void iWillReturnConfirmationToChannel() throws Throwable {
        accountPasswordHelper.getUpdatePasswordService().assertThat().returnConfirmationForUpdatePassword();
    }

    @And("^Return strength score to the channel$")
    public void returnStrengthScoreToTheChannel() throws Throwable {
        accountPasswordHelper.getUpdatePasswordService().assertThat().returnStrengthForUpdatePassword();
    }

    @And("^the field \"([^\"]*)\" contains a symbol \"([^\"]*)\"$")
    public void theFieldContainsASymbol(String field, String symbol) throws Throwable {
        accountPasswordHelper.setUpdatePasswordRequestFieldWithSymbol(field, symbol);
    }

    @And("^token has expired or used previously$")
    public void tokenHasExpiredOrUsedPreviously() throws Throwable {
        // From automation side we cover just the case of reused token
        // NOTHING TO DO, the step to verify the expiration of the token should will verify manually
        for (int i = 0; i < 6; i++)
            accountPasswordHelper.getLogin(testData.getPassword());
        accountPasswordHelper.callServiceUpdatePassword();
    }
}
