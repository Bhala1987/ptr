package feature.document.steps.services.createbasketservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.asserters.BasketsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.RemoveFlightFromBasketRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractFlights;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.RemoveFlightFromBasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collection;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.REMOVE_FLIGHT;

/**
 * RemoveFlightSteps handle the communication with the removeFlight service (aka removeFlightFromTheBasket).
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 *
 * @author rajakm
 */
@ContextConfiguration(classes = TestApplication.class)
public class RemoveFlightSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Steps
    private GetBasketSteps getBasketSteps;
    @Steps
    private BasketsAssertion basketAssertion;

    private RemoveFlightFromBasketService removeFlightFromBasketService;
    private BasketPathParams.BasketPathParamsBuilder pathParams;

    /**
     * Prepare the Path params for removeFlight getting data from testData values
     */
    private void setPathParams() {
        pathParams = BasketPathParams.builder()
                .basketId(testData.getData(BASKET_ID))
                .flightKey(testData.getData(FLIGHT_KEY))
                .path(REMOVE_FLIGHT);
    }

    /**
     * Create the request object, getting headers from testData and body from the previous method, and invoke the service;
     * it store the service created into testData SERVICE
     */
    private void invokeRemoveFlightService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        removeFlightFromBasketService = serviceFactory.removeFlightFromBasket(new RemoveFlightFromBasketRequest(headers.build(), pathParams.build()));
        testData.setData(SERVICE, removeFlightFromBasketService);
        removeFlightFromBasketService.invoke();
    }

    @Given("^I want to remove a flight from the basket$")
    public void setRemoveFlightParameters() throws EasyjetCompromisedException {
        getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
        BasketService basketService = testData.getData(BASKET_SERVICE);
        Basket basket = basketService.getResponse().getBasket();

        String flightKey = basket.getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("No passenger in the basket"))
                .getFlightKey();

        testData.setData(FLIGHT_KEY, flightKey);
    }

    /**
     * Remove a flight from the basket getting data from testData values;
     * it stores the basket in the response in testData
     */
    @Step("Remove flight from the basket")
    @When("^I send the request to removeFlights service$")
    public void sendRemoveFlightsRequest() {
        setPathParams();
        invokeRemoveFlightService();
    }

    @Then("^the flight will be removed from the basket$")
    public void theFlightWillBeRemovedFromTheBasket() {
        getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
        BasketService basketService = testData.getData(BASKET_SERVICE);
        basketAssertion.setResponse(basketService.getResponse());

        basketAssertion
                .theFlightIsNotPresentInTheBasket(testData.getData(FLIGHT_KEY));
    }

}