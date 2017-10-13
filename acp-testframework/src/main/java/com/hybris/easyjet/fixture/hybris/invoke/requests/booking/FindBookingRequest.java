package com.hybris.easyjet.fixture.hybris.invoke.requests.booking;

import com.hybris.easyjet.fixture.IQueryParams;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.HybrisRequest;

import static com.hybris.easyjet.config.constants.HttpMethods.GET;

/**
 * Created by daniel on 26/11/2016.
 */

public class FindBookingRequest extends HybrisRequest implements IRequest {

    public FindBookingRequest(HybrisHeaders headers, IQueryParams queryParameters) {
        super(headers, GET, null, queryParameters, null);
    }


}
