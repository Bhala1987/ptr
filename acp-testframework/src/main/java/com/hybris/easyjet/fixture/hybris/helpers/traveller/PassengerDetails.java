package com.hybris.easyjet.fixture.hybris.helpers.traveller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PassengerDetails extends Response {

    @JsonProperty("name")
    private Name name;
    @JsonProperty("phoneNumber")
    private String phoneNumber;
    @JsonProperty("email")
    private String email;
    @JsonProperty("passengerType")
    private String passengerType;
    @JsonProperty("nifNumber")
    private String nifNumber;
    @JsonProperty("ejPlusCardNumber")
    private String ejPlusCardNumber;

}