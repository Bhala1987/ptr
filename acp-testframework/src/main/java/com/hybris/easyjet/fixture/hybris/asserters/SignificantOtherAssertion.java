package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.dependants.operationconfirmation.SignificantOtherResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by claudiodamico on 09/03/2017.
 */
public class SignificantOtherAssertion extends Assertion<SignificantOtherAssertion, SignificantOtherResponse> {

    public SignificantOtherAssertion(SignificantOtherResponse significantOtherResponse) {

        this.response = significantOtherResponse;
    }

    public SignificantOtherAssertion significantOtherIsSuccessfullyAddedToTheCustomer(String customerId) {

        SignificantOtherResponse.OperationConfirmation confirmation = response.getUpdateConfirmation();

        assertThat(confirmation.getCustomerId()).isNotNull();
        assertThat(confirmation.getCustomerId()).isEqualTo(customerId);
        assertThat(confirmation.getPassengerId()).isNotNull();
        assertThat(confirmation.getRemainingChanges()).isNotNull();
        assertThat(confirmation.getChangesEndDate()).isNotNull();

        return this;

    }

    public SignificantOtherAssertion significantOtherIsSuccessfullyUpdatedToTheCustomer(String customerId, String passengerId) {

        SignificantOtherResponse.OperationConfirmation confirmation = response.getUpdateConfirmation();

        assertThat(confirmation.getCustomerId()).isNotNull();
        assertThat(confirmation.getCustomerId()).isEqualTo(customerId);
        assertThat(confirmation.getPassengerId()).isNotNull();
        assertThat(confirmation.getPassengerId()).isEqualTo(passengerId);
        assertThat(confirmation.getRemainingChanges()).isNotNull();
        assertThat(confirmation.getChangesEndDate()).isNotNull();

        return this;
    }

    public SignificantOtherAssertion significantOtherIsSuccessfullyRemovedFromTheCustomer(String customerId, String passengerId) {

        SignificantOtherResponse.OperationConfirmation confirmation = response.getUpdateConfirmation();

        assertThat(confirmation.getCustomerId()).isNotNull();
        assertThat(confirmation.getCustomerId()).isEqualTo(customerId);
        assertThat(confirmation.getPassengerId()).isNotNull();
        assertThat(confirmation.getPassengerId()).isEqualTo(passengerId);

        return this;
    }

    public SignificantOtherAssertion theRemainingChangesAreCorrectlyUpdated(int oldThreshold) {

        assertThat(oldThreshold - response.getUpdateConfirmation().getRemainingChanges()).isEqualTo(1);

        return this;
    }

    public SignificantOtherAssertion significantOtherIsReturned() {
        assertThat(response.getSignificantOtherResponse()).isNotNull();
        return this;
    }

}
