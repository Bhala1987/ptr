package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;

/**
 * Created by adevanna on 13/03/17.
 */
public class AddIdentityDocumentRequest extends HybrisRequest implements IRequest {

    public AddIdentityDocumentRequest(HybrisHeaders headers, IRequestBody requestBody, IPathParameters pathParameters) {
        super(headers, POST, pathParameters, null, requestBody);
    }
}
