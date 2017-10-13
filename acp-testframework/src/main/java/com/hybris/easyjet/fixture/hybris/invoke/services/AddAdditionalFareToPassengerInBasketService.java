package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.AddAdditionalFareToPassengerInBasketAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.Errors;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;

/**
 * Created by Niyi Falade on 19/06/17.
 */
public class AddAdditionalFareToPassengerInBasketService extends HybrisService implements IService {

    private Errors errorResponse;
    private BasketConfirmationResponse addAddtionalFareToPassengerInBasketResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *
     */
    protected AddAdditionalFareToPassengerInBasketService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(addAddtionalFareToPassengerInBasketResponse.getConfirmation().getBasketCode());
    }

    @Override
    protected void mapResponse() {
        addAddtionalFareToPassengerInBasketResponse = restResponse.as(BasketConfirmationResponse.class);

    }

    @Override
    public BasketConfirmationResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return addAddtionalFareToPassengerInBasketResponse;
    }

    @Override
    public AddAdditionalFareToPassengerInBasketAssertion assertThat() {
        return new AddAdditionalFareToPassengerInBasketAssertion(errorResponse);
    }
}
