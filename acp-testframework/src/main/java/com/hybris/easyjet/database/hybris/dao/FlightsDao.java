package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.TenantBeanFactoryPostProcessor;
import com.hybris.easyjet.database.QueryParser;
import com.hybris.easyjet.database.hybris.models.HybrisFlightDbModel;
import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by daniel on 23/11/2016.
 * provides readonly access to seatmap data in hybris
 */
@Repository
public class FlightsDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public FlightsDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public static FlightsDao getFlightsDaoFromSpring() {
        return (FlightsDao) TenantBeanFactoryPostProcessor.getFactory().getBean("flightsDao");
    }

    /**
     * @param flightKeys he seatmap keys to search for
     * @param withTaxes  boolean that specify if we want flight with taxes or not
     * @return a list of flights that exist in Hybris with the seatmap keys provided
     */
    public List<HybrisFlightDbModel> returnFlightsThatExistFromList(List<String> flightKeys, boolean withTaxes) {

        SqlParameterSource params = new MapSqlParameterSource("flightKeys", flightKeys);

        String query = "SELECT\n";
        if (withTaxes) {
            query += "DISTINCT [warehouses].[p_code] as flightkey\n";
        } else {
            query += "[warehouses].[p_code] as flightkey\n";
        }
        query +=
                ",CONVERT(VARCHAR(10), CAST(p_departuretime AS DATE), 105) as localdepdt\n" +
                        ",[p_arrivaltime]\n" +
                        ",[p_originterminal]\n" +
                        ",[p_destinationterminal]\n" +
                        ",[p_status]\n" +
                        ",[p_updateddeparturetime]\n" +
                        ",[warehouses].[p_active]\n" +
                        ",[p_scheduleddeptime]\n" +
                        ",[p_scheduledarrtime]\n" +
                        ",[p_planneddeptime]\n" +
                        ",[p_plannedarrtime]\n" +
                        ",p_infantsonseatlimit\n" +
                        ",p_infantsonseatconsumed\n" +
                        ",p_infantslimit\n" +
                        ",p_infantsconsumed\n" +
                        ",sector.p_code as route\n" +
                        ",origin.p_code as departs\n" +
                        ",dest.p_code as arrives\n" +
                        ",origin.p_defaultcurrency as currency\n" +
                        "FROM [dbo].[warehouses]\n" +
                        "INNER JOIN travelsector as sector ON p_travelsector = sector.PK\n" +
                        "INNER JOIN transportfacility as origin ON sector.p_origin = origin.PK\n" +
                        "INNER JOIN transportfacility as dest ON sector.p_destination = dest.PK\n" +
                        "INNER JOIN enumerationvalues as enum ON enum.PK = warehouses.p_status\n";
        if (withTaxes)
            query += "INNER JOIN taxrows as flighttax ON  flighttax.p_sectorcode = sector.p_code\n";
        query += !flightKeys.isEmpty() ?
                "WHERE [warehouses].[p_code] IN (:flightKeys)\n" +
                        "AND " : "WHERE ";
        query +=
                "p_departuretime BETWEEN DATEADD(day, 1, GETDATE()) AND DATEADD(month, 10, GETDATE())\n" +
                        "AND enum.Code != 'CANCELLED';";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) ->
                new HybrisFlightDbModel(
                        rs.getString("flightKey"),
                        rs.getString("localdepdt"),
                        rs.getString("p_arrivaltime"),
                        rs.getString("p_originterminal"),
                        rs.getString("p_destinationterminal"),
                        rs.getString("p_status"),
                        rs.getString("p_updateddeparturetime"),
                        rs.getString("p_active"),
                        rs.getString("p_scheduleddeptime"),
                        rs.getString("p_scheduledarrtime"),
                        rs.getString("p_planneddeptime"),
                        rs.getString("p_plannedarrtime"),
                        rs.getString("route"),
                        rs.getString("departs"),
                        rs.getString("arrives"),
                        rs.getString("currency"),
                        rs.getInt("p_infantsonseatlimit"),
                        rs.getInt("p_infantsonseatconsumed"),
                        rs.getInt("p_infantslimit"),
                        rs.getInt("p_infantsconsumed")
                )
        );
    }

    /**
     * @param flightKeys the flightKeys returned by 'getFlightKeysFromEResDB'
     * @param channel    the channel for which the request will be used
     * @param product    the product that will be added to the basket
     * @param needed     the quantity of that product that will be added
     * @return
     */
    public List<HybrisFlightDbModel> returnValidFlightKeyForStockLevel(List<String> flightKeys, String channel, String product, int needed) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("flightKeys", flightKeys)
                .addValue("channel", channel)
                .addValue("product", product)
                .addValue("needed", needed);

        String query =
                "SELECT\n" +
                        "warehouses.p_code as flightkey\n" +
                        ",DATE_FORMAT (CAST(p_departuretime AS DATE), '%d-%m-%Y') as localdepdt\n" +
                        ",p_arrivaltime\n" +
                        ",p_originterminal\n" +
                        ",p_destinationterminal\n" +
                        ",p_status\n" +
                        ",p_updateddeparturetime\n" +
                        ",warehouses.p_active\n" +
                        ",p_scheduleddeptime\n" +
                        ",p_scheduledarrtime\n" +
                        ",p_planneddeptime\n" +
                        ",p_plannedarrtime\n" +
                        ",p_infantsonseatlimit\n" +
                        ",p_infantsonseatconsumed\n" +
                        ",p_infantslimit\n" +
                        ",p_infantsconsumed\n" +
                        ",sector.p_code as route\n" +
                        ",origin.p_code as departs\n" +
                        ",dest.p_code as arrives\n" +
                        ",origin.p_defaultcurrency as currency\n" +
                        "FROM warehouses\n" +
                        "	INNER JOIN travelsector as sector ON p_travelsector = sector.PK\n" +
                        "	INNER JOIN transportfacility as origin ON sector.p_origin = origin.PK\n" +
                        "	INNER JOIN transportfacility as dest ON sector.p_destination = dest.PK\n" +
                        "	INNER JOIN enumerationvalues as enum ON enum.PK = warehouses.p_status\n" +
                        "	INNER JOIN stocklevels ON warehouses.PK = stocklevels.p_warehouse\n" +
                        "	WHERE stocklevels.p_productcode IN (\n" +
                        "		SELECT\n" +
                        "			p.p_code\n" +
                        "		FROM pricerows AS pr\n" +
                        "			JOIN enumerationvalues AS evc ON evc.pk = pr.p_salesapplication\n" +
                        "			JOIN currencies AS c ON c.pk = pr.p_currency\n" +
                        "			LEFT JOIN usergroups AS ug ON ug.pk = pr.p_ug\n" +
                        "			JOIN products AS p ON p.pk = pr.p_product\n" +
                        "			JOIN enumerationvalues AS eva ON eva.pk = p.p_approvalstatus\n" +
                        "			JOIN composedtypes AS ct ON ct.pk = p.TypePkString\n" +
                        "			WHERE p.p_catalogversion IN (\n" +
                        "				SELECT pk\n" +
                        "				FROM catalogversions\n" +
                        "				WHERE p_version = 'Online'\n" +
                        "			)\n" +
                        "			AND ct.InternalCode = :product\n" +
                        "			AND p.p_onlinedate < NOW()\n" +
                        "			AND p.p_offlinedate > NOW()\n" +
                        "			AND eva.Code = 'approved'\n" +
                        "			AND evc.Code = :channel\n" +
                        "			AND pr.p_transportofferingcode IS NULL\n" +
                        "			AND pr.p_travelsectorcode IS NULL\n" +
                        "			AND ug.p_uid IS NULL\n" +
                        "	)\n" +
                        "	AND stocklevels.p_reserved + :needed < stocklevels.p_available\n";

        query += !flightKeys.isEmpty() ? "AND warehouses.p_code IN (:flightKeys)\n" : "";
        query +=
                "AND p_departuretime BETWEEN DATE_ADD(NOW(), INTERVAL 1 DAY) AND DATE_ADD(NOW(), INTERVAL 10 MONTH)\n" +
                        "AND enum.Code != 'CANCELLED';";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) ->
                new HybrisFlightDbModel(
                        rs.getString("flightKey"),
                        rs.getString("localdepdt"),
                        rs.getString("p_arrivaltime"),
                        rs.getString("p_originterminal"),
                        rs.getString("p_destinationterminal"),
                        rs.getString("p_status"),
                        rs.getString("p_updateddeparturetime"),
                        rs.getString("p_active"),
                        rs.getString("p_scheduleddeptime"),
                        rs.getString("p_scheduledarrtime"),
                        rs.getString("p_planneddeptime"),
                        rs.getString("p_plannedarrtime"),
                        rs.getString("route"),
                        rs.getString("departs"),
                        rs.getString("arrives"),
                        rs.getString("currency"),
                        rs.getInt("p_infantsonseatlimit"),
                        rs.getInt("p_infantsonseatconsumed"),
                        rs.getInt("p_infantslimit"),
                        rs.getInt("p_infantsconsumed")
                )
        );
    }

    /**
     * Return valid stock level as int
     *
     * @param flightKey we want to retrive the stocklevel available
     * @param product   we want to retrive the stocklevel
     * @return
     */
    public int getAvailableStockLevelForFlight(String flightKey, String product) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("flightKey", flightKey)
                .addValue("product", product);

        String query =
                "SELECT sl.p_available\n" +
                        "FROM stocklevels AS sl\n" +
                        "	JOIN warehouses AS w ON w.pk = sl.p_warehouse\n" +
                        "WHERE w.p_code = :flightKey\n" +
                        "	AND sl.p_productcode = :product;";

        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, Integer.class);
    }

    /**
     * Return valid stock level as int
     *
     * @param flightKey we want to retrive the stocklevel available
     * @param product   we want to retrive the stocklevel
     * @return
     */
    public int getReservedStockLevelForFlight(String flightKey, String product) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("flightKey", flightKey)
                .addValue("product", product);

        String query =
                "SELECT sl.p_reserved\n" +
                        "FROM stocklevels AS sl\n" +
                        "	JOIN warehouses AS w ON w.pk = sl.p_warehouse\n" +
                        "WHERE w.p_code = :flightKey\n" +
                        "	AND sl.p_productcode = :product;";

        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, Integer.class);
    }

    public void updateTheDepartureDateForFlight(String flightKey, String date) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("flightKey", flightKey)
                .addValue("date", date);

        String query =
                "UPDATE warehouses \n" +
                        "SET p_departuretime = :date \n" +
                        "WHERE p_code = :flightKey";

        this.jdbcTemplate.update(query, params);
    }

    public Map<String, Integer> getInfantsLimitAndConsumed(String flightKey) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("flightKey", flightKey);

        String query =
                "SELECT warehouses.p_code as flightkey\n" +
                        ",p_infantsonseatlimit\n" +
                        ",p_infantsonseatconsumed\n" +
                        ",p_infantslimit\n" +
                        ",p_infantsconsumed\n" +
                        "FROM warehouses\n" +
                        "WHERE warehouses.p_code = :flightKey";

        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, (rs, rowNum) -> new HashMap<String, Integer>() {{
            put("InfantsLimit", rs.getInt("p_infantslimit"));
            put("InfantsConsumed", rs.getInt("p_infantsconsumed"));
            put("InfantsOnSeatLimit", rs.getInt("p_infantsonseatlimit"));
            put("InfantsOnSeatConsumed", rs.getInt("p_infantsonseatconsumed"));
        }});
    }
}