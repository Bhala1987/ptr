package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

/**
 * Created by Sudhir Talluri on 31/08/2017.
 * allows read access to airports data in Hybris
 */
@Repository
public class AmendCommitBookingDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public AmendCommitBookingDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * @return list of airport codes with desired status
     */
    public Integer getOrderHistoryCount(String orderRefNo) {
        String query = "SELECT count(*) FROM bookinghistory bh JOIN orders ord ON  bh.p_booking  =  ord.PK WHERE ( ord.p_bookingreference  =:orderRefNo and   ord.p_versionid  IS NULL)";
        SqlParameterSource params = new MapSqlParameterSource("orderRefNo", orderRefNo);
        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, Integer.class);
    }
    public Integer getOrderCount(String orderRefNo) {
        String query = "SELECT count(*) FROM orders ord WHERE (ord.p_bookingreference  =:orderRefNo)";
        SqlParameterSource params = new MapSqlParameterSource("orderRefNo", orderRefNo);
        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, Integer.class);
    }
    public Integer getPassengerHistoryCount(String orderRefNo, String passengerCode) {
        String query = "SELECT count(*) FROM bookinghistory bh JOIN orders ord ON  bh.p_booking  =  ord.PK INNER JOIN enumerationvalues AS ev ON ev.PK = bh.p_eventtype WHERE (ev.code = 'PASSENGER' " +
                "and bh.p_passengercode=:passengerCode and ord.p_bookingreference  =:orderRefNo and ord.p_versionid  IS NULL)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("orderRefNo", orderRefNo);
        params.addValue("passengerCode",passengerCode);

        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, Integer.class);
    }


}
