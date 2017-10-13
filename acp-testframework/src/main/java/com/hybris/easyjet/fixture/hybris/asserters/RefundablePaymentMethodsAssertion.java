package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.response.RefundPaymentMethodsResponse;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Java6Assertions.assertThat;



@NoArgsConstructor
public class RefundablePaymentMethodsAssertion extends Assertion<RefundablePaymentMethodsAssertion, RefundPaymentMethodsResponse> {

    public RefundablePaymentMethodsAssertion(RefundPaymentMethodsResponse paymentMethodsResponse) {
        this.response = paymentMethodsResponse;
    }


   public RefundablePaymentMethodsAssertion isIncludedInPaymentMethods(String paymentMethod) throws EasyjetCompromisedException {
      assertThat(response.getRefundPaymentMethods().stream()
            .filter(p -> paymentMethod.equals(p.getCode())).count() == 1)
            .isTrue();
      return this;
   }

   public RefundablePaymentMethodsAssertion isNotIncludedInPaymentMethods(String paymentMethod) throws EasyjetCompromisedException {
      assertThat(response.getRefundPaymentMethods().stream()
            .anyMatch(p -> paymentMethod.equals(p.getCode())))
            .isFalse();
      return this;
   }

   public RefundablePaymentMethodsAssertion areIncludedOnlyNMethods(int numMethods) throws EasyjetCompromisedException {
      assertThat(response.getRefundPaymentMethods().size() == numMethods)
            .isTrue();
      return this;
   }

   public RefundablePaymentMethodsAssertion areIncludedConfiguredPaymentMethods(List<String> refundPaymentMethods) {
      List<String> refundPaymCodes = response.getRefundPaymentMethods().stream().map(s -> s.getCode())
            .collect(Collectors.toList());
      assertThat(thePaymentListsAreEqual(refundPaymCodes, refundPaymentMethods)).isTrue();
      return this;
   }

   private static boolean thePaymentListsAreEqual(List<String> refundPaymentMethodsCodes,
         List<String> refundPaymentMethods) {
      boolean sameSize = refundPaymentMethodsCodes.size()==refundPaymentMethods.size();
      refundPaymentMethods.removeAll(refundPaymentMethodsCodes);
      return sameSize && refundPaymentMethods.isEmpty();
   }

}
