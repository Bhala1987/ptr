package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.hybris.easyjet.database.hybris.models.DealModel;
import com.hybris.easyjet.database.hybris.models.ItemModel;
import com.jolbox.bonecp.BoneCPDataSource;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class DealDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public DealDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public HashMap<String, ItemModel> getDeal(String applicationId, String officeId, String corporateId, String currency) {

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("applicationId", applicationId)
                .addValue("officeId", officeId)
                .addValue("corporateId", corporateId);

        String query =
                "SELECT\n" +
                        "ts.p_code AS SystemName\n" +
                        ", d.p_officeid AS OfficeId\n" +
                        ", d.p_corporateid AS CorporateId\n" +
                        ", pf.p_values AS POSFee\n" +
                        ", dt.p_discounts AS Discount\n" +
                        ", ejbt.p_code AS BookingId\n" +
                        "FROM deal AS d\n" +
                        "   LEFT JOIN booktype2deal AS b2d ON b2d.TargetPK = d.pk\n" +
                        "   LEFT JOIN ejbookingtype AS ejbt ON ejbt.pk = b2d.SourcePK\n" +
                        "   JOIN transactionalsystem AS ts ON d.p_transactionalsystem = ts.pk\n" +
                        "   JOIN bundledealmapping AS bm ON d.pk = bm.p_deal\n" +
                        "   JOIN posfeetier AS pf ON d.p_posfeetier = pf.pk\n" +
                        "   JOIN discounttier AS dt ON bm.p_discounttier = dt.pk\n" +
                        "WHERE d.p_active = 1\n" +
                        "   AND d.p_catalogversion IN (\n" +
                        "       SELECT pk\n" +
                        "       FROM catalogversions\n" +
                        "       WHERE p_version = 'Online'\n" +
                        "   )\n" +
                        "   AND ts.p_code = :applicationId\n" +
                        "   AND d.p_officeid = :officeId\n";

        query += StringUtils.isNotBlank(corporateId) ? "AND d.p_corporateid = :corporateId;" : "AND d.p_corporateid IS NULL;";

        DealModel deal = this.jdbcTemplate.queryForObject(QueryParser.parse(query), parameters, (rs, rowNum) -> new DealModel(
                rs.getString("SystemName"),
                rs.getString("OfficeId"),
                rs.getString("CorporateId"),
                rs.getString("POSFee"),
                rs.getString("Discount"),
                rs.getString("BookingId")
        ));

        parameters = new MapSqlParameterSource()
                .addValue("discounts", deal.getDiscounts())
                .addValue("currency", currency);

        query =
                "SELECT\n" +
                        "d.p_code AS Code\n" +
                        ",dr.p_value AS Value\n" +
                        "FROM dealdiscountrow AS dr\n" +
                        "   JOIN dealdiscount AS d ON dr.p_discount = d.pk\n" +
                        "   JOIN currencies AS c ON dr.p_currency = c.pk\n" +
                        "WHERE dr.pk IN (:discounts)\n" +
                        "   AND c.p_isocode = :currency;";

        ItemModel discount;
        try {
            discount = this.jdbcTemplate.queryForObject(QueryParser.parse(query), parameters, (rs, rowNum) -> new ItemModel(
                    rs.getString("Code"),
                    rs.getString("Value")
            ));
        } catch (EmptyResultDataAccessException e) {
            discount = null;
        }

        parameters = new MapSqlParameterSource()
                .addValue("fees", deal.getPosFees())
                .addValue("currency", currency);

        query =
                "SELECT\n" +
                        "t.p_code AS Code\n" +
                        ",tr.p_value AS Value\n" +
                        "FROM taxrows AS tr\n" +
                        "JOIN taxes AS t ON tr.p_tax = t.pk\n" +
                        "JOIN currencies AS c ON tr.p_currency = c.pk\n" +
                        "WHERE tr.pk IN (:fees)\n" +
                        "AND c.p_isocode = :currency;";

        ItemModel fee;
        try {
            fee = this.jdbcTemplate.queryForObject(QueryParser.parse(query), parameters, (rs, rowNum) -> new ItemModel(
                    rs.getString("Code"),
                    rs.getString("Value")
            ));
        } catch (EmptyResultDataAccessException e) {
            fee = null;
        }

        HashMap<String, ItemModel> dealValues = new HashMap<>();
        dealValues.put("discount", discount);
        dealValues.put("fee", fee);

        return dealValues;
    }

    public List<DealModel> getDeals(Boolean withCorp, Boolean withPOS, Boolean withDiscount) {

        String query =
                "SELECT\n" +
                        "ts.p_code as SystemName\n" +
                        ", d.p_officeid as OfficeId\n" +
                        ", d.p_corporateid as CorporateId\n" +
                        ", pf.p_values as POSFee\n" +
                        ", dt.p_discounts as Discount\n" +
                        ", ejbt.p_code AS BookingId\n" +
                        "FROM deal as d\n" +
                        "   LEFT JOIN booktype2deal AS b2d ON b2d.TargetPK = d.pk\n" +
                        "   LEFT JOIN ejbookingtype AS ejbt ON ejbt.pk = b2d.SourcePK\n" +
                        "   JOIN transactionalsystem as ts ON d.p_transactionalsystem = ts.pk\n" +
                        "   JOIN bundledealmapping as bm ON d.pk = bm.p_deal\n" +
                        "   JOIN posfeetier as pf ON d.p_posfeetier = pf.pk\n" +
                        "   JOIN discounttier as dt ON bm.p_discounttier = dt.pk\n" +
                        "WHERE d.p_active = 1\n" +
                        "   AND d.p_catalogversion IN (\n" +
                        "       SELECT pk\n" +
                        "       FROM catalogversions\n" +
                        "       WHERE p_version = 'Online'\n" +
                        "   )\n" +
                        "   AND d.p_officeid IS NOT NULL";
        if (withCorp != null) {
            if (withCorp) {
                query += "\nAND d.p_corporateid IS NOT NULL";
            } else {
                query += "\nAND d.p_corporateid IS NULL";
            }
        }
        if (withDiscount != null) {
            if (withDiscount) {
                query += "\nAND bm.p_discounttier IS NOT NULL";
            } else {
                query += "\nAND bm.p_discounttier IS NULL";
            }
        }
        if (withPOS != null) {
            if (withPOS) {
                query += "\nAND d.p_posfeetier IS NOT NULL";
            } else {
                query += "\nAND d.p_posfeetier IS NULL";
            }
        }
        query += ";";

        return this.jdbcTemplate.query(QueryParser.parse(query), (rs, rowNum) -> new DealModel(
                rs.getString("SystemName"),
                rs.getString("OfficeId"),
                rs.getString("CorporateId"),
                rs.getString("POSFee"),
                rs.getString("Discount"),
                rs.getString("BookingId")
        ));
    }

    public List<ItemModel> getDiscount(List<String> discounts, String currency) {

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("discounts", discounts)
                .addValue("currency", currency);

        String query =
                "SELECT\n" +
                        "d.p_code AS Code\n" +
                        ",dr.p_value AS Value\n" +
                        "FROM dealdiscountrow AS dr\n" +
                        "JOIN dealdiscount AS d ON dr.p_discount = d.pk\n" +
                        "JOIN currencies AS c ON dr.p_currency = c.pk\n" +
                        "WHERE dr.pk IN (:discounts)\n" +
                        "AND c.p_isocode = :currency;";

        return this.jdbcTemplate.query(QueryParser.parse(query), parameters, (rs, rowNum) -> new ItemModel(
                rs.getString("Code"),
                rs.getString("Value")
        ));
    }

    public List<ItemModel> getFee(List<String> fees, String currency) {

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("fees", fees)
                .addValue("currency", currency);

        String query =
                "SELECT\n" +
                        "t.p_code AS Code\n" +
                        ",tr.p_value AS Value\n" +
                        "FROM taxrows AS tr\n" +
                        "JOIN taxes AS t ON tr.p_tax = t.pk\n" +
                        "JOIN currencies AS c ON tr.p_currency = c.pk\n" +
                        "WHERE tr.pk IN (:fees)\n" +
                        "AND c.p_isocode = :currency;";

        return this.jdbcTemplate.query(QueryParser.parse(query), parameters, (rs, rowNum) -> new ItemModel(
                rs.getString("Code"),
                rs.getString("Value")
        ));
    }

}