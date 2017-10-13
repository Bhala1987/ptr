package com.hybris.easyjet.fixture.hybris.invoke.services.basket;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.AddHoldBagToBasketAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by rajakm on 07/03/2017.
 */
public class AddHoldBagToBasketService extends HybrisService implements IService {
    private BasketConfirmationResponse addHoldBagResponse;
    private BasketsResponse basketsResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public AddHoldBagToBasketService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public BasketConfirmationResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return addHoldBagResponse;
    }

    @Override
    public AddHoldBagToBasketAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new AddHoldBagToBasketAssertion(basketsResponse, addHoldBagResponse);
    }


    @Override
    protected void mapResponse() {
        addHoldBagResponse = restResponse.as(BasketConfirmationResponse.class);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(addHoldBagResponse.getOperationConfirmation());
    }
}
