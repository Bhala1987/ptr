package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IQueryParams;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;
import static com.hybris.easyjet.config.constants.HttpMethods.PUT;


/**
 * Created by marco on 15/02/17.
 */
public class SetAPIRequest extends HybrisRequest implements IRequest {
    /**
     * this class models a request that can be sent to a service
     *
     * @param headers        the headers that should be sent as part of the request
     * @param pathParameters any path parameters required
     * @param requestBody    a request body if required
     */
    public SetAPIRequest(HybrisHeaders headers, IPathParameters pathParameters, IRequestBody requestBody) {
        super(headers, POST, pathParameters, null, requestBody);
    }

    public SetAPIRequest(HybrisHeaders headers, IPathParameters pathParameters, IQueryParams queryParameters, IRequestBody requestBody) {
        super(headers, PUT, pathParameters, queryParameters, requestBody);
    }
}
