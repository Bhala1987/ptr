package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.GetAlternateAirportsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.GetAlternateAirportsResponse;

/**
 * Created by robertadigiorgio on 10/03/2017.
 */
public class GetAlternateAirportsService extends HybrisService implements IService {


    private GetAlternateAirportsResponse getAlternateAirportsResponse;

    /**
     * a service comprises a request, a client and an endpoint
     *
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    protected GetAlternateAirportsService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public GetAlternateAirportsResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return getAlternateAirportsResponse;
    }

    @Override
    public GetAlternateAirportsAssertion assertThat() {
        return new GetAlternateAirportsAssertion(getAlternateAirportsResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(getAlternateAirportsResponse);
    }

    @Override
    protected void mapResponse() {
        getAlternateAirportsResponse = restResponse.as(GetAlternateAirportsResponse.class);
    }
}
