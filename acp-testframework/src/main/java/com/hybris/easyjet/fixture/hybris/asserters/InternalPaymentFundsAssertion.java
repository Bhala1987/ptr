package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.InternalPaymentFundsResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by markphipps on 12/04/2017.
 */
public class InternalPaymentFundsAssertion extends Assertion<InternalPaymentFundsAssertion, InternalPaymentFundsResponse> {

    public InternalPaymentFundsAssertion (InternalPaymentFundsResponse internalPaymentFundsResponse) {
        this.response = internalPaymentFundsResponse;
    }

    public void internalPaymentFundsWereReturned () {
        int combinedSize = response.getVouchers().size() + response.getCreditFiles().size();
        assertThat(combinedSize).isGreaterThan(0)
                .withFailMessage("No internal payment funds were returned.");
    }
}
