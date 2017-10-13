package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;

/**
 * Created by giuseppecioce on 01/03/2017.
 */
public class ResetPasswordRequest extends HybrisRequest implements IRequest {
    public ResetPasswordRequest(HybrisHeaders headers, IPathParameters pathParameters, IRequestBody requestBody) {
        super(headers, POST, pathParameters, null, requestBody);
    }

    public ResetPasswordRequest(HybrisHeaders headers, IRequestBody requestBody) {
        super(headers, POST, null, null, requestBody);
    }
}
