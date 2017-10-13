package com.hybris.easyjet.fixture.hybris.invoke.requestbody.customerpreferences;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "type",
        "day",
        "month"
})
@Builder
@Getter
@Setter
public class KeyDate {

    @JsonProperty("type")
    public String type;
    @JsonProperty("day")
    public String day;
    @JsonProperty("month")
    public String month;

}
