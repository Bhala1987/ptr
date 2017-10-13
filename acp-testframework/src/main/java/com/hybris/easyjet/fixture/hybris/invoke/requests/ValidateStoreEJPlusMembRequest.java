package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;

/**
 * Created by Giuseppe Cioce on 20/12/2016.
 */
public class ValidateStoreEJPlusMembRequest extends HybrisRequest implements IRequest {

    public ValidateStoreEJPlusMembRequest(HybrisHeaders headers, IRequestBody requestBody) {
        super(headers, POST, null, null, requestBody);
    }
}
