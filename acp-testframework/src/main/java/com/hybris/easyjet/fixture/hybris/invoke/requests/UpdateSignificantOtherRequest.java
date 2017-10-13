package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.PUT;

/**
 * Created by adevanna on 13/03/17.
 */
public class UpdateSignificantOtherRequest extends HybrisRequest implements IRequest {

    public UpdateSignificantOtherRequest(HybrisHeaders headers, IPathParameters pathParameters, IRequestBody requestBody) {
        super(headers, PUT, pathParameters, null, requestBody);
    }
}
