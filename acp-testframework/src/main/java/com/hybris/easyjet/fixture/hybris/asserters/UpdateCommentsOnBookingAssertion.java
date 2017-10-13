package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.booking.operationconfirmation.UpdateCommentsOnBookingResponse;

/**
 * Created by rajakm on 03/08/2017.
 */
public class UpdateCommentsOnBookingAssertion extends Assertion<UpdateCommentsOnBookingAssertion, UpdateCommentsOnBookingResponse> {
    public UpdateCommentsOnBookingAssertion(UpdateCommentsOnBookingResponse updateCommentsOnBookingResponse) {
        this.response = updateCommentsOnBookingResponse;
    }
}
