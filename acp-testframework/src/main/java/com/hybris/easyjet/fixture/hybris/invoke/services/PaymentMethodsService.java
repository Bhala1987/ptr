package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.PaymentMethodsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.PaymentMethodsResponse;

/**
 * Created by daniel on 26/11/2016.
 */
public class PaymentMethodsService extends HybrisService implements IService {

    private PaymentMethodsResponse paymentMethodsResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    protected PaymentMethodsService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public PaymentMethodsAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new PaymentMethodsAssertion(paymentMethodsResponse);
    }

    @Override
    public PaymentMethodsResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return paymentMethodsResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(paymentMethodsResponse.getPaymentMethods());
    }

    @Override
    protected void mapResponse() {
        paymentMethodsResponse = restResponse.as(PaymentMethodsResponse.class);
    }
}
