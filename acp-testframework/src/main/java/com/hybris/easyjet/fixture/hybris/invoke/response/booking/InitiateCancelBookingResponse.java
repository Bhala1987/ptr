package com.hybris.easyjet.fixture.hybris.invoke.response.booking;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * Response returned from initiate cancel booking request.
 *
 * @author Joshua Curtis <j.curtis@reply.com>
 */
@Getter
@Setter
@JsonRootName("initiateCancellationConfirmation")
public class InitiateCancelBookingResponse extends Response {
    private InitiateCancellationConfirmation initiateCancellationConfirmation;

    @Getter
    @Setter
    public static class InitiateCancellationConfirmation {
        private String bookingReference;

        private String bookingStatus;

        private ArrayList<RefundOrFee> refundsAndFees = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class RefundOrFee {
        private String type;

        private Double amount;

        private String currency;

        private String feeCode;

        private String feeName;

        private String primaryReasonCode;

        private String primaryReasonName;

        private String originalPaymentMethod;

        private String originalPaymentMethodContext;
    }
}
