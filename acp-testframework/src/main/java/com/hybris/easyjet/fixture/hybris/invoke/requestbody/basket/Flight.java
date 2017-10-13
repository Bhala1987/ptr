package com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Flight {
    private String flightKey;
    private Double flightPrice;
    private String sector;
}