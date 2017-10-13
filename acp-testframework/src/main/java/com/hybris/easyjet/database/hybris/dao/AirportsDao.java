package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by daniel on 23/11/2016.
 * allows read access to airports data in Hybris
 */
@Repository
public class AirportsDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public AirportsDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * @param active active status of airports desired
     * @return list of airport codes with desired status
     */
    public List<String> getAirportsThatAreActive(boolean active) {

        SqlParameterSource params = new MapSqlParameterSource("active", active);

        String query =
                "SELECT tf.p_code AS code\n" +
                        "FROM transportfacility AS tf\n" +
                        "WHERE p_active = :active;";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> rs.getString("code"));
    }

    public List<String> getDCSAirports() {

        SqlParameterSource params = new MapSqlParameterSource();

        String query =
                "SELECT tf.p_code AS code\n" +
                        "FROM transportfacility AS tf\n" +
                        "WHERE p_active = 1\n" +
                        "   AND p_eresavailable = 1";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> rs.getString("code"));
    }

}
