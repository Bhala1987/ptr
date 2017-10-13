package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IQueryParams;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.DELETE;

/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 29/03/2017.
 */
public class RemoveFlightFromBasketRequest extends HybrisRequest implements IRequest {

    /**
     * this class models a request that can be sent to a service
     *
     * @param headers        the headers that should be sent as part of the request
     * @param pathParameters any path parameters required
     */
    public RemoveFlightFromBasketRequest(HybrisHeaders headers, IPathParameters pathParameters) {
        super(headers, DELETE, pathParameters, null, null);
    }

    /**
     * @param headers        the headers that should be sent as part of the request
     * @param pathParameters removeFlight path, basketId and flightKey
     * @param queryParams    from-search-results query parameter
     */
    public RemoveFlightFromBasketRequest(HybrisHeaders headers, IPathParameters pathParameters, IQueryParams queryParams) {
        super(headers, DELETE, pathParameters, queryParams, null);
    }
}