package com.hybris.easyjet.fixture.hybris.invoke.services.refdata;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.AirportsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.GetAirportsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by daniel on 26/11/2016.
 */
public class AirportsService extends HybrisService implements IService {

    private GetAirportsResponse getAirportsResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public AirportsService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public AirportsAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new AirportsAssertion(getAirportsResponse);
    }

    @Override
    public GetAirportsResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return getAirportsResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(getAirportsResponse.getAirports());
    }

    @Override
    protected void mapResponse() {
        getAirportsResponse = restResponse.as(GetAirportsResponse.class);
    }
}
