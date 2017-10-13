package com.hybris.easyjet.fixture.hybris.invoke.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by robertadigiorgio on 01/08/2017.
 */

@Getter
@Setter
public class GetFlightInterestResponse extends Response {
    private List<FlightInterests> flightInterests = new ArrayList<>();

    @Getter
    @Setter
    public static class FlightInterests {

        private String flightKey;
        private String flightNumber;
        private String carrier;
        private String departureDateTime;
        private String arrivalDateTime;
        private SectorInterestFlight sector;
        private List<FareTypeInterestFlight> fareTypes;
    }

    @Getter
    @Setter
    public static class SectorInterestFlight {
        private String code;
        private Departure departure;
        private Arrival arrival;

    }
    @Getter
    @Setter
    public static class Departure {
        private String code;
        private String name;
        private String marketGroup;
        private String terminal;
    }

    @Getter
    @Setter
    public static class Arrival {
        private String code;
        private String name;
        private String marketGroup;
        private String terminal;
    }

   @Getter
    @Setter
    public static class FareTypeInterestFlight {
        private String code;
        private String numberAvailable;
    }
}
