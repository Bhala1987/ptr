package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.HoldItemsDao;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHoldItemsHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.AddHoldItemsRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AddHoldItemsToBasketRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.GetSeatMapResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.AddHoldBagToBasketService;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.HOLD_BAG;
import static feature.document.GlobalHooks.clearCookiesInClient;

/**
 * Created by giuseppedimartino on 31/03/17.
 */

@ContextConfiguration(classes = TestApplication.class)
public class AddHoldBagProductSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BasketHoldItemsHelper basketHoldItemsHelper;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    private AddHoldBagToBasketService addHoldBagToBasketService;
    @Autowired
    private HoldItemsDao holdItemsDao;

    @Given("^I added an hold bag to first passenger$")
    public void iAddedAnHoldBagToFirstPassenger() throws Throwable {

        Basket basket = basketHelper.getBasketService().getResponse().getBasket();
        BasketPathParams pathParams = BasketPathParams.builder().basketId(basket.getCode()).path(HOLD_BAG).build();
        AddHoldItemsRequestBody body =
                AddHoldItemsRequestBody.builder()
                        .productCode("20kgbag")
                        .quantity(1)
                        .passengerCode(basket.getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getCode())
                        .flightKey(basket.getOutbounds().get(0).getFlights().get(0).getFlightKey())
                        .excessWeightProductCode("")
                        .excessWeightQuantity(0)
                        .override(false)
                        .build();

        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
        addHoldBagToBasketService = serviceFactory.addHoldBagToBasket(new AddHoldItemsToBasketRequest(headers.build(), pathParams, body));
        addHoldBagToBasketService.invoke();

        basketHelper.getBasket(basket.getCode(), testData.getChannel());

        addHoldBagToBasketService.assertThat().holdBagAddedForEachPassenger(basketHelper.getBasketService().getResponse());
    }

    @Given("^I added an hold bag to all passengers$")
    public void iAddedAnHoldBagToAllPassengers() throws Throwable {
        //Parameter one is to send the hold Bag count and the second is to send the excess weight required count
        basketHelper.addHoldAndExcessWeightBagsForAllPassengers(1,0);
    }

    @Given("^I added an hold bag to all passengers with excess weight$")
    public void iAddedAnHoldBagToAllPassengersWithExcessWeight() throws Throwable {
        Basket basket = basketHelper.getBasketService().getResponse().getBasket();
        String flightKey = basket.getOutbounds().get(0).getFlights().get(0).getFlightKey();
        BasketPathParams pathParams = BasketPathParams.builder().basketId(basket.getCode()).path(HOLD_BAG).build();
        basket.getOutbounds().get(0).getFlights().get(0).getPassengers().forEach(
                passenger -> {
                    AddHoldItemsRequestBody body =
                            AddHoldItemsRequestBody.builder()
                                    .productCode("20kgbag")
                                    .quantity(1)
                                    .passengerCode(passenger.getCode())
                                    .flightKey(flightKey)
                                    .excessWeightProductCode("3kgextraweight")
                                    .excessWeightQuantity(1)
                                    .override(false)
                                    .build();

                    HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
                    addHoldBagToBasketService = serviceFactory.addHoldBagToBasket(new AddHoldItemsToBasketRequest(headers.build(), pathParams, body));
                    addHoldBagToBasketService.invoke();
                }
        );

        basket=basketHelper.getBasket(basket.getCode(), testData.getChannel());
        testData.setData(SerenityFacade.DataKeys.OUTBOUND_FLIGHT,basket.getOutbounds().get(0).getFlights().get(0));
        addHoldBagToBasketService.assertThat().holdBagAddedForEachPassenger(basketHelper.getBasketService().getResponse());
    }

    @Given("^I added an hold bag to all passengers with '(\\d+)' '(.+)' excess weight$")
    public void iAddedAnHoldBagToAllPassengersWithEWQuantityEWProductExcessWeight(int quantity, String product) throws Throwable {
        Basket basket = basketHelper.getBasketService().getResponse().getBasket();
        String flightKey = basket.getOutbounds().get(0).getFlights().get(0).getFlightKey();
        BasketPathParams pathParams = BasketPathParams.builder().basketId(basket.getCode()).path(HOLD_BAG).build();

//        basket.getOutbounds().get(0).getFlights().get(0).getPassengers().forEach(
//                passenger -> {
                    AddHoldItemsRequestBody body =
                            AddHoldItemsRequestBody.builder()
                                    .productCode("20kgbag")
                                    .quantity(1)
//                                    .passengerCode(passenger.getCode())
//                                    .flightKey(flightKey)
                                    .excessWeightProductCode(product)
                                    .excessWeightQuantity(quantity)
                                    .override(false)
                                    .build();

                    HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
                    addHoldBagToBasketService = serviceFactory.addHoldBagToBasket(new AddHoldItemsToBasketRequest(headers.build(), pathParams, body));
                    addHoldBagToBasketService.invoke();
//                }
//        );

        basket=basketHelper.getBasket(basket.getCode(), testData.getChannel());

        addHoldBagToBasketService.assertThat().holdBagAddedForEachPassenger(basketHelper.getBasketService().getResponse());
        testData.setData(SerenityFacade.DataKeys.OUTBOUND_FLIGHT,basket.getOutbounds().get(0).getFlights().get(0));
    }

    @And("^I added a hold bag$")
    public void iAddedAHoldBag() throws Throwable {
        holdItemsDao.updateTheStock(testData.getFlightKey(), "20kgbag", "0");
        basketHelper.addHoldAndExcessWeightBagsForAllPassengers(1,0);
    }

    @And("^all the hold bag inventory sold out for the same flight$")
    public void allTheHoldBagInvemtorySoldoutForTheSameFlight() throws Throwable {
        holdItemsDao.updateTheStock(testData.getFlightKey(), "20kgbag", "500599");
        String originalChannel = testData.getChannel();
        testData.setChannel("ADAirport");
        basketHelper.addCustomFlightToTheBasket(testData.getFlightKey(), testData.getFlightKey().substring(8, 14), "1 Adult", testData.getChannel(), "Standard");
        basketHelper.addHoldAndExcessWeightBagsForAllPassengers(1,0);
        testData.setChannel(originalChannel);
        clearCookiesInClient();
    }
}
