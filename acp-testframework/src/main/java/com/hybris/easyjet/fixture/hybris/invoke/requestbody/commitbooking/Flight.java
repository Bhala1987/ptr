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
public class Flight {
    private String flightKey;
    private String flightNumber;
    private String carrier;
    private String departureDateTime;
    private String arrivalDateTime;
    private Sector sector;
    private List<Passenger> passengers;
}