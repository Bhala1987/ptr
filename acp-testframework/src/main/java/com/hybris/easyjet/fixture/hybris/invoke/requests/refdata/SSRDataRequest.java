package com.hybris.easyjet.fixture.hybris.invoke.requests.refdata;

import com.hybris.easyjet.fixture.IQueryParams;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.HybrisRequest;

import static com.hybris.easyjet.config.constants.HttpMethods.GET;

/**
 * Created by giuseppecioce on 09/02/2017.
 */
public class SSRDataRequest extends HybrisRequest implements IRequest {
    /**
     * this class models a request that can be sent to a service
     *
     * @param headers the headers that should be sent as part of the request
     */
    public SSRDataRequest(HybrisHeaders headers, IQueryParams queryParameters) {
        super(headers, GET, null, queryParameters, null);
    }
}
