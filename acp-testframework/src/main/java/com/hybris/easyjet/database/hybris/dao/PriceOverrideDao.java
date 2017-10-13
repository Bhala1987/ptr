package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.jolbox.bonecp.BoneCPDataSource;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by giuseppecioce on 04/04/2017.
 */
@Repository
@ToString
public class PriceOverrideDao {
    private NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public PriceOverrideDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<String> getValidDiscountReason() {

        String query =
                "SELECT p_code \n" +
                        "FROM ejdiscount;";

        return this.jdbcTemplate.query(QueryParser.parse(query), (rs, rowNum) -> rs.getString("p_code"));
    }
}
