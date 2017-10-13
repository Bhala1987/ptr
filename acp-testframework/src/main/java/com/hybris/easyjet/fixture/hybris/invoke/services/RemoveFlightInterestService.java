package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.RemoveFlightInterestServiceAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.requests.RemoveFlightInterestRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation.UpdateConfirmationResponse;

/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 8/9/2017.
 */

public class RemoveFlightInterestService extends HybrisService implements IService {

    private UpdateConfirmationResponse updateConfirmationResponse;

    public RemoveFlightInterestService(RemoveFlightInterestRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(updateConfirmationResponse.getOperationConfirmation());
    }

    @Override
    protected void mapResponse() {
        updateConfirmationResponse = restResponse.as(UpdateConfirmationResponse.class);
    }

    @Override
    public UpdateConfirmationResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return updateConfirmationResponse;
    }

    @Override
    public RemoveFlightInterestServiceAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new RemoveFlightInterestServiceAssertion(updateConfirmationResponse);
    }

    public RemoveFlightInterestServiceAssertion wasSuccessful() {
        assertThatServiceCallWasSuccessful();
        return new RemoveFlightInterestServiceAssertion(updateConfirmationResponse);
    }

    public int getStatusCode() {
        return super.restResponse.getStatusCode();
    }
}
