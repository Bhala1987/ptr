package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.savedpassenger.operationconfirmation.UpdateSavedPassengerResponse;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by giuseppecioce on 16/02/2017.
 */
public class UpdateSavedPassengerAssertion extends Assertion<UpdateSavedPassengerAssertion, UpdateSavedPassengerResponse> {

    public UpdateSavedPassengerAssertion(UpdateSavedPassengerResponse updateSavedPassengerResponse) {
        this.response = updateSavedPassengerResponse;
    }

    public UpdateSavedPassengerAssertion verifySuccessfullyUpdate(String customerId, String passengerId) {
        assertThat(Objects.nonNull(response.getUpdateConfirmation()));
        assertThat(response.getUpdateConfirmation().getCustomerId()).isEqualTo(customerId);
        assertThat(response.getUpdateConfirmation().getPassengerId()).isEqualTo(passengerId);
        return this;
    }

    public UpdateSavedPassengerAssertion verifySuccessfullyRemoveAllIdentityDocs(String customerId, String passengerId) {
        assertThat(Objects.nonNull(response.getUpdateConfirmation())).isEqualTo(true);
        assertThat(response.getUpdateConfirmation().getCustomerId()).isEqualTo(customerId);
        assertThat(response.getUpdateConfirmation().getPassengerId()).isEqualTo(passengerId);
        return this;
    }

}