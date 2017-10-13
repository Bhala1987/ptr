package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation.UpdatePasswordResponse;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by robertadigiorgio on 09/02/2017.
 */
public class UpdatePasswordAssertion extends Assertion<UpdatePasswordAssertion, UpdatePasswordResponse> {

    public UpdatePasswordAssertion(UpdatePasswordResponse updatePasswordResponse) {

        this.response = updatePasswordResponse;
    }

    public UpdatePasswordAssertion strengthIsTheExpected(String expected) {

        assertThat(expected).isEqualToIgnoringCase(response.getUpdateConfirmation().getPasswordStrength());
        return this;
    }

    public UpdatePasswordAssertion returnConfirmationForUpdatePassword() {

        assertThat(Objects.nonNull(response.getUpdateConfirmation()));
        return this;
    }

    public UpdatePasswordAssertion returnStrengthForUpdatePassword() {

        assertThat(Objects.nonNull(response.getUpdateConfirmation().getPasswordStrength()));
        return this;
    }
}
