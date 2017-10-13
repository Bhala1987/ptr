package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.customer.CustomerLoginResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by daniel on 26/11/2016.
 * assertion wrapper for customer login response object, provides reusable assertions to all tests
 */
public class CustomerLoginAssertion extends Assertion<CustomerLoginAssertion, CustomerLoginResponse> {

    public CustomerLoginAssertion(CustomerLoginResponse customerLoginResponse) {
        this.response = customerLoginResponse;
    }

    public CustomerLoginAssertion theLoginWasSuccesful() {
        assertThat(HybrisService.theJSessionCookie.get()).isNotNull();

        assertThat(response.getAuthenticationConfirmation().getCustomerId()).isNotEmpty();
        assertThat(response.getAuthenticationConfirmation().getAuthentication()).isNotNull();
        assertThat(response.getAuthenticationConfirmation().getAuthentication().getTokenType()).isEqualTo("bearer");

        return this;
    }

    public CustomerLoginAssertion theRememberMeCookieIsSet() {
        assertThat(HybrisService.theRememberMeCookie.get()).isNotNull();

        return this;
    }
}
