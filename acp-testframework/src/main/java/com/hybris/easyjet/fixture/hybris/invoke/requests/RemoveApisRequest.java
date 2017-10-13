package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.DELETE;

/**
 * Created by robertadigiorgio on 06/04/2017.
 */
public class RemoveApisRequest extends HybrisRequest implements IRequest {

    /**
     * this class models a request to delete the hold bags from the basket
     *
     * @param headers        no specific headers a part the standard one
     * @param pathParameters pathparams HOLD_BAG with productId
     */
    public RemoveApisRequest(HybrisHeaders headers, IPathParameters pathParameters) {
        super(headers, DELETE, pathParameters, null, null);
    }
}
