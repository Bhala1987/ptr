package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingSummaryResponse;

/**
 * Created by rajakm on 02/08/2017.
 */
public class BookingSummaryAssertion extends Assertion<BookingAssertion, GetBookingSummaryResponse> {

    public BookingSummaryAssertion(GetBookingSummaryResponse getBookingSummaryResponse) {

        this.response = getBookingSummaryResponse;
    }
}
