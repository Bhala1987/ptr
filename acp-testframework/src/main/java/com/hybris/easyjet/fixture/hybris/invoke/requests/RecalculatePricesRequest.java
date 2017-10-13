package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;

/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 08/05/2017.
 */
public class RecalculatePricesRequest extends HybrisRequest implements IRequest {

    /**
     * this class models a request that can be sent to a service
     *
     * @param headers        the headers that should be sent as part of the request
     * @param requestBody    any request body required
     */
    public RecalculatePricesRequest(HybrisHeaders headers, IRequestBody requestBody) {
        super(headers, POST, null, null, requestBody);
    }
}
