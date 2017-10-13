package com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.registercustomer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class PersonalDetails {

    private String email;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer memberId;
    private String password;
    private Integer age;
    private String title;
    private String firstName;
    private String lastName;
    private String ejPlusCardNumber;
    private String nifNumber;
    private String phoneNumber;
    private String alternativePhoneNumber;
    private String flightClubId;
    private String employeeId;
    private String employeeEmail;
    private String flightClubExpiryDate;
    private List<KeyDate> keyDates;

}
