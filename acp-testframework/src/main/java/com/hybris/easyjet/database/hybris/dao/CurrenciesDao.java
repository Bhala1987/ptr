package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.TenantBeanFactoryPostProcessor;
import com.hybris.easyjet.database.QueryParser;
import com.hybris.easyjet.database.hybris.models.CurrencyModel;
import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by daniel on 23/11/2016
 * provides readonly access to currency data in hybris
 */
@Repository
public class CurrenciesDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public CurrenciesDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public static CurrenciesDao getCurrenciesDaoFromSpring() {
        return (CurrenciesDao) TenantBeanFactoryPostProcessor.getFactory().getBean("currenciesDao");
    }

    public CurrencyModel getCurrency(String code) {

        SqlParameterSource params = new MapSqlParameterSource("code", code);
        String query =
                "SELECT " +
                        "p_isocode\n" +
                        ",p_digits\n" +
                        ",p_conversion\n" +
                        ",p_symbol\n" +
                        ",p_base\n" +
                        "FROM currencies\n" +
                        "WHERE p_isocode = :code;";

        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, (rs, rowNum) -> CurrencyModel.builder()
                .code(rs.getString("p_isocode"))
                .decimalPlaces(rs.getInt("p_digits"))
                .conversion(rs.getDouble("p_conversion"))
                .isBaseCurrency(rs.getBoolean("p_base"))
                .displaySymbol(rs.getString("p_symbol"))
                .build());
    }

    /**
     * @param active the active status of currencies that you wish to return
     * @return a list of currencies from hybris
     */
    public List<CurrencyModel> getCurrencies(boolean active) {

        SqlParameterSource params = new MapSqlParameterSource("p_active", active);
        String query =
                "SELECT " +
                        "p_isocode\n" +
                        ",p_digits\n" +
                        ",p_conversion\n" +
                        ",p_symbol\n" +
                        ",p_base\n" +
                        "FROM currencies\n" +
                        "WHERE p_active = :p_active;";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> CurrencyModel.builder()
                .code(rs.getString("p_isocode"))
                .decimalPlaces(rs.getInt("p_digits"))
                .conversion(rs.getDouble("p_conversion"))
                .isBaseCurrency(rs.getBoolean("p_base"))
                .displaySymbol(rs.getString("p_symbol"))
                .build());
    }

    /**
     * @param active the active status of currencies that you wish to return
     * @return a list of currencies from hybris
     */
    public List<String> getCurrenciesWithSpecifiedDecimalDigit(boolean active, int decimalDigit) {

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("p_active", active)
                .addValue("p_digits", decimalDigit);

        String currencyQuery =
                "SELECT\n" +
                        "[p_isocode]\n" +
                        "FROM [dbo].[currencies]\n" +
                        "WHERE p_active = :p_active\n" +
                        "AND p_digits = :p_digits;";

        return this.jdbcTemplate.query(QueryParser.parse(currencyQuery), namedParameters, (rs, rowNum) -> rs.getString("p_isocode"));
    }

    public BigDecimal getCurrencyConversionMargin () {

        BigDecimal actualMargin = BigDecimal.ONE;

        try {
            return actualMargin.add(getCurrencyConversionPropertyValue().multiply(new BigDecimal("0.01")));
        } catch (EmptyResultDataAccessException e) {
            return actualMargin;
        }

    }

    public BigDecimal getCurrencyConversionPropertyValue ()  {

        SqlParameterSource namedParameters = new MapSqlParameterSource();

        String currencyQuery =
                "SELECT pvc.p_propertyvalue AS margin\n" +
                        "FROM propvalueconfig AS pvc\n" +
                        "WHERE pvc.p_propertyname = 'flightPriceCurrencyConversionMargin';";

       return new BigDecimal(this.jdbcTemplate.queryForObject(QueryParser.parse(currencyQuery), namedParameters, String.class));
    }

    /**
     * @param creditFileName the credit file name that we are requesting the currency for
     * @return a currency code from hybris
     */
    public String getCurrencyCodeForCreditFile(String creditFileName) {

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("p_creditFileName", creditFileName);

        String query = "SELECT " +
                          "[p_isocode]\n" +
                          "FROM [dbo].[currencies] \n" +
                          "WHERE pk = (SELECT [p_currency] FROM [dbo].[creditfilefund] " +
                                      "WHERE p_code = :p_creditFileName);";

        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), namedParameters, String.class);
    }

    /**
     * getConversionRateByCode, it gets the currency conversion rate by currency
     * @param currency
     * @return
     */
    public Double getConversionRateByCode(String currency) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("p_isoCode", currency);
        String query = "SELECT p_conversion AS conversion FROM currencies WHERE p_isocode =:p_isoCode;";
        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), parameters, (rs, rowNum) -> rs.getDouble("conversion"));
    }
}