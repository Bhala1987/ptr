package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHoldItemsHelper;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import cucumber.api.java.en.*;
import org.apache.commons.exec.util.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by rajakm on 07/03/2017.
 */

@ContextConfiguration(classes = TestApplication.class)

public class AddHoldItemsToBasketSteps {
    protected static Logger LOG = LogManager.getLogger(AddSportEquipmentToBasketSteps.class);
    String passengerType;

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BasketHoldItemsHelper basketHoldItemsHelper;
    @Autowired
    private BasketHelper basketHelper;
    private String channel;
    private int threshold;
    private int thresholdForHoldBags;
    private int thresholdForSportsEqiupment;
    private String numberOfPassenger;
    private String holdItemIndex;
    private int itemIndex;

    @Given("^I have added a valid flight to my basket for the channel \"([^\"]*)\" and the bundle as \"([^\"]*)\"$")
    public void iHaveAddedAValidFlightToMyBasketForTheChannelAndTheBundle(String channel, String bundle) throws Throwable {
        String journey = "SINGLE";
        String passengerMix = "2 Adult, 2 Child";
        String currency = "GBP";
        this.channel = channel;
        basketHoldItemsHelper.addValidFlightToTheBasket(channel, bundle, journey, passengerMix, currency);
    }

    @Given("^I have added the flight with passengers as \"(.*)\" and from the channel \"(.*)\" to the basket$")
    public void i_have_added_the_flight_with_passengers_as_and_from_the_channel_to_the_basket(String passengerMix, String channel) throws Throwable {
        String bundleType = "Standard";
        String journey = "SINGLE";
        String currency = "GBP";
        this.channel = channel;
        passengerType = passengerMix.split("\\s+")[1];
        basketHelper.myBasketContainsAReturnFlightWithPassengerMix(passengerMix, channel);
    }

    @Given("^I have added a flight with the \"([^\"]*)\" bundle and passengers as (.*) and from the channel (.+) to the basket$")
    public void i_have_added_a_flight_with_the_bundle_and_passengers_as_and_from_the_channel_to_the_basket(String bundle, String passengerMix, String channel) throws Throwable {
        String journey = "SINGLE";
        String currency = "GBP";
        this.channel = channel;
        basketHoldItemsHelper.addValidFlightToTheBasket(channel, bundle, journey, passengerMix, currency);
    }

    @Given("^I have added \"([^\"]*)\" flights with passengers as \"([^\"]*)\" and from the channel (.+) to the basket$")
    public void i_have_added_flight_with_passengers_as_and_from_the_channel_to_the_basket(int quantity, String passengerMix, String channel) throws Throwable {
        String bundleType = "Standard";
        String journey = "SINGLE";
        String currency = "GBP";
        this.channel = channel;
        basketHoldItemsHelper.addValidFlightsToTheBasket(channel, bundleType, journey, passengerMix, quantity);
    }


    @And("^I have received a valid addHoldBagProduct request for the channel \"([^\"]*)\"$")
    public void i_have_received_a_valid_addholdbagproduct_request_for_the_channel_something_with_quantity_as_something(String channel) throws Throwable {
        if (channel == null) {
            channel = testData.getChannel();
        }
        basketHoldItemsHelper.buildRequestToAddHoldBags(channel);
    }

    @But("^request miss the mandatory field \"([^\"]*)\" for Holdbag defined in the service contract$")
    public void theTheMandatoryFieldForHoldbagDefinedInTheServiceContract(String field) throws Throwable {
        basketHoldItemsHelper.removeHoldBagServiceFieldFromRequestBody(field);
    }

    @And("^I have not received addHoldBagProduct request for the channel \"([^\"]*)\"$")
    public void i_have_not_received_addholdbagproduct_request_for_the_channel_something(String channel) throws Throwable {
        basketHoldItemsHelper.buildRequestToAddExcessBags(channel);
    }

    @And("^the request is only for excess weight hold item product$")
    public void the_request_is_only_for_excess_weight_hold_item_product() throws Throwable {
        //NO Implementation needed
    }

    @When("^I validate the request addHoldBagProduct$")
    public void i_validate_the_request_addholdbagproduct() throws Throwable {
        basketHoldItemsHelper.invokeServiceAddHoldBags(channel);
    }

    @Then("^I will return a error \"([^\"]*)\" to the channel for addHoldBagProduct$")
    public void iWillReturnAMessageToTheChannelForAddHoldBagProduct(String error) throws Throwable {

        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThatErrors().containedTheCorrectErrorMessage(StringUtils.split(error, "\\s*,\\s*"));
    }

    @When("^the request addHoldBagProduct \"([^\"]*)\" exceeds the threshold \"([^\"]*)\" set for the requesting channel and passenger$")
    public void the_request_addholdbagproduct_something_exceeds_the_threshold_something_set_for_the_requesting_channel_and_passenger(String qty, String maxthreshold) throws Throwable {
        if (Integer.valueOf(qty) > (Integer.valueOf(maxthreshold))) {
            basketHoldItemsHelper.invokeServiceAddHoldBags(channel);
            basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), channel);
        }
    }

    @And("^the hold item will not be added to the basket$")
    public void the_hold_item_will_not_be_added_to_the_basket() throws Throwable {
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().theBasketHasHoldBagItem();
    }

    @And("^the request contains a valid threshold set for \"([^\"]*)\" and the passenger mix \"([^\"]*)\"$")
    public void theRequestContainsAValidThresholdSetForTheItemAndThePassengerMix(String group, String mix) throws Throwable {
        int threshold = basketHoldItemsHelper.getThresholdForPassengerMix(channel, mix, group);
        basketHoldItemsHelper.updateHoldBagQuantityAllowPerPassenger(threshold);
    }

    @When("^I validate the addHoldBagProduct request$")
    public void iValidTheRequestToAddHoldBag() throws Throwable {
        if (channel == null) {
            channel = testData.getChannel();
        }
        basketHoldItemsHelper.invokeServiceAddHoldBags(channel);
    }

    @And("^I have received a valid request for \"([^\"]*)\" for the channel \"([^\"]*)\"$")
    public void iHaveReceivedAValidRequestForItemForTheChannel(String item, String channel) throws Throwable {
        basketHoldItemsHelper.buildRequestToAddHoldBags(item, channel);
    }

    @Then("^I will return an error \"([^\"]*)\" to the channel$")
    public void iWillReturnAMessageToTheChannel(String error) throws Throwable {
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @Then("^I will return an warning \"([^\"]*)\" to the channel$")
    public void iWillReturnAWarningMessageToTheChannel(String error) throws Throwable {
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().additionalInformationContains(error);
    }

    @Then("^I will add the hold bag product and create an order line for all the passengers$")
    public void i_will_add_the_hold_bag_product_and_create_an_order_line_for_all_the_passengers() throws Throwable {
        basketHoldItemsHelper.invokeServiceAddHoldBags(channel);
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().holdItemAddedForEachPassenger(basketHelper.getBasketService().getResponse());
    }

    @Then("^I will add the hold bag product by default and create an order line for all the passengers$")
    public void i_will_add_the_hold_bag_product_by_default_and_create_an_order_line_for_all_the_passengers() throws Throwable {
        basketHelper.getBasketService().assertThat().holdBagAddedAtPassengerLevel(basketHelper.getBasketService().getResponse());
    }

    @And("^add the price of the hold item and update the basket total$")
    public void add_the_price_of_the_hold_item_and_update_the_basket_total() throws Throwable {
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().holdBagFeesAppliedAtPassengerLevel(basketHelper.getBasketService().getResponse());
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().verifyBasketPriceIsUpdateForAllPassengers(basketHelper.getBasketService().getResponse(), basketHoldItemsHelper.getBasketPrice());
    }

    @And("^add the price of the hold items and update the basket total$")
    public void add_the_price_of_the_hold_items_and_update_the_basket_total() throws Throwable {
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().holdBagFeesAppliedAtPassengerLevel(basketHelper.getBasketService().getResponse());
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().verifyBasketPriceIsUpdateForAllPassengers(basketHelper.getBasketService().getResponse(), basketHoldItemsHelper.getBasketPrice());
    }

    @Then("^I will return the price including credit card fee for the channel \"([^\"]*)\"$")
    public void i_will_return_the_price_including_credit_card_fee_something_depending_on_the_channel_something(String channel) throws Throwable {
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().holdBagFeesAppliedAtPassengerLevel(basketHelper.getBasketService().getResponse());
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().verifyBasketPriceIsUpdate(basketHelper.getBasketService().getResponse(), basketHoldItemsHelper.getBasketPrice(), basketHoldItemsHelper.getPassengerCode(), basketHoldItemsHelper.getProductCode());
    }

    @Then("^I will verify the stock level is the same for holdbag$")
    public void iWillVerifyTheStockLevelIsTheSame() throws Throwable {
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().verifyStockLevelIsTheSame(basketHoldItemsHelper.getActualReservedItem(), basketHoldItemsHelper.getReservedAllocation());
    }

    @And("^I will verify the hold bag stock level depends on the channel$")
    public void iWillVerifyTheStockLevelDependsOnChannelChannel() throws Throwable {
        if (channel.equalsIgnoreCase("ADAirport") || channel.equalsIgnoreCase("ADCustomerService")) {
            basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().verifyStockLevelDecrease(basketHoldItemsHelper.getActualReservedItem(), basketHoldItemsHelper.getReservedAllocation(), basketHoldItemsHelper.getHoldBagQuantity());
        } else if (channel.equalsIgnoreCase("Digital") || channel.equalsIgnoreCase("PublicApiMobile") || channel.equalsIgnoreCase("PublicApiB2B")) {
            basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().verifyStockLevelIsTheSame(basketHoldItemsHelper.getActualReservedItem(), basketHoldItemsHelper.getReservedAllocation());
        }
    }

    @And("^I have received a valid request to add hold bag to all passenger$")
    public void iHaveReceivedAValidRequestToAddHoldBagToAllPassenger() throws Throwable {
        //basketHoldItemsHelper.buildRequestToAddProductToAllPassenger(basketHoldItemsHelper.getOuBoundFlightKeyBasedOnIndex(basketHelper.get))
        basketHoldItemsHelper.addProductToAllPassengersForSpecificFlight("", basketHoldItemsHelper.getOuBoundFlightKeyBasedOnIndex(0), 1);
    }


    @When("^I have received a valid request to add hold bag with excess weight to all passenger$")
    public void iHaveReceivedAValidRequestToAddHoldBagWithExcessWeightToAllPassenger() throws Throwable {
        basketHoldItemsHelper.addProductToAllPassengersForSpecificFlight("", basketHoldItemsHelper.getOuBoundFlightKeyBasedOnIndex(0), 1);
    }

    @Given("^I have added \"([^\"]*)\" flights with passengers as \"([^\"]*)\" and from the channel <Channel> to the basket$")
    public void iHaveAddedFlightsWithPassengersAsAndFromTheChannelChannelToTheBasket(String arg0, String arg1) throws Throwable {

    }

    @Given("^I have added flights with passenger details to my basket$")
    public void iHaveAddedFlightsWithPassengerDetailsToMyBasket() throws Throwable {
        basketHoldItemsHelper.createCartWithMultipleFlighsAndTraveller(channel);
    }

    @Given("^I have added flights with passenger details to my basket for \"([^\"]*)\"$")
    public void iHaveAddedFlightsWithPassengerDetailsToMyBasketFor(String channel) throws Throwable {
        this.channel = channel;
        basketHoldItemsHelper.createCartWithMultipleFlighsAndTraveller(channel);
    }

    @Then("^I should add the hold bag product to all the passengers for specific flight$")
    public void iWillAddTheHoldBagProductToAllThePassengersForSpecificFlight() throws Throwable {
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().holdBagAddedAtPassengerLevelToSpecificFlight(basketHelper.getBasketService().getResponse(), basketHoldItemsHelper.getRequestBody(), basketHoldItemsHelper.getHoldItem(""));
    }

    @Then("^I should add the hold bag product to specific passengers for specific flight$")
    public void iWillAddTheHoldBagProductToSpecificThePassengersForSpecificFlight() throws Throwable {
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().holdBagAddedSpecificPassengerToSpecificFlight(basketHelper.getBasketService().getResponse(), basketHoldItemsHelper.getRequestBody(), basketHoldItemsHelper.getHoldItem(""));
    }

    @And("^the quantity of hold bag items added should be same as requested quantity$")
    public void theCountOfHoldBagItemsAddedShouldBeSameAsRequestedQuantity() throws Throwable {
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().quantityOfHoldBagAddedAsExpected(basketHelper.getBasketService().getResponse(), 1);

    }

    @And("^I will add excess weight to each passenger$")
    public void iWillAddExcessWeightToEachPassenger() throws Throwable {
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().excessWeightAddedAtPassengerLevel(basketHelper.getBasketService().getResponse(), basketHoldItemsHelper.getFlightKeyForHoldBags(), basketHoldItemsHelper.getHoldItemProductCode(channel, "ExcessWeightProduct"), basketHoldItemsHelper.getExcessWeightQuantity());

    }

    @When("^I have received a valid request to add product as \"([^\"]*)\" to all passenger with quantity (.*)$")
    public void iHaveReceivedAValidRequestToAddProductAsToAllPassengerWith(String productType, int quantity) throws Throwable {
        basketHoldItemsHelper.addProductToAllPassengersForSpecificFlight(productType, basketHoldItemsHelper.getOuBoundFlightKeyBasedOnIndex(0), quantity);
        getBasket();

    }

    @When("^I have received a valid request to add product as \"([^\"]*)\" to specific passenger on specific flight$")
    public void iHaveReceivedAValidRequestToAddProductAsToSpecificPassengerWithSpecificFlight(String productType) throws Throwable {
        if (passengerType == null)
            passengerType = testData.getPassengerMix().split("\\s+")[1];
        basketHoldItemsHelper.addProductToSpecificPassengersForSpecificFlight(productType,
                basketHoldItemsHelper.getOuBoundFlightKeyBasedOnIndex(0), basketHoldItemsHelper.getPassengerWithType(0, passengerType));
        getBasket();

    }

    @When("^I have received a valid request to add product as \"([^\"]*)\" with excess weight quantity as \"([^\"]*)\" to all passenger$")
    public void iHaveReceivedAValidRequestToAddProductAsWithExcessWeightQuantityAsToAllPassenger(String productType, int quantity) throws Throwable {
        this.channel = testData.getChannel();
        basketHoldItemsHelper.addProductWithExcessWeightAllPassengersForSpecificFlight(productType, basketHoldItemsHelper.getOuBoundFlightKeyBasedOnIndex(0), channel, quantity);
        getBasket();
    }

    private void getBasket() {
        Basket basket = basketHelper.getBasketService().getResponse().getBasket();
        basketHelper.getBasket(basket.getCode(), testData.getChannel());
    }

    @When("^I have received a valid request to add product as \"([^\"]*)\" to all flights for specific passenger$")
    public void iHaveReceivedAValidRequestToAddProductAsToAllFlightsForSpecificPassenger(String productType) throws Throwable {
        basketHoldItemsHelper.addHoldItemToAllFlightsForSpecificPassenger(productType, basketHoldItemsHelper.getPassengerWithType(0, passengerType), channel);
        getBasket();
    }

    @When("^I request to add product as \"([^\"]*)\" with excess weight quantity as \"(.*)\" to all flights for specific passenger$")
    public void iRequestToAddProductAsWithExcessWeightQuantityAsToAllFlightsForSpecificPassenger(String productType, int excessWeightQuantity) throws Throwable {
        channel = testData.getChannel();
        basketHoldItemsHelper.addHoldItemWithExcessWeightToAllFlightsForSpecificPassenger(basketHoldItemsHelper.getPassengerWithType(0, passengerType), channel, productType, excessWeightQuantity);
        getBasket();
    }

    @And("^I will add excess weight to all flights for specific passenger$")
    public void iWillAddExcessWeightToAllFlightsForSpecificPassenger() throws Throwable {
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().excessWeightAddedToSpecificPassengerForAllFlights(basketHelper.getBasketService().getResponse(), basketHoldItemsHelper.getPassengerCode(), "3kgextraweight", basketHoldItemsHelper.getExcessWeightQuantity());

    }

    @When("^I have received a valid request to add product as \"([^\"]*)\" to all passenger on all flights$")
    public void iHaveReceivedAValidRequestToAddProductAsToAllPassengerOnAllFlights(String productType) throws Throwable {
        this.channel = testData.getChannel();
        basketHoldItemsHelper.addHoldItemToAllFlightsAllPassenger(channel, productType);
        getBasket();
    }

    @When("^I have received a valid request to add product as \"([^\"]*)\" with \"([^\"]*)\" excess weight to all passenger on all flights$")
    public void iHaveReceivedAValidRequestToAddProductAsWithExcessWeightToAllPassengerOnAllFlights(String productType, int excessWeightQuantity) throws Throwable {
        basketHoldItemsHelper.addHoldItemWithExcessWeightToAllFlightsAllPassenger(channel, productType, excessWeightQuantity);
        getBasket();
    }


    @Given("^I have the threshold set for \"([^\"]*)\" and the passenger mix \"([^\"]*)\" for \"([^\"]*)\"$")
    public void iHaveTheThresholdSetForAndThePassengerMixFor(String productType, String passengerMix, String channel) throws Throwable {
        this.channel = testData.getChannel();
        threshold = basketHoldItemsHelper.getThresholdForPassengerMix(this.channel, passengerMix, productType);
    }

    @And("^I have added the flight with passenger \"([^\"]*)\" with count outside the threshold$")
    public void iHaveAddedTheFlightWithPassengerWithCountOutsideTheThreshold(String passengerMix) throws Throwable {
        String bundleType = "Standard";
        String journey = "SINGLE";
        String currency = "GBP";
        passengerType = passengerMix.split("\\s+")[1];
        passengerMix = threshold + 1 + " " + passengerMix.split("\\s+")[1];
        basketHoldItemsHelper.addValidFlightToTheBasket(channel, bundleType, journey, passengerMix, currency);
    }

    @When("^I received request to add \"([^\"]*)\" to specific flight specific passenger$")
    public void iReceivedRequestToAddToSpecificFlightSpecificPassenger(String productType) throws Throwable {
        basketHoldItemsHelper.addHoldItemToSpecificPassengerSpecificFlight(productType, basketHoldItemsHelper.getOuBoundFlightKeyBasedOnIndex(0), basketHoldItemsHelper.getPassengerWithType(0, passengerType), channel);
    }

    @And("^I  add \"([^\"]*)\" until I reach the threshold$")
    public void iAddUntilIReachTheThreshold(String productType) throws Throwable {
        basketHoldItemsHelper.addHoldItemWithError(productType, basketHoldItemsHelper.getOuBoundFlightKeyBasedOnIndex(0), basketHoldItemsHelper.getPassengerWithType(0, passengerType), channel, threshold + 1);
    }

    @When("^I  add \"([^\"]*)\" for all passengers on flight until I reach the threshold$")
    public void iAddForAllPassengersOnFlightUntilIReachTheThreshold(String productType) throws Throwable {
        for (int index = 0; index <= threshold; index++) {
            basketHoldItemsHelper.addHoldItemWithError(channel, productType);
        }
    }

    @When("^I add \"([^\"]*)\" \"([^\"]*)\" for all passengers on flight$")
    public void iAddMaximumHoldItemsForAllPassengers(String quantity, String productType) throws Throwable {

        readThresholdsForHoldItems(productType);
        if (quantity.equalsIgnoreCase("maximum")) {
            addNumberOfHoldItems(threshold, productType);
        } else {
            addNumberOfHoldItems(Integer.parseInt(quantity), productType);
        }
        storeAddedHoldItems(productType);
    }

    private void storeAddedHoldItems(String productTypeStr) {
        String productCodeBasedOnType = basketHoldItemsHelper.getProductCodeBasedOnType(productTypeStr, testData.getChannel());
        List<String> holdItemsList = testData.getData(SerenityFacade.DataKeys.FLIGHT_HOLD_ITEMS);
        if (holdItemsList == null) {
            holdItemsList = new ArrayList<>();
        }
        holdItemsList.add(productCodeBasedOnType);
        testData.setData(SerenityFacade.DataKeys.FLIGHT_HOLD_ITEMS, holdItemsList);
    }

    private void addNumberOfHoldItems(int quantity, String productType) throws Throwable {
        for (int index = 0; index < quantity; index++) {
            basketHoldItemsHelper.addHoldItemWithError(testData.getChannel(), productType);
        }
    }

    private void readThresholdsForHoldItems(String productType) throws EasyjetCompromisedException {
        threshold = basketHoldItemsHelper.getThresholdForPassengerMix(testData.getChannel(), testData.getPassengerMix(), productType);
        if (productType.toLowerCase().contains("bag")) {
            this.thresholdForHoldBags = threshold;
        } else if (productType.toLowerCase().contains("sport")) {
            this.thresholdForSportsEqiupment = threshold;
        }
    }

    @When("^I  add \"([^\"]*)\" for all passengers on specific flight until I reach the threshold$")
    public void iAddForAllPassengersOnSpecificFlightUntilIReachTheThreshold(String productType) throws Throwable {
        basketHoldItemsHelper.addHoldItemWithErrorForAllPassengerSpecificFlight(channel, productType, basketHoldItemsHelper.getOuBoundFlightKeyBasedOnIndex(0), false, threshold);
    }

    @When("^I  add \"([^\"]*)\" for all passengers on specific flight until I reach the threshold with override as \"([^\"]*)\"$")
    public void iAddForAllPassengersOnSpecificFlightUntilIReachTheThresholdWithOverrideAs(String productType, boolean isOverride) throws Throwable {
        basketHoldItemsHelper.addHoldItemWithErrorForAllPassengerSpecificFlight(channel, productType, basketHoldItemsHelper.getOuBoundFlightKeyBasedOnIndex(0), isOverride, threshold + 1);
    }

    @Then("^I should add the \"([^\"]*)\" product to all the passengers for requested flight$")
    public void iShouldAddTheProductToAllThePassengersForRequestedFlight(String productType) throws Throwable {
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().holdBagAddedAtPassengerLevelToSpecificFlight(basketHelper.getBasketService().getResponse(), basketHoldItemsHelper.getRequestBody(), basketHoldItemsHelper.getHoldItem(""));
        // basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().holdBagAddedAtPassengerLevelToSpecificFlight(basketHelper.getBasketService().getResponse(), basketHoldItemsHelper.getFlightKeyForHoldBags(), basketHoldItemsHelper.getHoldItemProductCode());
    }

    @Then("^I will add the \"([^\"]*)\" product to specific passenger on all flights$")
    public void iWillAddTheProductToSpecificPassengerOnAllFlights(String arg0) throws Throwable {
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().holdItemAddedForEachFlightForSpecificPassenger(basketHelper.getBasketService().getResponse(), basketHoldItemsHelper.getPassengerCode(), basketHoldItemsHelper.getHoldItemProductCode());

    }

    @Then("^I should add the \"([^\"]*)\" product to all the passengers for all flights$")
    public void iShouldAddTheProductToAllThePassengersForAllFlights(String arg0) throws Throwable {
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().holdItemAddedForEachPassenger(basketHelper.getBasketService().getResponse());

    }

    @Then("^I will decrement the stock level for the flight for the number of requested hold item$")
    public void iWillDecrementTheStockLevelForTheFlightForTheNumberOfRequestedHoldItems() throws Throwable {
        basketHoldItemsHelper.getAddSportToBasketService().assertThat().verifyStockLevelDecrease(basketHoldItemsHelper.getActualReservedItem(), basketHoldItemsHelper.getReservedAllocation(), Integer.valueOf(numberOfPassenger));
    }

    @And("^I have added \"([^\"]*)\" flights to basket with passengers as \"([^\"]*)\" and '<fareType>' fare as '<journeyType>' journey$")
    public void iHaveAddedFlightsToBasketWithPassengersAsAndFareTypeFareAsJourneyTypeJourney(String passengerMix, String fareType, String journeyType) throws Throwable {

    }

    @And("^I added (.*) flights to basket with passenger mix \"([^\"]*)\",'(.+)' and '(.+)'$")
    public void i_have_added_something_flights_to_basket_with_passenger_mix_something_and_(int quantity, String passengerMix, String fareType, String journeyType) throws Throwable {
        this.channel = testData.getChannel();
        passengerType = passengerMix.split("\\s+")[1];
        basketHoldItemsHelper.addValidFlightsToTheBasket(channel, fareType, journeyType, passengerMix, quantity);
    }


    @When("^I received request to add \"([^\"]*)\" to \"([^\"]*)\" hold bag of specific passenger on specific flight$")
    public void iReceivedRequestToAddToHoldBagOfSpecificPassengerOnSpecificFlight(String productType, String holdItemIndex) throws Throwable {
        int itemIndex = Integer.valueOf(holdItemIndex.split("'")[0]);
        //   this.holdItemIndex = basketHoldItemsHelper.getIndex(itemIndex);
        basketHoldItemsHelper.addExcessWeightToSpecificPassengersForSpecificFlight(productType, basketHoldItemsHelper.getOuBoundFlightKeyBasedOnIndex(0), basketHoldItemsHelper.getPassengerWithType(0, passengerType), 1, itemIndex, false);
        getBasket();
    }


    @Then("^I should add the \"([^\"]*)\" product to all the passengers for specific flight$")
    public void iShouldAddTheProductToAllThePassengersForSpecificFlight(String productType) throws Throwable {
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().holdBagAddedAtPassengerLevelToSpecificFlight(basketHelper.getBasketService().getResponse(), basketHoldItemsHelper.getRequestBody(), basketHoldItemsHelper.getHoldItem(""));

        // basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().holdBagAddedAtPassengerLevelToSpecificFlight(basketHelper.getBasketService().getResponse(), basketHoldItemsHelper.getFlightKeyForHoldBags(), basketHoldItemsHelper.getHoldItemProductCode());
    }

    @And("^I have received the request to add \"([^\"]*)\" products as \"([^\"]*)\" to all passenger on all flights$")
    public void iHaveReceivedTheRequestToAddProductsAsToAllPassengerOnAllFlights(int quantity, String productType) throws Throwable {
        this.channel = testData.getChannel();
        for (int i = 0; i < quantity; i++) {
            basketHoldItemsHelper.addHoldItemToAllFlightsAllPassenger(channel, productType);
        }
        getBasket();
    }

    @Then("^I should add the Excess Weight product to requested (.*) Hold Bag$")
    public void iShouldAddTheExcessWeightProductToRequestedHoldBag(String index) throws Throwable {
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().
                excessWeightAddedSuccessfullyToSpecificHoldBag(basketHelper.getBasketService().getResponse(),
                        basketHoldItemsHelper.getRequestBody(), basketHoldItemsHelper.getOrderEntryNumber());
    }

    @When("^I received request to add \"([^\"]*)\" to with \"([^\"]*)\"$")
    public void iReceivedRequestToAddToWith(String productType, String condition) throws Throwable {
        int itemIndex = Integer.valueOf(holdItemIndex.split("'")[0]);
        this.holdItemIndex = basketHoldItemsHelper.getIndex(itemIndex);
        basketHoldItemsHelper.addExcessWeightToSpecificPassengersForSpecificFlight(productType, basketHoldItemsHelper.getOuBoundFlightKeyBasedOnIndex(0), basketHoldItemsHelper.getPassengerWithType(0, passengerType), 1, itemIndex, false);
        getBasket();
    }

    @When("^I received request to add \"([^\"]*)\" without hold bag$")
    public void iReceivedRequestToAdd(String productType) throws Throwable {
        this.holdItemIndex = "1234";
        basketHoldItemsHelper.addExcessWeightToSpecificPassengersForSpecificFlightWithError(productType, basketHoldItemsHelper.getOuBoundFlightKeyBasedOnIndex(0), basketHoldItemsHelper.getPassengerWithType(0, passengerType), 1, false);
    }

    @When("^I received request to add \"([^\"]*)\" to invalid hold item")
    public void iReceivedRequestToAddInvalidHoldItem(String productType) throws Throwable {
        this.holdItemIndex = "1234";
        basketHoldItemsHelper.addExcessWeightToSpecificPassengersForSpecificFlightWithError(productType, basketHoldItemsHelper.getOuBoundFlightKeyBasedOnIndex(0), basketHoldItemsHelper.getPassengerWithType(0, passengerType), 1, false);
        getBasket();
    }

    @And("^I have added \"([^\"]*)\" products as \"([^\"]*)\" to all passenger on all flights$")
    public void iHaveReceivedTheRequestToAddProductAsToAllPassengerOnAllFlights(int quantity, String productType) throws Throwable {
        this.channel = testData.getChannel();
        for (int i = 0; i < quantity; i++) {
            basketHoldItemsHelper.addHoldItemToAllFlightsAllPassenger(channel, productType);
        }
        getBasket();
    }

    @When("^I received request to add \"([^\"]*)\" \"([^\"]*)\" to \"([^\"]*)\" hold bag of specific passenger on specific flight$")
    public void iReceivedRequestToAddToHoldBagOfSpecificPassengerOnSpecificFlight(int excessWeightQuantity, String productType, String holdItemIndex) throws Throwable {
        itemIndex = Integer.valueOf(holdItemIndex.split("'")[0]);
        basketHoldItemsHelper.addExcessWeightToSpecificPassengersForSpecificFlight(productType, basketHoldItemsHelper.getOuBoundFlightKeyBasedOnIndex(0), basketHoldItemsHelper.getPassengerWithType(0, passengerType), excessWeightQuantity, itemIndex, false);
        getBasket();
    }

    @And("^I should add the credit card fees based on channel \"([^\"]*)\"$")
    public void iShouldAddTheCreditCardFeesBasedOn(String channel) throws Throwable {
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().creditCardFeesAreApplied(basketHelper.getBasketService().getResponse(), channel);

    }

    @When("^I received request to add \"([^\"]*)\" to \"([^\"]*)\" hold bag with different price$")
    public void iReceivedRequestToAddToHoldBagWithDifferentPrice(String productType, String holdItemIndex) throws Throwable {
        itemIndex = Integer.valueOf(holdItemIndex.split("'")[0]);
        basketHoldItemsHelper.addExcessWeightToSpecificPassengersForSpecificFlight(productType, basketHoldItemsHelper.getOuBoundFlightKeyBasedOnIndex(0), basketHoldItemsHelper.getPassengerWithType(0, passengerType), 1, itemIndex, true);
        getBasket();
    }

    @When("^I receive request to add \"([^\"]*)\" more than (\\d+) kg to \"([^\"]*)\" hold bag of specific passenger on specific flight$")
    public void iReceiveRequestToAddMoreThanKgToHoldBagOfSpecificPassengerOnSpecificFlight(String productType, int weight, String holdItemIndex) throws Throwable {
        itemIndex = Integer.valueOf(holdItemIndex.split("'")[0]);
        basketHoldItemsHelper.addExcessWeightToSpecificPassengersForSpecificFlightWithError(productType, basketHoldItemsHelper.getOuBoundFlightKeyBasedOnIndex(0), basketHoldItemsHelper.getPassengerWithType(0, passengerType), 5, true);
        getBasket();
    }

    @When("^I add product (.*) with (.*) excess weight to all passengers$")
    public void iHaveToAddProductAsWithExcessWeightToAllPassengerOnAllFlights(String productType, int excessWeightQuantity) throws Throwable {
        basketHoldItemsHelper.addHoldItemWithExcessWeightToAllFlightsAllPassenger(channel, productType, excessWeightQuantity);
        getBasket();
    }

    @When("^I add product (.*) to all passenger on all flights$")
    public void iAddProductAsToAllPassengerOnAllFlights(String productType) throws Throwable {
        this.channel = testData.getChannel();
        basketHoldItemsHelper.addHoldItemToAllFlightsAllPassenger(channel, productType);
        getBasket();
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().holdItemAddedForEachPassenger(basketHelper.getBasketService().getResponse());
    }

    @Then("^I see increment in stock level for the flight for the number of requested hold items, sports equipment$")
    public void iSeeIncrementInStockLevelForTheFlightForTheNumberOfRequestedHoldItemsSportsEquipment() throws Throwable {
        List<String> holdItems = testData.getData(SerenityFacade.DataKeys.FLIGHT_HOLD_ITEMS);
        Map<String, Integer> beforeCancellation = testData.getData(SerenityFacade.DataKeys.FLIGHT_INVENTORY);
        Map<String, Integer> afterCancellation = basketHoldItemsHelper.getActualReservedItem(holdItems);
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat().allHoldItemsIncrementedAfterCancelBooking(beforeCancellation, afterCancellation);
    }
}
