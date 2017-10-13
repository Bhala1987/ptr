package com.hybris.easyjet.fixture.hybris.invoke.requestbody.managebooking;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by giuseppecioce on 27/07/2017.
 */
@Getter
@Setter
@Builder
public class PaymentBalanceRequestBody implements IRequestBody {
    private List<PaymentMethod> paymentMethods;

    @Getter
    @Setter
    @Builder
    public static class PaymentMethod {
        private String paymentMethod;
        private String paymentCode;
        private Double paymentAmount;
    }
}
