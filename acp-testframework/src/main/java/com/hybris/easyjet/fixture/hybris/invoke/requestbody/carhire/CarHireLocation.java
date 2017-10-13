package com.hybris.easyjet.fixture.hybris.invoke.requestbody.carhire;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
public class CarHireLocation implements IRequestBody {
    private String pickUpAirport;
    private String pickUpStation;
    private String pickUpDate;
    private String pickUpTime;
    private String dropOffAirport;
    private String dropOffStation;
    private String dropOffDate;
    private String dropOffTime;
}