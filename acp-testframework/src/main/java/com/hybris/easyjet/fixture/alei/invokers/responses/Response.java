package com.hybris.easyjet.fixture.alei.invokers.responses;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hybris.easyjet.fixture.IResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by g.dimartino on 09/02/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Response implements IResponse {

    @JsonIgnore
    public Map<String, Object> additionalProperties = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}