package com.hybris.easyjet.fixture.hybris.invoke.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by markphipps on 12/04/2017.
 */
@Getter
@Setter
public class InternalPaymentFundsResponse extends Response {
    private List<InternalPaymentFund> creditFiles = new ArrayList<>();
    private List<InternalPaymentFund> vouchers = new ArrayList<>();

    @Getter
    @Setter
    public static class InternalPaymentFund {
        private String code;
        private String name;
        private String currencyCode;
        private Double originalBalance;
        private Double currentBalance;
        private String created;
        private String expiry;
    }

}
