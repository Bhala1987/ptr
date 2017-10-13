package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.dependants.AddDependantsIdentityDocumentRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.dependants.UpdateDependantsEjPlusCardNumberRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.dependants.UpdateDependantsIdentityDocumentRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.dependants.UpdateDependantsSavedSSRsRequestBody;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;
import static com.hybris.easyjet.config.constants.HttpMethods.PUT;

/**
 * Created by markphipps on 23/03/2017.
 */
public class UpdateDependantsRequest extends HybrisRequest implements IRequest {

    public UpdateDependantsRequest(HybrisHeaders headers, IPathParameters pathParameters, AddDependantsIdentityDocumentRequestBody requestBody) {
        super(headers, POST, pathParameters, null, requestBody);
    }

    public UpdateDependantsRequest(HybrisHeaders headers, IPathParameters pathParameters, UpdateDependantsIdentityDocumentRequestBody requestBody) {
        super(headers, PUT, pathParameters, null, requestBody);
    }

    public UpdateDependantsRequest(HybrisHeaders headers, IPathParameters pathParameters, UpdateDependantsEjPlusCardNumberRequestBody requestBody) {
        super(headers, PUT, pathParameters, null, requestBody);
    }

    public UpdateDependantsRequest(HybrisHeaders headers, IPathParameters pathParameters, UpdateDependantsSavedSSRsRequestBody requestBody) {
        super(headers, PUT, pathParameters, null, requestBody);
    }

}
