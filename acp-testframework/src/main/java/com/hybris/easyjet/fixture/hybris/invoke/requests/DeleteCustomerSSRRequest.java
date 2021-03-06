package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.DELETE;


/**
 * Created by robertadigiorgio on 10/04/2017.
 */
public class DeleteCustomerSSRRequest extends HybrisRequest implements IRequest {
    /**
     * this class models a request that can be sent to a service
     *
     * @param headers        the headers that should be sent as part of the request
     * @param pathParameters any path parameters required
     */
    public DeleteCustomerSSRRequest(HybrisHeaders headers, IPathParameters pathParameters) {
        super(headers, DELETE, pathParameters, null, null);
    }
}