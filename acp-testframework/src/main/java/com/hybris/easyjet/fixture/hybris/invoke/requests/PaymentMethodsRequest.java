package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IQueryParams;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.GET;
import static com.hybris.easyjet.config.constants.HttpMethods.POST;

/**
 * Created by daniel on 26/11/2016.
 */
public class PaymentMethodsRequest extends HybrisRequest implements IRequest {

    public PaymentMethodsRequest(HybrisHeaders headers, IQueryParams queryParameters) {
        super(headers, GET, null, queryParameters, null);
    }

    public PaymentMethodsRequest(HybrisHeaders headers, IPathParameters iPathParameters) {
        super(headers, GET, iPathParameters, null, null);
    }

    public PaymentMethodsRequest(HybrisHeaders headers, IPathParameters iPathParameters,IQueryParams queryParameters) {
        super(headers, GET, iPathParameters, queryParameters, null);
    }

    public PaymentMethodsRequest(HybrisHeaders headers) {
        super(headers, GET, null, null, null);
    }

    public PaymentMethodsRequest(HybrisHeaders headers, IPathParameters iPathParameters, IRequestBody iRequestBody) {
        super(headers, POST, iPathParameters, null, iRequestBody);
    }
}
