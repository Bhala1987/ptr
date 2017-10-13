package com.hybris.easyjet.fixture.hybris.invoke.response.common;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giuseppedimartino on 04/07/17.
 */
@Getter
@Setter
public class OfferPrice {
    private Double withCreditCardFee;
    private Double withDebitCardFee;
    private WithEJPlus withEJPlus;

    @Getter
    @Setter
    public static class WithEJPlus {
        private Staff staff;
        private Standard standard;

        @Getter
        @Setter
        public static class Staff {
            private Double withCreditCardFee;
            private Double withDebitCardFee;
        }

        @Getter
        @Setter
        public static class Standard {
            private Double withCreditCardFee;
            private Double withDebitCardFee;
        }
    }
}