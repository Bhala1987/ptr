package com.hybris.easyjet.fixture.hybris.asserters.customer.managepaymentdetails;

import com.hybris.easyjet.fixture.hybris.asserters.Assertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.managepaymentdetails.SavedPaymentMethodResponse;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by giuseppecioce on 21/06/2017.
 */
public class SavedPaymentMethodAssertion extends Assertion<SavedPaymentMethodAssertion, SavedPaymentMethodResponse> {
    public SavedPaymentMethodAssertion(SavedPaymentMethodResponse aResponse) {
        this.response = aResponse;
    }

    public SavedPaymentMethodAssertion verifyPaymentMethodEntityHasBeenStored(List<String> availablePaymentForCustomer) {
        assertThat(availablePaymentForCustomer)
                .withFailMessage("No payment entity has been stored against the customer profile")
                .isNotNull();

        assertThat(availablePaymentForCustomer)
                .withFailMessage("No payment entity has been stored against the customer profile")
                .isNotEmpty();

        if(Objects.nonNull(this.response)) {
            assertThat(availablePaymentForCustomer)
                    .withFailMessage("The list of available payment method for the customer, does not contain the desired payment method entity")
                    .contains(this.response.getOperationConfirmation().getSavedPaymentMethodReference());
        }
        return this;
    }

    public SavedPaymentMethodAssertion verifyPaymentMethodHasBeenSettedAsDefault(String actualPaymentRef) {
        assertThat(this.response.getOperationConfirmation().getSavedPaymentMethodReference())
                .withFailMessage("The payment method associated as default for the customer is not the expected one")
                .isEqualTo(actualPaymentRef);
        return this;
    }


}
