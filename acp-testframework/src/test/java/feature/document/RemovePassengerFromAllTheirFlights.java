package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.FlightFinder;
import com.hybris.easyjet.database.hybris.dao.BasketDao;
import com.hybris.easyjet.database.hybris.dao.FeesAndTaxesDao;
import com.hybris.easyjet.database.hybris.models.FeesAndTaxesModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.WaitHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BookingHelper;
import com.hybris.easyjet.fixture.hybris.helpers.FlightHelper;
import com.hybris.easyjet.fixture.hybris.helpers.ManageBookingHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.RemovePassengerQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.AddHoldItemsRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AddHoldItemsToBasketRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.DeleteCustomerProfileRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.RemovePassengerRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.Currency;
import com.hybris.easyjet.fixture.hybris.invoke.services.DeleteCustomerProfileService;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.RemovePassengerService;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.AddHoldBagToBasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import cucumber.api.PendingException;
import cucumber.api.Scenario;
import cucumber.api.java.en.And;
import cucumber.api.java.en.But;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.config.constants.CommonConstants.ONE_ADULT;
import static com.hybris.easyjet.config.constants.CommonConstants.STANDARD;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by robertadigiorgio on 12/04/2017.
 */

@ContextConfiguration(classes = TestApplication.class)

public class RemovePassengerFromAllTheirFlights {

    public static final String FLIGHT = "1 Flight";
    public static final String OUTBOUND = "outbound";
    public static final String INBOUND = "inbound";
    public static final String ADULT = "adult";
    private static final String INVALID_BASKET_CODE = "000";
    private static final String INVALID_PASSENGER_CODE = "000";


    private RemovePassengerService removePassengerService;

    @Autowired
    private FlightFinder flightFinder;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private FeesAndTaxesDao feesAndTaxesDao;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BasketDao basketDao;
    @Autowired
    private FlightHelper flightHelper;
    @Autowired
    private ManageBookingHelper manageBookingHelper;
    @Autowired
    private BookingHelper bookingHelper;
    @Autowired
    private BookingHelper commitBookingHelper;
    private DeleteCustomerProfileService deleteCustomerProfileService;
    private BasketsResponse basketFlights;
    private Basket basket;
    private String basketId;
    private String passengerIdOutbound, passenger1IdOutbound, passenger2IdOutbound;
    private String passengerIdInbound;
    private String all_related_flights;
    private String numberFlight;
    private Integer passengerTotal;
    private String passengerWithoutInfant;
    private String infantIdReletedPassengerRemoved;


    @And("^I want to remove passenger for \"([^\"]*)\"$")
    public void iWantToRemovePassengerFor(String numFlight) throws Throwable {
        testData.setBasketId(basketHelper.getBasketService().getResponse().getBasket().getCode());
        basketFlights = basketHelper.getBasketService().getResponse();
        basketId = basketFlights.getBasket().getCode();
        passengerIdOutbound = basketFlights.getBasket().getOutbounds().stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .filter(
                        basketPassenger -> basketPassenger.getPassengerDetails().getPassengerType().equals(ADULT)
                ).findFirst().get().getCode();

        if (Objects.nonNull(basketDao.searchPassengerIdOutbound(passengerIdOutbound))) {
            String passengerInbound = basketDao.searchPassengerIdOutbound(passengerIdOutbound).get(0);
            passengerIdInbound = basketDao.codePassengerIdOutbound(Long.valueOf(passengerInbound));
        }
        numberFlight = numFlight;
    }

    @But("^the request contain \"([^\"]*)\"$")
    public void theRequestContain(String invalid) throws Throwable {

        switch (invalid) {
            case "invalid basketId":
                testData.setBasketId("BASKET");
                break;
            case "invalid passengerId":
                passengerIdOutbound = "PASSENGER";
                passengerIdInbound = "PASSENGER";
                break;
        }
    }

    @When("^I send a request to remove passenger$")
    public void iSendARequestToRemovePassenger() throws Throwable {
        iRemoveAPassenger(passengerIdOutbound);
        pollingLoop().untilAsserted(() -> {
            Optional.ofNullable(passengerIdInbound).ifPresent(this::iRemoveAPassenger);
        });
    }


    @And("^I will not remove the adult passenger from the flight$")
    public void iWillNotRemoveTheAdultPassengerFromTheFlight() throws Throwable {
        pollingLoop().untilAsserted(() -> {
            try {
                basketHelper.getBasket(basketId);
            } catch (Throwable throwable) {
            }
            if (numberFlight.equals(FLIGHT)) {
                basketHelper.getBasketService().assertThat().passengerIsInTheBasket(passengerIdOutbound, OUTBOUND);
            } else {
                basketHelper.getBasketService().assertThat().passengerIsInTheBasket(passengerIdOutbound, OUTBOUND);
                basketHelper.getBasketService().assertThat().passengerIsInTheBasket(passengerIdInbound, INBOUND);
            }
        });
    }


    @Then("^I will receive a \"([^\"]*)\" message$")
    public void iWillReceiveAMessage(String warningCode) throws Throwable {
        removePassengerService.assertThat().additionalInformationReturned(warningCode);
    }

    @Then("^I will receive a confirmation message$")
    public void iWillReceiveAConfirmationMessage() throws Throwable {
        pollingLoop().timeout(60000, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            try {
                basketHelper.getBasket(basketId);
            } catch (Throwable throwable) {
            }
            if (numberFlight.equals(FLIGHT)) {
                basketHelper.getBasketService().assertThat().passengerIsnotInTheBasket(passengerIdOutbound, OUTBOUND);
            } else {
                basketHelper.getBasketService().assertThat().passengerIsnotInTheBasket(passengerIdOutbound, OUTBOUND);
                basketHelper.getBasketService().assertThat().passengerIsnotInTheBasket(passengerIdInbound, INBOUND);
            }
            removePassengerService.assertThat().confirmation(basketId);
        });
    }

    @Then("^I will reapportion any associated admin fee across the remaining passengers on that flight$")
    public void iWillReapportionAnyAssociatedAdminFeeAcrossTheRemainingPassengersOnThatFlight() throws Throwable {
        passengerTotal = testData.getData(PASSENGERS_TOTAL);
        passengerTotal = passengerTotal * 2 - 4;
        Currency currency = basketHelper.getBasketService().getResponse().getBasket().getCurrency();

        BigDecimal adminFee = BigDecimal.ZERO;
        List<FeesAndTaxesModel> adminFees = feesAndTaxesDao.getFees("AdminFee", null, currency.getCode(), ADULT);
        if (adminFees.size() > 0) {
            adminFee = new BigDecimal(adminFees.get(0)
                    .getFeeValue()
                    .toString());
            if (basketHelper.getBasketService().getResponse().getBasket().getInbounds() != null)
                adminFee = adminFee.multiply(new BigDecimal("0.5")).setScale(Integer.valueOf(currency.getDecimalPlaces()), BigDecimal.ROUND_UP);
        }
        BigDecimal expectedAdminFee = adminFee;

        pollingLoop().untilAsserted(() -> {
            try {
                basketHelper.getBasket(basketId, testData.getChannel());
            } catch (Throwable throwable) {
            }

            basketHelper.getBasketService().assertThat().theAdminFeeForEachPassengerIsCorrect(Integer.valueOf(currency.getDecimalPlaces()), expectedAdminFee);
        });
    }


    @Then("^I recalculate the flight total$")
    public void iRecalculateTheFlightTotal() throws Throwable {
        removePassengerService.assertThat().confirmation(basketId);
        Currency currency = basketHelper.getBasketService().getResponse().getBasket().getCurrency();
        FeesAndTaxesModel fee = feesAndTaxesDao.getAdminFees(currency.getCode()).get(0);
        basketHelper.getBasketService().assertThat().priceCalculationAreRight(Integer.valueOf(currency.getDecimalPlaces()), fee.getFeeValue());
    }

    @And("^I added the hold bags and sport equipment to passeger$")
    public void iAddedTheHoldBagsAndSportEquipmentToPasseger() throws Throwable {
        Basket basket = basketHelper.getBasketService().getResponse().getBasket();
        BasketPathParams pathParams = BasketPathParams.builder().basketId(basket.getCode()).path(HOLD_BAG).build();
        AddHoldItemsRequestBody body =
                AddHoldItemsRequestBody.builder()
                        .productCode("20kgbag")
                        .quantity(1)
                        .passengerCode(passengerIdOutbound)
                        .flightKey("")
                        .excessWeightProductCode("")
                        .excessWeightQuantity(0)
                        .override(false)
                        .build();

        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
        AddHoldBagToBasketService addHoldBagToBasketService = serviceFactory.addHoldBagToBasket(new AddHoldItemsToBasketRequest(headers.build(), pathParams, body));
        addHoldBagToBasketService.invoke();

        basketHelper.getBasket(basket.getCode(), testData.getChannel());

        addHoldBagToBasketService.assertThat().holdBagAddedForEachPassenger(basketHelper.getBasketService().getResponse());


        pathParams = BasketPathParams.builder().basketId(basket.getCode()).path(SPORT_EQUIP).build();
        body =
                AddHoldItemsRequestBody.builder()
                        .productCode("Snowboard")
                        .quantity(1)
                        .passengerCode(passengerIdOutbound)
                        .flightKey("")
                        .excessWeightProductCode("")
                        .excessWeightQuantity(0)
                        .override(false)
                        .build();

        headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
        addHoldBagToBasketService = serviceFactory.addHoldBagToBasket(new AddHoldItemsToBasketRequest(headers.build(), pathParams, body));
        addHoldBagToBasketService.invoke();

        basketHelper.getBasket(basket.getCode(), testData.getChannel());

        addHoldBagToBasketService.assertThat().holdBagAddedForEachPassenger(basketHelper.getBasketService().getResponse());
    }

    @And("^I remove the cabin bags, hold bags and sport equipment from the basket for the passenger$")
    public void iRemoveTheCabinBagsHoldBagsAndSportEquipmentFromTheBasketForThePassenger() throws Throwable {
        if (numberFlight.equals(FLIGHT)) {
            Integer ItemPassengerOutBound = basketDao.checkRemoveItemtoPassenger((passengerIdOutbound));
            removePassengerService.assertThat().fieldIsEmpty(ItemPassengerOutBound);

        } else {
            Integer ItemPassengerOutBound = basketDao.checkRemoveItemtoPassenger((passengerIdOutbound));
            removePassengerService.assertThat().fieldIsEmpty(ItemPassengerOutBound);

            Integer ItemPassengerInBound = basketDao.checkRemoveItemtoPassenger((passengerIdInbound));
            removePassengerService.assertThat().fieldIsEmpty(ItemPassengerInBound);
        }
    }


    @And("^I send a request to the delete customer profile$")
    public void iSendARequestToTheDeleteCustomerProfile() throws Throwable {
        CustomerPathParams pathParams = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).build();
        deleteCustomerProfileService = serviceFactory.deleteCustomerDetails(new DeleteCustomerProfileRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams));
        deleteCustomerProfileService.invoke();
    }

    private void iRemoveAPassenger(String aPassengerId) {
        BasketPathParams pathParams = BasketPathParams.builder().basketId(testData.getBasketId()).path(PASSENGER).passengerId(aPassengerId).build();
        RemovePassengerQueryParams removePassengerQueryParams = RemovePassengerQueryParams.builder().all_related_flights(all_related_flights).build();
        removePassengerService = serviceFactory.removePassengerFromBasket(new RemovePassengerRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, removePassengerQueryParams));
        testData.setData(SERVICE, removePassengerService);

        final int[] attempts = {3};
        try {
            pollingLoop().until(() -> {
                removePassengerService.invoke();
                attempts[0]--;
                return removePassengerService.getStatusCode() == 200 || attempts[0] == 0;
            });
        } catch (Exception e) {
            removePassengerService.getResponse();
        }
        Scenario scenario = testData.getData(SCENARIO);
        if (!scenario.getSourceTagNames().contains("@negative")) {
            testData.setBasketId(removePassengerService.getResponse().getOperationConfirmation().getBasketCode());
        }
    }

    @When("^I send a request to Remove one passenger that have an infant on lap (.*) with (.*) and (.*)$")
    public void iSendARequestToRemoveOnePassengerThatHaveAnInfantOnLapBookingWithPassengerAndFareType(String booking, String passengerMix, String fareType) throws Throwable {

        if (testData.keyExist(SerenityFacade.DataKeys.CHANNEL)) {
            testData.setChannel(testData.getData(SerenityFacade.DataKeys.CHANNEL));
            testData.setPassengerMix(testData.getData(SerenityFacade.DataKeys.PASSENGER_MIX));
            testData.setOrigin(testData.getData(SerenityFacade.DataKeys.ORIGIN));
            testData.setDestination(testData.getData(SerenityFacade.DataKeys.DESTINATION));
            testData.setOutboundDate(testData.getData(SerenityFacade.DataKeys.OUTBOUND_DATE));
            testData.setInboundDate(testData.getData(SerenityFacade.DataKeys.INBOUND_DATE));
            testData.setBasketId(testData.getData(SerenityFacade.DataKeys.BASKET_ID));
        }
        if (booking.equals("no Booking")) {

            testData.setPassengerMix(passengerMix);
            FlightsService flightsService = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate());
            testData.setData(GET_FLIGHT_SERVICE, flightsService);
            testData.setFlightKey(flightsService.getOutboundFlight().getFlightKey());
            basketHelper.addFlightsToBasket(fareType, OUTBOUND);
            testData.setData(BASKET_SERVICE, basketHelper.getBasketService());
            testData.setBasketId(basketHelper.getBasketService().getResponse().getBasket().getCode());
        }

        basketId = testData.getBasketId();
        basketHelper.getBasket(basketId, testData.getChannel());
        basketFlights = basketHelper.getBasketService().getResponse();
        testData.setData(ORIGINAL_BASKET, basketHelper.getBasketService().getResponse().getBasket());
        passengerIdOutbound = basketFlights.getBasket().getOutbounds().stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .filter(
                        basketPassenger -> basketPassenger.getPassengerDetails().getPassengerType().equals(ADULT)
                ).findFirst().orElse(null).getCode();

        all_related_flights = "false";

        List<Basket.Passenger> passengers = basketFlights.getBasket().getOutbounds().stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        if (passengers.size() > 2) {
            List<Basket.Passenger> listPassengerIdOutbound = passengers.stream()
                    .filter(passenger -> Objects.nonNull(passenger.getInfantsOnLap()) && new ArrayList<>(passenger.getInfantsOnLap()).isEmpty())
                    .collect(Collectors.toList());

            passengerWithoutInfant = listPassengerIdOutbound.stream()
                    .filter(
                            passenger -> passenger.getPassengerDetails().getPassengerType().equals(ADULT)
                    ).findFirst().orElse(null).getCode();

            infantIdReletedPassengerRemoved = passengers.stream()
                    .filter(basketPassenger -> basketPassenger.getCode().equals(passengerIdOutbound))
                    .map(Basket.Passenger::getInfantsOnLap)
                    .flatMap(Collection::stream)
                    .findFirst().orElse(null);
        }

        BasketPathParams pathParams = BasketPathParams.builder().basketId(basketId).path(PASSENGER).passengerId(passengerIdOutbound).build();
        RemovePassengerQueryParams removePassengerQueryParams = RemovePassengerQueryParams.builder().all_related_flights(all_related_flights).build();

        removePassengerService = serviceFactory.removePassengerFromBasket(new RemovePassengerRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, removePassengerQueryParams));
        testData.setData(SERVICE, removePassengerService);
        removePassengerService.invoke();
    }

    @Then("^I check that the passenger has been removed$")
    public void iCheckThatThePassengerHasBeenRemoved() {
        Basket basket = basketHelper.getBasket(basketId, testData.getChannel());
        removePassengerService.assertThat().checkThatPassengerIsRemoved(basket, passengerIdOutbound, passengerWithoutInfant, infantIdReletedPassengerRemoved);
    }

    @When("^I send a request to Remove a passenger with invalid basket id for single flight ([^\"]*)$")
    public void iSendARequestToRemoveAPassengerWithInvalidBasketId(String allFlights) throws EasyjetCompromisedException {
        testData.setChannel(testData.getData(SerenityFacade.DataKeys.CHANNEL));
        testData.setBasketId(testData.getData(SerenityFacade.DataKeys.BASKET_ID));

        all_related_flights = allFlights;
        Basket basket = basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        passengerIdOutbound = manageBookingHelper.getPassengerCodeInTheBasket(basket, ADULT);

        BasketPathParams pathParams = BasketPathParams.builder().basketId(INVALID_BASKET_CODE).path(PASSENGER).passengerId(passengerIdOutbound).build();
        RemovePassengerQueryParams removePassengerQueryParams = RemovePassengerQueryParams.builder().all_related_flights(all_related_flights).build();

        removePassengerService = serviceFactory.removePassengerFromBasket(new RemovePassengerRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, removePassengerQueryParams));
        testData.setData(SERVICE, removePassengerService);
        removePassengerService.invoke();
    }

    @When("^I send a request to Remove a passenger with invalid passenger id for single flight ([^\"]*)$")
    public void iSendARequestToRemoveAPassengerWithInvalidPassengerId(String allFlights) {
        testData.setChannel(testData.getData(SerenityFacade.DataKeys.CHANNEL));
        testData.setBasketId(testData.getData(SerenityFacade.DataKeys.BASKET_ID));

        all_related_flights = allFlights;

        BasketPathParams pathParams = BasketPathParams.builder().basketId(testData.getBasketId()).path(PASSENGER).passengerId(INVALID_PASSENGER_CODE).build();
        RemovePassengerQueryParams removePassengerQueryParams = RemovePassengerQueryParams.builder().all_related_flights(all_related_flights).build();

        removePassengerService = serviceFactory.removePassengerFromBasket(new RemovePassengerRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, removePassengerQueryParams));
        testData.setData(SERVICE, removePassengerService);
        removePassengerService.invoke();
    }

    @When("^I send a request to Remove a passenger for single flight ([^\"]*)$")
    public void iSendARequestToRemoveAPassenger(String allFlights) throws EasyjetCompromisedException {
        testData.setChannel(testData.getData(SerenityFacade.DataKeys.CHANNEL));
        testData.setBasketId(testData.getData(SerenityFacade.DataKeys.BASKET_ID));

        all_related_flights = allFlights;
        Basket basket = basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        passengerIdOutbound = manageBookingHelper.getPassengerCodeInTheBasket(basket, ADULT);

        BasketPathParams pathParams = BasketPathParams.builder().basketId(testData.getBasketId()).path(PASSENGER).passengerId(passengerIdOutbound).build();
        RemovePassengerQueryParams removePassengerQueryParams = RemovePassengerQueryParams.builder().all_related_flights(all_related_flights).build();

        removePassengerService = serviceFactory.removePassengerFromBasket(new RemovePassengerRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, removePassengerQueryParams));
        testData.setData(SERVICE, removePassengerService);
        removePassengerService.invoke();
        Basket amendedBasket = basketHelper.getBasket(testData.getBasketId(), testData.getChannel());

        GetBookingResponse getBookingResponse = bookingHelper.getBookingDetails(testData.getData(SerenityFacade.DataKeys.BOOKING_ID), testData.getData(SerenityFacade.DataKeys.CHANNEL));
        assertThat(basketHelper.getRefundAmtForBookingLessThan24Hr(getBookingResponse, amendedBasket, passengerIdOutbound).compareTo(BigDecimal.valueOf(Math.abs(amendedBasket.getPriceDifference().getAmountWithDebitCard()))) == 0)
                .isTrue()
                .withFailMessage("incorrect refund amount");

    }

    @Then("^the passenger should be removed$")
    public void thePassengerShouldBeRemoved() throws Throwable {
        iRemoveAPassenger(passengerIdOutbound);
        removePassengerService.assertThat().confirmation(testData.getData(SerenityFacade.DataKeys.BASKET_ID));
    }

    @Then("^I will get the cancel fee ([^\"]*)$")
    public void thenReturnCancelFee(Double cancelFee) throws Throwable {
        Basket amendedBasket = basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        GetBookingResponse getBookingResponse = bookingHelper.getBookingDetails(testData.getData(SerenityFacade.DataKeys.BOOKING_ID), testData.getData(SerenityFacade.DataKeys.CHANNEL));
        assertThat(basketHelper.getCancelAmtForBookingLessThan24Hr(getBookingResponse, amendedBasket, passengerIdOutbound).compareTo(BigDecimal.valueOf(cancelFee)) == 0)
                .isTrue()
                .withFailMessage("incorrect cancel fee");
    }

    @When("^I send a request to Remove 2 passengers for single flight ([^\"]*)$")
    public void iSendARequestToRemove2Passenger(String allFlights) throws EasyjetCompromisedException {
        testData.setChannel(testData.getData(SerenityFacade.DataKeys.CHANNEL));
        testData.setBasketId(testData.getData(SerenityFacade.DataKeys.BASKET_ID));

        all_related_flights = allFlights;

        passenger1IdOutbound = manageBookingHelper.getPassengerCodeInTheBasket(1);
        BasketPathParams pathParams;

        pathParams = BasketPathParams.builder().basketId(testData.getBasketId()).path(PASSENGER).passengerId(passenger1IdOutbound).build();
        RemovePassengerQueryParams removePassengerQueryParams = RemovePassengerQueryParams.builder().all_related_flights(all_related_flights).build();

        removePassengerService = serviceFactory.removePassengerFromBasket(new RemovePassengerRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, removePassengerQueryParams));
        testData.setData(SERVICE, removePassengerService);
        removePassengerService.invoke();
        Basket amendedBasket = basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        GetBookingResponse getBookingResponse = bookingHelper.getBookingDetails(testData.getData(SerenityFacade.DataKeys.BOOKING_ID), testData.getData(SerenityFacade.DataKeys.CHANNEL));

        assertThat(basketHelper.getRefundAmtForBookingLessThan24Hr(getBookingResponse, amendedBasket, passenger1IdOutbound).compareTo(BigDecimal.valueOf(Math.abs(amendedBasket.getPriceDifference().getAmountWithDebitCard()))) == 0)
                .isTrue()
                .withFailMessage("incorrect refund amount");
        BigDecimal cancelAmountAfterPax1Remove = basketHelper.getCancelAmtForBookingLessThan24Hr(getBookingResponse, amendedBasket, passenger2IdOutbound);

        passenger2IdOutbound = manageBookingHelper.getPassengerCodeInTheBasket(2);

        pathParams = BasketPathParams.builder().basketId(testData.getBasketId()).path(PASSENGER).passengerId(passenger2IdOutbound).build();

        removePassengerService = serviceFactory.removePassengerFromBasket(new RemovePassengerRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, removePassengerQueryParams));
        removePassengerService.invoke();
        testData.setData(SERVICE, commitBookingHelper.getCommitBookingService());

    }

    @Then("^I will not add the cancel fee ([^\"]*)$")
    public void thenAssertNoCancelFee(Double cancelFee) throws Throwable {
        Basket amendedBasket = basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        GetBookingResponse getBookingResponse = bookingHelper.getBookingDetails(testData.getData(SerenityFacade.DataKeys.BOOKING_ID), testData.getData(SerenityFacade.DataKeys.CHANNEL));
        assertThat(basketHelper.getCancelAmtForBookingLessThan24Hr(getBookingResponse, amendedBasket, passenger2IdOutbound).compareTo(BigDecimal.valueOf(cancelFee)) == 0)
                .isTrue()
                .withFailMessage("incorrect cancel fee");
    }

    @And("^the infant is assigned to the second adult on lap$")
    public void theInfantIsAssignedToTheSecondAdultOnLap() throws Throwable {
        Basket originalBasket = testData.getData(ORIGINAL_BASKET);
        basketHelper.getBasketService().assertThat().theInfantIsOnTheLapOfTheSecondAdult(originalBasket);
    }

    @And("^I send a request to Remove first passenger$")
    public void iSendARequestToRemoveAPassengerFirstPassenger() throws Throwable {
        Basket basket = basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        passengerIdOutbound = manageBookingHelper.getPassengerCodeInTheBasket(basket, ADULT);
        BasketPathParams pathParams = BasketPathParams.builder().basketId(testData.getBasketId()).path(PASSENGER).passengerId(passengerIdOutbound).build();
        RemovePassengerQueryParams removePassengerQueryParams = RemovePassengerQueryParams.builder().all_related_flights(all_related_flights).build();
        removePassengerService = serviceFactory.removePassengerFromBasket(new RemovePassengerRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, removePassengerQueryParams));
        testData.setData(SERVICE, removePassengerService);
        retryRemovePassenger();
    }
    private void retryRemovePassenger(){
        int[] noOfRetry = {6};
        WaitHelper.pollingLoop().until(()->{
            removePassengerService.invoke();
            noOfRetry[0]--;
            return removePassengerService.getRestResponse().statusCode()==200 || noOfRetry[0]==0;

        });

    }

    @When("^I commit booking request for amendable basket after delete additional passenger$")
    public void iCommitBookingRequestAfterDeleteAdditionalBasket()throws Throwable {
        testData.setPassengerMix(ONE_ADULT);
        testData.setFareType(STANDARD);
        bookingHelper.getAmendableBasketWithSavedPassenger(testData.getPassengerMix(), testData.getFareType(), new Pair<>(false, false));
        iRemoveAPassenger(passenger1IdOutbound = manageBookingHelper.getPassengerCodeInTheBasket(1));
        basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        Double amountWithDebitCard = basketHelper.getBasketService().getResponse().getBasket().getPriceDifference().getAmountWithDebitCard();
        String originalPaymentMethodContext = bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel()).getBookingContext().getBooking().getPayments().stream().findFirst().orElse(null).getTransactionId();
        basketHelper.getBasket(testData.getBasketId());
        BasketService basketService = basketHelper.getBasketService();
        bookingHelper.commitBookingWithPartialRefund(basketService.getResponse(), Math.abs(amountWithDebitCard), "24_HOUR_CANCELLATION", originalPaymentMethodContext, "card",false);
    }

    @And("^I want to check the passenger status for booking is INACTIVE$")
    public void checkPassengersBookingStatusIsInactive()throws Throwable{
        bookingHelper.getGetBookingService().assertThat().getPassengerActiveStatus();
    }

    @And("^I want to check the passenger amend status for booking is CHANGED$")
    public void checkPassengerEntryStatusIsChnaged()throws Throwable {
        bookingHelper.getGetBookingService().assertThat().getPassengerEntryStatus();
    }
    @And("^I send a request to remove all passengers except one passenger$")
    public void iSendARequestToRemoveAllPassengersExceptOnePassenger() throws Throwable {
        Basket basket = basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        List<String> passengerCodes = basket.getOutbounds().stream().flatMap(a -> a.getFlights().stream().flatMap(b -> b.getPassengers().stream().map(c -> c.getCode()))).collect(Collectors.toList());
        passengerCodes.stream().forEach(code->{
            BasketPathParams pathParams = BasketPathParams.builder().basketId(testData.getBasketId()).path(PASSENGER).passengerId(code).build();
            RemovePassengerQueryParams removePassengerQueryParams = RemovePassengerQueryParams.builder().all_related_flights(all_related_flights).build();
            removePassengerService = serviceFactory.removePassengerFromBasket(new RemovePassengerRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, removePassengerQueryParams));
            testData.setData(SERVICE, removePassengerService);
            retryRemovePassenger();
        });
    }
}