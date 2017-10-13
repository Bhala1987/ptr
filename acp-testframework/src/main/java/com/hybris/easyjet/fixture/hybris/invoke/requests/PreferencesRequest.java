package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.GET;

/**
 * Created by PTR Scaffolder.
 */
public class PreferencesRequest extends HybrisRequest implements IRequest {

    public PreferencesRequest(HybrisHeaders headers) {
        super(headers, GET, null, null, null);
    }
}