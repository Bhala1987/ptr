package com.hybris.easyjet.fixture.hybris.invoke.queryparams.factory;

import com.hybris.easyjet.database.FlightFinder;
import com.hybris.easyjet.database.hybris.models.HybrisFlightDbModel;
import com.hybris.easyjet.fixture.hybris.helpers.FlightPassengers;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.FlightQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.Passenger;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by grizhenkova on 11/24/2016.
 */
@Component
public class FlightQueryParamsFactory {

    private static Logger LOG = LogManager.getLogger(FlightQueryParamsFactory.class);


    public static FlightQueryParams InvalidFlightParams(FlightFinder flightFinder, String params) throws Throwable {
        LocalDate today = LocalDate.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter badFormat = DateTimeFormatter.BASIC_ISO_DATE;
        switch (params) {

            case "outbound date is before today":
                return generateFlightSearchCriteria(flightFinder.findAValidFlight()).outboundDate(today.minusDays(1).format(format)).adult("1").build();
            case "inbound date is before outbound date":
                return generateFlightSearchCriteria(flightFinder.findAValidFlight()).outboundDate(today.plusDays(5).format(format)).inboundDate(today.plusDays(4).format(format)).adult("1").build();
            case "outbound date in a wrong format":
                return generateFlightSearchCriteria(flightFinder.findAValidFlight()).outboundDate(today.format(badFormat)).adult("1").build();
            case "inbound date in a wrong format":
                return generateFlightSearchCriteria(flightFinder.findAValidFlight()).inboundDate(today.format(badFormat)).adult("1").build();
            case "no origin airport":
                return generateFlightSearchCriteria(flightFinder.findAValidFlight()).outboundDate(today.plusDays(5).format(format)).origin("").adult("1").build();
            case "no destination airport":
                return generateFlightSearchCriteria(flightFinder.findAValidFlight()).outboundDate(today.plusDays(5).format(format)).destination("").adult("1").build();
            case "wrong destination airport":
                return generateFlightSearchCriteria(flightFinder.findAValidFlight()).outboundDate(today.plusDays(5).format(format)).destination("LHR").adult("1").build();
            case "wrong origin airport":
                return generateFlightSearchCriteria(flightFinder.findAValidFlight()).outboundDate(today.plusDays(5).format(format)).origin("LHR").adult("1").build();
            case "wrong route":
                return generateFlightSearchCriteria(flightFinder.findAValidFlight()).outboundDate(today.plusDays(5).format(format)).origin("LTN").destination("AHO").adult("1").build();
            case "multiple incorrect search criteria":
                return generateFlightSearchCriteria(flightFinder.findAValidFlight()).outboundDate(today.minusDays(5).format(format)).origin("LTN").destination("FAO").adult("41").infant("3,3").build();
            default:
                throw new IllegalArgumentException("This is not a valid test parameter: " + params);
        }
    }

    public static FlightQueryParams generateFlightSearchCriteria(HybrisFlightDbModel flight, FlightPassengers passengers) {
        FlightQueryParams criteria = generateFlightSearchCriteria(flight).build();
        for (Passenger pax : passengers.getPassengers()) {
            if (pax.getPassengerType().toLowerCase().contains("adult")) {
                if (pax.getAdditionalSeats() != 0) {
                    criteria.setAdult(pax.getQuantity() + "," + pax.getAdditionalSeats());
                } else {
                    criteria.setAdult(Integer.toString(pax.getQuantity()));
                }
            }
            if (pax.getPassengerType().toLowerCase().contains("child")) {
                if (pax.getAdditionalSeats() != 0) {
                    criteria.setChild(pax.getQuantity() + "," + pax.getAdditionalSeats());
                } else {
                    criteria.setChild(Integer.toString(pax.getQuantity()));
                }
            }
            if (pax.getPassengerType().toLowerCase().contains("infant")) {
                if (pax.getInfantOnSeat() != null && pax.getInfantOnSeat()) {
                    criteria.setInfant(pax.getQuantity() + "," + pax.getQuantity());
                } else {
                    criteria.setInfant(Integer.toString(pax.getQuantity()));
                }
            }
        }
        return criteria;
    }

    public static FlightQueryParams generateFlightSearchCriteria(HybrisFlightDbModel flight, FlightPassengers passengers, String fareType) {
        FlightQueryParams criteria = generateFlightSearchCriteria(flight, fareType).build();
        for (Passenger pax : passengers.getPassengers()) {
            if (pax.getPassengerType().toLowerCase().contains("adult")) {
                if (pax.getAdditionalSeats() != 0) {
                    criteria.setAdult(pax.getQuantity() + "," + pax.getAdditionalSeats());
                } else {
                    criteria.setAdult(Integer.toString(pax.getQuantity()));
                }
            }
            if (pax.getPassengerType().toLowerCase().contains("child")) {
                if (pax.getAdditionalSeats() != 0) {
                    criteria.setChild(pax.getQuantity() + "," + pax.getAdditionalSeats());
                } else {
                    criteria.setChild(Integer.toString(pax.getQuantity()));
                }
            }
            if (pax.getPassengerType().toLowerCase().contains("infant")) {
                if (pax.getInfantOnSeat() != null && pax.getInfantOnSeat()) {
                    criteria.setInfant(pax.getQuantity() + "," + pax.getQuantity());
                } else {
                    criteria.setInfant(Integer.toString(pax.getQuantity()));
                }
            }
        }
        return criteria;
    }

    public static FlightQueryParams.FlightQueryParamsBuilder generateFlightSearchCriteria(HybrisFlightDbModel flight) {
        return FlightQueryParams.builder()
                .origin(flight.getDeparts())
                .destination(flight.getArrives())
                .fareTypes("Standard,Flexi,Inclusive")
                .outboundDate(flight.getLocalDepartureDate())
                .inboundDate(flight.getLocalDepartureDate());
    }

    public static FlightQueryParams.FlightQueryParamsBuilder generateFlightSearchCriteria(HybrisFlightDbModel flight, String fareType) {
        return FlightQueryParams.builder()
                .origin(flight.getDeparts())
                .destination(flight.getArrives())
                .fareTypes(fareType)
                .outboundDate(flight.getLocalDepartureDate());
    }

    public static FlightQueryParams.FlightQueryParamsBuilder generateReturnFlightSearchCriteria(HashMap<String, HybrisFlightDbModel> flight) {

        return FlightQueryParams.builder()
                .origin(flight.get("outbound").getDeparts())
                .destination(flight.get("outbound").getArrives())
                .outboundDate(flight.get("outbound").getLocalDepartureDate())
                .inboundDate(flight.get("inbound").getLocalDepartureDate());
    }

    public static FlightQueryParams generateReturnFlightSearchCriteria(HashMap<String, HybrisFlightDbModel> flights, FlightPassengers passengers) throws ParseException {
        FlightQueryParams criteria = generateReturnFlightSearchCriteria(flights).build();

        for (Passenger pax : passengers.getPassengers()) {
            if (pax.getPassengerType().toLowerCase().contains("adult")) {
                if (pax.getAdditionalSeats() != 0) {
                    criteria.setAdult(pax.getQuantity() + "," + pax.getAdditionalSeats());
                } else {
                    criteria.setAdult(Integer.toString(pax.getQuantity()));
                }
            }
            if (pax.getPassengerType().toLowerCase().contains("child")) {
                if (pax.getAdditionalSeats() != 0) {
                    criteria.setChild(pax.getQuantity() + "," + pax.getAdditionalSeats());
                } else {
                    criteria.setChild(Integer.toString(pax.getQuantity()));
                }
            }
            if (pax.getPassengerType().toLowerCase().contains("infant")) {
                if (Objects.nonNull(pax.getInfantOnSeat())) {
                    criteria.setInfant(pax.getQuantity() + "," + pax.getQuantity());
                } else {
                    criteria.setInfant(Integer.toString(pax.getQuantity()));
                }
            }
        }

        return criteria;
    }

    public static FlightQueryParams generateFlightSearchCriteria(FlightPassengers passengers, String origin, String destination, String outboundDate, String inboundDate, String flexiDays) {
        FlightQueryParams criteria = FlightQueryParams.builder().build();
        criteria.setOutboundDate(outboundDate);
        criteria.setInboundDate(inboundDate);
        criteria.setOrigin(origin);
        criteria.setDestination(destination);
        criteria.setFlexibleDays(flexiDays);
        for (Passenger pax : passengers.getPassengers()) {
            if (pax.getPassengerType().toLowerCase().contains("adult")) {
                if (pax.getAdditionalSeats() != 0) {
                    criteria.setAdult(pax.getQuantity() + "," + pax.getAdditionalSeats());
                } else {
                    criteria.setAdult(Integer.toString(pax.getQuantity()));
                }
            }
            if (pax.getPassengerType().toLowerCase().contains("child")) {
                if (pax.getAdditionalSeats() != 0) {
                    criteria.setChild(pax.getQuantity() + "," + pax.getAdditionalSeats());
                } else {
                    criteria.setChild(Integer.toString(pax.getQuantity()));
                }
            }
            if (pax.getPassengerType().toLowerCase().contains("infant")) {
                if (pax.getInfantOnSeat() != null && pax.getInfantOnSeat()) {
                    criteria.setInfant(pax.getQuantity() + "," + pax.getQuantity());
                } else {
                    criteria.setInfant(Integer.toString(pax.getQuantity()));
                }
            }
        }
        return criteria;
    }

}
