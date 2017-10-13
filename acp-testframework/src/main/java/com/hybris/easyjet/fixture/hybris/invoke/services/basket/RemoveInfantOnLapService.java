package com.hybris.easyjet.fixture.hybris.invoke.services.basket;

import com.hybris.easyjet.fixture.hybris.asserters.RemoveInfantOnLapAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.requests.RemoveInfantOnLapRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.RemoveInfantOnLapResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by giuseppedimartino on 20/06/17.
 */
public class RemoveInfantOnLapService extends HybrisService {

    private RemoveInfantOnLapResponse removeInfantOnLapResponse;

    /**
     * a service comprises a request, a client and an endpoint
     *
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    public RemoveInfantOnLapService(RemoveInfantOnLapRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public RemoveInfantOnLapResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return removeInfantOnLapResponse;
    }

    @Override
    public RemoveInfantOnLapAssertion assertThat() {
        return new RemoveInfantOnLapAssertion(removeInfantOnLapResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(removeInfantOnLapResponse.getOperationConfirmation());
    }

    @Override
    protected void mapResponse() {
        removeInfantOnLapResponse = restResponse.as(RemoveInfantOnLapResponse.class);
    }
}
