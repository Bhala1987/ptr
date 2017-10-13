package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;
import static com.hybris.easyjet.config.constants.HttpMethods.PUT;

/**
 * Created by giuseppecioce on 01/03/2017.
 */
public class AddHoldItemsToBasketRequest extends HybrisRequest implements IRequest {
    public AddHoldItemsToBasketRequest(HybrisHeaders headers, IPathParameters pathParams, IRequestBody requestBody) {
        super(headers, POST, pathParams, null, requestBody);
    }

    public AddHoldItemsToBasketRequest(HybrisHeaders headers, IPathParameters pathParams, IRequestBody requestBody, boolean isExcessWeight) {
        super(headers, PUT, pathParams, null, requestBody);
    }
}
