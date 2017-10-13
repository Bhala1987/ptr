package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.fixture.hybris.helpers.SavedPassengerHelper;
import com.hybris.easyjet.fixture.hybris.invoke.services.GetSavedPassengerService;
import com.hybris.easyjet.fixture.hybris.invoke.services.UpdateSavedPassengerService;
import cucumber.api.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by giuseppecioce on 16/02/2017.
 */
@ContextConfiguration(classes = TestApplication.class)

public class SavedPassengerSteps {

    @Autowired
    private SavedPassengerHelper passengerHelper;

    @Given("^I have added a passenger to an existing customer$")
    public void iHaveAddedAPassengerToAnExistingCustomer() throws Throwable {
        passengerHelper.addValidPassengerToExistingCustomer();
    }

    @And("^I have received a valid updateSavedPassenger request with missing \"([^\"]*)\"$")
    public void iHaveReceivedAValidUpdateSavedPassengerRequestWithMissing(String field) throws Throwable {
        passengerHelper.aInvalidRequestWithMissingFieldForSavedPassenger(field);
    }

    @And("^I have received a valid updateSavedPassenger request$")
    public void iHaveReceivedAValidUpdateSavedPassengeRrequest() throws Throwable {
        passengerHelper.aValidRequestToCreateASavedPassenger();
    }
    @And("^I have received a valid save passenger request$")
    public void iHaveReceivedAValidSavedPassengeRrequest() throws Throwable {
        passengerHelper.aValidRequestToCreateASavedPassenger();
    }


    @And("^I have received a valid updateIdentityDocument request$")
    public void iHaveReceivedAValidUpdateIdentityDocumentRequest() throws Throwable {
        passengerHelper.aValidRequestToCreateAIdentityDocument();
    }

    @And("^I have received a valid updateSSR request$")
    public void iHaveReceivedAValidUpdateSSRRequest() throws Throwable {
        passengerHelper.aValidRequestToCreateASSR();
    }

    @And("^I have added a identity document to an existing passenger$")
    public void iHaveAddedAIdentityDocumentToAnExistingPassenger() throws Throwable {
        passengerHelper.addValidIdentityDocumentToToExistingPassenger();
    }

    @But("^the request contains \"([^\"]*)\" as age and \"([^\"]*)\" as passenger type$")
    public void theRequestContainsAsAgeAndAsPassengerType(int age, String type) throws Throwable {
        passengerHelper.updateTypeAndAgeSavedPassenger(type, age);
    }

    @When("^I process the request for updateSavedPassenger$")
    public void iProcessTheRequestForUpdateSavedPassenger() throws Throwable {
        passengerHelper.addSavedPassengerFromRequest();
    }

    @When("^I process the request for updateIdentityDocument$")
    public void iProcessTheRequestForUpdateIdentityDocument() throws Throwable {
        passengerHelper.addIdentityDocumentFromRequest();
    }

    @When("^I process the request for savedSSR$")
    public void iProcessTheRequestForSavedSSR() throws Throwable {
        passengerHelper.addSSRsFromRequest();
    }

    @But("^the field \"([^\"]*)\" in the request has \"([^\"]*)\" length")
    public void theFieldInTheRequestHasLength(String field, int length) throws Throwable {
        passengerHelper.savedPassengerRequestWithFieldAndFieldLength(field, length);
    }

    @Then("^I will return an \"([^\"]*)\" error$")
    public void iWillReturnAnError(String error) throws Throwable {
        passengerHelper.getSavedPassengerSevice().assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @But("^the field \"([^\"]*)\" in the request contains \"([^\"]*)\" symbol$")
    public void theFieldInTheRequestContainsSymbol(String field, String symbol) throws Throwable {
        passengerHelper.setSavedPassengerProfileFieldWithSymbol(field, symbol);
    }

    @And("^the request contains a SSRs codes$")
    public void theRequestContainsASSRsCodes() throws Throwable {
        // NOTHING TO DO
    }

    @But("^more than \"([^\"]*)\" SSR have been passed in the request$")
    public void moreThanSSRHaveBeenPassedInTheRequest(int number) throws Throwable {
        passengerHelper.addMoreSSRToTheRequest(number);
    }

    @And("^the request contains an email with invalid format \"([^\"]*)\"$")
    public void theRequestContainsAnEmailWithInvalidFormat(String email) throws Throwable {
        passengerHelper.updateEmailSavedPassenger(email);
    }

    @And("^the request contains a eJ Plus Membership Number$")
    public void theRequestContainsAEJPlusMembershipNumber() throws Throwable {
        passengerHelper.addValidEJPlusMembership();
    }

    @And("^the request respect the restriction of the BR$")
    public void theRequestRespectTheRestrictionOfTheBR() throws Throwable {
        passengerHelper.aValidRequestToCreateACompleteSavedPassenger();
    }

    @And("^the \"([^\"]*)\" in the request has \"([^\"]*)\" length$")
    public void theInTheRequestHasLength(String field, int length) throws Throwable {
        passengerHelper.savedPassengerRequestWithFieldAndFieldLength(field, length);
    }

    @But("^the first character \"([^\"]*)\" of the \"([^\"]*)\" passed in the request is not a \"([^\"]*)\"$")
    public void theFirstCharacterOfThePassedInTheRequestIsNotA(String character, String field, String notAllowLetter) throws Throwable {
        if (!character.equalsIgnoreCase(notAllowLetter)) {
            passengerHelper.replaceFirstChar(field, character);
        }
    }

    @And("^the request contains a Date of Birth$")
    public void theRequestContainsADateOfBirth() throws Throwable {
        // NOTHING TO DO
    }

    @Then("^I will store the Saved passenger details$")
    public void iWillStoreTheSavedPassengerDetails() throws Throwable {
        // NOTHING TO DO
    }

    @And("^I will return a \"([^\"]*)\" message$")
    public void iWillReturnAMessage(String successfullMessage) throws Throwable {
        GetSavedPassengerService service = (GetSavedPassengerService) passengerHelper.getSavedPassengerSevice();
        service.assertThat().additionalInformationReturned(successfullMessage);
    }

    @Given("^I have received a valid removeSavedPassenger request$")
    public void iHaveReceivedAValidRemoveSavedPassengerRequest() throws Throwable {
        passengerHelper.prepareRemovePassengerFromCustomer();
    }

    @Then("^I will remove the saved passenger from the customer profile$")
    public void iWillRemoveTheSavedPassengerFromTheCustomerProfile() throws Throwable {
        // NOTHING TO DO
    }

    @When("^I process the request for removeSavedPassenger$")
    public void iProcessTheRequestForRemoveSavedPassenger() throws Throwable {
        passengerHelper.removePassengerFromCustomer();
    }

    @And("^I will verify the successfully response$")
    public void iWillVerifyTheSuccessfullyResponse() throws Throwable {
        UpdateSavedPassengerService service = (UpdateSavedPassengerService) passengerHelper.getSavedPassengerSevice();
        service.assertThat().verifySuccessfullyUpdate(passengerHelper.getCustomerId(), passengerHelper.getPassengerId());
    }

    @Then("^I will store the Identity document details$")
    public void iWillStoreTheIdentityDocumentDetails() throws Throwable {
        // NOTHING TO DO
    }

    @Then("^I will store the SSR data details$")
    public void iWillStoreTheSSRDataDetails() throws Throwable {
        // NOTHING TO DO
    }

    @And("^I will verify the successfully response for the new document$")
    public void iWillVerifyTheSuccessfullyResponseForTheNewDocument() throws Throwable {
        passengerHelper.getUpdateIdentityDocumentService().assertThat().verifySuccessfullyUpdateForIdentityDocument(passengerHelper.getCustomerId(), passengerHelper.getPassengerId(), passengerHelper.getDocumentId());
    }

    @But("^the EJplusmemebership not match the surname of the passenger")
    public void theEJplusmemebershipNotMatchTheSurnameOfThePassenger() throws Throwable {
        // NOTHING TO DO
    }

    @But("^this does not match with the passenger type$")
    public void thisDoesNotMatchWithThePassengerType() throws Throwable {
        passengerHelper.updateDateOfBirth();
    }

    @And("^I am updating SSRs that are mandatory to provide the Ts&Cs parameter$")
    public void iAmUpdatingSSRsThatAreMandatoryToProvideTheTsCsParameter() throws Throwable {
        passengerHelper.aValidRequestToCreateASSRForTsAndCsMandatory(true);
    }

    @When("^I process the request for savedSSR with Ts&Cs parameter$")
    public void iProcessTheRequestForSavedSSRWithTsCsParameter() throws Throwable {
        passengerHelper.addSavedPassengerFromRequest();
    }

    @When("^I don’t include the mandatory Ts and Cs parameter while updating as SSR$")
    public void iDonTIncludeTheMandatoryTsAndCsParameterWhileUpdatingAsSSR() throws Throwable {
        passengerHelper.aValidRequestToCreateASSRForTsAndCsWithoutTsAndCsParameter();
    }

    @And("^I am updating SSRs that are not mandatory to provide the Ts&Cs parameter$")
    public void iAmUpdatingSSRsThatAreNotMandatoryToProvideTheTsCsParameter() throws Throwable {
        passengerHelper.aValidRequestToCreateASSRForTsAndCsMandatory(false);
    }

    @And("^the request contains a eJ Plus Membership Number with status other than (.*)$")
    public void theRequestContainsAEJPlusMembershipNumberWithStatusOtherThanStatus(String status) throws Throwable {
       passengerHelper.updateEjPlusNumberWitthStatus(status);
    }

    @And("^I added a passenger to an existing customer$")
    public void iAddedAPassengerWithEjPlusNumberOtherThanStatusStatusToAnExistingCustomer() throws Throwable {
        passengerHelper.addPassengerToExistingCustomer();
    }
}