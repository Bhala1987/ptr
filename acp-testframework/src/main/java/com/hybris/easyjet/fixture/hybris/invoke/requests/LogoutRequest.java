package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;

/**
 * Created by giuseppedimartino on 13/02/17.
 */
public class LogoutRequest extends HybrisRequest {
    /**
     * this class models a request that can be sent to a service
     *
     * @param headers        the headers that should be sent as part of the request
     * @param pathParameters any path parameters required
     */
    public LogoutRequest(HybrisHeaders headers, IPathParameters pathParameters) {
        super(headers, POST, pathParameters, null, null);
    }
}
