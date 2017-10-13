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
public class DeallocationResponse extends InventoryResponse<DeallocationResponse.DeallocationResult> {

    @Getter
    @Setter
    public static class DeallocationResult extends InventoryResponse.Result {
        public List<DeallocationFare> deallocatedFares = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class DeallocationFare {
        public String fareClass;
        public Integer numberDeallocated;
    }

}