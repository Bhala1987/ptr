package com.hybris.easyjet.fixture.hybris.invoke.services.refdata;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.StationsForCarHireAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.StationsForCarHireResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by bhala.
 */
public class StationsForCarHireService extends HybrisService implements IService {

    private StationsForCarHireResponse stationsForCarHireResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public StationsForCarHireService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public StationsForCarHireAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new StationsForCarHireAssertion(stationsForCarHireResponse);
    }

    @Override
    public StationsForCarHireResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return stationsForCarHireResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(stationsForCarHireResponse.getStations());
    }

    @Override
    protected void mapResponse() {
        stationsForCarHireResponse = restResponse.as(StationsForCarHireResponse.class);
    }
}

