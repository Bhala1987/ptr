package com.hybris.easyjet.fixture.hybris.invoke.services.customer.managepaymentdetails;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.customer.managepaymentdetails.SavedPaymentMethodAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.managepaymentdetails.SavedPaymentMethodResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by giuseppecioce on 21/06/2017.
 */
public class SavedPaymentMethodService extends HybrisService implements IService {
    private SavedPaymentMethodResponse savedPaymentMethodResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public SavedPaymentMethodService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public SavedPaymentMethodResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return savedPaymentMethodResponse;
    }

    @Override
    public SavedPaymentMethodAssertion assertThat() {
        return new SavedPaymentMethodAssertion(savedPaymentMethodResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(savedPaymentMethodResponse.getOperationConfirmation());
    }

    @Override
    protected void mapResponse() {
        savedPaymentMethodResponse = restResponse.as(SavedPaymentMethodResponse.class);
    }
}
