package com.hybris.easyjet.database.hybris.models;

import lombok.Data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by daniel on 23/11/2016.
 */
@Data
public class HybrisFlightDbModel implements Comparable {

    private String flightKey;
    private String localDepartureDate;
    private String arrivalTime;
    private String originTerminal;
    private String destinationTerminal;
    private String status;
    private String updatedDepartureTime;
    private String active;
    private String p_scheduleddeptime;
    private String scheduledArrivalTime;
    private String plannedDepartureTime;
    private String plannedArrivalTime;
    private String route;
    private String departs;
    private String arrives;
    private String currency;
    private int infantsOnSeatLimit;
    private int infantsOnSeatConsumed;
    private int infantsLimit;
    private int infantsConsumed;

    public HybrisFlightDbModel(String flightKey, String localDepartureDate, String arrivalTime, String originTerminal, String destinationTerminal,
                               String status, String updatedDepartureTime, String active, String p_scheduleddeptime, String scheduledArrivalTime, String plannedDepartureTime,
                               String plannedArrivalTime, String route, String departs, String arrives, String currency, int infantsOnSeatLimit, int infantsOnSeatConsumed,
                               int infantsLimit, int infantsConsumed) {

        this.flightKey = flightKey;
        this.localDepartureDate = localDepartureDate;
        this.arrivalTime = arrivalTime;
        this.originTerminal = originTerminal;
        this.destinationTerminal = destinationTerminal;
        this.status = status;
        this.updatedDepartureTime = updatedDepartureTime;
        this.active = active;
        this.p_scheduleddeptime = p_scheduleddeptime;
        this.scheduledArrivalTime = scheduledArrivalTime;
        this.plannedDepartureTime = plannedDepartureTime;
        this.plannedArrivalTime = plannedArrivalTime;
        this.route = route;
        this.departs = departs;
        this.arrives = arrives;
        this.currency = currency;
        this.infantsOnSeatLimit = infantsOnSeatLimit;
        this.infantsOnSeatConsumed = infantsOnSeatConsumed;
        this.infantsLimit = infantsLimit;
        this.infantsConsumed = infantsConsumed;
    }

    @Override
    public int compareTo(Object o) {
        HybrisFlightDbModel hfm1 = (HybrisFlightDbModel) o;
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date date1 = null, date2 = null;

        try {
            date1 = df.parse(this.getLocalDepartureDate());
            date2 = df.parse(hfm1.getLocalDepartureDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(date1.before(date2)){
            return -1;
        } else if(date1.after(date2)){
            return 1;
        } else {
            return 0;
        }
    }
}
