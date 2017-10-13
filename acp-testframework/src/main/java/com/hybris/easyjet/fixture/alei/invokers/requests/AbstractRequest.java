package com.hybris.easyjet.fixture.alei.invokers.requests;

import com.hybris.easyjet.config.constants.HttpMethods;
import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IQueryParams;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.alei.invokers.ALHeaders;

public abstract class AbstractRequest implements IRequest {

    private final ALHeaders headers;
    private final HttpMethods httpMethod;
    private final IQueryParams queryParameters;
    private final IRequestBody requestBody;
    private final IPathParameters pathParameters;

    /**
     * this class models a request that can be sent to a service
     *
     * @param headers         the headers that should be sent as part of the request
     * @param httpMethod      the httpMethod of the request
     * @param pathParameters  any path parameters required
     * @param queryParameters any query parameters required
     * @param requestBody     a request body if required
     */
    public AbstractRequest(ALHeaders headers, HttpMethods httpMethod, IPathParameters pathParameters, IQueryParams queryParameters, IRequestBody requestBody) {
        this.headers = headers;
        this.httpMethod = httpMethod;
        this.pathParameters = pathParameters;
        this.queryParameters = queryParameters;
        this.requestBody = requestBody;
    }

    /**
     * @return the http method of the request
     */
    @Override
    public HttpMethods getHttpMethod() {
        return httpMethod;
    }

    /**
     * @return the Headers to be sent as part of the request
     */
    @Override
    public ALHeaders getHeaders() {
        return headers;
    }

    /**
     * @return the query parameters to be sent as part of the request
     */
    @Override
    public IQueryParams getQueryParameters() {
        return queryParameters;
    }

    /**
     * @return the request body to be sent as part of the request
     */
    @Override
    public IRequestBody getRequestBody() {
        return requestBody;
    }

    /**
     * @return the path parameters so be sent as part of the request
     */
    @Override
    public IPathParameters getPathParameters() {
        return pathParameters;
    }
}
