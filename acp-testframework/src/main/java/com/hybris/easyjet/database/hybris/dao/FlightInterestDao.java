package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.hybris.easyjet.database.hybris.models.FlightInterestModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.jolbox.bonecp.BoneCPDataSource;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;


@Repository
public class FlightInterestDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public FlightInterestDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * @return string of concatenated seatmap interests ids
     */
    private String getFlightInterestsStringForACustomer(String customerId) {
        String GET_FLIGHT_INTEREST_IDS =
                "SELECT\n" +
                        "[us].[p_flightinterests]\n" +
                        "FROM [dbo].[users] as us\n" +
                        "WHERE \n" +
                        "us.p_uid =:customerId";

        SqlParameterSource namedParameters = new MapSqlParameterSource("customerId", customerId);


        List<String> results = this.jdbcTemplate.query(QueryParser.parse(GET_FLIGHT_INTEREST_IDS), namedParameters, (rs, rowNum) -> rs.getString("p_flightinterests"));
        String flightInterestsList = null;
        if (results != null && !results.isEmpty()) {
            flightInterestsList = results.get(0);
        }
        return flightInterestsList;
    }


    /**
     * @return list of aded interests
     */
    public List<FlightInterestModel> getFlightInterestsForACustomer(String customerId) {
        String concatFlightInterestList = getFlightInterestsStringForACustomer(customerId);
        List<String> flightInterests = new ArrayList<String>();
        if (concatFlightInterestList != null) {
            flightInterests = Arrays.asList(concatFlightInterestList.split(","));
            flightInterests = flightInterests.stream().filter(i -> StringUtils.isNumeric(i)).collect(Collectors.toList());
        }

        if (flightInterests != null && !flightInterests.isEmpty()) {
            String GET_FLIGHT_INTEREST_RECORDS =
                    "SELECT [FI].[p_flightkey],\n" +
                            " [FI].[p_faretype]\n" +
                            "FROM [dbo].[flightregisteredinterest] AS FI\n" +
                            "WHERE FI.PK in (:flightInterests)";

            SqlParameterSource namedParameters = new MapSqlParameterSource("flightInterests", flightInterests);

            return this.jdbcTemplate.query(QueryParser.parse(GET_FLIGHT_INTEREST_RECORDS), namedParameters, (rs, rowNum) -> new FlightInterestModel(
                    rs.getString("p_flightkey"),
                    rs.getString("p_faretype")));
        } else {
            return new ArrayList<FlightInterestModel>();
        }

    }

    public void updateFlightDepartureTime(Date aDepartureTime, String aFlightKey) {
        String TIME_UPDATE = "update warehouses set p_departuretime=:aDepartureTime where p_code=:aFlightKey";

        Map<String, Object> params = new HashMap<>();
        params.put("aDepartureTime", aDepartureTime);
        params.put("aFlightKey", aFlightKey);

        this.jdbcTemplate.update(TIME_UPDATE, params);
    }


    public void updateOnlineClosureCheckinTime(String airportCode, String onlineClosureCheckinTime) {
        String APT_UPDATE = "update transportfacility set p_onlinecheckinclosuretime=:onlineClosureCheckinTime where p_code=:airportCode";

        Map<String, String> params = new HashMap<>();
        params.put("onlineClosureCheckinTime", onlineClosureCheckinTime);
        params.put("airportCode", airportCode);

        this.jdbcTemplate.update(APT_UPDATE, params);
    }

    public int getOnlineClosureCheckinTime(String airportCode) {
        String GET_ONLINECHECKINCLOSURETIME = "SELECT p_onlinecheckinclosuretime as hours FROM transportfacility WHERE p_code=:airportCode";

        Map<String, String> params = new HashMap<>();
        params.put("airportCode", airportCode);

        String hours = this.jdbcTemplate.queryForObject(GET_ONLINECHECKINCLOSURETIME, params, String.class);

        if (hours == null) {
            new EasyjetCompromisedException("There is no value for p_onlinecheckinclosuretime in the hybris DB, could be impex file issue");
        }

        return Integer.parseInt(hours);
    }

    public int getAirportGateClosureCheckinTime(String airportCode) {
        String GET_AIRPORTGATECHECKINCLOSURETIME = "SELECT p_gateCheckInClosureTime as hours FROM transportfacility WHERE p_code=:airportCode";

        Map<String, String> params = new HashMap<>();
        params.put("airportCode", airportCode);

        String hours = this.jdbcTemplate.queryForObject(GET_AIRPORTGATECHECKINCLOSURETIME, params, String.class);

        if (hours == null) {
            new EasyjetCompromisedException("There is no value for p_gateCheckInClosureTime in the hybris DB, could be impex file issue");
        }

        return Integer.parseInt(hours);
    }


    public String getAFlightKeyForAnAirport(String airportCode) {
        String GET_FLIGHT_KEY = "select  warehouses.p_code as flightkey " +
                " FROM warehouses " +
                " INNER JOIN travelsector as sector ON p_travelsector = sector.PK " +
                " INNER JOIN transportfacility as origin ON sector.p_origin = origin.PK " +
                " INNER JOIN transportfacility as dest ON sector.p_destination = dest.PK " +
                " INNER JOIN enumerationvalues as enum ON enum.PK = warehouses.p_status " +
                " INNER JOIN pointofservice as pos ON pos.p_transportfacility = origin.pk " +
                " where p_departuretime BETWEEN  DATE_ADD(NOW(), INTERVAL 0 MINUTE) AND DATE_ADD(NOW(), INTERVAL 6 MONTH) " +
                " AND enum.Code != 'CANCELLED' " +
                " AND origin.p_code = :airportCode " +
                " LIMIT 1";

        SqlParameterSource namedParameters = new MapSqlParameterSource("airportCode", airportCode);
        List<String> results = this.jdbcTemplate.query(GET_FLIGHT_KEY, namedParameters, (rs, rowNum) -> rs.getString("flightkey"));
        String flightKey = null;
        if (results != null && !results.isEmpty()) {
            flightKey = results.get(0);
        }
        return flightKey;
    }
}
