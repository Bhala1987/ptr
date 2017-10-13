package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IQueryParams;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.GET;

/**
 * Created by dwebb on 12/5/2016.
 */
public class IdentifyCustomerRequest extends HybrisRequest implements IRequest {

    public IdentifyCustomerRequest(HybrisHeaders headers, IQueryParams queryParams) {
        super(headers, GET, null, queryParams, null);
    }
}
