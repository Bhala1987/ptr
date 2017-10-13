package com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Sector {
    private String code;
    private Airport departure;
    private Airport arrival;
    private Boolean apisRequired;
    private Boolean nifNumberRequired;
}