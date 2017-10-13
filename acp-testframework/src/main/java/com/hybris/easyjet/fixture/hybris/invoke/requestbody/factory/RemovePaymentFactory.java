package com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory;

import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.managepaymentdetails.RemoveSavedPaymentRequestBody;

/**
 * Created by sudhir on 04/07/2017.
 */
public class RemovePaymentFactory {
    public static RemoveSavedPaymentRequestBody aBasicAddBankPaymentDetails(String savedPaymentRef) {
        return RemoveSavedPaymentRequestBody.builder()
                .savedPaymentMethodRef(savedPaymentRef)
                .build();
    }
}
