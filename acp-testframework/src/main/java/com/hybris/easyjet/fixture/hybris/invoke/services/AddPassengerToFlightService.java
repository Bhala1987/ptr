package com.hybris.easyjet.fixture.hybris.invoke.services;


import com.hybris.easyjet.fixture.hybris.asserters.AddPassengerToFlightAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AddPassengerToFlightRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.AddPassengerToFlightResponse;

/**
 * Created by dwebb on 12/2/2016.
 */

public class AddPassengerToFlightService extends HybrisService{

    private AddPassengerToFlightResponse addPassengerToFlightResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    protected AddPassengerToFlightService(AddPassengerToFlightRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public AddPassengerToFlightAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new AddPassengerToFlightAssertion(addPassengerToFlightResponse);
    }

    @Override
    public AddPassengerToFlightResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return addPassengerToFlightResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(addPassengerToFlightResponse.getOperationConfirmation());
    }

    @Override
    protected void mapResponse() {
        addPassengerToFlightResponse = restResponse.as(AddPassengerToFlightResponse.class);
    }

}
