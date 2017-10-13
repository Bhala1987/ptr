package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.hybris.easyjet.database.hybris.models.FeeTaxAndMissingCurrencyModel;
import com.hybris.easyjet.database.hybris.models.FeesAndTaxesModel;
import com.jolbox.bonecp.BoneCPDataSource;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by AndrewGr on 14/12/2016.
 * this class allows read access to admin fee data in Hybris
 */
@Repository
public class FeesAndTaxesDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private JdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public FeesAndTaxesDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {

        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * @return list of Admin Fees from Hybris
     */
    public List<FeesAndTaxesModel> getAdminFees(String feesCurrency) {
        return getFees("AdminFee", null, feesCurrency, null, null, null, null);
    }

    public List<FeesAndTaxesModel> getAdminFees(String feesCurrency, String bundle) {
        return getFees("AdminFee", null, feesCurrency, null, null, null, bundle);
    }

    public List<FeesAndTaxesModel> getCardFees() {
        return getFees("CardFee", null, null, null, null, null);
    }

    public List<FeesAndTaxesModel> getFeeOrTaxValue(String taxName, String currency, String passengerType) {
        return getFees(null, taxName, currency, passengerType, null, null);
    }

    public List<FeesAndTaxesModel> getFees(String taxName, String currency, String passengerType, String channel, String sector) {
        return getFees(null, taxName, currency, passengerType, channel, sector, null);
    }

    public List<FeesAndTaxesModel> getFees(String code, String taxName, String currency, String passengerType) {
        return getFees(code, taxName, currency, passengerType, null, null, null);
    }

    public List<FeesAndTaxesModel> getFees(String code, String taxName, String currency, String passengerType, String channel, String sector) {
        return getFees(code, taxName, currency, passengerType, channel, sector, null);
    }

    public List<FeesAndTaxesModel> getFees(String code, String taxName, String currency, String passengerType, String channel, String sector, String bundle) {

        MapSqlParameterSource params;
        String query;
        List<String> taxes = new ArrayList<String>(){{add("");}};

        if (StringUtils.isNotEmpty(bundle)) {
            params = new MapSqlParameterSource("bundle", bundle);

            query =
                    "SELECT p_taxes\n" +
                            "FROM bundletemplates\n" +
                            "WHERE p_catalogversion IN (\n" +
                            "   SELECT pk\n" +
                            "   FROM catalogversions\n" +
                            "   WHERE p_version = 'Online'\n" +
                            ")\n" +
                            "AND p_id = :bundle;";

            String taxList = this.namedParameterJdbcTemplate.queryForObject(QueryParser.parse(query), params, String.class);
            if (StringUtils.isNotBlank(taxList))
                taxes = Arrays.asList(taxList.replace(",#1,", "").split(","));
        }

        params = new MapSqlParameterSource()
                .addValue("code", code)
                .addValue("taxName", taxName)
                .addValue("currency", currency)
                .addValue("passengerType", passengerType)
                .addValue("channel", channel)
                .addValue("sector", sector)
                .addValue("taxes", taxes);

        query =
                "SELECT\n" +
                        "t.p_code\n" +
                        ",tslp.p_name\n" +
                        ",tr.p_passengertype\n" +
                        ",tr.p_value\n" +
                        ",c.p_isocode\n" +
                        "FROM taxes AS t\n" +
                        "INNER JOIN taxrows AS tr ON t.PK = tr.p_tax\n" +
                        "INNER JOIN taxeslp AS tslp ON t.PK = tslp.itempk\n" +
                        "LEFT OUTER JOIN enumerationvalues AS ev ON tr.p_channel = ev.PK\n" +
                        "LEFT OUTER JOIN currencies AS c ON tr.p_currency = c.PK\n" +
                        "WHERE GETDATE() > tr.p_starttime\n" +
                        "AND GETDATE() < tr.p_endtime\n";

        if (StringUtils.isNotEmpty(sector)) {
            query += "AND (tr.p_sectorcode = :sector OR tr.p_sectorcode IS NULL)\n";
        }

        if (StringUtils.isNotEmpty(channel)) {
            query += "AND (ev.Code = :channel  OR ev.Code IS NULL)\n";
        }

        if (StringUtils.isNotEmpty(passengerType)) {
            query += "AND (tr.p_passengertype = :passengerType OR tr.p_passengertype IS NULL)\n";
        } else if (passengerType != null) {
            query += "AND tr.p_passengertype IS NULL\n";
        }

        if (StringUtils.isNotEmpty(currency)) {
            query += "AND (c.p_isocode = :currency OR c.p_isocode IS NULL)\n";
        }

        if (StringUtils.isNotEmpty(channel)) {
            query += "AND (ev.Code = :channel  OR ev.Code IS NULL)\n";
        }

        if (StringUtils.isNotEmpty(bundle)) {
            query += "AND t.pk IN (:taxes)\n";
        }

        if (StringUtils.isNotEmpty(code)) {
            query += "AND t.p_code = :code;";
        } else if (StringUtils.isNotEmpty(taxName)) {
            query += "AND tslp.p_name = :taxName;";
        }

        return this.namedParameterJdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> new FeesAndTaxesModel(
                rs.getString("p_code"),
                rs.getString("p_name"),
                rs.getString("p_passengertype"),
                rs.getString("p_value"),
                rs.getString("p_isocode")
        ));

    }

    public HashMap<String, Double> getTaxesForPassenger(String sector, String currency, String passengerType) {

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("sector", sector)
                .addValue("currency", currency)
                .addValue("passengertype", passengerType);

        String query =
                "SELECT\n" +
                        "t.p_code AS code\n" +
                        ",tr.p_value AS value\n" +
                        "FROM taxrows AS tr\n" +
                        "	INNER JOIN currencies AS c ON tr.p_currency = c.pk\n" +
                        "	INNER JOIN taxes AS t ON tr.p_tax = t.pk\n" +
                        "WHERE GETDATE() > tr.p_starttime\n" +
                        "AND GETDATE() < tr.p_endtime\n" +
                        "AND tr.p_sectorcode = :sector\n" +
                        "AND c.p_isocode = :currency\n" +
                        "AND tr.p_passengertype = :passengertype";

        return this.namedParameterJdbcTemplate.query(QueryParser.parse(query), params, rs -> {

            HashMap<String, Double> taxes = new HashMap<>();
            while (rs.next()) {
                taxes.put(rs.getString("code"), rs.getDouble("value"));
            }
            return taxes;
        });

    }

    public List<FeesAndTaxesModel> getDiscount(String code, String discountName, String currency, String channel) {

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("code", code)
                .addValue("discountName", discountName)
                .addValue("currency", currency)
                .addValue("channel", channel);

        String query =
                "SELECT\n" +
                        "p_code\n" +
                        ",p_name\n" +
                        ",dr.p_value\n" +
                        ",c.p_isocode\n" +
                        ",ev.Code\n" +
                        "FROM ejdiscount AS d\n" +
                        "   INNER JOIN ejdiscountrow AS dr ON d.PK = dr.p_discount\n" +
                        "   INNER JOIN ejdiscountlp AS dlp ON d.pk = dlp.ITEMPK\n" +
                        "   LEFT OUTER JOIN enumerationvalues AS ev ON dr.p_channel = ev.PK\n" +
                        "   LEFT OUTER JOIN currencies AS c ON dr.p_currency = c.PK\n" +
                        "WHERE GETDATE() > dr.p_starttime\n" +
                        "   AND GETDATE() < dr.p_endtime\n" +
                        "   AND d.p_code like 'InternetDisc'\n";

        if (StringUtils.isNotEmpty(channel)) {
            query += "AND (ev.type = :channel OR ev.type IS NULL)\n";
        }

        if (StringUtils.isNotEmpty(currency)) {
            query += "AND (c.p_isocode = :currency OR c.p_isocode IS NULL)\n";
        }

        if (StringUtils.isNotEmpty(code)) {
            query += "AND d.p_code = :code;";
        } else if (StringUtils.isNotEmpty(discountName)) {
            query += "AND dlp.p_name = :discountName;";
        }

        return this.namedParameterJdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> new FeesAndTaxesModel(
                rs.getString("p_code"),
                rs.getString("p_name"),
                null,
                rs.getString("dr.p_value"),
                rs.getString("c.p_isocode")
        ));

    }

    public List<FeesAndTaxesModel> getFeesBasedOnType(String feesCurrency, String feesCode,String channel) {
        return getFees(feesCode, null, feesCurrency, null, channel, null, null);
    }

    public Integer countFeesAndTaxes() {

        String query = "select count(*) \n" +
              "from (select tr.pk \n" +
              "from taxrows as tr JOIN taxes as t on tr.p_tax = t.pk \n" +
              "where t.p_taxtype in (\n" +
              "select tt.pk from enumerationvalues as tt where tt.Code = 'FEE' or tt.Code = 'TAX')) as Res";

        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), Integer.class);
    }


    /* return a list of FeeTaxAndMissingCurrencyModel
       tax-code | missing-currency-isocode
     */
    public List<FeeTaxAndMissingCurrencyModel> getFeesAndTaxesWithoutCurrency() {

        String query =
              "SELECT tax.p_code , cur.p_isocode \n" +
              "FROM ( \n" +
                            "SELECT distinct tax.p_code \n" +
                            "FROM  taxes AS tax JOIN taxrows AS txr ON tax.pk = txr.p_tax \n" +
                            "WHERE  tax.p_taxtype  IN ( SELECT en.pk  FROM enumerationvalues en \n" +
                                                        "WHERE ( en.Code  IN ('FEE', 'TAX')) \n" +
                                                        "AND en.p_extensionname='ejcore') \n" +
              ") AS tax, currencies cur \n" +
              "WHERE not exists ( \n" +
                    "SELECT * \n" +
                    "FROM taxrows AS tr JOIN taxes AS t ON  tr.p_tax = t.PK  JOIN currencies AS c ON tr.p_currency = c.pk \n" +
                    "WHERE t.p_taxtype  IN (SELECT en.pk  FROM enumerationvalues en \n" +
                                            "WHERE ( en.Code  IN ('FEE', 'TAX')) \n" +
                                            "AND en.p_extensionname='ejcore') \n" +
              "AND NOW() BETWEEN tr.p_starttime AND tr.p_endtime \n" +
              "AND tax.p_code = t.p_code AND cur.p_isocode = c.p_isocode)";

        return this.jdbcTemplate.query(QueryParser.parse(query), (rs, rowNum) -> new FeeTaxAndMissingCurrencyModel(
              rs.getString("p_code"),
              rs.getString("p_isocode")));
    }
}
