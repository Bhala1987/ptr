package com.hybris.easyjet.fixture.alei.invokers.services.impl;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.alei.asserters.InventoryAssertion;
import com.hybris.easyjet.fixture.alei.invokers.responses.inventory.AllocationResponse;
import com.hybris.easyjet.fixture.alei.invokers.responses.inventory.DeallocationResponse;
import com.hybris.easyjet.fixture.alei.invokers.responses.inventory.InventoryResponse;
import com.hybris.easyjet.fixture.alei.invokers.services.AbstractService;

/**
 * Created by giuseppedimartino on 31/01/17.
 */
public class ALInventoryService extends AbstractService {

    private InventoryResponse inventoryResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    public ALInventoryService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public InventoryResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return inventoryResponse;
    }

    @Override
    public InventoryAssertion assertThat() {
        return new InventoryAssertion(inventoryResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(inventoryResponse.getResults());
    }

    @Override
    protected void mapResponse() {
        if ("allocations".equals(this.request.getPathParameters().get())) {
            inventoryResponse = restResponse.as(AllocationResponse.class);
        }
        else if ("deallocations".equals(this.request.getPathParameters().get())) {
            inventoryResponse = restResponse.as(DeallocationResponse.class);
        }
    }
}
