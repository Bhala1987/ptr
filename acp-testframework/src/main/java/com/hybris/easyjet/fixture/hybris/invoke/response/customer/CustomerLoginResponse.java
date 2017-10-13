package com.hybris.easyjet.fixture.hybris.invoke.response.customer;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.LoginResponse;
import lombok.Getter;
import lombok.Setter;

public class CustomerLoginResponse extends LoginResponse<CustomerLoginResponse.AuthenticationConfirmation> {

    @Getter
    @Setter
    public static class AuthenticationConfirmation extends LoginResponse.AuthenticationConfirmation {
        private String customerId;
    }

}