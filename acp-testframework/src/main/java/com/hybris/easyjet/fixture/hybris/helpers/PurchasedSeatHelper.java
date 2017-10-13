package com.hybris.easyjet.fixture.hybris.helpers;


import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.database.hybris.dao.SSRDataDao;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Passengers;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.SpecialRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.SeatMapPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.SeatMapQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.AssociateInfantRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.purchasedseats.*;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.PurchasedSeatRequestBodyFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AssociateInfantRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.GetSeatMapRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.PurchasedSeatRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.GetSeatMapResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.SavedSSRs;
import com.hybris.easyjet.fixture.hybris.invoke.services.AssociateInfantService;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.GetSeatMapService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.PurchasedSeatService;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.GetAmendableBookingService;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.GetBookingService;
import cucumber.api.Scenario;
import lombok.Getter;
import lombok.Setter;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.config.constants.CommonConstants.*;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static com.hybris.easyjet.fixture.hybris.helpers.PurchasedSeatHelper.SEATPRODUCTS.EXTRA_LEGROOM;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.*;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.SeatMapPathParams.FlightPaths.GET_SEAT_MAP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by giuseppecioce on 12/04/2017.
 */
@Getter
@Setter
@Component
public class PurchasedSeatHelper {

    @Getter
    @Setter
    private PurchasedSeatService purchasedSeatService;
    @Setter
    @Getter
    private GetSeatMapService seatMapService;
    @Setter
    @Getter
    private Map newAssociationPassengerSeat;
    @Setter
    @Getter
    private String allocatePurchasedSeat;
    @Setter
    @Getter
    private List<String> allocatePurchasedSeatsForAllPax = new ArrayList<>();
    @Setter
    @Getter
    private PricingHelper basketTotalPrice = new PricingHelper();

    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private FlightHelper flightHelper;
    @Autowired
    private AmendableBasketHelper amendableBasketHelper;
    @Autowired
    private TravellerHelper travellerHelper;
    @Autowired
    private SSRDataDao ssrDataDao;

    private ArrayList<GetSeatMapResponse.Product> availableSeat;
    private ArrayList<GetSeatMapResponse.Bay> seatBlock = new ArrayList<>();
    private Map<String, Seat> associationPassengerSeat;
    private BasketHelper basketHelper;
    private AddPurchasedSeatsRequestBody addPurchasedSeatRequestBody;
    private RemovePurchasedSeatRequestBody removePurchasedSeatRequestBody;
    private PassengerSeatChangeRequestBody passengerSeatChangeRequestBody;
    private BasketPathParams purchasedSeatPathParams;
    private GetAmendableBookingService amendableBookingService;
    private String seatAddedCode;
    private String fare;
    private PricingHelper flightTotalPrice = new PricingHelper();
    private PricingHelper passengerTotalPrice = new PricingHelper();
    private PricingHelper seatTotalPrice = new PricingHelper();
    private BasketsResponse basketAfterAddingPurchasedSeat;
    private Basket basketAfterChangingPurchasedSeat;
    private String passengerCode = "";
    private Double discountForFlexiFare;
    private static final String CORRECT_SEAT = "correctSeat";
    private static final String NO_SEAT_AVAILABLE = "No seat are available for the requested type seat, impossible proceed with test";
    private static final String ALL_SEAT_AVAILABLE = "All seat are available from get map seat, impossible proceed with test";
    private static final String NO_SEAT_EMERGENCY_AVAILABLE = "No seat are available in emergency exit, impossible proceed with test";
    private BookingPathParams.BookingPathParamsBuilder bookingPathParams;
    private GetBookingService getBookingService;

    public enum SEATPRODUCTS {EXTRA_LEGROOM, STANDARD, UPFRONT, GENERIC, EMERGENCY_EXIT}

    @Autowired
    public PurchasedSeatHelper(BasketHelper basketHelper) {
        this.basketHelper = basketHelper;
        availableSeat = new ArrayList<>();
        associationPassengerSeat = new HashMap();
    }

    public BasketsResponse getBasketResponse(String basketCode) {
        basketHelper.getBasket(basketCode, testData.getChannel());
        return basketHelper.getBasketService().getResponse();
    }

    public void preRequirementsForRemovePurchasedSeat(SEATPRODUCTS seat) throws EasyjetCompromisedException {
        availableSeat.clear();
        passengerCode = "";

        getMapPurchasedSeat(null);
        addPurchasedSeat(getSeat(seat));

        basketAfterAddingPurchasedSeat = getBasketResponse(getBasketCode());
        initPriceBeforeRemovingSeat(basketAfterAddingPurchasedSeat);

        initRequestRemovePurchasedSeat(ADULT);

    }

    public String getPassengerCode(String type) {
        return basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getPassengerDetails().getPassengerType().equalsIgnoreCase(type)).findFirst().orElse(null).getCode();
    }

    public void preRequirementsForChangePurchasedSeat(String passengerMix, String fareType, SEATPRODUCTS seatFrom, SEATPRODUCTS seatTo) throws Throwable {
        availableSeat.clear();
        associationPassengerSeat.clear();
        passengerCode = "";
        seatAddedCode = initSeatName(seatTo);
        fare = fareType;

        basketHelper.myBasketContainsAFlightWithPassengerMix(passengerMix, testData.getChannel(), fare, false);

        basketAfterAddingPurchasedSeat = getBasketResponse(getBasketCode());
        initPriceBeforeRemovingSeat(basketAfterAddingPurchasedSeat);

        getMapPurchasedSeat(null);
        addPurchasedSeatForAllPassengerUniqueRequest(seatFrom);

        initRequestChangePurchasedSeat(seatTo);
    }

    public void preRequirementsForRemovePurchasedSeatMoreThanOnePassenger(String passengerType) throws EasyjetCompromisedException {
        availableSeat.clear();
        passengerCode = "";
        getMapPurchasedSeat(null);
        addPurchasedSeatForAllPassengerUniqueRequest(EXTRA_LEGROOM);
        basketAfterAddingPurchasedSeat = getBasketResponse(getBasketCode());
        initRequestRemovePurchasedSeat(passengerType);
    }

    private void preRequirementsForRemovePurchasedSeatMoreThanOnePassenger(String passengerType, SEATPRODUCTS aSeatProduct) throws EasyjetCompromisedException {
        availableSeat.clear();
        passengerCode = "";
        getMapPurchasedSeat(null);
        addPurchasedSeatForAllPassengerUniqueRequest(aSeatProduct);
        basketAfterAddingPurchasedSeat = getBasketResponse(getBasketCode());
        initRequestRemovePurchasedSeat(passengerType);
    }

    private void initPriceBeforeRemovingSeat(BasketsResponse basketsResponse) {
        initBasketPriceBeforeRemovingSeat(basketsResponse);
        initFlightPriceBeforeRemovingSeat(basketsResponse);
        initPassengerPriceBeforeRemovingSeat(basketsResponse, getFirstPassengerAdultFromFirstOutboundFlightInBasket());
        initSeatTotalPriceBeforeRemovingSeat(basketsResponse, getFirstPassengerAdultFromFirstOutboundFlightInBasket());
    }

    private void initBasketPriceBeforeRemovingSeat(BasketsResponse basketsResponse) {
        basketTotalPrice.setSubtotalAmountWithCreditCard(basketsResponse.getBasket().getSubtotalAmountWithCreditCard());
        basketTotalPrice.setSubtotalAmountWithDebitCard(basketsResponse.getBasket().getSubtotalAmountWithDebitCard());
        basketTotalPrice.setTotalAmountWithCreditCard(basketsResponse.getBasket().getTotalAmountWithCreditCard());
        basketTotalPrice.setTotalAmountWithDebitCard(basketsResponse.getBasket().getTotalAmountWithDebitCard());
    }

    private void initFlightPriceBeforeRemovingSeat(BasketsResponse basketsResponse) {
        flightTotalPrice.setTotalAmountWithCreditCard(basketsResponse.getBasket().getOutbounds().stream().mapToDouble(f -> f.getJourneyTotalWithCreditCard()).findFirst().orElse(0));
        flightTotalPrice.setTotalAmountWithDebitCard(basketsResponse.getBasket().getOutbounds().stream().mapToDouble(f -> f.getJourneyTotalWithDebitCard()).findFirst().orElse(0));
    }

    private void initPassengerPriceBeforeRemovingSeat(BasketsResponse basketsResponse, String passengerCode) {
        passengerTotalPrice.setTotalAmountWithCreditCard(basketsResponse.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null).getPassengerTotalWithCreditCard());
        passengerTotalPrice.setTotalAmountWithDebitCard(basketsResponse.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null).getPassengerTotalWithDebitCard());
    }

    private void initSeatTotalPriceBeforeRemovingSeat(BasketsResponse basketsResponse, String passengerCode) {
        Basket.Passenger basketPassenger = basketsResponse.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null);
        seatTotalPrice.setTotalAmountWithCreditCard(Objects.isNull(basketPassenger.getSeat()) ? 0 : basketPassenger.getSeat().getPricing().getTotalAmountWithCreditCard());
        seatTotalPrice.setTotalAmountWithDebitCard(Objects.isNull(basketPassenger.getSeat()) ? 0 : basketPassenger.getSeat().getPricing().getTotalAmountWithDebitCard());
    }

    private BasketConfirmationResponse addAvailableEmergencyExitPurchasedSeatToBasketForEachPassengerAndFlight() throws EasyjetCompromisedException {
        getMapPurchasedSeat(null);
        List<Seat> availableSeats = getAvailableSeats(null);
        availableSeats.removeIf(seat -> {
            return !isEmergencyExit(seat.getSeatNumber());
        });
        addPurchasedSeatForEachPassengerAndFlight(availableSeats);
        return purchasedSeatService.getResponse();
    }

    public void addPurchasedSeatToBasketForEachPassengerAndFlight(SEATPRODUCTS aSeatProduct, boolean emergencyExitRequired) throws EasyjetCompromisedException {
        getMapPurchasedSeat(null);
        testData.setData(EXTRA_LEGROOM_BASEPRICE, seatMapService.getResponse().getProducts().stream().filter(seatProduct -> seatProduct.getId().equalsIgnoreCase("1") && seatProduct.getName().equalsIgnoreCase("Extra Legroom")).findFirst().get().getBasePrice());
        testData.setData(UPFRONT_BASEPRICE, seatMapService.getResponse().getProducts().stream().filter(seatProduct -> seatProduct.getId().equalsIgnoreCase("2") && seatProduct.getName().equalsIgnoreCase("Up Front")).findFirst().get().getBasePrice());
        testData.setData(STANDARD_BASEPRICE, seatMapService.getResponse().getProducts().stream().filter(seatProduct -> seatProduct.getId().equalsIgnoreCase("3") && seatProduct.getName().equalsIgnoreCase("Standard")).findFirst().get().getBasePrice());
        List<Seat> allSeat = getAvailableSeats(aSeatProduct);

        if (!emergencyExitRequired) {
            allSeat.removeIf(seat -> {
                return isEmergencyExit(seat.getSeatNumber());
            });
        }
        addPurchasedSeatForEachPassengerAndFlight(allSeat);
        verifyRestrictedRule();
    }

    public void addPurchasedSeatWithAdditionalSeatToBasketForEachPassengerAndFlight(SEATPRODUCTS aSeatProduct, int addlSeat) throws EasyjetCompromisedException {
        addPurchasedSeatWithAdditionalSeatForEachPassengerAndFlight(aSeatProduct, addlSeat);
        verifyRestrictedRule();
    }

    private void verifyRestrictedRule() throws EasyjetCompromisedException {
        if (Objects.nonNull(purchasedSeatService.getErrors())) {
            List<String> errorCodeDynamicRule = purchasedSeatService.getErrors().getErrors().stream().map(e -> e.getCode()).collect(Collectors.toList());
            if (errorCodeDynamicRule.contains("SVC_100022_3012")) {
                throw new EasyjetCompromisedException("(type: SVC_100022_3012, message: Seat not available (restricted by dynamic rule))");
            } else if (errorCodeDynamicRule.contains("SVC_100500_5041")) {
                throw new EasyjetCompromisedException("(type: SVC_100500_5041, message: Seat not available (restricted by dynamic rule))");
            }
        }
    }

    private void addPurchasedSeatToBasketWithAdditionalSeatAlreadyAllocated(SEATPRODUCTS aSeatProduct, Integer additionalSeat) throws EasyjetCompromisedException {
        getMapPurchasedSeat(null);
        addPurchasedSeatWithAdditionalSeatAlreadyAllocated(getAvailableSeats(aSeatProduct), additionalSeat);
    }

    private void addPurchasedSeatToBasketWithAdditionalSeat(SEATPRODUCTS aSeatProduct, Integer additionalSeat) throws EasyjetCompromisedException {
        getMapPurchasedSeat(null);
        addPurchasedSeatWithAdditionalSeat(getAvailableSeats(aSeatProduct), additionalSeat);
    }

    public void addPurchasedSeatWithAdditionalSeat(SEATPRODUCTS aSeatProduct, Integer additionalSeat) throws EasyjetCompromisedException {
        getMapPurchasedSeat(null);
        addPurchasedSeatWithAdditionalSeatForAllFlights(getAvailableSeats(aSeatProduct), additionalSeat);
    }

    private void addPurchasedSeatToBasketWithoutAdditionalSeat(SEATPRODUCTS aSeatProduct) throws EasyjetCompromisedException {
        getMapPurchasedSeat(null);
        addPurchasedSeatWithoutAdditionalSeat(getAvailableSeats(aSeatProduct));
    }

    private void addPurchasedSeatToBasketWithoutPrimarySeat(SEATPRODUCTS aSeatProduct) throws EasyjetCompromisedException {
        getMapPurchasedSeat(null);
        addPurchasedSeatWithoutPrimarySeat(getAvailableSeats(aSeatProduct));
    }

    private void addPurchasedSeatToBasketWithoutPrimarySeat(SEATPRODUCTS aSeatProduct, Integer additionalSeat) throws EasyjetCompromisedException {
        getMapPurchasedSeat(null);
        addPurchasedSeatWithoutPrimarySeat(getAvailableSeats(aSeatProduct), additionalSeat);
    }

    private void addPurchasedSeatToBasketWithAdditionalSeatOnlyPrimarySeatAlreadyAllocated(SEATPRODUCTS aSeatProduct, Integer additionalSeat) throws EasyjetCompromisedException {
        getMapPurchasedSeat(null);
        addPurchasedSeatWithAdditionalSeatOnlyPrimarySeatAlreadyAllocated(getAvailableSeats(aSeatProduct), additionalSeat);
    }

    private void changePurchasedSeatToBasketWithAdditionalSeat(SEATPRODUCTS aSeatProduct, Integer additionalSeat) throws EasyjetCompromisedException {
        getMapPurchasedSeat(null);
        changePurchasedSeatWithAdditionalSeat(getAvailableSeats(aSeatProduct), additionalSeat);
    }

    private void changePurchasedSeatToBasketWithAdditionalWithSeatNull(SEATPRODUCTS aSeatProduct, Integer additionalSeat) throws EasyjetCompromisedException {
        getMapPurchasedSeat(null);
        changePurchasedSeatWithAdditionalSeatWithNull(getAvailableSeats(aSeatProduct), additionalSeat);
    }

    public void changePurchasedSeatAdditionalSeatsToAlreadyAllocated(SEATPRODUCTS aSeatProduct, Integer additionalSeat) throws EasyjetCompromisedException {
        getMapPurchasedSeat(null);
        changePurchasedSeatWithAdditionalSeat(Arrays.asList(getNotAvailableSeat(aSeatProduct)), additionalSeat);
    }

    public void changePurchasedSeatAdditionalSeatToNull(SEATPRODUCTS aSeatProduct, Integer additionalSeat) throws EasyjetCompromisedException {
        testData.setVerifySeatAllocation(true);
        changePurchasedSeatToBasketWithAdditionalWithSeatNull(aSeatProduct, additionalSeat);
        basketHelper.getBasket(testData.getAmendableBasket(), testData.getChannel());
        testData.setData(BASKET_SERVICE, basketHelper.getBasketService());
    }

    public void addPurchasedSeatToBasketJustOnePassenger(SEATPRODUCTS aSeatProduct) throws EasyjetCompromisedException {
        getMapPurchasedSeat(null);
        addPurchasedSeat(getSeat(aSeatProduct));
    }

    public void addAlreadyAllocateSeatOnePassenger(SEATPRODUCTS aSeatProduct) throws EasyjetCompromisedException {
        getMapPurchasedSeat(null);
        addPurchasedSeat(getNotAvailableSeat(aSeatProduct));
    }

    public void addPurchasedSeatForFirstPassengerType(SEATPRODUCTS aSeatProduct, String type) throws EasyjetCompromisedException {
        getMapPurchasedSeat(null);
        List<Seat> availableSeats = getAvailableSeats(aSeatProduct);
        availableSeats.removeIf(seat -> {
            return isEmergencyExit(seat.getSeatNumber());
        });
        if (availableSeats.isEmpty()) {
            throw new EasyjetCompromisedException("No seat available without restriction for desired type: " + aSeatProduct.name());
        }
        Random r = new Random();
        addPurchasedSeat(availableSeats.get(r.nextInt(availableSeats.size())), type);
    }

    void addPurchasedSeatForEachPassengerAndFlight(List<Seat> aChosenSeat) throws EasyjetCompromisedException {
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());
        Basket basket = basketHelper.getBasketService().getResponse().getBasket();
        final boolean[] exception = {false};
        final EasyjetCompromisedException[] e = new EasyjetCompromisedException[1];
        basket.getOutbounds().forEach(journey -> {
            for (Basket.Flight flight : journey.getFlights()) {
                try {
                    addPurchasedSeatForEachPassenger(flight.getFlightKey(), flight.getPassengers().stream().filter(pass -> !"InfantOnLap".equalsIgnoreCase(pass.getFareProduct().getBundleCode())).collect(Collectors.toList()), aChosenSeat);
                } catch (EasyjetCompromisedException ej) {
                    exception[0] = true;
                    e[0] = ej;
                }
            }
        });
        if (exception[0]) {
            throw e[0];
        }
    }

    private void addPurchasedSeatWithAdditionalSeatForEachPassengerAndFlight(SEATPRODUCTS aSeatProduct, int addlSeat) throws EasyjetCompromisedException {
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());
        Basket basket = basketHelper.getBasketService().getResponse().getBasket();
        final boolean[] exception = {false};
        final EasyjetCompromisedException[] e = new EasyjetCompromisedException[1];
        basket.getOutbounds().forEach(journey -> {
            for (Basket.Flight flight : journey.getFlights()) {
                try {
                    List<Basket.Passenger> passengers = flight.getPassengers().stream().collect(Collectors.toList());
                    passengers.removeIf(pax -> {
                        return pax.getFareProduct().getBundleCode().equalsIgnoreCase("InfantOnLap");
                    });
                    for (Basket.Passenger passenger : flight.getPassengers()) {
                        getMapPurchasedSeat(flight.getFlightKey());
                        List<Seat> allSeat = getAvailableSeats(aSeatProduct);
                        final List<Seat> aChosenSeatAfterConsecutiveCheck = checkAdditionalSeatAreAvailable(allSeat, 1+addlSeat);
                        addPurchasedSeatWithAdditionalSeatForEachPassenger(flight.getFlightKey(), passenger, aChosenSeatAfterConsecutiveCheck, addlSeat);
                    }
                } catch (EasyjetCompromisedException ej) {
                    exception[0] = true;
                    e[0] = ej;
                }
            }
        });
        if(exception[0]) {
            throw e[0];
        }
    }

    public void addContinuousPurchasedSeatForEachPassengerAndFlight(List<Seat> aChosenSeat) throws EasyjetCompromisedException {
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());
        Basket basket = basketHelper.getBasketService().getResponse().getBasket();
        final boolean[] exception = {false};
        final EasyjetCompromisedException[] e = new EasyjetCompromisedException[1];
        List<Basket.Passenger> pax = basket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(p -> p.getPassengers().stream()).collect(Collectors.toList());
        final List<Seat> aChosenSeatAfterConsecutiveCheck = checkAdditionalSeatAreAvailable(aChosenSeat, pax.size() - 1);
        basket.getOutbounds().forEach(journey -> {
            for (Basket.Flight flight : journey.getFlights()) {
                try {
                    addContinuousPurchasedSeatForEachPassenger(flight.getFlightKey(), flight.getPassengers().stream().filter(pass -> !"InfantOnLap".equalsIgnoreCase(pass.getFareProduct().getBundleCode())).collect(Collectors.toList()), aChosenSeatAfterConsecutiveCheck);
                } catch (EasyjetCompromisedException ej) {
                    exception[0] = true;
                    e[0] = ej;
                }
            }
        });
        if (exception[0]) {
            throw e[0];
        }
    }

    private void addPurchasedSeatWithAdditionalSeatAlreadyAllocated(List<Seat> aChosenSeat, Integer additionalSeat) throws EasyjetCompromisedException {
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());
        Basket basket = basketHelper.getBasketService().getResponse().getBasket();
        final boolean[] exception = {false};
        final EasyjetCompromisedException[] e = new EasyjetCompromisedException[1];
        basket.getOutbounds().forEach(journey -> {
            for (Basket.Flight flight : journey.getFlights()) {
                try {
                    List<Basket.Passenger> passengers = flight.getPassengers().stream()
                            .filter(passenger -> !"INFANTONLAP".equalsIgnoreCase(passenger.getFareProduct().getCode()))
                            .collect(Collectors.toList());
                    addPurchasedSeatWithAdditionalSeatAlreadyAllocatedForFlight(flight.getFlightKey(), passengers, aChosenSeat, additionalSeat);
                } catch (EasyjetCompromisedException ej) {
                    exception[0] = true;
                    e[0] = ej;
                }
            }
        });
        if (exception[0]) {
            throw e[0];
        }
    }

    private void addPurchasedSeatWithAdditionalSeat(List<Seat> aChosenSeat, Integer additionalSeat) throws EasyjetCompromisedException {
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());
        Basket basket = basketHelper.getBasketService().getResponse().getBasket();
        final boolean[] exception = {false};
        final EasyjetCompromisedException[] e = new EasyjetCompromisedException[1];
        basket.getOutbounds().forEach(journey -> {
            for (Basket.Flight flight : journey.getFlights()) {
                try {
                    List<Basket.Passenger> passengers = flight.getPassengers().stream()
                            .filter(passenger -> !"INFANTONLAP".equalsIgnoreCase(passenger.getFareProduct().getCode()))
                            .collect(Collectors.toList());
                    addPurchasedSeatWithAdditionalSeatForFlight(flight.getFlightKey(), passengers, aChosenSeat, additionalSeat);
                } catch (EasyjetCompromisedException ej) {
                    exception[0] = true;
                    e[0] = ej;
                }
            }
        });
        if (exception[0]) {
            throw e[0];
        }
    }

    private void addPurchasedSeatWithAdditionalSeatForAllFlights(List<Seat> aChosenSeat, Integer additionalSeat) throws EasyjetCompromisedException {
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());
        Basket basket = basketHelper.getBasketService().getResponse().getBasket();
        final boolean[] exception = {false};
        final EasyjetCompromisedException[] e = new EasyjetCompromisedException[1];
        basket.getOutbounds().forEach(journey -> {
            for (Basket.Flight flight : journey.getFlights()) {
                try {
                    List<Basket.Passenger> passengers = flight.getPassengers().stream()
                            .filter(passenger -> !"INFANTONLAP".equalsIgnoreCase(passenger.getFareProduct().getCode()))
                            .collect(Collectors.toList());
                    addPurchasedSeatWithAdditionalSeat(flight.getFlightKey(), passengers, aChosenSeat, additionalSeat);
                } catch (EasyjetCompromisedException ej) {
                    exception[0] = true;
                    e[0] = ej;
                }
            }
        });
        if(exception[0]) {
            throw e[0];
        }
    }

    private void addPurchasedSeatWithoutAdditionalSeat(List<Seat> aChosenSeat) throws EasyjetCompromisedException {
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());
        Basket basket = basketHelper.getBasketService().getResponse().getBasket();
        final boolean[] exception = {false};
        final EasyjetCompromisedException[] e = new EasyjetCompromisedException[1];
        basket.getOutbounds().forEach(journey -> {
            for (Basket.Flight flight : journey.getFlights()) {
                try {
                    List<Basket.Passenger> passengers = flight.getPassengers().stream()
                            .filter(passenger -> !"INFANTONLAP".equalsIgnoreCase(passenger.getFareProduct().getCode()))
                            .collect(Collectors.toList());
                    addPurchasedSeatWithoutAdditionalSeatForFlight(flight.getFlightKey(), passengers, aChosenSeat);
                } catch (EasyjetCompromisedException ej) {
                    exception[0] = true;
                    e[0] = ej;
                }
            }
        });
        if (exception[0]) {
            throw e[0];
        }
    }

    private void addPurchasedSeatWithoutPrimarySeat(List<Seat> aChosenSeat) throws EasyjetCompromisedException {
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());
        Basket basket = basketHelper.getBasketService().getResponse().getBasket();
        final boolean[] exception = {false};
        final EasyjetCompromisedException[] e = new EasyjetCompromisedException[1];
        basket.getOutbounds().forEach(journey -> {
            for (Basket.Flight flight : journey.getFlights()) {
                try {
                    List<Basket.Passenger> passengers = flight.getPassengers().stream()
                            .filter(passenger -> !"INFANTONLAP".equalsIgnoreCase(passenger.getFareProduct().getCode()))
                            .collect(Collectors.toList());
                    addPurchasedSeatWithoutPrimarySeatForFlight(flight.getFlightKey(), passengers, aChosenSeat);
                } catch (EasyjetCompromisedException ej) {
                    exception[0] = true;
                    e[0] = ej;
                }
            }
        });
        if (exception[0]) {
            throw e[0];
        }
    }

    private void addPurchasedSeatWithoutPrimarySeat(List<Seat> aChosenSeat, Integer additionalSeat) throws EasyjetCompromisedException {
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());
        Basket basket = basketHelper.getBasketService().getResponse().getBasket();
        final boolean[] exception = {false};
        final EasyjetCompromisedException[] e = new EasyjetCompromisedException[1];
        basket.getOutbounds().forEach(journey -> {
            for (Basket.Flight flight : journey.getFlights()) {
                try {
                    List<Basket.Passenger> passengers = flight.getPassengers().stream()
                            .filter(passenger -> !"INFANTONLAP".equalsIgnoreCase(passenger.getFareProduct().getCode()))
                            .collect(Collectors.toList());
                    addPurchasedSeatWithoutPrimarySeatForFlight(flight.getFlightKey(), passengers, aChosenSeat, additionalSeat);
                } catch (EasyjetCompromisedException ej) {
                    exception[0] = true;
                    e[0] = ej;
                }
            }
        });
        if (exception[0]) {
            throw e[0];
        }
    }

    private void addPurchasedSeatWithAdditionalSeatOnlyPrimarySeatAlreadyAllocated(List<Seat> aChosenSeat, Integer additionalSeat) throws EasyjetCompromisedException {
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());
        Basket basket = basketHelper.getBasketService().getResponse().getBasket();
        final boolean[] exception = {false};
        final EasyjetCompromisedException[] e = new EasyjetCompromisedException[1];
        basket.getOutbounds().forEach(journey -> {
            for (Basket.Flight flight : journey.getFlights()) {
                try {
                    List<Basket.Passenger> passengers = flight.getPassengers().stream()
                            .filter(passenger -> !"INFANTONLAP".equalsIgnoreCase(passenger.getFareProduct().getCode()))
                            .collect(Collectors.toList());
                    addPurchasedSeatWithAdditionalSeatOnlyPrimarySeatAlreadyAllocatedForFlight(flight.getFlightKey(), passengers, aChosenSeat, additionalSeat);
                } catch (EasyjetCompromisedException ej) {
                    exception[0] = true;
                    e[0] = ej;
                }
            }
        });
        if(exception[0]) {
            throw e[0];
        }
    }

    private void changePurchasedSeatWithAdditionalSeat(List<Seat> aChosenSeat, Integer additionalSeat) throws EasyjetCompromisedException {
        basketHelper.getBasket(testData.getAmendableBasket());
        amendableBookingService = (GetAmendableBookingService) testData.getData("amendableBookingService");
        Basket basket = basketHelper.getBasket(
                amendableBookingService.getResponse().getOperationConfirmation().getBasketCode(),
                testData.getChannel()
        );

        final boolean[] exception = {false};
        final EasyjetCompromisedException[] e = new EasyjetCompromisedException[1];
        basket.getOutbounds().forEach(journey -> {
            for (Basket.Flight flight : journey.getFlights()) {
                try {
                    changePurchasedSeatWithAdditionalSeatForFlight(flight.getPassengers(), aChosenSeat, additionalSeat);
                } catch (EasyjetCompromisedException ej) {
                    exception[0] = true;
                    e[0] = ej;
                }
            }
        });
        if (exception[0]) {
            throw e[0];
        }
    }

    private void changePurchasedSeatWithAdditionalSeatWithNull(List<Seat> aChosenSeat, Integer additionalSeat) throws EasyjetCompromisedException {
        basketHelper.getBasket(testData.getAmendableBasket());
        amendableBookingService = (GetAmendableBookingService) testData.getData("amendableBookingService");
        Basket basket = basketHelper.getBasket(
                amendableBookingService.getResponse().getOperationConfirmation().getBasketCode(),
                testData.getChannel()
        );

        final boolean[] exception = {false};
        final EasyjetCompromisedException[] e = new EasyjetCompromisedException[1];
        basket.getOutbounds().forEach(journey -> {
            for (Basket.Flight flight : journey.getFlights()) {
                try {
                    changePurchasedSeatWithAdditionalSeatNullForFlight(flight.getPassengers(), aChosenSeat, additionalSeat);
                } catch (EasyjetCompromisedException ej) {
                    exception[0] = true;
                    e[0] = ej;
                }
            }
        });
        if (exception[0]) {
            throw e[0];
        }
    }

    private void addPurchasedSeatForEachPassenger(String flightKey, List<Basket.Passenger> passengers, List<Seat> aChosenSeat) throws EasyjetCompromisedException {
        addPurchasedSeat(PurchasedSeatRequestBodyFactory.aMultiPassengerAddPurchasedSeat(flightKey, passengers, aChosenSeat));
    }

    private void addPurchasedSeatWithAdditionalSeatForEachPassenger(String flightKey, List<Basket.Passenger> passengers, List<Seat> aChosenSeat, int addlSeat) throws EasyjetCompromisedException {
        addPurchasedSeat(PurchasedSeatRequestBodyFactory.aMultiPassengerAddPurchasedSeatWithAdditionalSeat(flightKey, passengers, aChosenSeat, addlSeat));
    }

    private void addPurchasedSeatWithAdditionalSeatForEachPassenger(String flightKey, Basket.Passenger passenger, List<Seat> aChosenSeat, int addlSeat) throws EasyjetCompromisedException {
        addPurchasedSeat(PurchasedSeatRequestBodyFactory.aSinglePassengerAddPurchasedSeatWithAdditionalSeat(flightKey, passenger, aChosenSeat, addlSeat));
    }

    private void addContinuousPurchasedSeatForEachPassenger(String flightKey, List<Basket.Passenger> passengers, List<Seat> aChosenSeat) throws EasyjetCompromisedException {
        addPurchasedSeat(PurchasedSeatRequestBodyFactory.aMultiPassengerAddContinuousPurchasedSeat(flightKey, passengers, aChosenSeat));
    }

    private void addPurchasedSeatWithAdditionalSeatAlreadyAllocatedForFlight(String flightKey, List<Basket.Passenger> passengers, List<Seat> aChosenSeat, Integer additionalSeat) throws EasyjetCompromisedException {
        List<Seat> availableAdditionalSeat = checkAdditionalSeatAreAvailable(aChosenSeat, additionalSeat);
        Random r = new Random();
        addPurchasedSeat(availableAdditionalSeat.get(r.nextInt(availableAdditionalSeat.size())));
        addPurchasedSeat(PurchasedSeatRequestBodyFactory.aBasicAddPurchasedSeatWithAdditionalSeat(flightKey, passengers, availableAdditionalSeat, additionalSeat));
    }

    /**
     * The method proced to add additional seat, you can specify with test data if you want to add an additional seat that has already been allocated
     *
     * @param flightKey
     * @param passengers
     * @param aChosenSeat
     * @param additionalSeat
     * @throws EasyjetCompromisedException
     */
    private void addPurchasedSeatWithAdditionalSeatForFlight(String flightKey, List<Basket.Passenger> passengers, List<Seat> aChosenSeat, Integer additionalSeat) throws EasyjetCompromisedException {
        List<Seat> availableAdditionalSeat = checkAdditionalSeatAreAvailable(aChosenSeat, additionalSeat);
        addPurchasedSeat(PurchasedSeatRequestBodyFactory.aBasicAddPurchasedSeatWithAdditionalSeat(flightKey, passengers, availableAdditionalSeat, additionalSeat));
    }

    /**
     * The method proced to add additional seat, you can specify with test data if you want to add an additional seat that has already been allocated
     * @param flightKey
     * @param passengers
     * @param aChosenSeat
     * @param additionalSeat
     * @throws EasyjetCompromisedException
     */
    private void addPurchasedSeatWithAdditionalSeat(String flightKey, List<Basket.Passenger> passengers, List<Seat> aChosenSeat, Integer additionalSeat) throws EasyjetCompromisedException {
        List<Seat> availableAdditionalSeat = checkAdditionalSeatAreAvailable(aChosenSeat, additionalSeat);
        addPurchasedSeat(PurchasedSeatRequestBodyFactory.anAddPurchasedSeatWithAdditionalSeat(flightKey, passengers, availableAdditionalSeat, additionalSeat));
    }

    /**
     * The method proced to add without additional seat
     *
     * @param flightKey
     * @param passengers
     * @param aChosenSeat
     * @throws EasyjetCompromisedException
     */
    private void addPurchasedSeatWithoutAdditionalSeatForFlight(String flightKey, List<Basket.Passenger> passengers, List<Seat> aChosenSeat) throws EasyjetCompromisedException {
        addPurchasedSeat(PurchasedSeatRequestBodyFactory.aMultiPassengerAddPurchasedSeatWithoutAdditionalSeat(flightKey, passengers, aChosenSeat));
    }

    /**
     * The method proced to add without primary seat
     *
     * @param flightKey
     * @param passengers
     * @param aChosenSeat
     * @throws EasyjetCompromisedException
     */
    private void addPurchasedSeatWithoutPrimarySeatForFlight(String flightKey, List<Basket.Passenger> passengers, List<Seat> aChosenSeat) throws EasyjetCompromisedException {
        addPurchasedSeat(PurchasedSeatRequestBodyFactory.aMultiPassengerAddPurchasedSeatWithoutPrimarySeat(flightKey, passengers, aChosenSeat));
    }

    /**
     * The method proced to add without primary seat
     *
     * @param flightKey
     * @param passengers
     * @param aChosenSeat
     * @param additionalSeat
     * @throws EasyjetCompromisedException
     */
    private void addPurchasedSeatWithoutPrimarySeatForFlight(String flightKey, List<Basket.Passenger> passengers, List<Seat> aChosenSeat, Integer additionalSeat) throws EasyjetCompromisedException {

        List<Seat> availableAdditionalSeat = checkAdditionalSeat(passengers, aChosenSeat, additionalSeat);

        AddPurchasedSeatsRequestBody primarySeat = PurchasedSeatRequestBodyFactory.aBasicAddPurchasedSeatWithAdditionalSeat(flightKey, passengers, availableAdditionalSeat, additionalSeat);

        primarySeat.getPassengerAndSeats().forEach(seat -> {

            List<AdditionalSeat> temp = seat.getAdditionalSeats();

            seat.setAdditionalSeats(null);
            AddPurchasedSeatsRequestBody primarySeatTemp = AddPurchasedSeatsRequestBody.builder().flightKey(flightKey).passengerAndSeats(Collections.singletonList(seat)).build();
            addPurchasedSeat(primarySeatTemp);

            seat.setSeat(null);
            seat.setAdditionalSeats(temp);
            AddPurchasedSeatsRequestBody additionalSeatTemp = AddPurchasedSeatsRequestBody.builder().flightKey(flightKey).passengerAndSeats(Collections.singletonList(seat)).build();
            addPurchasedSeat(additionalSeatTemp);
        });
    }

    /**
     * The method proced to add the additional seat, primary seat being already allocated
     * @param flightKey
     * @param passengers
     * @param aChosenSeat
     * @param additionalSeat
     * @throws EasyjetCompromisedException
     */
    private void addPurchasedSeatWithAdditionalSeatOnlyPrimarySeatAlreadyAllocatedForFlight(String flightKey, List<Basket.Passenger> passengers, List<Seat> aChosenSeat, Integer additionalSeat) throws EasyjetCompromisedException {

        List<Seat> availableAdditionalSeat = checkAdditionalSeat(passengers, aChosenSeat, additionalSeat);

        AddPurchasedSeatsRequestBody primarySeat = PurchasedSeatRequestBodyFactory.aMultiPassengerAddPurchasedSeatWithoutPrimarySeat(flightKey, passengers, availableAdditionalSeat);

        primarySeat.getPassengerAndSeats().forEach(seat -> {

            List<AdditionalSeat> temp = seat.getAdditionalSeats();

            seat.setSeat(null);
            seat.setAdditionalSeats(temp);
            AddPurchasedSeatsRequestBody additionalSeatTemp = AddPurchasedSeatsRequestBody.builder().flightKey(flightKey).passengerAndSeats(Collections.singletonList(seat)).build();
            addPurchasedSeat(additionalSeatTemp);
        });
    }

    private List<Seat> checkAdditionalSeat(List<Basket.Passenger> passengers, List<Seat> aChosenSeat, Integer additionalSeat) throws EasyjetCompromisedException {
        List<Seat> availableAdditionalSeat = new ArrayList<>();
        List<Seat> availableSeatForPassenger;
        for (int i = 0; i < passengers.size(); i++) {
            availableSeatForPassenger = checkAdditionalSeatAreAvailable(aChosenSeat, additionalSeat);
            for (Seat newSeat : availableSeatForPassenger) {
                aChosenSeat.remove(newSeat);
                availableAdditionalSeat.add(newSeat);
            }
        }
        return availableAdditionalSeat;
    }

    private void changePurchasedSeatWithAdditionalSeatForFlight(List<Basket.Passenger> passengers, List<Seat> aChosenSeat, Integer additionalSeat) throws EasyjetCompromisedException {
        List<Seat> availableAdditionalSeat = new ArrayList<>();
        List<Seat> availableSeatForPassenger = new ArrayList<>();

        for (int i = 0; i < passengers.size(); i++) {

            availableSeatForPassenger = checkAdditionalSeatAreAvailable(aChosenSeat, additionalSeat);

            for (Seat newSeat : availableSeatForPassenger) {
                aChosenSeat.remove(newSeat);
                availableAdditionalSeat.add(newSeat);
            }

        }
        changePurchasedSeat(PurchasedSeatRequestBodyFactory.aBasicChangeSeatWithAdditionalSeat(passengers, availableAdditionalSeat, additionalSeat));
    }

    private void changePurchasedSeatWithAdditionalSeatNullForFlight(List<Basket.Passenger> passengers, List<Seat> aChosenSeat, Integer additionalSeat) throws EasyjetCompromisedException {
        List<Seat> availableAdditionalSeat = checkAdditionalSeatAreAvailable(aChosenSeat, additionalSeat);
        PassengerSeatChangeRequestBody requestBody = PurchasedSeatRequestBodyFactory.aBasicChangeSeatWithAdditionalSeat(passengers, availableAdditionalSeat, additionalSeat);
        requestBody.getPassengerSeatChangeRequests().stream().findFirst().orElse(null).getSeat().setSeatNumber("");
        requestBody.getPassengerSeatChangeRequests().stream().findFirst().orElse(null).getAdditionalSeats().stream().findFirst().orElse(null).getSeat().setSeatNumber("");
        changePurchasedSeat(requestBody);
    }

    public void addPurchasedSeatToBasket(SEATPRODUCTS aSeatProduct) throws EasyjetCompromisedException {
        getMapPurchasedSeat(null);
        addPurchasedSeat(getSeat(aSeatProduct));
    }

    /**
     * The method try to add an emergency exit seat to a passenger
     *
     * @param aSeatProduct have to be an EXTRA_LEGROOM seat (each seat in emergency exit belong to this type)
     * @return
     * @throws EasyjetCompromisedException
     */
    public void addPurchasedSeatEmergencyExitToBasket(SEATPRODUCTS aSeatProduct) throws EasyjetCompromisedException {
        getMapPurchasedSeat(null);
        addPurchasedSeat(getEmergencyExitSeat(aSeatProduct));
    }

    public void addEmergencyExitSeatToBasketOnSpecificPassenger(SEATPRODUCTS aSeatProduct, String passengerCode) throws EasyjetCompromisedException {
        getMapPurchasedSeat(null);
        addPurchasedSeatOnPassenger(getEmergencyExitSeat(aSeatProduct), passengerCode);
    }

    public void addPurchasedSeatAndOneEmergencySeatForFirstAdultPassengers() throws EasyjetCompromisedException {

        BasketPathParams addPurchasedSeatPathParams = BasketPathParams.builder()
                .basketId(getBasketCode())
                .path(ADD_PURCHASED_SEAT)
                .build();

        Map<String, Seat> passengerSeat = new HashMap<>();
        List<Seat> availableSeats = getAvailableSeats(null); // return as a list::Seat all available seat
        List<Seat> availableEmergencySeats = new ArrayList<>(availableSeats);
        List<Seat> availableNonEmergencySeats = new ArrayList<>(availableSeats);

        availableEmergencySeats.removeIf(s -> {
            return !isEmergencyExit(s.getSeatNumber());
        });

        // remove emergency seat exit
        availableNonEmergencySeats.removeIf(s -> {
            return isEmergencyExit(s.getSeatNumber());
        });

        if (availableEmergencySeats.isEmpty() || availableNonEmergencySeats.isEmpty()) {
            throw new EasyjetCompromisedException(NO_SEAT_AVAILABLE);
        }

        String passengerCodeWithoutInfant = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> ADULT.equalsIgnoreCase(h.getPassengerDetails().getPassengerType())
                        && h.getInfantsOnLap().isEmpty()).findFirst().orElse(null).getCode();

        String passengerCodeWithInfant = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> ADULT.equalsIgnoreCase(h.getPassengerDetails().getPassengerType())
                        && !h.getInfantsOnLap().isEmpty()).findFirst().orElse(null).getCode();

        passengerSeat.put(passengerCodeWithoutInfant, availableEmergencySeats.stream().findFirst().orElse(null));
        passengerSeat.put(passengerCodeWithInfant, availableNonEmergencySeats.stream().findFirst().orElse(null));
        for (Map.Entry<String, Seat> map : passengerSeat.entrySet()) {
            allocatePurchasedSeatsForAllPax.add(map.getValue().getSeatNumber());
        }

        addPurchasedSeatRequestBody = PurchasedSeatRequestBodyFactory.aMultiplePassengerAddPurchasedSeat(getFirstOutboundFlightKeyFromBasket(), passengerSeat);
        purchasedSeatService = serviceFactory.managePurchasedSeat(new PurchasedSeatRequest(HybrisHeaders.getValid(testData.getChannel()).build(),
                addPurchasedSeatPathParams,
                addPurchasedSeatRequestBody));

        purchasedSeatService.invoke();
        verifyRestrictedRule();
        purchasedSeatService.getResponse();
    }

    public void addPurchasedSeatToBasketWithInvalidRequest(SEATPRODUCTS aSeatProduct, String invalid) throws EasyjetCompromisedException {
        getMapPurchasedSeat(null);
        addPurchasedSeatWithInvalidRequest(getSeat(aSeatProduct), invalid);
    }

    private void getMapPurchasedSeat(String flightKey) {
        getSeatMap(flightKey);
        seatMapService.getResponse();
        testData.setSeatingServiceHelper(seatMapService);

        initMapPurchasedSeat();
    }

    private void initMapPurchasedSeat() {
        availableSeat.clear();
        if (Objects.isNull(testData.getSeatingServiceHelper())) {
            getMapPurchasedSeat(null);
        }
        testData.getSeatingServiceHelper().getResponse().getProducts().forEach(item -> {
            availableSeat.add(item);
            if (item.getName().equalsIgnoreCase(STANDARD)) {
                discountForFlexiFare = item.getBasePrice();
                if ("Flexi".equalsIgnoreCase(testData.getFareType())) {
                    testData.setSeatDiscountForFare(discountForFlexiFare);
                }
            }
        });
    }

    private String getFirstOutboundFlightKeyFromBasket() {
        return basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getFlightKey();
    }

    private String getFirstPassengerAdultFromFirstOutboundFlightInBasket() {
        if (passengerCode.isEmpty()) {
            passengerCode = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().stream().filter(f -> ADULT.equalsIgnoreCase(f.getPassengerDetails().getPassengerType())).findFirst().orElse(null).getCode();
        }
        return basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().stream().filter(f -> ADULT.equalsIgnoreCase(f.getPassengerDetails().getPassengerType())).findFirst().orElse(null).getCode();
    }

    private String getFirstPassengerChildFromFirstOutboundFlightInBasket() {
        if (passengerCode.isEmpty()) {
            passengerCode = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().stream().filter(f -> CHILD.equalsIgnoreCase(f.getPassengerDetails().getPassengerType())).findFirst().orElse(null).getCode();
        }
        return basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().stream().filter(f -> CHILD.equalsIgnoreCase(f.getPassengerDetails().getPassengerType())).findFirst().orElse(null).getCode();
    }

    private String getFirstPassengerInfantFromFirstOutboundFlightInBasket() {
        if (passengerCode.isEmpty()) {
            passengerCode = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().stream().filter(f -> INFANT.equalsIgnoreCase(f.getPassengerDetails().getPassengerType())).findFirst().orElse(null).getCode();
        }
        return basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().stream().filter(f -> INFANT.equalsIgnoreCase(f.getPassengerDetails().getPassengerType())).findFirst().orElse(null).getCode();
    }

    List<Seat> getAvailableSeats(SEATPRODUCTS aSeatProduct) throws EasyjetCompromisedException {
        initMapPurchasedSeat();
        List<Seat> seatList = new ArrayList<>();
        List<String> availability = seatMapService.getResponse().getAvailability().getAvailable().getSeats().
                stream().collect(Collectors.toList());
        final boolean[] noSeat = {false};
        availableSeat.stream().filter(Objects.nonNull(aSeatProduct) ? f -> f.getName().equalsIgnoreCase(initSeatName(aSeatProduct)) : f -> true).forEach((GetSeatMapResponse.Product seat) -> {
            List<String> seatNum;
            try {
                seatNum = getListAvailableSeatNumberForType(seat.getSeats(), availability);
            } catch (Exception e) {
                noSeat[0] = true;
                return;
            }

            for (String s : seatNum) {
                seatList.add(Seat.builder()
                        .seatNumber(s)
                        .price(seat.getBasePrice().toString())
                        .code(seat.getId())
                        .build());
            }
        });

        if (noSeat[0]) {
            throw new EasyjetCompromisedException(NO_SEAT_AVAILABLE);
        }
        return seatList;
    }

    public Seat getSeat(SEATPRODUCTS aSeatProduct) throws EasyjetCompromisedException {
        List<Seat> availableSeats = getAvailableSeats(aSeatProduct);
        Random r = new Random();
        return availableSeats.get(r.nextInt(availableSeats.size()));
    }

    private Seat getEmergencyExitSeat(SEATPRODUCTS aSeatProduct) throws EasyjetCompromisedException {
        List<Seat> availableSeats = getAvailableSeats(aSeatProduct);
        availableSeats.removeIf(seat -> {
            return !isEmergencyExit(seat.getSeatNumber()) && !verifySeatAlreadyAdded(seat.getSeatNumber());
        });
        Random r = new Random();
        return availableSeats.get(r.nextInt(availableSeats.size()));
    }

    boolean isEmergencyExit(String seatNumber) {
        return seatMapService.getResponse().getMetadata().getIsEmergencyExit().getSeats().stream().collect(Collectors.toList()).contains(seatNumber);
    }

    private List<String> getListAvailableSeatNumberForType(List<String> seatsNumber, List<String> availableSeat) throws Exception {
        List<String> seatNum = new ArrayList<>();
        for (String s : seatsNumber) {
            if (availableSeat.contains(s) && !verifySeatAlreadyAdded(s)) {
                seatNum.add(s);
            }
        }

        if (seatNum.isEmpty()) {
            throw new Exception();
        } else {
            return seatNum;
        }
    }

    private List<String> getListNotAvailableSeatNumberForType(List<String> seatsNumber, List<String> availableSeat) throws Exception {
        List<String> seatNum = new ArrayList<>();
        for (String s : seatsNumber) {
            if (!availableSeat.contains(s) || verifySeatAlreadyAdded(s)) {
                seatNum.add(s);
            }
        }

        if (seatNum.isEmpty()) {
            throw new Exception();
        } else {
            return seatNum;
        }
    }

    private List<Seat> getNotAvailableSeats(SEATPRODUCTS aSeatProduct) throws EasyjetCompromisedException {
        initMapPurchasedSeat();
        List<Seat> seatList = new ArrayList<>();
        List<String> availability = seatMapService.getResponse().getAvailability().getAvailable().getSeats().stream().collect(Collectors.toList());
        final boolean[] noSeat = {false};
        availableSeat.stream().filter(Objects.nonNull(aSeatProduct) ? f -> f.getName().equalsIgnoreCase(initSeatName(aSeatProduct)) : f -> true).forEach((GetSeatMapResponse.Product seat) -> {
            List<String> seatNum;
            try {
                seatNum = getListNotAvailableSeatNumberForType(seat.getSeats(), availability);
            } catch (Exception e) {
                noSeat[0] = true;
                return;
            }

            for (String s : seatNum) {
                seatList.add(Seat.builder()
                        .seatNumber(s)
                        .price(seat.getBasePrice().toString())
                        .code(seat.getId())
                        .build());
            }
        });

        if (noSeat[0]) {
            throw new EasyjetCompromisedException(ALL_SEAT_AVAILABLE);
        }
        return seatList;
    }

    private Seat getNotAvailableSeat(SEATPRODUCTS aSeatProduct) throws EasyjetCompromisedException {
        List<Seat> notAvailableSeats = getNotAvailableSeats(aSeatProduct);
        Random r = new Random();
        return notAvailableSeats.get(r.nextInt(notAvailableSeats.size()));
    }

    public String initSeatName(SEATPRODUCTS aSeatProduct) {
        switch (aSeatProduct) {
            case EXTRA_LEGROOM:
                return "extra legroom";
            case STANDARD:
                return "standard";
            case UPFRONT:
                return "up front";
            default:
                return "";
        }
    }

    private GetSeatMapResponse.Product getProductFromSeat(Seat aSeat) {

        final GetSeatMapResponse.Product[] myChosenProduct = {null};
        initMapPurchasedSeat();

        availableSeat.forEach(product -> {
            if (product.getId().equals(aSeat.getCode())) {
                myChosenProduct[0] = product;
            }
        });

        return myChosenProduct[0];
    }

    public GetSeatMapResponse.Product getProductFromSeat(SEATPRODUCTS aSeat) throws EasyjetCompromisedException {
        return getProductFromSeat(getSeat(aSeat));
    }

    public String getBasketCode() {
        return basketHelper.getBasketService().getResponse().getBasket().getCode();
    }

    private void addPurchasedSeat(Seat aChosenSeat) {
        allocatePurchasedSeat = "";
        String tmpPassengerCode = getFirstPassengerAdultFromFirstOutboundFlightInBasket();
        testData.setPassengerId(tmpPassengerCode);
        addPurchasedSeat(PurchasedSeatRequestBodyFactory.aBasicAddPurchasedSeat(getFirstOutboundFlightKeyFromBasket(), tmpPassengerCode, aChosenSeat));
        allocatePurchasedSeat = aChosenSeat.getSeatNumber();
    }

    private void addPurchasedSeatOnPassenger(Seat aChosenSeat, String passengerCode) {
        allocatePurchasedSeat = "";
        testData.setPassengerId(passengerCode);
        addPurchasedSeat(PurchasedSeatRequestBodyFactory.aBasicAddPurchasedSeat(getFirstOutboundFlightKeyFromBasket(), passengerCode, aChosenSeat));
        allocatePurchasedSeat = aChosenSeat.getSeatNumber();
    }

    private void addPurchasedSeat(Seat aChosenSeat, String typePassenger) {
        allocatePurchasedSeat = "";
        switch (typePassenger.toLowerCase()) {
            case ADULT:
                passengerCode = getFirstPassengerAdultFromFirstOutboundFlightInBasket();
                break;
            case CHILD:
                passengerCode = getFirstPassengerChildFromFirstOutboundFlightInBasket();
                break;
            case INFANT:
                passengerCode = getFirstPassengerInfantFromFirstOutboundFlightInBasket();
                break;
            default:
                break;
        }
        addPurchasedSeatRequestBody = PurchasedSeatRequestBodyFactory.aBasicAddPurchasedSeat(getFirstOutboundFlightKeyFromBasket(), passengerCode, aChosenSeat);
        addPurchasedSeat(addPurchasedSeatRequestBody);
        allocatePurchasedSeat = aChosenSeat.getSeatNumber();
    }

    private void addPurchasedSeatWithInvalidRequest(Seat aChosenSeat, String invalid) {

        switch (invalid) {
            case "invalid basketId":
                String basketId = "BASKET";
                addPurchasedSeatWithInvalidParameter(PurchasedSeatRequestBodyFactory.aBasicAddPurchasedSeat(getFirstOutboundFlightKeyFromBasket(), getFirstPassengerAdultFromFirstOutboundFlightInBasket(), aChosenSeat), basketId);
                break;
            case "invalid flightKey":
                String flightKey = "FLIGHTKEY";
                addPurchasedSeat(PurchasedSeatRequestBodyFactory.aBasicAddPurchasedSeat(flightKey, getFirstPassengerAdultFromFirstOutboundFlightInBasket(), aChosenSeat));
                break;
            case "invalid passengerId":
                String passengerIdOutbound = "PASSENGER";
                addPurchasedSeat(PurchasedSeatRequestBodyFactory.aBasicAddPurchasedSeat(getFirstOutboundFlightKeyFromBasket(), passengerIdOutbound, aChosenSeat));
                break;
            case "invalid basePrice":
                String basePrice = "0.0";
                aChosenSeat.setPrice(basePrice);
                addPurchasedSeat(PurchasedSeatRequestBodyFactory.aBasicAddPurchasedSeat(getFirstOutboundFlightKeyFromBasket(), getFirstPassengerAdultFromFirstOutboundFlightInBasket(), aChosenSeat));
                break;
            default:
                break;
        }
    }

    public void addPurchasedSeatToBasketWithInvalidSeatNumber(SEATPRODUCTS aChosenSeat) throws EasyjetCompromisedException {
        getMapPurchasedSeat(null);
        AddPurchasedSeatsRequestBody requestBody = PurchasedSeatRequestBodyFactory.aBasicAddPurchasedSeat(getFirstOutboundFlightKeyFromBasket(), getFirstPassengerAdultFromFirstOutboundFlightInBasket(), getSeat(aChosenSeat));
        requestBody.getPassengerAndSeats().get(0).getSeat().setSeatNumber("");
        addPurchasedSeat(requestBody);
    }

    public void addPurchasedSeatToBasketWithInvalidSeatCode(SEATPRODUCTS aChosenSeat) throws EasyjetCompromisedException {
        getMapPurchasedSeat(null);
        AddPurchasedSeatsRequestBody requestBody = PurchasedSeatRequestBodyFactory.aBasicAddPurchasedSeat(getFirstOutboundFlightKeyFromBasket(), getFirstPassengerAdultFromFirstOutboundFlightInBasket(), getSeat(aChosenSeat));
        requestBody.getPassengerAndSeats().get(0).getSeat().setCode("");
        addPurchasedSeat(requestBody);
    }

    public void addPurchasedSeatToBasketWithInvalidPrice(SEATPRODUCTS aChosenSeat) throws EasyjetCompromisedException {
        getMapPurchasedSeat(null);
        AddPurchasedSeatsRequestBody requestBody = PurchasedSeatRequestBodyFactory.aBasicAddPurchasedSeat(getFirstOutboundFlightKeyFromBasket(), getFirstPassengerAdultFromFirstOutboundFlightInBasket(), getSeat(aChosenSeat));
        requestBody.getPassengerAndSeats().get(0).getSeat().setPrice("abc");
        addPurchasedSeat(requestBody);
    }

    public void addPurchasedSeatToBasketWithSeatServiceDisabled() {
        AddPurchasedSeatsRequestBody requestBody = PurchasedSeatRequestBodyFactory.aBasicAddPurchasedSeat(getFirstOutboundFlightKeyFromBasket(), getFirstPassengerAdultFromFirstOutboundFlightInBasket(), Seat.builder().seatNumber("2A").code("1").price("20.00").build());
        requestBody.getPassengerAndSeats().get(0).getSeat().setSeatNumber("666");
        addPurchasedSeat(requestBody);
    }

    private void addPurchasedSeat(AddPurchasedSeatsRequestBody requestBody) {
        BasketPathParams addPurchasedSeatPathParams = BasketPathParams.builder()
                .basketId(getBasketCode())
                .path(ADD_PURCHASED_SEAT)
                .build();

        //Set this for later assertion on basket
        List<AddPurchasedSeatsRequestBody> myRequestList = testData.getPurchsedSeatRequestBody();
        myRequestList.add(requestBody);
        testData.setPurchsedSeatRequestBody(myRequestList);

        purchasedSeatService = serviceFactory.managePurchasedSeat(new PurchasedSeatRequest(HybrisHeaders.getValid(testData.getChannel()).build(),
                addPurchasedSeatPathParams,
                requestBody));
        testData.setData(SERVICE, purchasedSeatService);
        purchasedSeatService.invoke();
    }

    private void changePurchasedSeat(PassengerSeatChangeRequestBody requestBody) {
        purchasedSeatPathParams = BasketPathParams.builder()
                .basketId(getBasketCode())
                .flightKey(getFirstOutboundFlightKeyFromBasket())
                .path(CHANGE_PURCHASED_SEAT)
                .build()
        ;
        //Set this for later assertion on basket
        purchasedSeatService = serviceFactory.managePurchasedSeat(new PurchasedSeatRequest(HybrisHeaders.getValid(testData.getChannel()).build(),
                purchasedSeatPathParams,
                requestBody));
        purchasedSeatService.invoke();
        testData.setData("purchasedSeatService", purchasedSeatService);

    }

    private void addPurchasedSeatWithInvalidParameter(AddPurchasedSeatsRequestBody requestBody, String invalidBasketCode) {
        BasketPathParams addPurchasedSeatPathParams = BasketPathParams.builder()
                .basketId(invalidBasketCode)
                .path(ADD_PURCHASED_SEAT)
                .build();

        //Set this for later assertion on basket
        List<AddPurchasedSeatsRequestBody> myRequestList = testData.getPurchsedSeatRequestBody();
        myRequestList.add(requestBody);
        testData.setPurchsedSeatRequestBody(myRequestList);

        purchasedSeatService = serviceFactory.managePurchasedSeat(new PurchasedSeatRequest(HybrisHeaders.getValid(testData.getChannel()).build(),
                addPurchasedSeatPathParams,
                requestBody));
        testData.setData(SERVICE, purchasedSeatService);
        purchasedSeatService.invoke();
    }

    public void addPurchasedSeatForAllPassenger(SEATPRODUCTS seatFrom) throws EasyjetCompromisedException {
        addPurchasedSeatForAllPassengerUniqueRequest(seatFrom);
    }

    public void addPurchasedSeatForAllPassengerWithOutInfantOL(SEATPRODUCTS seatFrom) throws EasyjetCompromisedException {
        addPurchasedSeatForAllPassengerUniqueRequestWithOutInfantOL(seatFrom);
    }

    private void addPurchasedSeatForAllPassengerUniqueRequest(SEATPRODUCTS seatFrom) throws EasyjetCompromisedException {
        BasketPathParams addPurchasedSeatPathParams = BasketPathParams.builder()
                .basketId(getBasketCode())
                .path(ADD_PURCHASED_SEAT)
                .build();
        Map<String, Seat> passengerSeat = new HashMap<>();
        List<AbstractPassenger> passengers = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).collect(Collectors.toList());
        for (AbstractPassenger item : passengers) {
            String codeP = item.getCode();
            Seat seatP = getSeat(seatFrom);
            passengerSeat.put(codeP, seatP);
            associationPassengerSeat.put(codeP, seatP);
        }

        for (Map.Entry<String, Seat> map : passengerSeat.entrySet()) {
            allocatePurchasedSeatsForAllPax.add(map.getValue().getSeatNumber());
        }

        addPurchasedSeatRequestBody = PurchasedSeatRequestBodyFactory.aMultiplePassengerAddPurchasedSeat(getFirstOutboundFlightKeyFromBasket(), passengerSeat);
        purchasedSeatService = serviceFactory.managePurchasedSeat(new PurchasedSeatRequest(HybrisHeaders.getValid(testData.getChannel()).build(),
                addPurchasedSeatPathParams,
                addPurchasedSeatRequestBody));

        purchasedSeatService.invoke();
    }

    private void addPurchasedSeatForAllPassengerUniqueRequestWithOutInfantOL(SEATPRODUCTS seatFrom) throws EasyjetCompromisedException {
        BasketPathParams addPurchasedSeatPathParams = BasketPathParams.builder()
                .basketId(getBasketCode())
                .path(ADD_PURCHASED_SEAT)
                .build();
        Map<String, Seat> passengerSeat = new HashMap<>();
        List<AbstractPassenger> passengers = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .filter(passenger -> !"INFANT_ON_LAP".equalsIgnoreCase(passenger.getFareProduct().getType()))
                .collect(Collectors.toList());

        for (AbstractPassenger item : passengers) {
            String codeP = item.getCode();
            Seat seatP = getSeat(seatFrom);
            passengerSeat.put(codeP, seatP);
            associationPassengerSeat.put(codeP, seatP);
        }
        addPurchasedSeatRequestBody = PurchasedSeatRequestBodyFactory.aMultiplePassengerAddPurchasedSeat(getFirstOutboundFlightKeyFromBasket(), passengerSeat);
        purchasedSeatService = serviceFactory.managePurchasedSeat(new PurchasedSeatRequest(HybrisHeaders.getValid(testData.getChannel()).build(),
                addPurchasedSeatPathParams,
                addPurchasedSeatRequestBody));

        purchasedSeatService.invoke();
    }

    private boolean verifySeatAlreadyAdded(String s) {
        final boolean[] found = {false};
        associationPassengerSeat.forEach((String k, Seat v) -> {
            if (v.getSeatNumber().equalsIgnoreCase(s)) {
                found[0] = true;
            }
        });
        return found[0];
    }

    private void initRequestRemovePurchasedSeat(String passengerType) throws EasyjetCompromisedException {
        purchasedSeatPathParams = BasketPathParams.builder()
                .basketId(getBasketCode())
                .flightKey(getFirstOutboundFlightKeyFromBasket())
                .path(REMOVE_PURCHASED_SEAT)
                .build()
        ;
        passengerCode = ADULT.equalsIgnoreCase(passengerType) ? getFirstPassengerAdultFromFirstOutboundFlightInBasket() : getFirstPassengerChildFromFirstOutboundFlightInBasket();
        String seatForPassenger = getSeatNumberForPassenger(passengerCode);
        removePurchasedSeatRequestBody = PurchasedSeatRequestBodyFactory.aBasicRemovePurchasedSeat(passengerCode, seatForPassenger);
    }

    private void initRequestChangePurchasedSeat(SEATPRODUCTS seatTo) throws EasyjetCompromisedException {
        purchasedSeatPathParams = BasketPathParams.builder()
                .basketId(getBasketCode())
                .flightKey(getFirstOutboundFlightKeyFromBasket())
                .path(CHANGE_PURCHASED_SEAT)
                .build();
        newAssociationPassengerSeat = new HashMap();
        for (Object passenger : associationPassengerSeat.keySet()) {
            String passCode = (String) passenger;
            Seat temp = getSeat(seatTo);
            PassengerSeatChangeRequests.Seat change = PassengerSeatChangeRequests.Seat.builder().price(Double.valueOf(temp.getPrice())).seatNumber(temp.getSeatNumber()).build();
            newAssociationPassengerSeat.put(passCode, change);
        }

        passengerSeatChangeRequestBody = PurchasedSeatRequestBodyFactory.aMultiChangePurchasedSeat(newAssociationPassengerSeat);
    }

    public void changePurchasedSeatAlreadyAllocated(SEATPRODUCTS seatTo, String passengerCode) throws EasyjetCompromisedException {
        purchasedSeatPathParams = BasketPathParams.builder()
                .basketId(getBasketCode())
                .flightKey(getFirstOutboundFlightKeyFromBasket())
                .path(CHANGE_PURCHASED_SEAT)
                .build();

        newAssociationPassengerSeat = new HashMap();
        Seat tempSeat = getNotAvailableSeat(seatTo);
        PassengerSeatChangeRequests.Seat change = PassengerSeatChangeRequests.Seat.builder().price(Double.valueOf(tempSeat.getPrice())).seatNumber(tempSeat.getSeatNumber()).build();
        newAssociationPassengerSeat.put(passengerCode, change);

        passengerSeatChangeRequestBody = PurchasedSeatRequestBodyFactory.aMultiChangePurchasedSeat(newAssociationPassengerSeat);
    }

    public void changePurchasedSeatAlreadyAllocated(SEATPRODUCTS seatTo, String passengerCode, String flightKey) throws EasyjetCompromisedException {
        purchasedSeatPathParams = BasketPathParams.builder()
                .basketId(getBasketCode())
                .flightKey(flightKey)
                .path(CHANGE_PURCHASED_SEAT)
                .build();

        newAssociationPassengerSeat = new HashMap();
        List<Seat> allSeat = getAvailableSeats(seatTo);
        PassengerSeatChangeRequests.Seat change = PassengerSeatChangeRequests.Seat.builder().price(Double.valueOf(allSeat.get(0).getPrice())).seatNumber(allSeat.get(0).getSeatNumber()).build();
        newAssociationPassengerSeat.put(passengerCode, change);

        passengerSeatChangeRequestBody = PurchasedSeatRequestBodyFactory.aMultiChangePurchasedSeat(newAssociationPassengerSeat);
    }

    public void removeFieldFromRequestBody(String field, String typeOfRequest) {
        testData.setValidationScenarios(true);
        if ("basketId".equals(field)) {
            purchasedSeatPathParams = BasketPathParams.builder()
                    .basketId("0")
                    .flightKey(getFirstOutboundFlightKeyFromBasket())
                    .path("CHANGE".equalsIgnoreCase(typeOfRequest) ? CHANGE_PURCHASED_SEAT : REMOVE_PURCHASED_SEAT)
                    .build();
        } else if ("flightKey".equals(field)) {
            purchasedSeatPathParams = BasketPathParams.builder()
                    .basketId(getBasketCode())
                    .flightKey("0")
                    .path("CHANGE".equalsIgnoreCase(typeOfRequest) ? CHANGE_PURCHASED_SEAT : REMOVE_PURCHASED_SEAT)
                    .build();
        } else if ("passengerId".equals(field)) {
            if (Objects.nonNull(removePurchasedSeatRequestBody)) {
                removePurchasedSeatRequestBody.getPassengersAndSeatsNumbers().stream().forEach(item -> item.setPassengerId(null));
            } else if (Objects.nonNull(passengerSeatChangeRequestBody)) {
                passengerSeatChangeRequestBody.getPassengerSeatChangeRequests().stream().forEach(item -> item.setPassengerOnFlightId("0"));
            }
        } else if ("seats".equals(field) || "seatNumber".equals(field)) {
            if (Objects.nonNull(removePurchasedSeatRequestBody)) {
                removePurchasedSeatRequestBody.getPassengersAndSeatsNumbers().stream().forEach(item -> item.setSeats(null));
            } else if (Objects.nonNull(passengerSeatChangeRequestBody)) {
                passengerSeatChangeRequestBody.getPassengerSeatChangeRequests().stream().forEach(item -> item.getSeat().setSeatNumber(null));
            }
        }
    }

    public void invokeRemovePurchasedSeatService() {
        purchasedSeatService = serviceFactory.managePurchasedSeat(new PurchasedSeatRequest(HybrisHeaders.getValid(testData.getChannel()).build(),
                purchasedSeatPathParams,
                removePurchasedSeatRequestBody,
                "DELETE"));

        final int[] attempts = {3};
        pollingLoop().until(() -> {
            purchasedSeatService.invoke();
            attempts[0]--;
            return purchasedSeatService.getStatusCode() == 200 || attempts[0] == 0;
        });
        if (!testData.getValidationScenarios()) {
            purchasedSeatService.getResponse();
        }
    }

    public void invokeChangePurchasedSeatService() throws EasyjetCompromisedException {
        purchasedSeatService = serviceFactory.managePurchasedSeat(new PurchasedSeatRequest(HybrisHeaders.getValid(testData.getChannel()).build(),
                purchasedSeatPathParams,
                passengerSeatChangeRequestBody));
        purchasedSeatService.invoke();
        String firstPassengerCode = passengerSeatChangeRequestBody.getPassengerSeatChangeRequests().get(0).getPassengerOnFlightId();
        String seatForPassenger = passengerSeatChangeRequestBody.getPassengerSeatChangeRequests().get(0).getSeat().getSeatNumber();
        initPriceChangePurchasedSeat(firstPassengerCode, seatForPassenger, getBasketCode());
    }

    public void invokeChangePurchasedSeat() throws EasyjetCompromisedException {
        purchasedSeatService = serviceFactory.managePurchasedSeat(new PurchasedSeatRequest(HybrisHeaders.getValid(testData.getChannel()).build(),
                purchasedSeatPathParams,
                passengerSeatChangeRequestBody));
        purchasedSeatService.invoke();
    }

    private void initPriceChangePurchasedSeat(String passengerCode, String seatForPassenger, String basketCode) throws EasyjetCompromisedException {
        if (!testData.getValidationScenarios()) {
            final BasketsResponse[] basketsResponse = new BasketsResponse[1];
            final AbstractPassenger.Seat[] seat = new AbstractPassenger.Seat[1];
            try {
                purchasedSeatService.getResponse();
                pollingLoop().until(() -> {
                    basketsResponse[0] = getBasketResponse(basketCode);
                    seat[0] = basketsResponse[0].getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null).getSeat();
                    return Objects.nonNull(seat[0]) && seat[0].getSeatNumber().equalsIgnoreCase(seatForPassenger);
                });
            } catch (ConditionTimeoutException e) {
                throw new EasyjetCompromisedException("The expected seat " + seatForPassenger + " has not been found for passenger " + passengerCode);
            }

            // Right Basket after changing purchased seat
            basketAfterChangingPurchasedSeat = basketsResponse[0].getBasket();

            verifyMatchPriceFromGetSeatMap(seat[0]);
            // Right Price for seat after changing it
            seatTotalPrice.setTotalAmountWithCreditCard(seat[0].getPricing().getTotalAmountWithCreditCard());
            seatTotalPrice.setTotalAmountWithDebitCard(seat[0].getPricing().getTotalAmountWithDebitCard());
        }
    }

    private void verifyMatchPriceFromGetSeatMap(AbstractPassenger.Seat seat) {
        BigDecimal aPrice = BigDecimal.valueOf(seat.getPricing().getBasePrice()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal expectedPrice = BigDecimal.valueOf(availableSeat.stream().filter(f -> f.getName().equalsIgnoreCase(seatAddedCode) && f.getSeats().contains(seat.getSeatNumber())).mapToDouble(g -> Double.valueOf(g.getBasePrice())).findFirst().orElse(0.0));
        BigDecimal discountToApply = BigDecimal.valueOf(FLEXI.equalsIgnoreCase(fare) ? testData.getSeatDiscountForFare(): 0.0);
        BigDecimal checkPrice = expectedPrice.subtract(discountToApply).setScale(2, RoundingMode.HALF_UP);
        assertThat(aPrice.stripTrailingZeros().equals(checkPrice.stripTrailingZeros()))
                .withFailMessage("The expected base price for purchased seat " + checkPrice + " does not match the current base price in the basket " + aPrice)
                .isTrue();
    }

    private String getSeatNumberForPassenger(String passengerCode) throws EasyjetCompromisedException {
        final BasketsResponse[] basketsResponse = new BasketsResponse[1];
        basketsResponse[0] = getBasketAfterAddingPurchasedSeat();

        try {
            pollingLoop().until(() -> {
                basketsResponse[0] = getBasketResponse(getBasketCode());

                return Objects.nonNull(basketsResponse[0].getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null).getSeat());
            });
        } catch (ConditionTimeoutException e) {
            throw new EasyjetCompromisedException("No seat are found for passenger " + passengerCode);
        }

        return basketsResponse[0].getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null).getSeat().getSeatNumber();
    }

    public Basket verifySeatHasBeenUpdate(String passengerCode, boolean added) throws EasyjetCompromisedException {
        final BasketsResponse[] basketsResponse = new BasketsResponse[1];
        basketsResponse[0] = getBasketAfterAddingPurchasedSeat();

        try {
            pollingLoop().until(() -> {
                basketsResponse[0] = getBasketResponse(getBasketCode());

                if (added) {
                    return Objects.nonNull(basketsResponse[0].getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null).getSeat());
                } else {
                    return Objects.isNull(basketsResponse[0].getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null).getSeat());
                }
            });
        } catch (ConditionTimeoutException e) {
            throw new EasyjetCompromisedException("No seat are found for passenger " + passengerCode);
        }

        return basketsResponse[0].getBasket();
    }

    public List<String> getAvailableSeat() {
        getMapPurchasedSeat(null);
        return testData.getSeatingServiceHelper().getResponse().getAvailability().getAvailable().getSeats().stream().collect(Collectors.toList());
    }

    public String getSeatForPassenger(String passengerCode) {
        return addPurchasedSeatRequestBody.getPassengerAndSeats().stream().filter(f -> f.getPassengerId().equalsIgnoreCase(passengerCode)).findFirst().orElse(null).getSeat().getSeatNumber();
    }

    public void invokeChangePurchasedSeatService(String amendableBasketCode) throws EasyjetCompromisedException {
        purchasedSeatPathParams.setBasketId(amendableBasketCode);
        purchasedSeatService = serviceFactory.managePurchasedSeat(new PurchasedSeatRequest(HybrisHeaders.getValid(testData.getChannel()).build(),
                purchasedSeatPathParams,
                passengerSeatChangeRequestBody));
        purchasedSeatService.invoke();
        String firstPassengerCode = passengerSeatChangeRequestBody.getPassengerSeatChangeRequests().get(0).getPassengerOnFlightId();
        String seatForPassenger = passengerSeatChangeRequestBody.getPassengerSeatChangeRequests().get(0).getSeat().getSeatNumber();
        initPriceChangePurchasedSeat(firstPassengerCode, seatForPassenger, amendableBasketCode);
    }

    public void addAndRemoveSeat(String passengerMix, String fareType, SEATPRODUCTS aSeatProduct) throws Throwable {
        testData.setPassengerMix(passengerMix);
        testData.setSeatProductInBasket(aSeatProduct);
        basketHelper.myBasketContainsAFlightWithPassengerMix(passengerMix, testData.getChannel(), fareType, false);
        preRequirementsForRemovePurchasedSeatMoreThanOnePassenger(ADULT, aSeatProduct);
        invokeRemovePurchasedSeatService();
        testData.setData(BASKET_SERVICE, basketHelper.getBasketService());
    }

    public void addStandardSeatAndAdditionalFare(SEATPRODUCTS aSeatProduct, String noOfPassengers) {
        testData.setVerifySeatAllocation(true);
        testData.setPassengerMix(testData.getPassengerMix());

        getMapPurchasedSeat(null);

        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());
        Basket basket = basketHelper.getBasketService().getResponse().getBasket();
        basket.getOutbounds().forEach(journey -> {
            for (Basket.Flight flight : journey.getFlights()) {
                List<Basket.Passenger> passengers = flight.getPassengers().stream()
                        .filter(passenger -> !"INFANTONLAP".equalsIgnoreCase(passenger.getFareProduct().getCode()))
                        .collect(Collectors.toList());
                try {
                    if (noOfPassengers.contains("1")) {
                        addPurchasedSeat(PurchasedSeatRequestBodyFactory.aMultiPassengerAddPurchasedSeat(flight.getFlightKey(), passengers, getAvailableSeats(aSeatProduct)));

                    } else {
                        addPurchasedSeat(PurchasedSeatRequestBodyFactory.anAddPurchasedSeatWithAdditionalSeat(flight.getFlightKey(), passengers, getAvailableSeats(aSeatProduct), 1));
                    }
                } catch (EasyjetCompromisedException e) {
                    new EasyjetCompromisedException(e.getMessage());
                }
            }
        });
    }

    private void addFlightBeforeAddSeat(String passengerMix, String fareType, SEATPRODUCTS aSeatProduct) throws Throwable {
        testData.setPassengerMix(passengerMix);
        testData.setSeatProductInBasket(aSeatProduct);

        FlightsService flightsService = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate());
        testData.setData(GET_FLIGHT_SERVICE, flightsService);
        testData.setFlightKey(flightsService.getOutboundFlight().getFlightKey());
        basketHelper.addFlightsToBasket(fareType, OUTBOUND);
        testData.setSeatProductInBasket(aSeatProduct);
    }

    public void addSeatWithAdditionalSeatAlreadyAllocated(String passengerMix, String fareType, SEATPRODUCTS aSeatProduct, Integer additionalSeat) throws Throwable {
        addFlightBeforeAddSeat(passengerMix, fareType, aSeatProduct);
        addPurchasedSeatToBasketWithAdditionalSeatAlreadyAllocated(aSeatProduct, additionalSeat);
    }

    public void addSeatWithAdditionalSeat(String passengerMix, String fareType, SEATPRODUCTS aSeatProduct, Integer additionalSeat) throws Throwable {
        addFlightBeforeAddSeat(passengerMix, fareType, aSeatProduct);
        addPurchasedSeatToBasketWithAdditionalSeat(aSeatProduct, additionalSeat);
        Scenario scenario = testData.getData(SCENARIO);
        if (!scenario.getSourceTagNames().contains(NEGATIVE_SCENARIO)) {
            purchasedSeatService.getResponse();
        }
        basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        testData.setData(BASKET_SERVICE, basketHelper.getBasketService());
        testData.setData(SerenityFacade.DataKeys.BASKET_ID, basketHelper.getBasketService().getResponse().getBasket().getCode());
    }

    public void addSeatWithoutAdditionalSeat(String passengerMix, String fareType, SEATPRODUCTS aSeatProduct) throws Throwable {
        addFlightBeforeAddSeat(passengerMix, fareType, aSeatProduct);
        addPurchasedSeatToBasketJustOnePassenger(aSeatProduct);
        testData.setData(BASKET_SERVICE, basketHelper.getBasketService());
    }

    public void changeSeatWithAdditionalSeat(String passengerMix, SEATPRODUCTS aSeatProduct, Integer additionalSeat) throws Throwable {
        testData.setPassengerMix(passengerMix);
        changePurchasedSeatToBasketWithAdditionalSeat(aSeatProduct, additionalSeat);
        purchasedSeatService.getResponse();
        basketHelper.getBasket(testData.getAmendableBasket(), testData.getChannel());
        testData.setData(BASKET_SERVICE, basketHelper.getBasketService());
    }

    /**
     * The method returns the list of available consecutive or adjacent or continuous seats
     *
     * @param availableSeats list of available seats
     * @param numberOfSeats  number of consecutive or adjacent or continuous seats required
     * @return List of available consecutive or adjacent or continuous seats
     * @throws EasyjetCompromisedException
     */

    private List<Seat> checkAdditionalSeatAreAvailable(List<Seat> availableSeats, Integer numberOfSeats) throws EasyjetCompromisedException {

        List<String> seatNumbers = new ArrayList<>();
        availableSeats.forEach(seat -> seatNumbers.add(seat.getSeatNumber()));

        String[] seats = seatNumbers.toArray(new String[seatNumbers.size()]);
        int tempNSeat = numberOfSeats + 1;
        String[] continuousSeats = new String[tempNSeat];
        int indexContinuous = 0;

        for (String seat : seats) {

            if (indexContinuous == 0) {
                continuousSeats[indexContinuous++] = seat;
            } else if (continuous(continuousSeats[indexContinuous - 1], seat)) {
                continuousSeats[indexContinuous++] = seat;
                if (indexContinuous == continuousSeats.length) {
                    break;
                }
            } else {
                indexContinuous = 0;
                continuousSeats[indexContinuous++] = seat;
            }
        }

        List<Seat> resSeats = new ArrayList<>();

        for (Seat seat : availableSeats) {
            for (String numSeat : continuousSeats) {
                if (seat.getSeatNumber().equals(numSeat)) {
                    resSeats.add(seat);
                }
            }
            if (resSeats.size() == tempNSeat) {
                break;
            }
        }

        if (resSeats.size() != tempNSeat) {
            throw new EasyjetCompromisedException("There are no " + numberOfSeats + " consecutive seat from seat map for the desired type " + availableSeats.get(0).getType());
        }

        return resSeats;
    }

    private static boolean continuous(String oldSeat, String currentSeat) {
        String[] oldSeatPos = oldSeat.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        String[] currentSeatPos = currentSeat.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        int letterOldSeat = (int) oldSeatPos[1].charAt(0);
        int letterSeat = (int) currentSeatPos[1].charAt(0);
        boolean theSeatIsSubsequent = false;

        if (checkSeatPositionValid((int) 'A', (int) 'C', letterOldSeat, letterSeat) || checkSeatPositionValid((int) 'D', (int) 'F', letterOldSeat, letterSeat)) {
            return false;
        }

        if (oldSeatPos[0].equals(currentSeatPos[0]) && letterOldSeat + 1 == letterSeat) {
            theSeatIsSubsequent = true;
        }
        return theSeatIsSubsequent;
    }

    private static boolean checkSeatPositionValid(int low, int high, int old, int current) {
        return old >= low && old <= high && (current > high || current < low);
    }

    public void addSeatAndUpadatePassenger(String correctSeat, String passengerMix, String fareType, SEATPRODUCTS aSeatProduct) throws Throwable {
        basketHelper.myBasketContainsAFlightWithPassengerMix(passengerMix, testData.getChannel(), fareType, false);
        testData.setSeatProductInBasket(aSeatProduct);

        if (CORRECT_SEAT.equalsIgnoreCase(correctSeat)) {

            addPurchasedSeatForAllPassenger(aSeatProduct);
            createRequestBodyForUpdatePassenger();

        } else {

            addPurchasedSeatEmergencyExitToBasket(SEATPRODUCTS.EXTRA_LEGROOM);
            createRequestBodyForUpdatePassenger();
        }
        final int[] attempt = {3};
        try {
            pollingLoop().until(
                    () -> {
                        Basket basket = basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
                        List<Basket.Flights> flights = basket.getOutbounds();
                        attempt[0]--;
                        return flights.stream()
                                .map(Basket.Flights::getFlights)
                                .flatMap(Collection::stream)
                                .map(Basket.Flight::getPassengers)
                                .flatMap(Collection::stream)
                                .anyMatch(passengers -> "WCHC".equalsIgnoreCase(passengers.getSpecialRequests().getSsrs().get(0).getCode()))
                                && attempt[0] > 0;
                    }
            );
        } catch (ConditionTimeoutException ignored) {
            fail("The SSR is empty in the basket");
        }

        testData.setData(BASKET_SERVICE, basketHelper.getBasketService());
    }

    public void updatePassengerAndAddSeat(String correctSeat, String passengerMix, String fareType, SEATPRODUCTS aSeatProduct) throws Throwable {
        basketHelper.myBasketContainsAFlightWithPassengerMix(passengerMix, testData.getChannel(), fareType, false);
        testData.setSeatProductInBasket(aSeatProduct);

        if (CORRECT_SEAT.equalsIgnoreCase(correctSeat)) {

            createRequestBodyForUpdatePassenger();
            addPurchasedSeatForFirstPassengerType(aSeatProduct, ADULT);

        } else {
            createRequestBodyForUpdatePassenger();
            addPurchasedSeatEmergencyExitToBasket(SEATPRODUCTS.EXTRA_LEGROOM);
        }
        basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        testData.setData(BASKET_SERVICE, basketHelper.getBasketService());
    }

    public void upadatePassengerAndAddSeatWithCommmitBooking(String correctSeat, String passengerMix, String fareType, SEATPRODUCTS aSeatProduct) throws Throwable {
        basketHelper.myBasketContainsAFlightWithPassengerMix(passengerMix, testData.getChannel(), fareType, false);
        testData.setSeatProductInBasket(aSeatProduct);

        if (CORRECT_SEAT.equalsIgnoreCase(correctSeat)) {
            addPurchasedSeatForFirstPassengerType(aSeatProduct, ADULT);
            createRequestBodyForUpdatePassenger();
        } else {

            addPurchasedSeatEmergencyExitToBasket(SEATPRODUCTS.EXTRA_LEGROOM);
            createRequestBodyForUpdatePassenger();
        }
    }

    private void createRequestBodyForUpdatePassenger() {

        Passengers updatePassengersRequestBody = travellerHelper.createValidRequestToAddPassengersForBasket(basketHelper.getBasketService().getResponse());

        List<SavedSSRs.Ssr> mySsrList = new ArrayList<>();
        SavedSSRs.Ssr ssrToAdd = new SavedSSRs.Ssr();
        ssrToAdd.setCode("WCHC");
        ssrToAdd.setIsTandCsAccepted(false);
        mySsrList.add(ssrToAdd);
        SpecialRequest addSSr = SpecialRequest.builder().build();
        addSSr.setSsrs(mySsrList);

        updatePassengersRequestBody.getPassengers().get(0).setSpecialRequests(addSSr);

        basketHelper.updatePassengersForChannel(updatePassengersRequestBody, testData.getChannel(), basketHelper.getBasketService().getResponse()
                .getBasket()
                .getCode());

        testData.setData("updatePassengerRequestBody", updatePassengersRequestBody);
    }

    public void addSeatWithAssociateInfant(String passengerMix, SEATPRODUCTS aSeatProduct, String fareType) throws Throwable {
        addFlightBeforeAddSeat(passengerMix, fareType, aSeatProduct);
        addPurchasedSeatToBasketForEachPassengerAndFlight(aSeatProduct, false);
        testData.setData(BASKET_SERVICE, basketHelper.getBasketService());

        String basketId = basketHelper.getBasketService().getResponse().getBasket().getCode();
        basketHelper.getBasket(basketId, testData.getChannel());
        BasketsResponse basketFlights = basketHelper.getBasketService().getResponse();

        List<Basket.Passenger> passengers = basketFlights.getBasket().getOutbounds().stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        List<Basket.Passenger> passengerIdOutbound = passengers.stream()
                .filter(passenger -> Objects.nonNull(passenger.getInfantsOnLap()) && new ArrayList<>(passenger.getInfantsOnLap()).isEmpty())
                .collect(Collectors.toList());


        String passengerId = passengerIdOutbound.stream()
                .filter(passenger -> Objects.nonNull(passenger.getInfantsOnLap()) && new ArrayList<>(passenger.getInfantsOnLap()).isEmpty())
                .filter(
                        passenger -> ADULT.equalsIgnoreCase(passenger.getPassengerDetails().getPassengerType())
                ).findFirst().orElse(null).getCode();
        testData.setData("passengerId", passengerId);

        String passengerIdInfantOnLap = passengers.stream()
                .map(Basket.Passenger::getInfantsOnLap)
                .flatMap(Collection::stream)
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("The passenger infant doesn't exist"));
        testData.setData("passengerIdInfant", passengerIdInfantOnLap);

        testData.setData("passengerOld", passengers.stream()
                .filter(
                        passenger -> "adult".equalsIgnoreCase(passenger.getPassengerDetails().getPassengerType()) && passenger.getInfantsOnLap().contains(passengerIdInfantOnLap)
                ).findFirst().orElse(null).getCode());

        BasketPathParams pathParams = BasketPathParams.builder().basketId(basketId).path(ASSOCIATE_INFANT).passengerId(passengerId).build();
        AssociateInfantRequestBody associateInfantRequestBody = AssociateInfantRequestBody.builder().infantPassengerId(passengerIdInfantOnLap).build();
        AssociateInfantService associateInfantService = serviceFactory.associateInfant(new AssociateInfantRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, associateInfantRequestBody));
        testData.setData(SERVICE, associateInfantService);
        associateInfantService.invoke();
        associateInfantService.getResponse();
    }

    public void addSeat(String passengerMix, SEATPRODUCTS aSeatProduct, String fareType, Integer additionalSeat) throws Throwable {
        addFlightBeforeAddSeat(passengerMix, fareType, aSeatProduct);
        addPurchasedSeatToBasketWithAdditionalSeat(aSeatProduct, additionalSeat);
        testData.setData(BASKET_SERVICE, basketHelper.getBasketService());
    }

    public void prepareForRemovePurchasedSeat(String paxType) throws Throwable {
        passengerCode = "";
        basketAfterAddingPurchasedSeat = getBasketResponse(getBasketCode());
        initPriceBeforeRemovingSeat(basketAfterAddingPurchasedSeat);

        initRequestRemovePurchasedSeat(paxType);
    }

    public BasketsResponse createABasketWithPassengerMixAndPurchasedSeat(String passengerMix, String channel, PurchasedSeatHelper.SEATPRODUCTS seatproducts, boolean emergencyExit) throws Throwable {
        if (emergencyExit) {
            pollingLoop().untilAsserted(
                    () -> {
                        basketHelper.myBasketContainsAFlightWithPassengerMix(passengerMix);

                        BasketService basketService = testData.getData(BASKET_SERVICE);
                        Basket basket = basketService.getResponse().getBasket();
                        testData.setData(SerenityFacade.DataKeys.BASKET_ID, basket.getCode());

                        assertThat(addAvailableEmergencyExitPurchasedSeatToBasketForEachPassengerAndFlight().getOperationConfirmation().getBasketCode().equalsIgnoreCase(testData.getData(SerenityFacade.DataKeys.BASKET_ID))).withFailMessage("Couldn't find Emergency Exit seat on this flight even after retries").isTrue();
                    });
        } else {
            basketHelper.myBasketContainsAFlightWithPassengerMix(passengerMix);
            addPurchasedSeatToBasketForEachPassengerAndFlight(seatproducts, false);
        }

        BasketsResponse basketsResponse = basketHelper.getBasketService().getResponse();
        basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToAddAllPassengersForBasket(basketsResponse), channel, basketsResponse.getBasket().getCode());
        basketHelper.getBasket(basketsResponse.getBasket().getCode(), channel);
        basketsResponse = basketHelper.getBasketService().getResponse();
        return basketsResponse;
    }

    public void addPurchasedSeatwithPrimarySeatOnly(String passengerMix, SEATPRODUCTS aSeatProduct, String fareType) throws Throwable {
        addFlightBeforeAddSeat(passengerMix, fareType, aSeatProduct);
        addPurchasedSeatToBasketWithoutAdditionalSeat(aSeatProduct);
    }

    public void addPurchasedSeatwithAdditionalSeatOnly(String passengerMix, SEATPRODUCTS aSeatProduct, String fareType) throws Throwable {
        addFlightBeforeAddSeat(passengerMix, fareType, aSeatProduct);
        addPurchasedSeatToBasketWithoutPrimarySeat(aSeatProduct);
    }

    public void addPurchasedSeatwithAdditionalSeatOnly(String passengerMix, SEATPRODUCTS aSeatProduct, String fareType, Integer additionalSeat) throws Throwable {
        addFlightBeforeAddSeat(passengerMix, fareType, aSeatProduct);
        addPurchasedSeatToBasketWithoutPrimarySeat(aSeatProduct, additionalSeat);
    }

    public void getSeatMap(String flightKey) {

        SeatMapPathParams pathParams = SeatMapPathParams.builder()
                    .flightId(Objects.nonNull(flightKey) ? flightKey : getFirstOutboundFlightKeyFromBasket())
                    .path(GET_SEAT_MAP)
                    .build();

        SeatMapQueryParams queryParams = SeatMapQueryParams.builder()
                .basketId(getBasketCode())
                .build();
        if (CommonConstants.PUBLIC_API_B2B_CHANNEL.equalsIgnoreCase(testData.getChannel())) {
            seatMapService = serviceFactory.getSeatMapService(new GetSeatMapRequest(HybrisHeaders.getValid(CommonConstants.DIGITAL_CHANNEL).build(), pathParams, queryParams));
        } else {
            seatMapService = serviceFactory.getSeatMapService(new GetSeatMapRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, queryParams));
        }
        seatMapService.invoke();
    }
}
