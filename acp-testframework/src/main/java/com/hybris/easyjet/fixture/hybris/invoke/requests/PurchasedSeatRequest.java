package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;

/**
 * Created by giuseppecioce on 12/04/2017.
 */
public class PurchasedSeatRequest extends HybrisRequest {
    public PurchasedSeatRequest(HybrisHeaders headers, IPathParameters pathParams, IRequestBody requestBody) {
        super(headers, POST, pathParams, null, requestBody);
    }

    public PurchasedSeatRequest(HybrisHeaders headers, IPathParameters pathParams, IRequestBody requestBody, String delete) {
        super(headers, POST, pathParams, null, requestBody);
    }
}
