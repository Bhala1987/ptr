package com.hybris.easyjet.fixture.hybris.invoke.services.basket;

import com.hybris.easyjet.fixture.hybris.asserters.UpdateBasicDetailsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.requests.UpdateBasicDetailsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by giuseppedimartino on 26/06/17.
 */
public class UpdateBasicDetailsService extends HybrisService {

    private BasketConfirmationResponse confirmationResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    public UpdateBasicDetailsService(UpdateBasicDetailsRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public BasketConfirmationResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return confirmationResponse;
    }

    @Override
    public UpdateBasicDetailsAssertion assertThat() {
        return new UpdateBasicDetailsAssertion(confirmationResponse);
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
