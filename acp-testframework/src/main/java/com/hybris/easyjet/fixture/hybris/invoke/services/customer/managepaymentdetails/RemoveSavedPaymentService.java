package com.hybris.easyjet.fixture.hybris.invoke.services.customer.managepaymentdetails;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.customer.managepaymentdetails.RemoveSavedPaymentAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.managepaymentdetails.RemoveSavedPaymentResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;
/**
 * Created by Sudhir Talluri on 04/07/2017.
 */
public class RemoveSavedPaymentService extends HybrisService implements IService {
    private  RemoveSavedPaymentResponse removeSavedPaymentResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public RemoveSavedPaymentService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public RemoveSavedPaymentResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return removeSavedPaymentResponse;
    }

    @Override
    public RemoveSavedPaymentAssertion assertThat() {
        return new RemoveSavedPaymentAssertion(removeSavedPaymentResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(removeSavedPaymentResponse.getOperationConfirmation());
    }

    @Override
    protected void mapResponse() {
        removeSavedPaymentResponse= restResponse.as(RemoveSavedPaymentResponse.class);
    }
}
