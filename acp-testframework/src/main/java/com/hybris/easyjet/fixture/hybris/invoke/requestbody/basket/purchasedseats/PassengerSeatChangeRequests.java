package com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.purchasedseats;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by giuseppecioce on 10/05/2017.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "passengerOnFlightId",
        "seat",
        "additionalSeats"
})
@Getter
@Setter
@Builder
public class PassengerSeatChangeRequests {
    @JsonProperty("passengerOnFlightId")
    private String passengerOnFlightId;
    @JsonProperty("seat")
    private Seat seat;
    @JsonProperty("additionalSeats")
    private List<Seats> additionalSeats;

    @Getter
    @Setter
    @Builder
    public static class Seat {
        @JsonProperty("price")
        private Double price;
        @JsonProperty("seatNumber")
        private String seatNumber;
    }
    @Getter
    @Setter
    @Builder
    public static class Seats {
        private Seat seat;
    }

}
