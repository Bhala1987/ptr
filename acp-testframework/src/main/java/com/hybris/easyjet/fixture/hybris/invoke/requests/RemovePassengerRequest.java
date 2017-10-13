package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IQueryParams;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.DELETE;

/**
 * Created by robertadigiorgio on 13/04/2017.
 */
public class RemovePassengerRequest extends HybrisRequest implements IRequest {

    /**
     * this class models a request that can be sent to a service
     *
     * @param headers         the headers that should be sent as part of the request
     * @param pathParameters  any path parameters required
     * @param queryParameters any query parameters required
     */
    public RemovePassengerRequest(HybrisHeaders headers, IPathParameters pathParameters, IQueryParams queryParameters) {
        super(headers, DELETE, pathParameters, queryParameters, null);
    }
}
