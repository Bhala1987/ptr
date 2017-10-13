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
public class CommunicationPreferences {
    public List<String> optedOutMarketing;
    public CustomerProfileResponse.Period optedOutPeriod;
    public List<String> contactMethods;
    public List<String> contactTypes;
    public String frequency;
    public List<KeyDate> keyDates;
}
