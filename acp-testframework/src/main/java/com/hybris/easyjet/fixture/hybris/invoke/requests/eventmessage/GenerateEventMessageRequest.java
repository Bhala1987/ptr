package com.hybris.easyjet.fixture.hybris.invoke.requests.eventmessage;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.EventMessagePathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.HybrisRequest;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;

/**
 * Created by tejaldudhale on 08/08/2017.
 */
public class GenerateEventMessageRequest extends HybrisRequest implements IRequest {

    public GenerateEventMessageRequest(HybrisHeaders headers, EventMessagePathParams pathParameters, IRequestBody requestBody) {
        super(headers, POST, pathParameters, null, requestBody);
    }
}
