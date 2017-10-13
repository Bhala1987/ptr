package com.hybris.easyjet.fixture.hybris.invoke.services.basket;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.AddCarToBasketAssertion;
import com.hybris.easyjet.fixture.hybris.asserters.AddHoldBagToBasketAssertion;
import com.hybris.easyjet.fixture.hybris.asserters.CarHireAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.carhire.CarHireResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;


public class AddCarToBasketService extends HybrisService implements IService {

    private BasketConfirmationResponse addCarToBasketResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    public AddCarToBasketService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(addCarToBasketResponse.getOperationConfirmation());
    }

    @Override
    public BasketConfirmationResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return addCarToBasketResponse;
    }
    @Override
    public AddCarToBasketAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new AddCarToBasketAssertion(addCarToBasketResponse);
    }
    @Override
    protected void mapResponse() {
        addCarToBasketResponse = restResponse.as(BasketConfirmationResponse.class);
    }

    public int getStatusCode() {
        return super.restResponse.getStatusCode();
    }
    @Override
    protected void assertThatServiceCallWasSuccessful() {
        if (restResponse.getStatusCode() == 200) {
            successful = true;
        }
    }
}