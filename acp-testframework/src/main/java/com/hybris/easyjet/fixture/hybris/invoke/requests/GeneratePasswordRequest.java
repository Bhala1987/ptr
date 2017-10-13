package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.PUT;

/**
 * Created by robertadigiorgio on 13/02/2017.
 */
public class GeneratePasswordRequest extends HybrisRequest implements IRequest {

    /**
     * this class models a request that can be sent to a service
     *
     * @param headers        the headers that should be sent as part of the request
     * @param pathParameters any path parameters required
     * @param requestBody    a request body if required
     */
    public GeneratePasswordRequest(HybrisHeaders headers, IPathParameters pathParameters, IRequestBody requestBody) {
        super(headers, PUT, pathParameters, null, requestBody);
    }
}
