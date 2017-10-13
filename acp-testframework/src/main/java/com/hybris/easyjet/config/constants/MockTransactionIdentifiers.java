package com.hybris.easyjet.config.constants;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by vijayapalkayyam on 14/07/2017.
 */
public enum MockTransactionIdentifiers {
    FLIGHT_UNAVAILABLE("FCPH3460FLIGHTUNAVAILABLE"),
    FLIGHT_PRICE_CHANGE("FCPH3460FLIGHTPRICECHANGE"),
    SEAT_UNAVAILABLE("FCPH3460SEATUNAVAILABLE"),
    SEAT_PRICE_CHANGE("FCPH3460SEATPRICECHANGE"),
    SEAT_AT_GATE("00000000-0000-0000-0000-000000123456"),
    DEALLOCATE_SEAT("999999999999999"),
    NO_ALLOCATE_SEAT("00000000-0000-0000-0000-000000123456999");

    private String transactionId;

    public static List<String> customXClientTransactionIds;

    MockTransactionIdentifiers(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    static {
        customXClientTransactionIds = Arrays.stream(values())
                    .map(MockTransactionIdentifiers::getTransactionId)
                    .collect(toList());
        }
}