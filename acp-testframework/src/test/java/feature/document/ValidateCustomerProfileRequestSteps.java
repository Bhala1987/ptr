package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.fixture.hybris.helpers.CustomerHelper;
import com.hybris.easyjet.fixture.hybris.helpers.SavedPassengerHelper;
import cucumber.api.java.en.And;
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

public class ValidateCustomerProfileRequestSteps {

    protected static Logger LOG = LogManager.getLogger(ValidateCustomerProfileRequestSteps.class);
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    SavedPassengerHelper savedPassengerHelper;

    @Given("^a valid request to create customer profile$")
    public void aValidRequestToCreateACustomerProfile() throws Throwable {
        customerHelper.aValidRequestToCreateAProfileForCustomer();
    }

    @When("^the passenger title is empty$")
    public void the_passenger_title_is_empty() throws Throwable {
        customerHelper.customerRequestWithMissingField("Digital", "title");
    }

    @Then("^a \"([^\"]*)\" error is returned as expected$")
    public void a_error_is_returned_as_expected(String errorMessage) throws Throwable {
        customerHelper.getRegisterCustomerService().assertThatErrors().containedTheCorrectErrorMessage(errorMessage);
    }

    @When("^the opted out marketing options are empty$")
    public void theOptedOutMarketingOptionsAreEmpty() throws Throwable {
        customerHelper.customerRequestWithMissingField("Digital", "optedOutMarketing");
    }

    @Then("^passenger \"([^\"]*)\" has their details added to customer profile$")
    public void passengerHasTheirDetailsAddedToCustomerProfile(String surname) throws Throwable {
        savedPassengerHelper.getAllSavedPassenger(surname);
    }

    @And("^the response should not contain authentication details$")
    public void theResponseShouldNotContainAuthenticationDetails() throws Throwable {
        customerHelper.getRegisterCustomerService().assertThat().authenticationNotReturned();
    }
}