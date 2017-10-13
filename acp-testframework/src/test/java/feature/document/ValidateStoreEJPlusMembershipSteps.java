package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.MembershipDao;
import com.hybris.easyjet.database.hybris.models.MemberShipModel;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.registercustomer.RegisterCustomerRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.RegisterCustomerFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ValidateStoreEJPlusMembRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.ValidateStoreEJPlusMembService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.fluttercode.datafactory.impl.DataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.REGISTER_CUSTOMER_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Giuseppe Cioce on 19/12/2016.
 */
@ContextConfiguration(classes = TestApplication.class)
public class ValidateStoreEJPlusMembershipSteps {

    public static final String COMPLETED = "COMPLETED";
    private static Logger LOG = LogManager.getLogger(ValidateStoreEJPlusMembershipSteps.class);
    @Autowired
    private HybrisServiceFactory serviceFactory;
    private ValidateStoreEJPlusMembService validateStoreEJPlusMembService;
    private RegisterCustomerRequestBody registerCustomerRequestBody;
    @Autowired
    private MembershipDao membershipDao;
    @Autowired
    private SerenityFacade testData;
    private MemberShipModel memberShipModel;

    /**
     * Create a valid ejPlusCardNumber
     */
    @Given("^provide a valid request to create a customer profile$")
    public void provideAValidRequestToCreateACustomerProfile() throws Throwable {
        registerCustomerRequestBody = RegisterCustomerFactory.aDigitalProfile();
        registerCustomerRequestBody.getPersonalDetails().setEmployeeId("645444");
        registerCustomerRequestBody.getPersonalDetails().setEmployeeEmail("j.smith@abctest.com");
        DataFactory df = new DataFactory();
        String ejPlusCardNumber = df.getNumberText(6);
        registerCustomerRequestBody.getPersonalDetails().setEjPlusCardNumber("S" + ejPlusCardNumber);
        testData.setData(REGISTER_CUSTOMER_REQUEST, registerCustomerRequestBody);
    }

    @When("^I validate the request$")
    public void iValidateTheRequest() throws Throwable {
        validateStoreEJPlusMembService = serviceFactory.validateEJPlusMembership(new ValidateStoreEJPlusMembRequest(HybrisHeaders.getValid("ADAirport").build(), registerCustomerRequestBody));
        validateStoreEJPlusMembService.invoke();
    }

    /**
     * The validation is successfully when no warning are back to the channel
     */

    @And("^the validation is successful$")
    public void theValidationIsSuccessful() throws Throwable {
        validateStoreEJPlusMembService.assertThat().additionalInformationIsEmpty();
    }

    /**
     * The customer is created successfully
     */

    @And("^the customer profile is created successfully$")
    public void theCustomerProfileIsCreatedSuccessFully() throws Throwable {
        validateStoreEJPlusMembService.assertThat().additionalInformationReturned("SVC_100047_2038");
    }

    /**
     * Create a valid ejPlusCardNumber for a standard customer
     */

    @And("^the employId in the request is missing$")
    public void theEmployIdInTheRequestIsMissing() {
        registerCustomerRequestBody.getPersonalDetails().setEmployeeId(null);
        registerCustomerRequestBody.getPersonalDetails().setEmployeeEmail(null);
        DataFactory df = new DataFactory();
        int ejPlusCardNumber = df.getNumberBetween(8, Integer.MAX_VALUE);
        registerCustomerRequestBody.getPersonalDetails().setEjPlusCardNumber("" + ejPlusCardNumber);
    }

    /**
     * BR_00430 - The number of characters passed is less than 8 characters will be verify
     * when the EmployID and EmployEmail are missed in the request
     */
    @And("^the length (.*) of ejPlusCardNumber passed is less than \"([^\"]*)\" characters$")
    public void theLengthOfEjPlusCardNumberPassedIsLessThanCharacters(int length, String strArg1)
            throws Throwable {
        String ejPlusCardNumber = "";
        if (length >= 0) {
            ejPlusCardNumber = registerCustomerRequestBody.getPersonalDetails().getEjPlusCardNumber().substring(0, length);
        }
        registerCustomerRequestBody.getPersonalDetails().setEjPlusCardNumber(ejPlusCardNumber);
        assertThat(registerCustomerRequestBody.getPersonalDetails()
                .getEjPlusCardNumber().length() < Integer.parseInt(strArg1)).isEqualTo(true);
    }

    /**
     * The test case varify the BR_00420
     */
    @And("^the first character of the ejPlusCarNumber is \"(.*)\"$")
    public void theFirstCharacterOfEjPlusCardnumberPassedIsNotA(String character)
            throws Throwable {
        String ejPlusCardNumber = character + registerCustomerRequestBody.getPersonalDetails().getEjPlusCardNumber();
        registerCustomerRequestBody.getPersonalDetails().setEjPlusCardNumber(ejPlusCardNumber);
    }

    /**
     * The test case verify the BR_3000
     * Change the date of the request body for the customer,
     * with an ejCardPlusMemebership with an expiration date in past
     */
    @And("^the expiry date for the ejplusmemebership is in the past$")
    public void theExpiryDateIsInThePast()
            throws Throwable {
        MemberShipModel expiredEJPlusMembership = membershipDao.getExpiredEJPlusMembership(COMPLETED);
        registerCustomerRequestBody.getPersonalDetails().setEjPlusCardNumber(expiredEJPlusMembership.getEjMemberShipNumber());
        registerCustomerRequestBody.getPersonalDetails().setLastName(expiredEJPlusMembership.getLastname());
    }

    /**
     * The test case verify the BR_00410
     * A valid customer with a valid card but associated to another customer
     */
    @And("^the surname of the customer passed in the request does not match the surname on the ejPlusCardNumber$")
    public void theSurnameOfTheCustomerPassedInTheRequestDoesNotMatchTheSurnameOnTheEjPlusCardNumber()
            throws Throwable {
        MemberShipModel memberShipModel = membershipDao.getEJPlusMemberOtherThanStatus(COMPLETED);
        registerCustomerRequestBody.getPersonalDetails().setEjPlusCardNumber(memberShipModel.getEjMemberShipNumber());
        registerCustomerRequestBody.getPersonalDetails().setLastName("test");
    }

    @And("^the details passed in the request respect the criteria$")
    public void theDetailsPassedInTheRequestRespectTheCriteria() {
        memberShipModel = membershipDao.getValidEJPlusMembershipForStaffWithStatus(COMPLETED);
        registerCustomerRequestBody.getPersonalDetails().setEjPlusCardNumber(memberShipModel.getEjMemberShipNumber());
        registerCustomerRequestBody.getPersonalDetails().setLastName(memberShipModel.getLastname());
    }

    /**
     * DO NOTHIN the RegisterCustomerFactory automatically build a body with a
     * ejPlusMembership not stored in DB
     */
    @And("^the ej Plus membership is not found in the database look up$")
    public void theEjPlusMembershipIsNotFoundInTheDatabaseLookUp() {
        // NOTHING TO DO
    }

    @Then("^customer profile service returns error code ([^\"]*)$")
    public void i_will_verify_a_error_something(String errorCode) throws Throwable {
        validateStoreEJPlusMembService.assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }

    @Then("^the customer profile service returns warning code ([^\"]*)$")
    public void i_will_verify_a_warning_something(String errorCode) throws Throwable {
        validateStoreEJPlusMembService.assertThat().additionalInformationContains(errorCode);
    }

    @And("^the provided ejPlusCardNumber is:(.*)$")
    public void theProvidedEjPlusCardNumberIsS(String aNumber) throws Throwable {
        registerCustomerRequestBody.getPersonalDetails().setEjPlusCardNumber(aNumber.trim());
    }


    @And("^I update the the EJPlusMembership number other than (.*)$")
    public void iUpdateTheTheEJPlusMembershipNumberWithStatus(String status) throws Throwable {
        MemberShipModel ejPlusMember = membershipDao.getEJPlusMemberOtherThanStatus(status);
        String validEjMembership = ejPlusMember.getEjMemberShipNumber();
        RegisterCustomerRequestBody registerCustomerRequest = testData.getData(REGISTER_CUSTOMER_REQUEST);
        registerCustomerRequest.getPersonalDetails().setEjPlusCardNumber(validEjMembership);
        registerCustomerRequest.getPersonalDetails().setLastName(ejPlusMember.getLastname());
        testData.setData(REGISTER_CUSTOMER_REQUEST, registerCustomerRequest);
    }

    @Then("^the customer profile service returns error code (.*)$")
    public void theCustomerProfileServiceReturnsErrorCodeSVC(String error) throws Throwable {
        validateStoreEJPlusMembService.assertThatErrors().containedTheCorrectErrorMessage(error);
    }
}
