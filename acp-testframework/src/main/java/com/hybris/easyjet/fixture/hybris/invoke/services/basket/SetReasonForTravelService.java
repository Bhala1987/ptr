package com.hybris.easyjet.fixture.hybris.invoke.services.basket;

import com.hybris.easyjet.fixture.IAssertion;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by Niyi Falade on 02/08/17.
 */
public class SetReasonForTravelService extends HybrisService {

    private BasketConfirmationResponse confirmationResponse;

    public SetReasonForTravelService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public BasketConfirmationResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return confirmationResponse;
    }

    @Override
    public IAssertion assertThat() {
        return null;
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
