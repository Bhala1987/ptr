package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.hybris.easyjet.database.hybris.models.TravelDocumentTypesModel;
import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by giuseppecioce on 08/02/2017.
 */
@Repository
public class TravelDocumentTypesDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public TravelDocumentTypesDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<TravelDocumentTypesModel> getTravelDocumentType(boolean active) {

        SqlParameterSource params = new MapSqlParameterSource("p_active", active);

        String query =
                "SELECT\n" +
                        "[p_code]\n" +
                        ",[p_active]\n" +
                        "FROM [dbo].[traveldocumenttype]\n" +
                        "WHERE p_active = :p_active;";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> new TravelDocumentTypesModel(
                rs.getString("p_code"),
                rs.getBoolean("p_active")));
    }

}