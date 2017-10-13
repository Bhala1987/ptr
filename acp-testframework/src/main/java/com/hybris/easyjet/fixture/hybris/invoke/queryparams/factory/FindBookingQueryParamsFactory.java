package com.hybris.easyjet.fixture.hybris.invoke.queryparams.factory;

import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.CaseConverter;
import com.hybris.easyjet.fixture.hybris.helpers.DateFormat;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.FindBookingQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hybris.easyjet.exceptions.EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INSUFFICIENT_DATA;
import static com.hybris.easyjet.fixture.hybris.helpers.DateFormat.getDate;

/**
 * Created by daniel on 28/11/2016.
 */
public class FindBookingQueryParamsFactory {

    private static final String SEARCH_IN_PAX = "searchInPax";
    private static final String SEARCH_IN_BOOKER = "searchInBooker";
    private static final String TRAVELLER = "traveller";
    private static final String BOOKER = "booker";
    private static final String LASTNAME = "lastname";
    private static final String FIRSTNAME = "firstname";
    private static final String EMAIL = "email";
    private static final String BOOKINGREF = "bookingreference";
    private static final String TITLE = "title";
    private static final String TRAVEL_FROM_DATE = "travelfromdate";
    private static final String TRAVEL_TO_DATE = "traveltodate";
    private static final String DOB = "dob";
    private static final String TRAVEL_DOC_TYPE = "traveldocumenttype";
    private static final String TRAVEL_DOC_NUMBER = "traveldocumentnumber";
    private static final String REFERENCE_NUMBER = "referenceNumber";
    private static final String POSTCODE = "postcode";

    private static boolean set(Object object, String fieldName, Object fieldValue) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(object, fieldValue);
                return true;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return false;
    }

    private static String getValue(GetBookingResponse booking, String param) throws EasyjetCompromisedException {

        List<GetBookingResponse.Flight> flights = booking.getBookingContext().getBooking().getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream())
                .collect(Collectors.toList());

        if (booking.getBookingContext().getBooking().getInbounds() != null) {
            flights.addAll(booking.getBookingContext().getBooking().getInbounds().stream()
                    .flatMap(f -> f.getFlights().stream())
                    .collect(Collectors.toList()));
        }

        GetBookingResponse.Flight firstFlight = flights.stream().findFirst().get();

        Optional<GetBookingResponse.Passenger> passenger = flights.stream()
                .flatMap(p -> p.getPassengers().stream())
                .findFirst();
        if (passenger.isPresent()) {

            try {
                switch (param.toLowerCase()) {
                    case LASTNAME:
                        return passenger.get().getPassengerDetails().getName().getLastName();
                    case FIRSTNAME:
                        return passenger.get().getPassengerDetails().getName().getFirstName();
                    case EMAIL:
                        return passenger.get().getPassengerDetails().getEmail();
                    case BOOKINGREF:
                        return booking.getBookingContext().getBooking().getBookingReference();
                    case TITLE:
                        return passenger.get().getPassengerDetails().getName().getTitle();
                    case TRAVEL_FROM_DATE:
                        return new DateFormat().withDate(firstFlight.getDepartureDateTime()).asYearMonthDay();
                    case TRAVEL_TO_DATE:
                        return new DateFormat().withDate(firstFlight.getDepartureDateTime()).asYearMonthDay();
                    case DOB:
                        return passenger.get().getPassengerAPIS().getDateOfBirth();
                    case TRAVEL_DOC_TYPE:
                        return passenger.get().getPassengerAPIS().getDocumentType();
                    case TRAVEL_DOC_NUMBER:
                        return passenger.get().getPassengerAPIS().getDocumentNumber();
                    default:
                        return null;

                }
            } catch (Exception ex) {
                throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
            }
        }
        else{
            return null;
        }
    }

    private static String getValueBasedOnCustomer(GetBookingResponse booking, String param) throws EasyjetCompromisedException {
        try {
            switch (param.toLowerCase()) {
                case LASTNAME:
                    return booking.getBookingContext().getBooking().getBookingContact().getName().getLastName();
                case FIRSTNAME:
                    return booking.getBookingContext().getBooking().getBookingContact().getName().getFirstName();
                case EMAIL:
                    return booking.getBookingContext().getBooking().getBookingContact().getEmailAddress();
                case REFERENCE_NUMBER:
                    return booking.getBookingContext().getBooking().getBookingReference();
                case TITLE:
                    return booking.getBookingContext().getBooking().getBookingContact().getName().getTitle();
                case TRAVEL_FROM_DATE:
                    if (booking.getBookingContext().getBooking().getOutbounds() != null) {
                        return new DateFormat().withDate(booking.getBookingContext().getBooking().getOutbounds().get(0).getFlights().get(0).getDepartureDateTime()).asYearMonthDay();
                    } else
                        break;
                case TRAVEL_TO_DATE:
                    if (booking.getBookingContext().getBooking().getOutbounds() != null) {
                        return new DateFormat().withDate(booking.getBookingContext().getBooking().getOutbounds().get(0).getFlights().get(0).getDepartureDateTime()).asYearMonthDay();
                    } else
                        break;
                case POSTCODE:
                    return booking.getBookingContext().getBooking().getBookingContact().getAddress().getPostalCode();
            }
        } catch (Exception ex) {
            throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
        }
        return null;
    }

    public static FindBookingQueryParams Basic_FindBookingParams(GetBookingResponse booking, boolean searchInPassenger) throws EasyjetCompromisedException {
        FindBookingQueryParams criteria = FindBookingQueryParams.builder().build();
        if (searchInPassenger) {
            criteria.setLastName(getValue(booking, LASTNAME));
            criteria.setFirstName(getValue(booking, FIRSTNAME));
//            criteria.setEmail(getValue(booking, EMAIL));
            criteria.setReferenceNumber(getValue(booking, REFERENCE_NUMBER));
            criteria.setTitle(getValue(booking, TITLE).toLowerCase());
            criteria.setTravelFromDate(getValue(booking, TRAVEL_FROM_DATE));
            criteria.setSearchInPax("true");
        } else {
            criteria.setLastName(getValueBasedOnCustomer(booking, LASTNAME));
            criteria.setFirstName(getValueBasedOnCustomer(booking, FIRSTNAME));
//            criteria.setEmail(getValueBasedOnCustomer(booking, EMAIL));
            criteria.setReferenceNumber(getValueBasedOnCustomer(booking, REFERENCE_NUMBER));
            criteria.setTitle(getValueBasedOnCustomer(booking, TITLE).toLowerCase());
            criteria.setTravelFromDate(getValueBasedOnCustomer(booking, TRAVEL_FROM_DATE));
            criteria.setSearchInBooker("true");
        }
        return criteria;
    }
    public static FindBookingQueryParams Basic_FindBookingParams(boolean searchInPassenger) throws EasyjetCompromisedException {
        FindBookingQueryParams criteria = FindBookingQueryParams.builder().build();
        if (searchInPassenger) {
            criteria.setSearchInPax("true");
        } else {
            criteria.setSearchInBooker("true");
        }
        return criteria;
    }
    public static FindBookingQueryParams Basic_FindBookingParamsWithDates(GetBookingResponse booking, boolean searchInPassenger) throws EasyjetCompromisedException {
        FindBookingQueryParams criteria = Basic_FindBookingParams(searchInPassenger);
        if (searchInPassenger) {
            criteria.setTravelFromDate(getValue(booking, TRAVEL_FROM_DATE));
        } else {
            criteria.setTravelFromDate(getValueBasedOnCustomer(booking, TRAVEL_FROM_DATE));
        }
        return criteria;
    }

    public static FindBookingQueryParams EmptyTheField(String fields, FindBookingQueryParams criteria) {
        List<String> parameters = Arrays.asList(fields.split("\\s*,\\s*"));
        for (String param : parameters) {
            set(criteria, param, null);
        }
        return criteria;
    }

    public static FindBookingQueryParams SetBookingParams(GetBookingResponse booking, String params, boolean searchInPassenger) throws EasyjetCompromisedException {
        FindBookingQueryParams criteria = FindBookingQueryParams.builder().build();

        List<String> parameters = Arrays.asList(params.split("\\s*,\\s*"));
        for (String param : parameters) {
            if (searchInPassenger){
                set(criteria, param, getValue(booking, param));
                set(criteria, SEARCH_IN_PAX, "true");
            }
            else
                set(criteria, param, getValueBasedOnCustomer(booking, param));
        }       set(criteria, SEARCH_IN_BOOKER, "true");
        return criteria;
    }

    public static FindBookingQueryParams SetBookingParams(GetBookingResponse booking, String params, boolean searchInPassenger, boolean searchInBooker) throws EasyjetCompromisedException {
        FindBookingQueryParams criteria = FindBookingQueryParams.builder().build();
        String type;
        String param;
        List<String> parameters = Arrays.asList(params.split("\\s*,\\s*"));
        for (String field : parameters) {
            if(field.contains("-")){
                param = field.split("-")[0];
                type = field.split("-")[1];
            }
            else{
                param = field;
                type = TRAVELLER;
            }

            if (type.equals(BOOKER)){
                set(criteria, param, getValueBasedOnCustomer(booking, param));
            }
            else
                set(criteria, param, getValue(booking, param));
        }
        set(criteria, SEARCH_IN_PAX, String.valueOf(searchInPassenger));
        set(criteria, SEARCH_IN_BOOKER, String.valueOf(searchInBooker));

        return criteria;
    }

    public static FindBookingQueryParams SetBookingParams(GetBookingResponse booking, String field, String value, boolean searchInPassenger) throws EasyjetCompromisedException {
        FindBookingQueryParams criteria = FindBookingQueryParams.builder().build();

            if (searchInPassenger){
                set(criteria, field, value);
            }
            else
                set(criteria, field, value);

        set(criteria, SEARCH_IN_PAX, String.valueOf(searchInPassenger));
        return criteria;
    }

    public static FindBookingQueryParams BookingWithTravelDateAsSearchCriteria(String params) {
        //get all booking refs
        //create random data for the provided paramaters
        //return the invalid criteria
        FindBookingQueryParams criteria = FindBookingQueryParams.builder().build();
        List<String> parameters = Arrays.asList(params.split("\\s*,\\s*"));
        for (String param : parameters) {
            set(criteria, param, travelDateAsRangeSearchCriterion(param));
        }
        return criteria;
    }

    public static FindBookingQueryParams BookingWithInvalidTravelDateAsSearchCriteria(String params, boolean searchInPassenger) throws ParseException {
        //get all booking refs
        //create random data for the provided paramaters
        //return the invalid criteria

        FindBookingQueryParams criteria = FindBookingQueryParams.builder().build();
        List<String> parameters = Arrays.asList(params.split("\\s*,\\s*"));
        for (String param : parameters) {
            if (searchInPassenger) {
                set(criteria, param, invalidTravelDateAsRangeSearchCriterion(param));
            }
        }
        set(criteria, SEARCH_IN_PAX, String.valueOf(searchInPassenger));
        return criteria;
    }

    public static FindBookingQueryParams SetBookingParams(GetBookingResponse booking, String params) throws EasyjetCompromisedException {
        FindBookingQueryParams criteria = FindBookingQueryParams.builder().build();
        List<String> parameters = Arrays.asList(params.split("\\s*,\\s*"));
        for (String param : parameters) {
            set(criteria, param, getValue(booking, param));
        }

        return criteria;
    }

    public static FindBookingQueryParams SetBookingParams(GetBookingResponse booking, String param, int charcount) throws EasyjetCompromisedException {
        FindBookingQueryParams criteria = FindBookingQueryParams.builder().build();
        set(criteria, param, getValue(booking, param));
        set(criteria, FIRSTNAME, getValue(booking, FIRSTNAME).substring(0, charcount));
        return criteria;
    }

    public static FindBookingQueryParams SetBookingParams(GetBookingResponse booking, String params, String caseFormat) {
        FindBookingQueryParams criteria = FindBookingQueryParams.builder().build();
        List<String> parameters = Arrays.asList(params.split("\\s*,\\s*"));
        CaseConverter c = new CaseConverter();
        for (String param : parameters) {
            try {
                set(criteria, param, c.convert(getValue(booking, param), caseFormat));
            } catch (Exception ex) {
                //TODO what is this try/catch for?
            }
        }
        return criteria;
    }

    public static FindBookingQueryParams invalidBooking() {
        //get existing bookings
        //create random bookingreference
        //check it is not in the list
        //return created criteria
        FindBookingQueryParams criteria = FindBookingQueryParams.builder().build();
        set(criteria, BOOKINGREF, InvalidBookingRef());
        return criteria;
    }

    private static String InvalidBookingRef() {
        //TODO: look up to check this is in fact, invalid
        return "93287hlkshedf";
    }

    public static FindBookingQueryParams BookingWithUnmatchableSearchCriteria(String params) {
        //get all booking refs
        //create random data for the provided paramaters
        //return the invalid criteria
        FindBookingQueryParams criteria = FindBookingQueryParams.builder().build();
        List<String> parameters = Arrays.asList(params.split("\\s*,\\s*"));
        for (String param : parameters) {
            set(criteria, param, UnmatchableSearchCriterion(param));
        }
        return criteria;
    }

    public static FindBookingQueryParams BookingWithInvalidEmailIdAsSearchCriteria() {
        FindBookingQueryParams criteria = FindBookingQueryParams.builder().build();
        set(criteria, "email", InvalidEmailID());
        return criteria;
    }

    private static String UnmatchableSearchCriterion(String param) {
        //TODO: actually search and make sure it is unique!!
        switch (param.toLowerCase()) {
            case TRAVEL_TO_DATE:
                return com.hybris.easyjet.fixture.hybris.helpers.DateFormat.getDate(-10);
            case TRAVEL_FROM_DATE:
                return getDate(-50);
            case EMAIL:
                return "testneverexists@easyjet.com";
            default:
                return "a";
        }
    }

    private static String travelDateAsRangeSearchCriterion(String param) {
        //TODO: actually search and make sure it is unique!!
        switch (param.toLowerCase()) {
            case TRAVEL_TO_DATE:
                return com.hybris.easyjet.fixture.hybris.helpers.DateFormat.getDate(3);
            case TRAVEL_FROM_DATE:
                return com.hybris.easyjet.fixture.hybris.helpers.DateFormat.getDate(0);
            default:
                return null;
        }
    }

    private static String invalidTravelDateAsRangeSearchCriterion(String param) throws ParseException {
        switch (param.toLowerCase()) {
            case TRAVEL_TO_DATE:
                return (new DateFormat().today().addDay(1));
            case TRAVEL_FROM_DATE:
                return (new DateFormat().today().addDay(3));
            default:
                return null;
        }
    }


    private static String InvalidEmailID() {
        return "tejal.dud";
    }

    public static FindBookingQueryParams searchWithOnlyThisParameter(String parameter, String value) {
        FindBookingQueryParams criteria = FindBookingQueryParams.builder().build();
        criteria.setParameter(parameter, value);
        return criteria;
    }
}
