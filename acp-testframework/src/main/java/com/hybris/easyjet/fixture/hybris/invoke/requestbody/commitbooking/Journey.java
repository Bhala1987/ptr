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
public class Journey {
    private Boolean isDirect;
    private String totalDuration;
    private Integer stops;
    private Double journeyTotalWithCreditCard;
    private Double journeyTotalWithDebitCard;
    private List<Flight> flights;
}