package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.ManageAdditionalFareToPassengerInBasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.PurchasedSeatHelper;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;


/**
 * Created by robertadigiorgio on 21/03/2017.
 */
@ContextConfiguration(classes = TestApplication.class)

public class AddFlightWithAnAdditionalFareToTheBasketSteps {

    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private ManageAdditionalFareToPassengerInBasketHelper manageAdditionalFareToPassengerInBasketHelper;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private PurchasedSeatHelper purchasedSeatHelper;

    @Given("^I sent a request to FindFlight to \"([^\"]*)\"$")
    public void iSentARequestToFindFlightTo(String channel) throws Throwable {
        manageAdditionalFareToPassengerInBasketHelper.findFlight("3 Adult", channel, "Standard");
    }

    @When("^I sent a request to AddFlight with additional seat to \"([^\"]*)\"$")
    public void iSentARequestToAddFlightWithAdditionalSeatTo(String channel) throws Throwable {
        basketHelper.addFlightToBasketWithAdditionalSeat(
                manageAdditionalFareToPassengerInBasketHelper.getFlightsService().getOutboundFlights(),
                manageAdditionalFareToPassengerInBasketHelper.getFlightsService().getResponse().getCurrency(),
                "1,2 adult; 1,2 child; 1,0 infant",
                "single",
                "Standard");
    }

    @Then("^I will generate a error message to inform the channel \"([^\"]*)\"$")
    public void iWillGenerateAErrorMessageToInformTheChannel(String code) throws Throwable {
        basketHelper.getBasketService().assertThatErrors().containedTheCorrectErrorMessage(code);
    }

    @Then("^I will request allocation for the Passenger including the additional seat$")
    public void iWillRequestAllocationForThePassengerIncludingTheAdditionalSeat() throws Throwable {

    }

    @Then("^I will return an updated basket$")
    public void iWillReturnAnUpdatedBasket() throws Throwable {
        basketHelper.getBasketService().assertThat().theBasketNotIsEmpty();
    }

    @And("^I will associate the additional seat with the respective passenger$")
    public void iWillAssociateTheAdditionalSeatWithTheRespectivePassenger() throws Throwable {
        basketHelper.getBasketService().assertThat().theAdditionalSeatsIsAdded();
    }

    @And("^I will recalculate passenger totals, flight totals and basket totals$")
    public void iWillRecalculatePassengerTotalsFlightTotalsAndBasketTotals() throws Throwable {
        manageAdditionalFareToPassengerInBasketHelper.calculateBasketAllTotals();
    }

    @Given("^I have found a valid flight and added to basket for (.*) for (.*)$")
    public void iHaveFoundAValidFlightAndAddedToBasketForPassengerMix(String passengerMix, String channel) throws Throwable {
        manageAdditionalFareToPassengerInBasketHelper.findAndAddFlight(passengerMix, channel);
    }

    @When("^I send addAdditionalFareToPassenger request for additional fares of passengers (.*) for (.*)$")
    public void iReceiveAddAdditionalFareToPassengerRequestForAdditionalFaresOfPassengersPassengerTypeForChannel(String additionalSeatMix, String channel) throws Throwable {
        manageAdditionalFareToPassengerInBasketHelper.addAdditionalFareToPassenger(additionalSeatMix, channel, "");
    }

    @Then("^I will return success response and an updated basket with additional fares added$")
    public void iWillReturnSuccessResponseAndAnUpdatedBasketWithAdditionalFaresAdded() throws Throwable {
        manageAdditionalFareToPassengerInBasketHelper.getManageAdditionalFareToPassengerInBasketService().getResponse().getOperationConfirmation().getBasketCode().equalsIgnoreCase(basketHelper.getBasketService().getResponse().getBasket().getCode());
        manageAdditionalFareToPassengerInBasketHelper.addAdditionalFareToPassengerInBasketSuccess();
    }

    @When("^I send the addAdditionalFareToPassenger request for additional fares of passengers (.*) for (.*) and invalid (.*)$")
    public void iReceiveTheAddAdditionalFareToPassengerRequestForAdditionalFaresOfPassengersPassengerTypeForChannelAndInvalidParameter(String additionalSeatMix, String channel, String parameter) throws Throwable {
        manageAdditionalFareToPassengerInBasketHelper.addAdditionalFareToPassenger(additionalSeatMix, channel, parameter);
    }

    @Then("^I will return the \"([^\"]*)\" in the addAdditionalFareToPassenger service response$")
    public void iWillReturnTheInTheAddAdditionalFareToPassengerServiceResponse(String errorCode) throws Throwable {
        manageAdditionalFareToPassengerInBasketHelper.getManageAdditionalFareToPassengerInBasketService().assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }

    @Then("^response returns \"([^\"]*)\"$")
    public void responseReturns(String errorCode) throws Throwable {
        manageAdditionalFareToPassengerInBasketHelper.getAddAdditionalFareToPassengerInBasketService().assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }


    @Then("^additionalfare service returns \"([^\"]*)\"$")
    public void additionalfareServiceReturns(String errorcode) throws Throwable {
        manageAdditionalFareToPassengerInBasketHelper.getManageAdditionalFareToPassengerInBasketService().assertThatErrors().containedTheCorrectErrorMessage(errorcode);
    }

    @And("^I add additonal fare for \"([^\"]*)\" the passengers$")
    public void iAddAdditonalFareForThePassengers(String noOfPax) throws Throwable {
        if (noOfPax.contains("1")){
            String passengerID  =basketHelper.getBasketService().getResponse().getBasket()
                    .getOutbounds().stream()
                    .flatMap(f -> f.getFlights().stream())
                    .flatMap(p -> p.getPassengers().stream())
                    .findFirst()
                    .get().getCode();

            manageAdditionalFareToPassengerInBasketHelper.addAdditionalFareToPassengerNew(passengerID);
        }else {
            manageAdditionalFareToPassengerInBasketHelper.additionalFareToPassengerInBasketHelperMultiplePassengers();
        }

        purchasedSeatHelper.addStandardSeatAndAdditionalFare(testData.getTypeOfSeat(),noOfPax);
    }

    @When("^add additionalfare to booking \"([^\"]*)\"$")
    public void addAdditionalfareToBooking(String noOfPax) throws Throwable {
        if (noOfPax.contains("1")) {
            String passengerID = basketHelper.getBasketService().getResponse().getBasket()
                    .getOutbounds().stream()
                    .flatMap(f -> f.getFlights().stream())
                    .flatMap(p -> p.getPassengers().stream())
                    .findFirst()
                    .get().getCode();

            manageAdditionalFareToPassengerInBasketHelper.addAdditionalFareToPassengerNew(passengerID);
        }
    }
}
