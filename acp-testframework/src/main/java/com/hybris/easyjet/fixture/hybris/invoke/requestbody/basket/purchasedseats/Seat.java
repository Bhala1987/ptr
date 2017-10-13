package com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.purchasedseats;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by giuseppecioce on 12/04/2017.
 */
@Builder
@Getter
@Setter
public class Seat {
    @JsonProperty("type")
    private String type;
    @JsonProperty("code")
    private String code;
    @JsonProperty("price")
    private String price;
    @JsonProperty("seatNumber")
    private String seatNumber;
}
