package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.customercomments.AddCommentToCustomerRequestBody;

import static com.hybris.easyjet.config.constants.HttpMethods.PUT;

/**
 * Created by Jamie Hughes on 03/07/17.
 */
public class UpdateCommentToCustomerRequest extends HybrisRequest{

    /**
     * this class models a request that can be sent to a service
     *
     * @param headers        the headers that should be sent as part of the request
     * @param pathParams     any path parameters required
     * @param requestBody    any request body required
     */

    public UpdateCommentToCustomerRequest(HybrisHeaders headers, CustomerPathParams pathParams, AddCommentToCustomerRequestBody requestBody) {
        super(headers, PUT, pathParams, null, requestBody);
    }
}
