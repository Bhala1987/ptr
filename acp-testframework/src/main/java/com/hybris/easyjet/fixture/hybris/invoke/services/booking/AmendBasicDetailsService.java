package com.hybris.easyjet.fixture.hybris.invoke.services.booking;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.AmendBasicDetailsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * AmendBasicDetailsService class, it updates basket the basic details
 */
public class AmendBasicDetailsService extends HybrisService implements IService {

    private BasketConfirmationResponse amendDetailsResponse;

    /**
     * AmendBasicDetailsService, parameterize constructor
     * @param request
     * @param endPoint
     */
    public AmendBasicDetailsService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public BasketConfirmationResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return amendDetailsResponse;
    }

    @Override
    public AmendBasicDetailsAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new AmendBasicDetailsAssertion(amendDetailsResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(amendDetailsResponse.getOperationConfirmation());
    }

    @Override
    protected void mapResponse() {
        amendDetailsResponse = restResponse.as(BasketConfirmationResponse.class);
    }

    public int getStatus() {
        return restResponse.getStatusCode();
    }
}
