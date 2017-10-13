package com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Address {
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String city;
    private String countyState;
    private String country;
    private String postalCode;
}
