package com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PassengerAPIS {
    private Name name;
    private String dateOfBirth;
    private String documentExpiryDate;
    private String documentNumber;
    private String documentType;
    private String gender;
    private String nationality;
    private String countryOfIssue;
}