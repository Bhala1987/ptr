package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.UpdatePassengerDetailsQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.AmendBasicDetailsRequestBody;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;

/**
 * AmendBasicDetailsRequest class, it holds request of AmendBasicDetails
 */
public class AmendBasicDetailsRequest extends HybrisRequest implements IRequest {

    /**
     * AmendBasicDetailsRequest, parameterize constructor
     *
     * @param headers
     * @param pathParams
     * @param queryParams
     * @param requestBody
     */
    public AmendBasicDetailsRequest(HybrisHeaders headers, BasketPathParams pathParams, UpdatePassengerDetailsQueryParams queryParams, AmendBasicDetailsRequestBody requestBody) {
        super(headers, POST, pathParams, queryParams, requestBody);
    }

    public AmendBasicDetailsRequest(HybrisHeaders headers, BasketPathParams pathParams, AmendBasicDetailsRequestBody requestBody) {
        super(headers, POST, pathParams, null, requestBody);
    }
}
