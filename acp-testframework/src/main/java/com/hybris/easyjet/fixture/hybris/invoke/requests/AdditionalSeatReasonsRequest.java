package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.GET;

public class AdditionalSeatReasonsRequest extends HybrisRequest{
    /**
     * @param headers
     */
    public AdditionalSeatReasonsRequest(HybrisHeaders headers) {
        super(headers, GET, null, null, null);
    }
}