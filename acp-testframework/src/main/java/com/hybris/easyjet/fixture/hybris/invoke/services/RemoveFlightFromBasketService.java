package com.hybris.easyjet.fixture.hybris.invoke.services;

/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 29/03/2017.
 */

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.RemoveFlightFromBasketAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;


public class RemoveFlightFromBasketService extends HybrisService implements IService {


    private BasketConfirmationResponse removeFlightFromBasketResponse;

    /**
     * a service comprises a request, a client and an endpoint
     *
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    protected RemoveFlightFromBasketService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public BasketConfirmationResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return removeFlightFromBasketResponse;
    }

    @Override
    public RemoveFlightFromBasketAssertion assertThat() {
        return new RemoveFlightFromBasketAssertion(removeFlightFromBasketResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(removeFlightFromBasketResponse.getOperationConfirmation());
    }

    @Override
    protected void mapResponse() {
        removeFlightFromBasketResponse = restResponse.as(BasketConfirmationResponse.class);
    }
}

