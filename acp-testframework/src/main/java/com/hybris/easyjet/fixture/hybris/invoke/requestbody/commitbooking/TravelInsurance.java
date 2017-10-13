package com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TravelInsurance {
    private String bundleCode;
    private String code;
    private String name;
    private String description;
    private Integer quantity;
    private Pricing pricing;
    private String externalPassengerId;
    private String travelType;
    private String originAirport;
    private String originAirportCountry;
    private String destinationAirport;
    private String destinationAirportCountry;
    private List<TravelInsurancePolicy> travelInsurancePolicies = null;
}
