package com.hybris.easyjet.fixture.hybris.invoke.response.booking;

import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * Created by Niyi Falade on 24/07/17.
 */
@Setter
@Getter
public class CancelBookingRefundResponse extends Response {

    private ArrayList<RefundOrFee> refundsAndFees = new ArrayList<>();
    private BookingCancellationConfirmation bookingCancellationConfirmation;


    @Getter
    @Setter
    @Builder
    public static class RefundOrFee {
        private String type;
        private double amount;
        private String currency;
        private String primaryReasonCode;
        private String primaryReasonName;
        private String orginalPaymentMethod;
        private String originalPaymentContext;

    }

    @Getter
    @Setter
    public static class BookingCancellationConfirmation{
        private String bookingReference;
        private String bookingStatus;

    }


}


