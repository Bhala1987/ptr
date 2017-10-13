package com.hybris.easyjet.fixture.hybris.invoke.requestbody.flightInterest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 8/9/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder

public class RemoveFlightInterestRequestBody implements IRequestBody  {

    @JsonProperty("flightInterestsToRemove")
    @Getter
    @Setter
    private List<FlightInterest> flightInterestsToRemove;

}

