package feature.document.steps.services.managebookingservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.BookingDao;
import com.hybris.easyjet.database.hybris.dao.CartDao;
import com.hybris.easyjet.database.hybris.dao.FeesAndTaxesDao;
import com.hybris.easyjet.database.hybris.models.PassengerStatus;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.asserters.BasketsAssertion;
import com.hybris.easyjet.fixture.hybris.asserters.ChangeFlightAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.ChangeFlightRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ChangeFlightRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractFlights;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.Currency;
import com.hybris.easyjet.fixture.hybris.invoke.services.ChangeFlightService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import com.hybris.easyjet.models.PassengerMix;
import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.But;
import cucumber.api.java.en.Then;
import feature.document.steps.constants.StepsRegex;
import feature.document.steps.services.GetFlightsSteps;
import feature.document.steps.services.createbasketservices.GetBasketSteps;
import net.thucydides.core.annotations.Steps;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.*;
import java.util.stream.Stream;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.CHANGE_FLIGHT;

/**
 * ChangeFlightSteps handle the communication with the changeFlight service.
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class ChangeFlightSteps {

    private static final String INVALID = "invalid";

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Autowired
    private FeesAndTaxesDao feesAndTaxesDao;
    @Autowired
    private CartDao cartDao;
    @Autowired
    private BookingDao bookingDao;

    @Steps
    private GetFlightsSteps getFlightSteps;
    @Steps
    private GetBasketSteps getBasketSteps;
    @Steps
    private ChangeFlightAssertion changeFlightAssertion;
    @Steps
    private BasketsAssertion basketsAssertion;

    private ChangeFlightService changeFlightService;
    private BasketPathParams.BasketPathParamsBuilder basketPathParams;
    private ChangeFlightRequestBody.ChangeFlightRequestBodyBuilder changeFlightRequestBody;

    private String flightKey;
    private String price;
    private List<String> passengers;
    private HashMap<String, PassengerStatus> passengersStatus;

    private void setRequestPathParameter() {
        basketPathParams = BasketPathParams.builder()
                .basketId(testData.getData(BASKET_ID))
                .flightKey(testData.getData(FLIGHT_KEY))
                .path(CHANGE_FLIGHT);
    }

    private void setRequestBody() {
        changeFlightRequestBody = ChangeFlightRequestBody.builder()
                .newFlightKey(flightKey)
                .price(price)
                .passengers(passengers);
    }

    private void invokeChangeFlightService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        changeFlightService = serviceFactory.changeFlight(new ChangeFlightRequest(headers.build(), basketPathParams.build(), changeFlightRequestBody.build()));
        testData.setData(SERVICE, changeFlightService);
        changeFlightService.invoke();
    }

    private void sendChangeFlightRequest() {
        setRequestPathParameter();
        setRequestBody();
        invokeChangeFlightService();
    }

    @And("^I want to change a flight with another one" + StepsRegex.SECTOR + StepsRegex.FARE_TYPE + StepsRegex.PASSENGER_MIX + StepsRegex.DATES + StepsRegex.FLEXIBLE_DAYS + StepsRegex.DEAL + StepsRegex.CURRENCY + "$")
    public void setChangeFlightsParameters(String origin, String destination, String fareType, String passengerMix, String outboundDate, String inboundDate, String flexibleDays, String applicationId, String officeId, String corporateId, String currency) throws EasyjetCompromisedException {
        if (Objects.isNull(origin) && Objects.isNull(destination) && Objects.isNull(outboundDate) && Objects.isNull(inboundDate)) {
            origin = "different";
        }
        getFlightSteps.setChangeFlightsParameters(origin, destination, fareType, testData.getData(PASSENGER_MIX), outboundDate, inboundDate, flexibleDays, applicationId, officeId, corporateId, currency);
        getFlightSteps.sentGetFlightsRequest();
        FindFlightsResponse.Flight outboundFlight = testData.getData(OUTBOUND_FLIGHT);
        flightKey = outboundFlight.getFlightKey();
        // The flight was already checked by the getFlight steps; the price should be the same for all the passengers
        price = outboundFlight.getFareTypes().stream()
                .filter(flightFareType -> flightFareType.getFareTypeCode().equals(testData.getData(FARE_TYPE)))
                .findFirst().get().getPassengers().get(0).getBasePrice().toString();
        passengers = new ArrayList<>();
        if (StringUtils.isNotBlank(passengerMix)) {
            PassengerMix passengersMix = new PassengerMix(passengerMix);
            HashMap<String, Integer> passengersList = new HashMap<>();
            passengersList.put("adult", passengersMix.getAdult());
            passengersList.put("child", passengersMix.getChild());
            passengersList.put("infantOnSeat", passengersMix.getInfantOnSeat());
            passengersList.put("infantOnLap", passengersMix.getInfantOnLap());

            BasketService basketService = testData.getData(BASKET_SERVICE);
            Basket basket = basketService.getResponse().getBasket();
            basket.getOutbounds().stream()
                    .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                    .filter(flight -> flight.getFlightKey().equals(testData.getData(FLIGHT_KEY)))
                    .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                    .forEach(
                            passenger -> {
                                if (passenger.getPassengerDetails().getPassengerType().equals("infant")) {
                                    if (passenger.getFareProduct().getBundleCode().equals("InfantOnLap") && passengersList.get("infantOnLap") > 0) {
                                        passengers.add(passenger.getCode());
                                        passengersList.merge(
                                                "infantOnLap",
                                                -1,
                                                Integer::sum
                                        );
                                    } else {
                                        passengers.add(passenger.getCode());
                                        passengersList.merge(
                                                "infantOnSeat",
                                                -1,
                                                Integer::sum
                                        );
                                    }
                                } else if (passengersList.get(passenger.getPassengerDetails().getPassengerType()) > 0) {
                                    passengers.add(passenger.getCode());
                                    passengersList.merge(
                                            passenger.getPassengerDetails().getPassengerType(),
                                            -1,
                                            Integer::sum
                                    );
                                }
                            }
                    );
        }
    }

    @But("^the request for changeFlight (.*)$")
    public void theRequestForChangeFlightField(String invalidValue) {
        switch (invalidValue) {
            case "contains wrong price":
                price = "1.0";
                break;
            case "miss new flight key":
                flightKey = null;
                break;
            case "miss new flight base price":
                price = null;
                break;
            case "basketId is invalid":
                testData.setData(BASKET_ID, INVALID);
                break;
            case "old flightKey is not in the basket":
                testData.setData(FLIGHT_KEY, flightKey);
                break;
            case "new flightKey is already in the basket":
                flightKey = testData.getData(FLIGHT_KEY);
                break;
            case "passenger Ids is invalid":
                passengers = Collections.singletonList(INVALID);
                break;
        }
    }

    @And("^I sen[d|t] the changeFlight request$")
    public void iSendTheChangeFlightRequest() {
        passengersStatus = new HashMap<>();
        GetBookingResponse bookingResponse = testData.getData(GET_BOOKING_RESPONSE);
        Stream.concat(bookingResponse.getBookingContext().getBooking().getOutbounds().stream(), bookingResponse.getBookingContext().getBooking().getInbounds().stream())
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                .forEach(
                        passenger -> passengersStatus.put(passenger.getCode(), bookingDao.getBookingPassengerStatus(testData.getData(BOOKING_ID), passenger.getCode()))
                );
        sendChangeFlightRequest();
    }

    @And("^I change the flight$")
    public void changeFlight() {
        getFlightSteps.sentGetFlightsRequest();
        FindFlightsResponse.Flight outboundFlight = testData.getData(OUTBOUND_FLIGHT);
        flightKey = outboundFlight.getFlightKey();
        // The flight was already checked by the getFlight steps; the price should be the same for all the passengers
        price = outboundFlight.getFareTypes().stream()
                .filter(flightFareType -> flightFareType.getFareTypeCode().equals(testData.getData(FARE_TYPE)))
                .findFirst().get().getPassengers().get(0).getBasePrice().toString();
        passengers = testData.getData(PASSENGER_LIST);

        sendChangeFlightRequest();
    }

    @Then("^the new flight is added to the basket$")
    public void theNewFlightIsAddedToTheBasket() {
        getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
        BasketService basketService = testData.getData(BASKET_SERVICE);
        Basket basket = basketService.getResponse().getBasket();

        changeFlightAssertion
                .theNewFlightIsAddedToTheBasket(basket, flightKey)
                .taxesAndFeesAreAppliedToTheNewFlight(basket, flightKey, feesAndTaxesDao)
                .thePriceOfTheNewFlightIsRight(basket, testData.getData(FLIGHT_KEY), flightKey, cartDao)
                .passengerDetailsAreCopiedToTheNewFlight(basket, testData.getData(FLIGHT_KEY), flightKey, cartDao)
                .passengerProductsAreAddedToTheNewFlight(basket, testData.getData(FLIGHT_KEY), flightKey, cartDao)
                .theOldFlightIsDeactivated(basket, testData.getData(FLIGHT_KEY), flightKey, cartDao);
    }

    @Then("^no change flight fee for " + StepsRegex.FARE_TYPES + " fare will be added$")
    public void noChangeFlightFeeWillBeAdded(String fare) {
        getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
        BasketService basketService = testData.getData(BASKET_SERVICE);
        Basket basket = basketService.getResponse().getBasket();

        changeFlightAssertion
                .noChangeFlightFeeIsAdded(basket, flightKey, feesAndTaxesDao, fare);
    }

    @Then("^the change flight fee (FlightFlexiFee>60|FlightFlexiFee<59) will be added$")
    public void theChangeFlightFeeFlightFlexiFeeWillBeAdded(String feesCode) throws Throwable {
        getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
        BasketService basketService = testData.getData(BASKET_SERVICE);
        Basket basket = basketService.getResponse().getBasket();

        changeFlightAssertion
                .feeIsApplied(basket, flightKey, feesCode);
    }

    @Then("^the new basket calculation are right$")
    public void theNewBasketCalculationAreRight() {
        getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
        BasketService basketService = testData.getData(BASKET_SERVICE);
        Basket basket = basketService.getResponse().getBasket();

        Currency currency = basket.getCurrency();
        Double fee = feesAndTaxesDao.getAdminFees(currency.getCode()).get(0).getFeeValue();
        if (testData.keyExist(FARE_TYPE) && testData.getData(FARE_TYPE).equals("Flexi")) {
            fee = 0.0;
        }

        basketsAssertion
                .priceCalculationAreRight(Integer.valueOf(currency.getDecimalPlaces()), fee, basket);
    }

    @And("^the new passenger status is$")
    public void theNewPassengerStatusShouldBe(DataTable status) throws EasyjetCompromisedException {
        getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
        BasketService basketService = testData.getData(BASKET_SERVICE);
        Basket basket = basketService.getResponse().getBasket();

        basket.getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .filter(flight -> flight.getFlightKey().equals(flightKey))
                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                .filter(passenger -> passenger.getEntryStatus().equals("NEW"))
                .forEach(
                        passenger -> {
                            PassengerStatus expectedPassengerStatus = passengersStatus.get(passenger.getFareProduct().getPricing().getPriceDifference().getToeiCode());
                            PassengerStatus actualPassengerStatus = cartDao.getCartPassengerStatus(testData.getData(BASKET_ID), passenger.getCode());

                            Map<String, String> expectedStatus = status.asMap(String.class, String.class);
                            if (expectedStatus.containsKey("consignment")) {
                                if (!expectedStatus.get("consignment").isEmpty()) {
                                    expectedPassengerStatus.setConsignmentStatus(expectedStatus.get("consignment"));
                                }
                            } else {
                                expectedPassengerStatus.setConsignmentStatus(actualPassengerStatus.getConsignmentStatus());
                            }
                            if (expectedStatus.containsKey("APIS")) {
                                if (!expectedStatus.get("APIS").isEmpty()) {
                                    expectedPassengerStatus.setApisStatus(expectedStatus.get("APIS"));
                                }
                            } else {
                                expectedPassengerStatus.setApisStatus(actualPassengerStatus.getApisStatus());
                            }
                            if (expectedStatus.containsKey("ICTS")) {
                                if (!expectedStatus.get("ICTS").isEmpty()) {
                                    expectedPassengerStatus.setIctsStatus(expectedStatus.get("ICTS"));
                                }
                            } else {
                                expectedPassengerStatus.setIctsStatus(actualPassengerStatus.getIctsStatus());
                            }

                            changeFlightAssertion
                                    .passengerStatusIsCorrect(expectedPassengerStatus, actualPassengerStatus);
                        });
    }

    @And("^APIS details should not be removed$")
    public void APISDetailsShouldNotBeRemoved() throws EasyjetCompromisedException {
        getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
        BasketService basketService = testData.getData(BASKET_SERVICE);
        Basket basket = basketService.getResponse().getBasket();

        changeFlightAssertion.isAPISExistForNewlyAddedPassenger(basket);
    }

    @Then("^the passenger in the new flight is linked to the other flights$")
    public void thePassengerInTheNewFlightIsLinkedToTheOtherFlights() throws EasyjetCompromisedException {
        getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
        BasketService basketService = testData.getData(BASKET_SERVICE);
        Basket basket = basketService.getResponse().getBasket();

        changeFlightAssertion
                .passengerIsLinkedToOriginalLinkedFlights(basket, testData.getData(PASSENGER_LIST));
    }

}