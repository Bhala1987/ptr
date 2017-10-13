package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;

/**
 * Created by sudhir on 04/07/2017.
 */
public class RemoveSavedPaymentRequest extends HybrisRequest implements IRequest {


    public RemoveSavedPaymentRequest(HybrisHeaders headers, IPathParameters iPathParameters, IRequestBody iRequestBody) {
        super(headers, POST, iPathParameters, null, iRequestBody);
    }
}
