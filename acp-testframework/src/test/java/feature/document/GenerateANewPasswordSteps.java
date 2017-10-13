package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.database.hybris.dao.CustomerDao;
import com.hybris.easyjet.database.hybris.models.CustomerModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.CustomerHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.GeneratePasswordRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.GeneratePasswordRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation.GeneratePasswordResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.GeneratePasswordService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import org.awaitility.core.ConditionTimeoutException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.List;

import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.GENERATE_PASSWORD;

/**
 * Created by robertadigiorgio on 13/02/2017.
 */

@ContextConfiguration(classes = TestApplication.class)

public class GenerateANewPasswordSteps {
    private GeneratePasswordService generatePasswordService;
    private GeneratePasswordResponse generatePasswordResponse;

    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private CustomerDao customerDao;

    private String currentCustomerId;
    private String email;
    private String generatePassword;
    private String agentId;


    @Given("^There are customer Id into Database$")
    public void thereAreCustomerIdIntoDatabase() throws Throwable {

        List<CustomerModel> customerList = customerDao.returnValidCustomerWithShippingAddress();
        if (CollectionUtils.isEmpty(customerList)) {
            throw new EasyjetCompromisedException(EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INSUFFICIENT_DATA);
        }
        Collections.shuffle(customerList);
        CustomerModel firstCustomer = customerList.stream().findAny().get();

        //AgentId will change in the next sprints
        agentId = firstCustomer.getUid();
        currentCustomerId = firstCustomer.getUid();
        email = firstCustomer.getCustomerid();
    }

    @When("^I send a request to update password from \"([^\"]*)\"$")
    public void iSendARequestToUpdatePasswordFrom(String channel) throws Throwable {
        CustomerPathParams params = CustomerPathParams.builder().customerId(currentCustomerId).path(GENERATE_PASSWORD).build();
        GeneratePasswordRequestBody body = GeneratePasswordRequestBody.builder().agentId(agentId).build();
        generatePasswordService = serviceFactory.getGeneratePassword(new GeneratePasswordRequest(HybrisHeaders.getValid(channel).build(), params, body));
        generatePasswordService.invoke();
    }

    @And("^I will generate a new Medium Strength password$")
    public void iWillGenerateANewMediumStrengthPassword() throws Throwable {
        generatePasswordService.assertThat().passwordIsStrongEnough();
    }

    @Then("^I will store the new password against the profile$")
    public void iWillStoreTheNewPasswordAgainstTheProfile() throws Throwable {
        generatePassword = generatePasswordService.getResponse().getGeneratePasswordConfirmation().getPassword();

        try {
            pollingLoop().untilAsserted(() -> {
                customerHelper.loginWithValidCredentials(StringUtils.EMPTY, email, generatePassword, false);
                customerHelper.getLoginDetailsService().assertThat().theLoginWasSuccesful();
            });
        }catch(ConditionTimeoutException e){
            throw new EasyjetCompromisedException("Could not login with newly created password");
        }

    }

    @And("^I will remove any Saved APIS, Saved Payment methods, SSR and Saved Passengers details from the Customer's profile$")
    public void iWillRemoveAnySavedAPISSavedPaymentMethodsSSRAndSavedPassengersDetailsFromTheCustomerSProfile() throws Throwable {

        Integer paimentInfos = customerDao.getCustomersPaymentInfoWithId(currentCustomerId);
        generatePasswordService.assertThat().fieldIsEmpty(paimentInfos);

        Integer traveller = customerDao.getCustomersTravellerWithId(currentCustomerId);
        generatePasswordService.assertThat().fieldIsEmpty(traveller);

        Integer apisDetatil = customerDao.getCustomerAPISDetailWithId(currentCustomerId);
        generatePasswordService.assertThat().fieldIsEmpty(apisDetatil);

        String ssrDetail = customerDao.getCustomerSSRWithId(currentCustomerId).get(0);
        generatePasswordService.assertThat().fieldIsEmpty(ssrDetail);

    }

}
