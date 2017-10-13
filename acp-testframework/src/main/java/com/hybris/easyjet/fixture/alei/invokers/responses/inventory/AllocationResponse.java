package com.hybris.easyjet.fixture.alei.invokers.responses.inventory;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giuseppedimartino on 21/04/17.
 */
@Getter
@Setter
public class AllocationResponse extends InventoryResponse<AllocationResponse.AllocationResult> {

    @Getter
    @Setter
    public static class AllocationResult extends InventoryResponse.Result {
        public String baseCurrencyCode;
        public Boolean requestedPriceMet;
        public List<AllocationFare> allocatedFares = new ArrayList<>();
        public List<Price> allocatedPrices = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class AllocationFare {
        public String fareClass;
        public Integer numberAllocated;
    }

    @Getter
    @Setter
    public static class Price {
        public String passengerType;
        public Double price;
    }

}