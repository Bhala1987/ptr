package com.hybris.easyjet.fixture.hybris.invoke.response.common;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by g.dimartino on 13/05/17.
 */
@Getter
@Setter
public abstract class AbstractFlights<F extends AbstractFlights.AbstractFlight> {
    private Boolean isDirect;
    private String totalDuration;
    private Integer stops;
    private List<F> flights = new ArrayList<>();

    @Getter
    @Setter
    public abstract static class AbstractFlight {
        private String flightKey;
        private String flightNumber;
        private String carrier;
        private List<String> linkedFlights;
    }

    @Getter
    @Setter
    public abstract static class AbstractFoundFlight<S extends AbstractSector, P extends AbstractPassenger> extends AbstractFlight {
        private String departureDateTime;
        private String arrivalDateTime;
        private S sector;
        private List<P> passengers = new ArrayList<>();
    }

}