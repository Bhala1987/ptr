package feature.document.steps.services.managebookingservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.BookingDao;
import com.hybris.easyjet.database.hybris.models.PassengerStatus;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.asserters.RemoveInfantOnLapAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.RemoveInfantOnLapRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.RemoveInfantOnLapRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractFlights;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.RemoveInfantOnLapService;
import cucumber.api.java.en.But;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import feature.document.steps.services.createbasketservices.GetBasketSteps;
import net.thucydides.core.annotations.Steps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collection;
import java.util.stream.Stream;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.REMOVE_INFANT_ON_LAP;

/**
 * RemoveInfantOnLapSteps handle the communication with the removeInfantOnLap service.
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class RemoveInfantOnLapSteps {

    private static final String INVALID = "INVALID";

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Autowired
    private BookingDao bookingDao;

    @Steps
    private GetBasketSteps getBasketSteps;
    @Steps
    private RemoveInfantOnLapAssertion removeInfantOnLapAssertion;

    private RemoveInfantOnLapService removeInfantOnLapService;
    private BasketPathParams.BasketPathParamsBuilder basketPathParams;
    private RemoveInfantOnLapRequestBody.RemoveInfantOnLapRequestBodyBuilder removeInfantOnLapRequestBody;

    private Basket basket;
    private String infantCode;
    private String relatedAdult;
    private PassengerStatus originalPassengerStatus;

    private void setRequestParameter() {
        basketPathParams = BasketPathParams.builder()
                .basketId(testData.getData(BASKET_ID))
                .passengerId(relatedAdult)
                .path(REMOVE_INFANT_ON_LAP);
    }

    private void setRequestBody() {
        removeInfantOnLapRequestBody = RemoveInfantOnLapRequestBody.builder()
                .infantOnLapPassengerCode(infantCode);
    }

    private void invokeRemoveInfantOnLapService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        removeInfantOnLapService = serviceFactory.removeInfantOnLap(new RemoveInfantOnLapRequest(headers.build(), basketPathParams.build(), removeInfantOnLapRequestBody.build()));
        testData.setData(SERVICE, removeInfantOnLapService);
        removeInfantOnLapService.invoke();
    }

    @Given("^I want to remove an infant on lap$")
    public void setRemoveInfantOnLapParameters() throws EasyjetCompromisedException {
        getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));

        BasketService basketService = testData.getData(BASKET_SERVICE);
        basket = basketService.getResponse().getBasket();

        infantCode = basket.getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                .filter(passenger -> passenger.getFareProduct().getType().equalsIgnoreCase("InfantOnLapProduct"))
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("No infant in the cart")).getCode();

        originalPassengerStatus = bookingDao.getBookingPassengerStatus(testData.getData(BOOKING_ID), infantCode);

        relatedAdult = basket.getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                .filter(passenger -> passenger.getInfantsOnLap().contains(infantCode))
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("The infant is not assigned to any passenger")).getCode();
    }

    @But("^request to remove infants contains (.*)$")
    public void sendRemoveInfantOnLapRequestWith(String param) throws EasyjetCompromisedException {
        switch (param.toLowerCase()) {
            case "invalidbasketid":
                testData.setData(BASKET_ID, INVALID);
                break;
            case "invalidpassengerid":
                relatedAdult = INVALID;
                break;
            case "emptyinfantid":
                relatedAdult = INVALID;
                infantCode = null;
                break;
            case "invalidinfantid":
                infantCode = INVALID;
                break;
            case "invalidinfantmap":
                infantCode = basket.getOutbounds().stream()
                        .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                        .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                        .filter(passenger -> passenger.getFareProduct().getType().equalsIgnoreCase("InfantOnLapProduct"))
                        .filter(passenger -> !passenger.getCode().equals(infantCode))
                        .findFirst().orElseThrow(() -> new EasyjetCompromisedException("No infant in the cart")).getCode();
                break;
            case "infantonseat":
                infantCode = basket.getOutbounds().stream()
                        .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                        .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                        .filter(passenger -> passenger.getPassengerDetails().getPassengerType().equals("infant"))
                        .filter(passenger -> !passenger.getFareProduct().getType().equalsIgnoreCase("InfantOnLapProduct"))
                        .findFirst().orElseThrow(() -> new EasyjetCompromisedException("No infant on seat in the cart")).getCode();
                break;
        }
    }

    @When("^I send the request to removeInfantOnLap service$")
    public void sendRemoveInfantOnLapRequest() {
        setRequestParameter();
        setRequestBody();
        invokeRemoveInfantOnLapService();
    }

    @Then("^the infant is removed from the cart$")
    public void theInfantIsRemovedFromTheCart() throws EasyjetCompromisedException {
        getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
        BasketService basketService = testData.getData(BASKET_SERVICE);
        basket = basketService.getResponse().getBasket();

        PassengerStatus actualPassengerStatus = bookingDao.getBookingPassengerStatus(testData.getData(BOOKING_ID), infantCode);

        String flightKey = Stream.concat(basket.getOutbounds().stream(), basket.getInbounds().stream())
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .filter(flight -> flight.getPassengers().stream()
                        .map(AbstractPassenger::getCode)
                        .anyMatch(passengerCode -> passengerCode.equals(relatedAdult)))
                .findFirst().get().getFlightKey();

        removeInfantOnLapAssertion
                .infantIsRemoved(basket, flightKey, infantCode)
                .infantProductIsRemoved(basket, flightKey, relatedAdult, infantCode)
                .statusIsNotChanged(originalPassengerStatus, actualPassengerStatus);
    }

}