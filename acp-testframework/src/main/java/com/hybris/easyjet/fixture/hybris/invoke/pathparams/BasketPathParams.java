package com.hybris.easyjet.fixture.hybris.invoke.pathparams;

import com.hybris.easyjet.fixture.IPathParameters;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.DEFAULT;

/**
 * Created by daniel on 28/11/2016.
 */
@Builder
@Getter
@Setter
public class BasketPathParams extends PathParameters implements IPathParameters {

    public static final String PASSENGERS = "/passengers";
    public static final String FLIGHTS = "/flights";
    public static final String HOLD_ITEMS = "/hold-items";
    public static final String SPORTS_EQUIPMENT = "/sports-equipment";
    public static final String HOLD_BAGS = "/hold-bags";
    public static final String CHANGE_FLIGHT_REQUEST = "/change-flight-request";
    public static final String PRICE_OVERRIDES = "/price-overrides";
    public static final String SEAT_PRODUCTS = "/seat-products";
    public static final String REMOVE_SEATS_REQUEST = "/remove-seats-request";
    public static final String ADDITIONAL_FARES_REQUEST = "/additional-fares-request";
    public static final String ASSOCIATE_INFANT_REQUEST = "/associate-infant-request";
    public static final String HOLD_BAG_ITEMS = "/hold-bag-items";
    public static final String CONVERT_CURRENCY_REQUEST = "/convert-currency-request";
    public static final String CHANGE_SEATS_REQUEST = "/change-seats-request";
    public static final String UPDATE_BASIC_DETAILS_REQUEST = "/update-basic-details-request";
    public static final String REMOVE_INFANT_ON_LAP_REQUEST = "/remove-infant-on-lap-request";
    public static final String ADD_SSRS_REQUEST = "/add-ssrs-request";
    public static final String UPDATE_SSRS_REQUEST = "/update-ssrs-request";
    public static final String REMOVE_SSRS_REQUEST = "/remove-ssrs-request";
    public static final String ADD_INFANT_ON_LAP_REQUEST = "/add-infant-on-lap-request";
    public static final String ADD_PASSENGER_REQUEST = "/add-passenger-request";
    public static final String CALCULATE_PAYMENT_BALANCE_REQUEST = "/calculate-payment-balance-request";
    public static final String CAR_HIRE_QUOTE_REQUEST = "/car-hire-quote-request ";
    public static final String ADD_CAR_TO_BASKET_REQUEST = "/car-hire-product ";
    public static final String SET_REASON_FOR_TRAVEL = "/booking-reason";
    public static final String COMPENSATION = "/compensation";
    public static final String GENERATE_QUOTE = "/generate-quote-request";

    private String basketId;
    private String passengerId;
    private String overrideDiscountCode;
    private String flightKey;
    private String productId;
    private String index;
    @Builder.Default
    private BasketPaths path = DEFAULT;
    private String passengerMap;

    /**
     * @return
     */
    @Override
    public String get() {
        if (!isPopulated(basketId)) {
            throw new IllegalArgumentException("You must specify a basketId for this service.");
        }

        switch (path) {
            case PASSENGER:
                if (isPopulated(passengerId)) {
                    return basketId + PASSENGERS + "/" + passengerId;
                } else {
                    return basketId + PASSENGERS;
                }
            case SPORT_EQUIP:
                if (isPopulated(productId)) {
                    return basketId + HOLD_ITEMS + SPORTS_EQUIPMENT + "/" + productId;
                } else {
                    return basketId + HOLD_ITEMS + SPORTS_EQUIPMENT;
                }
            case HOLD_BAG:
                if (isPopulated(productId)) {
                    return basketId + HOLD_ITEMS + HOLD_BAGS + "/" + productId;
                } else {
                    return basketId + HOLD_ITEMS + HOLD_BAGS;
                }
            case REMOVE_FLIGHT:
                return basketId + FLIGHTS + "/" + flightKey;
            case CHANGE_FLIGHT:
                return basketId + FLIGHTS + "/" + flightKey + CHANGE_FLIGHT_REQUEST;
            case OVERRIDE_PRICE:
                return basketId + PRICE_OVERRIDES;
            case REMOVE_OVERRIDE_PRICE:
                return basketId + PRICE_OVERRIDES + "/" + overrideDiscountCode;
            case ADD_PURCHASED_SEAT:
                return basketId + SEAT_PRODUCTS;
            case REMOVE_PURCHASED_SEAT:
                return basketId + FLIGHTS + "/" + flightKey + REMOVE_SEATS_REQUEST;
            case MANAGE_ADDITIONAL_SEAT_TO_PASSENGER:
                return basketId + PASSENGERS + "/" + passengerId + ADDITIONAL_FARES_REQUEST;
            case ASSOCIATE_INFANT:
                return basketId + PASSENGERS + "/" + passengerId + ASSOCIATE_INFANT_REQUEST;
            case EXCESS_WEIGHT:
                if (isPopulated(index)) {
                    return basketId + HOLD_ITEMS + HOLD_BAG_ITEMS + "/" + index;
                } else {
                    return basketId + HOLD_ITEMS + HOLD_BAG_ITEMS;
                }
            case CURRENCY:
                return basketId + CONVERT_CURRENCY_REQUEST;
            case CHANGE_PURCHASED_SEAT:
                return basketId + FLIGHTS + "/" + flightKey + CHANGE_SEATS_REQUEST;
            case UPDATE_BASIC_DETAILS:
                return basketId + PASSENGERS + "/" + passengerId + UPDATE_BASIC_DETAILS_REQUEST;
            case ADD_ADDITIONAL_FARE_TO_PASSENGER:
                return basketId + PASSENGERS + "/" + passengerMap + ADDITIONAL_FARES_REQUEST;
            case REMOVE_INFANT_ON_LAP:
                return basketId + PASSENGERS + "/" + passengerId + REMOVE_INFANT_ON_LAP_REQUEST;
            case ADD_PASSENGER_SSR:
                return basketId + PASSENGERS + "/" + passengerId + ADD_SSRS_REQUEST;
            case UPDATE_PASSENGER_SSR:
                return basketId + PASSENGERS + "/" + passengerId + UPDATE_SSRS_REQUEST;
            case REMOVE_PASSENGER_SSR:
                return basketId + PASSENGERS + "/" + passengerId + REMOVE_SSRS_REQUEST;
            case ADD_INFANT_ON_LAP:
                return basketId + PASSENGERS + "/" + passengerId + ADD_INFANT_ON_LAP_REQUEST;
            case ADD_PASSENGER_TO_FLIGHT:
                return basketId + ADD_PASSENGER_REQUEST;
            case SET_REASON_FOR_TRAVEL:
                return basketId + SET_REASON_FOR_TRAVEL;
            case CALCULATE_PAYMENT_BALANCE:
                return basketId + CALCULATE_PAYMENT_BALANCE_REQUEST;
            case CAR_HIRE:
                return basketId + CAR_HIRE_QUOTE_REQUEST;
            case COMPENSATION:
                return basketId + COMPENSATION;
            case GENERATE_QUOTE:
                return basketId + GENERATE_QUOTE;
            case ADD_CAR_TO_BASKET:
                return basketId + ADD_CAR_TO_BASKET_REQUEST;
            default:
                return basketId;
        }
    }

    public enum BasketPaths {
        DEFAULT,
        PASSENGER,
        SPORT_EQUIP,
        HOLD_BAG,
        REMOVE_FLIGHT,
        CHANGE_FLIGHT,
        OVERRIDE_PRICE,
        REMOVE_OVERRIDE_PRICE,
        ADD_PURCHASED_SEAT,
        REMOVE_PURCHASED_SEAT,
        ADD_ADDITIONAL_SEAT_TO_PASSENGER,
        MANAGE_ADDITIONAL_SEAT_TO_PASSENGER,
        EXCESS_WEIGHT,
        CURRENCY,
        CHANGE_PURCHASED_SEAT,
        ASSOCIATE_INFANT,
        UPDATE_BASIC_DETAILS,
        REMOVE_INFANT_ON_LAP,
        ADD_ADDITIONAL_FARE_TO_PASSENGER,
        ADD_PASSENGER_SSR,
        UPDATE_PASSENGER_SSR,
        REMOVE_PASSENGER_SSR,
        ADD_INFANT_ON_LAP,
        ADD_PASSENGER_TO_FLIGHT,
        ADD_SSR_TO_PASSENGER,
        SET_REASON_FOR_TRAVEL,
        CALCULATE_PAYMENT_BALANCE,
        CAR_HIRE,
        COMPENSATION,
        GENERATE_QUOTE,
        ADD_CAR_TO_BASKET
    }
}
