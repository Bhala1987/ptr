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
public class Name extends Response {

    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("lastName")
    private String lastName;
    @JsonProperty("fullName")
    private String fullName;
    @JsonProperty("middleName")
    private String middleName;
    @JsonProperty("title")
    private String title;

}