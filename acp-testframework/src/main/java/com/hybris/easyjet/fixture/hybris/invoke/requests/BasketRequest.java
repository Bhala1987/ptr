package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IQueryParams;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.*;

/**
 * Created by daniel on 26/11/2016.
 */
public class BasketRequest extends HybrisRequest implements IRequest {

    public BasketRequest(HybrisHeaders headers, IRequestBody requestBody) {
        super(headers, POST, null, null, requestBody);
    }

    public BasketRequest(HybrisHeaders headers, IPathParameters pathParams) {
        super(headers, GET, pathParams, null, null);
    }

    public BasketRequest(HybrisHeaders headers, IPathParameters pathParams, IQueryParams queryParam) {
        super(headers, DELETE, pathParams, queryParam, null);
    }

    public BasketRequest(HybrisHeaders headers, IPathParameters pathParams,IRequestBody requestBody) {
        super(headers, PUT, pathParams,null, requestBody);
    }
}
