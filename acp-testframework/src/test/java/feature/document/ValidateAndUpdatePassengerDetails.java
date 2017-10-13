package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.TravellerHelper;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Passenger;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Passengers;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.ORIGINAL_BASKET;

@ContextConfiguration(classes = TestApplication.class)
public class ValidateAndUpdatePassengerDetails {
    private static final Logger LOG = LogManager.getLogger(ValidateAndUpdatePassengerDetails.class);

    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private TravellerHelper travellerHelper;

    @When("^I send a request to update the age for the adult passenger to ([^\"]*) age$")
    public void updateAgeOfPassenger(String newAge) throws Throwable {
        testData.setData(ORIGINAL_BASKET, basketHelper.getBasketService().getResponse().getBasket());
        Passengers savedTraveller = travellerHelper.createValidRequestToAddPassengersForBasket(basketHelper.getBasketService().getResponse());
        Passenger passenger = getInfantPassengerWithRelatedAdult(savedTraveller);

        if (null != passenger)
            changeAgeOfAdultAssociatedToInfant(newAge, savedTraveller, passenger);

        basketHelper.updatePassengersForChannel(savedTraveller,
                testData.getChannel(),
                basketHelper.getBasketService().getResponse().getBasket().getCode());
    }

    private Passenger getInfantPassengerWithRelatedAdult(Passengers savedTraveller) throws IllegalAccessException {
        return savedTraveller.getPassengers().stream()
                .filter(h -> (!h.getRelatedAdult().isEmpty()) && h.getPassengerDetails().getPassengerType().equals(CommonConstants.INFANT)).findFirst()
                .orElseThrow(() -> new IllegalAccessException("No infant passenger in the basket"));
    }

    private void changeAgeOfAdultAssociatedToInfant(String newAge, Passengers savedTraveller, Passenger passenger) throws IllegalAccessException {
        savedTraveller.getPassengers().stream()
                .filter(p -> p.getCode().equals(passenger.getRelatedAdult()))
                .findFirst()
                .orElseThrow(() -> new IllegalAccessException("No adult passenger in the basket with an infant"))
                .setAge(Integer.parseInt(newAge));
    }

    @Then("^the infant is assigned to the second adult on their lap$")
    public void theInfantIsAssignedToTheSecondAdultOnTheirLap() throws Throwable {
        Basket originalBasket = testData.getData(ORIGINAL_BASKET);
        basketHelper.getBasketService().assertThat().theInfantIsOnTheLapOfTheSecondAdult(originalBasket);
    }
}
