package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.FindBookingQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.SetApiBookingRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.PassengerApisFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.SetAPIRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.SetApisBookingService;
import lombok.Getter;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.PASSENGER_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;
import static com.hybris.easyjet.config.constants.CommonConstants.*;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams.BookingPaths.SET_APIS_BOOKING;
import static org.junit.Assert.fail;

@Component
public class SetAPIHelper {

    @Getter
    private SetApisBookingService setApisBookingService;
    @Getter
    private SetApiBookingRequestBody requestBodyApisBooking;

    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private SerenityFacade testData;


    /**
     * The following method provide to build a request body to set the apis for a specific passenger. You need to have configured previously throw SerenityFacade the desired passenger id
     *
     * @param bookingContext to build the request
     * @param fieldToChange  field in the pay load that I want to modify
     * @return
     */
    private SetApiBookingRequestBody buildDifferentRequestBody(GetBookingResponse.BookingContext bookingContext, String fieldToChange) {
        AbstractPassenger passenger = bookingContext.getBooking().getOutbounds().stream()
                .flatMap(outbound -> outbound.getFlights().stream())
                .flatMap(flight -> flight.getPassengers().stream())
                .filter(bookingPassenger -> bookingPassenger.getCode().equals(testData.getPassengerId()))
                .findFirst()
                .orElse(null);

        AbstractPassenger.PassengerAPIS passengerAPIS = passenger.getPassengerAPIS();

        if (Objects.nonNull(passengerAPIS)) {
            requestBodyApisBooking = PassengerApisFactory.setAPIBookingRequestBody(passengerAPIS);
        } else {
            requestBodyApisBooking = PassengerApisFactory.aBasicBookingPassengerApis();
        }

        switch (passenger.getPassengerDetails().getPassengerType()) {
            case "adult":
                requestBodyApisBooking.getApi().getName().setTitle("mr");
                requestBodyApisBooking.getApi().setDateOfBirth(getDateOfBirthFromAge(99));
                break;
            case "child":
                requestBodyApisBooking.getApi().getName().setTitle("mr");
                requestBodyApisBooking.getApi().setDateOfBirth(getDateOfBirthFromAge(15));
                break;
            case "infant":
                requestBodyApisBooking.getApi().getName().setTitle("infant");
                requestBodyApisBooking.getApi().setDateOfBirth(getDateOfBirthFromAge(1));
                break;
            default:
                break;
        }

        return changeFieldWithValue(requestBodyApisBooking, fieldToChange);
    }

    private SetApiBookingRequestBody changeFieldWithValue(SetApiBookingRequestBody requestBodyApisBooking, String fieldToChange) {
        Calendar date = getDate();
        String pattern = "yyyy-MM-dd";
        switch (fieldToChange) {
            case "DifferentName":
                String title = requestBodyApisBooking.getApi().getName().getTitle();
                requestBodyApisBooking.getApi().setName(PassengerApisFactory.aBasicName());
                requestBodyApisBooking.getApi().getName().setTitle(title);
                break;
            case "ValidExpiredDate":
                requestBodyApisBooking.getApi().setDocumentExpiryDate(LocalDate.now().plusDays(4).format(DateTimeFormatter.ofPattern(pattern)));
                break;
            case "InvalidExpiredDate":
                requestBodyApisBooking.getApi().setDocumentExpiryDate(LocalDate.now().minusDays(6).format(DateTimeFormatter.ofPattern(pattern)));
                break;
            case "WrongPassport":
                requestBodyApisBooking.getApi().setDocumentType("WrongPassport");
                break;
            case "IdentityCard":
                requestBodyApisBooking.getApi().setDocumentType("ID_CARD");
                break;
            case "InvalidDateOfBirthNull":
                requestBodyApisBooking.getApi().setDateOfBirth("");
                break;
            case "InvalidDocumentNumber":
                requestBodyApisBooking.getApi().setDocumentNumber("1");
                break;
            case "DocumentNumberHasSpclChars":
                requestBodyApisBooking.getApi().setDocumentNumber("2$%44");
                break;
            case "DocumentNumbernull":
                requestBodyApisBooking.getApi().setDocumentNumber("");
                break;
            case "AdultAgeLessThan16":
                date.add(Calendar.YEAR, -15);
                date.add(Calendar.DATE, -2);
                requestBodyApisBooking.getApi().setDateOfBirth(getDateFormatter().format(date.getTime()));
                break;
            case "MoreThanInfantAge":
                date.add(Calendar.YEAR, 2);
                date.add(Calendar.DATE, 10);
                requestBodyApisBooking.getApi().setDateOfBirth(getDateFormatter().format(date.getTime()));
                break;
            case "InfantLessThan14days":
                date.add(Calendar.DATE, -12);
                requestBodyApisBooking.getApi().setDateOfBirth(getDateFormatter().format(date.getTime()));
                break;
            case "ChildAgeLess":
                date.add(Calendar.YEAR, -2);
                date.add(Calendar.DATE, +2);
                requestBodyApisBooking.getApi().setDateOfBirth(getDateFormatter().format(date.getTime()));
                break;
            case "ChildAgeMore":
                date.add(Calendar.YEAR, 15);
                date.add(Calendar.DATE, 2);
                requestBodyApisBooking.getApi().setDateOfBirth(getDateFormatter().format(date.getTime()));
                break;
            case "InfantWithOutAdult":
                date.add(Calendar.YEAR, -16);
                date.add(Calendar.DATE, +2);
                requestBodyApisBooking.getApi().setDateOfBirth(getDateFormatter().format(date.getTime()));
                break;
            case "NameDoesNotMatch":
                requestBodyApisBooking.getApi().getName().setFirstName("somethingElse");
                break;
            case "DuplicateDocumentNo":
                requestBodyApisBooking.getApi().setDocumentNumber(testData.getDocumentId());
                break;
            default:
                break;
        }
        return requestBodyApisBooking;
    }

    private Calendar getDate() {
        String departureDateTime = testData.getBookingResponse().getBookingContext().getBooking().getOutbounds()
                .stream().findFirst().orElse(null).getFlights().stream().findFirst().orElse(null).getDepartureDateTime();
        Calendar calender = Calendar.getInstance();
        try {
            calender.setTime(getDateFormatterWithDay().parse(departureDateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calender;
    }

    private static SimpleDateFormat getDateFormatterWithDay() {
        return new SimpleDateFormat("EEE d-MMM-yyyy HH-mm-ss");
    }

    private static SimpleDateFormat getDateFormatter() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    public void invokeUpdateIdentityDocument(boolean apisToFutureFLight, String fieldToChange) {
        requestBodyApisBooking = buildDifferentRequestBody(testData.getBookingResponse().getBookingContext(), fieldToChange);

        BookingPathParams params = BookingPathParams.builder().bookingId(testData.getData(SerenityFacade.DataKeys.BOOKING_ID)).path(SET_APIS_BOOKING).passengerId(testData.getPassengerId()).build();
        FindBookingQueryParams findBookingQueryParams = FindBookingQueryParams.builder().apisToFutureFlight(Boolean.toString(apisToFutureFLight)).build();

        setApisBookingService = serviceFactory.setApisBooking(new SetAPIRequest(HybrisHeaders.getValid(testData.getChannel()).build(), params, findBookingQueryParams, requestBodyApisBooking));
        testData.setData(SERVICE, setApisBookingService);

        invoke();

        setApisBookingService.getResponse();
    }

    public void invokeUpdateIdentityDocument(boolean apisToFutureFLight) {

        requestBodyApisBooking = PassengerApisFactory.aBasicBookingPassengerApis();
        if (testData.getPassengerId() == null && testData.getData(PASSENGER_ID) != null) {
            testData.setPassengerId(testData.getData(PASSENGER_ID));
        }
        BookingPathParams params = BookingPathParams.builder().bookingId(testData.getData(SerenityFacade.DataKeys.BOOKING_ID)).path(SET_APIS_BOOKING).passengerId(testData.getPassengerId()).build();
        FindBookingQueryParams findBookingQueryParams = FindBookingQueryParams.builder().apisToFutureFlight(Boolean.toString(apisToFutureFLight)).build();

        setApisBookingService = serviceFactory.setApisBooking(new SetAPIRequest(HybrisHeaders.getValid(testData.getChannel()).build(), params, findBookingQueryParams, requestBodyApisBooking));
        testData.setData(SERVICE, setApisBookingService);

        invoke();

        setApisBookingService.getResponse();
    }

    public void invokeUpdateIdentityDocumentForEachPassenger(boolean apisToFutureFLight, List<Basket.Passenger> passengers) {

        for (Basket.Passenger p : passengers) {
            requestBodyApisBooking = PassengerApisFactory.aBasicBookingPassengerApis();
            requestBodyApisBooking.getApi().setDateOfBirth(getDateOfBirthFromType(p.getPassengerDetails().getPassengerType()));

            BookingPathParams params = BookingPathParams.builder().bookingId(testData.getData(SerenityFacade.DataKeys.BOOKING_ID)).path(SET_APIS_BOOKING).passengerId(p.getCode()).build();
            FindBookingQueryParams findBookingQueryParams = FindBookingQueryParams.builder().apisToFutureFlight(Boolean.toString(apisToFutureFLight)).build();

            setApisBookingService = serviceFactory.setApisBooking(new SetAPIRequest(HybrisHeaders.getValid(testData.getChannel()).build(), params, findBookingQueryParams, requestBodyApisBooking));
            testData.setData(SERVICE, setApisBookingService);

            invoke();

            setApisBookingService.getResponse();
        }
    }

    public void invokeUpdateIdentityDocumentError(boolean apisToFutureFLight, String fieldToChange) {
        invokeAndUpdateIdentityDocument(apisToFutureFLight, fieldToChange);
        invokeWithError();
    }

    private void invokeAndUpdateIdentityDocument(boolean apisToFutureFLight, String fieldToChange) {
        requestBodyApisBooking = buildDifferentRequestBody(testData.getBookingResponse().getBookingContext(), fieldToChange);

        BookingPathParams params = BookingPathParams.builder().bookingId(testData.getData(SerenityFacade.DataKeys.BOOKING_ID)).path(SET_APIS_BOOKING).passengerId(testData.getPassengerId()).build();
        FindBookingQueryParams findBookingQueryParams = FindBookingQueryParams.builder().apisToFutureFlight(Boolean.toString(apisToFutureFLight)).build();

        setApisBookingService = serviceFactory.setApisBooking(new SetAPIRequest(HybrisHeaders.getValid(testData.getChannel()).build(), params, findBookingQueryParams, requestBodyApisBooking));
        testData.setData(SERVICE, setApisBookingService);
    }

    private void invokeWithError() {
        final int[] attempts = {3};
        try {
            pollingLoop().until(() -> {
                setApisBookingService.invoke();
                attempts[0]--;
                return setApisBookingService.getStatusCode() == 400 || attempts[0] == 0;
            });
        } catch (ConditionTimeoutException e) {
            if (setApisBookingService.getErrors() != null) {
                fail(setApisBookingService.getErrors().getErrors().stream().toString());
            }
        }

    }

    private void invoke() {
        final int[] attempts = {3};
        try {
            pollingLoop().until(() -> {
                setApisBookingService.invoke();
                attempts[0]--;
                return setApisBookingService.getStatusCode() == 200 || attempts[0] == 0;
            });
        } catch (ConditionTimeoutException e) {
            if (setApisBookingService.getErrors() != null) {
                fail(setApisBookingService.getErrors().getErrors().stream().toString());
            }
        }

    }

    public void invokeUpdateIdentityDocument(boolean apisToFutureFLight, String fieldToChange, String channel) {
        requestBodyApisBooking = buildDifferentRequestBody(testData.getBookingResponse().getBookingContext(), fieldToChange);
        BookingPathParams params = BookingPathParams.builder().bookingId(testData.getData(SerenityFacade.DataKeys.BOOKING_ID)).path(SET_APIS_BOOKING).passengerId(testData.getPassengerId()).build();
        FindBookingQueryParams findBookingQueryParams = FindBookingQueryParams.builder().apisToFutureFlight(Boolean.toString(apisToFutureFLight)).build();

        setApisBookingService = serviceFactory.setApisBooking(new SetAPIRequest(HybrisHeaders.getValid(
                channel).build(), params, findBookingQueryParams, requestBodyApisBooking));
        testData.setData(SERVICE, setApisBookingService);
        setApisBookingService.invoke();
    }

    public void invokeUpdateIdentityDocumentWithDuplicateDocumentNumber(boolean apisToFutureFLight, String duplicateField) {
        requestBodyApisBooking = buildDifferentRequestBody(testData.getBookingResponse().getBookingContext(), "");
        updateBody(duplicateField);
        BookingPathParams params = BookingPathParams.builder().bookingId(testData.getData(SerenityFacade.DataKeys.BOOKING_ID)).path(SET_APIS_BOOKING).passengerId(testData.getPassengerId()).build();
        FindBookingQueryParams findBookingQueryParams = FindBookingQueryParams.builder().apisToFutureFlight(Boolean.toString(apisToFutureFLight)).build();
        setApisBookingService = serviceFactory.setApisBooking(new SetAPIRequest(HybrisHeaders.getValid(testData.getChannel()).build(), params, findBookingQueryParams, requestBodyApisBooking));
        testData.setData(SERVICE, setApisBookingService);
        invokeWithError();
    }

    private void updateBody(String duplicateField) {
        if ("documentId".equals(duplicateField)) {
            requestBodyApisBooking.getApi().setDocumentNumber(testData.getDocumentId());

        } else {
            throw new RuntimeException("Invalid Field selected " + duplicateField);
        }
    }

    private static String getDateOfBirthFromAge(int age) {
        int dobMonth = 01;
        int dobDay = 01;

        LocalDate now = LocalDate.now();
        LocalDate dob = now.minusYears(age)
                .minusMonths(dobMonth)
                .minusDays(dobDay);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dob.format(formatter);
    }

    private static String getDateOfBirthFromType(String type) {

        int dobMonth = 01;
        int dobDay = 01;

        LocalDate now = LocalDate.now();
        LocalDate dob = now.minusYears(getAgeFromType(type.toLowerCase()))
                .minusMonths(dobMonth)
                .minusDays(dobDay);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dob.format(formatter);
    }

    private static int getAgeFromType(String type) {
        switch (type) {
            case ADULT:
                return 25;
            case CHILD:
                return 14;
            case INFANT:
                return 0;
            default:
                return 25;
        }
    }
}
