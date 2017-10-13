package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.CustomerDao;
import com.hybris.easyjet.database.hybris.dao.MembershipDao;
import com.hybris.easyjet.database.hybris.models.MemberShipModel;
import com.hybris.easyjet.fixture.hybris.helpers.CustomerHelper;
import com.hybris.easyjet.fixture.hybris.helpers.EventMessageCreationHelper;
import com.hybris.easyjet.fixture.hybris.helpers.StaffMembertoCustomerProfileAssociationHelper;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.Customer;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.RegisterCustomerFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.UpdateCustomerDetailsService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ID;

/**
 * Created by giuseppedimartino on 17/02/17.
 */
@ContextConfiguration(classes = TestApplication.class)

public class UpdateCustomerDetailsSteps {

    public static final String COMPLETED = "COMPLETED";
    @Autowired
    MembershipDao membershipDao;
    private UpdateCustomerDetailsService updateCustomerDetailsService;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private StaffMembertoCustomerProfileAssociationHelper staffHelper;
    @Autowired
    private SerenityFacade testData;

    private String customerId;
    private Customer body;
    private MemberShipModel memberShipModel;
    @Autowired
    private EventMessageCreationHelper eventMessageCreationHelper;

    @Given("^I create a new valid customer$")
    public void iCreateANewValidCustomer() throws Throwable {
        customerHelper.customerAccountExistsWithAKnownPassword();
        customerId = customerHelper.getRegisterCustomerService().getResponse().getRegistrationConfirmation().getCustomerId();
        testData.setData(CUSTOMER_ID, customerId);
        testData.setAccessToken(customerHelper.getRegisterCustomerService().getResponse().getBookingConfirmation().getAuthentication().getAccessToken());
    }

    @Given("^the request not contain the (.*)$")
    public void theRequestNotContainTheField(String field) throws Throwable {
        Customer.PersonalDetails.PersonalDetailsBuilder builder = Customer.PersonalDetails.builder();
        if (!field.equals("email"))
            builder.email("succcessj.henry@abctest.com");
        if (!field.equals("type"))
            builder.type("adult");
        if (!field.equals("age"))
            builder.age(20);
        if (!field.equals("title"))
            builder.title("mr");
        if (!field.equals("first name"))
            builder.firstName("John");
        if (!field.equals("last name"))
            builder.lastName("Henry");
        if (!field.equals("EJPlus card number"))
            builder.ejPlusCardNumber("00453560");
        if (!field.equals("nif number"))
            builder.nifNumber("876765512");
        if (!field.equals("phone number"))
            builder.phoneNumber("774012854");
        if (!field.equals("alternative phone number"))
            builder.alternativePhoneNumber("0200123821");
        if (!field.equals("flight club id"))
            builder.flightClubId("543443");
        if (!field.equals("flight club expiry date"))
            builder.flightClubExpiryDate("2017-02-09");
        builder.keyDates(new ArrayList<Customer.PersonalDetails.KeyDates>());
        body = Customer.builder()
                .personalDetails(builder.build())
                .contactAddress(new ArrayList<Customer.ContactAddress>() {{
                    Customer.ContactAddress.builder()
                            .addressLine1("52, Main Street")
                            .addressLine2("Flat 2B")
                            .addressLine3("")
                            .city("Oxford")
                            .country("GBR")
                            .postalCode("OX11 2ES")
                            .build();
                }})
                .build();
    }

    @When("^I send a request to the update customer service$")
    public void iSendARequestToTheUpdateCustomerService() throws Throwable {
        customerHelper.updateCustomerDetails(customerId, body);
       // eventMessageCreationHelper.validateCustomerUpdateMessage(customerId);
    }


    @Then("^I will return (.+) for missing mandatory (.*)$")
    public void iWillReturnAnErrorMessageForMissingMandatory(String error, String field) throws Throwable {
        customerHelper.getUpdateCustomerDetailsService().assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @Given("^the (.*) does not match with the (.*)$")
    public void theAgeDoesNotMatchWithThePassengerType(String age, String type) throws Throwable {
        Customer.PersonalDetails.PersonalDetailsBuilder builder = Customer.PersonalDetails.builder()
                .email("success+j.henry@abctest.com")
                .age(Integer.valueOf(age))
                .type(type)
                .title("mr")
                .firstName("John")
                .lastName("Henry")
                .ejPlusCardNumber("00453560")
                .nifNumber("876765512")
                .phoneNumber("774012854")
                .alternativePhoneNumber("0200123821")
                .flightClubId("543443")
                .flightClubExpiryDate("2017-02-09")
                .keyDates(new ArrayList<Customer.PersonalDetails.KeyDates>());
        body = Customer.builder()
                .personalDetails(builder.build())
                .contactAddress(new ArrayList<Customer.ContactAddress>())
                .build();
    }

    @Then("^I should add the validation error message for (.*) to the return message$")
    public void iShouldAddTheValidationErrorMessageForFieldToTheReturnMessage(String error) throws Throwable {
        customerHelper.getUpdateCustomerDetailsService().assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @Given("^the new value for (.*) is \"(.*)\"$")
    public void theNewValueForFieldIsUpdate(String field, String value) throws Throwable {


        Customer.ContactAddress.ContactAddressBuilder contactAddressBuilder = Customer.ContactAddress.builder()
                .addressLine1(field.equals("addressLine1") ? value : "52, Main Street")
                .addressLine2(field.equals("addressLine2") ? value : "Flat 2B")
                .addressLine3(field.equals("addressLine3") ? value : "")
                .city(field.equals("city") ? value : "Oxford")
                .country(field.equals("country") ? value : "GBR")
                .postalCode(field.equals("PostCode") ? value : "OX11 2ES");
        Customer.PersonalDetails.PersonalDetailsBuilder personalDetailsBuilder = Customer.PersonalDetails.builder()
                .email(field.equals("email") ? value : String.format("success"+"%s@abctest.com", String.valueOf(new Date().toString().hashCode())))
                .type(field.equals("type") ? value : "adult")
                .age(field.equals("age") ? Integer.valueOf(value) : 20)
                .title(field.equals("title") ? value : "mr")
                .firstName(field.equals("first name") ? value : "Arthur")
                .lastName(field.equals("last name") ? value : "Campbell")
                .ejPlusCardNumber(field.equals("EJPlus card number") ? value : "00453560")
                .nifNumber(field.equals("nif number") ? value : "876765512")
                .phoneNumber(field.equals("phone number") ? value : "774012854")
                .alternativePhoneNumber(field.equals("alternative phone number") ? value : "0200123821")
                .flightClubId(field.equals("seatmap club id") ? value : "543443")
                .flightClubExpiryDate(field.equals("seatmap club expiry date") ? value : "2017-02-09")
                .keyDates(new ArrayList<Customer.PersonalDetails.KeyDates>()
                );
        body = Customer.builder()
                .personalDetails(personalDetailsBuilder.build())
                .contactAddress(new ArrayList<Customer.ContactAddress>() {{
                    add(contactAddressBuilder.build());
                }})
                .build();
    }

    @Given("^the Customer is a staff$")
    public void theCustomerIsAStaff() throws Throwable {
        staffHelper.associateCustomerProfileWithStaffMemberFromRequest(false);
    }

    @Given("^the eJ Plus Membership Number not match with the stored Surname$")
    public void theEJPlusMembershipNumberDoesNotDoesNotMatchWithTheStoredSurname() throws Throwable {
        Customer.ContactAddress.ContactAddressBuilder contactAddressBuilder = Customer.ContactAddress.builder()
                .addressLine1("52, Main Street")
                .addressLine2("Flat 2B")
                .addressLine3("")
                .city("Oxford")
                .country("GBR")
                .postalCode("OX11 2ES");
        Customer.PersonalDetails.PersonalDetailsBuilder personalDetailsBuilder = Customer.PersonalDetails.builder()
                .email("j.henry@abctest.com")
                .type("adult")
                .age(20)
                .title("mr")
                .firstName("John")
                .lastName("Henri")
                .ejPlusCardNumber("00453560")
                .nifNumber("876765512")
                .phoneNumber("774012854")
                .alternativePhoneNumber("0200123821")
                .flightClubId("543443")
                .flightClubExpiryDate("2017-02-09")
                .keyDates(new ArrayList<Customer.PersonalDetails.KeyDates>()
                );
        body = Customer.builder()
                .personalDetails(personalDetailsBuilder.build())
                .contactAddress(new ArrayList<Customer.ContactAddress>() {{
                    add(contactAddressBuilder.build());
                }})
                .build();
    }

    @Given("^the email is not already known$")
    public void theEmailIsNotAlreadyKnown() throws Throwable {
        Customer.ContactAddress.ContactAddressBuilder contactAddressBuilder = Customer.ContactAddress.builder()
                .addressLine1("52, Main Street")
                .addressLine2("Flat 2B")
                .addressLine3("")
                .city("Oxford")
                .country("GBR")
                .postalCode("OX11 2ES");
        Customer.PersonalDetails.PersonalDetailsBuilder personalDetailsBuilder = Customer.PersonalDetails.builder()
                .email(UUID.randomUUID().toString().replace("-", "").substring(0, 9) + "@abctest.com")
                .type("adult")
                .age(20)
                .title("mr")
                .firstName("John")
                .lastName("Henry")
                .ejPlusCardNumber("00453560")
                .nifNumber("876765512")
                .phoneNumber("774012854")
                .alternativePhoneNumber("0200123821")
                .flightClubId("543443")
                .flightClubExpiryDate("2017-02-09")
                .keyDates(new ArrayList<Customer.PersonalDetails.KeyDates>()
                );
        body = Customer.builder()
                .personalDetails(personalDetailsBuilder.build())
                .contactAddress(new ArrayList<Customer.ContactAddress>() {{
                    add(contactAddressBuilder.build());
                }})
                .build();
    }

    @Then("^I update the customer profile$")
    public void iUpdateTheCustomerProfile() throws Throwable {
        customerHelper.getUpdateCustomerDetailsService().assertThat().customerUpdated(customerId);
    }

    @Given("^the email is already linked to an active customer profile$")
    public void theEmailIsAlreadyLinkedToAnActiveCustomerProfile() throws Throwable {
        String email = customerDao.getAllCustomers().get(0).getCustomerid();
        Customer.ContactAddress.ContactAddressBuilder contactAddressBuilder = Customer.ContactAddress.builder()
                .addressLine1("52, Main Street")
                .addressLine2("Flat 2B")
                .addressLine3("")
                .city("Oxford")
                .country("GBR")
                .postalCode("OX11 2ES");
        Customer.PersonalDetails.PersonalDetailsBuilder personalDetailsBuilder = Customer.PersonalDetails.builder()
                .email(email)
                .type("adult")
                .age(20)
                .title("mr")
                .firstName("John")
                .lastName("Henry")
                .ejPlusCardNumber("00453560")
                .nifNumber("876765512")
                .phoneNumber("774012854")
                .alternativePhoneNumber("0200123821")
                .flightClubId("543443")
                .flightClubExpiryDate("2017-02-09")
                .keyDates(new ArrayList<Customer.PersonalDetails.KeyDates>()
                );
        body = Customer.builder()
                .personalDetails(personalDetailsBuilder.build())
                .contactAddress(new ArrayList<Customer.ContactAddress>() {{
                    add(contactAddressBuilder.build());
                }})
                .build();
    }

    @And("^I intend to update my profile with a valid Staff EJPlus number$")
    public void iIntendToUpdateMyProfileWithAValidEJPlusNumber() throws Throwable {
        memberShipModel = membershipDao.getValidEJPlusMembershipForStaffWithStatus(COMPLETED);
        theNewValueForFieldIsUpdate("EJPlus card number", memberShipModel.getEjMemberShipNumber());
        this.body.personalDetails.lastName = memberShipModel.getLastname();
    }

    @And("^I intend to update my profile with a valid non-Staff EJPlus number$")
    public void iIntendToUpdateMyProfileWithAValidnonstaffEJPlusNumber() throws Throwable {
        MemberShipModel memberShipModel = membershipDao.getEJPlusMemberBasedOnStatus("COMPLETED");
        theNewValueForFieldIsUpdate("EJPlus card number", memberShipModel.getEjMemberShipNumber());
        this.body.personalDetails.lastName = memberShipModel.getLastname();
    }

    @And("^I intend to update my EJPlus number with status other than (.*)$")
    public void iIntendToUpdateMyEJPlusNumberWithStatusOtherThanStatus(String status) throws Throwable {
        MemberShipModel ejPlusMemberOtherThanStatus = membershipDao.getEJPlusMemberOtherThanStatus(status);
        String eJPlusNumer = ejPlusMemberOtherThanStatus.getEjMemberShipNumber();
        theNewValueForFieldIsUpdate("EJPlus card number", eJPlusNumer);
        this.body.personalDetails.lastName = ejPlusMemberOtherThanStatus.getLastname();
    }

    @Then("^I should get error with code (.*)$")
    public void iShouldGetTheErrorWithCodeAsSVC(String code) throws Throwable {
        customerHelper.getUpdateCustomerDetailsService().assertThatErrors().containedTheCorrectErrorMessage(code);
    }

    @And("^I update the customer profile details$")
    public void iUpdateTheCustomerProfileDetails() throws Throwable {
        body = Customer.builder()
                .personalDetails(RegisterCustomerFactory.getPersonalDetails().build())
                .contactAddress(new ArrayList<Customer.ContactAddress>() {{
                    add(RegisterCustomerFactory.getContactAddress().build());
                }})
                .build();

        if (customerId == null){
            customerId = testData.getData(CUSTOMER_ID);
            customerHelper.updateCustomerDetailsUsingAccessToken(customerId,body);
        } else {
            customerHelper.updateCustomerDetails(customerId,body);
        }
    }
}
