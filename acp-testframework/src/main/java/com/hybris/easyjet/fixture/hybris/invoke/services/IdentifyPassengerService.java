package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.hybris.asserters.IdentifyPassengerAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.IdentifyPassengerRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.IdentifyPassengerResponse;

/**
 * Created by rajakm on 11/10/2017.
 */
public class IdentifyPassengerService extends HybrisService {

    private IdentifyPassengerResponse identifyPassengerResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public IdentifyPassengerService(IdentifyPassengerRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public IdentifyPassengerAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new IdentifyPassengerAssertion(identifyPassengerResponse);
    }

    @Override
    public IdentifyPassengerResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return identifyPassengerResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(identifyPassengerResponse);
    }

    @Override
    protected void mapResponse() {
        identifyPassengerResponse = restResponse.as(IdentifyPassengerResponse.class);
    }

}
