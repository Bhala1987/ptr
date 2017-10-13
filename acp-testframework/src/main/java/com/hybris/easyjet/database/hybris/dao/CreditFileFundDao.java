package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.hybris.easyjet.database.hybris.models.CreditFileFundModel;
import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

/**
 * CreditFileFundDao class, it gets the relevant information for creditfilefund
 */
@Component
public class CreditFileFundDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public CreditFileFundDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * getCreditFileBalance, it returns start & current balance for the relevant code & date
     * @param currentDate
     * @param creditFileCode
     * @return
     */
    public CreditFileFundModel getCreditFileBalance(String currentDate, String creditFileCode) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("p_startdate", currentDate)
                .addValue("p_expirydate", currentDate)
                .addValue("p_code", creditFileCode);

        String query = "SELECT  p_startbalance AS startBalance, p_currentbalance AS currentBalance FROM creditfilefund WHERE p_startdate <=:p_startdate AND p_expirydate >=:p_expirydate AND p_code=:p_code;";
        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), parameters, (rs, rowNum) -> new CreditFileFundModel(
                rs.getDouble("startBalance"),
                rs.getDouble("currentBalance")
        ));
    }
}
