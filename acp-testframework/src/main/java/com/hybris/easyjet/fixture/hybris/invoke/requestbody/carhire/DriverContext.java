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
public class DriverContext implements IRequestBody {
    private String passengerCode;
    private String fullName;
    private String passengerType;
    private String countryResidence;
    private int age;
}