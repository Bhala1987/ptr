package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.GET;

/**
 * Created by markphipps on 29/03/2017.
 */
public class DependantsRequest extends HybrisRequest implements IRequest {
    public DependantsRequest(HybrisHeaders headers, IPathParameters pathParameters) {
        super(headers, GET, pathParameters, null, null);
    }
}
