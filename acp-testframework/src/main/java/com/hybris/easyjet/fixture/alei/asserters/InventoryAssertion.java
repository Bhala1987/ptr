package com.hybris.easyjet.fixture.alei.asserters;

import com.hybris.easyjet.fixture.alei.invokers.responses.Response;

/**
 * Created by giuseppedimartino on 31/01/17.
 */
public class InventoryAssertion extends Assertion<InventoryAssertion, Response> {

    public InventoryAssertion(Response inventoryResponse) {

        this.response = inventoryResponse;
    }

}
