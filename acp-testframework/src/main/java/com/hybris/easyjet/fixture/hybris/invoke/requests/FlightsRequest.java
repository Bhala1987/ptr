package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IQueryParams;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import lombok.ToString;

import static com.hybris.easyjet.config.constants.HttpMethods.GET;

/**
 * Created by daniel on 26/11/2016.
 */
@ToString
public class FlightsRequest extends HybrisRequest implements IRequest {

    public FlightsRequest(HybrisHeaders headers, IQueryParams queryParameters) {
        super(headers, GET, null, queryParameters, null);
    }
}
