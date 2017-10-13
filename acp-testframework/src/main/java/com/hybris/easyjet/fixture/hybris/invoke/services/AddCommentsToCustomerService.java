package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IAssertion;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation.AddCommentsToCustomerResponse;

/**
 * Created by blackstar on 26/06/17.
 */
public class AddCommentsToCustomerService extends HybrisService {

    private AddCommentsToCustomerResponse addCommentsToCustomerResponse;

    public AddCommentsToCustomerService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public AddCommentsToCustomerResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return addCommentsToCustomerResponse;
    }

    @Override
    public IAssertion assertThat() {
        return null;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(addCommentsToCustomerResponse);
    }

    @Override
    protected void mapResponse() {
        addCommentsToCustomerResponse = restResponse.as(AddCommentsToCustomerResponse.class);

    }
}
