package com.hybris.easyjet.fixture.hybris.invoke.response.managebooking;

import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by giuseppecioce on 27/07/2017.
 */
@Getter
@Setter
public class PaymentBalanceResponse  extends Response {
    private ProposedPayments proposedPayments;
    private RemainingBalance remainingBalance;

    @Getter
    @Setter
    public static class ProposedPayments {
        private List<PaymentMethod> paymentMethods;

        @Getter
        @Setter
        public static class PaymentMethod {
            private String paymentMethod;
            private String paymentCode;
            private Double paymentAmount;
            private Double feeAmount;
        }
    }

    @Getter
    @Setter
    public static class RemainingBalance {
        private Double withCreditCard;
        private Double withDebitCard;
    }
}
