package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;

/**
 * Created by claudiodamico on 08/03/2017.
 */
public class AddSignificantOtherRequest extends HybrisRequest implements IRequest {

    public AddSignificantOtherRequest(HybrisHeaders headers, IRequestBody requestBody, IPathParameters pathParameters) {
        super(headers, POST, pathParameters, null, requestBody);
    }
}
