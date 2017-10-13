package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.dependants.operationconfirmation.IdentityDocumentResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by adevanna on 13/03/17.
 */
public class SignificantOtherIdDocumentAssertion extends Assertion<SignificantOtherIdDocumentAssertion, IdentityDocumentResponse> {

    public SignificantOtherIdDocumentAssertion(IdentityDocumentResponse identityDocumentResponse) {

        this.response = identityDocumentResponse;
    }

    public void significantOtherIsSuccessfullyAddedToTheCustomer(String customerId, String passengerId) {

        IdentityDocumentResponse.OperationConfirmation confirmation = response.getUpdateConfirmation();

        assertThat(confirmation.getCustomerId()).isNotNull();
        assertThat(confirmation.getCustomerId()).isEqualTo(customerId);
        assertThat(confirmation.getPassengerId()).isNotNull();
        assertThat(confirmation.getPassengerId()).isEqualTo(passengerId);
        assertThat(confirmation.getDocumentId()).isNotNull();

    }

    public void significantOtherIsSuccessfullyUpdatedToTheCustomer(String customerId, String passengerId, String documentId) {

        IdentityDocumentResponse.OperationConfirmation confirmation = response.getUpdateConfirmation();

        assertThat(confirmation.getCustomerId()).isNotNull();
        assertThat(confirmation.getCustomerId()).isEqualTo(customerId);
        assertThat(confirmation.getPassengerId()).isNotNull();
        assertThat(confirmation.getPassengerId()).isEqualTo(passengerId);
        assertThat(confirmation.getDocumentId()).isNotNull();
        assertThat(confirmation.getDocumentId()).isEqualTo(documentId);
    }

    public void documentIsSuccessfullyRemovedFromTheSignificantOther(String customerId, String passengerId) {

        IdentityDocumentResponse.OperationConfirmation confirmation = response.getUpdateConfirmation();

        assertThat(confirmation.getCustomerId()).isNotNull();
        assertThat(confirmation.getCustomerId()).isEqualTo(customerId);
        assertThat(confirmation.getPassengerId()).isNotNull();
        assertThat(confirmation.getPassengerId()).isEqualTo(passengerId);
        assertThat(confirmation.getDocumentId()).isNull();
    }
}
