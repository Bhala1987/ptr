package com.hybris.easyjet.fixture.hybris.invoke.requests.refdata;

import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.HybrisRequest;

import static com.hybris.easyjet.config.constants.HttpMethods.GET;

/**
 * Created by giuseppcioce on 08/02/2017.
 */
public class TravelDocumentTypesRequest extends HybrisRequest {

    public TravelDocumentTypesRequest(HybrisHeaders headers) {
        super(headers, GET, null, null, null);
    }
}
