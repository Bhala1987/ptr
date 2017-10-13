package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.hybris.easyjet.database.hybris.helpers.AirportsForIndirectRoutes;
import com.hybris.easyjet.database.hybris.helpers.IndirectRoute;
import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * Created by ptr-kvijayapal on 2/8/2017.
 */
@Repository
public class IndirectFlightRoutesDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public IndirectFlightRoutesDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * @return list of all indirect routes
     */
    public List<IndirectRoute> getAllIndirectRoutes() {

        String query =
                "SELECT\n" +
                        "p_origin AS origin\n" +
                        ",p_destination AS destination\n" +
                        "FROM travelroute\n" +
                        "WHERE pk IN (\n" +
                        "SELECT DISTINCT sourcePK\n" +
                        "FROM transroutesector\n" +
                        "WHERE sourcePK IN (\n" +
                        "SELECT sourcePK\n" +
                        "FROM transroutesector\n" +
                        "GROUP BY sourcePK HAVING count(sourcePK) > 1\n" +
                        ")\n" +
                        ");";

        return this.jdbcTemplate.query(QueryParser.parse(query), (rs, rowNum) -> new IndirectRoute(
                rs.getString("origin"),
                rs.getString("destination")
        ));
    }

    public List<AirportsForIndirectRoutes> getListOfAirports() {

        String query =
                "SELECT\n" +
                        "pk\n" +
                        ",p_code\n" +
                        "FROM transportfacility";

        return this.jdbcTemplate.query(QueryParser.parse(query), (rs, rowNum) -> new AirportsForIndirectRoutes(
                rs.getString("pk"),
                rs.getString("p_code")
        ));
    }

    public List<String> getListOfViaAirportsFor(String origin, String destination) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("origin", origin)
                .addValue("destination", destination);

        String query =
                "SELECT p_code\n" +
                        "FROM transportfacility\n" +
                        "WHERE pk IN(\n" +
                        "SELECT p_destination\n" +
                        "FROM travelsector\n" +
                        "WHERE pk IN(\n" +
                        "SELECT targetPK\n" +
                        "FROM transroutesector\n" +
                        "WHERE sourcepk IN(\n" +
                        "SELECT pk\n" +
                        "FROM travelroute\n" +
                        "WHERE p_origin = (\n" +
                        "SELECT PK\n" +
                        "FROM transportfacility\n" +
                        "WHERE p_code = :origin\n" +
                        ")\n" +
                        "AND p_destination = (\n" +
                        "SELECT PK\n" +
                        "FROM transportfacility\n" +
                        "WHERE p_code = :destination\n" +
                        ")\n" +
                        "AND PK IN(\n" +
                        "SELECT sourcePK\n" +
                        "FROM transroutesector\n" +
                        "GROUP BY sourcePK HAVING count(sourcePK) > 1\n" +
                        ")\n" +
                        ")\n" +
                        "AND sequencenumber = 0\n" +
                        ")\n" +
                        ");";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, numRow) -> rs.getString("p_code"));

    }

    public List<String> getListOfAternateAirportsForAMarketGroup(String marketGroup) {

        SqlParameterSource params = new MapSqlParameterSource("marketgroup", marketGroup);

        String query =
                "SELECT tf.p_code AS type\n" +
                        "FROM transportfacility AS tf\n" +
                        "JOIN location AS l ON tf.p_location = l.pk\n" +
                        "WHERE l.p_code = :marketgroup";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, numRow) -> rs.getString("code"));

    }

    public List<String> getListOfAlternateAirportsForAnAirport(String airport) {

        SqlParameterSource params = new MapSqlParameterSource("airport", airport);

        String query =
                "SELECT tf.p_code AS type\n" +
                        "FROM transportfacility AS tf\n" +
                        "JOIN location AS l ON tf.p_location = l.pk\n" +
                        "WHERE l.pk = (\n" +
                        "	SELECT p_location\n" +
                        "	FROM transportfacility\n" +
                        "	WHERE p_code = :airport);";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, numRow) -> rs.getString("type"));

    }

    public List<String> getFlightKeyForRoute(String route) {
        SqlParameterSource params = new MapSqlParameterSource("p_code", route);

        String query =
                "SELECT warehouses.p_code \n" +
                        "FROM warehouses JOIN travelsector ON warehouses.p_travelsector = travelsector.PK\n" +
                        "WHERE travelsector.p_code = :p_code\n" +
                        "ORDER BY warehouses.p_departuretime DESC;";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, numRow) -> rs.getString("p_code"));
    }
}