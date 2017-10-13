package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.fixture.hybris.helpers.DependantsHelpers;
import com.hybris.easyjet.fixture.hybris.helpers.SignificantOthersHelper;
import cucumber.api.PendingException;
import cucumber.api.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by adevanna on 09/03/2017.
 */
@ContextConfiguration(classes = TestApplication.class)

public class UpdateSignificantOthersSteps {

    @Autowired
    private SignificantOthersHelper significantOthersHelper;

    @Autowired
    private DependantsHelpers updateDepedendantsHelpers;
    @Autowired
    private DependantsHelpers dependantsHelpers;


    @Given("^I have received a request to update a Significant Other with missing \"([^\"]*)\"$")
    public void iHaveReceivedARequestToUpdateASignificantOtherWithMissing(String field) throws Throwable {
        significantOthersHelper.createUpdateRequestWithMissingParameter(field);
    }

    @When("^the Significant other is updated$")
    public void iProcessTheRequestToUpdateTheSignificantOther() throws Throwable {
        significantOthersHelper.processUpdateSignificantOthersRequest();
    }

    @Then("^I will receive one \"([^\"]*)\" message to the channel$")
    public void iWillReceiveOneMessageToTheChannel(String error) throws Throwable {
        significantOthersHelper.getSignificantOtherService().assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @Given("^I have received a valid request to update a Significant Others to the Staff customer$")
    public void iHaveReceivedAValidRequestToUpdateASignificantOthersToTheStaffCustomer() throws Throwable {
        significantOthersHelper.createUpdateRequest();
    }

    @But("^the request contains the value \"([^\"]*)\" as age and \"([^\"]*)\" as passenger type$")
    public void theRequestContainsTheValueAsAgeAndAsPassengerType(String arg0, String arg1) throws Throwable {
        significantOthersHelper.createUpdateRequestWithInvalidAge(arg1, Integer.parseInt(arg0));
    }

    @Then("^I will return error \"([^\"]*)\"$")
    public void iWillReturnError(String arg0) throws Throwable {
        significantOthersHelper.getSignificantOtherService().assertThatErrors().containedTheCorrectErrorMessage(arg0);
    }

    @But("^\"([^\"]*)\" length is \"([^\"]*)\"$")
    public void lengthIs(String arg0, String arg1) throws Throwable {
        significantOthersHelper.changeFieldLength(arg0, Integer.parseInt(arg1), SignificantOthersHelper.SignificantOthersServiceType.UPDATE);
    }

    @Then("^I will get update Significant Other error for field length \"([^\"]*)\"$")
    public void iWillGetUpdateSignificantOtherErrorForFieldLength(String arg0) throws Throwable {
        significantOthersHelper.getSignificantOtherService().assertThatErrors().containedTheCorrectErrorMessage(arg0);
    }

    @But("^request contains \"(.*)\" as field and \"(.*)\" as value$")
    public void requestContainsAsFieldAndAsValue(String arg0, String arg1) throws Throwable {
        significantOthersHelper.changeFieldValueWithInvalidChars(arg0, arg1, SignificantOthersHelper.SignificantOthersServiceType.UPDATE);
    }

    @But("^field (.*) is not valid because it contains (.*)$")
    public void fieldIsNotValidBecauseItContains(String field, String invalidChar) throws Throwable {
        significantOthersHelper.changeFieldValueWithInvalidChars(field, invalidChar, SignificantOthersHelper.SignificantOthersServiceType.UPDATE);
    }

    @Then("^I will get a Invalid char error for \"([^\"]*)\"$")
    public void iWillGetAInvalidCharErrorFor(String arg0) throws Throwable {
        significantOthersHelper.getSignificantOtherService().assertThatErrors().containedTheCorrectErrorMessage(arg0);
    }

    @And("^the request contains an eJ Plus Membership Number$")
    public void theRequestContainsAnEJPlusMembershipNumber() throws Throwable {
        significantOthersHelper.createAddRequestWithEjPlusMembership(SignificantOthersHelper.SignificantOthersServiceType.UPDATE);
    }
    @And("^the request contains an eJ Plus Membership Number with status other than (.*)$")
    public void theRequestContainsAnEJPlusMembershipNumberWithStatus(String status) throws Throwable {
        significantOthersHelper.createAddRequestWithEjPlusMembershipWithStatus(SignificantOthersHelper.SignificantOthersServiceType.UPDATE,status);
    }


    @But("^the first character \"([^\"]*)\" of the \"([^\"]*)\" passed in the request is not a 'S'$")
    public void theFirstCharacterOfThePassedInTheRequestIsNotAS(String arg0, String arg1) throws Throwable {
        // Do nothing, check already in the creation of a request with a valid email
    }

    @But("^the \"([^\"]*)\" is not valid because contains \"([^\"]*)\"$")
    public void theIsNotValidBecauseContains(String arg0, String arg1) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @But("^surname does not match the Ej Plus surname membership$")
    public void surnameDoesNotMatchTheEjPlusSurnameMembership() throws Throwable {
        significantOthersHelper.createAddRequestWithInvalidEjPlusMembership(SignificantOthersHelper.SignificantOthersServiceType.UPDATE);
    }

    @And("^request contains \"([^\"]*)\" which is an email address with an invalid format$")
    public void requestContainsWhichIsAnEmailAddressWithAnInvalidFormat(String arg0) throws Throwable {
        significantOthersHelper.createAddRequestWithInvalidEmail(arg0, SignificantOthersHelper.SignificantOthersServiceType.UPDATE);
    }

    @And("^request contains a valid email address$")
    public void requestContainsAValidEmailAddress() throws Throwable {
        significantOthersHelper.createAddRequestWithValidEmail(SignificantOthersHelper.SignificantOthersServiceType.UPDATE);
    }

    @But("^email is not linked to an existing customer profile$")
    public void emailIsNotLinkedToAnExistingCustomerProfile() throws Throwable {
        significantOthersHelper.createAddRequestWithNotLinkedEmail(SignificantOthersHelper.SignificantOthersServiceType.UPDATE);
    }

    @But("^the belongs to a customer already linked to an other Staff Customer$")
    public void theBelongsToACustomerAlreadyLinkedToAnOtherStaffCustomer() throws Throwable {
        String email = significantOthersHelper.createAlreadyLinkedStaffCustomerEmail();
        significantOthersHelper.createAddRequestWithAlreadyLinkedEmail(email, SignificantOthersHelper.SignificantOthersServiceType.UPDATE);
    }

    @And("^email address is associated to a registered email account$")
    public void emailAddressIsAssociatedToARegisteredEmailAccount() throws Throwable {
        // Do nothing, check already in the creation of a request with a valid email
    }

    @And("^email is not linked to another staff account$")
    public void emailIsNotLinkedToAnotherStaffAccount() throws Throwable {
        // Do nothing, check already in the creation of a request with a valid email
    }

    @Then("^Significant Other is successfully associated to the Staff Customer$")
    public void significantOtherIsSuccessfullyAssociatedToTheStaffCustomer() throws Throwable {
        significantOthersHelper.getSignificantOtherService().assertThat()
                .significantOtherIsSuccessfullyUpdatedToTheCustomer(significantOthersHelper.getCustomerWithSignificantOtherId(), significantOthersHelper.getPassengerId());
    }

    @And("^request contains more than \"([^\"]*)\" SSR codes$")
    public void requestContainsMoreThanSSRCodes(String arg0) throws Throwable {
        significantOthersHelper.createUpdateRequestWithInvalidNumberOfSavedSSRs(Integer.parseInt(arg0));
    }

    @When("^I process the request to update a Significant Other$")
    public void iProcessTheRequestToUpdateASignificantOther() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^I will generate an \"([^\"]*)\" error$")
    public void iWillGenerateAnError(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^I have received a valid update Identity Document request$")
    public void iHaveReceivedAValidUpdateIdentityDocumentRequest() throws Throwable {
        significantOthersHelper.createUpdateIdentityDocumentRequest();
    }

    @When("^I process the request for update Identity Document$")
    public void iProcessTheRequestForUpdateIdentityDocument() throws Throwable {
        significantOthersHelper.processUpdateSignificantOthersDocumentRequest();
    }

    @When("^I process the request for delete Identity Document$")
    public void iProcessTheRequestForDeleteIdentityDocument() throws Throwable {
        significantOthersHelper.processDeleteSignificantOthersDocumentRequest();
    }

    @Then("^I will return a \"([^\"]*)\" error$")
    public void iWillReturnAError(String errorCode) throws Throwable {
        if(significantOthersHelper.getSignificantOtherIdDocumentService()!=null)
        significantOthersHelper.getSignificantOtherIdDocumentService().assertThatErrors().containedTheCorrectErrorMessage(errorCode);
        else
            dependantsHelpers.getUpdateDependantsService().assertThatErrors().containedTheCorrectErrorMessage(errorCode);

    }
    @Then("^I will return a \"([^\"]*)\" error for update dependant$")
    public void iWillReturnAErrorDependant(String errorCode) throws Throwable {
            dependantsHelpers.getUpdateDependantsService().assertThatErrors().containedTheCorrectErrorMessage(errorCode);

    }
    @But("^field \"([^\"]*)\" in the request contains \"([^\"]*)\" symbol$")
    public void fieldInTheRequestContainsSymbol(String arg0, String arg1) throws Throwable {
        significantOthersHelper.changeFieldValueWithSpecialChars(arg0, arg1, SignificantOthersHelper.SignificantOthersServiceType.ADD);
    }

    @And("^request contains a Date of Birth$")
    public void requestContainsADateOfBirth() throws Throwable {
        // Do nothing, check already in the creation of the identity document
    }

    @But("^this does not match the passenger type$")
    public void thisDoesNotMatchThePassengerType() throws Throwable {
        significantOthersHelper.changeDocumentDateOfBirth(SignificantOthersHelper.SignificantOthersServiceType.UPDATE);
    }

    @When("^I process the request to remove the identity document$")
    public void iProcessTheRequestToRemoveTheIdentityDocument() throws Throwable {
        significantOthersHelper.processDeleteSignificantOthersDocumentRequest();
    }

    @Then("^the specified identity document information of the Significant in Customer profile are successfully removed$")
    public void theSpecifiedIdentityDocumentInformationOfTheSignificantInCustomerProfileAreSuccessfullyRemoved() throws Throwable {
        significantOthersHelper.getSignificantOtherIdDocumentService().assertThat()
                .documentIsSuccessfullyRemovedFromTheSignificantOther(significantOthersHelper.getCustomerWithSignificantOtherId(), significantOthersHelper.getPassengerId());
        significantOthersHelper.createGetCustomerProfileRequest();
        significantOthersHelper.processGetCustomerProfileRequest();
        significantOthersHelper.getCustomerProfileService().assertThat()
                .documentIsSuccessfullyRemovedFromTheSignificantOther(significantOthersHelper.getCustomerWithSignificantOtherId(), significantOthersHelper.getPassengerId());
    }

    @Given("^I have received a request to remove all the identity documents$")
    public void iHaveReceivedARequestToRemoveAllTheIdentityDocuments() throws Throwable {
        significantOthersHelper.createDeleteAllDocumentRequest();
    }

    @And("^customer is a valid staff customer$")
    public void customerIsAValidStaffCustomer() throws Throwable {
        // Do nothing, check already in the creation of the staff customer
    }

    @Then("^the significant other information is successfully updated to the Staff Customer$")
    public void theSignificantOtherInformationIsSuccessfullyUpdatedToTheStaffCustomer() throws Throwable {
        significantOthersHelper.getSignificantOtherService().assertThat()
                .significantOtherIsSuccessfullyUpdatedToTheCustomer(significantOthersHelper.getCustomerWithSignificantOtherId(), significantOthersHelper.getPassengerId());
    }

    @And("^a confirmation to the channel is returned$")
    public void aConfirmationToTheChannelIsReturned() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^the significant other information is successfully deleted from the Staff Customer$")
    public void theSignificantOtherInformationIsSuccessfullyDeletedFromTheStaffCustomer() throws Throwable {
        significantOthersHelper.getSignificantOtherService().assertThat()
                .significantOtherIsSuccessfullyRemovedFromTheCustomer(significantOthersHelper.getCustomerWithSignificantOtherId(), significantOthersHelper.getPassengerId());
    }

    @And("^there are one or more validation error messages$")
    public void thereAreOneOrMoreValidationErrorMessages() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^failed values should not be updated$")
    public void failedValuesShouldNotBeUpdated() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^validation errors should be returned$")
    public void validationErrorsShouldBeReturned() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Given("^a valid getCustomerProfile$")
    public void aValidGetCustomerProfile() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^I retrieve customer profile$")
    public void iRetrieveCustomerProfile() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^I will return updated values$")
    public void iWillReturnUpdatedValues() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^I will see number of changes still allowed$")
    public void iWillSeeNumberOfChangesStillAllowed() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Given("^I have added a staff Customer with a Significant Other$")
    public void iHaveAddedAStaffCustomerWithASignificantOther() throws Throwable {
        significantOthersHelper.createStaffCustomerWithSignificantOther(null);
    }

    @And("^I have received a valid add Identity Document request to the Significant Other$")
    public void iHaveReceivedAValidAddIdentityDocumentRequestToTheSignificantOther() throws Throwable {
        significantOthersHelper.createAddIdentityDocumentRequest();
    }

    @And("^I have received a valid update Identity Document request for the Significant Other$")
    public void iHaveReceivedAValidUpdateIdentityDocumentRequestForTheSignificantOther() throws Throwable {
        significantOthersHelper.createUpdateIdentityDocumentRequest();
    }

    @When("^I process the request for add Identity Document$")
    public void iProcessTheRequestForAddIdentityDocument() throws Throwable {
        significantOthersHelper.processAddSignificantOthersDocumentRequest();
    }

    @And("^I have received a valid add Identity Document request$")
    public void iHaveReceivedAValidAddIdentityDocumentRequest() throws Throwable {
        significantOthersHelper.createAddIdentityDocumentRequest();
    }

    @Given("^I have added a staff Customer with a Significant Other and an Identity Document$")
    public void iHaveAddedAStaffCustomerWithASignificantOtherAndAnIdentityDocument() throws Throwable {
        significantOthersHelper.createSignificantOthersWithIdentityDocuments();
    }

    @And("^I have received a request to remove a single identity document$")
    public void iHaveReceivedARequestToRemoveASingleIdentityDocument() throws Throwable {
        significantOthersHelper.createDeleteDocumentRequest();
    }

    @And("^I delete a Significant Other to the Staff customer$")
    public void iHaveReceivedAValidRequestToDeleteASignificantOtherToTheStaffCustomer() throws Throwable {
        significantOthersHelper.createDeleteSignificantOthertRequest();
    }

    @When("^the Significant other is deleted$")
    public void iProcessTheRequestForDeleteSignificantOthers() throws Throwable {
        significantOthersHelper.processDeleteSignificantOthersRequest();
    }

    @Then("^I will return updated values for significant other$")
    public void iWillReturnUpdatedValuesForSignificantOther() throws Throwable {
        significantOthersHelper.getCustomerProfileService().assertThat().theSignificantOtherIsSuccessfullyUpdated();

    }

    @But("^\"([^\"]*)\" length for identity document is \"([^\"]*)\"$")
    public void lengthForIdentityDocumentIs(String arg0, String arg1) throws Throwable {
        significantOthersHelper.changeIdentityDocumentFieldLength(arg0, Integer.parseInt(arg1), SignificantOthersHelper.SignificantOthersServiceType.ADD);
    }

    @Given("^I have received a valid request to update a the ssrs for a Significant Others to the Staff customer$")
    public void iHaveReceivedAValidRequestToUpdateATheSsrsForASignificantOthersToTheStaffCustomer() throws Throwable {
        significantOthersHelper.createSSRsUpdateRequest();
    }

    @But("^\"([^\"]*)\" length for id document is \"([^\"]*)\"$")
    public void lengthForIdDocumentIs(String arg0, String arg1) throws Throwable {
        significantOthersHelper.changeIdentityDocumentFieldLength(arg0, Integer.parseInt(arg1), SignificantOthersHelper.SignificantOthersServiceType.UPDATE);
    }

    @But("^field \"([^\"]*)\" in the request contains \"([^\"]*)\"$")
    public void fieldInTheRequestContains(String arg0, String arg1) throws Throwable {
        significantOthersHelper.changeFieldValueWithSpecialChars(arg0, arg1, SignificantOthersHelper.SignificantOthersServiceType.UPDATE);
    }

    @And("^I process the request to update Significant other$")
    public void iProcessTheRequestToUpdateSignificantOther() throws Throwable {
        significantOthersHelper.processUpdateSignificantOthersRequest();
    }

    @Given("^I have deleted a significant other$")
    public void iHaveDeletedASignificantOther() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^I process the request to delete SignificantOthers$")
    public void iProcessTheRequestToDeleteSignificantOthers() throws Throwable {
        significantOthersHelper.processDeleteSignificantOthersRequest();
    }

    @When("^I add a new significant other to the same or another customer$")
    public void iAddANewSignificantOtherToTheSameOrAnotherCustomer() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^I use the deleted Significant Other’s email$")
    public void iUseTheDeletedSignificantOtherSEmail() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^SignificantOther should be added or updated$")
    public void significantotherShouldBeAddedOrUpdated() throws Throwable {
        significantOthersHelper.getSignificantOtherService().assertThat().significantOtherIsSuccessfullyAddedToTheCustomer(significantOthersHelper.getCustomerWithSignificantOtherId());
    }

    @Given("^I have added a staff Customer with a Significant Other with email$")
    public void iHaveAddedAStaffCustomerWithASignificantOtherWithEmail() throws Throwable {
        significantOthersHelper.createStaffCustomerWithSignificantOtherWithEmail();
    }

    @And("^I have received an add request to add the same Significant Other with the email$")
    public void iHaveReceivedAnAddRequestToAddTheSameSignificantOtherWithTheEmail() throws Throwable {
        significantOthersHelper.createAddSameCustomerRequest();
    }

    @When("^I process the request to add the same Significant Other$")
    public void iProcessTheRequestToAddTheSameSignificantOther() throws Throwable {
        significantOthersHelper.processAddSignificantOthersRequest();
    }


}
