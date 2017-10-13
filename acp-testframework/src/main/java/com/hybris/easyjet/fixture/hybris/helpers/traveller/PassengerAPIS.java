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
public class PassengerAPIS extends Response {

    @JsonProperty("name")
    private Name name;
    @JsonProperty("dateOfBirth")
    private String dateOfBirth;
    @JsonProperty("documentExpiryDate")
    private String documentExpiryDate;
    @JsonProperty("documentNumber")
    private String documentNumber;
    @JsonProperty("documentType")
    private String documentType;
    @JsonProperty("gender")
    private String gender;
    @JsonProperty("nationality")
    private String nationality;
    @JsonProperty("countryOfIssue")
    private String countryOfIssue;

}