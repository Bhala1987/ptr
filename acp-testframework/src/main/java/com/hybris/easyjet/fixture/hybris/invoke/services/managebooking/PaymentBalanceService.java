package com.hybris.easyjet.fixture.hybris.invoke.services.managebooking;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.PaymentBalanceAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.managebooking.PaymentBalanceResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by giuseppecioce on 27/07/2017.
 */
public class PaymentBalanceService extends HybrisService implements IService {

    private PaymentBalanceResponse paymentBalanceResponse;
    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public PaymentBalanceService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public PaymentBalanceResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return paymentBalanceResponse;
    }

    @Override
    public PaymentBalanceAssertion assertThat() {
        return new PaymentBalanceAssertion(paymentBalanceResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(paymentBalanceResponse);
    }

    @Override
    protected void mapResponse() {
        paymentBalanceResponse = restResponse.as(PaymentBalanceResponse.class);
    }
}
