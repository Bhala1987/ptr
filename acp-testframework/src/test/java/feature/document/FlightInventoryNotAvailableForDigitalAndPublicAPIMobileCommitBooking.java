package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.CartDao;
import com.hybris.easyjet.database.hybris.dao.PassengerInformationDao;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.CommitBookingService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.FLIGHT_FULLY_ALLOCATED;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;

/**
 * Created by giuseppedimartino on 26/04/17.
 */

@ContextConfiguration(classes = TestApplication.class)
public class FlightInventoryNotAvailableForDigitalAndPublicAPIMobileCommitBooking {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private CartDao cartDao;
    @Autowired
    private PassengerInformationDao passengerInformationDao;
    private Basket basket;
    private List<String> removedPassengers;

    @Then("^I should receive a confirmation message with code '(SVC_\\d+_\\d+)'$")
    public void iWillReturnConfirmationMessageToTheChannel(String errorCode) throws Throwable {
        basket = basketHelper.getBasketService().getResponse().getBasket();
        CommitBookingService commitBooking = testData.getData(SERVICE);
        commitBooking.assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }

    @And("^the flight should be removed from basket$")
    public void iWillRemoveTheFlightFromTheBasket() throws Throwable {
        removedPassengers = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                .map(Basket.Flights::getFlights).flatMap(Collection::stream)
                .filter(outboundFlight -> outboundFlight.getFlightKey().equals(testData.getActualFlightKey()))
                .map(Basket.Flight::getPassengers).flatMap(Collection::stream)
                .map(Basket.Passenger::getCode).collect(Collectors.toList());

        basketHelper.getBasket(basket.getCode());
        basketHelper.getBasketService().assertThat().theBasketDoesNotContainsTheFlight(testData.getData(FLIGHT_FULLY_ALLOCATED));

    }

    @And("^and the inbound journey type is now single$")
    public void andTheInboundJourneyTypeIsNowSingle() throws Throwable {
        basketHelper.getBasketService().assertThat().theFlightAssociatedWithTheRemovedFlightIsSingle(cartDao, basket.getCode(), null);
    }

    @And("^the information of the removed passengers should be removed$")
    public void theInformationOfTheRemovedPassengersShouldBeRemoved() throws Throwable {
        basketHelper.getBasketService().assertThat().thePassengerHaveNoInformationAssociated(passengerInformationDao, removedPassengers);
    }

    @And("^all other products which are associated to the flight should be removed$")
    public void allOtherProductsWhichAreAssociatedToTheFlightShouldBeRemoved() throws Throwable {
        basketHelper.getBasketService().assertThat().theBasketDoesNotContainsEntriesForTheFlight(cartDao, testData.getActualFlightKey());
    }

    @And("^the association of the passenger has been removed from (outbound|inbound|other) flight$")
    public void theAssociationOfThePassengerHasBeenRemovedFromInboundFlight(String journey) throws Throwable {
        if (!journey.equals("inbound"))
            basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                    .map(Basket.Flights::getFlights).flatMap(Collection::stream)
                    .filter(outboundFlight -> outboundFlight.getFlightKey().equals(testData.getActualFlightKey()))
                    .map(Basket.Flight::getPassengers).flatMap(Collection::stream)
                    .map(Basket.Passenger::getCode).forEach(
                    passenger -> {
                        basketHelper.getBasketService().assertThat().thePassengerIsNotAssociatedWithTheRemovedPassengers(cartDao, basket.getCode(), passenger, removedPassengers);
                    }
            );
        if (!journey.equals("outbound"))
            basketHelper.getBasketService().getResponse().getBasket().getInbounds().stream()
                    .map(Basket.Flights::getFlights).flatMap(Collection::stream)
                    .filter(outboundFlight -> outboundFlight.getFlightKey().equals(testData.getActualFlightKey()))
                    .map(Basket.Flight::getPassengers).flatMap(Collection::stream)
                    .map(Basket.Passenger::getCode).forEach(
                    passenger -> {
                        basketHelper.getBasketService().assertThat().thePassengerIsNotAssociatedWithTheRemovedPassengers(cartDao, basket.getCode(), passenger, removedPassengers);
                    }
            );

    }

}