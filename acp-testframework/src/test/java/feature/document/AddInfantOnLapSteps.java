package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.database.hybris.dao.FeesAndTaxesDao;
import com.hybris.easyjet.database.hybris.models.FeesAndTaxesModel;
import com.hybris.easyjet.fixture.hybris.helpers.AmendableBasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BookingHelper;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.AddInfantOnLapRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractFlights;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.Currency;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.AddInfantOnLapService;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.But;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BOOKING_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CHANNEL;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by vijayapalkayyam on 27/06/2017.
 */
@ContextConfiguration(classes = TestApplication.class)
public class AddInfantOnLapSteps {
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private AmendableBasketHelper amendableBasketHelper;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private BookingHelper bookingHelper;
    @Autowired
    private FeesAndTaxesDao feesAndTaxesDao;
    private BasketPathParams params;
    private AddInfantOnLapService addInfantOnLapService;
    private AddInfantOnLapRequestBody requestBody;
    private BasketService basketService;


    @Then("^I should receive an error (.*) while adding$")
    public void iShouldReceiveAnErrorWhileAdding(String errorCode) throws Throwable {
        amendableBasketHelper
                .getAddInfantOnLapService()
                .assertThatErrors()
                .containedTheCorrectErrorMessage(errorCode);
    }

    @When("^I attempt to add infantOnLap to (.*) passenger$")
    public void iAttemptToAddInfantOnLapToPassenger(String passenger) throws Throwable {
        amendableBasketHelper
                .generateParamsToAddInfantOnLapBasedOnPassenger(passenger)
                .generateValidRequestBodyToAddInfantOnLap()
                .invokeAddInfantOnLap();
    }

    @But("^I attempt to add add infantOnLap with no (.*) information$")
    public void infantonlapHasNoInformation(String missingField) throws Throwable {
        amendableBasketHelper
                .generateValidParamsToAddInfantOnLap()
                .generateRequestBodyToAddInfantOnLapWithMissingData(missingField)
                .invokeAddInfantOnLap();
    }

    @When("^I attempt to add infantOnLap with (.*) basket$")
    public void iAttemptToAddInfantOnLapWithBasket(String basketId) throws Throwable {
        amendableBasketHelper
                .generateParamsToAddInfantOnLapForBasket(basketId)
                .generateValidRequestBodyToAddInfantOnLap()
                .invokeAddInfantOnLap();
    }

    @Then("^InfantOnLap gets added successfully$")
    public void infantOnLapGetsAddedSuccessfully() throws Throwable {
        AddInfantOnLapService addInfantOnLapService = amendableBasketHelper.getAddInfantOnLapService();
        addInfantOnLapService.assertThat().containsPassengerCode(testData.getAmendableBasket());
        String infantOnLapCode = addInfantOnLapService.getResponse().getOperationConfirmation().getInfantOnLapPassengerCode();
        Basket basket = basketHelper.getBasket(testData.getAmendableBasket(), testData.getChannel());
        Currency currency = basket.getCurrency();
        FeesAndTaxesModel fee = feesAndTaxesDao.getAdminFees(currency.getCode()).get(0);
        basketHelper.getBasketService().assertThat()
                .passengerExists(infantOnLapCode)
                .hasCabinBagBundleAddedToThePassenger(infantOnLapCode)
                .infantOnLapBundleAdded(infantOnLapCode)
                .priceCalculationAreRight(Integer.valueOf(currency.getDecimalPlaces()), fee.getFeeValue(), basket);
    }

    @Then("^associate the infant on own seat to its associated adult passenger$")
    public void associateTheInfantOnOwnSeatToItsAssociatedAdultPassenger() throws Throwable {
      basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());
      basketHelper.getBasketService().assertThat().checkThatInfantOnSeatIsAssociatedToFirstAdult(basketHelper.getBasketService().getResponse().getBasket());
    }

    @And("^I select the (.*) passenger to associate an infant on seat to$")
    public void iSelectTheFirstPassengerToAssociateAnInfantOnSeatTo(String Passenger) throws Throwable {

        List<Basket.Passenger> passengers = basketHelper.getPassengerWithInfantOnLap(basketHelper.getBasketService().getResponse().getBasket());

        switch (Passenger){
            case "doesNotExist":
                testData.setPassengerId("000000491503422311881_20170829ALCLTN2224_87961334224");
                break;
            case "infant":
                List<Basket.Passenger> infants = basketHelper.getInfantOnLapOnFlight(basketHelper.getBasketService().getResponse().getBasket());
                testData.setPassengerId(infants.get(0).getCode());
                break;
            default:
                testData.setPassengerId(passengers.get(0).getCode());
                break;
        }
        testData.setData(SerenityFacade.DataKeys.BASKET_ID, basketHelper.getBasketService().getResponse().getBasket().getCode());
        testData.setData("PassengersOnFlight",getAllPassengers(basketHelper.getBasketService().getResponse().getBasket()));
    }

    private List<Basket.Passenger> getAllPassengers(Basket basket){
        return basket.getOutbounds().stream()
                .flatMap(flights -> flights.getFlights().stream())
                .flatMap(p -> p.getPassengers().stream())
                .filter(p -> p.getActive().equals(true))
                .collect(Collectors.toList());
    }

    @And("^I added an infant on lap in the amendable basket$")
    public void iAddedAnInfantOnLapInTheAmendableBasket() throws Throwable {
        Stream<Basket.Passenger> adultPassenger = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                .filter(passenger -> passenger.getPassengerDetails().getPassengerType().equalsIgnoreCase(CommonConstants.ADULT));

        testData.setData(SerenityFacade.DataKeys.PASSENGER_ID, adultPassenger.findFirst().get().getCode());
        basketHelper.invokeAddInfantOnLap(testData.getData(SerenityFacade.DataKeys.CHANNEL), testData.getData(SerenityFacade.DataKeys.BASKET_ID), testData.getData(SerenityFacade.DataKeys.PASSENGER_ID));
    }

    @And("^the booking should have an infant on lap$")
    public void theBookingShouldHaveAnInfantOnLap() throws Throwable {
        GetBookingResponse.Booking bookingDetails = bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getData(CHANNEL)).getBookingContext().getBooking();

        GetBookingResponse.Passenger passengerWithInfant = bookingDetails.getOutbounds().stream().flatMap(f->f.getFlights().stream()).flatMap(p->p.getPassengers().stream()).filter(pass -> pass.getCode().equalsIgnoreCase(testData.getData(SerenityFacade.DataKeys.PASSENGER_ID))).findFirst().orElseThrow(() -> new IllegalArgumentException("No passenger with code " + testData.getData(SerenityFacade.DataKeys.PASSENGER_ID)));

        assertThat(passengerWithInfant.getInfantsOnLap().get(0).equalsIgnoreCase(testData.getData(SerenityFacade.DataKeys.INFANT_ON_LAP_ID))).withFailMessage("No infant on lap has been associated to adult").isTrue();

        GetBookingResponse.Passenger infantOnLap = bookingDetails.getOutbounds().stream().flatMap(f->f.getFlights().stream()).flatMap(p->p.getPassengers().stream()).filter(pass -> pass.getCode().equalsIgnoreCase(testData.getData(SerenityFacade.DataKeys.INFANT_ON_LAP_ID))).findFirst().orElseThrow(() -> new IllegalArgumentException("No infant on lap passenger with code " + testData.getData(SerenityFacade.DataKeys.INFANT_ON_LAP_ID)));

        assertThat(infantOnLap.getFareProduct().getCode().equalsIgnoreCase(CommonConstants.INFANT_ON_LAP)).withFailMessage("Infant on lap booking is not DONE").isTrue();
    }
}