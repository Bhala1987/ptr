package com.hybris.easyjet.fixture.hybris.invoke.services.basket;

import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.ManageAdditionalFareToPassengerInBasketAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ManageAdditionalFareToPassengerInBasketRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 12/04/2017.
 */
public class ManageAdditionalFareToPassengerInBasketService extends HybrisService implements IService {

    private BasketConfirmationResponse manageAdditionalFareToPassengerInBasketResponse;

    /**
     * a service comprises a request and an endpoint
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    public ManageAdditionalFareToPassengerInBasketService(ManageAdditionalFareToPassengerInBasketRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public BasketConfirmationResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return manageAdditionalFareToPassengerInBasketResponse;
    }

    @Override
    public ManageAdditionalFareToPassengerInBasketAssertion assertThat() {
        return new ManageAdditionalFareToPassengerInBasketAssertion(manageAdditionalFareToPassengerInBasketResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(manageAdditionalFareToPassengerInBasketResponse.getOperationConfirmation());
    }

    @Override
    protected void mapResponse() {
        manageAdditionalFareToPassengerInBasketResponse = restResponse.as(BasketConfirmationResponse.class);
    }
}

