package com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.purchasedseats;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by giuseppecioce on 10/05/2017.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "passengerSeatChangeRequests"
})
@Getter @Setter @Builder
public class PassengerWithAdditionalSeatChangeRequestBody implements IRequestBody {
    @JsonProperty("passengerSeatChangeRequests")
    private List<PassengerSeatChangeRequests> passengerSeatChangeRequests;
    @JsonProperty("seats")
    private List<String> seats;
    @JsonProperty("additionalSeats")
    private List<String> additionalSeats;

}
