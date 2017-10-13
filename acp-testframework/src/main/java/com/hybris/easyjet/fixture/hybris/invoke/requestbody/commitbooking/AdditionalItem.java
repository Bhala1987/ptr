package com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdditionalItem {

    private String type;
    private String bundleCode;
    private String code;
    private String name;
    private String description;
    private Integer quantity;
    private Pricing pricing;

}