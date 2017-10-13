package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.database.hybris.models.CustomerModel;
import com.hybris.easyjet.fixture.hybris.helpers.CustomerProfileHelper;
import com.hybris.easyjet.fixture.hybris.helpers.IdentifyCustomerHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.IdentifyCustomerQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.IdentifyCustomerRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.IdentifyCustomerService;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;

/**
 * Created by dwebb on 12/5/2016.
 */
@ContextConfiguration(classes = TestApplication.class)

public class IdentifyCustomerSteps {

    private IdentifyCustomerService identifyCustomerService;

    private CustomerModel dbCustomer;
    @Autowired
    private CustomerProfileHelper customerProfileHelper;
    @Autowired
    private IdentifyCustomerHelper identifyCustomerHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Given("^a customer with valid data exists$")
    public void aCustomerWithValidDataExists() throws Throwable {
        dbCustomer = customerProfileHelper.findAValidCustomerProfile();
    }

    @When("^I search using \"([^\"]*)\" only$")
    public void iSearchUsingOnly(String field) throws Throwable {
        IdentifyCustomerQueryParams identifyQueryParams = identifyCustomerHelper.getParamsFor(field, dbCustomer);
        identifyCustomerService = serviceFactory.identifyCustomer(new IdentifyCustomerRequest(HybrisHeaders.getValid("Digital").build(), identifyQueryParams));
        identifyCustomerService.invoke();
    }

    @Then("^I am informed that I cannot search using only lastname$")
    public void iAmInformedThatICannotSearchUsingOnlyLastname() throws Throwable {
        identifyCustomerService.assertThatErrors().containedTheCorrectErrorMessage("SVC_100123_1103");
    }

    @Then("^I am informed that I cannot search using only firstname$")
    public void iAmInformedThatICannotSearchUsingOnlyFirstname() throws Throwable {
        identifyCustomerService.assertThatErrors().containedTheCorrectErrorMessage("SVC_100123_1105");
    }

    @Then("^I am informed that I cannot search using only title$")
    public void iAmInformedThatICannotSearchUsingOnlyTitle() throws Throwable {
        identifyCustomerService.assertThatErrors().containedTheCorrectErrorMessage("SVC_100123_1100");
    }

    @When("^I search using lastname and firstname is empty$")
    public void iSearchUsingLastnameAndFirstnameIsEmpty() throws Throwable {
        IdentifyCustomerQueryParams identifyQueryParams = IdentifyCustomerQueryParams.builder().lastname(dbCustomer.getLastname()).firstname("{EMPTY}").build();
        identifyCustomerService = serviceFactory.identifyCustomer(new IdentifyCustomerRequest(HybrisHeaders.getValid("Digital").build(), identifyQueryParams));
        identifyCustomerService.invoke();
    }

    @Then("^I am informed that lastname needs a firstname value$")
    public void iAmInformedThatLastnameNeedsAFirstnameValue() throws Throwable {
        identifyCustomerService.assertThatErrors().containedTheCorrectErrorMessage("SVC_100123_1103");
    }

    @When("^I search using lastname and firstname is (\\d+) character$")
    public void iSearchUsingLastnameAndFirstnameIsCharacter(int chars) throws Throwable {
        IdentifyCustomerQueryParams identifyQueryParams = IdentifyCustomerQueryParams.builder().lastname(dbCustomer.getLastname()).firstname(dbCustomer.getFirstname().substring(0, 1)).build();
        identifyCustomerService = serviceFactory.identifyCustomer(new IdentifyCustomerRequest(HybrisHeaders.getValid("Digital").build(), identifyQueryParams));
        identifyCustomerService.invoke();
    }

    @When("^I search using lastname and title$")
    public void iSearchUsingLastnameAndTitle() throws Throwable {
        IdentifyCustomerQueryParams identifyQueryParams = IdentifyCustomerQueryParams.builder().lastname(dbCustomer.getLastname()).title(dbCustomer.getTitle()).build();
        identifyCustomerService = serviceFactory.identifyCustomer(new IdentifyCustomerRequest(HybrisHeaders.getValid("Digital").build(), identifyQueryParams));
        identifyCustomerService.invoke();
    }

    @When("^I search for the profile using \"([^\"]*)\"$")
    public void iSearchForTheProfileUsing(String casing) throws Throwable {
        IdentifyCustomerQueryParams identifyQueryParams = identifyCustomerHelper.queryParamsFromCustomer(dbCustomer, casing);
        identifyCustomerService = serviceFactory.identifyCustomer(new IdentifyCustomerRequest(HybrisHeaders.getValid("Digital").build(), identifyQueryParams));
        identifyCustomerService.invoke();
    }

    @When("^I search using the email address \"([^\"]*)\"$")
    public void iSearchUsingTheEmailAddress(String email) throws Throwable {
        IdentifyCustomerQueryParams identifyQueryParams = IdentifyCustomerQueryParams.builder().email(email).build();
        identifyCustomerService = serviceFactory.identifyCustomer(new IdentifyCustomerRequest(HybrisHeaders.getValid("Digital").build(), identifyQueryParams));
        identifyCustomerService.invoke();
    }

    @Then("^I am informed that the email address is not valid$")
    public void iAmInformedThatTheEmailAddressIsNotValid() throws Throwable {
        identifyCustomerService.assertThatErrors().containedTheCorrectErrorMessage("SVC_100123_1106");
    }

    @Then("^the customer profile is returned$")
    public void theCustomerProfileIsReturned() throws Throwable {
        try {
            identifyCustomerService.assertThat().thatCorrectCustomerDetailsWereReturned(dbCustomer);
        } catch (Exception e) {
            pollingLoop().untilAsserted(() -> {
                identifyCustomerService.invoke();
                identifyCustomerService.assertThat().thatCorrectCustomerDetailsWereReturned(dbCustomer);
            });
        }
    }

    @When("^I search for multiple matching profiles$")
    public void i_search_for_multiple_matching_profiles() throws Throwable {
        IdentifyCustomerQueryParams identifyQueryParams = IdentifyCustomerQueryParams.builder().title(dbCustomer.getTitle()).firstname(dbCustomer.getFirstname()).lastname(dbCustomer.getLastname()).build();
        identifyCustomerService = serviceFactory.identifyCustomer(new IdentifyCustomerRequest(HybrisHeaders.getValid("Digital").build(), identifyQueryParams));
        identifyCustomerService.invoke();
    }

    @When("^I search for a customer with no matching data$")
    public void i_search_for_a_customer_with_no_matching_data() throws Throwable {
        IdentifyCustomerQueryParams identifyQueryParams = IdentifyCustomerQueryParams.builder().title(dbCustomer.getTitle()).firstname(RandomStringUtils.randomAlphabetic(6).toLowerCase()).lastname(RandomStringUtils.randomAlphabetic(7).toLowerCase()).build();
        identifyCustomerService = serviceFactory.identifyCustomer(new IdentifyCustomerRequest(HybrisHeaders.getValid("Digital").build(), identifyQueryParams));
        identifyCustomerService.invoke();
    }

    @Then("^I should see \"([^\"]*)\" error message$")
    public void i_should_see_error_message(String rtnMessage) throws Throwable {
        identifyCustomerService.assertThat();
        identifyCustomerService.assertThat().thatVerifyTheReponseMessage(rtnMessage);
    }

    @When("^I search for multiple matching profiles and choose to sort the results by \"([^\"]*)\"$")
    public void iSearchForMultipleMatchingProfilesAndChooseToSortTheResultsBy(String sortField) throws Throwable {
        IdentifyCustomerQueryParams identifyQueryParams = IdentifyCustomerQueryParams.builder()
                .title(dbCustomer.getTitle())
                .firstname(dbCustomer.getFirstname())
                .lastname(dbCustomer.getLastname())
                .sortfield(sortField)
                .order("ascending")
                .build();
        identifyCustomerService = serviceFactory.identifyCustomer(new IdentifyCustomerRequest(HybrisHeaders.getValid("Digital").build(), identifyQueryParams));
        identifyCustomerService.invoke();
    }

    @Then("^the results are sorted by \"([^\"]*)\"$")
    public void theResultsAreSortedBy(String sortField) throws Throwable {
        identifyCustomerService.assertThat().thenDataReturnedIsSortedBy(sortField);
    }

    @When("^I search for the profile using partial firstName and lastName$")
    public void iSearchForTheProfileUsingPartial() throws Throwable {
        IdentifyCustomerQueryParams identifyQueryParams = IdentifyCustomerQueryParams.builder().lastname(dbCustomer.getLastname().substring(0, 2)).firstname(dbCustomer.getFirstname().substring(0, 2)).build();
        identifyCustomerService = serviceFactory.identifyCustomer(new IdentifyCustomerRequest(HybrisHeaders.getValid("Digital").build(), identifyQueryParams));
        pollingLoop().untilAsserted(() -> {
            identifyCustomerService.invoke();
        });
    }
}