package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation.ResetPasswordResponse;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by giuseppecioce on 01/03/2017.
 */
public class ResetPasswordAssertion extends Assertion<ResetPasswordAssertion, ResetPasswordResponse> {

    public ResetPasswordAssertion(ResetPasswordResponse resetPasswordResponse) {

        this.response = resetPasswordResponse;
    }

    public ResetPasswordAssertion verifyTheTokenHasBeenCreated(String resetToken) {

        assertThat(Objects.nonNull(resetToken)).isEqualTo(true);
        return this;
    }
}
