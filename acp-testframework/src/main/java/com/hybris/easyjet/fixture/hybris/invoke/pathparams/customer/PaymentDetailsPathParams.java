package com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer;

import com.hybris.easyjet.fixture.hybris.invoke.pathparams.PathParameters;
import lombok.Builder;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.PaymentDetailsPathParams.PaymentDetailsPaths.DEFAULT;

@Builder
public class PaymentDetailsPathParams extends PathParameters {

    private static final String BASE_URI = "payment-methods";
    private static final String CREDIT_PATH = "credit-cards";
    private static final String DEBIT_PATH = "debit-cards";
    private static final String BANK_PATH = "bank-accounts";
    private static final String UPDATE_PATH = "make-default-request";
    private static final String REMOVE_PATH = "remove-payment-method-request";

    private String customerId;
    private String savedPaymentMethodReferenceId;
    @Builder.Default
    private PaymentDetailsPaths path = DEFAULT;

    @Override
    public String get() {

        if (!isPopulated(customerId)) {
            throw new IllegalArgumentException("You must specify a customerId for this service.");
        }

        List<String> uri = new ArrayList<>();
        uri.add(customerId);
        uri.add(BASE_URI);
        switch (this.path) {
            case DEFAULT:
                break;
            case CREDIT_CARD:
                uri.add(CREDIT_PATH);
                if (StringUtils.isNotBlank(savedPaymentMethodReferenceId)) {
                    uri.add(savedPaymentMethodReferenceId);
                    uri.add(UPDATE_PATH);
                }
                break;
            case DEBIT_CARD:
                uri.add(DEBIT_PATH);
                if (StringUtils.isNotBlank(savedPaymentMethodReferenceId)) {
                    uri.add(savedPaymentMethodReferenceId);
                    uri.add(UPDATE_PATH);
                }
                break;
            case BANK_ACCOUNT:
                uri.add(BANK_PATH);
                if (StringUtils.isNotBlank(savedPaymentMethodReferenceId)) {
                    uri.add(savedPaymentMethodReferenceId);
                    uri.add(UPDATE_PATH);
                }
                break;
            case REMOVE:
                uri.add(REMOVE_PATH);
                break;
        }

        return StringUtils.join(uri, '/');

    }

    public enum PaymentDetailsPaths {
        DEFAULT,
        CREDIT_CARD,
        DEBIT_CARD,
        BANK_ACCOUNT,
        REMOVE
    }

}