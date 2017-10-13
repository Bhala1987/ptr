package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.InternalPaymentFundsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.InternalPaymentFundsResponse;

/**
 * Created by markphipps on 12/04/2017.
 */
public class InternalPaymentFundsService extends HybrisService implements IService {
    private InternalPaymentFundsResponse internalPaymentFundsResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public InternalPaymentFundsService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public InternalPaymentFundsAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new InternalPaymentFundsAssertion(internalPaymentFundsResponse);
    }

    @Override
    public InternalPaymentFundsResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return internalPaymentFundsResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(internalPaymentFundsResponse);
    }

    @Override
    protected void mapResponse() {
        internalPaymentFundsResponse = restResponse.as(InternalPaymentFundsResponse.class);
    }
}
