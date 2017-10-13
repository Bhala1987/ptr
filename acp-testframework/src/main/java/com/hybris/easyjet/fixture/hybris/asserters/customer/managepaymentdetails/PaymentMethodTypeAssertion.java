package com.hybris.easyjet.fixture.hybris.asserters.customer.managepaymentdetails;

import com.hybris.easyjet.fixture.hybris.asserters.Assertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.managepaymentdetails.PaymentMethodTypeResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Niyi Falade on 19/07/17.
 */
public class PaymentMethodTypeAssertion extends Assertion<PaymentMethodTypeAssertion, PaymentMethodTypeResponse> {
    public PaymentMethodTypeAssertion(PaymentMethodTypeResponse aResponse) {
        this.response = aResponse;
    }


    public PaymentMethodTypeAssertion verifyDebitCardDetailsiSPopulated(){
        assertThat(this.response.getDebitCards().size()> 1)
                .withFailMessage("No debit card details saved - Please verify you have saved your payment type");
        assertThat(this.response.getDebitCards().get(0).getPaymentMethodCode().contains("DM"));
        return this;
    }

    public PaymentMethodTypeAssertion verifyExpirediSNotPopulated(){
        assertThat(this.response.getCreditCards().isEmpty())
                .withFailMessage("expired card returned");
        return this;
    }
}
