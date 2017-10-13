package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.PriceOverrideAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.PriceOverrideBasketResponse;

/**
 * Created by giuseppecioce on 29/03/2017.
 */
public class PriceOverrideBasketService extends HybrisService implements IService {
    private PriceOverrideBasketResponse priceOverrideBasketResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    protected PriceOverrideBasketService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public PriceOverrideBasketResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return priceOverrideBasketResponse;
    }

    @Override
    public PriceOverrideAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new PriceOverrideAssertion(priceOverrideBasketResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(priceOverrideBasketResponse.getOperationConfirmation());
    }

    @Override
    protected void mapResponse() {
        priceOverrideBasketResponse = restResponse.as(PriceOverrideBasketResponse.class);
    }

    public boolean getSuccessful() {
        return successful;
    }
}
