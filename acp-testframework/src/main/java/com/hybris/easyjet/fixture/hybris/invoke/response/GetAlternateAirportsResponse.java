package com.hybris.easyjet.fixture.hybris.invoke.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by robertadigiorgio on 10/03/2017.
 */
@Getter
@Setter
public class GetAlternateAirportsResponse extends Response {
    public AlternateAirports alternateAirports;

    @Getter
    @Setter
    public static class AlternateAirports {
        private String departureCode;
        private String destinationCode;
        private Integer maxDistance;
        private List<Airport> airports;
    }

    @Getter
    @Setter
    public static class Airport {
        private String airportCode;
        private Integer distance;
    }

}