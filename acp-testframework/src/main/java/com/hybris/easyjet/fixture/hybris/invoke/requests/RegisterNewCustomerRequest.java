package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IQueryParams;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;

/**
 * Created by dwebb on 12/15/2016.
 */
public class RegisterNewCustomerRequest extends HybrisRequest implements IRequest {

    public RegisterNewCustomerRequest(HybrisHeaders headers, IRequestBody requestBody) {
        super(headers, POST, null, null, requestBody);
    }

    public RegisterNewCustomerRequest(HybrisHeaders headers, IRequestBody requestBody, IQueryParams queryParams) {
        super(headers, POST,null,queryParams, requestBody);
    }

}
