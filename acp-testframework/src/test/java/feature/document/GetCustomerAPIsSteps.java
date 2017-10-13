package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.ApiDao;
import com.hybris.easyjet.database.hybris.dao.CustomerAuditDao;
import com.hybris.easyjet.fixture.hybris.helpers.CustomerHelper;
import com.hybris.easyjet.fixture.hybris.helpers.SavedPassengerHelper;
import com.hybris.easyjet.fixture.hybris.helpers.SetAPIHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.SetAPIRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.PassengerApisFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.GetAPIsForCustomerRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.SetAPIRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.savedpassenger.SavedPassengerRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.*;
import cucumber.api.DataTable;
import cucumber.api.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ID;
import static com.hybris.easyjet.fixture.WaitHelper.pause;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.APIS;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.GET_SAVED_PASSENGER;


/**
 * Steps for Get APIS For The Customer Profile feature file
 */
@ContextConfiguration(classes = TestApplication.class)

public class GetCustomerAPIsSteps {

    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private SavedPassengerHelper savedPassengerHelper;
    @Autowired
    private ApiDao apiDao;
    @Autowired
    private CustomerAuditDao customerAuditDao;

    private String passengerId;
    private SetAPIHelper setAPIHelper = new SetAPIHelper();
    private CustomerPathParams params;
    private GetCustomerAPIsService getCustomerAPIsService;
    private GetSavedPassengerService getSavedPassengerService;
    private SetAPIService setAPIsService;
    private String dateOfBirth = "2010-12-12", documentExpiryDate = "2050-12-12", documentType = "PASSPORT", gender = "MALE", nationality = "GBR", countryOfIssue = "GBR", fullName = "I am a Customer", documentNumber = "AK123456";

    @Autowired
    private SerenityFacade testData;
    private UpdateCustomerDetailsService removeApisCustomerService;

    private void iCreatedACustomer() {
        customerHelper.childCustomerAccountExistsWithAKnownPassword();
    }

    @Given("^I have APIs stored for a customer for less than 16 months$")
    public void iHaveAPIsStoredForACustomerForLessThan16Months() throws Throwable {
        iCreatedACustomer();
        pause();
        params = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(APIS).build();
        SetAPIRequestBody requestBody = PassengerApisFactory.aCustomerPassengerApis(dateOfBirth, documentExpiryDate, documentNumber, documentType, gender, nationality, countryOfIssue, fullName);
        setAPIsService = serviceFactory.setApi(new SetAPIRequest(HybrisHeaders.getValidWithToken(testData.getChannel(), testData.getAccessToken()).build(), params, requestBody));
        setAPIsService.invoke();
    }

    @When("^I request for APIs for that customer$")
    public void iRequestForAPIsForThatCustomer() throws Throwable {
        getCustomerAPIsService = serviceFactory.getCustomerAPIs(new GetAPIsForCustomerRequest((HybrisHeaders.getValidWithToken(testData.getChannel(), testData.getAccessToken()).build()), params));
        getCustomerAPIsService.invoke();
    }

    @Then("^I will return the API details for that customer")
    public void iWillReturnTheAPIDetailsSuccessfully() throws Throwable {
        pollingLoop().untilAsserted(()->{
            getCustomerAPIsService.invoke();
            getCustomerAPIsService.assertThat().onlyOneDocumentExistsAndAllTheDetailsReturnedAreCorrect(dateOfBirth, documentExpiryDate, documentType, gender, nationality, countryOfIssue, fullName, documentNumber);
        });

    }

    @Given("^I have APIs stored for a \"([^\"]*)\" for less than X months$")
    public void iHaveAPIsStoredForAForLessThanXMonths(String customerOrPassenger) throws Throwable {
        iCreatedACustomer();
        savedPassengerHelper.addValidPassengerToExistingCustomer(testData.getData(CUSTOMER_ID)
        );
        passengerId = savedPassengerHelper.addValidIdentityDocumentToToExistingPassengerViaChannel("Digital", "1980-12-12", documentExpiryDate, documentNumber, documentType, gender, nationality, countryOfIssue, "I am a passenger");
        passengerId = savedPassengerHelper.addValidIdentityDocumentToToExistingPassenger("1980-12-12", documentExpiryDate, documentNumber, documentType, gender, nationality, countryOfIssue, "I am a passenger");
    }

    @When("^I request for APIs for that passenger$")
    public void iRequestForAPIsForThatPassenger() throws Throwable {
        params = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(GET_SAVED_PASSENGER).build();
        getSavedPassengerService = serviceFactory.getSavedPassenger(new SavedPassengerRequest((HybrisHeaders.getValidWithToken(testData.getChannel(), testData.getAccessToken()).build()), params, "GET"));
        getSavedPassengerService.invoke();
    }

    @Then("^I will return the passenger API details successfully$")
    public void iWillReturnThePassengerAPIDetailsSuccessfully() throws Throwable {
        getSavedPassengerService.assertThat().allTheDetailsReturnedAreCorrect("1980-12-12", documentExpiryDate, documentNumber, documentType, gender, nationality, countryOfIssue, "I am a passenger");
    }

    @Given("^I have No APIs stored for a customer for less than X = 16 months$")
    public void iHaveNoAPIsStoredForACustomerForLessThanXMonths() throws Throwable {
        iCreatedACustomer();
        params = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(APIS).build();
    }

    @Then("^I will not return the API details$")
    public void iWillNotReturnTheAPIDetails() throws Throwable {
        getCustomerAPIsService.assertThat().noDocumentsPresent();
    }

    @Given("^I have no APIs stored for a \"([^\"]*)\" for less than X months$")
    public void iHaveNoAPIsStoredForAForLessThanXMonths(String arg0) throws Throwable {
        iCreatedACustomer();
        savedPassengerHelper.addValidPassengerToExistingCustomer(testData.getData(CUSTOMER_ID));
    }

    @Then("^I will not return the passenger API details$")
    public void iWillNotReturnThePassengerAPIDetails() throws Throwable {
        getSavedPassengerService.assertThat().allTheDetailsReturnedAreCorrect();
    }

    @And("^customer (.*) not logged in$")
    public void theCustomerNotLoggedIn(String customerId) throws Throwable {
        testData.setData(CUSTOMER_ID, customerId);
    }

    @When("^I request APIs for that customer$")
    public void iRequestAPIsForThatCustomer() throws Throwable {
        params = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(APIS).build();
        getCustomerAPIsService = serviceFactory.getCustomerAPIs(new GetAPIsForCustomerRequest((HybrisHeaders.getValid(testData.getChannel()).build()), params));
        getCustomerAPIsService.invoke();
    }

    @Then("^I should receive an error$")
    public void iReceiveAnError() throws Throwable {
        getCustomerAPIsService.assertThatErrors();
    }

    @When("^I request APIs for customer (.*)$")
    public void iRequestAPIsForCustomer(String customerId) throws Throwable {
        testData.setData(CUSTOMER_ID, customerId);
        iRequestAPIsForThatCustomer();
    }

    @Then("^I should return with no apis but an error \"([^\"]*)\"$")
    public void iShouldReturnNoApisButAnError(String errorCode) throws Throwable {
        getCustomerAPIsService.assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }

    @And("^the customer (.*) has legacy APIs in AL$")
    public void theCustomerHasLegacyAPIsInAL(String customerId) throws Throwable {
        testData.setData(CUSTOMER_ID, customerId);
    }

    @When("^I use an invalid customer (.*)$")
    public void iUseAnInvalidCustomer(String invaliCustomer) throws Throwable {
        testData.setData(CUSTOMER_ID, invaliCustomer);
    }

    @But("^that customer has no Legacy APIs in AL$")
    public void thatCustomerHasNoLegacyAPIsInAL() throws Throwable {
        // No need to do anything here as this newly created customer in Hybris will not have any Legacy APIs in AL
    }

    @And("^I have added new API as follows$")
    public void iHaveAddedNewAPIAsFollows(DataTable dt) throws Throwable {
        params = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(APIS).build();
        List<List<String>> data = dt.raw();
        int newAdditionOfAPIsCount = data.size() - 1;
        for (int i = 1; i <= newAdditionOfAPIsCount; i++) {
            SetAPIRequestBody requestBody = PassengerApisFactory.aCustomerPassengerApis(data.get(i).get(0), data.get(i).get(1), data.get(i).get(2), data.get(i).get(3), data.get(i).get(4), data.get(i).get(5), data.get(i).get(6), data.get(i).get(7));
            setAPIsService = serviceFactory.setApi(new SetAPIRequest(HybrisHeaders.getValidWithToken(testData.getChannel(), testData.getAccessToken()).build(), params, requestBody));
            setAPIsService.invoke();
        }
    }

    @When("^I add new API as follows$")
    public void iAddNewAPIAsFollows(DataTable dt) throws Throwable {
        iHaveAddedNewAPIAsFollows(dt);
    }

    @And("^no APIs in hybris for the same customer$")
    public void noAPIsInHybris() throws Throwable {
        customerHelper.removeAllAPIsForCustomer(testData.getData(CUSTOMER_ID), testData.getChannel());
    }

    @Then("^I should receive the combined following APIs$")
    public void iShouldReceiveCombinedFollowingAPIs(DataTable dt) throws Throwable {
        getCustomerAPIsService.assertThat().verifyExpectedAPIsAreReturned(dt);
    }

    @Then("^I should receive the following APIs$")
    public void iShouldReceiveTheFollowingAPIs(DataTable dt) throws Throwable {
        getCustomerAPIsService.assertThat().verifyExpectedAPIsAreReturned(dt);
    }

    @And("^has following legacy API in AL$")
    public void cusHasFollowingLegacyAPIInAL(DataTable dt) throws Throwable {
        // Nothing to do here as we have this data in Legacy AL
    }

    @When("^I add duplicate API as follows$")
    public void iAddNewDuplicateAPIAsFollows(DataTable dt) throws Throwable {
        iAddNewAPIAsFollows(dt);
    }

    @Then("^I should receive no duplicates$")
    public void iShouldReceiveNoDuplicates() throws Throwable {
        // This will be taken care by the next assertion
    }

    @And("^for the duplicate records information from hybris is returned$")
    public void forTheDuplicateRecordsInformationFromHybrisIsReturned(DataTable dt) throws Throwable {
        iShouldReceiveCombinedFollowingAPIs(dt);
    }

    @Then("^I should receive the following expired API$")
    public void iShouldReceiveTheFollowingExpiredAPI(DataTable dt) throws Throwable {
        iShouldReceiveCombinedFollowingAPIs(dt);
    }

    @And("^I add the following expired API in hybris$")
    public void iHaveTheFollowingAPIInHybris(DataTable dt) throws Throwable {
        iHaveAddedNewAPIAsFollows(dt);
    }

    @And("^using the customer (.*)$")
    public void usingTheCustomerCus(String customerId) throws Throwable {
        testData.setData(CUSTOMER_ID, customerId);
        if (!testData.getChannel().contains("AD")) {
            customerHelper.loginWithValidCredentials(testData.getChannel(), "a.rossi@reply.co.uk", "1234", false);
        }
    }
}
