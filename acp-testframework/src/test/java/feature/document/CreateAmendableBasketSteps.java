package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.AmendableBasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BASKET_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BOOKING_ID;
import static feature.document.GlobalHooks.clearCookiesInClient;

/**
 * Created by vijayapalkayyam on 27/07/2017.
 */
@ContextConfiguration(classes = TestApplication.class)
public class CreateAmendableBasketSteps {
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private AmendableBasketHelper amendableBasketHelper;
    @Autowired
    private BasketHelper basketHelper;

    @When("^I request an amendable basket for a (.*)")
    public void iRequestAnAmendableBasketFor(String passengerOrBooking) throws Throwable {
        amendableBasketHelper.createAmendableBasketFor(passengerOrBooking);
    }

    @Then("^I should receive amendable basket$")
    public void iShouldReceiveAmendableBasket() throws Throwable {
        amendableBasketHelper.basketIdIsCreated();
    }

    @And("^the basket should contain linked flights$")
    public void theBasketShouldContainLinkedFlights() {
        amendableBasketHelper.basketHasLinkedFlights();
    }

    @When("^I request an amendable basket for an invalid booking reference$")
    public void iRequestAnAmendableBasketForAnInvalidBookingReference() throws Throwable {
        amendableBasketHelper.createAmendableBasketForInvalidBookingReference();
    }

    @Then("^I should receive an error \"([^\"]*)\"$")
    public void iShouldReceiveAnError(String error) throws Throwable {
        amendableBasketHelper.containedTheCorrectErrorMessage(error);
    }

    @When("^I get the amendable basket$")
    public void iGetTheAmendableBasket() throws Throwable {
        amendableBasketHelper.getBasketForAmendableBasket(testData.getAmendableBasket());
    }

    @And("^I should see that specific passenger details and their associates$")
    public void includingAssociatedPassengerDetails() throws Throwable {
        amendableBasketHelper.passengerDetailsExistsForAllPassengerIncludingAssociates();
    }

    @Then("^I should see all the passenger details$")
    public void iShouldSeeAllThePassengerDetails() throws Throwable {
        amendableBasketHelper.passengerDetailsExistsForAllPassengersInBasket();
    }

    @And("^line item prices will remain unchanged$")
    public void lineItemPricesWillRemainUnchanged() throws Throwable {
        // We do not have to do anything as we are making sure line item price change in the previous step itself
    }

    @When("^I attempt to add (.*) for passenger who (.*) locked$")
    public void iAttemptToAddForPassengerWhoIsNotLocked(String productType, String lockedOrNotPassenger) throws Throwable {
        amendableBasketHelper.addHoldItemForPassenger(productType, lockedOrNotPassenger);
    }

    @Then("^error (.*) should return while adding hold item")
    public void errorShouldReturnWhileAddingHodItem(String error) throws Throwable {
        amendableBasketHelper.containedTheErrorMessageForHoldBag(error);
    }

    @Then("^I should see hold item (.*) added successfully$")
    public void iShouldSeeHoldItemAddedSuccessfully(String productType) throws Throwable {
        amendableBasketHelper.holdItemAddedToTheExpectedPassenger(productType);
    }

    @And("^create an amendable basket for passenger$")
    public void createAnAmendableBasketForPassenger() throws Throwable {
        amendableBasketHelper.createAmendableBasketForASpecificPassengerWithoutReadingFromBooking(new ArrayList<String>() {{
            add(testData.getPassengerId());
        }});
    }

    @And("^I have an existing lock (.*)$")
    public void iHaveExistingLockExistingLock(String passengerOrBooking) throws Throwable {
        iRequestAnAmendableBasketFor(passengerOrBooking);
    }

    @When("^I attempt to lock (.*)$")
    public void iAttemptToLockSecondRequestForLock(String passengerOrBooking) throws Throwable {
        amendableBasketHelper.createAmendableBasketFor(passengerOrBooking, false, true);
    }

    @Then("^I should see the (.*)$")
    public void iShouldSeeThe(String expectedResult) throws Throwable {
        if (expectedResult.equalsIgnoreCase("success") || expectedResult.equalsIgnoreCase("new basket")) {
            amendableBasketHelper.basketIdIsCreated();
        } else {
            amendableBasketHelper.containedTheCorrectErrorMessage(expectedResult);
        }
    }

    @And("^I moved to new session while previous session is still active$")
    public void iMovedToDifferentSession() throws Throwable {
        clearCookiesInClient();
    }

    @When("^I lock whole booking again with override lock (.*)$")
    public void iAttemptToOckWholeBookingAgainWithOverrideLock(String override) throws Throwable {
        amendableBasketHelper.createAmendableBasketFor("whole booking", "true".equalsIgnoreCase(override), true);
    }

    @And("^old amendable basket should be deleted$")
    public void oldAmendableBasketShouldBeDeleted() throws Throwable {
        amendableBasketHelper.oldAmendableBasketShouldHaveBeenDeleted();
    }

    @When("^I add (\\d+) additional fare for each passenger in the amendable basket with invalid (.*)$")
    public void iAddAdditionalFareForEachPassengerInTheAmendableBasketWithInvalidParameter(int addlFare, String parameter) throws Throwable {
        amendableBasketHelper.addAdditionalFareToPassenger(addlFare, parameter);
    }

    @When("^I add (\\d+) additional fare for each passenger in the amendable basket$")
    public void iAddAdditionalFareForEachPassengerInTheAmendableBasket(int addlFare) throws Throwable {
        amendableBasketHelper.addAdditionalFareToPassenger(addlFare, null);
    }

    @And("^the passenger should have additional seat in the basket$")
    public void thePassengerShouldHaveAdditionalSeatInTheBasket() throws Throwable {
        amendableBasketHelper.addAdditionalFareToEachPassenger(basketHelper.getBasket(testData.getData(BASKET_ID), testData.getChannel()));
    }

    @Then("^I should receive a successful operation confirmation response with the basket id$")
    public void successfulOperationConfirmationReponseWithBasketID() throws Throwable {
        amendableBasketHelper.addAdditionalFareToPassengerSuccessBasketID(testData.getData(BASKET_ID));
    }

    @Then("^I should receive an error with error code (.*)$")
    public void iShouldReceiveAnErrorWithErrorCodeErrorCode(String errorCode) throws Throwable {
        amendableBasketHelper.addAdditionalFareToPassengerError(errorCode);
    }

    @And("^I should receive a warning with warning code (.*)$")
    public void iShouldReceiveAWarningWithWarningCode(String warningCode) throws Throwable {
        amendableBasketHelper.addAdditionalFareToPassengerWarning(warningCode);
    }

    @Then("^lock should have removed$")
    public void lockShouldHaveRemoved() throws Throwable {
        oldAmendableBasketShouldBeDeleted();
    }

    @And("^I create amendable basket for the booking created$")
    public void iCreateAmendableBasketForTheBookingCreated() throws Throwable {
        String amendableBasket = basketHelper.createAmendableBasket(testData.getData(BOOKING_ID));
        testData.setBasketId(amendableBasket);
    }

    @And("^I try get invalid amendable basket code$")
    public void iTryGetInvalidAmendableBasketCode() throws Throwable {
        basketHelper.invokeGetBasket(testData.getAmendableBasket() + "1234",testData.getChannel());
    }

    @Then("^I see an invalid basket error$")
    public void iSeeAnInvalidBasketError() throws Throwable {
        basketHelper.getBasketService().assertThatErrors().containedTheCorrectErrorMessage("SVC_100013_1001");
    }

    @And("^have the basket updated to add additional fare$")
    public void haveTheBasketUpdatedToAddAdditionalFare() throws Throwable {
        basketHelper.invokeGetBasket(testData.getBasketId(), testData.getChannel());
    }
}