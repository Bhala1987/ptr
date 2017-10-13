package com.hybris.easyjet.fixture.hybris.helpers;


import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.CheckInFlightRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.CheckInFlightRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.CheckInFlightService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import lombok.Getter;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BOOKING_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.FLIGHT_KEY;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams.BookingPaths.CHECKIN;

@Component
public class CheckInHelper {

    @Getter
    public CheckInFlightService checkInFlightService;

    @Autowired
    private SerenityFacade testData;

    private HybrisServiceFactory serviceFactory;
    private BookingPathParams bookingPathParams;
    private CheckInFlightRequestBody checkInFlightRequestBody;

    @Autowired
    public CheckInHelper(HybrisServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    public void checkInAFlight(List<String> passengerCodes) {
//        NOT ABLE TO CHECK IN WITHOUT MOCK RESPONSE THEREFORE PLEASE MAKE SURE YOU RUN WIREMOCK LOCALLY
        System.setProperty("mocked", "true");
        String passengerMix = testData.getPassengerMix();
        if ("1 Adult, 1 Infant".equalsIgnoreCase(passengerMix)
                || "1 Adult, 1 Infant OL".equalsIgnoreCase(passengerMix)
                || "2 Adults".equalsIgnoreCase(passengerMix)
                || "2 Adult".equalsIgnoreCase(passengerMix)
                || "2 Adult, 1 Infant".equals(passengerMix)) {
            if ("1 Adult, 1 Infant".equalsIgnoreCase(passengerMix)
                    || "2 Adults".equalsIgnoreCase(passengerMix)
                    || "2 Adult, 1 Infant".equals(passengerMix)) {
                buildCheckInRequestForAllPassenger();
            } else {
                buildCheckInRequestForSpecificPassenger(passengerCodes);
            }
        } else {
            buildCheckInRequestForSpecificPassenger(passengerCodes);
        }
        invokeCheckInService();
    }

    public void checkInAFlightWithCustomerClientTransactionId(String customTransaction) {
        buildCheckInRequestForSpecificPassenger(Arrays.asList(testData.getPassengerId()));
        invokeCheckInServiceSpecifyTransactionId(customTransaction);
    }

    public void checkInFlightWithDangerousGoodSetToFalse() {
        buildCheckInRequestForAllPassenger();
        checkInFlightRequestBody.setIsDangerousGoodsAccepted(false);
        invokeCheckInService();
    }

    public void checkInAllFlight(boolean isGoodsAccepted) {
        buildCheckInRequestForAllPassenger();
        checkInFlightRequestBody.setIsDangerousGoodsAccepted(isGoodsAccepted);
        invokeCheckInService();
    }

    public void invokeCheckInService() {
        checkInFlightService = serviceFactory.checkInFlightService(new CheckInFlightRequest(HybrisHeaders.getValid(testData.getChannel()).build(), bookingPathParams, checkInFlightRequestBody));
        int[] noOfAttempts = {5};
        try {
            pollingLoop().until(() -> {
                checkInFlightService.invoke();
                noOfAttempts[0]--;
                return checkInFlightService.getStatusCode() == 200 || noOfAttempts[0] == 0;
            });
        } catch (ConditionTimeoutException ct) {
            checkInFlightService.getRestResponse();
        }
    }

    private void invokeCheckInServiceSpecifyTransactionId(String customTransaction) {
        checkInFlightService = serviceFactory.checkInFlightService(new CheckInFlightRequest(HybrisHeaders.getValidXClientTransactionId(testData.getChannel(), customTransaction).build(), bookingPathParams, checkInFlightRequestBody));
        checkInFlightService.invoke();
    }

    private void buildCheckInRequestForAllPassenger() {
        bookingPathParams = BookingPathParams.builder().bookingId(testData.getData(BOOKING_ID)).path(CHECKIN).build();
        checkInFlightRequestBody = getCheckInFlightRequestBodyForAll();
    }

    public void buildCheckInRequestForSpecificPassenger(List<String> passengersCode) {
        bookingPathParams = BookingPathParams.builder().bookingId(testData.getData(BOOKING_ID)).path(CHECKIN).build();
        if (testData.getData(FLIGHT_KEY) != null) {
            checkInFlightRequestBody = getCheckInFlightRequestBodyForSpecificPassengerOnFlight(passengersCode, testData.getData(FLIGHT_KEY));
        } else {
            checkInFlightRequestBody = getCheckInFlightRequestBodyForSpecificPassengerOnFlight(passengersCode, testData.getFlightKey());
        }
    }

    private static CheckInFlightRequestBody getCheckInFlightRequestBodyForAll() {
        return CheckInFlightRequestBody.builder()
                .isCheckInAll(true)
                .isDangerousGoodsAccepted(true)
                .build();
    }

    private static CheckInFlightRequestBody getCheckInFlightRequestBodyForSpecificPassengerOnFlight(List<String> passengerCode, String flightKey) {
        return CheckInFlightRequestBody.builder()
                .flights(Arrays.asList(
                        CheckInFlightRequestBody.CheckFlight.builder()
                                .flightKey(flightKey)
                                .passengers(new ArrayList<String>() {{
                                    for (String s : passengerCode) {
                                        add(s);
                                    }
                                }})
                                .build()
                        )
                )
                .isDangerousGoodsAccepted(true)
                .isCheckInAll(false).build();
    }
}
