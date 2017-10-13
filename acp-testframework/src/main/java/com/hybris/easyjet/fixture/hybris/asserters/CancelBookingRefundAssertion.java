package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.booking.CancelBookingRefundResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Niyi Falade on 24/07/17.
 * Updated by bhalasaravananthiruvarangamrajalakshmi on 23/08/2017.
 */
public class CancelBookingRefundAssertion extends Assertion<CancelBookingRefundAssertion, CancelBookingRefundResponse> {

    public CancelBookingRefundAssertion(CancelBookingRefundResponse cancelBookingRefundResponse) {
        this.response = cancelBookingRefundResponse;
    }

    public void bookingReferenceAssertion(String bookingReference) {
        assertThat(response.getBookingCancellationConfirmation().getBookingReference().equalsIgnoreCase(bookingReference)).withFailMessage("Expected booking reference is "+bookingReference+" but the actual booking reference is "+response.getBookingCancellationConfirmation().getBookingReference()).isTrue();
    }

    public void bookingStatusAssertion(String bookingStatus) {
        assertThat(response.getBookingCancellationConfirmation().getBookingStatus().equalsIgnoreCase(bookingStatus)).withFailMessage("Expected booking status is "+bookingStatus+ " but the actual booking status is "+response.getBookingCancellationConfirmation().getBookingStatus()).isTrue();
    }
}
