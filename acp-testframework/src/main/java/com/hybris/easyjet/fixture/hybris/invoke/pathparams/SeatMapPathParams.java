package com.hybris.easyjet.fixture.hybris.invoke.pathparams;

import com.hybris.easyjet.fixture.IPathParameters;
import lombok.Builder;

import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.SeatMapPathParams.FlightPaths.DEFAULT;


/**
 * Created by giuseppedimartino on 13/02/17.
 */
@Builder
public class SeatMapPathParams extends PathParameters implements IPathParameters {

    private static final String SEAT_MAP_SERVICE_PATH = "/seat-map";

    private String flightId;
    private FlightPaths path;

    @Override
    public String get() {

        if (!isPopulated(flightId)) {
            throw new IllegalArgumentException("You must specify a flightId for this service.");
        }

        if (path == null) {
            path = DEFAULT;
        }

        if (this.path == FlightPaths.GET_SEAT_MAP) {
            return flightId + "/seat-map";
        }

        return flightId;

    }

    public enum FlightPaths {
        DEFAULT, GET_SEAT_MAP
    }

}
