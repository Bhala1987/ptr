package com.hybris.easyjet.fixture.alei.invokers.requestbodies;

import lombok.Builder;

import java.util.List;

/**
 * Created by giuseppedimartino on 24/04/17.
 */
public class AllocateInventoryRequestBody extends InventoryRequestBody<AllocateInventoryRequestBody.Fare> {

    @Builder
    private AllocateInventoryRequestBody(String uniqueIdentifier, List<Fare> fares) {
        super(uniqueIdentifier, fares);
    }

    @Builder
    public static class Fare extends InventoryRequestBody.Fare {
        public String flightKey;
        public String fareType;
        public String baseCurrencyCode;
        public List<RequestedPrice> requestedPrices;
        public List<Passenger> passengerMix;
        public Integer numberOfInfants;
    }

    @Builder
    public static class RequestedPrice {
        public String passengerType;
        public Double price;
    }

    @Builder
    public static class Passenger {
        public String passengerType;
        public Integer numberRequired;
    }
}