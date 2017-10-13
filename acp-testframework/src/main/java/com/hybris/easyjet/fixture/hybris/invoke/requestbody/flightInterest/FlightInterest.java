package com.hybris.easyjet.fixture.hybris.invoke.requestbody.flightInterest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class FlightInterest implements IRequestBody {

    @JsonProperty("flightKey")
    private String flightKey;
    @JsonProperty("fareType")
    private String fareType;

    @JsonProperty("flightKey")
    public String getFlightKey() {
        return flightKey;
    }

    @JsonProperty("flightKey")
    public void setFlightKey(String flightKey) {
        this.flightKey = flightKey;
    }

    @JsonProperty("fareType")
    public String getFareType() {
        return fareType;
    }

    @JsonProperty("fareType")
    public void setFareType(String fareType) {
        this.fareType = fareType;
    }

}
