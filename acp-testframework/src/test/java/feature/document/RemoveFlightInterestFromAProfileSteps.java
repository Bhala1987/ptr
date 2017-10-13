package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.FlightInterestHelper;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ID;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 01/08/2017.
 */

@ContextConfiguration(classes = TestApplication.class)

public class RemoveFlightInterestFromAProfileSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    FlightInterestHelper flightInterestHelper;


    @When("^I send an removeFlightInterest with (.*) customer ID, (.*) flight key, (.*) bundle, (.*) login for (.*) flights$")
    public void iSendAnRemoveFlightInterestWithCustomerIdFlightKeyBundleLoginForFlights(String customerID, String flightKey, String bundle, String login, int numFlights) throws Throwable {
        flightInterestHelper.removeFlightInterest(customerID, flightKey, bundle, login, numFlights);
    }

    @Then("^I receive forbidden error$")
    public void iReceiveForbiddenError() throws Throwable {
        assertThat(flightInterestHelper.removeFlightInterestService().getStatusCode()).isEqualTo(403);
    }

    @Then("^I should get successful removal of flight interest response$")
    public void iShouldGetSuccessfulRemovalOfFlightInterestResponse() throws Throwable {
        flightInterestHelper.removeFlightInterestService().assertThat().customerIdIsEqualTo(testData.getData(CUSTOMER_ID));
    }

    @And("^I should not see the flight interest in the getFlight interest$")
    public void iShouldNotSeeTheFlightInterestInTheGetFlightInterest() throws Throwable {
        flightInterestHelper.getFlightInterestServiceAfterRemoval().assertThat().noFlightInterestsInGetFlightInterests();
    }

    @And("^I should not see the flight interest in the profile$")
    public void iShouldNotSeeTheFlightInterestInTheProfile() throws Throwable {
        flightInterestHelper.customerProfileService().assertThat().noFlightInterestsInCustomerProfile();
    }

    @Then("^I should receive an error with code (.*)$")
    public void iShouldReceiveAnErrorWithCode(String error) throws Throwable {
        flightInterestHelper.removeFlightInterestService().assertThat().customerIdIsEqualTo(testData.getData(CUSTOMER_ID));
        flightInterestHelper.removeFlightInterestService().assertThat().additionalInformationContains(error);
    }
}





