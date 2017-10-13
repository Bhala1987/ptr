package com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HoldItem {
    private String bundleCode;
    private String code;
    private String name;
    private String description;
    private Integer quantity;
    private Pricing pricing;
    private Integer maxlength;
    private Integer maxWidth;
    private Integer maxHeight;
    private Integer maxWeight;
    private List<ExtraWeight> extraWeight;
}