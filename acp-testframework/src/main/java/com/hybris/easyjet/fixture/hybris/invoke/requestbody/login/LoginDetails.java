package com.hybris.easyjet.fixture.hybris.invoke.requestbody.login;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class LoginDetails implements IRequestBody {

    private String email;
    private String password;
    private Boolean rememberme;

    public LoginDetails withEmail(String email) {
        this.email = email;
        return this;
    }

    public LoginDetails withPassword(String password) {
        this.password = password;
        return this;
    }

    public LoginDetails withRememberme(Boolean rememberMe) {
        this.rememberme = rememberMe;
        return this;
    }

}
