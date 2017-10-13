package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.asserters.CreateCompensationAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.CreateCompensationResponse;

/**
 * Created by Niyi Falade on 18/09/17.
 */
public class CreateCompensationService extends HybrisService{

    private CreateCompensationResponse createCompensationResponse;

    /**
     * a service comprises a request, a client and an endpoint
     *
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    protected CreateCompensationService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public CreateCompensationResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return createCompensationResponse;
    }

    @Override
    public CreateCompensationAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new CreateCompensationAssertion(createCompensationResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(createCompensationResponse);

    }

    @Override
    protected void mapResponse() { createCompensationResponse = restResponse.as(CreateCompensationResponse.class);
    }
}
