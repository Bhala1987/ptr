package com.hybris.easyjet.fixture.hybris.invoke.requests.booking;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.GetAmendableBookingRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.HybrisRequest;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;

/**
 * Created by vijayapalkayyam on 15/05/2017.
 */
public class GetAmendableBookingRequest extends HybrisRequest implements IRequest {

    public GetAmendableBookingRequest(HybrisHeaders headers, IPathParameters pathParameters, GetAmendableBookingRequestBody getAmendableBookingRequestBody) {
        super(headers, POST, pathParameters, null, getAmendableBookingRequestBody);
    }
}