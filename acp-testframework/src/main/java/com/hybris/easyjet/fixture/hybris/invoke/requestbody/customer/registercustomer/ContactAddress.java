package com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.registercustomer;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ContactAddress {

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String country;
    private String postalCode;

}