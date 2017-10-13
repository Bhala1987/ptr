package com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentMethod implements IRequestBody {
    private String paymentMethod;
    private String paymentCode;
    private Card card;
    private BankAccount bankAccount;
    private MobilePayment mobilePayment;
    private VoucherPayment voucher;
    private CashPayment cash;
    private FundPayment fundPayment;
    private Double paymentAmount;
    private String paymentCurrency;
    private Boolean savePaymentMethod;
    private String savedPaymentMethodReference;
    private Map<String, Object> associatedProducts;
}