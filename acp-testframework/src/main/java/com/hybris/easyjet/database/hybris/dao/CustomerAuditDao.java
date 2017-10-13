package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.jolbox.bonecp.BoneCPDataSource;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
/**
 * Created by marco on 16/02/17.
 */
@Repository
@ToString
public class CustomerAuditDao {

    private NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public CustomerAuditDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public int countRowsByCustomerIdAndType(String customerId, String eventType) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("eventType", eventType)
                .addValue("customerId", customerId);

        String query =
                "SELECT COUNT(1)\n" +
                        "FROM customeraudit AS audit\n" +
                        "JOIN enumerationvalues AS en ON audit.p_eventtype = en.PK\n" +
                        "JOIN users AS u ON audit.p_modifiedbyuser = u.PK\n" +
                        "WHERE en.Code = :eventType\n" +
                        "AND u.p_uid = :customerId\n" +
                        "AND audit.modifiedTS IS NOT NULL;";

        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, Integer.class);
    }

}