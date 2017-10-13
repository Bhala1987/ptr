package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.TenantBeanFactoryPostProcessor;
import com.hybris.easyjet.database.QueryParser;
import com.hybris.easyjet.database.hybris.models.PassengerTypeDbModel;
import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by daniel on 23/11/2016.
 * provides readonly access to passenger reference data in hybris
 */
@Repository
public class PassengerTypeDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public PassengerTypeDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public static PassengerTypeDao getTestDataFromSpring() {
        return (PassengerTypeDao) TenantBeanFactoryPostProcessor.getFactory().getBean("passengerTypeDao");
    }

    /**
     * @param passengerType the passenger type to search for
     * @return list of passenger types that match the passenger type requested
     */
    public PassengerTypeDbModel getPassengersOfType(String passengerType) {

        SqlParameterSource params = new MapSqlParameterSource("passengerType", passengerType);

        String query =
                "SELECT\n" +
                        "[p_code]\n" +
                        ",[p_minage]\n" +
                        ",[p_maxage]\n" +
                        ",[p_passengertypecode]\n" +
                        ",[p_active]\n" +
                        "FROM [dbo].[passengertype]\n" +
                        "WHERE p_active = 1\n" +
                        "   AND p_code = :passengerType;";

        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, (rs, rowNum) -> new PassengerTypeDbModel(
                rs.getString("p_code"),
                rs.getInt("p_minage"),
                rs.getInt("p_maxage"),
                rs.getString("p_passengertypecode"),
                rs.getBoolean("p_active")));
    }

    /**
     * @return list of all passenger types
     */
    public List<PassengerTypeDbModel> getPassengerTypes() {

        String query =
                "SELECT\n" +
                        "[p_code]\n" +
                        ",[p_minage]\n" +
                        ",[p_maxage]\n" +
                        ",[p_passengertypecode]\n" +
                        ",[p_active]\n" +
                        "FROM [dbo].[passengertype]\n" +
                        "WHERE p_active = 1;";

        return this.jdbcTemplate.query(QueryParser.parse(query), (rs, rowNum) -> new PassengerTypeDbModel(
                rs.getString("p_code"),
                rs.getInt("p_minage"),
                rs.getInt("p_maxage"),
                rs.getString("p_passengertypecode"),
                rs.getBoolean("p_active")));
    }
}
