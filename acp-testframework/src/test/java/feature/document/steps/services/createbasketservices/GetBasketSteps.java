package feature.document.steps.services.createbasketservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.models.DealModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.BasketRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.CommitBookingService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import net.thucydides.core.annotations.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;

/**
 * GetBasketSteps handle the communication with the getBasket service.
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 */
@ContextConfiguration(classes = TestApplication.class)
public class GetBasketSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private BasketService basketService;
    private BasketPathParams.BasketPathParamsBuilder basketPathParams;

    private void setPathParameter(String basketId) {
        basketPathParams = BasketPathParams.builder().basketId(basketId);
    }

    private void invokeGetBasketService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        basketService = serviceFactory.getBasket(new BasketRequest(headers.build(), basketPathParams.build()));
        testData.setData(SERVICE, basketService);
        testData.setData(BASKET_SERVICE, basketService);
        basketService.invoke();
    }

    @Step
    public void sendGetBasketRequest(String basketId) {
        setPathParameter(basketId);
        invokeGetBasketService();
    }

    @Then("^the basket booking type is (STANDARD_CUSTOMER|based on deal)$")
    public void checkBasketType(String basketType) {
        sendGetBasketRequest(testData.getData(BASKET_ID));
        if (testData.keyExist(DEALS)) {
            List<DealModel> deals = testData.getData(DEALS);
            basketType = deals.get(0).getBookingType();
        }
        basketService.assertThat()
                .basketTypeIsRight(basketType);
    }

    //TODO this method assume that only one flight with one type of passenger (i.e. adult) is present in the basket, ACP is sending a list of unsorted information so we cannot distinguish between affected data information
    @And("^the basket is updated with the new price$")
    public void theBasketIsUpdatedWithTheNewPrice() throws EasyjetCompromisedException {
        CommitBookingService commitBookingService = testData.getData(SERVICE);
        String newPrice = commitBookingService.getErrors().getErrors().stream()
                .filter(error -> error.getCode().equals("SVC_100022_3012"))
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("The error code SVC_100022_3012 was not present"))
                .getAffectedData().stream()
                .filter(affectedData -> affectedData.dataName.equals("price"))
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("The new price was not present"))
                .dataValue;

        sendGetBasketRequest(testData.getData(BASKET_ID));
        basketService.assertThat()
                .newPriceIsApplied(testData.getData(FLIGHT_KEY), "adult", newPrice);

    }

}