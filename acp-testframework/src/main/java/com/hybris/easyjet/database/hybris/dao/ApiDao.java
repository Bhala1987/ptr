package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.jolbox.bonecp.BoneCPDataSource;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by marco on 16/02/17.
 */
@Repository
@ToString
public class ApiDao {

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public ApiDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int countApis() {
        String query =
                "SELECT COUNT(1)\n" +
                        "FROM advancepassengerinfo;";

        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), Integer.class);

    }

    public List<String> getDocumentIdsForCustomer(String customerId) {
        SqlParameterSource params = new MapSqlParameterSource("p_uid", customerId);

        String query =
                "SELECT [p_docid]\n" +
                        "FROM [dbo].[advancepassengerinfo] api \n" +
                        "JOIN [dbo].[users] u on u.pk = api.p_customer \n" +
                        "WHERE u.p_uid = :p_uid";

        return this.namedParameterJdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> rs.getString("p_docid"));
    }
}