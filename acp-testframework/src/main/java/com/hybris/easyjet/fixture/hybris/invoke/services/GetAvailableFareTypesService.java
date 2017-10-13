package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IResponse;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.GetAvailableFareTypesAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.GetAvailableFareTypesResponse;

/**
 * Created by marco on 23/02/17.
 */
public class GetAvailableFareTypesService extends HybrisService implements IService {

    private GetAvailableFareTypesResponse getAvailableFareTypesResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    public GetAvailableFareTypesService(IRequest request, String endPoint) {
        super(request, endPoint);
    }


    @Override
    public IResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return getAvailableFareTypesResponse;
    }

    @Override
    public GetAvailableFareTypesAssertion assertThat() {
        return new GetAvailableFareTypesAssertion(getAvailableFareTypesResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(getAvailableFareTypesResponse.getAvailableFareTypes());
    }

    @Override
    protected void mapResponse() {
        getAvailableFareTypesResponse = restResponse.as(GetAvailableFareTypesResponse.class);
    }
}
