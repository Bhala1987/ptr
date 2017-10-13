package com.hybris.easyjet.fixture.hybris.invoke.requestbody;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL) // This body is used for a PATCH (even though it's POSTed)
public class AmendBasicDetailsRequestBody implements IRequestBody {
    private String nifNumber;
    private Integer age;
    private Name name;
    private String phoneNumber;
    private String email;
    private String diallingCode;
    private String ejPlusCardNumber;
    private String savedPassengerCode;
    private Boolean saveToCustomerProfile;
}