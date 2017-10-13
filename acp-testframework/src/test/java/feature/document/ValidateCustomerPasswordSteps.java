package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.fixture.hybris.helpers.CustomerHelper;
import cucumber.api.java.en.But;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by AndrewGr on 23/12/2016.
 */
@ContextConfiguration(classes = TestApplication.class)

public class ValidateCustomerPasswordSteps {

    protected static Logger LOG = LogManager.getLogger(ValidateCustomerPasswordSteps.class);
    @Autowired
    private CustomerHelper customerHelper;

    @Given("^a valid request to create a profile for customer$")
    public void aValidRequestToCreateAProfileForCustomer() throws Throwable {
        customerHelper.aValidRequestToCreateAProfileForCustomer();
    }

    @When("^the request is validated$")
    public void theRequestIsValidated() throws Throwable {
        customerHelper.requestCreationOfACustomerProfile();
    }

    @But("^the password has symbol \"([^\"]*)\"$")
    public void thePasswordHas(String symbolToInsertIntoPassword) throws Throwable {
        customerHelper.setCustomerPasswordWithSymbol(symbolToInsertIntoPassword);
    }

    @But("^the password has a space in it$")
    public void thePasswordHasASpaceInIt() throws Throwable {
        customerHelper.setCustomerPasswordWithSpace();
    }

    @But("^the password length is \"([^\"]*)\"$")
    public void thePasswordLengthIs(int passwordLength) throws Throwable {
        customerHelper.setCustomerPasswordLength(passwordLength);
    }

    @But("^the password has a guessable word \"([^\"]*)\"$")
    public void thePasswordHasAGuessableWord(String password) throws Throwable {
        customerHelper.setCustomerPasswordWithAGuessableWord(password);
    }

    @Then("^a password strength \"([^\"]*)\"is returned$")
    public void aPasswordStrengthIsReturned(String expectedPasswordStrength) throws Throwable {
        customerHelper.getRegisterCustomerService().assertThat().theCustomersPasswordStrengthAsExpected(customerHelper.getRequest(), expectedPasswordStrength);
    }

    @But("^the password is set to \"([^\"]*)\"$")
    public void thePasswordIsSetTo(String password) throws Throwable {
        customerHelper.setCustomerPasswordWithAGuessableWord(password);
    }

    @Then("^a illegal password \"([^\"]*)\" error is returned$")
    public void aIllegalPasswordErrorIsReturned(String error) throws Throwable {
        customerHelper.getRegisterCustomerService().assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @Then("^a \"([^\"]*)\" error is returned$")
    public void aErrorIsReturned(String error) throws Throwable {
        customerHelper.getRegisterCustomerService().assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @When("^the request is validated for negative scenario$")
    public void theRequestIsValidatedForNegativeScenario() throws Throwable {
        theRequestIsValidated();
    }
}
