package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IQueryParams;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.GET;

/**
 * Created by robertadigiorgio on 10/03/2017.
 */
public class GetAlternateAirportsRequest extends HybrisRequest implements IRequest {
    /**
     * @param headers
     * @param queryParameters
     */
    public GetAlternateAirportsRequest(HybrisHeaders headers, IQueryParams queryParameters) {
        super(headers, GET, null, queryParameters, null);
    }
}