package com.hybris.easyjet.fixture.hybris.invoke.response.common;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by g.dimartino on 21/05/17.
 */
@Getter
@Setter
public abstract class LoginResponse<C extends LoginResponse.AuthenticationConfirmation> extends Response {
    private C authenticationConfirmation;

    @JsonGetter("agentAuthenticationConfirmation")
    public C getAgentAuthenticationConfirmation() {
        return authenticationConfirmation;
    }

    @JsonSetter("agentAuthenticationConfirmation")
    public void setAgentAuthenticationConfirmation(C agentAuthenticationConfirmation) {
        this.authenticationConfirmation = agentAuthenticationConfirmation;
    }

    public enum PasswordStrength {
        WEAK, MEDIUM, STRONG
    }

    @Getter
    @Setter
    public static class AuthenticationConfirmation extends AbstractConfirmation.OperationConfirmation {
        private Authentication authentication;
        private PasswordStrength passwordStrength;
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