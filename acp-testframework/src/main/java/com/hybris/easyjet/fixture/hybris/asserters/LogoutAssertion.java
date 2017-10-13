package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.customer.LogoutResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by giuseppedimartino on 13/02/17.
 */
public class LogoutAssertion extends Assertion<LogoutAssertion, LogoutResponse> {

    public LogoutAssertion(LogoutResponse logoutResponse) {

        this.response = logoutResponse;
    }

    public LogoutAssertion customerProperlyLoggedOut() {
        assertThat(response.getAdditionalInformations()).isEmpty();
        return this;
    }
}
