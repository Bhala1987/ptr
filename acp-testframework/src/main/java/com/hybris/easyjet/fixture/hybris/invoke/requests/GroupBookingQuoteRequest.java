package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.GroupBookingQuoteRequestBody;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;

public class GroupBookingQuoteRequest extends HybrisRequest {

    public GroupBookingQuoteRequest(HybrisHeaders headers, BasketPathParams pathParameters, GroupBookingQuoteRequestBody requestBody) {
        super(headers, POST, pathParameters, null, requestBody);
    }
}