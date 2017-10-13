package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.hybris.easyjet.database.hybris.models.ChannelPropertiesModel;
import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * Created by dwebb on 1/11/2017.
 */
@Repository
public class ChannelPropertiesDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public ChannelPropertiesDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * @return list of valid customers with a shipping address
     */
    public List<ChannelPropertiesModel> returnChannelProperties() {

        String query =
                "SELECT\n" +
                        "ev.code\n" +
                        ",p_propertyname\n" +
                        ",p_propertyvalue\n" +
                        "FROM propvalueconfig\n" +
                        "INNER JOIN enumerationvalues ev ON ev.PK = p_channel\n" +
                        "ORDER BY p_channel;";

        return this.jdbcTemplate.query(QueryParser.parse(query), (rs, rowNum) -> new ChannelPropertiesModel(
                rs.getString("code"),
                rs.getString("p_propertyname"),
                rs.getString("p_propertyvalue")));
    }

}