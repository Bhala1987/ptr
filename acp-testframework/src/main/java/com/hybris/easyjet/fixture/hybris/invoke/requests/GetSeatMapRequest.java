package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IQueryParams;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.GET;

/**
 * Created by jamie on 21/03/2017.
 */
public class GetSeatMapRequest extends HybrisRequest implements IRequest {

    public GetSeatMapRequest(HybrisHeaders headers, IPathParameters pathParams, IQueryParams queryParams) {
        super(headers, GET, pathParams, queryParams, null);
    }

}
