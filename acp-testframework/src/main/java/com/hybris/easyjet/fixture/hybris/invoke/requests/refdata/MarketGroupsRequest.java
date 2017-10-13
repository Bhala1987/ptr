package com.hybris.easyjet.fixture.hybris.invoke.requests.refdata;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.HybrisRequest;

import static com.hybris.easyjet.config.constants.HttpMethods.GET;

/**
 * Created by daniel on 26/11/2016.
 */
public class MarketGroupsRequest extends HybrisRequest implements IRequest {

    public MarketGroupsRequest(HybrisHeaders headers) {
        super(headers, GET, null, null, null);
    }
}
