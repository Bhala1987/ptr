package com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.customerpreferences;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "fromDate",
        "toDate"
})
@Builder
@Getter
@Setter
public class OptedOutPeriod {

    @JsonProperty("fromDate")
    public String fromDate;
    @JsonProperty("toDate")
    public String toDate;

}
