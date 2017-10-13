package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.hybris.asserters.ChangeFlightAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ChangeFlightRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;

/**
 * Created by robertadigiorgio on 11/07/2017.
 */
public class ChangeFlightService extends HybrisService {

    private BasketConfirmationResponse confirmationResponse;

    /**
     * a service comprises a request, a client and an endpoint
     *
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    protected ChangeFlightService(ChangeFlightRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public BasketConfirmationResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return confirmationResponse;
    }

    @Override
    public ChangeFlightAssertion assertThat() {
        return new ChangeFlightAssertion(confirmationResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(confirmationResponse.getOperationConfirmation());
    }

    @Override
    protected void mapResponse() {
        confirmationResponse = restResponse.as(BasketConfirmationResponse.class);
    }

    public int getStatusCode(){
       return restResponse.getStatusCode();
    }
}