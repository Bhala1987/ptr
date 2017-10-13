package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.DeleteRecentSearchesQueryParams;

import static com.hybris.easyjet.config.constants.HttpMethods.DELETE;

/**
 * Created by albertowork on 7/7/17.
 */
public class DeleteRecentSearchesRequest extends HybrisRequest implements IRequest {
    public DeleteRecentSearchesRequest(HybrisHeaders headers, IPathParameters pathParams, DeleteRecentSearchesQueryParams queryParams) {
        super(headers, DELETE, pathParams, queryParams, null);
    }
}
