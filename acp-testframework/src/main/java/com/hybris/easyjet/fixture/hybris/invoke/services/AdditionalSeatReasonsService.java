package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.hybris.asserters.AdditionalSeatReasonsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AdditionalSeatReasonsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.AdditionalSeatReasonsResponse;

public class AdditionalSeatReasonsService extends HybrisService {

    private AdditionalSeatReasonsResponse additionalSeatReasonsResponse;

    /**
     * a service comprises a request, a client and an endpoint
     *
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    protected AdditionalSeatReasonsService(AdditionalSeatReasonsRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public AdditionalSeatReasonsResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return additionalSeatReasonsResponse;
    }

    @Override
    public AdditionalSeatReasonsAssertion assertThat() {
        return new AdditionalSeatReasonsAssertion(additionalSeatReasonsResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(additionalSeatReasonsResponse.getAdditionalSeatReasons());
    }

    @Override
    protected void mapResponse() {
        additionalSeatReasonsResponse = restResponse.as(AdditionalSeatReasonsResponse.class);
    }
}
