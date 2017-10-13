package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.DELETE;

/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 8/9/2017.
 */

public class RemoveFlightInterestRequest extends HybrisRequest implements IRequest {

    public RemoveFlightInterestRequest(HybrisHeaders headers, IPathParameters pathParameters, IRequestBody requestBody) {
        super(headers, DELETE, pathParameters, null, requestBody);
    }
}
