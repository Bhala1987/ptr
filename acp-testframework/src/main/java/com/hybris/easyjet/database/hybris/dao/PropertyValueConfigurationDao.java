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
 * Created by tejaldudhale on 26/06/2017.
 */
@Repository
public class PropertyValueConfigurationDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public PropertyValueConfigurationDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public String getPropertyValueBasedOnName(String channel, String propName) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("channel", channel)
                .addValue("propName", propName);

        String query =
                "SELECT p_propertyvalue\n" +
                        "FROM propvalueconfig AS pvc\n" +
                        "   INNER JOIN enumerationvalues AS ev ON ev.PK = pvc.p_channel\n" +
                        "WHERE p_propertyname = :propName\n" +
                        "   AND Code = :channel;";

        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, String.class);
    }

    public List<String> getPropertyValuesBasedOnName(String channel, String propName) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("channel", channel)
                .addValue("propName", propName);

        String query =
                "SELECT p_propertyvalue\n" +
                        "FROM propvalueconfig AS pvc\n" +
                        "   INNER JOIN enumerationvalues AS ev ON ev.PK = pvc.p_channel\n" +
                        "WHERE p_propertyname = :propName\n" +
                        "   AND Code = :channel;";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> rs.getString("p_propertyvalue"));
    }

    public String getPropertyValueBasedOnName(String propName) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("propName", propName);

        String query =
                "SELECT p_propertyvalue\n" +
                        "FROM propvalueconfig\n" +
                        "WHERE p_propertyname = :propName;";

        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, String.class);
    }

}