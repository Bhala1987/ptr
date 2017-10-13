package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.SetAPIAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.SetAPIResponse;


/**
 * Implementation of Customer.setApi() service
 */
public class SetAPIService extends HybrisService implements IService {

    private SetAPIResponse setAPIResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    public SetAPIService(IRequest request, String endPoint) {
        super(request, endPoint);
    }


    @Override
    public SetAPIResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return setAPIResponse;
    }

    @Override
    public SetAPIAssertion assertThat() {
        return new SetAPIAssertion(setAPIResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(setAPIResponse.getCustomerId());
    }

    @Override
    protected void mapResponse() {
        setAPIResponse = restResponse.as(SetAPIResponse.class);
    }
}
