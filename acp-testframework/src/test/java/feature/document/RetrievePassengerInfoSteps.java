package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.CustomerDao;
import com.hybris.easyjet.database.hybris.models.CustomerModel;
import com.hybris.easyjet.fixture.hybris.helpers.*;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.UpdatePasswordRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.DependantsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.GetSignificantOtherRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.UpdatePasswordRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.savedpassenger.SavedPassengerRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.*;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ID;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.*;

/**
 * Created by markphipps on 04/04/2017.
 */
@ContextConfiguration(classes = TestApplication.class)
public class RetrievePassengerInfoSteps {
    @Autowired
    private SavedPassengerHelper savedPassengerHelper;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private SignificantOthersHelper significantOthersHelper;
    @Autowired
    private AccountPasswordHelper accountPasswordHelper;
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private SerenityFacade testData;

    private GetSavedPassengerService savedPassengerService;
    private GetDependantsService dependantsService;
    private SignificantOtherService significantOthersService;
    private UpdatePasswordRequestBody bodyRequestUpdatePassword;
    private UpdatePasswordService updatePasswordService;
    private String customerId = "cus00000001";
    private String email;
    private String accessToken;
    @Autowired
    private StaffMembertoCustomerProfileAssociationHelper staffMembertoCustomerProfileAssociationHelper;
    private CustomerModel localCustomer;

    @Given("^that a customer has saved passenger information$")
    public void aCustomerHasSavedPassengerInformation() throws Throwable {
        savedPassengerHelper.addValidPassengerToExistingCustomerWithPassword(testData.getData(CUSTOMER_ID));
    }

    @Given("^that an unauth customer has saved passenger information$")
    public void anUnAuthCustomerHasSavedPassengerInformation() throws Throwable {
        savedPassengerHelper.addValidPassengerToExistingCustomer();
        customerId = savedPassengerHelper.getCustomerId();
    }

    @And("^the customer is not logged in$")
    public void theCustomerIsNotLoggedIn() throws Throwable {
        customerId = savedPassengerHelper.getCustomerId();
    }

    @Given("^the customer is logged in$")
    public void iTheCustomerIsLoggedIn() throws Throwable {
        List<CustomerModel> customers = savedPassengerHelper.getAllCustomers();
        if (customerId.isEmpty()) {
            aCustomerHasSavedPassengerInformation();
        }
        getCustomerDetail(customerId, customers);

        customerHelper.loginWithValidCredentials(testData.getChannel(), testData.getEmail(), "p0rtalT3cH", false);
        accessToken = customerHelper.getLoginDetailsService().getResponse().getAuthenticationConfirmation().getAuthentication().getAccessToken();
    }

    @Given("^that a staff Customer is logged in$")
    public void iCreateAStaffCustomerAndLogIn() throws Throwable {
        List<CustomerModel> customers = savedPassengerHelper.getAllCustomers();
        aCustomerHasSavedPassengerInformation();
        getCustomerDetail(customerId, customers);
        staffMembertoCustomerProfileAssociationHelper.associateCustomerProfileWithStaffMemberFromId(customerId, false, localCustomer);
        customerHelper.loginWithValidCredentials("Digital", email, "p0rtalT3cH", false);
        accessToken = customerHelper.getLoginDetailsService().getResponse().getAuthenticationConfirmation().getAuthentication().getAccessToken();
    }

    @Given("^that the \"([^\"]*)\" has initiated a request to getSavedPassenger with accessToken$")
    public void thatTheHasInitiatedAuthenticatedRequestToGetSavedPassenger(String channel) throws Throwable {
        CustomerPathParams savedPassengerParams = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(GET_SAVED_PASSENGER).build();
        savedPassengerService = serviceFactory.getSavedPassenger(new SavedPassengerRequest(HybrisHeaders.getValidWithToken(channel, accessToken).build(), savedPassengerParams, "GET"));
    }

    @Given("^that the \"([^\"]*)\" has initiated a request to getSavedPassenger with no access token$")
    public void thatTheHasInitiatedAuthenticatedRequestToGetSavedPassengerWithoutToken(String channel) throws Throwable {
        CustomerPathParams savedPassengerParams = CustomerPathParams.builder().customerId(customerId).path(GET_SAVED_PASSENGER).build();
        savedPassengerService = serviceFactory.getSavedPassenger(new SavedPassengerRequest(HybrisHeaders.getValid(channel).build(), savedPassengerParams, "GET"));
    }

    @Given("^that the \"([^\"]*)\" has initiated an authenticated request to getDependants$")
    public void thatTheHasInitiatedARequestToGetDependents(String channel) throws Throwable {
        CustomerPathParams dependantParams = CustomerPathParams.builder().customerId(customerId).path(DEPENDANTS_SERVICE_PATH).build();
        dependantsService = serviceFactory.getDependantsService(new DependantsRequest(HybrisHeaders.getValidWithToken(channel, accessToken).build(), dependantParams));
    }

    @When("^I receive an authenticated request to getDependents from the channel$")
    public void iReceiveARequestToGetDependentsFromTheChannel() throws Throwable {
        dependantsService.invoke();
    }

    @When("^I receive an authenticated request to getSavedPassenger from the channel$")
    public void iReceiveARequestToGetSavedPassengerFromTheChannel() throws Throwable {
        savedPassengerService.invoke();
    }

    @When("^I receive an unauthenticated request to getSavedPassenger from the channel$")
    public void iReceiveAnUnauthRequestToGetSavedPassengerFromTheChannel() throws Throwable {
        savedPassengerService.invoke();
    }

    @Then("^I will get the saved passenger for the customer to the channel$")
    public void iWillReceiveTheSavedPassengerForTheCustomerToTheChannel() throws Throwable {
        savedPassengerService.assertThat().savedPassengerDetailsAreReturned();
    }

    @Then("^I will return a list of Dependents associated to the authenticated customer$")
    public void iWillReturnAListOfDependentsAssociatedToTheCustomer() throws Throwable {
        Assert.assertFalse(dependantsService.getResponse().toString().isEmpty());
    }

    private void getCustomerDetail(String id, List<CustomerModel> customers) {
        for (CustomerModel customer : customers) {
            if (customer.getUid().equals(id)) {
                localCustomer = customer;
                email = customer.getCustomerid();
            }
        }
    }

    private void updatePassword(String channel, String newPassword) {
        CustomerPathParams params = CustomerPathParams.builder().customerId(customerId).path(PASSWORD).build();
        bodyRequestUpdatePassword = UpdatePasswordRequestBody.builder().currentPassword(null)
                .newPassword(newPassword).build();
        updatePasswordService = serviceFactory
                .getUpdatePassword(new UpdatePasswordRequest(HybrisHeaders.getValid(channel).build(), params, bodyRequestUpdatePassword));
        updatePasswordService.invoke();
    }

    @And("^the staff customer has significant others$")
    public void theStaffCustomerHasSignificantOthers() throws Throwable {
        anUnAuthCustomerHasSavedPassengerInformation();
        significantOthersHelper.createAddRequest(customerId);
        significantOthersHelper.processAddSignificantOthersRequest();
    }

    @Given("^that the \"([^\"]*)\" has initiated an authenticated request to getSignificantOthers$")
    public void thatTheHasInitiatedARequestToGetSignificantOthers(String channel) throws Throwable {
        CustomerPathParams params = CustomerPathParams.builder().customerId(customerId).path(GET_SIGNIFICANT_OTHER).build();
        significantOthersService = serviceFactory.getSignificantOtherService(new GetSignificantOtherRequest(HybrisHeaders.getValidWithToken(channel, accessToken).build(), params));
    }

    @Given("^the \"([^\"]*)\" has initiated an unauthenticated request to getSignificantOthers$")
    public void thatTheHasInitiatedAnUnAuthRequestToGetSignificantOthers(String channel) throws Throwable {
        CustomerPathParams params = CustomerPathParams.builder().customerId(customerId).path(GET_SIGNIFICANT_OTHER).build();
        significantOthersService = serviceFactory.getSignificantOtherService(new GetSignificantOtherRequest(HybrisHeaders.getValid(channel).build(), params));
    }

    @When("^I receive a request to getSignificantOthers from the channel$")
    public void iReceiveARequestToGetSignificantOthersFromTheChannel() throws Throwable {
        significantOthersService.invoke();
    }

    @Then("^I will return a list of Significant associated to the customer$")
    public void iWillReturnAListOfSignificantAssociatedToTheCustomer() throws Throwable {
        Assert.assertFalse(significantOthersService.getResponse().toString().isEmpty());
    }

    @Then("^I should add error message \"([^\"]*)\" to the SavedPassenger return message$")
    public void iShouldAddErrorMessageToTheSavedPassengerReturnMessage(String error) throws Throwable {
        savedPassengerService.assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @Then("^I should add error message \"([^\"]*)\" to the Significant Others return message$")
    public void iShouldAddErrorMessageToTheSignificantOthersReturnMessage(String error) throws Throwable {
        significantOthersService.assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @Given("^I create a customer and change their password$")
    public void iCreateACustomerAndChangeTheirPassword() {
        accountPasswordHelper.createNewAccountForCustomerAndLoginIt();
        accountPasswordHelper.updatePassword(testData.getChannel(), "p0rtalT3cH");
    }
}
