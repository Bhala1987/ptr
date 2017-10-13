package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.jolbox.bonecp.BoneCPDataSource;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by robertadigiorgio on 20/04/2017.
 */
@Repository
@ToString
public class BasketDao {
    private NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public BasketDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<String> searchPassengerIdOutbound(String passengerId) {

        SqlParameterSource params = new MapSqlParameterSource("p_code", passengerId);
        String query =
                "SELECT item_t2.p_sameperson\n" +
                        "FROM travelorderentryinfo item_t0\n" +
                        "JOIN orderentrytraveller item_t1 ON item_t1.SourcePK = item_t0.PK\n" +
                        "JOIN traveller item_t2 ON item_t1.TargetPK = item_t2.PK\n" +
                        "WHERE item_t0.p_code = :p_code;";


        String pks = this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> rs.getString("p_sameperson")).get(0);
        if (Objects.isNull(pks)) {
            return null;
        } else {
            return Arrays.asList(pks.substring(4, pks.length() - 1).split(","));
        }
    }

    public String codePassengerIdOutbound(Long passengerId) {

        SqlParameterSource params = new MapSqlParameterSource("p_code", passengerId);
        String query =
                "SELECT  item_t0.p_code\n" +
                        "FROM travelorderentryinfo item_t0\n" +
                        "JOIN orderentrytraveller item_t1 ON item_t1.SourcePK  =  item_t0.PK\n" +
                        "JOIN traveller item_t2 ON  item_t1.TargetPK  =  item_t2.PK\n" +
                        "WHERE  item_t2.PK  = :p_code;";
        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, String.class);
    }


    public int checkRemoveItemtoPassenger(String passeggerId) {

        SqlParameterSource params = new MapSqlParameterSource("p_code", passeggerId);
        String query =
                "SELECT COUNT(*)\n" +
                        "FROM cartentries AS entri\n" +
                        "JOIN travelorderentryinfo AS travel ON entri.p_travelorderentryinfo = travel.PK\n" +
                        "WHERE travel.p_code = :p_code;";

        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, Integer.class);
    }

    public Double getSeatProductPriceForPassenger(String passeggerId, String productCode) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_code", passeggerId)
                .addValue("product_code", productCode);
        String query =
                "SELECT entri.p_baseprice\n" +
                        "FROM cartentries AS entri\n" +
                        "JOIN travelorderentryinfo AS travel ON entri.p_travelorderentryinfo = travel.PK\n" +
                        "JOIN products AS p ON p.pk = entri.p_product\n" +
                        "WHERE travel.p_code = :p_code AND p.p_code = :product_code;";

        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, Double.class);
    }
}
