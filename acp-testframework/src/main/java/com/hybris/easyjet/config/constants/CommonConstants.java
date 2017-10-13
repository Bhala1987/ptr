package com.hybris.easyjet.config.constants;

/**
 * Created by tejaldudhale on 23/06/2017.
 */
public class CommonConstants {
    //*****CHANNEL*****
    public static final String DIGITAL_CHANNEL = "Digital";
    public static final String PUBLIC_API_MOBILE_CHANNEL = "PublicApiMobile";
    public static final String PUBLIC_API_B2B_CHANNEL = "PublicApiB2B";
    public static final String AD_CHANNEL = "ADAirport";
    public static final String AD_CUSTOMER_SERVICE = "ADCustomerService";
    //*****JOURNEY TYPE*****
    public static final String OUTBOUND = "outbound";
    public static final String INBOUND = "inbound";
    public static final String SINGLE = "single";
    public static final String OUTBOUND_INBOUND  = "outbound/inbound";
    //*****PRODUCT*****
    public static final String FARE_PRODUCT = "FARE_PRODUCT";
    public static final String INFANTONLAP_PRODUCT = "InfantOnLapProduct";
    //*****FARE TYPE*****
    public static final String STANDARD = "Standard";
    public static final String FLEXI = "Flexi";
    public static final String STAFF = "Staff";
    public static final String STANDBY = "Standby";
    //*****BOOKING TYPE*****
    public static final String BUSINESS = "BUSINESS";
    //*****NAME ATTRIBUTES*****
    public static final String FIRSTNAME = "firstname";
    public static final String LASTNAME = "lastname";
    public static final String TITLE = "title";
    public static final String MIDDLENAME = "middlename";
    //*****FLIGHT STATUS*****
    public static final String AVAILABLE = "AVAILABLE";
    //*****COMMON*****
    public static final String SHOULD_NOT = "should not";
    public static final String SHOULD = "should";
    public static final String DEPARTURE_THRESHOLD_NAME_CHANGE = "thresholdForNameChangeBasedOnDeparture";
    public static final String ONE = "1";
    public static final String INCORRECT = "incorrect";
    public static final String INVALID = "invalid";
    public static final String COMPLETED = "COMPLETED";
    //*****PAYMENT-TYPES*****
    public static final String COMBINATION = "combination";
    public static final String DEBIT = "debit";
    public static final String CREDIT = "credit";
    public static final String CREDITFILEFUND = "creditfilefund";
    public static final String CREDITFILE = "creditfile";
    public static final String ELV = "elv";
    public static final String CASH = "cash";
    //*****PAYMENT-RELATED*****
    public static final String REFUND = "refund";
    public static final String CARD = "card";
    public static final String CREDITFILEFUNDREFUND = "CREDITFILEFUND";
    public static final String CARDREFUND = "CARD";
    //*****PASSENGER*****
    public static final String ONE_ADULT = "1 Adult";
    //*****ERROR MESSAGE*****
    public static final String SEATMAP_ERROR = "Error getting a valid seat map";
    public static final String NO_SEAT_AVAILABLE = "No seat are available from seating service for the desired type ";
    //*****PASSENGER TYPE******
    public static final String ADULT = "adult";
    public static final String CHILD = "child";
    public static final String INFANT = "infant";
    public static final String INFANT_ON_LAP = "InfantOnLap";
    //*****CURRENCY*****
    public static final String GBP = "GBP";
    //*****TAG******
    public static final String NEGATIVE_SCENARIO = "@negative";
    //allowed cap for oubound date from current date
    public static final int NUMBER_OF_HOURS = 2;
//    *****CREDIT CARD FEE ******
    public static final double CREDITCARD_FEE_PERCENTAGE = 0.05;

    public enum BookingType {
        ;
        public static final String BUSINESS = "BUSINESS";
        public static final String STAFF = "STAFF";
        public static final String STANDARD_CUSTOMER = "STANDARD_CUSTOMER";
    }

    public enum JSONSCHEMAS {
        ;
        public static final String BOOKING = "CreateBooking";
        public static final String UPDATE_BOOKING = "UpdateBooking";
        public static final String CREATE_CUSTOMER = "CreateCustomer";
        public static final String UPDATE_CUSTOMER = "UpdateCustomer";
        public static final String BOOKING_CANCELLED = "BookingCancelled";
        public static final String BOOKING_COMMENT = "BookingCommentUpdated";
        public static final String CUSTOMER_COMMENT = "CustomerCommentUpdated";
    }

}
