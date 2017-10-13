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
public class Seat {
    private String bundleCode;
    private String code;
    private String name;
    private String description;
    private Integer quantity;
    private Pricing pricing;
    private String seatNumber;
    private String seatBand;
    private List<String> seatCharacteristics;
}