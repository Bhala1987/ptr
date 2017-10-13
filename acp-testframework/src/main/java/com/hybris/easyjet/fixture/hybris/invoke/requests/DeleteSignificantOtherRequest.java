package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.DELETE;

/**
 * Created by adevanna on 13/03/17.
 */
public class DeleteSignificantOtherRequest extends HybrisRequest implements IRequest {

    public DeleteSignificantOtherRequest(HybrisHeaders headers, IRequestBody requestBody, IPathParameters pathParameters) {
        super(headers, DELETE, pathParameters, null, requestBody);
    }
}
