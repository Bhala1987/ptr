package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.database.hybris.dao.MembershipDao;
import com.hybris.easyjet.fixture.hybris.helpers.SignificantOthersHelper;
import cucumber.api.PendingException;
import cucumber.api.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by claudiodamico on 07/03/2017.
 */
@ContextConfiguration(classes = TestApplication.class)

public class AddSignificantOthersSteps {

    public static final String COMPLETED = "COMPLETED";
    @Autowired
    private SignificantOthersHelper significantOthersHelper;
    @Autowired
    MembershipDao membershipDao;

    @Given("^I have received a request to add a Significant Other with missing \"([^\"]*)\"$")
    public void iHaveReceivedARequestToAddASignificantOtherWithMissing(String field) throws Throwable {
        significantOthersHelper.createAddRequestWithMissingParameter(field);
    }

    @When("^the Significant Other is added to the Staff Member$")
    public void iProcessTheRequestToAddTheSignificantOther() throws Throwable {
        significantOthersHelper.processAddSignificantOthersRequest();
    }

    @When("^the Significant Other is added to the Staff Member for negative scenario$")
    public void iProcessTheRequestToAddTheSignificantOtherForNegative() throws Throwable {
        significantOthersHelper.setPositiveScenario(false);
        significantOthersHelper.processAddSignificantOthersRequest();
    }

    @Then("^I will receive a \"([^\"]*)\" message to the channel$")
    public void iWillReceiveAMessageToTheChannel(String errorCode) throws Throwable {
        significantOthersHelper.getSignificantOtherService().assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }

    @Given("^I add a Significant Other to the Staff customer$")
    public void iAddASignificantOtherToTheStaffCustomer() throws Throwable {
        significantOthersHelper.createAddRequest();
    }

    @But("^the length of \"([^\"]*)\" is \"([^\"]*)\"$")
    public void theLengthOfIs(String field, String length) throws Throwable {
        significantOthersHelper.changeFieldLength(field, Integer.parseInt(length), SignificantOthersHelper.SignificantOthersServiceType.ADD);
    }

    @Then("^I will get add Significant Other error for field length \"([^\"]*)\"$")
    public void iWillGetAddSignificantOtherErrorForFieldLength(String error) throws Throwable {
        significantOthersHelper.getSignificantOtherService().assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @But("^the request contains \"(.*)\" as field and \"(.*)\" as value$")
    public void theRequestContainsAsFieldAndAsValue(String field, String value) throws Throwable {
        significantOthersHelper.changeFieldValueWithInvalidChars(field, value, SignificantOthersHelper.SignificantOthersServiceType.ADD);
    }

    @But("^the surname does not match the Ej Plus surname membership$")
    public void theSurnameDoesNotMatchTheEjPlusSurnameMembership() throws Throwable {
        significantOthersHelper.createAddRequestWithInvalidEjPlusMembership(SignificantOthersHelper.SignificantOthersServiceType.ADD);
    }

    @And("^the request contains \"([^\"]*)\" which is an email address with an invalid format$")
    public void theRequestContainsWhichIsAnEmailAddressWithAnInvalidFormat(String email) throws Throwable {
        significantOthersHelper.createAddRequestWithInvalidEmail(email, SignificantOthersHelper.SignificantOthersServiceType.ADD);
    }

    @And("^it contains a valid email address$")
    public void theRequestContainsAValidEmailAddress() throws Throwable {
        significantOthersHelper.createAddRequestWithValidEmail(SignificantOthersHelper.SignificantOthersServiceType.ADD);
    }

    @But("^the email is not linked to an existing customer profile$")
    public void theEmailIsNotLinkedToAnExistingCustomerProfile() throws Throwable {
        significantOthersHelper.createAddRequestWithNotLinkedEmail(SignificantOthersHelper.SignificantOthersServiceType.ADD);
    }

    @But("^the email belongs to a customer already linked to an other Staff Customer$")
    public void theEmailBelongsToACustomerAlreadyLinkedToAnOtherStaffCustomer() throws Throwable {
        String email = significantOthersHelper.createAlreadyLinkedStaffCustomerEmail();
        significantOthersHelper.createAddRequestWithAlreadyLinkedEmail(email, SignificantOthersHelper.SignificantOthersServiceType.ADD);
    }

    @And("^the email address is associated to a registered email account$")
    public void theEmailAddressIsAssociatedToARegisteredEmailAccount() throws Throwable {
        // Do nothing, check already in the creation of a request with a valid email
    }

    @And("^the email is not linked to another staff account$")
    public void theEmailIsNotLinkedToAnotherStaffAccount() throws Throwable {
        // Do nothing, check already in the creation of a request with a valid email
    }

    @Then("^the Significant Other is successfully associated to the Staff Customer$")
    public void theSignificantOtherIsSuccessfullyAssociatedToTheStaffCustomer() throws Throwable {
        significantOthersHelper.getSignificantOtherService().assertThat().significantOtherIsSuccessfullyAddedToTheCustomer(significantOthersHelper.getCustomerId());
    }

    @And("^the request contains more than \"([^\"]*)\" SSR codes$")
    public void theRequestContainsMoreThanSSRCodes(String arg0) throws Throwable {
        significantOthersHelper.createAddRequestWithInvalidNumberOfSavedSSRs(Integer.parseInt(arg0));
    }

    @But("^the value for \"([^\"]*)\" is missing$")
    public void theValueForIsMissing(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^the customer is a valid staff customer$")
    public void theCustomerIsAValidStaffCustomer() throws Throwable {
        // Do nothing, check already in the creation of a valid Significant Other request
    }

    @Then("^the significant other information is successfully added to the Staff Customer$")
    public void theSignificantOtherInformationIsSuccessfullyAddedToTheStaffCustomer() throws Throwable {
        significantOthersHelper.getSignificantOtherService().assertThat().significantOtherIsSuccessfullyAddedToTheCustomer(significantOthersHelper.getCustomerId());
    }

    @And("^I have received a valid getCustomerProfile$")
    public void iHaveReceivedAValidGetCustomerProfile() throws Throwable {
        significantOthersHelper.createGetCustomerProfileRequest();
    }

    @When("^I retrieve the customer profile$")
    public void iRetrieveTheCustomerProfile() throws Throwable {
        significantOthersHelper.processGetCustomerProfileRequest();
    }

    @Then("^I will return the updated values$")
    public void iWillReturnTheUpdatedValues() throws Throwable {
        significantOthersHelper.getCustomerProfileService().assertThat().theSignificantOtherIsSuccessfullyAdded();
    }

    @And("^I will see the number of changes still allowed$")
    public void iWillSeeTheNumberOfChangesStillAllowed() throws Throwable {
        significantOthersHelper.getCustomerProfileService().assertThat().theRemainingChangesIsCorrectlyShown();
    }

    @Then("^I will get a Significant Other Invalid character error for \"([^\"]*)\"$")
    public void iWillGetASignificantOtherInvalidCharacterErrorFor(String error) throws Throwable {
        significantOthersHelper.getSignificantOtherService().assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @And("^the request contains a eJPlusMembership Number$")
    public void theRequestContainsAEJPlusMembershipNumber() throws Throwable {
        significantOthersHelper.createAddRequestWithEjPlusMembership(SignificantOthersHelper.SignificantOthersServiceType.ADD,membershipDao.getValidEJPlusMembershipForStaffWithStatus(COMPLETED));
    }

    @And("^the first character \"([^\"]*)\" of the \"([^\"]*)\" is not a \"([^\"]*)\"$")
    public void theFirstCharacterOfTheIsNotA(String arg0, String arg1, String arg2) throws Throwable {
        //Do nothing, check executed in the creation of the request with a ej plus number and invalid length
    }

    @But("^the request contains \"([^\"]*)\" as passenger type and \"([^\"]*)\" as age$")
    public void theRequestContainsAsPassengerTypeAndAsAge(String type, String age) throws Throwable {
        significantOthersHelper.createRequestWithInvalidAge(type, Integer.parseInt(age));
    }

    @Then("^I will return \"([^\"]*)\" as error$")
    public void iWillReturnAsError(String arg0) throws Throwable {
        significantOthersHelper.getSignificantOtherService().assertThatErrors().containedTheCorrectErrorMessage(arg0);
    }

    @Given("^I have a Staff customer with a Significant Other added$")
    public void iHaveAStaffCustomerWithASignificantOtherAdded() throws Throwable {
        significantOthersHelper.createStaffCustomerWithSignificantOther(null);
    }

    @But("^the field (.*) is not valid because it contains (.*)$")
    public void theFieldIsNotValidBecauseItContains(String field, String invalidChar) throws Throwable {
        significantOthersHelper.changeFieldValueWithInvalidChars(field, invalidChar, SignificantOthersHelper.SignificantOthersServiceType.ADD);
    }

    @And("^the request contains a valid Identity Document$")
    public void theRequestContainsAValidIdentityDocument() throws Throwable {
        significantOthersHelper.createAddRequestWithValidIdentityDocument();
    }

    @Given("^I have a Staff customer with a complete Significant Other added$")
    public void iHaveAStaffCustomerWithACompleteSignificantOtherAdded() throws Throwable {
        significantOthersHelper.createStaffCustomerWithCompleteSignificantOther();
    }

    @Then("^I will return the updated values for the complete significant others$")
    public void iWillReturnTheUpdatedValuesForTheCompleteSignificantOthers() throws Throwable {
        significantOthersHelper.getCustomerProfileService().assertThat()
                .theCompleteSignificantOtherIsSuccessfullyAdded(significantOthersHelper.getEmail(), significantOthersHelper.getCustomerWithSignificantOtherId());
    }

    @Then("^I will update to the count of changes allowed$")
    public void iWillUpdateToTheCountOfChangesAllowed() throws Throwable {
        significantOthersHelper.getSignificantOtherService().assertThat().theRemainingChangesAreCorrectlyUpdated(significantOthersHelper.getRemainingChanges());
    }

    @But("^the number of remaining changes for the customer is \"([^\"]*)\"$")
    public void theNumberOfRemainingChangesForTheCustomerIs(String arg0) throws Throwable {
        significantOthersHelper.addXSignificantOthersToTheCustomer(Integer.parseInt(arg0));
    }

    @Then("^the Significant Other user is allow to find flight with (.*) fare$")
    public void theUserGroupOfTheUserWillBeSignificantOtherGroup(String fareType) throws Throwable {
        significantOthersHelper.loginWithLastRequest();
        significantOthersHelper.findAFlightRequest(fareType);
        significantOthersHelper.getFlightService().assertThat().fareTypeIsAvailable(fareType);
    }

    @Given("^a significant other customer$")
    public void aSignificantOtherCustomer() throws Throwable {
        significantOthersHelper.createAddRequestNoLogin();
        significantOthersHelper.createAddRequestWithValidEmail(SignificantOthersHelper.SignificantOthersServiceType.ADD);
        significantOthersHelper.processAddSignificantOthersRequest();
    }

    @And("^I have a valid customer profile$")
    public void ihaveReceiveAValidCustomerProfileRequest() throws Throwable {
        significantOthersHelper.createGetCustomerProfileRequest(significantOthersHelper.getCustomerSignificantOtherId());
    }

    @And("^I get the significant other of the linked staff customer$")
    public void iGetSignificantOtherLinkedStaffCustomer() throws Throwable {
        significantOthersHelper.getCustomerProfileService().assertThat().theSignificantOtherIsInCostumer(significantOthersHelper.getEmail());
    }

    @And("^I login with the significant other customer$")
    public void iLoginwithSignificantOtherCustomer() throws Throwable {
        significantOthersHelper.loginWithLastRequest();
    }

    @When("^I find for a valid Flight with (.*)$")
    public void iFindForAValidFlight(String fareType) throws Throwable {
        significantOthersHelper.findAFlightRequest(fareType);
    }

    @Then("Fare Type (.*) is included in the results$")
    public void fareTypeIncludedInResponse(String fareType) throws Throwable {
        significantOthersHelper.getFlightService().assertThat().fareTypeIsAvailable(fareType);
    }

    @And("^I update Significant Other to delete the email$")
    public void iHaveReceiveAUpdateSignificantOtherRequestToDeleteTheEmail() throws Throwable {
        significantOthersHelper.createUpdateRequestToRemoveEmail();
    }


    @Then("^the previous Significant Other user is not allowed to find a flight with (.*) fare$")
    public void theUserGroupOfTheUserWillBeCostumerGroup(String fareType) throws Throwable {
        significantOthersHelper.loginWithLastRequest();
        significantOthersHelper.findAFlightRequest("");
        significantOthersHelper.getFlightService().assertThat().fareTypeIsNotAvailable(fareType);
    }

    @And("^I have receive a delete Significant Other request$")
    public void iHaveReceiveADeleteSignificantOtherRequest() throws Throwable {
        significantOthersHelper.createDeleteSignificantOthertRequest();
    }

    @But("^([^\"]*) Significant other added even if delete later$")
    public void weHadAddedSignificantOtherPreviouslyEvenIfDeletedLater(String count) throws Throwable {
        significantOthersHelper.addAndDeleteXSignificantOthersToTheCustomer(Integer.parseInt(count));
    }
}

