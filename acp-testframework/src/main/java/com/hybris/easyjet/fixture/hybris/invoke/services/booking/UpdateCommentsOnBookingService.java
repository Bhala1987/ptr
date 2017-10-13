package com.hybris.easyjet.fixture.hybris.invoke.services.booking;

import com.hybris.easyjet.fixture.IAssertion;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.operationconfirmation.UpdateCommentsOnBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by rajakm on 03/08/2017.
 */
public class UpdateCommentsOnBookingService extends HybrisService implements IService {
    private UpdateCommentsOnBookingResponse updateCommentsOnBookingResponse;

    public UpdateCommentsOnBookingService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public UpdateCommentsOnBookingResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return updateCommentsOnBookingResponse;
    }

    @Override
    public IAssertion assertThat() {
        return null;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(updateCommentsOnBookingResponse);
    }

    @Override
    protected void mapResponse() {
        updateCommentsOnBookingResponse = restResponse.as(UpdateCommentsOnBookingResponse.class);

    }
}
