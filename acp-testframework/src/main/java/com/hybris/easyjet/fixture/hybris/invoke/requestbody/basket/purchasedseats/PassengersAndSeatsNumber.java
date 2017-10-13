package com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.purchasedseats;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class PassengersAndSeatsNumber {
    @JsonProperty("passengerId")
    private String passengerId;
    @JsonProperty("seats")
    private List<String> seats;
}
