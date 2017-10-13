package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.List;

/**
 * Created by giuseppedimartino on 28/04/17.
 */
@Repository
public class PassengerInformationDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public PassengerInformationDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public int getPassengerInformationNumber (String passenger) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("passenger", passenger);

        String query =
                "SELECT COUNT(*)\n" +
                "FROM passengerinformation AS pi\n" +
                "   INNER JOIN traveller AS t ON t.p_info = pi.PK\n" +
                "   INNER JOIN orderentrytraveller AS oet ON oet.TargetPK = t.PK\n" +
                "   INNER JOIN travelorderentryinfo AS toei ON toei.PK = oet.SourcePK\n" +
                "WHERE toei.p_code = :passenger;";

        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, Integer.class);

    }

    public List<HashMap<String, String>> getVoucherInformation (String orderRefNo) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("orderRefNo", orderRefNo);
        String query =
                "SELECT p_vouchercode,p_activedatefrom, p_activedateto,p_emailaddress FROM discounts where p_originalbookingreference = :orderRefNo;";
        List<HashMap<String, String>> voucherDetails = this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> new HashMap<String, String>() {{
            put("voucherCode", rs.getString("p_vouchercode"));
            put("dateFrom", rs.getString("p_activedatefrom"));
            put("dateTo", rs.getString("p_activedateto"));
            put("email", rs.getString("p_emailaddress"));
        }});
        return voucherDetails;

    }
}
