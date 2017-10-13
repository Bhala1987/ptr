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
 * Created by giuseppecioce on 10/02/2017.
 */
@Repository
public class EnumerationValuesDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public EnumerationValuesDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public String getPCodeForChannel(String channel) {

        SqlParameterSource params = new MapSqlParameterSource("Code", channel);

        String query =
                "SELECT PK\n" +
                        "FROM [dbo].[enumerationvalues]\n" +
                        "WHERE Code = :Code;";
        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, String.class);
    }

}