package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;

/**
 * Created by Niyi Falade on 24/07/17.
 */
public class CancelBookingRefundRequest  extends HybrisRequest {
    public CancelBookingRefundRequest(HybrisHeaders headers, IPathParameters pathParameters,IRequestBody requestBody) {
        super(headers, POST, pathParameters, null, requestBody);
    }
}
