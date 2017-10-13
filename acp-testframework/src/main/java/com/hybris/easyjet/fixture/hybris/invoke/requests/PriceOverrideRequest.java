package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.DELETE;
import static com.hybris.easyjet.config.constants.HttpMethods.POST;

/**
 * Created by giuseppecioce on 29/03/2017.
 */
public class PriceOverrideRequest extends HybrisRequest implements IRequest {
    public PriceOverrideRequest(HybrisHeaders headers, IPathParameters pathParams, IRequestBody requestBody) {
        super(headers, POST, pathParams, null, requestBody);
    }

    public PriceOverrideRequest(HybrisHeaders headers, IPathParameters pathParams) {
        super(headers, DELETE, pathParams, null, null);
    }
}
