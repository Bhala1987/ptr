package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IAssertion;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation.RemoveCommentsToCustomerResponse;

public class RemoveCommentsToCustomerService extends HybrisService {

    private RemoveCommentsToCustomerResponse removeCommentsToCustomerResponse;

    public RemoveCommentsToCustomerService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public RemoveCommentsToCustomerResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return removeCommentsToCustomerResponse;
    }

    @Override
    public IAssertion assertThat() {
        return null;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(removeCommentsToCustomerResponse);
    }

    @Override
    protected void mapResponse() {
        removeCommentsToCustomerResponse = restResponse.as(RemoveCommentsToCustomerResponse.class);

    }
}
