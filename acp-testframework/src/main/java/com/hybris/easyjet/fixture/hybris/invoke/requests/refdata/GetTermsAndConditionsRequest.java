package com.hybris.easyjet.fixture.hybris.invoke.requests.refdata;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.HybrisRequest;

import static com.hybris.easyjet.config.constants.HttpMethods.GET;

/**
 * Created by rajakm on 12/09/2017.
 */
public class GetTermsAndConditionsRequest extends HybrisRequest implements IRequest {

    public GetTermsAndConditionsRequest(HybrisHeaders headers) {
        super(headers, GET, null, null, null);
    }
}
