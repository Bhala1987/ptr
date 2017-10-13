package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.CustomerDao;
import com.hybris.easyjet.fixture.hybris.helpers.CustomerHelper;
import com.hybris.easyjet.fixture.hybris.helpers.StaffMembertoCustomerProfileAssociationHelper;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ID;

/**
 * Created by siva on 01/11/2017.
 */
@ContextConfiguration(classes = TestApplication.class)

public class StaffMemebertoCustomerProfileAssociationSteps {

    @Autowired
    private CustomerDao hybrisCustomersDao;
    @Autowired
    private StaffMembertoCustomerProfileAssociationHelper staffMembertoCustomerProfileAssociationHelper;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private SerenityFacade testData;

    @And("^a valid request to associate staff member to member account$")
    public void aValidRequestToAssociateStaffMemberToMemberAccount() throws Throwable {
        staffMembertoCustomerProfileAssociationHelper.associateCustomerProfileWithStaffMemberFromRequest(false);
    }

    @And("^a invalid request to associate staff member to member account$")
    public void aInvalidRequestToAssociateStaffMemberToMemberAccount() throws Throwable {
        staffMembertoCustomerProfileAssociationHelper.associateCustomerProfileWithStaffMemberFromRequest(true);
    }

    @Then("^It will return a error message \"([^\"]*)\" to the channel$")
    public void itWillReturnAErrorMessageToTheChannel(String error) throws Throwable {
        staffMembertoCustomerProfileAssociationHelper.getRegisterStaffFaresService().assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @When("^the mandatory field \"([^\"]*)\" is not passed in the request$")
    public void theMandatoryFieldIsNotPassedInTheRequest(String missingParamater) throws Throwable {
        staffMembertoCustomerProfileAssociationHelper.createforStaffMemberRequestWithMissingParameters(missingParamater);
    }

    @Given("^a request to associate staff member to member account to validate \"([^\"]*)\" mandatory field$")
    public void aRequestToAssociateStaffMemberToMemberAccountToValidateMandatoryField(String mandatoryField) throws Throwable {
        staffMembertoCustomerProfileAssociationHelper.associateCustomerProfileWithMissingStaffMemberFromRequest(mandatoryField);
    }

    @Then("^member account is not associated$")
    public void memberAccountIsNotAssociated() throws Throwable {
        staffMembertoCustomerProfileAssociationHelper.getRegisterStaffFaresService().assertThatErrors().containedTheCorrectErrorMessage("SVC_100269_2013");
    }

    @Then("^member account is associated$")
    public void memberAccountIsAssociated() throws Throwable {
        staffMembertoCustomerProfileAssociationHelper.getRegisterStaffFaresService().assertThat().registrationIsConfirmed();
    }

    @Given("^a valid customer profile has been created$")
    public void aValidCustomerProfileHasBeenCreated() throws Throwable {
        customerHelper.aValidRequestToCreateAProfileForCustomer();
        customerHelper.requestCreationOfACustomerProfile();
        customerHelper.getRegisterCustomerService().assertThat()
                .theCustomerProfileWasCreated(customerHelper.getRequest())
                .additionalInformationReturned("SVC_100047_2038");
        testData.setData(CUSTOMER_ID, customerHelper.getRegisterCustomerService().getResponse().getRegistrationConfirmation().getCustomerId());
    }

}
