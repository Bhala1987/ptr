package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.savedpassenger.operationconfirmation.UpdateIdentityDocumentResponse;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by giuseppecioce on 16/02/2017.
 */
public class UpdateIdentityDocumentAssertion extends Assertion<UpdateIdentityDocumentAssertion, UpdateIdentityDocumentResponse> {

    public UpdateIdentityDocumentAssertion(UpdateIdentityDocumentResponse updateIdentityDocumentResponse) {
        this.response = updateIdentityDocumentResponse;
    }

    public UpdateIdentityDocumentAssertion verifySuccessfullyUpdateForIdentityDocument(String customerId, String passengerId, String documentId) {
        assertThat(Objects.nonNull(response.getUpdateConfirmation()));
        assertThat(response.getUpdateConfirmation().getCustomerId()).isEqualTo(customerId);
        assertThat(response.getUpdateConfirmation().getPassengerId()).isEqualTo(passengerId);
        assertThat(response.getUpdateConfirmation().getDocumentId()).isEqualTo(documentId);
        return this;
    }

}