package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHoldItemsHelper;
import cucumber.api.java.en.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by giuseppecioce on 01/03/2017.
 */
@ContextConfiguration(classes = TestApplication.class)

public class AddSportEquipmentToBasketSteps {
    protected static Logger LOG = LogManager.getLogger(AddSportEquipmentToBasketSteps.class);

    @Autowired
    private BasketHoldItemsHelper basketHoldItemsHelper;
    @Autowired
    private SerenityFacade testData;
    private String channel;
    private int threshold;

    @Given("^I have a valid flight in my basket for the channel \"([^\"]*)\"$")
    public void iHaveAValidFlightInMyBasketForTheChannel(String channel) throws Throwable {
        String bundle = "Standard";
        String journey = "SINGLE";
        String passengerMix = "2 Adult, 2 Child";
        String currency = "GBP";

        this.channel = channel;
        basketHoldItemsHelper.addValidFlightToTheBasket(channel, bundle, journey, passengerMix, currency);
    }

    @And("^I have received a valid addSportsEquipment request for the channel \"([^\"]*)\"$")
    public void iHaveReceivedAValidAddSportsEquipmentRequestForTheChannel(String channel) throws Throwable {
        basketHoldItemsHelper.buildRequestToAddSportEquipment(channel);
    }

    @And("^I have received a valid addSportEquipment request for the channel \"([^\"]*)\"$")
    public void iHaveReceivedAValidAddSportEquipmentRequestForTheChannel(String channel) throws Throwable {
        basketHoldItemsHelper.buildRequestToAddSportEquipment(channel);
        this.channel = channel;
    }

    @But("^request miss the mandatory field \"([^\"]*)\" defined in the service contract$")
    public void theRequestMissTheMandatoryFieldDefinedInTheServiceContract(String field) throws Throwable {
        basketHoldItemsHelper.removeFieldFromRequestBody(field);
    }

    @When("^I valid the request to addSportsEquipment$")
    public void iValidTheRequestToAddSportsEquipment() throws Throwable {
        if (channel == null) {
            channel = testData.getChannel();
        }
            basketHoldItemsHelper.invokeServiceAddSportItems(channel);
    }

    @Then("^I will return a message \"([^\"]*)\" to the channel$")
    public void iWillReturnAMessageToTheChannel(String error) throws Throwable {
        basketHoldItemsHelper.getAddSportToBasketService().assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @Given("^I have a valid flight in my basket for the channel \"([^\"]*)\" with passenger mix \"([^\"]*)\"$")
    public void iHaveAValidFlightInMyBasketForTheChannelWithPassengerMix(String channel, String mix) throws Throwable {
        String bundle = "Standard";
        String journey = "SINGLE";
        String currency = "GBP";
        this.channel = channel;
        basketHoldItemsHelper.addValidFlightToTheBasket(channel, bundle, journey, mix, currency);
    }

    @And("^I have received a valid request for item \"([^\"]*)\" for the channel \"([^\"]*)\"$")
    public void iHaveReceivedAValidRequestForItemForTheChannel(String item, String channel) throws Throwable {
        basketHoldItemsHelper.buildRequestToAddSportEquipment(item, channel);
    }

    @But("^the request exceeds the threshold set for the item \"([^\"]*)\" and the passenger mix \"([^\"]*)\"$")
    public void theRequestExceedsTheThresholdSetForTheItemAndThePassengerMix(String group, String mix) throws Throwable {
         threshold = basketHoldItemsHelper.getThresholdForPassengerMix(channel, mix, group);
    }

    @And("^request contains a valid threshold set for the item \"([^\"]*)\" and the passenger mix \"([^\"]*)\"$")
    public void theRequestContainsAValidThresholdSetForTheItemAndThePassengerMix(String group, String mix) throws Throwable {
        int threshold = basketHoldItemsHelper.getThresholdForPassengerMix(channel, mix, group);
        basketHoldItemsHelper.updateQuantityAllowPerPassenger(threshold);
    }

    @And("^add the price of the hold item to the basket total$")
    public void addThePriceOfTheHoldItemToTheBasketTotal() throws Throwable {
        basketHoldItemsHelper.getAddSportToBasketService().assertThat().verifySportItemInTheBasket(basketHoldItemsHelper.getBasketId(), basketHoldItemsHelper.getBasketResponse(channel));
    }

    @And("^request contains an inventory that exceeds the cap$")
    public void requestContainsAnInventoryThatExceedsTheCap() throws Throwable {
        basketHoldItemsHelper.updateQuantityOverThresholdPerFlight();
    }

    @Then("^I will create a order line for each sector and passenger$")
    public void iWillCreateAOrderLineForEachSectorAndPassenger() throws Throwable {
        basketHoldItemsHelper.getAddSportToBasketService().assertThat().verifyOrderIsCreatedForEachPassenger(basketHoldItemsHelper.getBasketResponse(channel), basketHoldItemsHelper.getPassengerCode(), basketHoldItemsHelper.getProductCode());
    }

    @And("^update the total price of the basket$")
    public void updateTheTotalPriceOfTheBasket() throws Throwable {
        basketHoldItemsHelper.getAddSportToBasketService().assertThat().verifyBasketPriceIsUpdate(basketHoldItemsHelper.getBasketResponse(channel), basketHoldItemsHelper.getBasketPrice(), basketHoldItemsHelper.getPassengerCode(), basketHoldItemsHelper.getProductCode());
    }

    @Then("^I will decrement the stock level for the flight for the number of requested hold items$")
    public void iWillDecrementTheStockLevelForTheFlightForTheNumberOfRequestedHoldItems() throws Throwable {
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().verifyStockLevelDecrease(
                basketHoldItemsHelper.getActualReservedItem(),
                basketHoldItemsHelper.getReservedAllocation(),
                1);
    }

    @And("^request contains an override flag based if the channel is allowed to override the message$")
    public void requestContainsAnOverrideFlagBasedIfTheChannelIsAllowedToOverrideTheMessage() throws Throwable {
        basketHoldItemsHelper.enableOverrideWarning();
    }

    @Then("^I will verify the stock level is the same$")
    public void iWillVerifyTheStockLevelIsTheSame() throws Throwable {
        basketHoldItemsHelper.getAddSportToBasketService().assertThat().verifyStockLevelIsTheSame(basketHoldItemsHelper.getActualReservedItem(), basketHoldItemsHelper.getReservedAllocation());
    }

    @Then("^I will return the price including credit card fee depending on the channel$")
    public void iWillReturnThePriceIncludingCreditCardFeeDependingOnTheChannel() throws Throwable {
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().verifySportItemInTheBasket(basketHoldItemsHelper.getBasketId(), basketHoldItemsHelper.getBasketResponse(channel));
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().verifyBasketPriceIsUpdate(basketHoldItemsHelper.getBasketResponse(channel), basketHoldItemsHelper.getBasketPrice(), basketHoldItemsHelper.getPassengerCode(), basketHoldItemsHelper.getProductCode());
    }
    @And("^the sport item will not be added to the basket$")
    public void theSportItemWillNotBeAddedToTheBasket() throws Throwable {
        basketHoldItemsHelper.getAddSportToBasketService().assertThatErrorsOverride().verifyBasketNotContainProductCode(basketHoldItemsHelper.getBasketResponse(channel), basketHoldItemsHelper.getFlightKey(), basketHoldItemsHelper.getProductCode());
    }

    @Given("^I have a valid flight in my basket for the channel \"([^\"]*)\" with currency \"([^\"]*)\"$")
    public void iHaveAValidFlightInMyBasketForTheChannelWithCurrency(String channel, String currency) throws Throwable {
        String bundle = "Standard";
        String journey = "SINGLE";
        String passengerMix = "2 Adult, 2 Child";
        this.channel = channel;
        basketHoldItemsHelper.addValidFlightToTheBasket(channel, bundle, journey, passengerMix, currency);
    }
}
