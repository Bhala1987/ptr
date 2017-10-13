package com.hybris.easyjet.fixture.hybris.invoke.requests.booking;

import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.IdentifyPassengerRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.HybrisRequest;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;

/**
 * Created by rajakm on 11/10/2017.
 */
public class IdentifyPassengerRequest extends HybrisRequest {
    public IdentifyPassengerRequest(HybrisHeaders headers, IdentifyPassengerRequestBody requestBody) {
        super(headers, POST, null, null, requestBody);
    }
}
