package com.hybris.easyjet.fixture;

import com.hybris.easyjet.config.constants.HttpMethods;

/**
 * Created by daniel on 26/11/2016.
 */
public interface IRequest {

    IHeaders getHeaders();

    IQueryParams getQueryParameters();

    IRequestBody getRequestBody();

    HttpMethods getHttpMethod();

    IPathParameters getPathParameters();

}
