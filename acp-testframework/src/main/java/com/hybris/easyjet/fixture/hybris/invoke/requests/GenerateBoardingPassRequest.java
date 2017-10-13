package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.GenerateBoardingPassRequestBody;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;


/**
 * Created by albertowork on 5/24/17.
 */
public class GenerateBoardingPassRequest extends HybrisRequest implements IRequest {
    public GenerateBoardingPassRequest(HybrisHeaders headers, IPathParameters pathParams, GenerateBoardingPassRequestBody requestBody) {
        super(headers, POST, pathParams, null, requestBody);
    }
}
