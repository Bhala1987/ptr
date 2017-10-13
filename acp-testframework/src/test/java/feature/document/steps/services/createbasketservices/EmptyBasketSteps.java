package feature.document.steps.services.createbasketservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.BasketQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.BasketRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.DeleteBasketService;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;


@ContextConfiguration(classes = TestApplication.class)

public class EmptyBasketSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private DeleteBasketService emptyBasketService;
    private BasketQueryParams.BasketQueryParamsBuilder emptyBasketQueryParam;

    private void setQueryParam() {
        emptyBasketQueryParam = BasketQueryParams.builder().actionType("empty").basketId(testData.getData(BASKET_ID));
    }

    private void invokeEmptyBasketService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        emptyBasketService = serviceFactory.deleteBasket(new BasketRequest(headers.build(), null, emptyBasketQueryParam.build()));
        testData.setData(SERVICE, emptyBasketService);
        testData.setData(BASKET_SERVICE, emptyBasketService);
        emptyBasketService.invoke();
    }

    @Step("Empty basket")
    @When("^I send the request to emptyBasket service$")
    public void sendEmptyBasketRequest() {
        setQueryParam();
        invokeEmptyBasketService();
    }
}
