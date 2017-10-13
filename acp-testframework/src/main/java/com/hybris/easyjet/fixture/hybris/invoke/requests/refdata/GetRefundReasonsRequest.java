package com.hybris.easyjet.fixture.hybris.invoke.requests.refdata;

import com.hybris.easyjet.config.constants.HttpMethods;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.HybrisRequest;

/**
 * Created by giuseppedimartino on 04/07/17.
 */
public class GetRefundReasonsRequest extends HybrisRequest {
    /**
     * this class models a request that can be sent to a service
     *
     * @param headers the headers that should be sent as part of the request
     */
    public GetRefundReasonsRequest(HybrisHeaders headers) {
        super(headers, HttpMethods.GET, null, null, null);
    }
}