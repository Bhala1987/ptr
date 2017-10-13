package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.PUT;

/**
 * Created by daniel on 26/11/2016.
 */
public class BasketTravellerRequest extends HybrisRequest implements IRequest {

    public BasketTravellerRequest(HybrisHeaders headers, IPathParameters pathParams, IRequestBody requestBody) {
        super(headers, PUT, pathParams, null, requestBody);
    }
}
