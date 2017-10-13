package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.PriceOverrideBasketHelper;
import cucumber.api.java.en.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by giuseppecioce on 22/03/2017.
 */
@ContextConfiguration(classes = TestApplication.class)

public class ApplyPriceOverrideToBasketSteps {
    protected static Logger LOG = LogManager.getLogger(ApplyPriceOverrideToBasketSteps.class);

    @Autowired
    private PriceOverrideBasketHelper priceOverrideBasketHelper;
    @Autowired
    private SerenityFacade testData;
    private String channel;

    @Given("^I have in my basket a direct flight with different passenger mix \"([^\"]*)\" using channel \"([^\"]*)\"$")
    public void iHaveInMyBasketADirectFlightWithDifferentPassengerMixUsingChannel(String mix, String channel) throws Throwable {
        testData.setChannel(channel);
        priceOverrideBasketHelper.getBasketHoldItemsHelper().addValidFlightToTheBasket(channel, testData.getFareType(), testData.getJourneyType(), mix, testData.getCurrency());
    }

    @And("^I have added product to my basket$")
    public void iHaveAddedProductToMyBasket() throws Throwable {
        priceOverrideBasketHelper.addItemEachPassenger(testData.getChannel());
    }

    @And("^the channel has initiated a price override$")
    public void theChannelHasInitiatedAPriceOverride() throws Throwable {
        priceOverrideBasketHelper.buildRequestToApplyOverridePrice();
    }

    @But("^the request miss to specify the mandatory field \"([^\"]*)\"$")
    public void theRequestMissToSpecifyTheMandatoryField(String field) throws Throwable {
        priceOverrideBasketHelper.removeFieldFromRequestBodyToApply(field);
    }

    @When("^I receive the request to apply the discount to the basket$")
    public void iReceiveTheRequestToApplyTheDiscountToTheBasket() throws Throwable {
        priceOverrideBasketHelper.invokeApplyOverridePrice(testData.getChannel());
    }

    @Then("^return error messages \"([^\"]*)\" to the channel$")
    public void returnErrorMessagesToTheChannel(String errorCode) throws Throwable {
        priceOverrideBasketHelper.getPriceOverrideBasketService().assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }

    @And("^I have received a removePriceOverride request$")
    public void iHaveReceivedARemovePriceOverrideRequest() throws Throwable {
        priceOverrideBasketHelper.buildRequestToRemoveOverridePrice();
    }

    @When("^I validate the request removePriceOverride$")
    public void iValidateTheRequestRemovePriceOverride() throws Throwable {
        priceOverrideBasketHelper.invokeRemoveOverridePrice(channel);
    }

    @But("^the request to remove the item miss to specify the mandatory field \"([^\"]*)\"$")
    public void theRequestToRemoveTheItemMissToSpecifyTheMandatoryField(String field) throws Throwable {
        priceOverrideBasketHelper.removeFieldFromRequestBodyToRemove(field);
    }

    @Then("^I will add a discount line including the discount reason for the requested amount$")
    public void iWillAddADiscountLineIncludingTheDiscountReasonForTheRequestedAmount() throws Throwable {
        priceOverrideBasketHelper.getPriceOverrideBasketService().assertThat().
                verifyDiscountIsAppliedOnBasketLevel(priceOverrideBasketHelper.getBasketHoldItemsHelper().getBasketResponse(testData.getChannel()), priceOverrideBasketHelper.getDiscountReason());
    }

    @And("^I will update the total price of the basket less the discount$")
    public void iWillUpdateTheTotalPriceOfTheBasketLessTheDiscountBasketPriceDiscountTotalUpdatedPrice() throws Throwable {
        priceOverrideBasketHelper.getPriceOverrideBasketService().assertThat().
                theTotalPriceOfBasketHasBeenUpdatedOnProductLevel(priceOverrideBasketHelper.getBasketHoldItemsHelper().getBasketResponse(testData.getChannel()),
                        priceOverrideBasketHelper.getBasketPriceBeforeApplyOverride(), priceOverrideBasketHelper.getAmountDiscount());
    }

    @When("^I receive a valid request to apply a discount to the passenger$")
    public void iReceiveAValidRequestToApplyADiscountToThePassenger() throws Throwable {
        priceOverrideBasketHelper.invokeApplyOverridePriceOnPassenger(testData.getChannel());
    }

    @Then("^I will add a discount line in the the passenger price including the discount reason for the requested amount$")
    public void iWillAddADiscountLineInTheThePassengerPriceIncludingTheDiscountReasonForTheRequestedAmount() throws Throwable {
        priceOverrideBasketHelper.getPriceOverrideBasketService().assertThat().verifyDiscountIsAppliedOnPassengerLevel(priceOverrideBasketHelper.getBasketHoldItemsHelper().getBasketResponse(testData.getChannel()), priceOverrideBasketHelper.getDiscountReason(), priceOverrideBasketHelper.getPassenger());
    }

    @When("^I receive a valid request to apply a discount to the product$")
    public void iReceiveAValidRequestToApplyADiscountToTheProduct() throws Throwable {
        priceOverrideBasketHelper.invokeApplyOverridePriceOnProduct(testData.getChannel());
    }

    @Then("^I will add a discount line in the the product price including the discount reason for the requested amount$")
    public void iWillAddADiscountLineInTheTheProductPriceIncludingTheDiscountReasonForTheRequestedAmount() throws Throwable {
        priceOverrideBasketHelper.getPriceOverrideBasketService().assertThat().verifyDiscountIsAppliedOnProductLevel(priceOverrideBasketHelper.getDiscountReason(), priceOverrideBasketHelper.getActualPassenger(testData.getChannel()), priceOverrideBasketHelper.getPassengerProductCode());
    }

    @Then("^I will remove the Discount item from the basket$")
    public void iWillRemoveTheDiscountItemFromTheBasket() throws Throwable {
        priceOverrideBasketHelper.getPriceOverrideBasketService().assertThat().verifyDiscountHasBeenRemovedOnBasketLevel(priceOverrideBasketHelper.getBasketHoldItemsHelper().getBasketResponse(channel), priceOverrideBasketHelper.getDiscountReason());
    }


    @And("^Update the total price of the basket$")
    public void updateTheTotalPriceOfTheBasket() throws Throwable {
        priceOverrideBasketHelper.getPriceOverrideBasketService().assertThat().theTotalPriceOfBasketHasBeenUpdated(priceOverrideBasketHelper.getBasketHoldItemsHelper().getBasketResponse(channel), priceOverrideBasketHelper.getBasketPriceAfterApplyOverride(), priceOverrideBasketHelper.getAmountDiscount());
    }

    @Then("^I will remove the Discount item from the basket on product level$")
    public void iWillRemoveTheDiscountItemFromTheBasketOnProductLevel() throws Throwable {
        priceOverrideBasketHelper.getPriceOverrideBasketService().assertThat().verifyDiscountIsRemovedOnProductLevel(priceOverrideBasketHelper.getDiscountReason(), priceOverrideBasketHelper.getPassenger(), priceOverrideBasketHelper.getPassengerProductCode());
    }

    @And("^I will Update the price of the line item$")
    public void iWillUpdateThePriceOfTheLineItem() throws Throwable {
        priceOverrideBasketHelper.getPriceOverrideBasketService().assertThat()
                .verifyThePriceOfProductHasBeenUpdated(
                        priceOverrideBasketHelper.getBasketHoldItemsHelper().getBasketResponse(channel),
                        priceOverrideBasketHelper.getBasketHoldItemsHelper().getProductCode(),
                        priceOverrideBasketHelper.getAmountDiscount(),
                        priceOverrideBasketHelper.getPassengerPriceProductCodeAfterApplyOverride());
    }

    @And("^update the total of the passenger$")
    public void updateTheTotalOfThePassenger() throws Throwable {
        priceOverrideBasketHelper.getPriceOverrideBasketService().assertThat()
                .verifyThePriceOfThePassengerHasBeenDecreased(
                        priceOverrideBasketHelper.getBasketHoldItemsHelper().getBasketResponse(testData.getChannel()),
                        priceOverrideBasketHelper.getAmountDiscount(),
                        priceOverrideBasketHelper.getPassengerPriceBeforeOverridePrice());
    }

    @And("^update the total of the product$")
    public void updateTheTotalOfTheProduct() throws Throwable {
        priceOverrideBasketHelper.getPriceOverrideBasketService().assertThat()
                .verifyThePriceOfProductHasBeenDecreased(
                        priceOverrideBasketHelper.getBasketHoldItemsHelper().getBasketResponse(testData.getChannel()),
                        priceOverrideBasketHelper.getBasketHoldItemsHelper().getProductCode(),
                        priceOverrideBasketHelper.getAmountDiscount(),
                        priceOverrideBasketHelper.getPassengerPriceProductCodeBeforeApplyOverride());
    }
}
