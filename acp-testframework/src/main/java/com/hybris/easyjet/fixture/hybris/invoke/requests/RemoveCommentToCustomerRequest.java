package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;

import static com.hybris.easyjet.config.constants.HttpMethods.DELETE;

/**
 * Created by Jamie Hughes on 03/07/17.
 */
public class RemoveCommentToCustomerRequest extends HybrisRequest{

    /**
     * this class models a request that can be sent to a service
     *
     * @param headers        the headers that should be sent as part of the request
     * @param pathParams     any path parameters required
     */

    public RemoveCommentToCustomerRequest(HybrisHeaders headers, CustomerPathParams pathParams) {
        super(headers, DELETE, pathParams, null, null);
    }
}
