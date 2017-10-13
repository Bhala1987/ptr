package com.hybris.easyjet.fixture.hybris.invoke.services.basket;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IResponse;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.RemoveHoldBagProductAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by giuseppedimartino on 30/03/17.
 */
public class RemoveProductService extends HybrisService implements IService {

    private BasketConfirmationResponse confirmationResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    public RemoveProductService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public IResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return confirmationResponse;
    }

    @Override
    public RemoveHoldBagProductAssertion assertThat() {
        return new RemoveHoldBagProductAssertion(confirmationResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(confirmationResponse.getOperationConfirmation());
    }

    @Override
    protected void mapResponse() {
        confirmationResponse = restResponse.as(BasketConfirmationResponse.class);
    }

}