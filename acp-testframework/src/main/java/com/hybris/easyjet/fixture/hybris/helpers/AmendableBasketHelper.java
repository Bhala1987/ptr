package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.database.hybris.dao.CartDao;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.AddInfantOnLapRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.GetAmendableBookingRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.AddAdditionalFareToPassengerInBasketRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.AddInfantOnLapFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AddInfantOnLapRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ManageAdditionalFareToPassengerInBasketRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.GetAmendableBookingRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.AddInfantOnLapService;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.ManageAdditionalFareToPassengerInBasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.GetAmendableBookingService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.MANAGE_ADDITIONAL_SEAT_TO_PASSENGER;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams.BookingPaths.AMENDABLE_BOOKING_REQUEST;
import static com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.CreateAmendableBasketBodyFactory.createABodyForBookingLevelAmendableBasket;
import static com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.CreateAmendableBasketBodyFactory.createABodyForPassengerLevelAmendableBasket;
import static java.lang.Boolean.FALSE;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by vijayapalkayyam on 27/06/2017.
 */
@Component
public class AmendableBasketHelper {
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private SerenityFacade testData;
    @Getter
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private BasketHoldItemsHelper basketHoldItemsHelper;
    @Autowired
    private CartDao cartDao;
    private BasketPathParams basketPathParams;
    private AddInfantOnLapService addInfantOnLapService;
    private AddInfantOnLapRequestBody addInfantOnLapRequestBody;
    private List<String> adultsWithNoInfantOnLapPassengerCodes = new ArrayList<>();
    private List<String> adultCodesWhoHasInFantsOnLapAlready = new ArrayList<>();
    private List<String> childPassengerCodes = new ArrayList<>();
    private List<String> infantPassengerCodes = new ArrayList<>();
    private List<String> requestingPassengersWithTheirAssociates;
    private ArrayList<String> passengersWhoAreNotLocked;
    private ArrayList<String> passengersWhoAreLocked;
    private GetBookingResponse.Booking booking;
    private List<String> allPassengersInTheBooking;
    private GetAmendableBookingService getAmendableBookingService;
    private String passengerCodeForWhichHoldItemAdding;
    private String flightKeyForWhichHoldItemAdding;
    private List<String> outboundAdultPassengers;
    private List<String> outboundChildPassengers;
    private List<String> outboundInfantOnLapPassengers;
    private List<String> inboundAdultPassengers;
    private List<String> inboundChildPassengers;
    private List<String> inboundInfantOnLapPassengers;
    private Map<GetBookingResponse.Passenger, List<String>> outboundPassengersWithTheirAssociations;
    private List<String> passengersRequesting;
    private String firstAmendableBasketForTheBooking;
    private ManageAdditionalFareToPassengerInBasketService manageAdditionalFareToPassengerInBasketService;

    private static final String ADULT = "adult";
    private static final String CHILD = "child";
    private static final String INVALID = "invalid";


    private void createAmendableBasketFor(BookingPathParams params, GetAmendableBookingRequestBody body) {

        getAmendableBookingService = serviceFactory.getAmendableBooking(
                new GetAmendableBookingRequest(
                        HybrisHeaders.getValid(testData.getChannel()).build(),
                        params,
                        body
                )
        );
        getAmendableBookingService.invoke();
        if (getAmendableBookingService.getStatusCode() == 200) {
            String newAmendableBasketCreated = getAmendableBookingService.getResponse()
                    .getOperationConfirmation()
                    .getBasketCode();
            testData.setAmendableBasket(newAmendableBasketCreated);
            testData.setData(SerenityFacade.DataKeys.BASKET_ID, newAmendableBasketCreated);
        }
    }

    private BookingPathParams getParamsUsingBooking(String bookingRef) {
        return BookingPathParams.builder()
                .bookingId(bookingRef)
                .path(AMENDABLE_BOOKING_REQUEST)
                .build();
    }

    public AddInfantOnLapService getAddInfantOnLapService() {
        return addInfantOnLapService;
    }

    private void invokeAddInfantOnLap(BasketPathParams params, AddInfantOnLapRequestBody requestBody) {
        addInfantOnLapService = serviceFactory
                .addInfantOnLap(new AddInfantOnLapRequest(HybrisHeaders.getValid(testData.getChannel()).build(), params, requestBody));
        addInfantOnLapService.invoke();
    }

    public AmendableBasketHelper generateParamsToAddInfantOnLapForBasket(String basketId) {
        basketPathParams = BasketPathParams.builder()
                .basketId(basketId)
                .passengerId("valid")
                .path(BasketPathParams.BasketPaths.ADD_INFANT_ON_LAP)
                .build();
        return this;
    }

    private void findPassengerCodesFor(String basketId) {
        Basket basket = basketHelper.getBasket(basketId, testData.getChannel());
        adultsWithNoInfantOnLapPassengerCodes.clear();
        adultCodesWhoHasInFantsOnLapAlready.clear();
        childPassengerCodes.clear();
        infantPassengerCodes.clear();
        basket.getOutbounds().stream().flatMap(bound -> bound.getFlights().stream()).flatMap(flight -> flight.getPassengers().stream()).forEach(
                passenger -> {
                    switch (passenger.getPassengerDetails().getPassengerType().toLowerCase()) {
                        case ADULT:
                            if (passenger.getInfantsOnLap().isEmpty()) {
                                adultsWithNoInfantOnLapPassengerCodes.add(passenger.getCode());
                            } else {
                                adultCodesWhoHasInFantsOnLapAlready.add(passenger.getCode());
                            }
                            break;
                        case CHILD:
                            childPassengerCodes.add(passenger.getCode());
                            break;
                        case "infant":
                            infantPassengerCodes.add(passenger.getCode());
                            break;
                        default:
                            break;
                    }
                });
    }

    public AmendableBasketHelper generateParamsToAddInfantOnLapBasedOnPassenger(String passenger) {
        String basketId;
        if (testData.getAmendableBasket() != null) {
            basketId = testData.getAmendableBasket();
        } else {
            basketId = testData.getData(SerenityFacade.DataKeys.BASKET_ID);
        }

        String passengerId = passenger;
        if (!passengerId.equalsIgnoreCase(INVALID)) {
            findPassengerCodesFor(basketId);
            switch (passengerId) {
                case "validAdult":
                case "valid":
                    passengerId = adultsWithNoInfantOnLapPassengerCodes.get(0);
                    break;
                case "adultWithInfantOnLapAlready":
                    passengerId = adultCodesWhoHasInFantsOnLapAlready.get(0);
                    break;
                case CHILD:
                    passengerId = childPassengerCodes.get(0);
                    break;
                case "infant":
                    passengerId = infantPassengerCodes.get(0);
                    break;
                default:
                    break;
            }
        }
        basketPathParams = BasketPathParams.builder()
                .basketId(basketId)
                .passengerId(passengerId)
                .path(BasketPathParams.BasketPaths.ADD_INFANT_ON_LAP)
                .build();
        return this;
    }

    public AmendableBasketHelper generateRequestBodyToAddInfantOnLapWithMissingData(String missingField) {
        addInfantOnLapRequestBody = AddInfantOnLapFactory.getAddInfantOnLapBodyWithMissing(missingField);
        return this;
    }

    public void invokeAddInfantOnLap() {
        invokeAddInfantOnLap(basketPathParams, addInfantOnLapRequestBody);
    }

    public AmendableBasketHelper generateValidParamsToAddInfantOnLap() {
        return generateParamsToAddInfantOnLapBasedOnPassenger("validAdult");
    }

    public AmendableBasketHelper generateValidRequestBodyToAddInfantOnLap() {
        addInfantOnLapRequestBody = AddInfantOnLapFactory.getAddInfantOnLapBody();
        return this;
    }

    public void createAmendableBasketForInvalidBookingReference() {
        createAmendableBasketFor(getParamsUsingBooking("INVALIDBOOKING_0000"), createABodyForBookingLevelAmendableBasket(FALSE));
    }

    public GetAmendableBookingService getAmendableBasket() {
        BookingPathParams.BookingPathParamsBuilder bookingPathParams = BookingPathParams.builder()
                .bookingId(testData.getData(SerenityFacade.DataKeys.BOOKING_ID))
                .path(AMENDABLE_BOOKING_REQUEST);

        GetAmendableBookingRequestBody.GetAmendableBookingRequestBodyBuilder getAmendableBookingRequestBody = GetAmendableBookingRequestBody.builder().overrideLocking(Boolean.TRUE);

        getAmendableBookingService = serviceFactory.getAmendableBooking(new GetAmendableBookingRequest(HybrisHeaders.getValid(testData.getChannel()).build(), bookingPathParams.build(), getAmendableBookingRequestBody.build()));
        getAmendableBookingService.invoke();

        testData.setData(SerenityFacade.DataKeys.BASKET_ID, getAmendableBookingService.getResponse().getOperationConfirmation().getBasketCode());

        return getAmendableBookingService;
    }


    private void createAmendableBasketFor(GetAmendableBookingRequestBody body) {
        createAmendableBasketFor(getParamsUsingBooking(testData.getData(SerenityFacade.DataKeys.BOOKING_ID)), body);
    }

    public void createAmendableBasketFor(String passengerOrBooking) throws EasyjetCompromisedException {
        createAmendableBasketFor(passengerOrBooking, true, false);
    }

    public void createAmendableBasketFor(String passengerToRequest, boolean overrideFlag, boolean isItASecondRequestForSameBooking) throws EasyjetCompromisedException {
        if (!isItASecondRequestForSameBooking) {
            readPassengersFromBooking();
        }
        switch (passengerToRequest.toLowerCase()) {
            case "booking":
            case "whole booking":
                createAmendableBasketFor(createABodyForBookingLevelAmendableBasket(overrideFlag));
                break;
            case "any outbound adult passenger":
                passengersRequesting = Arrays.asList(outboundAdultPassengers.get(new Random().nextInt(outboundAdultPassengers.size())));
                break;
            case "any outbound child passenger":
                passengersRequesting = Arrays.asList(outboundChildPassengers.get(new Random().nextInt(outboundChildPassengers.size())));
                break;
            case "any outbound infantonlap passenger":
                passengersRequesting = Arrays.asList(outboundInfantOnLapPassengers.get(new Random().nextInt(outboundInfantOnLapPassengers.size())));
                break;
            case "different outbound adult passenger":
                passengersRequesting = Arrays.asList(outboundAdultPassengers.stream().filter(pas -> !passengersWhoAreLocked.contains(pas)).findFirst()
                        .orElseThrow(() -> new EasyjetCompromisedException("could not find different adult")));
                break;
            case "any inbound adult passenger":
                passengersRequesting = Arrays.asList(inboundAdultPassengers.get(new Random().nextInt(inboundAdultPassengers.size())));
                break;
            case "different inbound adult passenger":
                passengersRequesting = Arrays.asList(inboundAdultPassengers.stream().filter(pas -> !passengersWhoAreLocked.contains(pas)).findFirst()
                        .orElseThrow(() -> new EasyjetCompromisedException("could not find different adult")));
                break;
            case "any inbound child passenger":
                passengersRequesting = Arrays.asList(inboundChildPassengers.get(new Random().nextInt(inboundChildPassengers.size())));
                break;
            case "any inbound infantonlap passenger":
                passengersRequesting = Arrays.asList(inboundInfantOnLapPassengers.get(new Random().nextInt(inboundInfantOnLapPassengers.size())));
                break;
            case "any two outbound adult passengers":
                if (outboundAdultPassengers.size() < 2) {
                    throw new EasyjetCompromisedException("At least two adult passengers required to continue with this test.");
                }
                passengersRequesting = pickNRandomPassengers(outboundAdultPassengers, 2);
                break;
            case "corresponding inbound adult passenger":
                passengersRequesting =
                        outboundPassengersWithTheirAssociations
                                .entrySet()
                                .stream()
                                .filter(entry -> passengersRequesting.contains(entry.getKey().getCode()))
                                .flatMap(entry -> entry.getValue().stream())
                                .filter(pax -> inboundAdultPassengers.contains(pax))
                                .collect(toList());
                passengersRequesting = Arrays.asList(passengersRequesting.get(new Random().nextInt(passengersRequesting.size())));
                break;
            case "same outbound adult passenger":
            case "same outbound child passenger":
            case "same outbound InfantOnLap passenger":
                passengersRequesting = Arrays.asList(passengersRequesting.get(new Random().nextInt(passengersRequesting.size())));
                break;
            default:
                passengersRequesting = getAnyOnePassengerAsList(outboundPassengersWithTheirAssociations);
                break;
        }
        updateLockingOfPassengers(passengerToRequest, isItASecondRequestForSameBooking);
        if (!passengerToRequest.toLowerCase().contains("booking")) {
            createAmendableBasketFor(createABodyForPassengerLevelAmendableBasket(passengersRequesting));
        }
        if (!isItASecondRequestForSameBooking) {
            firstAmendableBasketForTheBooking = testData.getAmendableBasket();
        }
    }

    private static List<String> pickNRandomPassengers(List<String> lst, int n) {
        List<String> copy = new LinkedList<>(lst);
        Collections.shuffle(copy);
        return copy.subList(0, n);
    }

    private void updateLockingOfPassengers(String passengerToRequest, boolean isItASecondRequestForSameBooking) {
        if (passengerToRequest.toLowerCase().contains("booking")) {
            if (!isItASecondRequestForSameBooking) {
                deriveLockedAndNotLockedPassengers(allPassengersInTheBooking);
            }
        } else if (isItASecondRequestForSameBooking) {
            updateLockedNotLockedPassengers(passengersRequesting);
        } else {
            deriveLockedAndNotLockedPassengers(passengersRequesting);
        }
    }

    private void updateLockedNotLockedPassengers(List<String> newPassengerRequesting) {
        passengersWhoAreLocked.addAll(newPassengerRequesting);
        passengersWhoAreLocked.addAll(getPassengersAssociationsAsList(newPassengerRequesting, outboundPassengersWithTheirAssociations));
        passengersWhoAreNotLocked.removeAll(passengersWhoAreLocked);
        passengersWhoAreLocked = removeDuplicates(passengersWhoAreLocked);
        passengersWhoAreNotLocked = removeDuplicates(passengersWhoAreNotLocked);
    }

    public void createAmendableBasketForASpecificPassengerWithoutReadingFromBooking(List<String> passenger) {
        createAmendableBasketFor(createABodyForPassengerLevelAmendableBasket(passenger));
    }

    private void readPassengersFromBooking() {
        GetBookingResponse bookingResponse;
        if (testData.dataExist(SerenityFacade.DataKeys.GET_BOOKING_RESPONSE)) {
            bookingResponse = testData.getData(SerenityFacade.DataKeys.GET_BOOKING_RESPONSE);
        } else {
            bookingResponse = testData.getBookingResponse();
        }
        booking = bookingResponse.getBookingContext().getBooking();
        allPassengersInTheBooking = new ArrayList<>(getAllPassengerCodesFor(booking.getOutbounds()));
        allPassengersInTheBooking.addAll(getAllPassengerCodesFor(booking.getInbounds()));
        outboundPassengersWithTheirAssociations = getPassengersWithTheirAssociations(booking.getOutbounds());

        outboundAdultPassengers = getPassengerAsList(ADULT, booking.getOutbounds());
        outboundChildPassengers = getPassengerAsList(CHILD, booking.getOutbounds());
        outboundInfantOnLapPassengers = getPassengerAsList("lap", booking.getOutbounds());

        inboundAdultPassengers = getPassengerAsList(ADULT, booking.getInbounds());
        inboundChildPassengers = getPassengerAsList(CHILD, booking.getInbounds());
        inboundInfantOnLapPassengers = getPassengerAsList("lap", booking.getInbounds());
    }

    private void deriveLockedAndNotLockedPassengers(List<String> passengersRequestedAmendableBasket) {
        requestingPassengersWithTheirAssociates = new ArrayList<>(getPassengersAssociationsAsList(passengersRequestedAmendableBasket, outboundPassengersWithTheirAssociations));

        passengersWhoAreLocked = new ArrayList<>(passengersRequestedAmendableBasket);
        passengersWhoAreLocked.addAll(requestingPassengersWithTheirAssociates);

        passengersWhoAreNotLocked = new ArrayList<>(allPassengersInTheBooking);
        passengersWhoAreNotLocked.removeAll(passengersWhoAreLocked);

        passengersWhoAreLocked = removeDuplicates(passengersWhoAreLocked);
        passengersWhoAreNotLocked = removeDuplicates(passengersWhoAreNotLocked);
    }

    private ArrayList<String> removeDuplicates(ArrayList<String> passengers) {
        ArrayList<String> pas = new ArrayList<>();
        Set<String> hs = new HashSet<>();
        hs.addAll(passengers);
        pas.addAll(hs);
        return pas;
    }

    private List<String> getAllPassengerCodesFor(List<GetBookingResponse.Flights> bounds) {
        return bounds.stream()
                .flatMap(bound -> bound.getFlights().stream())
                .flatMap(flight -> flight.getPassengers().stream())
                .map(AbstractPassenger::getCode)
                .collect(toList());
    }

    private List<String> getAnyOnePassengerAsList(Map<GetBookingResponse.Passenger, List<String>> passengers) {
        return asList(passengers.entrySet().iterator().next().getKey().getCode());
    }

    private List<String> getPassengerAsList(String paxType, List<GetBookingResponse.Flights> bounds) {
        return bounds
                .stream()
                .flatMap(bound -> bound.getFlights().stream())
                .flatMap(flight -> flight.getPassengers().stream())
                .filter(passenger -> passengerMatchesPaxType(paxType, passenger))
                .map(AbstractPassenger::getCode)
                .collect(toList());
    }

    private boolean passengerMatchesPaxType(String paxType, GetBookingResponse.Passenger passenger) {
        if ("lap".equalsIgnoreCase(paxType)) {
            return containsIgnoreCase(passenger.getFareProduct().getType(), paxType);
        } else {
            return passenger.getPassengerDetails().getPassengerType().equalsIgnoreCase(paxType);
        }
    }

    private List<String> getPassengersAssociationsAsList(List<String> passengersList, Map<GetBookingResponse.Passenger, List<String>> passengersMap) {
        List<String> associatedPassengers = new ArrayList<>();
        for (String pas : passengersList) {
            associatedPassengers.addAll(
                    passengersMap
                            .entrySet()
                            .stream()
                            .filter(entry -> entry.getKey().getCode().equals(pas))
                            .flatMap(entry -> entry.getValue().stream())
                            .collect(toList()));
        }
        return associatedPassengers;
    }

    private LinkedHashMap<GetBookingResponse.Passenger, List<String>> getPassengersWithTheirAssociations(List<GetBookingResponse.Flights> bounds) {
        return bounds.stream()
                .flatMap(bound -> bound.getFlights().stream()) // gets the list of all outbound flights
                .flatMap(flight -> flight.getPassengers().stream()) // gets the list of all passengers
                .collect(Collectors.toMap( // Collects to a linked HashMap, produces Illegal statement exception if there is any duplicate passenger in the list
                        passenger -> passenger,
                        this::getAssociatedPassengers, //gets all the associated passengers
                        (passenger, passengerMap) -> {
                            throw new IllegalStateException(String.format("Duplicate key %s", passenger));
                        },
                        LinkedHashMap::new));
    }

    /**
     * Associated passengers are all passengers who are on lap and same passenger but with different passenger codes on different flights
     * with in the same booking (including any connecting flights or return flights)
     **/

    private List<String> getAssociatedPassengers(GetBookingResponse.Passenger passenger) {
        List<String> passengers = passenger.getPassengerMap();
        passengers.addAll(passenger.getInfantsOnLap());
        return passengers;
    }

    public void getBasketForAmendableBasket(String amendableBasket) {
        basketHelper.getBasket(amendableBasket);
    }

    public void passengerDetailsExistsForAllPassengerIncludingAssociates() {
        basketHelper
                .getBasketService()
                .assertThatForAmandableBasket()
                .passengerDetailsExistsFor(requestingPassengersWithTheirAssociates, booking);
    }

    public void passengerDetailsExistsForAllPassengersInBasket() {
        basketHelper
                .getBasketService()
                .assertThatForAmandableBasket()
                .passengerDetailsExistsFor(allPassengersInTheBooking, booking);
    }

    public void basketIdIsCreated() {
        getAmendableBookingService.assertThat().basketIsCreated();
    }

    public void basketHasLinkedFlights() {
        basketHelper.getBasket(testData.getAmendableBasket());
        Basket basket = basketHelper.getBasketService().getResponse().getBasket();
        getAmendableBookingService.assertThat().basketHasLinkedFlights(basket);
    }

    public void containedTheCorrectErrorMessage(String error) {
        getAmendableBookingService.assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    public void addHoldItemForPassenger(String productType, String lockedOrNotPassenger) throws Throwable {

        if (containsIgnoreCase(productType, "sport") || containsIgnoreCase(productType, "hold bag")) {
            if ("is not".equalsIgnoreCase(lockedOrNotPassenger)) {
                derivePassengerAndFlightKeyToAddHoldItem(passengersWhoAreNotLocked);
                basketHoldItemsHelper.addHoldItemWithError(productType, flightKeyForWhichHoldItemAdding, passengerCodeForWhichHoldItemAdding, 1, testData.getChannel(), testData.getAmendableBasket());
            } else {
                derivePassengerAndFlightKeyToAddHoldItem(passengersWhoAreLocked);
                basketHoldItemsHelper.addHoldItemToSpecificPassengerSpecificFlight(productType, flightKeyForWhichHoldItemAdding, passengerCodeForWhichHoldItemAdding, testData.getChannel(), testData.getAmendableBasket());
            }
        } else {
            if ("is not".equalsIgnoreCase(lockedOrNotPassenger)) {
                derivePassengerAndFlightKeyToAddHoldItem(passengersWhoAreNotLocked);
                basketHoldItemsHelper.
                        addExcessWeightToSpecificPassengersForSpecificFlightWithError
                                (productType, flightKeyForWhichHoldItemAdding, passengerCodeForWhichHoldItemAdding, 1, false, testData.getAmendableBasket());
            } else {
                derivePassengerAndFlightKeyToAddHoldItem(passengersWhoAreLocked);
                basketHoldItemsHelper
                        .addExcessWeightToSpecificPassengersForSpecificFlight
                                (productType, flightKeyForWhichHoldItemAdding, passengerCodeForWhichHoldItemAdding, 1, 1, false, testData.getAmendableBasket());
            }
        }
    }

    private void derivePassengerAndFlightKeyToAddHoldItem(List<String> passengers) {
        passengerCodeForWhichHoldItemAdding = passengers.get(new Random().nextInt(passengers.size()));
        flightKeyForWhichHoldItemAdding = getFlightKeyForPassenger(passengerCodeForWhichHoldItemAdding);
    }

    public void containedTheErrorMessageForHoldBag(String error) {
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    private String getFlightKeyForPassenger(String passengerCode) {
        return passengerCode.split("_")[1];
    }

    public void holdItemAddedToTheExpectedPassenger(String productType) {
        basketHelper.getBasket(testData.getAmendableBasket());
        basketHoldItemsHelper.getAddHoldBagToBasketService().assertThat();
        if (containsIgnoreCase(productType, "sport") || containsIgnoreCase(productType, "hold bag")) {
            basketHoldItemsHelper
                    .getAddHoldBagToBasketService()
                    .assertThat()
                    .verifyHoldItemAddedOnlyToTheExpectedPassenger(productType, passengerCodeForWhichHoldItemAdding, testData.getAmendableBasket(), basketHelper.getBasketService().getResponse());
        } else {
            basketHoldItemsHelper
                    .getAddHoldBagToBasketService()
                    .assertThat()
                    .excessWeightAddedSuccessfullyToSpecificHoldBag(basketHelper.getBasketService().getResponse(), basketHoldItemsHelper.getRequestBody(), basketHoldItemsHelper.getOrderEntryNumber());
        }
    }

    public void oldAmendableBasketShouldHaveBeenDeleted() {
        assertThat(cartDao.isBasketExists(firstAmendableBasketForTheBooking)).isFalse();
    }

    public void addAdditionalFareToPassenger(int addlFare, String parameter) {

        Basket basketTmp = getBasketHelper().getBasketService().getResponse().getBasket();
        if (Objects.nonNull(basketTmp.getOutbounds()) && !basketTmp.getOutbounds().isEmpty()) {
            addAdditionalFare(CommonConstants.OUTBOUND, basketTmp, parameter, addlFare);
        }
        if (Objects.nonNull(basketTmp.getInbounds()) && !basketTmp.getInbounds().isEmpty()) {
            addAdditionalFare(CommonConstants.INBOUND, basketTmp, parameter, addlFare);
        }
    }

    private void addAdditionalFare(String bound, Basket basketTmp, String parameter, int addlFare) {
        List<Basket.Flight> basketFlights = new ArrayList<>();
        if (bound.equalsIgnoreCase(CommonConstants.OUTBOUND)) {
            basketFlights = basketTmp.getOutbounds()
                    .stream()
                    .flatMap(f -> f.getFlights().stream())
                    .collect(Collectors.toList());
        } else if (bound.equalsIgnoreCase(CommonConstants.INBOUND)) {
            basketFlights = basketTmp.getInbounds()
                    .stream()
                    .flatMap(f -> f.getFlights().stream())
                    .collect(Collectors.toList());
        }

        for (Basket.Flight flight : basketFlights) {

            List<Basket.Passenger> basketPassengers = flight.getPassengers().stream().collect(Collectors.toList());
            for (Basket.Passenger traveller : basketPassengers) {

                if ("basketId".equalsIgnoreCase(parameter)) {
                    basketPathParams = BasketPathParams.builder().basketId(INVALID).path(MANAGE_ADDITIONAL_SEAT_TO_PASSENGER).passengerId(traveller.getCode()).build();
                } else if ("passengerId".equalsIgnoreCase(parameter)) {
                    basketPathParams = BasketPathParams.builder().basketId(basketTmp.getCode()).path(MANAGE_ADDITIONAL_SEAT_TO_PASSENGER).passengerId(INVALID).build();
                } else {
                    basketPathParams = BasketPathParams.builder().basketId(basketTmp.getCode()).path(MANAGE_ADDITIONAL_SEAT_TO_PASSENGER).passengerId(traveller.getCode()).build();
                }
                AddAdditionalFareToPassengerInBasketRequestBody addAdditionalFareToPassengerInBasketRequestBody;
                if ("zeroFare".equalsIgnoreCase(parameter)) {
                    addAdditionalFareToPassengerInBasketRequestBody = AddAdditionalFareToPassengerInBasketRequestBody.builder().numberOfFares(0).build();
                } else if ("requestBody".equalsIgnoreCase(parameter)) {
                    addAdditionalFareToPassengerInBasketRequestBody = AddAdditionalFareToPassengerInBasketRequestBody.builder().build();
                } else {
                    addAdditionalFareToPassengerInBasketRequestBody = AddAdditionalFareToPassengerInBasketRequestBody.builder().numberOfFares(addlFare).build();
                }
                addAdditionalFareToPassengerInBasketRequestBody.setAdditionalSeatReason("COMFORT");
                manageAdditionalFareToPassengerInBasketService = serviceFactory.manageAdditionalFareToPassengerInBasket(new ManageAdditionalFareToPassengerInBasketRequest(HybrisHeaders.getValid(testData.getChannel()).build(), basketPathParams, addAdditionalFareToPassengerInBasketRequestBody));
                manageAdditionalFareToPassengerInBasketService.invoke();
                testData.setData(SerenityFacade.DataKeys.SERVICE, manageAdditionalFareToPassengerInBasketService);
            }
        }
    }

    public void addAdditionalFareToEachPassenger(Basket basket) {
        basketHelper.getBasketService().assertThat().additionalSeatAddedForEachPassenger(basket);
    }

    public void addAdditionalFareToPassengerSuccessBasketID(String basketID) {
        manageAdditionalFareToPassengerInBasketService.assertThat().basketOperationConfirmation(basketID);
    }

    public void addAdditionalFareToPassengerError(String errorCode) {
        manageAdditionalFareToPassengerInBasketService.assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }

    public void addAdditionalFareToPassengerWarning(String warningCode) {
        manageAdditionalFareToPassengerInBasketService.assertThat().containedTheCorrectWarningMessage(warningCode);
    }
}