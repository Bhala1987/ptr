package feature.document.steps.services.managecustomerservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.CustomerDao;
import com.hybris.easyjet.database.hybris.dao.MembershipDao;
import com.hybris.easyjet.database.hybris.models.CustomerModel;
import com.hybris.easyjet.database.hybris.models.MemberShipModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.ValidateMembershipRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ValidateMembershipRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.validationconfirmation.ValidateMembershipResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.ValidateMembershipNumberService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * ValidateMembershipNumberSteps handle the communication with the validateMembershipNumber service.
 * It makes use of testData to store parameters that can be used by other steps.
 * Created by rajakm on 21/08/2017.
 */
@ContextConfiguration(classes = TestApplication.class)
public class ValidateMembershipNumberSteps {

    private static final String NON_EXIST_CUSTOMER_NAME = "testtest";
    private static final String COMPLETED = "COMPLETED";
    private static final String LAST_NAME = "lastname";
    private static final String MEMBERSHIP_NUMBER = "ejplusnumber";
    private static final String CUSTOMER_ID = "customerid";
    private static final String STAFF = "staff";
    private static final String CUSTOMER = "customer";

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Autowired
    private MembershipDao membershipDao;
    @Autowired
    private CustomerDao customerDao;

    private ValidateMembershipNumberService validateMembershipNumberService;
    private ValidateMembershipRequestBody.ValidateMembershipRequestBodyBuilder validateMembershipRequestBody;

    private String customerId = "";
    private String lastName = "";
    private String memberShipNumber = "";
    private MemberShipModel membership;
    private List<CustomerModel> customerModel;

    private void setRequestBody() {
        validateMembershipRequestBody = ValidateMembershipRequestBody.builder()
                .customerId(customerId)
                .lastName(lastName)
                .membershipNumber(memberShipNumber);
    }

    private void invokeValidateMembershipNumberService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
        validateMembershipNumberService = serviceFactory.validateMembershipNumberService(new ValidateMembershipRequest(headers.build(), validateMembershipRequestBody.build()));
        testData.setData(SERVICE, validateMembershipNumberService);
        validateMembershipNumberService.invoke();
    }

    private void sendValidateMembershipRequest() {
        setRequestBody();
        invokeValidateMembershipNumberService();
    }

    @And("^want to send ejPlusMembership number (.*) is less than expected size$")
    public void wantToSendEjPlusMembershipNumberInvalidEjPlusIsLessThanExpectedSize(String invalidEJPlus) throws EasyjetCompromisedException {
        membership = membershipDao.getEJPlusMemberBasedOnStatus(COMPLETED);
        lastName = membership.getLastname();
        memberShipNumber = invalidEJPlus;
    }

    @And("^want to send the request contains only (customerId|lastName|ejPlusNumber)$")
    public void wantToSendTheRequestContainsOnlyField(String field) throws EasyjetCompromisedException {
        membership = membershipDao.getEJPlusMemberBasedOnStatus(COMPLETED);
        customerModel = customerDao.returnValidCustomerWithShippingAddress();

        switch (field.toLowerCase()) {
            case CUSTOMER_ID: {
                customerId = customerModel.stream()
                        .findFirst().orElseThrow(() -> new EasyjetCompromisedException(EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INSUFFICIENT_DATA))
                        .getUid();
                break;
            }
            case LAST_NAME: {
                lastName = membership.getLastname();
                break;
            }
            case MEMBERSHIP_NUMBER: {
                memberShipNumber = membership.getEjMemberShipNumber();
                break;
            }
        }
    }

    @And("^want to send the request with surname and ejPlusnumber not match$")
    public void wantToSendTheRequestWithSurnameAndEjPlusnumberNotMatch() throws EasyjetCompromisedException {
        membership = membershipDao.getEJPlusMemberBasedOnStatus(COMPLETED);
        lastName = NON_EXIST_CUSTOMER_NAME;
        memberShipNumber = membership.getEjMemberShipNumber();
    }

    @And("^want to send the request with ejPlus number (.*) is not identified$")
    public void wantToSendTheRequestWithEjPlusNumberEjPlusIsNotIdentified(String ejPlus) throws EasyjetCompromisedException {
        membership = membershipDao.getEJPlusMemberBasedOnStatus(COMPLETED);
        lastName = membership.getLastname();
        memberShipNumber = ejPlus;
    }

    @And("^want to send the request with ejPlus number expiry date is in the past$")
    public void wantToSendTheRequestWithEjPlusNumberExpiryDateIsInThePast() {
        membership = membershipDao.getExpiredEJPlusMembership(COMPLETED);
        assertThat(membership).isNotNull();
        lastName = membership.getLastname();
        memberShipNumber = membership.getEjMemberShipNumber();
    }

    @And("^want to send the request with (.*) ejPlus number and surname is valid$")
    public void wantToSendTheRequestWithTypeEjPlusNumberAndSurnameIsValid(String type) throws EasyjetCompromisedException {
        if (type.equalsIgnoreCase(STAFF)) {
            membership = membershipDao.getValidEJPlusMembershipForStaffWithStatus(COMPLETED);
        } else if (type.equalsIgnoreCase(CUSTOMER)) {
            membership = membershipDao.getEJPlusMemberBasedOnStatus(COMPLETED);
        }
        lastName = membership.getLastname();
        memberShipNumber = membership.getEjMemberShipNumber();
    }

    @When("^I send the request to validateMembership service$")
    public void iSendTheRequestToValidateMembershipService() {
        sendValidateMembershipRequest();
    }

    @Then("^the ejPlus membership details returned to the channel$")
    public void theEjPlusMembershipDetailsReturnedToTheChannel() {
        ValidateMembershipResponse actualValidateMembershipResponse = validateMembershipNumberService.getResponse();
        validateMembershipNumberService.assertThat().hasMembershipDetailsReturned(actualValidateMembershipResponse, true, memberShipNumber, lastName);
    }

}