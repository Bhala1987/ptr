package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.SeatMapPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.SeatMapQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.AddFlightRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.Passenger;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.purchasedseats.Seat;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.PurchasedSeatRequestBodyFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.BasketRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.GetSeatMapRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.PurchasedSeatRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.GetSeatMapResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.services.GetSeatMapService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.PurchasedSeatService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.IS_ALREADY_ALLOCATED_SEAT;
import static com.hybris.easyjet.config.constants.CommonConstants.*;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.ADD_PURCHASED_SEAT;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.SeatMapPathParams.FlightPaths.GET_SEAT_MAP;


/**
 * Created by giuseppecioce on 24/08/2017.
 *
 * The following class has been created in order to set the precondition to the test that need to manage seat.
 * Appropriate method garanties that for the success scenarios, the flight used in the test has enough seat for the passenger mix.
 * Is it possible set up the precondition for test required validation for seat already allocated, in this case will be choose a fligth
 * where the number of total seat (extra_legeroom & upfront & stadard) is more than available seats.
 */
class AlreadyAllocationSeatHelper {

    private static final Logger LOG = LogManager.getLogger(AddFlightRequestBody.class);

    private final HybrisServiceFactory serviceFactory;
    private BasketHelper basketHelper;
    private SerenityFacade testData;

    AlreadyAllocationSeatHelper(HybrisServiceFactory serviceFactory, BasketHelper basketHelper, SerenityFacade serenityFacade) {
        this.serviceFactory = serviceFactory;
        this.basketHelper = basketHelper;
        this.testData = serenityFacade;
    }
    /**
     * Given a flight key a basket id and a seat product, the method verify the availability for the desired seat.
     * If the aSeatProduct the method will consider all available seat.
     * To enable this feature has been required call the prestep
     *
     * "And I want to proceed with add purchased seat <seat>"
     *
     * @param flight flight
     * @param basketCode basketCode
     * @param aSeatProduct aSeatProduct
     * @return true or false
     */
    boolean verifyAllocationForSeatType(AddFlightRequestBody flight, String basketCode, PurchasedSeatHelper.SEATPRODUCTS aSeatProduct) throws Throwable {
        if (testData.isVerifySeatAllocation()) {
            GetSeatMapResponse seatMapResponse = checkAvailabilityForSeat(flight.getFlights().get(0).getFlightKey(), basketCode); // invoke seat map service
            // check availability for passenger mix
            int numberOfPassenger = new FlightPassengers(testData.getPassengerMix()).getTotalNumberOfPassengers();
            List<String> availableSeatForType = new ArrayList<>();
            // verify availability for desired flight key and purchased seat type
            String finalSeatName = getSeatName(aSeatProduct);
            List<String> availability = seatMapResponse.getAvailability().getAvailable().getSeats().stream().collect(Collectors.toList());
            List<String> completeListOfSeat = seatMapResponse.getProducts().stream().filter(Objects.nonNull(aSeatProduct) ? f -> f.getName().equalsIgnoreCase(finalSeatName) : f -> true).flatMap(f -> f.getSeats().stream()).collect(Collectors.toList());

            if("emergency exit".equalsIgnoreCase(finalSeatName)) { // check for emergency exit seat
                seatMapResponse.getMetadata().getIsEmergencyExit().getSeats().forEach((String seat) -> {
                    if (availability.contains(seat)) {
                        availableSeatForType.add(seat);
                    }
                });
            } else { // check for type of seat (1,2or3)
                seatMapResponse.getProducts().stream().filter(Objects.nonNull(aSeatProduct) ? f -> f.getName().equalsIgnoreCase(finalSeatName) : f -> true).forEach((GetSeatMapResponse.Product seat) -> {
                    for (String s : seat.getSeats()) {
                        if (availability.contains(s)) {
                            availableSeatForType.add(s);
                            if(availableSeatForType.size() == numberOfPassenger) { // checked availability for passenger mix -> all fine
                                break;
                            }
                        }
                    }
                });
            }

            boolean result = !availableSeatForType.isEmpty() && availableSeatForType.size() >= numberOfPassenger;
            needAlreadyAllocatedSeat(result && (availability.size() >= completeListOfSeat.size()), flight, getAvailableSeats(aSeatProduct, seatMapResponse));
            return result;
        } else {
            return true;
        }
    }

    /**
     * Return as a list of seat the available seat
     * @param aSeatProduct if not null the list will include only item for the desired type of seat
     * @param seatMapResponse seatMapResponse
     * @return seat list
     * @throws EasyjetCompromisedException exception
     */
    private List<Seat> getAvailableSeats(PurchasedSeatHelper.SEATPRODUCTS aSeatProduct, GetSeatMapResponse seatMapResponse) throws EasyjetCompromisedException {
        List<Seat> seatList = new ArrayList<>();
        List<String> availability = seatMapResponse.getAvailability().getAvailable().getSeats().stream().collect(Collectors.toList());
        final boolean[] noSeat = {false};
        seatMapResponse.getProducts().stream().filter(Objects.nonNull(aSeatProduct) ? f -> f.getName().equalsIgnoreCase(getSeatName(aSeatProduct)) : f -> true).forEach((GetSeatMapResponse.Product seat) -> {
            List<String> seatNum;
            try {
                seatNum = getListAvailableSeatNumberForType(seat.getSeats(), availability);
            } catch (Exception e) {
                LOG.error(e);
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

    /**
     * Return as a string the available seat number
     * @param seatsNumber seatsNumber
     * @param availableSeat availableSeat
     * @return seat number
     * @throws Exception exception
     */
    private static List<String> getListAvailableSeatNumberForType(List<String> seatsNumber, List<String> availableSeat) throws Exception { //NOSONAR
        List<String> seatNum = new ArrayList<>();
        for (String s : seatsNumber) {
            if (availableSeat.contains(s)) {
                seatNum.add(s);
            }
        }

        if (seatNum.isEmpty()) {
            throw new Exception("Seat number is not empty");
        } else {
            return seatNum;
        }
    }

    /**
     * Verify if the test required a validation for seat already allocated.
     * To enable this feature has been required call the prestep
     *
     * "And I want to proceed with add already allocated purchased seat <seat>"
     *
     * @param result result
     * @param flight flight
     * @param available available
     * @throws Throwable exception
     */
    private void needAlreadyAllocatedSeat(boolean result, AddFlightRequestBody flight, List<Seat> available) throws Throwable {
        if(result && (testData.dataExist(IS_ALREADY_ALLOCATED_SEAT) && ((Boolean) testData.getData(IS_ALREADY_ALLOCATED_SEAT)))) {
            addFlightAndAllocateSeat(flight, available);
            basketHelper.attemptToAddFlightToBasket(HybrisHeaders.getValid(testData.getChannel()).build(), flight);
        }
    }

    /**
     * The method add throw acp a seat on the flight
     * @param flight flight
     * @param available available
     */
    private void addFlightAndAllocateSeat(AddFlightRequestBody flight, List<Seat> available) {
        HybrisService.theJSessionCookie.remove();

        AddFlightRequestBody tempFlight = copyRequestBody(flight);
        tempFlight.setPassengers(Arrays.asList(Passenger.builder().passengerType(ADULT).quantity(1).additionalSeats(0).infantOnSeat(false).build()));
        BasketService basketService = serviceFactory.addFlight(new BasketRequest(HybrisHeaders.getValid(AD_CHANNEL).build(), tempFlight));
        basketService.invoke();

        Basket basket = basketService.getResponse().getBasket();
        basket.getOutbounds().forEach(journey -> {
            for (Basket.Flight f : journey.getFlights()) {
                try {
                    BasketPathParams addPurchasedSeatPathParams = BasketPathParams.builder()
                            .basketId(basketService.getResponse().getBasket().getCode())
                            .path(ADD_PURCHASED_SEAT)
                            .build();

                    PurchasedSeatService purchasedSeatService = serviceFactory.managePurchasedSeat(new PurchasedSeatRequest(HybrisHeaders.getValid(AD_CHANNEL).build(),
                            addPurchasedSeatPathParams,
                            PurchasedSeatRequestBodyFactory.aMultiPassengerAddPurchasedSeat(f.getFlightKey(), f.getPassengers().stream().filter(pass -> !INFANT_ON_LAP.equalsIgnoreCase(pass.getFareProduct().getBundleCode())).collect(Collectors.toList()), available)));
                    purchasedSeatService.invoke();
                } catch (EasyjetCompromisedException ej) {
                    LOG.error(ej);
                    ej.printStackTrace();
                }
            }
        });
        HybrisService.theJSessionCookie.remove();
    }

    private static AddFlightRequestBody copyRequestBody(AddFlightRequestBody body) {
        return AddFlightRequestBody.builder()
                .flights(body.getFlights())
                .toeiCode(body.getToeiCode())
                .currency(body.getCurrency())
                .routeCode(body.getRouteCode())
                .fareType(body.getFareType())
                .journeyType(body.getJourneyType())
                .overrideWarning(false)
                .passengers(body.getPassengers())
                .routePrice(body.getRoutePrice())
                .bookingType(body.getBookingType())
                .build();
    }

    /**
     * invoke get seat map if the check on availability seat is required
     * @param flightKey flightKey
     * @param basketCode  basketCode
     * @return seat map response
     */
    private GetSeatMapResponse checkAvailabilityForSeat(String flightKey, String basketCode) {
        SeatMapPathParams pathParams = SeatMapPathParams.builder()
                .flightId(flightKey)
                .path(GET_SEAT_MAP)
                .build();

        SeatMapQueryParams queryParams = SeatMapQueryParams.builder()
                .basketId(basketCode)
                .build();

        GetSeatMapService seatMapService;
        if ("PublicApiB2B".equalsIgnoreCase(testData.getChannel())) {
            seatMapService = serviceFactory.getSeatMapService(new GetSeatMapRequest(HybrisHeaders.getValid("Digital").build(), pathParams, queryParams));
        } else {
            seatMapService = serviceFactory.getSeatMapService(new GetSeatMapRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, queryParams));
        }
        seatMapService.invoke();

        return seatMapService.getResponse();
    }

    /**
     * Get seat name from enum type
     * @param aSeatProduct a seat product
     * @return seatName
     */
    private String getSeatName(PurchasedSeatHelper.SEATPRODUCTS aSeatProduct) {
        String seatName;
        switch (aSeatProduct) {
            case EXTRA_LEGROOM:
                seatName = "extra legroom";
                break;
            case STANDARD:
                seatName = STANDARD;
                break;
            case UPFRONT:
                seatName = "up front";
                break;
            case EMERGENCY_EXIT:
                seatName = "emergency exit";
                break;
            case GENERIC:
            default:
                seatName = "generic";
                break;
        }
        return seatName;
    }
}
