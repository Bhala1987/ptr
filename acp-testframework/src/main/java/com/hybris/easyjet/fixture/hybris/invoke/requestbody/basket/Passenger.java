package com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Passenger {
    private String passengerType;
    private Integer quantity;
    private Integer additionalSeats;
    private Boolean infantOnSeat;
}
