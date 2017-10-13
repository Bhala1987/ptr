package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by daniel on 23/11/2016.
 * provides read access to countries held in Hybris
 */
@Repository
public class CountriesDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public CountriesDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * @param active the active status of countries you wish to return
     * @return a list of country iso codes for counntries
     */
    public List<String> getCountries(boolean active) {

        SqlParameterSource params = new MapSqlParameterSource("p_active", active);
        String query =
                "SELECT [p_isocode]\n" +
                        "FROM [dbo].[countries]\n" +
                        "WHERE p_active = :p_active;";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> rs.getString("p_isocode"));
    }

    /**
     * @param active the active status of countries you wish to return
     * @return a map of country iso codes for countries with related diallingCode.
     */
    public Map<String, String> getCountriesAndDiallingCode(boolean active) {

        SqlParameterSource params = new MapSqlParameterSource("p_active", active);
        String query =
                "SELECT [p_isocode]\n" +
                        ",[p_diallingcode]\n" +
                        "FROM [dbo].[countries] " +
                        "WHERE p_active = :p_active;";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, rs -> {

            HashMap<String, String> isDiallingCodePopulatedForCountryCode = new HashMap<>();
            while (rs.next()) {
                isDiallingCodePopulatedForCountryCode.put(rs.getString("p_isocode"), rs.getString("p_diallingcode"));
            }
            return isDiallingCodePopulatedForCountryCode;
        });
    }
}