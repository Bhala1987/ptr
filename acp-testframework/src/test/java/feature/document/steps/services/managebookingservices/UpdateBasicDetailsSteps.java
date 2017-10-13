package feature.document.steps.services.managebookingservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.*;
import com.hybris.easyjet.database.hybris.models.FeesAndTaxesModel;
import com.hybris.easyjet.database.hybris.models.PassengerStatus;
import com.hybris.easyjet.database.hybris.models.PassengerTypeDbModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.asserters.Assertion;
import com.hybris.easyjet.fixture.hybris.asserters.BasketsAssertion;
import com.hybris.easyjet.fixture.hybris.asserters.RemoveInfantOnLapAssertion;
import com.hybris.easyjet.fixture.hybris.asserters.UpdateBasicDetailsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.UpdatePassengerDetailsQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.Name;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.UpdateBasicDetailsRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.UpdateBasicDetailsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractFlights;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.Currency;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.UpdateBasicDetailsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.GetBookingService;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import feature.document.steps.constants.StepsRegex;
import feature.document.steps.services.createbasketservices.GetBasketSteps;
import net.thucydides.core.annotations.Steps;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Stream;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.UPDATE_BASIC_DETAILS;

/**
 * UpdateBasicDetailsSteps handle the communication with the updateBasicDetails service.
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class UpdateBasicDetailsSteps {

    private static final String SVC_100009_2002 = "SVC_100009_2002"; // Change Age has resulted in a change in Passenger Type

    private static final String TITLE_FEE = "TitleFee";
    private static final String NAME_FEE_BEFORE_THRESHOLD = "NameFeeBeforeThreshold";
    private static final String NAME_FEE_AFTER_THRESHOLD = "NameFeeAfterThreshold";

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Autowired
    private PassengerTypeDao passengerTypeDao;
    @Autowired
    private FeesAndTaxesDao feesAndTaxesDao;
    @Autowired
    private CartDao cartDao;
    @Autowired
    private BookingDao bookingDao;
    @Autowired
    private PropertyValueConfigurationDao propertyValueConfigurationDao;

    @Steps
    private GetBasketSteps getBasketSteps;
    @Steps
    private Assertion assertion;
    @Steps
    private RemoveInfantOnLapAssertion removeInfantOnLapAssertion;
    @Steps
    private UpdateBasicDetailsAssertion updateBasicDetailsAssertion;
    @Steps
    private BasketsAssertion basketsAssertion;

    private UpdateBasicDetailsService updateBasicDetailsService;
    private BasketPathParams.BasketPathParamsBuilder basketPathParams;
    private UpdatePassengerDetailsQueryParams.UpdatePassengerDetailsQueryParamsBuilder updatePassengerDetailsQueryParams;
    private UpdateBasicDetailsRequestBody.UpdateBasicDetailsRequestBodyBuilder updateBasicDetailsRequestBody;

    private Basket basket;
    private String passengerId;
    private List<String> passengers;
    private List<PassengerStatus> originalBookingPassengerStatus = new ArrayList<>();
    private String title;
    private String name;
    private String originalPassengerType;

    private void setRequestPathParameter() {
        basketPathParams = BasketPathParams.builder()
                .basketId(testData.getData(BASKET_ID))
                .passengerId(passengerId)
                .path(UPDATE_BASIC_DETAILS);
    }

    private void setRequestQueryParameter(){
        updatePassengerDetailsQueryParams = UpdatePassengerDetailsQueryParams.builder()
                .operationTypeUpdate("UPDATE");
    }

    private void setRequestBody(Map<String, Object> details) {
        updateBasicDetailsRequestBody = UpdateBasicDetailsRequestBody.builder();

        for (Map.Entry<String, Object> entry : details.entrySet()) {
            try {
                updateBasicDetailsRequestBody.getClass().getMethod(entry.getKey(), entry.getValue().getClass()).invoke(updateBasicDetailsRequestBody, entry.getValue());
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
            }
        }
    }

    private void invokeUpdateBasicDetailsService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        updateBasicDetailsService = serviceFactory.updateBasicDetails(new UpdateBasicDetailsRequest(headers.build(), basketPathParams.build(), updatePassengerDetailsQueryParams.build(), updateBasicDetailsRequestBody.build()));
        testData.setData(SERVICE, updateBasicDetailsService);
        updateBasicDetailsService.invoke();
    }

    private void sendUpdateBasicDetailsRequest(Map<String, Object> details) {
        setRequestPathParameter();
        setRequestQueryParameter();
        setRequestBody(details);
        invokeUpdateBasicDetailsService();
    }

    @When("^I change the age of an? " + StepsRegex.PASSENGER_TYPES + " with " + StepsRegex.PASSENGER_TYPES + " age$")
    public void iChangeTheAgeOfOriginalPassengerTypeWithPassengerTypeAge(String originalPassengerType, String newPassengerType) throws EasyjetCompromisedException {
        this.originalPassengerType = originalPassengerType;
        getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
        BasketService basketService = testData.getData(BASKET_SERVICE);
        basket = basketService.getResponse().getBasket();

        String passengerType;
        if (originalPassengerType.startsWith("infant")) {
            passengerType = "infant";
        } else {
            passengerType = originalPassengerType;
        }

        Stream<Basket.Passenger> passengersCodes = basket.getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                .filter(passenger -> passenger.getPassengerDetails().getPassengerType().equals(passengerType));

        if (originalPassengerType.equals("infantOnSeat")) {
            passengersCodes = passengersCodes.filter(passenger -> !passenger.getFareProduct().getType().equalsIgnoreCase("InfantOnLapProduct"));
        } else if (originalPassengerType.equals("infantOnLap")) {
            passengersCodes = passengersCodes.filter(passenger -> passenger.getFareProduct().getType().equalsIgnoreCase("InfantOnLapProduct"));
        }

        passengerId = passengersCodes.findFirst()
                .orElseThrow(() -> new EasyjetCompromisedException("No " + originalPassengerType + " passenger is present in the basket"))
                .getCode();

        passengers = cartDao.getAssociatedPassenger(basket.getCode(), passengerId);
        passengers.add(0, passengerId);

        for (String passenger : passengers) {
            originalBookingPassengerStatus.add(bookingDao.getBookingPassengerStatus(testData.getData(BOOKING_ID), passenger));
        }

        PassengerTypeDbModel hybrisPassenger = passengerTypeDao.getPassengersOfType(newPassengerType);
        int age = new Random().nextInt(Math.min(99, hybrisPassenger.getMaxAge()) - hybrisPassenger.getMinAge()) + hybrisPassenger.getMinAge();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("age", age);

        sendUpdateBasicDetailsRequest(requestBody);
    }

    @When("^I change the title for a passenger$")
    public void iChangeTheTitleForAPassenger() throws EasyjetCompromisedException {
        getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
        BasketService basketService = testData.getData(BASKET_SERVICE);
        basket = basketService.getResponse().getBasket();

        passengerId = basket.getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("No passenger in the cart")).getCode();

        title = "miss";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", Name.builder().title(title).build());

        sendUpdateBasicDetailsRequest(requestBody);
    }

    @When("^I changed? (less|more) than the minimum chargeable characters in the name of a passenger$")
    public void iChangeTheNameForAPassenger(String quantity) throws EasyjetCompromisedException {
        getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
        BasketService basketService = testData.getData(BASKET_SERVICE);
        basket = basketService.getResponse().getBasket();

        Basket.Passenger passenger = basket.getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("No passenger in the cart"));

        passengerId = passenger.getCode();

        String passengerName = passenger.getPassengerDetails().getName().getFirstName();

        int threshold = Integer.parseInt(propertyValueConfigurationDao.getPropertyValueBasedOnName("numberOfChangedCharactersAllowedForNameChange"));

        if (quantity.equals("less")) {
            name = passengerName.concat(StringUtils.repeat("x", threshold));
        } else {
            name = passengerName.concat(StringUtils.repeat("x", threshold + 1));
        }
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", Name.builder().firstName(name).build());

        sendUpdateBasicDetailsRequest(requestBody);
    }

    @Then("^the passenger is changed to " + StepsRegex.PASSENGER_TYPES + "(?: for all the flights)?$")
    public void thePassengerIsChangedToPassengerType(String passengerType) throws EasyjetCompromisedException {
        assertion.additionalInformationContains(updateBasicDetailsService.getResponse(), SVC_100009_2002);

        getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
        BasketService basketService = testData.getData(BASKET_SERVICE);
        basket = basketService.getResponse().getBasket();
        Currency currency = basket.getCurrency();
        Double fee = feesAndTaxesDao.getAdminFees(currency.getCode()).get(0).getFeeValue();
        if (testData.keyExist(FARE_TYPE) && testData.getData(FARE_TYPE).equals("Flexi")) {
            fee = 0.0;
        }

        for (int i = 0; i < passengers.size(); i++) {
            passengerId = passengers.get(i);
            String flightKey = Stream.concat(basket.getOutbounds().stream(), basket.getInbounds().stream())
                    .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                    .filter(flight -> flight.getPassengers().stream()
                            .map(AbstractPassenger::getCode)
                            .anyMatch(passengerCode -> passengerCode.equals(passengerId)))
                    .findFirst().get().getFlightKey();

            PassengerStatus actualBookingPassengerStatus = bookingDao.getBookingPassengerStatus(testData.getData(BOOKING_ID), passengerId);

            if (passengerType.equals("infant")) {
                PassengerStatus actualCartPassengerStatus = cartDao.getCartPassengerStatus(testData.getData(BASKET_ID), passengerId);

                updateBasicDetailsAssertion
                        .originalPassengerIsSetToInactive(basket, flightKey, passengerId)
                        .newInfantOnLapIsAddedToTheFlight(basket, flightKey)
                        .newInfantHasTheSameStatusHasTheOriginalPassenger(basket, flightKey, actualCartPassengerStatus, cartDao);
            } else {
                if (originalPassengerType.startsWith("infant")) {
                    removeInfantOnLapAssertion
                            .infantIsRemoved(basket, flightKey, passengerId);
                    if (originalPassengerType.equals("infantOnLap")) {
                        GetBookingService bookingService = testData.getData(GET_BOOKING_SERVICE);
                        GetBookingResponse.Booking booking = bookingService.getResponse().getBookingContext().getBooking();

                        String relatedAdult = booking.getOutbounds().stream()
                                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                                .filter(aPassenger -> aPassenger.getInfantsOnLap().contains(passengerId))
                                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("The infant is not assigned to any passenger")).getCode();
                        removeInfantOnLapAssertion
                                .infantProductIsRemoved(basket, flightKey, relatedAdult, passengerId);
                    }

                    basketsAssertion
                            .passengerTypeIsChanged(basket, flightKey, passengerId, passengerType, "FareProduct")
                            .taxesAreAppliedForPassenger(basket, flightKey, passengerId, passengerType, currency.getCode(), feesAndTaxesDao);
                } else {
                    basketsAssertion
                            .passengerTypeIsChanged(basket, flightKey, passengerId, passengerType, "FareProduct");
                }

                updateBasicDetailsAssertion
                        .passengerStatusIsCorrect("Booking", flightKey, originalBookingPassengerStatus.get(i), actualBookingPassengerStatus);
            }
        }

        basketsAssertion
                .priceCalculationAreRight(Integer.valueOf(currency.getDecimalPlaces()), fee, basket);
    }

    @Then("^the TitleFee is added to the passenger$")
    public void theTitleFeeIsAddedToThePassenger() {
        getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
        BasketService basketService = testData.getData(BASKET_SERVICE);
        basket = basketService.getResponse().getBasket();

        Currency currency = basket.getCurrency();
        FeesAndTaxesModel adminFee = feesAndTaxesDao.getAdminFees(currency.getCode()).get(0);

        FeesAndTaxesModel titleFee = feesAndTaxesDao.getFeesBasedOnType(basket.getCurrency().getCode(), TITLE_FEE, testData.getData(CHANNEL)).get(0);

        List<String> passengers = cartDao.getAssociatedPassenger(basket.getCode(), passengerId);
        passengers.add(passengerId);

        updateBasicDetailsAssertion
                .titleIsChangedForThePassenger(basket, passengers, title)
                .feePriceIsRight(basket, passengers, titleFee);

        basketsAssertion
                .priceCalculationAreRight(Integer.valueOf(currency.getDecimalPlaces()), adminFee.getFeeValue(), basket);
    }

    @Then("^the NameFee (before|after) threshold is ((?:not )?added) to the passenger$")
    public void theNameFeeBeforeThresholdIsAddedToThePassenger(String fee, String applyFee) {
        getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
        final BasketService[] basketService = {testData.getData(BASKET_SERVICE)};
        basket = basketService[0].getResponse().getBasket();

        Currency currency = basket.getCurrency();
        FeesAndTaxesModel adminFee = feesAndTaxesDao.getAdminFees(currency.getCode()).get(0);

        FeesAndTaxesModel nameFee;
        if (fee.equals("before")) {
            nameFee = feesAndTaxesDao.getFeesBasedOnType(basket.getCurrency().getCode(), NAME_FEE_BEFORE_THRESHOLD, testData.getData(CHANNEL)).get(0);
        } else {
            nameFee = feesAndTaxesDao.getFeesBasedOnType(basket.getCurrency().getCode(), NAME_FEE_AFTER_THRESHOLD, testData.getData(CHANNEL)).get(0);
        }

        pollingLoop().untilAsserted(() -> {
            getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
            basketService[0] = testData.getData(BASKET_SERVICE);
            basket = basketService[0].getResponse().getBasket();

            List<String> passengers = cartDao.getAssociatedPassenger(basket.getCode(), passengerId);
            passengers.add(passengerId);

            updateBasicDetailsAssertion.nameIsChangedForThePassenger(basket, passengers, name);
            if (applyFee.equals("added")) {
                updateBasicDetailsAssertion.feePriceIsRight(basket, passengers, nameFee);
            } else {
                updateBasicDetailsAssertion.feeIsNotPresent(basket, passengers, nameFee);
            }

            basketsAssertion
                    .priceCalculationAreRight(Integer.valueOf(currency.getDecimalPlaces()), adminFee.getFeeValue(), basket);
        });
    }

}