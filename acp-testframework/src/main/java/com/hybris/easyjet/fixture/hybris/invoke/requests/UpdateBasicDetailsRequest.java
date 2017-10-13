package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.UpdatePassengerDetailsQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.UpdateBasicDetailsRequestBody;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;

/**
 * Created by giuseppedimartino on 26/06/17.
 */
public class UpdateBasicDetailsRequest extends HybrisRequest {
    /**
     * this class models a request that can be sent to a service
     *
     * @param headers         the headers that should be sent as part of the request
     * @param pathParameters  any path parameters required
     * @param requestBody     a request body if required
     */
    public UpdateBasicDetailsRequest(HybrisHeaders headers, BasketPathParams pathParameters, UpdatePassengerDetailsQueryParams queryParams, UpdateBasicDetailsRequestBody requestBody) {
        super(headers, POST, pathParameters, queryParams, requestBody);
    }
}