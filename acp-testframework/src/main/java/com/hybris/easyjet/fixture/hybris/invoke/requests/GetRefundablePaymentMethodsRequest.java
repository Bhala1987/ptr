package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.config.constants.HttpMethods;
import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;



public class GetRefundablePaymentMethodsRequest extends HybrisRequest {
    /**
     * this class models a request that can be sent to a service
     *
     * @param headers the headers that should be sent as part of the request
     */
    public GetRefundablePaymentMethodsRequest(HybrisHeaders headers, IPathParameters pathParameters) {
        super(headers, HttpMethods.GET, pathParameters, null, null);
    }
}
