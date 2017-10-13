package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.registercustomer.RegisterCustomerRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation.RegisterCustomerResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by dwebb on 12/15/2016.
 * assertion wrapper for register customer response object, provides reusable assertions to all tests
 */
public class RegisterCustomerAssertion extends Assertion<RegisterCustomerAssertion, RegisterCustomerResponse> {

    public RegisterCustomerAssertion(RegisterCustomerResponse registerCustomerResponse) {

        this.response = registerCustomerResponse;
    }

    public RegisterCustomerAssertion theProfileResponseIsValid() {

        assertThat(response.getRegistrationConfirmation()).isNotNull();
        return this;
    }

    public RegisterCustomerAssertion theCustomerProfileWasCreated(RegisterCustomerRequestBody request) {

        assertThat(response.getRegistrationConfirmation()).isNotNull();
        assertThat(response.getRegistrationConfirmation().getCustomerId()).isNotEmpty();

        return this;
    }

    public RegisterCustomerAssertion theCustomersPasswordStrengthAsExpected(RegisterCustomerRequestBody request, String expectedStrength) {

        assertThat(response.getRegistrationConfirmation()).isNotNull();
        assertThat(response.getRegistrationConfirmation().getPasswordStrength()).isEqualToIgnoringCase(expectedStrength);
        return this;
    }

    public RegisterCustomerAssertion authenticationNotReturned() {
        assertThat(
            response.getRegistrationConfirmation().getAuthentication()
        ).isNull();

        return this;
    }
}
