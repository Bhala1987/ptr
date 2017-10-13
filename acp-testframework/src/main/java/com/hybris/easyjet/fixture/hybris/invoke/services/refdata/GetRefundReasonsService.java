package com.hybris.easyjet.fixture.hybris.invoke.services.refdata;

import com.hybris.easyjet.fixture.hybris.asserters.GetRefundReasonsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.requests.refdata.GetRefundReasonsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.GetRefundReasonsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by giuseppedimartino on 04/07/17.
 */
public class GetRefundReasonsService extends HybrisService {
    private GetRefundReasonsResponse getRefundReasonsResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    public GetRefundReasonsService(GetRefundReasonsRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public GetRefundReasonsResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return getRefundReasonsResponse;
    }

    @Override
    public GetRefundReasonsAssertion assertThat() {
        return new GetRefundReasonsAssertion(getRefundReasonsResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(getRefundReasonsResponse.getPrimaryRefundReasons());
    }

    @Override
    protected void mapResponse() {
        getRefundReasonsResponse = restResponse.as(GetRefundReasonsResponse.class);
    }
}