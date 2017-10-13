package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.GET;

/**
 * Created by vijayapalkayyam on 29/03/2017.
 */
public class GetAPIsForCustomerRequest extends HybrisRequest implements IRequest {

    /**
     * this class models a request that can be sent to a service
     *
     * @param headers        the headers that should be sent as part of the request
     * @param pathParameters any path parameters required
     */
    public GetAPIsForCustomerRequest(HybrisHeaders headers, IPathParameters pathParameters) {
        super(headers, GET, pathParameters, null, null);
    }
}



