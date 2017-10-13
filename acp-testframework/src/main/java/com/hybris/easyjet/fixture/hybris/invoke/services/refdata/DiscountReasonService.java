package com.hybris.easyjet.fixture.hybris.invoke.services.refdata;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.DiscountReasonAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.DiscountReasonResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by giuseppecioce on 31/03/2017.
 */
public class DiscountReasonService extends HybrisService implements IService {

    private DiscountReasonResponse discountReasonResponse;

    public DiscountReasonService(IRequest iRequest, String endpoint) {
        super(iRequest, endpoint);
    }

    @Override
    public DiscountReasonAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new DiscountReasonAssertion(discountReasonResponse);
    }

    @Override
    public DiscountReasonResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return discountReasonResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(discountReasonResponse.getDiscountReasons());
    }

    @Override
    protected void mapResponse() {
        discountReasonResponse = restResponse.as(DiscountReasonResponse.class);
    }
}
