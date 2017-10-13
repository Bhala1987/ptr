package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.fixture.hybris.helpers.CustomerHelper;
import cucumber.api.java.en.But;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by dwebb on 12/15/2016.
 */
@ContextConfiguration(classes = TestApplication.class)
public class RegisterCustomerSteps {

    @Autowired
    private CustomerHelper customerHelper;

    @Given("^a valid request to create a customer profile$")
    public void aValidRequestToCreateACustomerProfile() throws Throwable {
        customerHelper.aValidRequestToCreateAProfileForCustomer();
    }

    @When("^I request creation of a customer profile$")
    public void iRequestCreationOfACustomerProfile() throws Throwable {
        customerHelper.requestCreationOfACustomerProfile();
    }

    @Then("^the customer profile is created$")
    public void theCustomerProfileIsCreated() throws Throwable {
        customerHelper.getRegisterCustomerService().assertThat()
            .theCustomerProfileWasCreated(customerHelper.getRequest())
            .additionalInformationReturned("SVC_100047_2038");
    }

    @Given("^I have provided valid mandatory fields for \"([^\"]*)\" with the missing field \"([^\"]*)\"$")
    public void iHaveProvidedValidMandatoryFieldsForWithTheMissingField(String channel, String field) throws Throwable {
        customerHelper.customerRequestWithMissingField(channel, field);
    }

    @Then("^I will get a Invalid character error for \"([^\"]*)\"$")
    public void iWillGetAInvalidCharacterErrorFor(String error) throws Throwable {
        customerHelper.getRegisterCustomerService().assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @But("^the \"([^\"]*)\" is not valid because it contains \"(.*)\"$")
    public void theIsNotValidBecauseItContains(String field, String symbol) throws Throwable {
        customerHelper.setCustomerProfileFieldWithSymbol(field, symbol);
    }

    @Then("^I will get a customer creation error for the missing field \"([^\"]*)\"$")
    public void iWillGetACustomerCreationErrorForTheMissingField(String error) throws Throwable {
        customerHelper.getRegisterCustomerService().assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @Given("^an existing customer profile with known e-mail address$")
    public void anExistingCustomerProfileWithKnownEMailAddress() throws Throwable {
        customerHelper.customerAccountExistsWithAKnownEmail();
    }

    @When("^I request creation of a new customer profile with the same e-mail address$")
    public void iRequestCreationOfANewCustomerProfileWithTheSameEMailAddress() throws Throwable {
        customerHelper.creatNewCustomerProfileWithPeviouslyUsedEmail();
    }

    @Then("^an email registered validation error is returned$")
    public void anEmailRegisteredValidationErrorIsReturned() throws Throwable {
        customerHelper.getRegisterCustomerService().assertThatErrors().containedTheCorrectErrorMessage("SVC_100047_2031");
    }

    @But("^the \"([^\"]*)\" length is \"([^\"]*)\"$")
    public void theLengthIs(String field, int length) throws Throwable {
        customerHelper.customerRequestWithFieldAndFieldLength(field, length);
    }

    @But("^the email field is in an invalid format \"([^\"]*)\"$")
    public void theEmailFieldIsInAnInvalidFormat(String invalidEmail) throws Throwable {
        customerHelper.createNewCustomerProfileWithEmail(invalidEmail);
    }

    @Then("^an Invalid Email Format validation error is returned$")
    public void anInvalidEmailFormatValidationErrorIsReturned() throws Throwable {
        customerHelper.getRegisterCustomerService().assertThatErrors().containedTheCorrectErrorMessage("SVC_100047_2030");
    }

    @Then("^I will get a customer creation error for field length \"([^\"]*)\"$")
    public void iWillGetACustomerCreationErrorForFieldLength(String error) throws Throwable {
        customerHelper.getRegisterCustomerService().assertThatErrors().containedTheCorrectErrorMessage(error);
    }
}
