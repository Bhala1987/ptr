package com.hybris.easyjet.fixture.hybris.invoke.response.common;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public abstract class AbstractProductItem {
    private String orderEntryNumber;
    private String type;
    private String bundleCode;
    private String code;
    private String name;
    private String description;
    private Integer quantity;
    private Pricing pricing;
    private String entryStatus;
    private Boolean active;
}
