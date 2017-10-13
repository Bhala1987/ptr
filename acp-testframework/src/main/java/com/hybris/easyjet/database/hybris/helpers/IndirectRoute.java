package com.hybris.easyjet.database.hybris.helpers;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Created by ptr-kvijayapal on 2/9/2017.
 */
@Data
@EqualsAndHashCode
public class IndirectRoute {

    private static ThreadLocal<List<AirportsForIndirectRoutes>> airports = new ThreadLocal<List<AirportsForIndirectRoutes>>();
    private String source;
    private String destination;

    public IndirectRoute(String source, String destination) {

        this.source = getCode(source);
        this.destination = getCode(destination);
    }

    public static void setAirportListAs(List<AirportsForIndirectRoutes> listOfAirports) {

        airports.set( listOfAirports) ;
    }

    public String getSource() {

        return source;
    }

    public String getDestination() {

        return destination;
    }

    private String getCode(String pk) {

        for (AirportsForIndirectRoutes airport : airports.get()) {
            if (pk.equals(airport.getPk())) {
                return airport.getCode();
            }
        }
        return null;
    }

}