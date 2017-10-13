package com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory;

import com.hybris.easyjet.fixture.hybris.helpers.FlightPassengers;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.*;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.fluttercode.datafactory.impl.DataFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by giuseppecioce on 06/03/2017.
 */
@ToString
@Component
public class BasketHoldItemsFactory {

    private static final DataFactory df = new DataFactory();
    public static int defaultHoldItemQuantity = 0;
    private static Logger LOG = LogManager.getLogger(SavedPassengerFactory.class);
    private static Random random = new Random(System.currentTimeMillis());

    private static boolean set(Object object, String fieldName, Object fieldValue) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true); // You might want to set modifier to public first.
                if (field.getName().equals(fieldName)) {
                    try {
                        Field fieldx = clazz.getDeclaredField(fieldName);
                        fieldx.setAccessible(true);
                        fieldx.set(object, fieldValue);
                        return true;
                    } catch (NoSuchFieldException e) {
                        LOG.error(e);
                        clazz = clazz.getSuperclass();
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        }
        return false;
    }

    private static String getRandomString(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }

    public static AddSportEquipmentRequestBody aBasicHoldItemsSport() {
        return AddSportEquipmentRequestBody.builder()
                .productCode("")
                .quantity(0)
                .passengerCode("")
                .flightKey("")
                .override(false)
                .build();
    }

    public static AddSportEquipmentRequestBody missingFieldHoldItemsSport(String field) {
        AddSportEquipmentRequestBody request = aBasicHoldItemsSport();
        switch (field) {
            case "productCode":
            case "passengerCode":
            case "flightKey":
            case "override":
                set(request, field, null);
                break;
            case "quantity":
                set(request, field, -1);
                break;
            default:
                break;
        }
        return request;
    }

    public static AddHoldItemsRequestBody missingFieldHoldItemsSport(String field, AddHoldItemsRequestBody request) {
        switch (field) {
            case "productCode":
            case "passengerCode":
            case "flightKey":
            case "override":
                set(request, field, null);
                break;
            case "quantity":
                set(request, field, -1);
                break;
            default:
                break;
        }
        return request;
    }

    public static AddHoldItemsRequestBody aBasicHoldItems() {
        return AddHoldItemsRequestBody.builder()
                .productCode("")
                .quantity(0)
                .excessWeightProductCode("")
                .excessWeightQuantity(0)
                .passengerCode("")
                .flightKey("")
                .override(false)
                .build();
    }

    public static AddHoldItemsRequestBody aHoldItems() {
        return AddHoldItemsRequestBody.builder()
                .build();
    }


    public static AddHoldItemsRequestBody aBasicHoldItems(String qty, String paxCode) {
        return AddHoldItemsRequestBody.builder()
                .productCode("holdbag20kg")
                .quantity(Integer.valueOf(qty))
                .excessWeightProductCode("")
                .excessWeightQuantity(0)
                .passengerCode(paxCode)
                .flightKey("")
                .override(false)
                .build();
    }

    public static AddHoldItemsRequestBody addHoldItemsRequestBody(String flightKey, String passengerCode, String holdItemProductCode, String excessWeightProductType, Integer excessWeightQuantity) {
        return AddHoldItemsRequestBody.builder()
                .productCode(holdItemProductCode)
                .excessWeightProductCode(excessWeightProductType)
                .excessWeightQuantity(excessWeightQuantity)
                .passengerCode(passengerCode)
                .flightKey(flightKey)
                .override(false)
                .build();
    }

    public static AddHoldItemsRequestBody missingFieldHoldItems(String field, AddHoldItemsRequestBody request) {
        switch (field) {
            case "productCode":
            case "excessWeightProductCode":
            case "excessWeightQuantity":
            case "passengerCode":
            case "flightKey":
            case "override":
                set(request, field, null);
                break;
            case "quantity":
                set(request, field, -1);
                break;
            default:
                break;
        }
        return request;
    }

    /**
     * The method build a request body for add flith to basket
     *
     * @param flightKey
     * @param currency
     * @param bundle       possible value are Standard, Flexi
     * @param journeyType
     * @param passengerMix
     * @return
     */
    public static AddFlightRequestBody aBasicAddFlightToBasket(String flightKey, String currency, String bundle, String journeyType, String passengerMix) {
        FlightPassengers passengers = new FlightPassengers(passengerMix);
        return AddFlightRequestBody.builder()
                .flights(new ArrayList<>(Arrays.asList(
                        Flight.builder().
                                flightKey(flightKey)
                                .sector(getSector(flightKey))
                                .flightPrice(new Double(200.0))
                                .build()
                )))
                .toeiCode("ABC")
                .overrideWarning(false)
                .routePrice(new Double(200.0))
                .currency(currency)
                .routeCode(getSector(flightKey))
                .fareType(bundle)
                .bookingType("STANDARD_CUSTOMER")
                .journeyType(journeyType)
                .passengers(passengers.getPassengers())
                .build();
    }

    private static String getSector(String flightKey) {
        return flightKey.substring(8, 14);
    }

    public static PriceOverrideRequestBody aBasicOverridePriceBasketLevel(String reasonCode) {
        return PriceOverrideRequestBody.builder()
                .reasonCode(reasonCode)
                .overrideTotalAmount(5.00)
                .comment("")
                .feeChargeCode("")
                .passengerCode("")
                .productCode("")
                .isApplicableToAllPassengers(false)
                .build();
    }

    public static PriceOverrideRequestBody missingFieldOverridePrice(String field, PriceOverrideRequestBody request) {
        switch (field) {
            case "passengerCode":
            case "isApplicableToAllPassengers":
            case "productCode":
            case "reasonCode":
            case "comment":
            case "feeChargeCode":
                set(request, field, null);
                break;
            case "overrideTotalAmount":
                set(request, field, -1);
                break;
            default:
                break;
        }
        return request;
    }


}
