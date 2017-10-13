package com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractConfirmation;
import lombok.Getter;
import lombok.Setter;

public class RegisterCustomerResponse extends AbstractConfirmation<RegisterCustomerResponse.OperationConfirmation> {

    @Getter
    @Setter
    public static class OperationConfirmation extends UpdateConfirmationResponse.OperationConfirmation {
        private String passwordStrength;
        private Authentication authentication;
    }

    @Getter
    @Setter
    public static class Authentication {
        private String accessToken;
        private String tokenType;
        private Integer expiresIn;
        private String refreshToken;
        private String scope;
    }

}