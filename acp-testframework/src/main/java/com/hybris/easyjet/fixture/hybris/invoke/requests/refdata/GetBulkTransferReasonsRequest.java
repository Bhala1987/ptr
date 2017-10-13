package com.hybris.easyjet.fixture.hybris.invoke.requests.refdata;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.HybrisRequest;

import static com.hybris.easyjet.config.constants.HttpMethods.GET;

public class GetBulkTransferReasonsRequest extends HybrisRequest implements IRequest {

    public GetBulkTransferReasonsRequest(HybrisHeaders headers) {
        super(headers, GET, null, null, null);
    }

}