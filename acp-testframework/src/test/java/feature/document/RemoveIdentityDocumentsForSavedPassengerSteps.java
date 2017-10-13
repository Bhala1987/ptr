package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.fixture.hybris.helpers.SavedPassengerHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.savedpassenger.SavedPassengerRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.UpdateIdentityDocumentService;
import com.hybris.easyjet.fixture.hybris.invoke.services.UpdateSavedPassengerService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.DELETE_ALL_IDENTITY_DOCUMENTS;

/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 05/04/2017.
 */
@ContextConfiguration(classes = TestApplication.class)

public class RemoveIdentityDocumentsForSavedPassengerSteps {

    protected static Logger LOG = LogManager.getLogger(RemoveIdentityDocumentsForSavedPassengerSteps.class);

    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private SavedPassengerHelper savedPassengerHelper;
    private UpdateSavedPassengerService updateSavedPassenger;
    private UpdateIdentityDocumentService updateIdentityDocumentService;
    private CustomerPathParams customerPathParams;

    private String channel;
    private String customerId;
    private String passengerId;


    private void main(int n) throws Throwable {
        savedPassengerHelper.addValidPassengerToExistingCustomer();
        if (n == 0) {
            savedPassengerHelper.addValidIdentityDocumentToToExistingPassenger();
        } else {
            savedPassengerHelper.addValidIdentityDocumentToToExistingPassenger(n);
        }
        updateIdentityDocumentService = (UpdateIdentityDocumentService) savedPassengerHelper.getSavedPassengerSevice();
        updateIdentityDocumentService.assertThat().verifySuccessfullyUpdateForIdentityDocument(savedPassengerHelper.getCustomerId(), savedPassengerHelper.getPassengerId(), savedPassengerHelper.getDocumentId());


        customerId = savedPassengerHelper.getCustomerId();
        passengerId = savedPassengerHelper.getPassengerId();
    }

    private void body() throws Throwable {
        customerPathParams = CustomerPathParams.builder().customerId(customerId).passengerId(passengerId).path(DELETE_ALL_IDENTITY_DOCUMENTS).build();
    }

    private void tail() throws Throwable {
        updateSavedPassenger = serviceFactory.updateSavedPassenger(new SavedPassengerRequest(HybrisHeaders.getValid(channel).build(), customerPathParams));
        updateSavedPassenger.invoke();
    }

    @Given("^am using Channel \"([^\"]*)\" for remove all identity documents of the saved passenger$")
    public void amUsingChannelChannelForRemoveAllIdentityDocumentsOfTheSavedPassenger(String channel) throws Throwable {
        this.channel = channel;
    }

    @And("^I have (\\d+) identity documents added to the saved passenger$")
    public void iHaveNumberOfDocsIdentityDocumentsAddedToTheSavedPassenger(int n) throws Throwable {
        main(n);
    }

    @And("^I have (\\d+) identity document added to the saved passenger$")
    public void iHaveIdentityDocumentAddedToTheSavedPassenger(int arg0) throws Throwable {
        main(0);
    }

    @When("^I receive the updateSavedPassengers request to remove all identity documents of the saved passenger with invalid \"([^\"]*)\"$")
    public void iReceiveTheUpdateSavedPassengersRequestToRemoveAllIdentityDocumentsOfTheSavedPassengerWithInvalidParameter(String parameter) throws Throwable {
        main(0);
        if (parameter.equalsIgnoreCase("CustomerID")) {
            customerPathParams = CustomerPathParams.builder().customerId("invalid").passengerId(passengerId).path(DELETE_ALL_IDENTITY_DOCUMENTS).build();
        } else if (parameter.equalsIgnoreCase("PassengerID")) {
            customerPathParams = CustomerPathParams.builder().customerId(customerId).passengerId("invalid").path(DELETE_ALL_IDENTITY_DOCUMENTS).build();
        }
        tail();
    }

    @When("^I receive the updateSavedPassengers request to remove all identity documents of the saved passenger$")
    public void iReceiveTheUpdateSavedPassengersRequestToRemoveAllIdentityDocumentsOfTheSavedPassenger() throws Throwable {
        body();
        tail();
    }

    @Then("^I will return the \"([^\"]*)\" for removal of all identity documents of the saved passenger$")
    public void iWillReturnTheForRemovalOfAllIdentityDocumentsOfTheSavedPassenger(String errorCode) throws Throwable {
        updateSavedPassenger.assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }

    @Then("^I return confirmation on completion of removal of all identity documents of the saved passenger$")
    public void iReturnConfirmationOnCompletionOfRemovalOfAllIdentityDocumentsOfTheSavedPassenger() throws Throwable {
        updateSavedPassenger.assertThat().verifySuccessfullyRemoveAllIdentityDocs(customerId, passengerId);
    }
}
