package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.dependants.operationconfirmation.UpdateDependantsResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by markphipps on 23/03/2017.
 */
public class UpdateDependantsAssertion extends Assertion<UpdateDependantsAssertion, UpdateDependantsResponse> {

    private final UpdateDependantsResponse updateDependantsResponse;

    public UpdateDependantsAssertion(UpdateDependantsResponse updateDependantsResponse) {
        this.updateDependantsResponse = updateDependantsResponse;
    }

    public UpdateDependantsAssertion dependantIsUpdatedForCustomer(String customerId) {
        UpdateDependantsResponse.OperationConfirmation confirmation = updateDependantsResponse.getUpdateConfirmation();

        assertThat(confirmation.getCustomerId()).isNotNull();
        assertThat(confirmation.getCustomerId()).isEqualTo(customerId);
        assertThat(confirmation.getPassengerId()).isNotNull();

        return this;
    }

    public UpdateDependantsAssertion significantOtherIsSuccessfullyUpdatedToTheCustomer(String customerId, String passengerId) {
        UpdateDependantsResponse.OperationConfirmation confirmation = updateDependantsResponse.getUpdateConfirmation();

        assertThat(confirmation.getCustomerId()).isNotNull();
        assertThat(confirmation.getCustomerId()).isEqualTo(customerId);
        assertThat(confirmation.getPassengerId()).isNotNull();
        assertThat(confirmation.getPassengerId()).isEqualTo(passengerId);

        return this;
    }
}
