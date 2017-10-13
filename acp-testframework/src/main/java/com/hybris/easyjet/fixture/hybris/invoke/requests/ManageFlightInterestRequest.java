package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.GET;
import static com.hybris.easyjet.config.constants.HttpMethods.POST;

/**
 * Created by ptr-kvijayapal on 2/1/2017.
 */

public class ManageFlightInterestRequest extends HybrisRequest implements IRequest {

    public ManageFlightInterestRequest(HybrisHeaders headers, IPathParameters pathParameters, IRequestBody requestBody) {
        super(headers, POST, pathParameters, null, requestBody);
    }

    public ManageFlightInterestRequest(HybrisHeaders headers, IPathParameters pathParameters) {
        super(headers, GET, pathParameters, null, null);
    }
}
