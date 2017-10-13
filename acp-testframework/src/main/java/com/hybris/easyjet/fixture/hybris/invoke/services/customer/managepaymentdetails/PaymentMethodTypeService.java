package com.hybris.easyjet.fixture.hybris.invoke.services.customer.managepaymentdetails;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.customer.managepaymentdetails.PaymentMethodTypeAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.managepaymentdetails.PaymentMethodTypeResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by Niyi Falade on 19/07/17.
 */
public class PaymentMethodTypeService  extends HybrisService implements IService {
    private PaymentMethodTypeResponse paymentMethodTypeResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public PaymentMethodTypeService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(paymentMethodTypeResponse);

    }

    @Override
    protected void mapResponse() {paymentMethodTypeResponse = restResponse.as(PaymentMethodTypeResponse.class);   }

    @Override
    public PaymentMethodTypeResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return paymentMethodTypeResponse;
    }

    @Override
    public PaymentMethodTypeAssertion assertThat() {
        return new PaymentMethodTypeAssertion(paymentMethodTypeResponse);
    }
}
