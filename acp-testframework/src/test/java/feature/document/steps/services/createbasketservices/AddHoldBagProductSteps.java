package feature.document.steps.services.createbasketservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.SerenityReporter;
import com.hybris.easyjet.database.hybris.dao.HoldItemsDao;
import com.hybris.easyjet.fixture.hybris.asserters.AddHoldBagToBasketAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.AddHoldItemsRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AddHoldItemsToBasketRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractFlights;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.AddHoldBagToBasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import feature.document.steps.constants.StepsRegex;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.HOLD_BAG;

/**
 * AddHoldBagProductSteps handle the communication with the addHoldBagProduct service.
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 */
@ContextConfiguration(classes = TestApplication.class)
public class AddHoldBagProductSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private HoldItemsDao holdItemsDao;
    @Steps
    private AddHoldBagToBasketAssertion addHoldBagToBasketAssertion;
    @Steps
    private SerenityReporter reporter;

    private AddHoldBagToBasketService addHoldBagService;
    private BasketPathParams.BasketPathParamsBuilder basketPathParams;
    private AddHoldItemsRequestBody.AddHoldItemsRequestBodyBuilder addHoldItemsRequestBody;

    private String passengerId;
    private Double price;
    private Integer excessWeightQuantity = 0;
    private Double excessWeightPrice;
    private String excessWeightProductCode;
    private Integer stockBeforeAddToBasket = 0;
    private Integer stockAfterAddToBasket = 0;
    private Integer stockAfterClearingBasket = 0;
    private static final String DEFAULT_HOLD_BAG_PRODUCT = "20kgbag";
    private static final String DEFAULT_EXCESS_WEIGHT_PRODUCT = "3kgextraweight";

    private void setRequestPathParameter() {
        basketPathParams = BasketPathParams.builder()
                .basketId(testData.getData(BASKET_ID))
                .path(HOLD_BAG);
    }

    private void setRequestBody() {
        addHoldItemsRequestBody = AddHoldItemsRequestBody.builder()
                .productCode(DEFAULT_HOLD_BAG_PRODUCT)
                .price(price)
                .flightKey(testData.getData(FLIGHT_KEY));

        if (!Objects.isNull(passengerId)) {
            addHoldItemsRequestBody.passengerCode(passengerId);
        }

        if (excessWeightQuantity > 0) {
            addHoldItemsRequestBody
                    .excessWeightProductCode(excessWeightProductCode)
                    .excessWeightQuantity(excessWeightQuantity)
                    .excessWeightPrice(excessWeightPrice);
        }
    }

    private void invokeAddHoldBagService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        addHoldBagService = serviceFactory.addHoldBagToBasket(new AddHoldItemsToBasketRequest(headers.build(), basketPathParams.build(), addHoldItemsRequestBody.build()));
        testData.setData(SERVICE, addHoldBagService);
        testData.setData(ADD_HOLD_BAG_SERVICE, addHoldBagService);
        addHoldBagService.invoke();
    }

    private void sendAddHoldBagRequest() {
        setRequestPathParameter();
        setRequestBody();
        invokeAddHoldBagService();
    }

    @Step("Add {0} hold bag for {3} passenger")
    @When("^I add" + StepsRegex.HOLD_ITEM + "$")
    public void addHoldBag(Integer holdBagQuantity, String excessWeightType, Integer excessWeightQuantity, String passengerWithHolddBag) {
        stockBeforeAddToBasket = Integer.parseInt(holdItemsDao.getReservedStockLevelForFlight(testData.getData(FLIGHT_KEY), DEFAULT_HOLD_BAG_PRODUCT).get(0));
        int holdBags = Objects.isNull(holdBagQuantity) ? 1 : holdBagQuantity;
        price = 0.0;
        if (!Objects.isNull(excessWeightQuantity)) {
            this.excessWeightQuantity = excessWeightQuantity;
            if (StringUtils.isNotBlank(excessWeightType)) {
                excessWeightProductCode = excessWeightType;
                excessWeightPrice = 0.0;
            } else {
                excessWeightProductCode = DEFAULT_EXCESS_WEIGHT_PRODUCT;
                excessWeightPrice = 0.0;
            }
        }
        if (StringUtils.isBlank(passengerWithHolddBag) || passengerWithHolddBag.equals("each")) {
            passengerId = null;
            for (int i = 0; i < holdBags; i++) {
                sendAddHoldBagRequest();
            }
        } else {
            int passengers = Integer.parseInt(passengerWithHolddBag);
            BasketService basketService = testData.getData(BASKET_SERVICE);
            List<String> passengerList = basketService.getResponse().getBasket().getOutbounds().stream()
                    .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                    .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                    .filter(passenger -> !passenger.getPassengerDetails().getPassengerType().equals("infant"))
                    .map(AbstractPassenger::getCode)
                    .collect(Collectors.toList());

            for (int i = 0; i < passengers; i++) {
                passengerId = passengerList.get(i);
                for (int j = 0; j < holdBags; j++) {
                    sendAddHoldBagRequest();
                }
            }
        }
        stockAfterAddToBasket = Integer.parseInt(holdItemsDao.getReservedStockLevelForFlight(testData.getData(FLIGHT_KEY), DEFAULT_HOLD_BAG_PRODUCT).get(0));

    }

    @And("^hold bags are deallocated$")
    public void iVerifyTheHoldBagsAreDeallocated() {
        stockAfterClearingBasket = Integer.parseInt(holdItemsDao.getReservedStockLevelForFlight(testData.getData(FLIGHT_KEY), DEFAULT_HOLD_BAG_PRODUCT).get(0));
        getHoldBagStockProperties();
        addHoldBagService = testData.getData(ADD_HOLD_BAG_SERVICE);
        addHoldBagToBasketAssertion.setResponse(addHoldBagService.getResponse());
        addHoldBagToBasketAssertion.verifyStockLevelIsTheSame(stockBeforeAddToBasket, stockAfterClearingBasket);
    }

    private void getHoldBagStockProperties() {
            reporter.info("Holdbag stock consumed before clearing basket :" + stockAfterAddToBasket + "; Holdbag stock consumed after clearing basket: " + stockAfterClearingBasket);
    }
}