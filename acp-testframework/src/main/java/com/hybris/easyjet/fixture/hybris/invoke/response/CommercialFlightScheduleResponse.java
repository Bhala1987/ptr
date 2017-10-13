package com.hybris.easyjet.fixture.hybris.invoke.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jamie on 13/02/2017.
 */

@Getter
@Setter
public class CommercialFlightScheduleResponse extends Response {
    private List<Schedule> schedules = new ArrayList<>();

    @Getter
    @Setter
    public static class Schedule {
        private String flightNumber;
        private String carrier;
        private String departureTime;
        private String arrivalTime;
        private Sector sector;
        private Terminal departureTerminal;
        private Terminal arrivalTerminal;
    }

    @Getter
    @Setter
    public static class Sector {
        private String code;
        private Airport departureAirport;
        private Airport arrivalAirport;
    }

    @Getter
    @Setter
    public static class Terminal {
        private String code;
        private String name;
    }

    @Getter
    @Setter
    public static class Airport {
        private String airportCode;
        private String airportName;
    }

}