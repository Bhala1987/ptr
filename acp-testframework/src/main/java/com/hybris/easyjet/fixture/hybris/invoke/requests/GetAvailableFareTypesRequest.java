package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IQueryParams;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.GET;


/**
 * Created by marco on 23/02/17.
 */
public class GetAvailableFareTypesRequest extends HybrisRequest implements IRequest {

    /**
     * @param headers
     * @param queryParameters
     */
    public GetAvailableFareTypesRequest(HybrisHeaders headers, IQueryParams queryParameters) {
        super(headers, GET, null, queryParameters, null);
    }
}
