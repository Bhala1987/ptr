package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;

/**
 * Created by daniel on 26/11/2016.
 */
public class LoginRequest extends HybrisRequest implements IRequest {

    public LoginRequest(HybrisHeaders headers, IRequestBody requestBody) {
        super(headers, POST, null, null, requestBody);
    }
}
