package com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.purchasedseats;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by giuseppecioce on 12/04/2017.
 */
@Builder
@Getter
@Setter
public class AddPurchasedSeatsRequestBody implements IRequestBody {
    @JsonProperty("flightKey")
    private String flightKey;
    @JsonProperty("passengerAndSeats")
    private List<PassengerAndSeat> passengerAndSeats;
}
