package feature.document.steps.services.createbasketservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.SerenityReporter;
import com.hybris.easyjet.database.hybris.dao.HoldItemsDao;
import com.hybris.easyjet.fixture.hybris.asserters.AddSportEquipmentToBasketAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.AddSportEquipmentRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AddHoldItemsToBasketRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractFlights;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.AddSportToBasketService;
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
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.SPORT_EQUIP;

/**
 * AddSportsEquipProductSteps handle the communication with the sports-equipment service.
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 */
@ContextConfiguration(classes = TestApplication.class)
public class AddSportsEquipProductSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private HoldItemsDao holdItemsDao;
    private AddSportToBasketService addSportToBasketService;
    @Steps
    private AddSportEquipmentToBasketAssertion addSportEquipmentToBasketAssertion;
    @Steps
    private SerenityReporter reporter;
    private BasketPathParams.BasketPathParamsBuilder basketPathParams;
    private AddSportEquipmentRequestBody.AddSportEquipmentRequestBodyBuilder addSportEquipmentRequestBody;
    private Integer stockBeforeAddToBasket = 0;
    private Integer stockAfterAddToBasket = 0;
    private Integer stockAfterClearingBasket = 0;
    private static final String DEFAULT_SPORT_ITEM_PRODUCT = "Snowboard";

    private String passengerId;

    private void setRequestPathParameter() {
        basketPathParams = BasketPathParams.builder()
                .basketId(testData.getData(BASKET_ID))
                .path(SPORT_EQUIP);
    }

    private void setRequestBody() {
        addSportEquipmentRequestBody = AddSportEquipmentRequestBody.builder()
                .productCode(DEFAULT_SPORT_ITEM_PRODUCT)
                .flightKey(testData.getData(FLIGHT_KEY))
                .override(true)
                .quantity(1);

        if (!Objects.isNull(passengerId)) {
            addSportEquipmentRequestBody.passengerCode(passengerId);
        }
    }

    private void invokeAddSportsEquipService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        addSportToBasketService = serviceFactory.getAddSportEquipmentToBasket(new AddHoldItemsToBasketRequest(headers.build(), basketPathParams.build(), addSportEquipmentRequestBody.build()));
        testData.setData(SERVICE, addSportToBasketService);
        testData.setData(ADD_SPORTS_ITEM_SERVICE, addSportToBasketService);
        addSportToBasketService.invoke();
    }

    private void sendAddSportsEquipRequest() {
        setRequestPathParameter();
        setRequestBody();
        invokeAddSportsEquipService();
    }

    @Step("Add {0} sports item for {3} passenger")
    @When("^I add" + StepsRegex.SPORTS_ITEM + "$")
    public void addSportItem(Integer SportsItemQuantity, String passengerWithSportsItem) {
        stockBeforeAddToBasket = Integer.parseInt(holdItemsDao.getReservedStockLevelForFlight(testData.getData(FLIGHT_KEY), DEFAULT_SPORT_ITEM_PRODUCT).get(0));
        int sportItems = Objects.isNull(SportsItemQuantity) ? 1 : SportsItemQuantity;
        if (StringUtils.isBlank(passengerWithSportsItem) || passengerWithSportsItem.equals("each")) {
            passengerId = null;
            for (int i = 0; i < sportItems; i++) {
                sendAddSportsEquipRequest();
            }
        } else {
            int passengers = Integer.parseInt(passengerWithSportsItem);
            BasketService basketService = testData.getData(BASKET_SERVICE);
            List<String> passengerList = basketService.getResponse().getBasket().getOutbounds().stream()
                    .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                    .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                    .filter(passenger -> !passenger.getPassengerDetails().getPassengerType().equals("infant"))
                    .map(AbstractPassenger::getCode)
                    .collect(Collectors.toList());

            for (int i = 0; i < passengers; i++) {
                passengerId = passengerList.get(i);
                for (int j = 0; j < sportItems; j++) {
                    sendAddSportsEquipRequest();
                }
            }
        }
        stockAfterAddToBasket = Integer.parseInt(holdItemsDao.getReservedStockLevelForFlight(testData.getData(FLIGHT_KEY), DEFAULT_SPORT_ITEM_PRODUCT).get(0));
    }

    @And("^sport items are deallocated$")
    public void iVerifyTheHoldBagsAreDeallocated() {
        stockAfterClearingBasket = Integer.parseInt(holdItemsDao.getReservedStockLevelForFlight(testData.getData(FLIGHT_KEY), DEFAULT_SPORT_ITEM_PRODUCT).get(0));
        getSportItemStockProperties();
        addSportToBasketService = testData.getData(ADD_SPORTS_ITEM_SERVICE);
        addSportEquipmentToBasketAssertion.setResponse(addSportToBasketService.getResponse());
        addSportEquipmentToBasketAssertion.verifyStockLevelIsTheSame(stockBeforeAddToBasket, stockAfterClearingBasket);
    }

    private void getSportItemStockProperties() {
        reporter.info("SportsItem stock consumed before clearing basket :" + stockAfterAddToBasket + "; SportsItem stock consumed after clearing basket: " + stockAfterClearingBasket);
    }
}
