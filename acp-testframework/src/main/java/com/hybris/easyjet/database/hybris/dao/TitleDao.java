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
 * provides readonly access to title reference data in hybris
 */
@Repository
public class TitleDao {

    private NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public TitleDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * @param active the active status desired
     * @return a list of Titles which match the desired active status
     */
    public List<String> findTitlesWhichAreActive(boolean active) {

        SqlParameterSource params = new MapSqlParameterSource("p_active", active);
        String query =
                "SELECT\n" +
                        "p_code as title\n" +
                        "FROM [dbo].[titles]\n" +
                        "WHERE p_active = :p_active;";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> rs.getString("title"));

    }

}
