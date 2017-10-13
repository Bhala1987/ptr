package com.hybris.easyjet.fixture.hybris.helpers.traveller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import groovy.transform.builder.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Remark extends Response {

    @JsonProperty("code")
    private String code;
    @JsonProperty("name")
    private String name;

}