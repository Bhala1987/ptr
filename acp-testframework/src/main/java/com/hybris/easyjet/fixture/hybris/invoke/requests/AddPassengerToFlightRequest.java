package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.AddPassengerToFlightRequestBody;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;

public class AddPassengerToFlightRequest extends HybrisRequest {
    /**
     * this class models a request that can be sent to a service
     *
     * @param headers        the headers that should be sent as part of the request
     * @param pathParameters any path parameters required
     * @param requestBody    a request body if required
     */
    public AddPassengerToFlightRequest(HybrisHeaders headers, BasketPathParams pathParameters, AddPassengerToFlightRequestBody requestBody) {
        super(headers, POST, pathParameters, null, requestBody);
    }
}