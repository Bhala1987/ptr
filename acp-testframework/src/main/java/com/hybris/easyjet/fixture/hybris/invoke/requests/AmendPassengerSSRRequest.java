package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.AmendPassengerSSRRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.DeletePassengerSSRRequestBody;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;

/**
 * Request object to amend passenger details request.
 *
 * @author Joshua Curtis <j.curtis@reply.com>
 */
public class AmendPassengerSSRRequest extends HybrisRequest {
    public AmendPassengerSSRRequest(HybrisHeaders headers, IPathParameters pathParams, AmendPassengerSSRRequestBody requestBody) {
        super(headers, POST, pathParams, null, requestBody);
    }

    public AmendPassengerSSRRequest(HybrisHeaders headers, IPathParameters pathParams, DeletePassengerSSRRequestBody requestBody) {
        super(headers, POST, pathParams, null, requestBody);
    }
}
