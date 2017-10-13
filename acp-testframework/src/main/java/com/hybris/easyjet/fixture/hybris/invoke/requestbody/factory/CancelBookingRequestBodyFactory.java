package com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory;

import com.hybris.easyjet.fixture.hybris.invoke.requestbody.CancelBookingRefundRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.InitiateCancelBookingResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 22/08/2017.
 */
@Component
public class CancelBookingRequestBodyFactory {

    public CancelBookingRefundRequestBody cancelBookingRequestBodyFactory(ArrayList<InitiateCancelBookingResponse.RefundOrFee> refundOrFees){

        return CancelBookingRefundRequestBody.builder().refundsAndFees(new ArrayList<CancelBookingRefundRequestBody.RefundOrFee>() {{

            refundOrFees.forEach(refundOrFee -> add(CancelBookingRefundRequestBody.RefundOrFee.builder()
                            .type(refundOrFee.getType())
                            .amount(refundOrFee.getAmount())
                            .currency(refundOrFee.getCurrency())
                            .primaryReasonCode(refundOrFee.getPrimaryReasonCode())
                            .primaryReasonName(refundOrFee.getPrimaryReasonName())
                    .originalPaymentMethod(refundOrFee.getOriginalPaymentMethod())
                    .originalPaymentMethodContext(refundOrFee.getOriginalPaymentMethodContext())
                            .feeCode(refundOrFee.getFeeCode())
                            .feeName(refundOrFee.getFeeName())
                    .build()));
        }
        }).build();
    }

}
