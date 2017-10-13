package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.GetFlightInterestAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.GetFlightInterestResponse;

/**
 * Created by robertadigiorgio on 01/08/2017.
 */
public class GetFlightInterestService extends HybrisService implements IService {

    private GetFlightInterestResponse getFlightInterestResponse;

    /**
     * a service comprises a request, a client and an endpoint
     *
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    protected GetFlightInterestService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public GetFlightInterestResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return getFlightInterestResponse;
    }

    @Override
    public GetFlightInterestAssertion assertThat() {
        return new GetFlightInterestAssertion(getFlightInterestResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(getFlightInterestResponse);
    }

    @Override
    protected void mapResponse() {
        getFlightInterestResponse = restResponse.as(GetFlightInterestResponse.class);

    }
    
    public int getStatusCode() {
        return super.restResponse.getStatusCode();
    }
}


