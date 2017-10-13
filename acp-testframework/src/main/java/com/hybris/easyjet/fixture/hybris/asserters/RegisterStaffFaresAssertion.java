package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.customer.registerStaffFares.RegisterStaffFaresResponse;

import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;

public class RegisterStaffFaresAssertion extends Assertion<RegisterStaffFaresAssertion, RegisterStaffFaresResponse> {
    private final String FAIL_MESSAGE = "Staff fare registration not successfull";

    public RegisterStaffFaresAssertion(RegisterStaffFaresResponse registerStaffFaresResponse) {

        this.response = registerStaffFaresResponse;
    }

    public RegisterStaffFaresAssertion registrationIsConfirmed() {
        assertThat( of(response).isPresent() )
                .isTrue()
                .withFailMessage(FAIL_MESSAGE)
        ;

        assertThat( of(response.getRegistrationConfirmation().getCustomerId()).isPresent() )
                .isTrue()
                .withFailMessage(FAIL_MESSAGE)
        ;
        assertThat( of(response.getRegistrationConfirmation().getHref()).isPresent() )
                .isTrue()
                .withFailMessage(FAIL_MESSAGE)
        ;

        return this;
    }
}
