package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.FeesAndTaxesDao;
import com.hybris.easyjet.database.hybris.dao.MembershipDao;
import com.hybris.easyjet.database.hybris.models.FeesAndTaxesModel;
import com.hybris.easyjet.database.hybris.models.MemberShipModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.UpdatePassengerDetailsQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.AmendBasicDetailsRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.GetAmendableBookingRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.Name;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.CreateAmendableBasketBodyFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AmendBasicDetailsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.GetAmendableBookingRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractFlights;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AugmentedPriceItem;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.Currency;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.AmendBasicDetailsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.GetAmendableBookingService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BASKET_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CHANNEL;
import static com.hybris.easyjet.config.constants.CommonConstants.ADULT;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams.BookingPaths.AMENDABLE_BOOKING_REQUEST;
import static com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket.Passenger;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Java6Assertions.assertThat;


/**
 * Created by rajakm on 14/06/2017.
 */
@Component
public class ManageBookingHelper {

    @Autowired
    @Getter
    private BasketHelper basketHelper;
    @Getter
    @Setter
    private MemberShipModel membership;
    @Getter
    private AmendBasicDetailsService amendBasicDetailsService;

    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BookingHelper bookingHelper;
    @Autowired
    private FeesAndTaxesDao feesAndTaxesDao;
    @Autowired
    private MembershipDao membershipDao;


    private Basket basket;
    private String ordRefId;
    private String actualPassengerType = null;
    private int actualPassengerAge = 0;
    private String passengerCode = null;
    private String initialPassengerStatus = null;
    private String initialPassengerAPIStatus = null;
    private String initialPassengerICTSStatus = null;
    private String initialPassengerBundleCode = null;
    private String currentPassengerStatus = null;
    private String currentPassengerAPIStatus = null;
    private String currentPassengerICTSStatus = null;

    private GetAmendableBookingService amendableBookingService;
    private AmendBasicDetailsRequestBody updateBasicDetailsRequestBody;

    private static final String COMPLETED = "COMPLETED";
    private static final String UPDATE_SURNAME = "UpdateSurname";
    private String passengerLastName;
    private String eJPlusNumber;

    public void amendBooking() {
        BookingPathParams params = BookingPathParams.builder().bookingId(testData.getData(SerenityFacade.DataKeys.BOOKING_ID)).path(AMENDABLE_BOOKING_REQUEST).build();
        GetAmendableBookingRequestBody amendableBookingRequestBody = CreateAmendableBasketBodyFactory.createABodyForBookingLevelAmendableBasket(TRUE);
        amendableBookingService = serviceFactory.getAmendableBooking(new GetAmendableBookingRequest(HybrisHeaders.getValid(testData.getData(CHANNEL)).build(), params, amendableBookingRequestBody));
        amendableBookingService.invoke();
        assertThat(amendableBookingService.getResponse().getOperationConfirmation().getBasketCode()).isNotNull();
    }

    public void getBasketInEditMode() {
        basket = basketHelper.getBasket(amendableBookingService.getResponse().getOperationConfirmation().getBasketCode(), testData.getData(CHANNEL));
        assertThat(basket).isNotNull();
        ordRefId = amendableBookingService.getResponse().getOperationConfirmation().getBasketCode();
        testData.setData(BASKET_ID, ordRefId);
    }

    private void getPreManageBookingStatuses(String passengerCode) {
        GetBookingResponse preEditBookingResponse = bookingHelper.getBookingDetails(testData.getData(SerenityFacade.DataKeys.BOOKING_ID), testData.getData(CHANNEL));
        List<GetBookingResponse.Passenger> passengers = preEditBookingResponse.getBookingContext().getBooking().getOutbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList());
        passengers.addAll(preEditBookingResponse.getBookingContext().getBooking().getInbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList()));

        for (GetBookingResponse.Passenger passenger : passengers) {
            if (passenger.getCode().equals(passengerCode)) {
                initialPassengerStatus = passenger.getPassengerStatus();
                initialPassengerAPIStatus = passenger.getApisStatus();
                initialPassengerICTSStatus = passenger.getIctsStatus();
                initialPassengerBundleCode = passenger.getFareProduct().getBundleCode();
                break;
            }
        }
    }

    public String getPassengerCodeInTheBasket(String passengerType) {
        basket = null;
        basket = basketHelper.getBasket(testData.getData(BASKET_ID), testData.getData(CHANNEL));
        assertThat(basket).isNotNull();

        List<Basket.Passenger> passengers = basket.getOutbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList());
        passengers.addAll(basket.getInbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList()));

        for (Passenger passenger : passengers) {
            if ((passenger.getInfantsOnLap().isEmpty()) && (passenger.getPassengerDetails().getPassengerType().equalsIgnoreCase(passengerType))) {
                passengerCode = passenger.getCode();
                break;
            }
        }
        return passengerCode;
    }

    public String getPassengerCodeInTheBasket(Basket basket, String passengerType) throws EasyjetCompromisedException {

        List<Basket.Passenger> passengers = basket.getOutbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList());
        passengers.addAll(basket.getInbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList()));

        return passengers.stream().filter(passenger -> passenger.getPassengerDetails().getPassengerType().equalsIgnoreCase(passengerType)).findFirst().orElseThrow(() -> new EasyjetCompromisedException("No passengers with the specifiued type")).getCode();
    }

    public String getPassengerCodeInTheBasket(int passengerIndex) {
        basket = basketHelper.getBasket(testData.getData(BASKET_ID), testData.getData(CHANNEL));
        List<Basket.Passenger> passengers = basket.getOutbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList());
        passengers.addAll(basket.getInbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList()));

        return passengers.get(passengerIndex - 1).getCode();
    }

    private String getFirstAdultPassengerCodeInTheFlight(String flightKey) {

        List<Basket.Passenger> passengers = basket.getOutbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList());
        passengers.addAll(basket.getInbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList()));
        passengers = passengers.stream().filter(pgrs -> pgrs.getCode().contains(flightKey)).collect(Collectors.toList());

        for (Passenger passenger : passengers) {
            if (ADULT.equalsIgnoreCase(passenger.getPassengerDetails().getPassengerType())) {
                passengerCode = passenger.getCode();
                break;
            }
        }
        return passengerCode;
    }

    public void amendPassengerAgeAndType(String passengerCode, int newAge, String allRelatedFlights) {
        BasketPathParams basketPathParams = BasketPathParams.builder().basketId(ordRefId).passengerId(passengerCode).path(BasketPathParams.BasketPaths.UPDATE_BASIC_DETAILS).build();
        AmendBasicDetailsRequestBody amendBasicDetailsRequestBody = AmendBasicDetailsRequestBody.builder().age(newAge).build();
        amendBasicDetailsService = serviceFactory.amendBasicDetails(new AmendBasicDetailsRequest(HybrisHeaders.getValid(testData.getData(CHANNEL)).build(), basketPathParams, UpdatePassengerDetailsQueryParams.builder().allRelatedFlights(allRelatedFlights).build(), amendBasicDetailsRequestBody));
    }

    public void invokeAmendBasicDetailsService() {
        amendBasicDetailsService.invoke();
    }

    public void verifyAmendBookingIsSuccessful() {
        amendBasicDetailsService.assertThat().basketIsUpdated();
    }

    public void verifyAmendBasicDetailsAdditionalMessage(String expectedMessage) {
        amendBasicDetailsService.assertThat().additionalInformationContains(expectedMessage);
    }

    public void verifyAmendBookingIsNotSuccessful(String expectedError) {
        amendBasicDetailsService.assertThatErrors().containedTheCorrectErrorMessage(expectedError);
    }

    private Passenger getPassengerFromTheBasket(String actualPassengerCode) {
        passengerCode = actualPassengerCode;
        basket = null;
        Passenger actualPassenger = null;
        basket = basketHelper.getBasket(testData.getData(BASKET_ID), testData.getData(CHANNEL));
        assertThat(basket).isNotNull();

        List<Basket.Passenger> passengers = basket.getOutbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList());
        passengers.addAll(basket.getInbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList()));

        for (Passenger passenger : passengers) {
            if (passenger.getCode().equals(passengerCode)) {
                actualPassenger = passenger;
                break;
            }
        }
        assertThat(actualPassenger).isNotNull();
        return actualPassenger;
    }

    public List<String> getPassengerMapFromTheBasket(String actualPassengerCode) {
        passengerCode = actualPassengerCode;
        basket = null;
        List<String> passengerMap = null;
        basket = basketHelper.getBasket(ordRefId, testData.getData(CHANNEL));
        assertThat(basket).isNotNull();

        List<Basket.Passenger> passengers = basket.getOutbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList());
        passengers.addAll(basket.getInbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList()));

        for (Passenger passenger : passengers) {
            if (passenger.getCode().equals(passengerCode)) {
                passengerMap = passenger.getPassengerMap();
                break;
            }
        }
        assertThat(passengerMap).isNotNull();
        return passengerMap;
    }

    public void getPassengerAgeAndType(String passengerCode) {
        Passenger passenger = getPassengerFromTheBasket(passengerCode);
        assertThat(passenger).isNotNull();
        actualPassengerType = passenger.getPassengerDetails().getPassengerType();
        actualPassengerAge = passenger.getAge();
    }

    public void verifyPassengerAge(int expectedPassengerAge) {
        pollingLoop().untilAsserted(() -> {
            getPassengerAgeAndType(passengerCode);
            assertThat(actualPassengerAge == expectedPassengerAge).isTrue();
        });
    }

    public void verifyPassengerType(String expectedPassengerType) {
        pollingLoop().untilAsserted(() -> {
            getPassengerAgeAndType(passengerCode);
            assertThat(actualPassengerType.equalsIgnoreCase(expectedPassengerType)).isTrue();
        });
    }

    public void verifyTaxesAndFeesExist(String passengerCode, String passengerType, String channel, String tax) throws EasyjetCompromisedException {
        basketHelper.getBasketService().assertThat().taxesAreAppliedForPassenger(passengerCode, passengerType, channel, tax, basket.getCurrency().getCode(), feesAndTaxesDao);
    }

    public void verifyBasketTotalAfterAmendBooking() {
        assertThat(basket).isNotNull();
        Currency currency = basket.getCurrency();
        FeesAndTaxesModel fee = feesAndTaxesDao.getAdminFees(currency.getCode()).get(0);
        basketHelper.getBasketService().assertThat().priceCalculationAreRight(Integer.valueOf(currency.getDecimalPlaces()), fee.getFeeValue(), basket);
    }

    private void getPostManageBookingStatuses(String passengerCode) {
        GetBookingResponse postEditBookingResponse = bookingHelper.getBookingDetails(testData.getData(SerenityFacade.DataKeys.BOOKING_ID), testData.getData(CHANNEL));

        List<GetBookingResponse.Passenger> passengers = postEditBookingResponse.getBookingContext().getBooking().getOutbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList());
        passengers.addAll(postEditBookingResponse.getBookingContext().getBooking().getOutbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList()));

        for (GetBookingResponse.Passenger passenger : passengers) {
            if (passenger.getCode().equals(passengerCode)) {
                currentPassengerStatus = passenger.getPassengerStatus();
                currentPassengerAPIStatus = passenger.getApisStatus();
                currentPassengerICTSStatus = passenger.getIctsStatus();
                break;
            }
        }
    }

    public void verifyPassengerStatusAfterAmendBooking(String passengerCode) {
        pollingLoop().untilAsserted(() -> {
            getPreManageBookingStatuses(passengerCode);
            getPostManageBookingStatuses(passengerCode);
            assertThat(initialPassengerStatus.equals(currentPassengerStatus)).isTrue();
        });
    }

    public void verifyPassengerAPISStatusAfterAmendBooking(String passengerCode) {
        pollingLoop().untilAsserted(() -> {
            getPreManageBookingStatuses(passengerCode);
            getPostManageBookingStatuses(passengerCode);
            assertThat(initialPassengerAPIStatus.equals(currentPassengerAPIStatus)).isTrue();
        });
    }

    public void verifyPassengerICTSStatusAfterAmendBooking(String passengerCode) {
        pollingLoop().untilAsserted(() -> {
            getPreManageBookingStatuses(passengerCode);
            getPostManageBookingStatuses(passengerCode);
            assertThat(initialPassengerICTSStatus).isEqualTo(currentPassengerICTSStatus);
        });
    }

    public void verifyExistingBundleHasRemoved(String actualPassengerCode) {
        pollingLoop().untilAsserted(() -> {
            Passenger passenger = getPassengerFromTheBasket(actualPassengerCode);
            getPreManageBookingStatuses(passengerCode);
            assertThat(passenger).isNotNull();
            assertThat(passenger.getFareProduct().getBundleCode().equalsIgnoreCase(initialPassengerBundleCode)).isFalse();
        });
    }

    public void verifyExistingProductHasRemoved(String actualPassengerCode) {
        pollingLoop().untilAsserted(() -> {
            Passenger passenger = getPassengerFromTheBasket(actualPassengerCode);
            assertThat(passenger).isNotNull();
            assertThat(passenger.getHoldItems()).isNullOrEmpty();
        });
    }

    public void verifyInfantOnLapProductIsAdded(String actualPassengerCode, String passengerType) {
        pollingLoop().untilAsserted(() -> {
            Passenger passenger = getPassengerFromTheBasket(actualPassengerCode);
            assertThat(passenger).isNotNull();
            assertThat(passenger.getFareProduct().getBundleCode().equalsIgnoreCase(passengerType)).isTrue();
        });
    }

    public void verifyInfantOnLapIsAssociatedToAdult(String infantPassengerCode) {
        String flightKey = infantPassengerCode.split("_")[1];
        String firstAdultPassengerCode = getFirstAdultPassengerCodeInTheFlight(flightKey);
        pollingLoop().untilAsserted(() -> {
            Passenger firstAdultPassenger = getPassengerFromTheBasket(firstAdultPassengerCode);
            assertThat(firstAdultPassenger.getInfantsOnLap().isEmpty()).isFalse();
            assertThat(firstAdultPassenger.getInfantsOnLap().stream().findFirst().get().equalsIgnoreCase(infantPassengerCode)).isTrue();
        });
    }

    public void verifyAmendableBasketCurrency() {
        assertThat(basket.getCurrency().getCode()).isEqualTo(testData.getActualCurrency());
    }

    public void amendReqBodyWithEJPlusNumber(Basket.Passenger passengers, String memberType) {
        MemberShipModel memberShipModelFirst;
        switch (memberType) {
            case "anotherCustomer":
                MemberShipModel msm = (MemberShipModel) testData.getData("ejPlusMember");
                memberShipModelFirst = getFilteredEJPlusNumber(msm.getEjMemberShipNumber());
                break;
            case "staff":
                memberShipModelFirst = membershipDao.getValidEJPlusMembershipForStaffWithStatus(COMPLETED);
                break;
            case "customer":
                memberShipModelFirst = membershipDao.getValidEJPlusMembershipForStaffWithStatus(COMPLETED);
                break;
            default:
                throw new RuntimeException("Invalid ejplus member type selected" + memberType);
        }

        com.hybris.easyjet.fixture.hybris.invoke.requestbody.Name name =
                com.hybris.easyjet.fixture.hybris.invoke.requestbody.Name.builder().lastName(memberShipModelFirst.getLastname()).build();

        AmendBasicDetailsRequestBody.AmendBasicDetailsRequestBodyBuilder builder;
        try {
            builder =
                    (AmendBasicDetailsRequestBody.AmendBasicDetailsRequestBodyBuilder) MethodUtils.invokeExactMethod(
                            AmendBasicDetailsRequestBody.builder(),
                            "name", name);

            invokeAmendBasicDetails(passengers.getCode(), builder.build());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        builder.ejPlusCardNumber(memberShipModelFirst.getEjMemberShipNumber());
        invokeAmendBasicDetails(passengers.getCode(), builder.build());
        testData.setData("ejPlusMember", memberShipModelFirst);
    }

    public void amendReqBodyWithEJPlusMemberLastNameChange(Basket.Passenger passengers) {

        Name name = Name.builder().lastName("UpdatedLastName").build();

        AmendBasicDetailsRequestBody.AmendBasicDetailsRequestBodyBuilder builder;
        try {
            builder =
                    (AmendBasicDetailsRequestBody.AmendBasicDetailsRequestBodyBuilder) MethodUtils.invokeExactMethod(
                            AmendBasicDetailsRequestBody.builder(),
                            "name", name);

            invokeAmendBasicDetails(passengers.getCode(), builder.build());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
    private MemberShipModel getFilteredEJPlusNumber(String ejPlusNumber) {
        List<MemberShipModel> allMemberShipModels = membershipDao.getAllEJPlusMembershipsWithStatus(COMPLETED);
        return getFilteredMembershipModelFirstMember(ejPlusNumber, allMemberShipModels);
    }

    private static MemberShipModel getFilteredMembershipModelFirstMember(String ejPlusNumber, List<MemberShipModel> allMemberShipModels) {
        List<MemberShipModel> memberShipModelList = allMemberShipModels.stream().filter(msm -> !msm.getEjMemberShipNumber()
                .equals(ejPlusNumber)).collect(Collectors.toList());
        return memberShipModelList.stream().findFirst().orElse(null);
    }

    private void invokeAmendBasicDetails(String passengerCode, AmendBasicDetailsRequestBody amendBasicDetailsRequestBody) {
        BasketPathParams basketPathParams = BasketPathParams.builder()
                .basketId(testData.getData(BASKET_ID))
                .passengerId(passengerCode)
                .path(BasketPathParams.BasketPaths.UPDATE_BASIC_DETAILS)
                .build();

        amendBasicDetailsService = serviceFactory.amendBasicDetails(
                new AmendBasicDetailsRequest(
                        HybrisHeaders.getValid(testData.getData(CHANNEL)).build(),
                        basketPathParams,
                        amendBasicDetailsRequestBody
                )
        );

        invokeServiceUntilSuccess();
    }

    private void invokeServiceUntilSuccess() {
        final int[] attempts = {3};
        try {
            pollingLoop().until(() -> {
                amendBasicDetailsService.invoke();
                attempts[0]--;
                return amendBasicDetailsService.getStatus() == 200 || attempts[0] == 0;
            });
        } catch (ConditionTimeoutException e) {
        }
        amendBasicDetailsService.getResponse();
    }

    public Double getAdminFeeFromBasket() {
        Double adminFeeValue = 0.0;
        Optional<AugmentedPriceItem> adminFee = basketHelper.getBasket(testData.getAmendableBasket(), testData.getData(CHANNEL)).getFees().getItems().stream()
                .filter(c -> "AdminFee".equals(c.getCode()))
                .findFirst();
        if (adminFee.isPresent()) {
            adminFeeValue = adminFee.get().getAmount();
        }
        return adminFeeValue;
    }

    public void verifyAdminFeeNotApplied(String newSector) {
        basket = null;
        basket = basketHelper.getBasket(testData.getAmendableBasket(), testData.getData(CHANNEL));
        assertThat(basket).isNotNull();

        List<Basket.Passenger> passengers = basket.getOutbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream())
                .filter(ps -> ps.getCode().contains(newSector))
                .collect(Collectors.toList());
        passengers.addAll(basket.getInbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream())
                .filter(ps -> ps.getCode().contains(newSector))
                .collect(Collectors.toList()));

        for (Passenger passenger : passengers) {
            assertThat(passenger.getFareProduct().getPricing().getFees()
                    .stream()
                    .filter(fee -> fee.getCode().equals("AdminFee"))
                    .collect(Collectors.toList())).isEmpty();
        }
    }

    public void manageUpdateBasicPassengerDetails(String fieldToUpdate) {
        updateBasicDetailsRequestBody = AmendBasicDetailsRequestBody.builder().name(Name.builder().build()).build();
        switch (fieldToUpdate) {
            case "addEJPlusMembership":
                updateMembershipForPassenger();
                break;
            case "removeEJPlusMembership":
                membership = MemberShipModel.builder().ejMemberShipNumber("").build();
                updateBasicDetailsRequestBody.setName(null);
                updateBasicDetailsRequestBody.setEjPlusCardNumber("");
                break;
            case UPDATE_SURNAME:
                updateSurname();
                break;
            default:
                break;
        }

        invokeUpdatePassengerDetails();
    }

    private void updateSurname() {
        updateBasicDetailsRequestBody.getName().setLastName(UPDATE_SURNAME);
    }

    private void updateMembershipForPassenger() {
        membership = membershipDao.getValidEJPlusMembershipForStaffWithStatus(COMPLETED);
        updateBasicDetailsRequestBody.getName().setLastName(membership.getLastname());
        invokeUpdatePassengerDetails();
        amendBasicDetailsService.getResponse();
        updateBasicDetailsRequestBody.setName(null);
        updateBasicDetailsRequestBody.setEjPlusCardNumber(membership.getEjMemberShipNumber());
    }

    public void updateEJPlusMembershipForStandardPassenger() throws EasyjetCompromisedException {
        updateBasicDetailsRequestBody = AmendBasicDetailsRequestBody.builder().name(Name.builder().build()).build();
        if (eJPlusNumber == null) {
            membership = membershipDao.getEJPlusMemberBasedOnStatus(COMPLETED);
            passengerLastName = membership.getLastname();
            eJPlusNumber = membership.getEjMemberShipNumber();
        }
        updateBasicDetailsRequestBody.getName().setLastName(passengerLastName);
        invokeUpdatePassengerDetails();
        amendBasicDetailsService.getResponse();

        pollingLoop().untilAsserted(() -> {
            Passenger passenger = getPassengerFromTheBasket(testData.getPassengerId());
            assertThat(passenger.getPassengerDetails().getName().getLastName().equalsIgnoreCase(passengerLastName)).isTrue();
        });

        updateBasicDetailsRequestBody.setName(null);
        updateBasicDetailsRequestBody.setEjPlusCardNumber(eJPlusNumber);
    }

    public void updateEJPlusMembershipWithIncorrectLastName() throws EasyjetCompromisedException {
        updateBasicDetailsRequestBody = AmendBasicDetailsRequestBody.builder().name(Name.builder().build()).build();
        membership = membershipDao.getEJPlusMemberBasedOnStatus(COMPLETED);
        updateSurname();
        invokeUpdatePassengerDetails();
        amendBasicDetailsService.getResponse();

        pollingLoop().untilAsserted(() -> {
            Passenger passenger = getPassengerFromTheBasket(testData.getPassengerId());
            assertThat(passenger.getPassengerDetails().getName().getLastName().equalsIgnoreCase(UPDATE_SURNAME)).isTrue();
        });

        updateBasicDetailsRequestBody.setName(null);
        updateBasicDetailsRequestBody.setEjPlusCardNumber(membership.getEjMemberShipNumber());
    }

    public void updateExpiredMembershipForPassenger() {
        updateBasicDetailsRequestBody = AmendBasicDetailsRequestBody.builder().name(Name.builder().build()).build();
        membership = membershipDao.getExpiredEJPlusMembership(COMPLETED);
        updateBasicDetailsRequestBody.getName().setLastName(membership.getLastname());
        invokeUpdatePassengerDetails();
        amendBasicDetailsService.getResponse();

        pollingLoop().untilAsserted(() -> {
            Passenger passenger = getPassengerFromTheBasket(testData.getPassengerId());
            assertThat(passenger.getPassengerDetails().getName().getLastName().equalsIgnoreCase(membership.getLastname())).isTrue();
        });

        updateBasicDetailsRequestBody.setName(null);
        updateBasicDetailsRequestBody.setEjPlusCardNumber(membership.getEjMemberShipNumber());
    }

    public void updateIncorrectMembershipForPassenger(String incorrectFormat) throws EasyjetCompromisedException {
        updateBasicDetailsRequestBody = AmendBasicDetailsRequestBody.builder().name(Name.builder().build()).build();
        membership = membershipDao.getEJPlusMemberBasedOnStatus(COMPLETED);
        updateSurname();
        invokeUpdatePassengerDetails();
        amendBasicDetailsService.getResponse();

        pollingLoop().untilAsserted(() -> {
            Passenger passenger = getPassengerFromTheBasket(testData.getPassengerId());
            assertThat(passenger.getPassengerDetails().getName().getLastName().equalsIgnoreCase(UPDATE_SURNAME)).isTrue();
        });


        updateBasicDetailsRequestBody.setName(null);
        updateBasicDetailsRequestBody.setEjPlusCardNumber(incorrectFormat);
    }

    public void updateMembershipWithNotCompleteStatus() throws EasyjetCompromisedException {
        updateBasicDetailsRequestBody = AmendBasicDetailsRequestBody.builder().name(Name.builder().build()).build();
        membership = membershipDao.getEJPlusMemberOtherThanStatus(COMPLETED);
        updateBasicDetailsRequestBody.getName().setLastName(membership.getLastname());
        invokeUpdatePassengerDetails();
        amendBasicDetailsService.getResponse();

        pollingLoop().untilAsserted(() -> {
            Passenger passenger = getPassengerFromTheBasket(testData.getPassengerId());
            assertThat(passenger.getPassengerDetails().getName().getLastName().equalsIgnoreCase(membership.getLastname())).isTrue();
        });

        updateBasicDetailsRequestBody.setName(null);
        updateBasicDetailsRequestBody.setEjPlusCardNumber(membership.getEjMemberShipNumber());
    }

    public void invokeUpdatePassengerDetails() {
        invokeAmendBasicDetails(testData.getPassengerId(), updateBasicDetailsRequestBody);
    }

    public void invokeUpdatePassengerDetailsForErroneous() {
        invokeAmendBasicDetailsForErroneous(testData.getPassengerId(), updateBasicDetailsRequestBody);
    }

    private void invokeAmendBasicDetailsForErroneous(String passengerCode, AmendBasicDetailsRequestBody amendBasicDetailsRequestBody) {
        BasketPathParams basketPathParams = BasketPathParams.builder()
                .basketId(testData.getData(BASKET_ID))
                .passengerId(passengerCode)
                .path(BasketPathParams.BasketPaths.UPDATE_BASIC_DETAILS)
                .build();

        amendBasicDetailsService = serviceFactory.amendBasicDetails(
                new AmendBasicDetailsRequest(
                        HybrisHeaders.getValid(testData.getData(CHANNEL)).build(),
                        basketPathParams,
                        amendBasicDetailsRequestBody
                )
        );
    }

    public void updateEJPlusMembershipForIncorrectPaxId() throws EasyjetCompromisedException {
        updateBasicDetailsRequestBody = AmendBasicDetailsRequestBody.builder().name(Name.builder().build()).build();
        if (eJPlusNumber == null) {
            membership = membershipDao.getEJPlusMemberBasedOnStatus(COMPLETED);
            passengerLastName = membership.getLastname();
            eJPlusNumber = membership.getEjMemberShipNumber();
        }
        updateBasicDetailsRequestBody.setEjPlusCardNumber(eJPlusNumber);
    }

    public Basket getBasketAfterUpdateMembership() throws EasyjetCompromisedException {
        Basket[] basketsResponse = new Basket[1];
        try {
            pollingLoop().until(() -> {
                basketsResponse[0] = basketHelper.getBasket(testData.getData(BASKET_ID), testData.getData(CHANNEL));
                String actualMembership = basketsResponse[0].getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(testData.getPassengerId())).findFirst().orElse(null).getPassengerDetails().getEjPlusCardNumber();
                String expectedMembership = Objects.isNull(membership) ? "" : membership.getEjMemberShipNumber();
                return Objects.nonNull(actualMembership) && actualMembership.equalsIgnoreCase(expectedMembership);
            });
        } catch (ConditionTimeoutException e) {
            throw new EasyjetCompromisedException("EJPlus not valid for " + testData.getPassengerId());
        }
        return basketsResponse[0];
    }

    public List<String> getAllFlightKeysFromTheBasket() {

        basket = null;
        basket = basketHelper.getBasket(testData.getData(SerenityFacade.DataKeys.BASKET_ID), testData.getData(SerenityFacade.DataKeys.CHANNEL));
        assertThat(basket).isNotNull();

        List<String> flightKeys = basket.getOutbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .map(AbstractFlights.AbstractFlight::getFlightKey).collect(Collectors.toList());
        flightKeys.addAll(basket.getInbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .map(AbstractFlights.AbstractFlight::getFlightKey).collect(Collectors.toList()));

        return flightKeys;
    }

    public void verifyTheNewPassengersAreAdded(String flightKey, List<String> expectedPaxCodes) {
        basket = null;
        basket = basketHelper.getBasket(testData.getData(SerenityFacade.DataKeys.BASKET_ID), testData.getData(SerenityFacade.DataKeys.CHANNEL));
        assertThat(basket).isNotNull();
        List<String> passengerCodes = basket.getOutbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(px -> px.getPassengers().stream())
                .filter(c -> c.getCode().contains(flightKey))
                .map(AbstractPassenger::getCode).collect(Collectors.toList());
        passengerCodes.addAll(basket.getInbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(px -> px.getPassengers().stream())
                .filter(c -> c.getCode().contains(flightKey))
                .map(AbstractPassenger::getCode).collect(Collectors.toList()));

        for (String paxCode : expectedPaxCodes) {
            assertThat(passengerCodes.contains(paxCode)).isTrue();
        }
    }

    public void verifyTheNewPassengersAreAdded(List<String> expectedPaxCodes) {
        basket = null;
        basket = basketHelper.getBasket(testData.getData(SerenityFacade.DataKeys.BASKET_ID), testData.getData(SerenityFacade.DataKeys.CHANNEL));
        assertThat(basket).isNotNull();
        List<String> passengerCodes = basket.getOutbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(px -> px.getPassengers().stream())
                .map(AbstractPassenger::getCode).collect(Collectors.toList());
        passengerCodes.addAll(basket.getInbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(px -> px.getPassengers().stream())
                .map(AbstractPassenger::getCode).collect(Collectors.toList()));

        if (Objects.nonNull(expectedPaxCodes)) {
            for (String paxCode : expectedPaxCodes) {
                assertThat(passengerCodes.contains(paxCode)).isTrue();
            }
        }
    }

    public void verifyTheBundleForNewPassengersAdded(List<String> paxCodes, String fareType) {
        basket = null;
        basket = basketHelper.getBasket(testData.getData(SerenityFacade.DataKeys.BASKET_ID), testData.getData(SerenityFacade.DataKeys.CHANNEL));
        assertThat(basket).isNotNull();
        List<Basket.Passenger> passengers = basket.getOutbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList());
        passengers.addAll(basket.getInbounds().stream().flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList()));

        for (Passenger passenger : passengers) {
            if (paxCodes.contains(passenger.getCode())) {
                assertThat(passenger.getFareProduct().getBundleCode().equalsIgnoreCase(fareType));
                assertThat(passenger.getFareProduct().getQuantity().intValue()).isNotZero();
            }
        }
    }

    public void verifyBasketTotalsMoreAfterAddingInfant() {
        Basket amendableBasket = basketHelper.getBasket(testData.getData(SerenityFacade.DataKeys.BASKET_ID), testData.getData(SerenityFacade.DataKeys.CHANNEL));
        BasketService oldBasket = testData.getData(SerenityFacade.DataKeys.BASKET_SERVICE);
        assertThat(amendableBasket.getTotalAmountWithCreditCard() > oldBasket.getResponse().getBasket().getTotalAmountWithCreditCard()).isTrue();
        assertThat(amendableBasket.getSubtotalAmountWithDebitCard() > oldBasket.getResponse().getBasket().getTotalAmountWithDebitCard()).isTrue();
    }
}
