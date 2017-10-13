package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.booking.operationconfirmation.AddCommentToBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.GetBookingService;

/**
 * Created by rajakm on 05/05/2017.
 */
public class AddCommentToBookingAssertion extends Assertion<AddCommentToBookingAssertion, AddCommentToBookingResponse> {
    private GetBookingService getBookingService;

    public AddCommentToBookingAssertion(AddCommentToBookingResponse addCommentToBookingResponse) {

       this.response = addCommentToBookingResponse;
    }
}
