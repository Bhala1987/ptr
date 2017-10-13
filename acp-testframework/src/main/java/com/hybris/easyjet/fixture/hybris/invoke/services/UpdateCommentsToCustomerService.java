package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IAssertion;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation.UpdateCommentsToCustomerResponse;

/**
 * Created by blackstar on 26/06/17.
 */
public class UpdateCommentsToCustomerService extends HybrisService implements IService {

    private UpdateCommentsToCustomerResponse updateCommentsToCustomerResponse;

    public UpdateCommentsToCustomerService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public UpdateCommentsToCustomerResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return updateCommentsToCustomerResponse;
    }

    @Override
    public IAssertion assertThat() {
        return null;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(updateCommentsToCustomerResponse);
    }

    @Override
    protected void mapResponse() {
        updateCommentsToCustomerResponse = restResponse.as(UpdateCommentsToCustomerResponse.class);

    }
}
