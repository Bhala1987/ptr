package com.hybris.easyjet.fixture.hybris.invoke.requestbody.customerpreferences;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.CustomerProfileResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TravelPreferences {
    public List<String> preferredAirports;
    public List<String> travellingTo;
    public List<String> tripTypes;
    public List<String> travellingWhen;
    public List<String> travellingWith;
    public List<String> travellingSeasons;
    public CustomerProfileResponse.Period travellingPeriod;
}