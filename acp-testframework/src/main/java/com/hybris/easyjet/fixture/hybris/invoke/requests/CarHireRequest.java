package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;

/**
 * Created by stalluri on 26/07/2017.
 */
public class CarHireRequest extends HybrisRequest implements IRequest {
    public CarHireRequest(HybrisHeaders headers, IPathParameters pathParams, IRequestBody requestBody) {
        super(headers, POST, pathParams, null, requestBody);
    }
}
