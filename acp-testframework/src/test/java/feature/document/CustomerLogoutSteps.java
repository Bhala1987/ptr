package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.CustomerHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.LogoutRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ProfileRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.CustomerLogoutService;
import com.hybris.easyjet.fixture.hybris.invoke.services.CustomerProfileService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ID;
import static com.hybris.easyjet.fixture.WaitHelper.pause;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.LOGOUT;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.PROFILE;

/**
 * Created by giuseppedimartino on 10/02/17.
 */
@ContextConfiguration(classes = TestApplication.class)

public class CustomerLogoutSteps {

    private CustomerLogoutService customerLogoutService;
    private CustomerProfileService customerProfileService;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private SerenityFacade testData;

    @Given("^I create a new customer$")
    public void iCreateANewCustomer() throws Throwable {
        customerHelper.customerAccountExistsWithAKnownPassword();
        testData.setData(CUSTOMER_ID, customerHelper.getRegisterCustomerService().getResponse().getRegistrationConfirmation().getCustomerId());
    }

    @When("^I send a request to the logout service$")
    public void iSendARequestToTheLogoutService() throws Throwable {
        CustomerPathParams params = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(LOGOUT).build();
        customerLogoutService = serviceFactory.logoutCustomer(new LogoutRequest(HybrisHeaders.getValid(testData.getChannel()).build(), params));
        customerLogoutService.invoke();
    }

    @Then("^I will end the Customer's active session$")
    public void iWillEndTheCustomerSActiveSession() throws Throwable {
        customerLogoutService.assertThat().customerProperlyLoggedOut();
    }

    @Given("^the customer already exist$")
    public void theCustomerExist() throws Throwable {
        iCreateANewCustomer();
        CustomerPathParams params = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(PROFILE).build();
        customerProfileService = serviceFactory.getCustomerProfile(new ProfileRequest(HybrisHeaders.getValid("Digital").build(), params));
        customerProfileService.invoke();
        customerProfileService.assertThat().theProfileIsValid(testData.getData(CUSTOMER_ID));
    }

    @Given("^the customer (\\d+) doesn't exist$")
    public void theCustomerDoesnTExist(String customer) throws Throwable {
        testData.setData(CUSTOMER_ID, customer);
        CustomerPathParams params = CustomerPathParams.builder().customerId(customer).path(PROFILE).build();
        customerProfileService = serviceFactory.getCustomerProfile(new ProfileRequest(HybrisHeaders.getValid("Digital").build(), params));
        customerProfileService.invoke();
        customerProfileService.assertThatErrors().containedTheCorrectErrorMessage("SVC_100041_1001");
    }

    @Then("^I return the error message (.*) to the channel$")
    public void iReturnTheErrorMessageToTheChannel(String error) throws Throwable {
        customerLogoutService.assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @When("^I make session to expire through (.*)$")
    public void iExpiresTheSessionThrough(String sessionEndDueTo) throws Throwable {
        if (sessionEndDueTo.equalsIgnoreCase("logout")) {
            CustomerPathParams params = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(LOGOUT).build();
            customerLogoutService = serviceFactory.logoutCustomer(new LogoutRequest(HybrisHeaders.getValid("Digital").build(), params));
            customerLogoutService.invoke();
            // test has to wait 90 seconds literally to wait for the session to expire to test this scenario
        } else if(sessionEndDueTo.equalsIgnoreCase("inactivity"))
            pause(120000);
        else throw new EasyjetCompromisedException(sessionEndDueTo + "Logout or Maximum inactivity are only allowed");
    }

    @When("^I logout$")
    public void iLogout() throws Throwable {
        iSendARequestToTheLogoutService();
    }


}