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
 * Created by giuseppecioce on 31/03/2017.
 */
@Repository
public class DiscountReasonDao {
    private NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public DiscountReasonDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<String> getAllValidDiscount(String currency) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("p_isocode", currency);

        String query =
                "SELECT ejdiscount.p_code\n" +
                        "FROM ejdiscount\n" +
                        "WHERE ejdiscount.PK IN (\n" +
                        "\tSELECT  item_t0.p_discount\n" +
                        "\tFROM ejdiscountrow item_t0 \n" +
                        "\tWHERE (NOW() BETWEEN  item_t0.p_starttime  AND  item_t0.p_endtime  OR  item_t0.p_endtime  IS NULL OR  item_t0.p_starttime  IS NULL AND  item_t0.p_endtime  IS NULL)\n" +
                        "    AND item_t0.p_currency IN (\n" +
                        "\t\tSELECT currencies.PK\n" +
                        "        FROM currencies\n" +
                        "        WHERE currencies.p_isocode = :p_isocode\n" +
                        "    )\n" +
                        ");\n";

        return this.jdbcTemplate.query(QueryParser.parse(query), namedParameters, (rs, rowNum) -> rs.getString("p_code"));
    }

    public List<String> getValidDiscountIgnoreCurrency() {
        String query =
                "SELECT ejdiscount.p_code\n" +
                        "FROM ejdiscount\n" +
                        "WHERE ejdiscount.PK IN (\n" +
                        "\tSELECT  item_t0.p_discount\n" +
                        "\tFROM ejdiscountrow item_t0 \n" +
                        "\tWHERE (NOW() BETWEEN  item_t0.p_starttime  AND  item_t0.p_endtime  OR  item_t0.p_endtime  IS NULL OR  item_t0.p_starttime  IS NULL AND  item_t0.p_endtime  IS NULL)\n" +
                        ");\n";
        return this.jdbcTemplate.query(QueryParser.parse(query), (rs, rowNum) -> rs.getString("p_code"));
    }

    public List<String> getInvalidDiscount(String currency) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("p_isocode", currency);

        String query =
                "SELECT ejdiscount.p_code\n" +
                        "FROM ejdiscount\n" +
                        "WHERE ejdiscount.PK IN (\n" +
                        "\tSELECT  item_t0.p_discount\n" +
                        "\tFROM ejdiscountrow item_t0 \n" +
                        "\tWHERE (NOW() NOT BETWEEN  item_t0.p_starttime  AND  item_t0.p_endtime)\n" +
                        ");\n";

        return this.jdbcTemplate.query(QueryParser.parse(query), namedParameters, (rs, rowNum) -> rs.getString("p_code"));
    }

}
