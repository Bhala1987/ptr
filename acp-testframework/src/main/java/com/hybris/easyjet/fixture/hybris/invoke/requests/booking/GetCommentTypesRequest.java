package com.hybris.easyjet.fixture.hybris.invoke.requests.booking;

import com.hybris.easyjet.fixture.IQueryParams;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.HybrisRequest;

import static com.hybris.easyjet.config.constants.HttpMethods.GET;

/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 05/07/2017.
 */
public class GetCommentTypesRequest extends HybrisRequest implements IRequest {

    /**
     * this class models a request that can be sent to a service
     *
     * @param headers        the headers that should be sent as part of the request
     * @param queryParams    any query parameters required
     */
    public GetCommentTypesRequest(HybrisHeaders headers, IQueryParams queryParams) {
        super(headers, GET, null, queryParams, null);
    }
}
