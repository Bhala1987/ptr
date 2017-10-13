package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.hybris.easyjet.database.hybris.models.SectorsModel;
import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by giuseppedimartino on 31/01/17.
 */
@Repository
public class SectorsDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public SectorsDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<SectorsModel> returnActiveSectors() {

        String query =
                "SELECT\n" +
                        "p_code\n" +
                        ",p_distance\n" +
                        ",p_apis\n" +
                        "FROM travelsector\n" +
                        "WHERE p_sectorstart < NOW()\n" +
                        "AND p_sectorend > NOW();";

        return this.jdbcTemplate.query(QueryParser.parse(query), (rs, numRow) -> new SectorsModel(
                rs.getString("p_code"),
                rs.getString("p_distance"),
                rs.getString("p_apis")
        ));

    }

    public List<SectorsModel> returnSectorsWithApiTrue(boolean isAPis) {

        SqlParameterSource params = new MapSqlParameterSource().addValue("p_apis", isAPis);

        String query =
                "SELECT\n" +
                        "p_code\n" +
                        ",p_apis\n" +
                        "FROM travelsector\n" +
                        "WHERE p_sectorstart < GETDATE()\n" +
                        "AND p_sectorend > GETDATE() " +
                        "AND p_apis = :p_apis;";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> SectorsModel.builder()
                .code(rs.getString("p_code"))
                .apis(rs.getBoolean("p_apis"))
                .build());


    }



    public List<SectorsModel> returnActiveSectorsForOriginAirport(String origin) {

        SqlParameterSource params = new MapSqlParameterSource("origin", origin);

        String query =
                "SELECT\n" +
                        "ts.p_code\n" +
                        ",ts.p_distance\n" +
                        ",ts.p_apis\n" +
                        "FROM travelsector as ts\n" +
                        "LEFT JOIN transportfacility as tfo ON ts.p_origin = tfo.pk\n" +
                        "WHERE ts.p_sectorstart < NOW()\n" +
                        "AND ts.p_sectorend > NOW()\n" +
                        "AND tfo.p_code = :origin;";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, numRow) -> new SectorsModel(
                rs.getString("p_code"),
                rs.getString("p_distance"),
                rs.getString("p_apis")
        ));

    }

    public String returnPKSectorFromCode(String code) {

        SqlParameterSource params = new MapSqlParameterSource("p_code", code);

        String query =
                "SELECT\n" +
                        "PK\n" +
                        "FROM travelsector\n" +
                        "WHERE p_code = :p_code\n";

        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, String.class);

    }

}
