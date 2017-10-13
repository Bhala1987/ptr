package com.hybris.easyjet.fixture.hybris.invoke.requests.refdata;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.HybrisRequest;

import static com.hybris.easyjet.config.constants.HttpMethods.GET;

/**
 * Created by daniel on 26/11/2016.
 * represents a request to the getAirports() service
 */
public class AirportsRequest extends HybrisRequest implements IRequest {

    /**
     * only headers need to be provided to the constructor
     *
     * @param headers
     */
    public AirportsRequest(HybrisHeaders headers) {
        super(headers, GET, null, null, null);
    }
}
