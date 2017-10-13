package com.hybris.easyjet.config.constants;

/**
 * Created by antonellospiggia on 27/02/2017.
 */
public class FlightInterestConstants {
    public static final String INVALID_FLIGHTKEY = "INVALID_FLIGHTKEY";

    public static final String EXPECTED_HREF_PATTERN = "/commercial/v1/customers/CUSTOMERID/seatmap-interests";

    public static final String STANDBY_FARE = "Standby";
    public static final String STAFF_FARE = "Staff";
    public static final String INVALID_FARE = "INVALID_FARE";

    public static final String LEGAL_FARE_TYPE = "Staff";

    public static final String TEST_AIRPORT_CODE = "LTN";
    public static final String CHECKIN_TIME = "1";

    public static final String DIGITAL_CHANNEL = "Digital";
    public static final String AD_CHANNEL = "ADAirport";


    public static final String FARE_TYPE_ERROR_CODE = "SVC_100050_2019";
    public static final String FARE_TYPE_ERROR_MESSAGE = "Invalid fare code.";
    public static final String TIME_LIMIT_ERROR_CODE = "SVC_100050_2016";
    public static final String TIME_LIMIT_ERROR_MESSAGE = "Requested seatmap interest cannot be registered. Flight too close to departure.";
    public static final String MAX_REGISTERED_ERROR_CODE = "SVC_100050_2017";
    public static final String MAX_REGISTERED_ERROR_MESSAGE = "Requested seatmap interest cannot be registered. Exceeded maximum capacity.";
    public static final String DUPLICATED_ERROR_CODE = "SVC_100050_2020";
    public static final String DUPLICATED_ERROR_MESSAGE = "Requested seatmap interest already exists";
    public static final String UNSUPPORTED_CHANNEL_ERROR_CODE = "SVC_100050_2022";
    public static final String UNSUPPORTED_CHANNEL_ERROR_MESSAGE = "Incorrect Channel";
    public static final String INVALID_FLIGHT_KEY_ERROR_CODE = "SVC_100050_2021";
    public static final String INVALID_FLIGHT_KEY_ERROR_MESSAGE = "Flight key not found.";

    public static final String CANCELLED_FLIGHT_IN_BASKET_ERROR = "SVC_100012_3001";
    public static final String OTHER_FLIGHT_IN_BASKET_CONNECTION_WARNING = "SVC_100012_3002";
    public static final String INFANTS_ON_OWN_SEATS_LIMIT_WARNING = "SVC_100012_3006";
    public static final String OTHER_FLIGHT_CONNECTION_TIME_WARNING = "SVC_100012_3005";
}
