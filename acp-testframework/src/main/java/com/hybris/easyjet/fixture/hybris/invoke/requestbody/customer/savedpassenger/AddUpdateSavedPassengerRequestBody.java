package com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.savedpassenger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by giuseppecioce on 16/02/2017.
 */
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddUpdateSavedPassengerRequestBody implements IRequestBody {
    private String type;
    private String title;
    private String firstName;
    private String lastName;
    private Integer age;
    private String phoneNumber;
    private String ejPlusCardNumber;
    private String email;
    private String nifNumber;
    private String flightClubId;
    private String flightClubExpiryDate;
    private AddUpdateSSRRequestBody savedSSRs;
}