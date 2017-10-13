package com.hybris.easyjet.database;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.eres.EresFlightsDao;
import com.hybris.easyjet.database.hybris.dao.FlightsDao;
import com.hybris.easyjet.database.hybris.models.HybrisFlightDbModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.hybris.easyjet.exceptions.EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.*;

/**
 * Created by daniel on 23/11/2016.
 * find
 */
@Component
public class FlightFinder {

    static Random rand = new Random();
    private EresFlightsDao eresFlightsDao;
    private FlightsDao hybrisFlightsDao;
    private SerenityFacade testData;


    /**
     * @param eresFlightsDao   autowired datasource which allows connectivity to the eRes database
     * @param hybrisFlightsDao autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public FlightFinder(EresFlightsDao eresFlightsDao, FlightsDao hybrisFlightsDao) {
        this.eresFlightsDao = eresFlightsDao;
        this.hybrisFlightsDao = hybrisFlightsDao;
    }

    /**
     * @return a seatmap with at least one available seat
     * @throws Throwable
     */
    public HybrisFlightDbModel findAValidFlight() throws Throwable {
        List<HybrisFlightDbModel> validFlights = findValidFlights(getFromDate(), getToDate(), 1, null, null, null, false);
        return validFlights.get(rand.nextInt(validFlights.size()));
    }

    /**
     * @param totalPassengers desired number of available seats on the seatmap
     * @return a seatmap with the desired number of available seats
     * @throws Throwable
     */
    public HybrisFlightDbModel findAValidFlight(int totalPassengers) throws Throwable {
        List<HybrisFlightDbModel> validFlights = findValidFlights(getFromDate(), getToDate(), totalPassengers, null, null, null, false);
        return validFlights.get(rand.nextInt(validFlights.size()));
    }


    /**
     * @return a list of flights with at least one available seat
     * @throws Throwable
     */
    public List<HybrisFlightDbModel> findValidFlights(int numberOfPassengers, String bundles, boolean withFlightTax) throws Throwable {
        return findValidFlights(getFromDate(), getToDate(), numberOfPassengers, null, null, bundles, withFlightTax);
    }

    public HashMap<String, HybrisFlightDbModel> findAValidFlightWithReturn(int numberOfPassengers, boolean withFlightTax, String bundles) throws Throwable {
        HashMap<String, List<HybrisFlightDbModel>> flightWithReturn = findValidFlightsWithReturnFlights(getFromDate(), getToDate(), numberOfPassengers, null, null, bundles, withFlightTax);
        return filterFlightsWithReturn(flightWithReturn);
    }

    private HashMap<String, HybrisFlightDbModel> filterFlightsWithReturn(HashMap<String, List<HybrisFlightDbModel>> flightWithReturn) {
        List<HybrisFlightDbModel> outbound = flightWithReturn.get("outbound");
        List<HybrisFlightDbModel> inbound = flightWithReturn.get("inbound");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List<HybrisFlightDbModel> usableOutbound = outbound.stream()
                .filter(fl -> inbound.stream()
                        .anyMatch(fl3 -> {
                                    Date d1 = null;
                                    Date d2 = null;
                                    try {
                                        d1 = formatter.parse(fl3.getPlannedDepartureTime());
                                        d2 = formatter.parse(fl.getPlannedArrivalTime());
                                    } catch (Exception e) {
                                        return false;
                                    }
                                    return fl3.getArrives().equals(fl.getDeparts()) && fl3.getDeparts().equals(fl.getArrives()) && d1.after(d2);
                                }
                        )
                ).collect(Collectors.toList());
        HybrisFlightDbModel outboundFlight = usableOutbound.get(rand.nextInt(usableOutbound.size()));

        List<HybrisFlightDbModel> usableInbound = inbound.stream()
                .filter(fl -> {
                            Date d1 = null;
                            Date d2 = null;
                            try {
                                d1 = formatter.parse(fl.getPlannedDepartureTime());
                                d2 = formatter.parse(outboundFlight.getPlannedArrivalTime());
                            } catch (Exception e) {
                                return false;
                            }
                            return fl.getArrives().equals(outboundFlight.getDeparts()) && fl.getDeparts().equals(outboundFlight.getArrives()) && d1.after(d2);
                        }
                ).collect(Collectors.toList());
        HybrisFlightDbModel inboundFlight = usableInbound.get(rand.nextInt(usableInbound.size()));
        return new HashMap<String, HybrisFlightDbModel>() {{
            put("outbound", outboundFlight);
            put("inbound", inboundFlight);
        }};
    }

    public List<HybrisFlightDbModel> findUniqueValidFlights() throws Throwable {
        return createUniqueFlightList(findValidFlights(getFromDate(), getToDate(), 1, null, null, null, false));
    }

    private Calendar getFromDate() {
        Calendar fromDate = Calendar.getInstance(); // creates calendar
        fromDate.setTime(new Date()); // sets calendar time/date
        fromDate.add(Calendar.HOUR_OF_DAY, 2); // adds two hours
        return fromDate;
    }

    private Calendar getToDate() {
        Calendar toDate = Calendar.getInstance();
        toDate.setTime(new Date()); // Now use today date.
        //updated as per latest data as we have flights only for next 20 days
        toDate.add(Calendar.DATE, 15);

        return toDate;
    }

    public HybrisFlightDbModel findSoldOutFlight() throws Throwable {
        List<HybrisFlightDbModel> validFlights = findSoldOutFlights(getFromDate(), getToDate(), 1, null, null, null, false);
        return validFlights.get(rand.nextInt(validFlights.size()));
    }

    private List<HybrisFlightDbModel> findSoldOutFlights(Calendar fromDate, Calendar toDate, int numberOfPassengers, String depAirport, String arrAirport, String bundles, boolean withFlightTax) throws EasyjetCompromisedException {
        List<String> flightsAvailableInEres = getFlightKeysFromEResDB(fromDate, toDate, numberOfPassengers, depAirport, arrAirport, bundles);
        List<HybrisFlightDbModel> flightsAvailableInHybrisAndEres = hybrisFlightsDao.returnFlightsThatExistFromList(flightsAvailableInEres, withFlightTax);

        List<HybrisFlightDbModel> soldOutFlights=new ArrayList<>();

        if (flightsAvailableInHybrisAndEres.size() < 1) {
            throw new EasyjetCompromisedException(NO_FLIGHTS_IN_ERES_AND_HYBRIS);
        }
        //temp type to avoid seatmap with inventory 0 from mocks
        int index = 0;
        for (index = flightsAvailableInHybrisAndEres.size() - 1; index > 0; index--) {
            if (flightsAvailableInHybrisAndEres.get(index).getFlightKey().endsWith("6")) {
                soldOutFlights.add(flightsAvailableInHybrisAndEres.get(index));

            }
        }
        return soldOutFlights;
    }

    /**
     * @param fromDate           the date that the seatmap should depart after
     * @param toDate             the date that the seatmap should depart before
     * @param numberOfPassengers the desired number of available seats on the seatmap
     * @param bundles            nullable string of the bundles to return from eRes, eg "Flexible Fares, Regular Fare"
     * @param depAirport         nullable string
     * @param arrAirport         nullable string
     * @return a seatmap that departs between the dates with the desired number of available seats
     * @throws EasyjetCompromisedException an exception which provides the ability to filter the serenity report
     */
    private List<HybrisFlightDbModel> findValidFlights(Calendar fromDate, Calendar toDate, int numberOfPassengers, String depAirport, String arrAirport, String bundles, boolean withFlightTax) throws EasyjetCompromisedException {
        List<String> flightsAvailableInEres = getFlightKeysFromEResDB(fromDate, toDate, numberOfPassengers, depAirport, arrAirport, bundles);
        List<HybrisFlightDbModel> flightsAvailableInHybrisAndEres = hybrisFlightsDao.returnFlightsThatExistFromList(flightsAvailableInEres, withFlightTax);

        if (flightsAvailableInHybrisAndEres.size() < 1) {
            throw new EasyjetCompromisedException(NO_FLIGHTS_IN_ERES_AND_HYBRIS);
        }
        //temp type to avoid seatmap with inventory 0 from mocks
        int index = 0;
        for (index = flightsAvailableInHybrisAndEres.size() - 1; index > 0; index--) {
            if (flightsAvailableInHybrisAndEres.get(index).getFlightKey().endsWith("6")) {
                flightsAvailableInHybrisAndEres.remove(flightsAvailableInHybrisAndEres.get(index));

            }
        }
        return flightsAvailableInHybrisAndEres;
    }

    private HashMap<String, List<HybrisFlightDbModel>> findValidFlightsWithReturnFlights(Calendar fromDate, Calendar toDate, int numberOfPassengers, String depAirport, String arrAirport, String bundles, boolean withFlightTax) throws EasyjetCompromisedException {
        List<String> flightsAvailableInEres = getFlightKeysFromEResDB(fromDate, toDate, numberOfPassengers, depAirport, arrAirport, bundles);

        List<HybrisFlightDbModel> flightsAvailableInHybrisAndEres = hybrisFlightsDao.returnFlightsThatExistFromList(flightsAvailableInEres, withFlightTax);

        List<HybrisFlightDbModel> inbound = flightsAvailableInHybrisAndEres.stream()
                .filter(fl -> flightsAvailableInHybrisAndEres.stream()
                        .anyMatch(fl3 -> fl3.getArrives().equals(fl.getDeparts()) && fl3.getDeparts().equals(fl.getArrives()))
                ).collect(Collectors.toList());

        List<HybrisFlightDbModel> outbound = new ArrayList<>();
        inbound.stream().forEach(fl -> {
            if (outbound.stream().noneMatch(fl3 -> fl3.getArrives().equals(fl.getDeparts()) && fl3.getDeparts().equals(fl.getArrives()))) {
                outbound.add(fl);
            }
        });

        inbound.removeAll(outbound);

        if (outbound.size() == 0 || inbound.size() == 0) {
            if (flightsAvailableInEres.size() == 0)
                throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
            else
                throw new EasyjetCompromisedException(NO_FLIGHTS_IN_ERES_AND_HYBRIS);
        }

        HashMap<String, List<HybrisFlightDbModel>> flightWithReturn = new HashMap<String, List<HybrisFlightDbModel>>();
        flightWithReturn.put("outbound", outbound);
        flightWithReturn.put("inbound", inbound);
        return flightWithReturn;
    }

    private List<String> getFlightKeysFromEResDB(Calendar fromDate, Calendar toDate, int numberOfPassengers, String depAirport, String arrAirport, String bundles) throws EasyjetCompromisedException {
        List<String> flightsAvailableInEres = new ArrayList<>();

        if ("true".equals(System.getProperty("eres"))) {
            flightsAvailableInEres = eresFlightsDao.getAvailableFlights(numberOfPassengers, fromDate, toDate, depAirport, arrAirport, bundles);

            if (flightsAvailableInEres.size() < 1) {
                throw new EasyjetCompromisedException(NO_FLIGHTS_IN_ERES);
            }
        }

        return flightsAvailableInEres;
    }

    private List<HybrisFlightDbModel> createUniqueFlightList(List<HybrisFlightDbModel> flights) {
        Set<String> uniqueFlights = new HashSet<>();
        return flights.stream().filter(flight -> uniqueFlights.add(flight.getRoute())).collect(Collectors.toList());
    }

    /**
     * @return a seatmap with at least one available seat
     * @throws EasyjetCompromisedException
     */
    public List<HybrisFlightDbModel> findNValidFlights() throws EasyjetCompromisedException {
        return findValidFlights(getFromDate(), getToDate(), 1, null, null, null, false);
    }

}