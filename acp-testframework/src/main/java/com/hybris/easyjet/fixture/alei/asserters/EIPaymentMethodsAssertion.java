package com.hybris.easyjet.fixture.alei.asserters;

import com.hybris.easyjet.fixture.alei.invokers.responses.paymentmethods.EIPaymentMethodsResponse;

/**
 * Created by marco on 21/04/17.
 */
public class EIPaymentMethodsAssertion extends Assertion<EIPaymentMethodsAssertion, EIPaymentMethodsResponse> {

   public EIPaymentMethodsAssertion(EIPaymentMethodsResponse eiPaymentMethodsResponse) {
      this.response = eiPaymentMethodsResponse;
   }
}
