package feature.document.steps.services.createbasketservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.asserters.BasketsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.RemovePassengerRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractFlights;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.RemovePassengerService;
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
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.PASSENGER;

/**
 * RemovePassengerSteps handle the communication with the removePassenger service (aka removePassengerFromBasket).
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 *
 * @author gd <g.dimartino@reply.it>
 */
@ContextConfiguration(classes = TestApplication.class)
public class RemovePassengerSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Steps
    private GetBasketSteps getBasketSteps;
    @Steps
    private BasketsAssertion basketAssertion;

    private RemovePassengerService removePassengerService;
    private BasketPathParams.BasketPathParamsBuilder pathParams;

    private void setPathParams() {
        pathParams = BasketPathParams.builder()
                .basketId(testData.getData(BASKET_ID))
                .passengerId(testData.getData(PASSENGER_ID))
                .path(PASSENGER);
    }

    private void invokeRemovePassengerService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        removePassengerService = serviceFactory.removePassengerFromBasket(new RemovePassengerRequest(headers.build(), pathParams.build(), null));
        testData.setData(SERVICE, removePassengerService);
        removePassengerService.invoke();
    }

    @Given("^I want to remove a passenger from the flight$")
    public void setRemovePassengerParameters() throws EasyjetCompromisedException {
        getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
        BasketService basketService = testData.getData(BASKET_SERVICE);
        Basket basket = basketService.getResponse().getBasket();

        String passengerId = basket.getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("No passenger in the basket"))
                .getCode();

        testData.setData(PASSENGER_ID, passengerId);
    }

    @Step("Remove flight from the basket")
    @When("^I send the request to removePassenger service$")
    public void sendRemovePassengerRequest() {
        setPathParams();
        invokeRemovePassengerService();
    }

    @Then("^the passenger will be removed from the basket$")
    public void theAddedFlightShouldBeRemovedFromBasket() {
        getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
        BasketService basketService = testData.getData(BASKET_SERVICE);
        basketAssertion.setResponse(basketService.getResponse());

        basketAssertion
                .thePassengerIsNotPresentInTheBasket(testData.getData(PASSENGER_ID));
    }

}
