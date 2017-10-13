package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.hybris.easyjet.database.hybris.models.DeallocateFareModel;
import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.constants.DBConstants.FARE_TYPE;

/**
 * Created by giuseppedimartino on 26/04/17.
 */
@Repository
public class FareClassDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public FareClassDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<DeallocateFareModel> getFareClassForCart(String basketId, String flightKey) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("basketId", basketId)
                .addValue("flightKey", flightKey)
                .addValue("fareType", FARE_TYPE);

        String query =
                "SELECT ce.p_fareclass AS fareClass, COUNT(ce.p_fareclass) AS count\n" +
                        "FROM carts AS c\n" +
                        "   INNER JOIN cartentries AS ce ON ce.p_order = c.PK\n" +
                        "       AND ce.p_product IN (\n" +
                        "           SELECT PK\n" +
                        "           FROM products\n" +
                        "           WHERE p_code IN (:fareType)\n" +
                        "       )\n" +
                        "WHERE c.p_code = :basketId\n" +
                        "   AND ce.p_flightcode = :flightKey\n";

        if ("local".equals(System.getProperty("environment")))
            query += "GROUP BY fareClass;";
        else
            query += "GROUP BY ce.p_fareclass;";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, numRow) -> new DeallocateFareModel(
                rs.getString("fareClass"),
                rs.getInt("count")
        )).stream().filter(dfm -> dfm.getNumberRequired() > 0).collect(Collectors.toList());
    }

    public List<DeallocateFareModel> getFareClassForOrder(String bookingReference, String flightKey) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("bookingReference", bookingReference)
                .addValue("flightKey", flightKey)
                .addValue("fareType", FARE_TYPE);

        String query =
                "SELECT oe.p_fareclass AS fareClass, COUNT(oe.p_fareclass) AS count\n" +
                        "FROM orders AS o\n" +
                        "   INNER JOIN orderentries AS oe ON oe.p_order = o.PK\n" +
                        "       AND oe.p_product IN (\n" +
                        "           SELECT PK\n" +
                        "           FROM products\n" +
                        "           WHERE p_code IN (:fareType)\n" +
                        "       )\n" +
                        "WHERE o.p_bookingreference = :bookingReference\n" +
                        "   AND oe.p_flightcode = :flightKey\n";

        if ("local".equals(System.getProperty("environment")))
            query += "GROUP BY fareClass;";
        else
            query += "GROUP BY oe.p_fareclass;";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, numRow) -> new DeallocateFareModel(
                rs.getString("fareClass"),
                rs.getInt("count")
        )).stream().filter(dfm -> dfm.getNumberRequired() > 0).collect(Collectors.toList());
    }

}