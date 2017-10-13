package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IAssertion;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.CheckInFlightResponse;

/**
 * Created by Niyi Falade on 26/06/17.
 */
public class CheckInFlightService extends HybrisService  {

    private CheckInFlightResponse checkInFlightResponse;

    public CheckInFlightService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public CheckInFlightResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return checkInFlightResponse;
    }

    @Override
    public IAssertion assertThat() {
        return null;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(checkInFlightResponse);

    }

    @Override
    protected void mapResponse() {
        checkInFlightResponse = restResponse.as(CheckInFlightResponse.class);
    }
    public int getStatusCode() {
        return restResponse.getStatusCode();
    }

}
