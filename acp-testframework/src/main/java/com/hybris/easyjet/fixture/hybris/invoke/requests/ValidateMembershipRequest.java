package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.ValidateMembershipRequestBody;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;

/**
 * Created by rajakm on 21/08/2017.
 */
public class ValidateMembershipRequest extends HybrisRequest {
    public ValidateMembershipRequest(HybrisHeaders headers, ValidateMembershipRequestBody requestBody) {
        super(headers, POST, null, null, requestBody);
    }
}
