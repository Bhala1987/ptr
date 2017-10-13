package com.hybris.easyjet.fixture.hybris.invoke.requestbody;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * Created by Niyi Falade on 24/07/17.
 */
@Getter
@Setter
@Builder
public class CancelBookingRefundRequestBody implements IRequestBody {

    private ArrayList<RefundOrFee> refundsAndFees;


    @Getter
    @Setter
    @Builder
    public static class RefundOrFee {
        private String type;
        private Double amount;
        private String currency;
        private String primaryReasonCode;
        private String primaryReasonName;
        private String originalPaymentMethod;
        private String originalPaymentMethodContext;
        private String feeCode;
        private String feeName;
    }


}
