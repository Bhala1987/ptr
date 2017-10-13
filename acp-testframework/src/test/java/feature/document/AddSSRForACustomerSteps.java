package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.SpecialServiceRequestHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ProfileRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.CustomerProfileService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ID;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.PROFILE;

/**
 * Created by robertadigiorgio on 10/04/2017.
 */
@ContextConfiguration(classes = TestApplication.class)

public class AddSSRForACustomerSteps {
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private SpecialServiceRequestHelper ssrHelper;
    @Autowired
    private SerenityFacade testData;
    private CustomerProfileService customerProfileService;
    private int maxSSRsAllowedPerCustomer = 5;


    @When("^I send a request to (add|update) SSR with \"([^\"]*)\"$")
    public void iSendARequestDoAddSSRWith(String addOrUpdate, String channel) throws Throwable {
        ssrHelper.sendAddSSRRequestWithChannel(addOrUpdate, channel);
    }

    @And("^I (add|update) more than x SSR in the request \"([^\"]*)\" with \"([^\"]*)\"$")
    public void iAddMoreThanXSSRInTheRequestWith(String addOrUpdate, Integer numberOfRequest, String channel) throws Throwable {
        ssrHelper.iAddMoreThanXSSRInTheRequestWithChannel(addOrUpdate, numberOfRequest, channel);
    }

    @And("^I send the body request where T&C acceptance is null$")
    public void iSendTheBodyRequestWhereTCAcceptanceIsNull() throws Throwable {
        ssrHelper.sendSSrBodyRequestWhereTCAcceptanceIsNull();
    }

    @And("^I want to send a request to (add|update) SSR$")
    public void iWantToSendARequestToAddSSR(String addOrUpdate) throws Throwable {
        ssrHelper.sendAddWchcSSRRequest(addOrUpdate);
    }

    @And("^I (add|update) an SSR$")
    public void iAddAnSSR(String addOrUpdate) throws Throwable {
        ssrHelper.sendAddWchcSSRRequest(addOrUpdate);
    }

    @When("^I (add|update) the same SSR again$")
    public void iAddTheSSRAgain(String addOrUpdate) throws Throwable {
        ssrHelper.sendAddWchcSSRRequest(addOrUpdate);
    }

    @When("^I (add|update) an empty SSR block$")
    public void iAddAnEmptySSRBlock(String addOrUpdate) throws Throwable {
        ssrHelper.sendEmptySSRBlock(addOrUpdate);
    }

    @When("^I (add|update) an SSR \"([^\"]*)\" that is inaccessible from channel \"([^\"]*)\"$")
    public void iAddAnSsrThatIsInaccessibleFromChannel(String addOrUpdate, String ssr, String channel) throws Throwable {
        ssrHelper.sendSsrFromChannel(addOrUpdate, ssr, channel);
    }

    @And("^I (add|update) the maximum number of SSRs with \"([^\"]*)\"$")
    public void iAddTheMaximumNumberOfSSRsWith(String addOrUpdate, String channel) throws Throwable {
        ssrHelper.sendNumberOfSsrWithChannel(maxSSRsAllowedPerCustomer, addOrUpdate, channel);
    }

    @And("^I attempt to (update) more than the maximum number of SSRs allowed with \"([^\"]*)\"$")
    public void iAttemptToUpdateMoreThanTheMaximumNumberOfSSRsAllowedWith(String update, String channel) throws Throwable {
        ssrHelper.sendNumberOfSsrWithChannel(maxSSRsAllowedPerCustomer+1, update, channel);
    }

    @Then("^I will receive an SSR added confirmation message$")
    public void iWillReceiveAnSSRAddedConfirmationMessage() throws Throwable {
        Assert.assertTrue("SSR does not appear to have been added to customer", ssrHelper.ssrAddedSuccessfully());
    }

    @When("^I (add|update) an SSR \"([^\"]*)\"$")
    public void iAddAnSSR(String addOrUpdate, String ssrsToUpdate) throws Throwable {
        ssrHelper.addSSRFor(addOrUpdate, Arrays.asList(ssrsToUpdate.split("\\s*,\\s*")));
    }

    @Then("^I should see \"([^\"]*)\" added$")
    public void iShouldSeeAdded(String expectedSSrs) throws Throwable {
        Assert.assertTrue("SSR does not appear to have been added to customer", ssrHelper.ssrAddedSuccessfully());
        List<String> ssrs = Arrays.asList(expectedSSrs.split("\\s*,\\s*"));
        CustomerPathParams profilePathParams = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(PROFILE).build();
        customerProfileService = serviceFactory.getCustomerProfile(new ProfileRequest(HybrisHeaders.getValid("Digital").build(), profilePathParams, null));
        customerProfileService.invoke();
        customerProfileService.assertThat().numberOfSavedSsrsAre(ssrs.size());
        customerProfileService.assertThat().savedSSRsShouldCustmerHave(ssrs);
    }

    @Then("^I should see only \"([^\"]*)\" not the previous ones$")
    public void iShouldSeeOnlyssrs(String expectedSSrs) throws Throwable {
        iShouldSeeAdded(expectedSSrs);
    }
}