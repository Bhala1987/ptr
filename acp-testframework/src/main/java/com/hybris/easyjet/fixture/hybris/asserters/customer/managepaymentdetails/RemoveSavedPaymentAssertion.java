package com.hybris.easyjet.fixture.hybris.asserters.customer.managepaymentdetails;

import com.hybris.easyjet.fixture.hybris.asserters.Assertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.CustomerProfileResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.managepaymentdetails.PaymentMethodTypeResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.managepaymentdetails.RemoveSavedPaymentResponse;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by sudhir on 05/04/2017.
 */
public class RemoveSavedPaymentAssertion extends Assertion<RemoveSavedPaymentAssertion, RemoveSavedPaymentResponse> {
    public RemoveSavedPaymentAssertion(RemoveSavedPaymentResponse aResponse) {
        this.response = aResponse;
    }

    public RemoveSavedPaymentAssertion verifyPaymentMethodHasBeenDeleted(CustomerProfileResponse.Customer customer, String savedPaymentMethodReference) {

        boolean isPresent = false;
        List<PaymentMethodTypeResponse.PaymentCard> paymentMethods = customer.getAdvancedProfile().getSavedPayments().getDebitCards().stream().collect(Collectors.toList());
        for(PaymentMethodTypeResponse.PaymentCard paymentCard: paymentMethods) {
            if (paymentCard.getSavedPaymentMethodReference().equalsIgnoreCase(savedPaymentMethodReference)) {
                isPresent = true;
                break;
            }
        }
        assertThat(isPresent)
                .withFailMessage("The payment method with ref " + savedPaymentMethodReference + " is still present against the customer profile")
                .isFalse();

        return this;
    }
}
