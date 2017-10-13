package com.hybris.easyjet.fixture.hybris.invoke.requests.booking;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.HybrisRequest;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;

/**
 * Created by rajakm on 05/05/2017.
 */
public class AddCommentToBookingRequest extends HybrisRequest implements IRequest {
    /**
     * this class models a request that can be sent to a service
     *
     * @param headers         the headers that should be sent as part of the request
     * @param pathParams  any path parameters required
     * @param requestBody     a request body if required
     */

    public AddCommentToBookingRequest(HybrisHeaders headers, IPathParameters pathParams, IRequestBody requestBody) {
        super(headers, POST, pathParams, null, requestBody);
    }
}
