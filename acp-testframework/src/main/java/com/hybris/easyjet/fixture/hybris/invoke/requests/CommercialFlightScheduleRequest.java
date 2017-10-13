package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IQueryParams;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.GET;

/**
 * Created by jamie on 13/02/2017.
 */
public class CommercialFlightScheduleRequest extends HybrisRequest implements IRequest {


    public CommercialFlightScheduleRequest(HybrisHeaders headers, IQueryParams queryParameters) {
        super(headers, GET, null, queryParameters, null);
    }

    public CommercialFlightScheduleRequest(HybrisHeaders headers) {
        super(headers, GET, null, null, null);
    }

}
