package com.hybris.easyjet.fixture.hybris.invoke.requests.booking;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.HybrisRequest;

import static com.hybris.easyjet.config.constants.HttpMethods.GET;

/**
 * Created by rajakm on 02/08/2017.
 */
public class GetBookingSummaryRequest extends HybrisRequest implements IRequest {

    public GetBookingSummaryRequest(HybrisHeaders headers, CustomerPathParams pathParameters) {
        super(headers, GET, pathParameters, null, null);
    }
}
