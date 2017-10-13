package com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PassengerDetails {
    private Name name;
    private String phoneNumber;
    private String email;
    private String passengerType;
    private String nifNumber;
    private String ejPlusCardNumber;
}