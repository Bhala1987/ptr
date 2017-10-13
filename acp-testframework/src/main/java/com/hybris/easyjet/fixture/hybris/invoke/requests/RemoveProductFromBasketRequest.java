package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IQueryParams;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.DELETE;

/**
 * Created by giuseppedimartino on 30/03/17.
 */
public class RemoveProductFromBasketRequest extends HybrisRequest implements IRequest {

    /**
     * this class models a request to delete the hold bags from the basket
     *
     * @param headers         no specific headers a part the standard one
     * @param pathParameters  pathparams HOLD_BAG with productId
     * @param queryParameters not mandatory; it can include: passengerCode and/or flightKey; and, for excessWeight products excessWeightProductCode and excessWeightQuantity
     */
    public RemoveProductFromBasketRequest(HybrisHeaders headers, IPathParameters pathParameters, IQueryParams queryParameters) {
        super(headers, DELETE, pathParameters, queryParameters, null);
    }
}
