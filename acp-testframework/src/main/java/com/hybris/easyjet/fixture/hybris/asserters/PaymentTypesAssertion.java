package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.PaymentTypesResponse;

/**
 * Created by daniel on 26/11/2016.
 * assertion wrapper for payment types response object, provides reusable assertions to all tests
 */
public class PaymentTypesAssertion extends Assertion<PaymentTypesAssertion, PaymentTypesResponse> {

    public PaymentTypesAssertion(PaymentTypesResponse paymentTypesResponse) {

        this.response = paymentTypesResponse;
    }

}
