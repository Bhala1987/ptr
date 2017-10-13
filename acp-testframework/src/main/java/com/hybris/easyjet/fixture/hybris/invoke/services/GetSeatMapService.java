package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.GetSeatMapServiceAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.GetSeatMapResponse;

/**
 * Created by jamie on 21/03/2017.
 */
public class GetSeatMapService extends HybrisService implements IService {

    private GetSeatMapResponse getSeatMapResponse;

    /**
     * a service comprises a request, a client and an endpoint
     *
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    protected GetSeatMapService(IRequest request, String endPoint) {
        super(request, endPoint);
    }


    @Override
    protected void checkThatResponseBodyIsPopulated() {
    }

    @Override
    protected void mapResponse() {
        getSeatMapResponse = restResponse.as(GetSeatMapResponse.class);
    }

    @Override
    public GetSeatMapResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return getSeatMapResponse;
    }

    @Override
    public GetSeatMapServiceAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new GetSeatMapServiceAssertion(getSeatMapResponse);
    }
}
