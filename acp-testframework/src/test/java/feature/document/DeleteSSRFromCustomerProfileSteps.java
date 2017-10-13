package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.CustomerHelper;
import com.hybris.easyjet.fixture.hybris.helpers.SpecialServiceRequestHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.DeleteCustomerSSRRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.DeleteCustomerSSRService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.DELETE_SSR_CUSTOMER;

/**
 * Created by markphipps on 26/04/2017.
 */

@ContextConfiguration(classes = TestApplication.class)
public class DeleteSSRFromCustomerProfileSteps {
    @Autowired
    private SerenityFacade testData;

    private GetCustomerProfileSteps customerProfileSteps;

    @Autowired
    private SpecialServiceRequestHelper ssrHelper;

    private List<String> ssrList = new ArrayList<>();

    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Autowired
    private CustomerHelper customerHelper;

    private DeleteCustomerSSRService deleteCustomerSsrService;
    private int maxSSRsAllowedPerCustomer = 5;

    @Given("^I have a customer with saved SSRs$")
    public void iHaveACustomerWithSavedSSRs() throws Throwable {
        customerHelper.customerAccountExistsWithAKnownPassword();
        testData.setData(CUSTOMER_ID, customerHelper.getRegisterCustomerService().getResponse().getRegistrationConfirmation().getCustomerId());
        ssrHelper.sendNumberOfSsrWithChannel(maxSSRsAllowedPerCustomer, "add",testData.getChannel());
    }

    @Given("^that the channel creates a request to delete a single SSR from a customer profile$")
    public void thatTheCreatesARequestToDeleteASingleSSRFromACustomerProfile() throws Throwable {
        CustomerPathParams pathParams = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(DELETE_SSR_CUSTOMER).documentId("BLND").build();
        deleteCustomerSsrService = serviceFactory.deleteCustomerSSRService(new DeleteCustomerSSRRequest(HybrisHeaders.getValid(testData.getChannel()).build(),pathParams));
    }

    @Given("^the channel has initiated an invalid delete SSR request$")
    public void theChannelHasInitiatedAnInvalidDeleteSSRRequest() throws Throwable {
        CustomerPathParams pathParams = CustomerPathParams.builder().customerId("invalid").path(DELETE_SSR_CUSTOMER).documentId("BLND").build();
        deleteCustomerSsrService = serviceFactory.deleteCustomerSSRService(new DeleteCustomerSSRRequest(HybrisHeaders.getValid(testData.getChannel()).build(),pathParams));
    }

    @When("^the delete SSR request is sent$")
    public void theDeleteSSRRequestIsSent() throws Throwable {
        deleteCustomerSsrService.invoke();
        testData.setData(SERVICE, deleteCustomerSsrService);
    }

    @Given("^that the channel creates a request to delete all SSRs from a customer profile$")
    public void thatTheCreatesARequestToDeleteAllSSRsFromACustomerProfile() throws Throwable {
        CustomerPathParams pathParams = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(DELETE_SSR_CUSTOMER).documentId("").build();
        deleteCustomerSsrService = serviceFactory.deleteCustomerSSRService(new DeleteCustomerSSRRequest(HybrisHeaders.getValid(testData.getChannel()).build(),pathParams));
    }

    @When("^the channel attempts to delete all SSRs again$")
    public void theChannelAttemptsToDeleteAllSSRsAgain() throws Throwable {
        thatTheCreatesARequestToDeleteAllSSRsFromACustomerProfile();
        theDeleteSSRRequestIsSent();
        testData.setData(SERVICE, deleteCustomerSsrService);
    }

    @When("^the channel attempts to remove a single SSR$")
    public void theChannelAttemptsToRemoveASingleSSR() throws Throwable {
        thatTheCreatesARequestToDeleteASingleSSRFromACustomerProfile();
        theDeleteSSRRequestIsSent();
        testData.setData(SERVICE, deleteCustomerSsrService);
    }

    @Then("^SSR delete confirmation is returned to the channel$")
    public void ssrDeleteConfirmationIsReturnedToTheChannel() throws Throwable {
        Assert.assertTrue("SSR does not appear to have been delete from customer", deleteCustomerSsrService.getResponse().getUpdateConfirmation().getCustomerId().equalsIgnoreCase(testData.getData(CUSTOMER_ID)));
    }
}
