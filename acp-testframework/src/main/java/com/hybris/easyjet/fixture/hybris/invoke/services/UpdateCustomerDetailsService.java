package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.UpdateCustomerDetailsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation.UpdateConfirmationResponse;

/**
 * Created by giuseppedimartino on 17/02/17.
 */
public class UpdateCustomerDetailsService extends HybrisService implements IService {


    private UpdateConfirmationResponse updateConfirmationResponse;

    /**
     * a service comprises a request, a client and an endpoint
     *
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    protected UpdateCustomerDetailsService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public UpdateConfirmationResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return updateConfirmationResponse;
    }

    @Override
    public UpdateCustomerDetailsAssertion assertThat() {
        return new UpdateCustomerDetailsAssertion(updateConfirmationResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(updateConfirmationResponse.getUpdateConfirmation());
    }

    @Override
    protected void mapResponse() {
        updateConfirmationResponse = restResponse.as(UpdateConfirmationResponse.class);
    }
}
