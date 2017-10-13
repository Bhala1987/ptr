package com.hybris.easyjet.fixture.hybris.invoke.requests.refdata;


import com.hybris.easyjet.fixture.IQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.HybrisRequest;

import static com.hybris.easyjet.config.constants.HttpMethods.GET;

/**
 * Created by giuseppedimartino on 31/01/17.
 */
public class SectorsRequest extends HybrisRequest {

    /**
     * this class models a request that can be sent to a service
     *
     * @param headers         the headers that should be sent as part of the request
     * @param queryParameters any query parameters required
     */
    public SectorsRequest(HybrisHeaders headers, IQueryParams queryParameters) {
        super(headers, GET, null, queryParameters, null);
    }

}
