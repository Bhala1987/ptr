package com.hybris.easyjet.fixture.hybris.helpers.traveller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Passenger extends Response {

    @JsonProperty("code")
    private String code;
    @JsonProperty("relatedAdult")
    private String relatedAdult;
    @JsonProperty("passengerDetails")
    private PassengerDetails passengerDetails;
    @JsonProperty("age")
    private Integer age;
    @JsonProperty("isLead")
    private Boolean isLead;
    @JsonProperty("passengerAPIS")
    private PassengerAPIS passengerAPIS;
    @JsonProperty("specialRequests")
    private SpecialRequest specialRequests;
    @JsonProperty("saveToCustomerProfile")
    private Boolean saveToCustomerProfile;
    @JsonProperty("updateSavedPassengerCode")
    private String updateSavedPassengerCode;
    @JsonProperty("savedPassengerCode")
    private String savedPassengerCode;


}