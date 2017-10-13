package com.hybris.easyjet.fixture.hybris.invoke.response.customer.managepaymentdetails;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Niyi Falade on 19/07/17.
 */
@Getter
@Setter
public class PaymentMethodTypeResponse extends Response {
    private List<BankAccounts> bankAccounts = new ArrayList();
    private List<PaymentCard> creditCards = new ArrayList();
    private List<PaymentCard> debitCards = new ArrayList();


    @Getter
    @Setter
    public static class PaymentCard {
        private String cardExpiryMonth;
        private String cardExpiryYear;
        private String cardHolderName;
        private String cardIssueNumber;
        private String cardLast4Digits;
        private String cardValidFromMonth;
        private String cardValidFromYear;
        private String code;
        @JsonIgnore
        private String isDefault;
        private String isExpired;
        private String paymentMethod;
        private String paymentMethodCode;
        private String cardType;
        private String paymentMethodId;
        private String savedPaymentMethodReference;
    }

    @Getter
    @Setter
    public static class BankAccounts {
        private String code;
        private String paymentMethodId;
        private String paymentMethod;
        private String paymentMethodCode;
        private String savedPaymentMethodReference;
        private String accountHolderName;
        private String accountNumberLast4Digits;
        private String bankCity;
        private String bankCode;
        private String bankCountryCode;
        private String bankName;
        @JsonIgnore
        private String isDefault;

    }



}
