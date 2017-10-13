package com.hybris.easyjet.fixture.hybris.invoke.requestbody.flightInterest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;

import java.util.List;

/**
 * Created by marco on 10/02/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class FlightInterestRequestBody implements IRequestBody {

    @JsonProperty("flightInterests")
    private List<FlightInterest> flightInterests;

    @JsonProperty("flightInterests")
    public List<FlightInterest> getFlightInterest() {
        return flightInterests;
    }

    @JsonProperty("flightInterests")
    public void setFlightInterests(List<FlightInterest> fareType) {
        this.flightInterests = flightInterests;
    }

}
