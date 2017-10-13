package com.hybris.easyjet.fixture.alei.invokers.requestbodies;

import lombok.Builder;

import java.util.List;

/**
 * Created by giuseppedimartino on 24/04/17.
 */
public class DeallocateInventoryRequestBody extends InventoryRequestBody<DeallocateInventoryRequestBody.Fare> {

    @Builder
    public DeallocateInventoryRequestBody(String uniqueIdentifier, List<Fare> fares) {
        super(uniqueIdentifier, fares);
    }

    @Builder
    public static class Fare extends InventoryRequestBody.Fare {
        public String flightKey;
        public String fareType;
        public List<DeallocateFare> DeallocateFares;
        public Integer numberOfInfants;
    }

    @Builder
    public static class DeallocateFare {
        public String fareClass;
        public int numberRequired;
    }

}