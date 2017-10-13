package com.hybris.easyjet.fixture.hybris.invoke.response.common;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Profile {
    private String code;
    private String type;
    private String title;
    private String firstName;
    private String lastName;
    private Integer age;
    private String phoneNumber;
    private String email;
    private String ejPlusCardNumber;
    private String nifNumber;
    private String flightClubId;
    private String flightClubExpiryDate;
    private List<IdentityDocument> identityDocuments = new ArrayList<>();
    private SpecialRequest savedSSRs;

}