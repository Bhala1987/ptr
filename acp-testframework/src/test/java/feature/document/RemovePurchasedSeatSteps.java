package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.FeesAndTaxesDao;
import com.hybris.easyjet.database.hybris.models.FeesAndTaxesModel;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.PurchasedSeatHelper;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.But;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import feature.document.steps.services.createbasketservices.GetBasketSteps;
import net.thucydides.core.annotations.Steps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BASKET_SERVICE;

/**
 * Created by giuseppecioce on 12/04/2017.
 */
@ContextConfiguration(classes = TestApplication.class)

public class RemovePurchasedSeatSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private PurchasedSeatHelper purchasedSeatHelper;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private FeesAndTaxesDao feesAndTaxesDao;
    @Steps
    private GetBasketSteps getBasketSteps;

    @And("^the channel \"([^\"]*)\" has initiated a request to remove a purchased seat$")
    public void theChannelHasInitiatedARequestToRemoveAPurchasedSeatForAPassenger(String channel) throws Throwable {
        purchasedSeatHelper.preRequirementsForRemovePurchasedSeat(testData.getTypeOfSeat());
    }

    @But("^the request miss the the mandatory \"([^\"]*)\" defined in the service contract$")
    public void theRequestMissTheTheMandatoryDefinedInTheServiceContract(String field) throws Throwable {
        purchasedSeatHelper.removeFieldFromRequestBody(field, "");
    }

    @When("^I validate the RemoveSeatProduct request$")
    public void iValidateTheRemoveSeatProductRequest() throws Throwable {
        purchasedSeatHelper.invokeRemovePurchasedSeatService();
    }

    @Then("^I will return an error message \"([^\"]*)\" to the channel$")
    public void iWillReturnAnErrorMessageToTheChannel(String errorCode) throws Throwable {
        purchasedSeatHelper.getPurchasedSeatService().assertThatErrors().containedTheCorrectErrorMessage(errorCode);

    }

    @Then("^I will return a successful remove purchased seat response$")
    public void iWillReturnASuccessfulRemovePurchasedSeatResponse() throws Throwable {
        purchasedSeatHelper.getPurchasedSeatService().assertThat().verifySuccessfullyResponseAfterRemoveSeat(purchasedSeatHelper.getBasketCode());
    }

    @And("^the channel \"([^\"]*)\" has initiated a request to remove a purchased seat on passenger type \"([^\"]*)\"$")
    public void theChannelHasInitiatedARequestToRemoveAPurchasedSeatOnPassengerType(String channel, String type) throws Throwable {
        purchasedSeatHelper.preRequirementsForRemovePurchasedSeatMoreThanOnePassenger(type);
    }

    @Then("^I will remove the purchased seat from the passenger in the basket$")
    public void iWillRemoveThePurchasedSeatFromThePassengerInTheBasket() throws Throwable {
        // NOTHING TO-DO, already covered from the check on all passenger on the flight (above method)
    }

    @And("^I will remove purchased seats for all other passengers on the flight in the basket$")
    public void iWillRemovePurchasedSeatsForAllOtherPassengersOnTheFlightInTheBasket() throws Throwable {
        purchasedSeatHelper.getPurchasedSeatService().assertThat().verifyAllSeatHasBeenRemovedFromPassengers(purchasedSeatHelper.getBasketResponse(purchasedSeatHelper.getBasketCode()),purchasedSeatHelper.getPassengerCode());
    }

    @And("^I will recalculate passenger totals$")
    public void iWillRecalculatePassengerTotals() throws Throwable {
        purchasedSeatHelper.getPurchasedSeatService().assertThat().verifyPriceForPassengerHasBeenUpdateAfterRemoving(purchasedSeatHelper.getBasketAfterAddingPurchasedSeat(), purchasedSeatHelper.getBasketResponse(purchasedSeatHelper.getBasketCode()), purchasedSeatHelper.getSeatTotalPrice(), purchasedSeatHelper.getPassengerCode());
    }

    @And("^I will recalculate basket totals$")
    public void iWillRecalculateBasketTotals() throws Throwable {
        purchasedSeatHelper.getPurchasedSeatService().assertThat().verifyPriceForBasketHasBeenUpdateAfterRemoving(purchasedSeatHelper.getBasketAfterAddingPurchasedSeat().getBasket(), purchasedSeatHelper.getBasketResponse(purchasedSeatHelper.getBasketCode()).getBasket(), purchasedSeatHelper.getSeatTotalPrice());
    }

    @And("^I will recalculate basket totals with (.*)$")
    public void iWillRecalculateBasketTotals(String fareType) throws Throwable {
        BasketService basketService = testData.getData(BASKET_SERVICE);
        Basket basket = basketService.getResponse().getBasket();
        basketHelper.getBasket(basket.getCode());
        FeesAndTaxesModel fee = feesAndTaxesDao.getAdminFees(basket.getCurrency().getCode(), fareType).stream().findFirst().orElse(null);
        Double feeValue;
        if (fee != null) {
            feeValue = fee.getFeeValue();
        } else {
            feeValue = 0.0;
        }
        basketHelper.getBasketService().assertThat().priceCalculationAreRight(Integer.valueOf(basket.getCurrency().getDecimalPlaces()), feeValue);
    }

    @When("^I added the seat and removed it with (.*) and (.*) and (.*)$")
    public void iAddedTheSeatAndRemovedItWithPassengerMixAndFareTypeAndSeat(String passengerMix, String fareType, PurchasedSeatHelper.SEATPRODUCTS aSeatProduct) throws Throwable {
        purchasedSeatHelper.addAndRemoveSeat(passengerMix, fareType, aSeatProduct);
    }

    @Then("^I check the cabin bag$")
    public void iCheckTheCabinBag() throws Throwable {
        purchasedSeatHelper.getPurchasedSeatService().assertThat().checkCabinBagAfterRemoveSeat(purchasedSeatHelper.getBasketAfterAddingPurchasedSeat(), purchasedSeatHelper.getBasketResponse(purchasedSeatHelper.getBasketCode()));
    }

    @When("^I send a request to remove primary seat for (.*) passenger$")
    public void i_send_a_request_to_remove_primary_seat_for_something_passenger(String paxType) throws Throwable {
        purchasedSeatHelper.prepareForRemovePurchasedSeat(paxType);
        purchasedSeatHelper.invokeRemovePurchasedSeatService();
        purchasedSeatHelper.getPurchasedSeatService().assertThat().verifySuccessfullyResponseAfterRemoveSeat(purchasedSeatHelper.getBasketCode());
    }

    @Then("^both primary and additional seat should be removed$")
    public void both_primary_and_additional_seat_should_be_removed() throws Throwable {
        getBasketSteps.sendGetBasketRequest(testData.getData(SerenityFacade.DataKeys.BASKET_ID));
        BasketService basketService = testData.getData(SerenityFacade.DataKeys.BASKET_SERVICE);
        Basket basket = basketService.getResponse().getBasket();

        purchasedSeatHelper.getPurchasedSeatService().assertThat().verifyPrimaryAndAdditionalSeatHasBeenRemovedFromPassenger(basket, purchasedSeatHelper.getPassengerCode());
    }
}
