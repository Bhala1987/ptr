package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.models.ProductModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHoldItemsHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.HEADERS;
import static com.hybris.easyjet.exceptions.EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INSUFFICIENT_DATA;


/**
 * Created by marco on 27/04/17.
 */
@ContextConfiguration(classes = TestApplication.class)

public class Restrict10kgHoldItemToTheBasketSteps {

   private String channel;
   @Autowired
   SerenityFacade testData;

   @Autowired
   private BasketHoldItemsHelper basketHoldItemsHelper;

   @Given("^I have an hold bag product with a restriction for a channel$")
   public void i_have_an_hold_bag_product_with_a_restriction_for_a_channel() throws Throwable {
      // the product 10kgbag should have a channel restriction. If not, throw an exception
      ProductModel productModel = basketHoldItemsHelper.getProductWithRestrictedChannel(BasketHoldItemsHelper.TEN_KG_BAG);
      if (productModel == null || CollectionUtils.isEmpty(productModel.getChannels())) {
         throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
      }
      channel = productModel.getChannels().get(0);
      testData.setChannel(channel);
      testData.setData(HEADERS, HybrisHeaders.getValid(channel));

   }

   @And("^I have added one flight with one passenger for that channel to the basket$")
   public void iHaveAddedOneFlightWithOnePassengerForThatChannelToTheBasket() throws Throwable {
      basketHoldItemsHelper.addValidFlightToTheBasket(channel, "Standard", "SINGLE", "1 Adult", "GBP");
   }

   @When("^I try to add that product with addHoldBagProduct$")
   public void iTryToAddThatProductWithAddHoldBagProduct() throws Throwable {
      basketHoldItemsHelper.buildRequestToAdd10kgHoldBags("HoldBag");
      basketHoldItemsHelper.invokeServiceAddHoldBags(channel);
   }

   @Then("^I will return a message \"([^\"]*)\" to the channel for addHoldBagProduct$")
   public void iWillReturnAMessageToTheChannelForAddHoldBagProduct(String errorCode) throws Throwable {
      basketHoldItemsHelper.getAddHoldBagToBasketService().assertThatErrors().containedTheCorrectErrorMessage(errorCode);
   }
}
