package com.hybris.easyjet.database.eres;

import com.jolbox.bonecp.BoneCPDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by daniel on 23/11/2016.
 * This class provides the ability to get available flights from eRes
 */
@Repository
public class EresFlightsDao {
    private static final Logger LOG = LogManager.getLogger(EresFlightsDao.class);

    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource for connecting to the eRes databse
     */
    @Autowired
    public EresFlightsDao(@Qualifier("eresDataSource") BoneCPDataSource dataSource) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * @param numberOfSeatsNeeded the minimum number of available seats needed on the seatmap
     * @param fromDate            the date that the seatmap must depart after
     * @param toDate              the date the the seatmap must depart before
     * @param departureAirport    the departure Airport
     * @param arrivalAirport      the Arrival Airport
     * @return
     */
    public List<String> getAvailableFlights(int numberOfSeatsNeeded, Calendar fromDate, Calendar toDate, String departureAirport, String arrivalAirport, String bundles) {

        String fromDateForQuery = new SimpleDateFormat("yyyy-MM-dd HH:mm:00.000").format(fromDate.getTime());
        String toDateForQuery = new SimpleDateFormat("yyyy-MM-dd HH:mm:00.000").format(toDate.getTime());

        MapSqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("fromDate", fromDateForQuery)
                .addValue("toDate", toDateForQuery)
                .addValue("numberOfSeats", numberOfSeatsNeeded);

        String query = "SELECT DISTINCT TOP(300) seg.[FlightKey] " +
                " FROM [ejFlight].[dbo].[Flight] seg" +
                " INNER JOIN [ejFlight].[dbo].[AirportCode] as Dep" +
                " ON Dep.AirportCodeId = DepAirportCodeID" +
                " INNER JOIN [ejFlight].[dbo].[AirportCode] as Arr" +
                " ON Arr.AirportCodeId = ArrAirportCodeID" +
                " INNER JOIN FlightFare" +
                " ON seg.FlightID = FlightFare.FlightID" +
                " INNER JOIN FareClassCode" +
                " ON FlightFare.FareClassCodeID = FareClassCode.FareClassCodeID" +
                " INNER JOIN FareClassPriceTranslation" +
                " ON FareClassCode.FareClassCodeID = FareClassPriceTranslation.FareClassCodeID" +
                " INNER JOIN dbo.FlightFare ff WITH (NOLOCK) ON ff.FlightID = seg.FlightID" +
                " AND ff.AU >  seg.SeatsSold" +
                " AND ff.AUmin <= seg.SeatsSold + :numberOfSeats" +
                " AND (seg.SeatsSold < seg.Lid" +
                " AND seg.Lid != 0)" +
                " AND ff.AUMin != seg.Lid" +
                " WHERE LocalDepDtTm BETWEEN :fromDate AND :toDate";

        if (departureAirport != null) {
            namedParameters.addValue("depAirport", departureAirport);
            namedParameters.addValue("arrAirport", arrivalAirport);
            query += " AND Dep.AirportCode = :depAirport and Arr.AirportCode = :arrAirport;";
        } else {
            query += ";";
        }

        if (bundles != null) {
            query = query.substring(0, query.length() - 1);
            String[] bundleList = bundles.split(",");
            String list = StringUtils.join(bundleList, "','");
            list = "'" + list + "%'";
            list = parseFareClassCodeDescriptionToBundle(list);
            query += " AND FareClassCode.FareClassCodeDescription LIKE (" + list + ");";
        }

        return this.jdbcTemplate.query(query, namedParameters, (rs, rowNum) -> rs.getString("FlightKey"));
    }

    private String parseFareClassCodeDescriptionToBundle(String aList) {
        return aList
                .replace("Standard", "Regular Fare");
    }

    public void cleanData() {
        LOG.info("================ RELEASING THE FLIGHTS STARTED ================ ");
        this.jdbcTemplate.update("UPDATE Flight SET SeatsSold = 0;", new MapSqlParameterSource());
        LOG.info("================ RELEASING THE FLIGHTS COMPLETED ================ ");
    }
}