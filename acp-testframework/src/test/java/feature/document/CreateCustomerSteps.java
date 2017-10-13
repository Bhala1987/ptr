package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.CustomerHelper;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.registercustomer.RegisterCustomerRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.RegisterCustomerFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.CustomerProfileService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.IdentifyCustomerService;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;

/**
 * Created by giuseppedimartino on 26/04/17.
 */

@ContextConfiguration(classes = TestApplication.class)
public class CreateCustomerSteps {


    RegisterCustomerRequestBody registerCustomerRequest;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    private IdentifyCustomerService identifyCustomerService;

    @Given("^I have created a new customer$")
    public void iCreateACustomer() throws Throwable {
        customerHelper.createRandomCustomer(testData.getChannel());
        testData.setData(CUSTOMER_ID, customerHelper.getRegisterCustomerService().getResponse().getRegistrationConfirmation().getCustomerId());
        testData.setAccessToken(customerHelper.getRegisterCustomerService().getResponse().getRegistrationConfirmation().getAuthentication().getAccessToken());
    }

    @When("^I request for create customer profile$")
    public void iRequestForCreateCustomerProfile() throws Throwable {
        customerHelper.createCustomerProfileFromRequest(testData.getData(REGISTER_CUSTOMER_REQUEST));
    }

    @And("^I have valid request to create customer$")
    public void iHaveValidRequestToCreateCustomer() throws Throwable {
        registerCustomerRequest = RegisterCustomerFactory.aDigitalProfile();
        testData.setData(REGISTER_CUSTOMER_REQUEST, registerCustomerRequest);
    }

    @Then("^I should get the warning with code as (.*)$")
    public void iShouldGetTheWaringWithCodeAsSVC(String code) throws Throwable {
        customerHelper.getRegisterCustomerService().assertThat().additionalInformationContains(code);
    }

    @And("^customer profile is created successfully$")
    public void theCustomerProfileIsCreatedSuccessFully() throws Throwable {
        customerHelper.getRegisterCustomerService().assertThat().additionalInformationReturned("SVC_100047_2038");
        testData.setData(CUSTOMER_ID, customerHelper.getRegisterCustomerService().getResponse().getConfirmation().getCustomerId());
    }

    @Then("^I should get the error with code as (.*)$")
    public void iShouldGetTheErrorWithCodeAsSVC(String code) throws Throwable {
        customerHelper.getRegisterCustomerService().assertThatErrors().containedTheCorrectErrorMessage(code);
    }

    @And("^I request the customer profile$")
    public void iSentARequestToGetCustomerProfile() throws Throwable {
        customerHelper.getCustomerProfile(testData.getData(CUSTOMER_ID));
        testData.setData(GET_CUSTOMER_PROFILE_SERVICE, testData.getData(SERVICE));
    }

    @And("^I login as newly created customer$")
    public void iLoginAsNewlyCreatedCustomer() throws Throwable {
        if (testData.getChannel().equalsIgnoreCase("Digital") || testData.getChannel().equalsIgnoreCase("PublicApiMobile")) {
            RegisterCustomerRequestBody registerCustomerRequestBody = testData.getData(REGISTER_CUSTOMER_REQUEST);
            String email = registerCustomerRequestBody.getPersonalDetails().getEmail();
            String password = registerCustomerRequestBody.getPersonalDetails().getPassword();
            customerHelper.loginWithValidCredentials(testData.getChannel(), email, password, false);
        }

    }

    @Given("^I request for create temporary customer profile$")
    public void iRequestForCreateTemporaryCustomerProfile() throws Throwable {
        customerHelper.createTemporaryCustomerProfile();
    }

    @When("^search for temporary registered customer using firstname and lastname$")
    public void searchForTemporaryRegisteredCustomerUsingFirstnameAndLastname() throws Throwable {
        customerHelper.searchForTemporaryCustomer();
    }

    @Then("^Identify Customer service returns (.*)$")
    public void identifyCustomerServiceReturnsErrorCode(String errorCode) throws Throwable {
        identifyCustomerService =   testData.getData(IDENTIFY_CUSTOMER_SERVICE);
        identifyCustomerService.assertThat().noResultIsFound(errorCode);
    }

    @When("^I request the temporary customer profile$")
    public void iRequestTheCustomerProfile() {
        try{
            customerHelper.getCustomerProfile(testData.getData(CUSTOMER_ID));
        } catch (AssertionError error){
            handleError(error);
        }
    }

    @Then("^the temporary customer should be removed$")
    public void theTemporaryCustomerShouldBeRemoved() {
        customerHelper.getCustomerProfile(testData.getData(CUSTOMER_ID));

        CustomerProfileService customerProfileService = testData.getData(SERVICE);
        customerProfileService.assertThatErrors().containedTheCorrectErrorMessage("SVC_100000_2086");
    }

    private void handleError(AssertionError errorMessage){
        if(!errorMessage.getMessage().contains("SVC_100000_2069")){
            Assert.fail(" Operation should not be allowed for user ::" + testData.getData(CUSTOMER_ID));
        }
    }
}
