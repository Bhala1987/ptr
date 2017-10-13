package com.hybris.easyjet.fixture.hybris.invoke.response.customer.registerStaffFares;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hybris.easyjet.fixture.IResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by robertadigiorgio on 24/02/2017.
 */
@Getter
@Setter
public class RegisterStaffFaresResponse extends Response implements IResponse {

    @JsonProperty("registrationConfirmation")
    public RegistrationConfirmation registrationConfirmation;

    @Getter
    @Setter
    public class RegistrationConfirmation extends Response {
        @JsonProperty("customerId")
        public String customerId;
        @JsonProperty("href")
        public String href;
    }
}
