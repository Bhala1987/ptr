package com.hybris.easyjet.fixture.hybris.invoke.requests.booking;

import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.UpdateCommentsOnBookingRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.HybrisRequest;

import static com.hybris.easyjet.config.constants.HttpMethods.PUT;

/**
 * Created by rajakm on 03/08/2017.
 */
public class UpdateCommentsOnBookingRequest extends HybrisRequest {
    /**
     * this class models a request that can be sent to a service
     *
     * @param headers        the headers that should be sent as part of the request
     * @param pathParams     any path parameters required
     * @param requestBody    any request body required
     */

    public UpdateCommentsOnBookingRequest(HybrisHeaders headers, BookingPathParams pathParams, UpdateCommentsOnBookingRequestBody requestBody) {
        super(headers, PUT, pathParams, null, requestBody);
    }
}
