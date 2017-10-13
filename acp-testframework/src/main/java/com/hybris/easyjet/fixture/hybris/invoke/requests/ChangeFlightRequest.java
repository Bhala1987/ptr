package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.ChangeFlightRequestBody;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;

/**
 * Created by robertadigiorgio on 11/07/2017.
 */
public class ChangeFlightRequest extends HybrisRequest {

    public ChangeFlightRequest(HybrisHeaders headers, BasketPathParams pathParams, ChangeFlightRequestBody requestBody) {
        super(headers, POST, pathParams, null, requestBody);
    }
}