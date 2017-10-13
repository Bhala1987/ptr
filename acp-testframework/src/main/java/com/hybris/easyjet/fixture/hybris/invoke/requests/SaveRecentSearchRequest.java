package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.GET;

/**
 * Created by ptr-kvijayapal on 2/1/2017.
 */

public class SaveRecentSearchRequest extends HybrisRequest implements IRequest {

    public SaveRecentSearchRequest(HybrisHeaders headers, IPathParameters pathParams) {
        super(headers, GET, pathParams, null, null);
    }
}
