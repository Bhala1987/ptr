package com.hybris.easyjet.fixture.hybris.invoke.requests.booking;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IQueryParams;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.HybrisRequest;

import static com.hybris.easyjet.config.constants.HttpMethods.GET;

/**
 * Created by daniel on 26/11/2016.
 */
public class GetBookingRequest extends HybrisRequest implements IRequest {

    public GetBookingRequest(HybrisHeaders headers, IPathParameters pathParameters) {
        super(headers, GET, pathParameters, null, null);
    }

    public GetBookingRequest(HybrisHeaders headers, IPathParameters pathParameters, IQueryParams queryParam) {
        super(headers, GET, pathParameters, queryParam, null);
    }
}
