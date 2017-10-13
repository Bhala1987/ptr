package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IQueryParams;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.GET;

/**
 * Created by dwebb on 12/2/2016.
 */
public class ProfileRequest extends HybrisRequest implements IRequest {

    public ProfileRequest(HybrisHeaders headers, IPathParameters pathParams, IQueryParams queryParameters) {
        super(headers, GET, pathParams, queryParameters, null);
    }

    public ProfileRequest(HybrisHeaders headers, IPathParameters pathParams) {
        super(headers, GET, pathParams, null, null);
    }

}
