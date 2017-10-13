package com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExtraWeight {
    private String bundleCode;
    private String code;
    private String name;
    private String description;
    private Integer quantity;
    private Pricing pricing;
}
