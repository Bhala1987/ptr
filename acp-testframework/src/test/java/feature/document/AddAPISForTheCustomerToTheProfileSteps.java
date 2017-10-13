package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.ApiDao;
import com.hybris.easyjet.database.hybris.dao.CustomerAuditDao;
import com.hybris.easyjet.fixture.hybris.helpers.CustomerHelper;
import com.hybris.easyjet.fixture.hybris.helpers.SetAPIHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.SetAPIRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.PassengerApisFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.SetAPIRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.SetAPIService;
import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ID;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.APIS;


/**
 * Steps for ReceiveRequestToAddAPISForTheCustomerToTheProfile feature file
 */
@ContextConfiguration(classes = TestApplication.class)

public class AddAPISForTheCustomerToTheProfileSteps {

    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private ApiDao apiDao;
    @Autowired
    private CustomerAuditDao customerAuditDao;
    @Autowired
    private SerenityFacade testData;

    private SetAPIHelper setAPIHelper = new SetAPIHelper();
    private int apiNumberBeforeCall;
    private int customerAuditsBeforeCall;

    private SetAPIService setAPIService;
    private String dateOfBirth = "2010-12-12", documentExpiryDate = "2050-12-12", documentType = "PASSPORT", gender = "MALE", nationality = "GBR", countryOfIssue = "GBR", fullName = "Mario Verdi", documentNumber = "AK123456";

    @Given("^that I have registered a new user$")
    public void thatIHaveRegisteredANewUser() throws Throwable {
        // Create a child user
        customerHelper.childCustomerAccountExistsWithAKnownPassword();
        apiNumberBeforeCall = apiDao.countApis();
        customerAuditsBeforeCall = customerAuditDao.countRowsByCustomerIdAndType(testData.getData(CUSTOMER_ID), "APIS");
    }

    @When("^I send a request to add an API with \"([^\"]*)\"$")
    public void iSendARequestToAddAnAPIWith(String documentNumber) throws Throwable {
        CustomerPathParams params = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(APIS).build();
        SetAPIRequestBody requestBody = PassengerApisFactory.aCustomerPassengerApis(dateOfBirth, documentExpiryDate, documentNumber, documentType, gender, nationality, countryOfIssue, fullName);
        setAPIService = serviceFactory.setApi(new SetAPIRequest(HybrisHeaders.getValid("Digital").build(), params, requestBody));
        setAPIService.invoke();
    }

    @When("^I send a request to add an API with a non matching date of birth \"([^\"]*)\"$")
    public void iSendARequestToAddAnAPIWithANonMatchingDateOfBirth(String dateOfBirth) throws Throwable {
        CustomerPathParams params = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(APIS).build();
        SetAPIRequestBody requestBody = PassengerApisFactory.aCustomerPassengerApis(dateOfBirth, documentExpiryDate, documentNumber, documentType, gender, nationality, countryOfIssue, fullName);
        setAPIService = serviceFactory.setApi(new SetAPIRequest(HybrisHeaders.getValid("Digital").build(), params, requestBody));
        setAPIService.invoke();
    }

    @When("^I send a valid request to add an API$")
    public void iSendAValidRequestToAddAnAPI() throws Throwable {
        CustomerPathParams params = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(APIS).build();
        SetAPIRequestBody requestBody = PassengerApisFactory.aCustomerPassengerApis(dateOfBirth, documentExpiryDate, documentNumber, documentType, gender, nationality, countryOfIssue, fullName);
        setAPIService = serviceFactory.setApi(new SetAPIRequest(HybrisHeaders.getValid("Digital").build(), params, requestBody));
        setAPIService.invoke();
    }

    @When("^I create a request without a required field \"([^\"]*)\"$")
    public void iCreateARequestWithoutARequiredField(String fieldToSetToNull) throws Throwable {
        CustomerPathParams params = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(APIS).build();
        SetAPIRequestBody requestBody = PassengerApisFactory.aCustomerPassengerApis(dateOfBirth, documentExpiryDate, documentNumber, documentType, gender, nationality, countryOfIssue, fullName);
        PassengerApisFactory.setFieldUsingReflection(requestBody, fieldToSetToNull, StringUtils.EMPTY);
        setAPIService = serviceFactory.setApi(new SetAPIRequest(HybrisHeaders.getValid("Digital").build(), params, requestBody));
        setAPIService.invoke();
    }

    @When("^I send a invalid request with more than one invalid field$")
    public void iSendAInvalidRequestWithMoreThanOneInvalidField(DataTable nullFields) throws Throwable {
        List<List<String>> data = nullFields.raw();
        List<String> firstRow = data.get(0);
        CustomerPathParams params = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(APIS).build();
        SetAPIRequestBody requestBody = PassengerApisFactory.aCustomerPassengerApis(dateOfBirth, documentExpiryDate, documentNumber, documentType, gender, nationality, countryOfIssue, fullName);
        for (String fieldToSetToNull : firstRow) {
            PassengerApisFactory.setFieldUsingReflection(requestBody, fieldToSetToNull, StringUtils.EMPTY);
        }
        setAPIService = serviceFactory.setApi(new SetAPIRequest(HybrisHeaders.getValid("Digital").build(), params, requestBody));
        setAPIService.invoke();
    }

    @Then("^I should add the validation error message with \"([^\"]*)\"$")
    public void iShouldAddTheValidationErrorMessageWith(String code) throws Throwable {
        setAPIService.assertThatErrors().containedTheCorrectErrorMessage(code);
    }

    @Then("^I should receive a confirmation response$")
    public void iShouldReceiveAConfirmationResponse() throws Throwable {
        setAPIService.assertThat().assertSuccess();
    }

    @Then("^I should return the validation errors as per Manage APIS Validation$")
    public void iShouldReturnTheValidationErrorSAsPerManageAPISValidation(DataTable errorList) throws Throwable {
        List<List<String>> data = errorList.raw();
        List<String> firstRow = data.get(0);
        setAPIService.assertThatErrors().containedTheCorrectErrorMessage(firstRow.get(0), firstRow.get(1), firstRow.get(2));
    }

    @And("^I should not have added any API record$")
    public void iShouldNotHaveAddedAnyAPIRecord() throws Throwable {
        int apiNumberAfterCall = apiDao.countApis();
        setAPIService.assertThat().assertIsAdded(apiNumberBeforeCall, apiNumberAfterCall, 0);
    }

    @And("^I should record and audit record including Record User ID, Date and time of creation$")
    public void iShouldRecordAndAuditRecordIncludingRecordUserIDDateAndTimeOfCreation() throws Throwable {
        int auditsAfterCall = customerAuditDao.countRowsByCustomerIdAndType(testData.getData(CUSTOMER_ID), "APIS");
        setAPIService.assertThat().assertIsAdded(customerAuditsBeforeCall, auditsAfterCall);
    }

}