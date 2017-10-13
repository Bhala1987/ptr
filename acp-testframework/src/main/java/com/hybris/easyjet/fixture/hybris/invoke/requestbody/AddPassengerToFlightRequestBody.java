package com.hybris.easyjet.fixture.hybris.invoke.requestbody;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@lombok.Builder
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddPassengerToFlightRequestBody implements IRequestBody {

    private String passengerType;
    private String overrideLimits;
    private String bundleCode;
    private List<String> flightKeys;
    private String responsibleAdultPassengerCode;
}