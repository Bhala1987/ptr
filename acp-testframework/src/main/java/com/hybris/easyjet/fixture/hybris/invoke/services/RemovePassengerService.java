package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.RemovePassengerAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;

/**
 * Created by robertadigiorgio on 13/04/2017.
 */
public class RemovePassengerService extends HybrisService implements IService {


    private BasketConfirmationResponse confirmationResponse;

    /**
     * a service comprises a request, a client and an endpoint
     *
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    protected RemovePassengerService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public BasketConfirmationResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return confirmationResponse;
    }

    @Override
    public RemovePassengerAssertion assertThat() {
        return new RemovePassengerAssertion(confirmationResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(confirmationResponse.getOperationConfirmation());
    }

    @Override
    protected void mapResponse() {
        confirmationResponse = restResponse.as(BasketConfirmationResponse.class);
    }

    public int getStatusCode() {
        return restResponse.getStatusCode();
    }
}