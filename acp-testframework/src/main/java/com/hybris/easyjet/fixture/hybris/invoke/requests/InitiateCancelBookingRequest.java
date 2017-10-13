package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;

/**
 * Request used when cancelling a booking.
 */
public class InitiateCancelBookingRequest extends HybrisRequest {
    public InitiateCancelBookingRequest(HybrisHeaders headers, IPathParameters pathParameters) {
        super(headers, POST, pathParameters, null, null);
    }
}
