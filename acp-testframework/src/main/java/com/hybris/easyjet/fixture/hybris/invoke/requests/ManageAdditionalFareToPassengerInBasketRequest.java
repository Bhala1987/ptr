package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IQueryParams;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.DELETE;
import static com.hybris.easyjet.config.constants.HttpMethods.POST;

/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 12/04/2017.
 */
public class ManageAdditionalFareToPassengerInBasketRequest extends HybrisRequest implements IRequest {

    /**
     * this class models a request that can be sent to a service
     *
     * @param headers        the headers that should be sent as part of the request
     * @param pathParams     any path parameters required
     * @param requestBody    any request body required
     */
    public ManageAdditionalFareToPassengerInBasketRequest(HybrisHeaders headers, IPathParameters pathParams, IRequestBody requestBody) {
        super(headers, POST, pathParams, null, requestBody);
    }

    /**
     * this class models a request that can be sent to a service
     *
     * @param headers        the headers that should be sent as part of the request
     * @param pathParams     any path parameters required
     */
    public ManageAdditionalFareToPassengerInBasketRequest(HybrisHeaders headers, IPathParameters pathParams, IQueryParams queryParams) {
        super(headers, DELETE, pathParams, queryParams, null);
    }
}
