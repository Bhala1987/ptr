package com.hybris.easyjet.fixture.hybris.invoke.response.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Address {
    private String code;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String city;
    private String county_state;
    private String country;
    private String postalCode;
}