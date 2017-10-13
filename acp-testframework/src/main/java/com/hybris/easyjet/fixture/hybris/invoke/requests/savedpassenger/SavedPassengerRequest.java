package com.hybris.easyjet.fixture.hybris.invoke.requests.savedpassenger;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.HybrisRequest;

import static com.hybris.easyjet.config.constants.HttpMethods.*;

/**
 * Created by giuseppecioce on 16/02/2017.
 */
public class SavedPassengerRequest extends HybrisRequest implements IRequest {

    public SavedPassengerRequest(HybrisHeaders headers, IPathParameters pathParams, String action) {
        super(headers, GET, pathParams, null, null);
    }

    public SavedPassengerRequest(HybrisHeaders headers, IPathParameters pathParams) {
        super(headers, DELETE, pathParams, null, null);
    }

    public SavedPassengerRequest(HybrisHeaders headers, IPathParameters pathParams, IRequestBody requestBody) {
        super(headers, POST, pathParams, null, requestBody);
    }

    public SavedPassengerRequest(HybrisHeaders headers, IPathParameters pathParams, IRequestBody requestBody, String update) {
        super(headers, PUT, pathParams, null, requestBody);
    }
}
