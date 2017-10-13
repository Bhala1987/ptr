package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.CommercialFlightScheduleAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.CommercialFlightScheduleResponse;

/**
 * Created by jamie on 13/02/2017.
 */
public class CommercialFlightScheduleService extends HybrisService implements IService {

    private CommercialFlightScheduleResponse theCommmercialFlightScheduleResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public CommercialFlightScheduleService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public CommercialFlightScheduleAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new CommercialFlightScheduleAssertion(theCommmercialFlightScheduleResponse);
    }

    @Override
    public CommercialFlightScheduleResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return theCommmercialFlightScheduleResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(theCommmercialFlightScheduleResponse.getSchedules());
    }

    @Override
    protected void mapResponse() {
        theCommmercialFlightScheduleResponse = restResponse.as(CommercialFlightScheduleResponse.class);
    }

}