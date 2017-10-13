package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.CustomerDao;
import com.hybris.easyjet.database.hybris.dao.MembershipDao;
import com.hybris.easyjet.database.hybris.models.CustomerModel;
import com.hybris.easyjet.database.hybris.models.MemberShipModel;
import com.hybris.easyjet.fixture.hybris.helpers.*;
import com.hybris.easyjet.fixture.hybris.invoke.services.DeleteCustomerSSRService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

/**
 * Created by markphipps on 27/03/2017.
 */
@ContextConfiguration(classes = TestApplication.class)
public class UpdateDependantsSteps {

    @Autowired
    private SavedPassengerHelper savedPassengerHelper;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private DependantsHelpers dependantsHelpers;
    @Autowired
    private StaffMembertoCustomerProfileAssociationHelper staffMembertoCustomerProfileAssociationHelper;
    @Autowired
    private SignificantOthersHelper significantOthersHelper;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private DeleteCustomerSSRService deleteCustomerSsrService;
    private String customerId;
    private CustomerModel localCustomer;
    private String email;
    private String accessToken;
    @Autowired
    private MembershipDao membershipDao;

    @Given("^the channel has initiated an updateDependents request$")
    public void theChannelHasInitiatedAnUpdateDependentsRequest() throws Throwable {
        dependantsHelpers.createDependantUpdateEjPlusRequest(null, null);
    }

    @Given("^the channel has initiated an update Dependents ejPlus request in an invalid format$")
    public void theChannelHasInitiatedAnUpdateDependentsEjPlusRequestInAnInvalidFormat() throws Throwable {
        dependantsHelpers.createDependantUpdateEjPlusRequestWithEjPlusParameter(null);
    }

    @When("^I receive the update Dependants request$")
    public void iReceiveTheRequest() throws Throwable {
        dependantsHelpers.processUpdateDependantEjPlusRequest();
    }

    @Then("^I will return an invalid request response$")
    public void iWillReturnAnInvalidRequestResponse() throws Throwable {
        dependantsHelpers.getUpdateDependantsService().assertThatErrors().containedTheCorrectErrorMessage("err");
    }

    @Given("^that I have received a valid update Dependents eJPlus request with ejPlus number \"([^\"]*)\"$")
    public void thatIHaveReceivedAValidUpdateDependentsEJPlusRequestWithEjPlusNumber(String ejPlusCardNumber) throws Throwable {
        dependantsHelpers.createDependantUpdateEjPlusRequestWithEjPlusParameter(ejPlusCardNumber);
    }

    @Given("^that I have received a valid update Dependents eJPlus request$")
    public void thatIHaveReceivedAValidUpdateDependentsEjPlusRequest() throws Throwable {
        dependantsHelpers.createDependantUpdateEjPlusRequest(null, null);
    }

    @Then("^I should add the validation error message \"([^\"]*)\" to the return message$")
    public void iShouldAddTheValidationErrorMessageToTheReturnMessage(String message) throws Throwable {
        dependantsHelpers.getUpdateDependantsService().assertThatErrors().containedTheCorrectErrorMessage(message);
    }

    @Then("^I should add the validation warning message \"([^\"]*)\" to the return message$")
    public void iShouldAddTheValidationWarningMessageToTheReturnMessage(String message) throws Throwable {
        dependantsHelpers.getUpdateDependantsService().assertThatErrors().containedTheCorrectErrorMessage(message);
    }

    @Then("^I should store the eJ Plus number for the Dependent Passenger$")
    public void iShouldStoreTheEJPlusNumberForTheDependentPassenger() throws Throwable {
        dependantsHelpers.getCustomerWithDependantId();
        dependantsHelpers.getPassengerId();
    }

    @And("^return confirmation message to the channel$")
    public void returnConfirmationMessageToTheChannel() throws Throwable {
        dependantsHelpers.getUpdateDependantsService().assertThat().dependantIsUpdatedForCustomer(dependantsHelpers.getCustomerWithDependantId());
    }

    @Given("^I have received a valid request to update a the ssrs for a Dependant to the Staff customer$")
    public void iHaveReceivedAValidRequestToUpdateATheSsrsForADependantToTheStaffCustomer() throws Throwable {
        significantOthersHelper.createSSRsUpdateRequestForDependant(false);
    }

    @When("^I validate if the threshold for the significant other has reached$")
    public void iValidateIfTheThresholdForTheSignificantOtherHasReached() throws Throwable {
        significantOthersHelper.processUpdateSignificantOthersRequest();
    }

    @Given("^I have received a valid request to add a document for a Dependant to the Staff customer$")
    public void iHaveReceivedAValidRequestToAddADocumentForADependantToTheStaffCustomer() throws Throwable {
        significantOthersHelper.createAddIdentityDocumentRequestForDependant();
        significantOthersHelper.processAddSignificantOthersDocumentRequest();
    }

    @When("^I validate the document number$")
    public void iValidateTheDocumentNumber() throws Throwable {
        significantOthersHelper.processAddSignificantOthersDocumentRequest();
    }

    @Given("^I have received a valid request to update a document for a Dependant to the Staff customer$")
    public void iHaveReceivedAValidRequestToUpdateADocumentForADependantToTheStaffCustomer() throws Throwable {
        significantOthersHelper.setCustomerWithSignificantOtherId(dependantsHelpers.getCustomerWithDependantId());
        significantOthersHelper.setPassengerId(dependantsHelpers.getPassengerId());
        significantOthersHelper.createUpdateIdentityDocumentRequest();
    }

    @When("^Ts and Cs are not accepted$")
    public void tsAndCsAreNotAccepted() throws Throwable {
        significantOthersHelper.createUpdateRequestWithAllSSrsHavingFalseTsCsFlag();
    }

    @And("^I have received a valid update Identity Document request for the Dependant$")
    public void iHaveReceivedAValidUpdateIdentityDocumentRequestForTheDependant() throws Throwable {
        significantOthersHelper.createUpdateIdentityDocumentRequestForDependant();
    }

    @When("^I process the request$")
    public void iProcessTheRequest() throws Throwable {
        significantOthersHelper.processUpdateSignificantOthersRequest();
    }

    @Given("^that I have received a valid update Dependents eJPlus request for \"([^\"]*)\" with ejPlus number \"([^\"]*)\"$")
    public void thatIHaveReceivedAValidUpdateDependentsEJPlusRequestForWithEjPlusNumber(String dependant, String eJPlus) throws Throwable {
        dependantsHelpers.createDependantUpdateForDependantEjPlusRequestWithEjPlusParameter(dependant, eJPlus);
    }

    @Given("^I have customer who is also a staff member$")
    public void iHaveCustomerWhoIsAlsoAStaffMember() throws Throwable {
        iCreateAStaffCustomerAndLogIn(dependantsHelpers.getCustomerWithDependantId());
    }

    public void iCreateAStaffCustomerAndLogIn(String customer) throws Throwable {
        customerId = customer;
        List<CustomerModel> customers = getAllCustomers();
        savedPassengerHelper.addValidPassengerToExistingCustomerWithPassword(customerId);
        getCustomerDetail(customer, customers);
        staffMembertoCustomerProfileAssociationHelper.associateCustomerProfileWithStaffMemberFromId(customerId, false, localCustomer);
        customerHelper.loginWithValidCredentials("Digital", email, "p0rtalT3cH", false);
        accessToken = customerHelper.getLoginDetailsService().getResponse().getAuthenticationConfirmation().getAuthentication().getAccessToken();
    }

    public List<CustomerModel> getAllCustomers() {
        return customerDao.getAllCustomers();
    }

    private void getCustomerDetail(String id, List<CustomerModel> customers) {
        for (CustomerModel customer : customers) {
            if (customer.getUid().equals(id)) {
                localCustomer = customer;
                email = customer.getCustomerid();
            }
        }
    }

    @Given("^I have received a valid request to update a the ssrs for a Dependant to the Staff customer with terms not accepted$")
    public void iHaveReceivedAValidRequestToUpdateATheSsrsForADependantToTheStaffCustomerWithTermsNotAccepted() throws Throwable {
        significantOthersHelper.createSSRsUpdateRequestForDependant(null);
    }

    @Given("^the channel has initiated an update Dependents ejPlus request with invalid customer$")
    public void theChannelHasInitiatedAnUpdateDependentsEjPlusRequestWithInvalidCustomer() throws Throwable {
        dependantsHelpers.createDependantUpdateEjPlusRequest("000", null);
    }

    @Given("^the channel has initiated an update Dependents ejPlus request with invalid dependant$")
    public void theChannelHasInitiatedAnUpdateDependentsEjPlusRequestWithInvalidDependant() throws Throwable {
        dependantsHelpers.setDependantId("000");
        dependantsHelpers.createDependantUpdateEjPlusRequest(null, dependantsHelpers.getPassengerId());
    }

    @Given("^I sent update Dependents eJPlus request for customer \"([^\"]*)\" and dependant \"([^\"]*)\" with ejPlus number \"([^\"]*)\"$")
    public void iSentUpdateDependenteJPlus(String customer, String dependant, String ejPlus) throws Throwable {
        customerHelper.loginWithValidCredentials(testData.getChannel(), "a.rossi@reply.co.uk", "1234", false);
        dependantsHelpers.createDependantUpdateForDependantEjPlusRequestWithEjPlusParameter(customer, dependant, ejPlus);
    }
    @Given("^I have valid update dependents request with ejPlus number status other than (.*)$")
    public void createValidRequestToUpdateDependant(String ejPlusStatus) throws Throwable {
        MemberShipModel staffOtherThanStatus = membershipDao.getValidEJPlusMembershipForStaffOtherThanStatus(ejPlusStatus);
        dependantsHelpers.createUpdateDependantRequestWithEJPlusStatus(staffOtherThanStatus.getEjMemberShipNumber());
    }

    @Then("^I receive dependant update confirmation$")
    public void iReceiveDependantUpdateConfirmation() throws Throwable {
        Assert.assertTrue("Dependant has not been successfully updated", dependantsHelpers.updateDependantWasSuccessful());
    }

    @Then("^I will receive updated dependant ssr confirmation$")
    public void iWillReceiveUpdatedDependantSsrConfirmation() throws Throwable {
        System.out.println("ID IS " + significantOthersHelper.getSignificantOtherService().getResponse().getUpdateConfirmation().getCustomerId());
    }

    @Given("^I have added an SSR to a dependant$")
    public void iHaveAddedAnSSRToADependant() throws Throwable {
        significantOthersHelper.createSSRsUpdateRequestForDependant(true);
        significantOthersHelper.processUpdateSignificantOthersRequest();
    }

    @When("^I remove SSR from dependant$")
    public void iRemoveSSRFromDependant() throws Throwable {
        significantOthersHelper.createSSRsDeleteRequestForDependant(true);
        significantOthersHelper.processUpdateSignificantOthersRequest();
    }

    @Then("^I receive confirmation that the dependant has been updated$")
    public void iReceiveConfirmationThatTheDependantHasBeenUpdated() throws Throwable {
        Assert.assertTrue(significantOthersHelper.getSignificantOtherUpdateConfirmation());
    }

    @When("^I receive a change to the DoB in the document to a value not matching their type$")
    public void iReceiveAChangeToTheDoBInTheDocumentToAValueNotMatchingTheirType() throws Throwable {
        significantOthersHelper.createUpdateIdentityDocumentRequestForDependant();
        significantOthersHelper.changeDocumentDateOfBirth(SignificantOthersHelper.SignificantOthersServiceType.UPDATE);
    }

    @When("^I raise a request to delete document from Dependant$")
    public void iRaiseARequestToDeleteDocumentFromDependant() throws Throwable {
        significantOthersHelper.createDeleteDependantDocumentRequest();
    }

    @Then("^I return document has been removed confirmation$")
    public void iReturnDocumentHasBeenRemovedConfirmation() throws Throwable {
        Assert.assertTrue(significantOthersHelper.getSignificantOtherDocumentUpdateConfirmation());
    }
}

