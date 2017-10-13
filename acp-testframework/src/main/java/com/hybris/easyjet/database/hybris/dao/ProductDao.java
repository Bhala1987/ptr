package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.hybris.easyjet.database.hybris.models.ProductModel;
import com.jolbox.bonecp.BoneCPDataSource;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

/**
 * Created by marco on 27/02/17.
 */
@Repository
public class ProductDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public ProductDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<ProductModel> getProductsForBundle(String bundleCode) {

        SqlParameterSource params = new MapSqlParameterSource("bundleCode", bundleCode);

        String query =
                "SELECT\n" +
                        "prod.p_code\n" +
                        ",en.codeLowerCase AS prodType\n" +
                        "FROM prod2bundletemplrel AS rel\n" +
                        "JOIN products AS prod ON rel.SourcePK = prod.PK\n" +
                        "JOIN bundletemplates AS bundle ON rel.TargetPK = bundle.PK\n" +
                        "JOIN catalogversions AS cv ON bundle.p_catalogversion = cv.PK\n" +
                        "JOIN enumerationvalues AS en ON prod.p_producttype = en.PK\n" +
                        "WHERE bundle.p_id = :bundleCode\n" +
                        "AND cv.p_version = 'Staged';";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> new ProductModel(
                rs.getString("p_code"),
                rs.getString("prodType")
        ));
    }

    public List<ProductModel> getRestrictedChannelsForProduct(String productCode) {

        SqlParameterSource params = new MapSqlParameterSource("productCode", productCode);

        String query =
                "SELECT\n" +
                        "prod.p_code\n" +
                        ",channel.Code AS restrictedChannel\n" +
                        "FROM products AS prod\n" +
                        "JOIN catalogversions AS cv ON prod.p_catalogversion = cv.PK\n" +
                        "JOIN bagprod2chanrestrictrel AS b2c ON b2c.SourcePK = prod.PK\n" +
                        "JOIN enumerationvalues AS channel ON b2c.TargetPK = channel.PK\n" +
                        "WHERE prod.p_code = :productCode\n" +
                        "AND cv.p_version = 'Online';";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> new ProductModel(
              rs.getString("p_code"),
              StringUtils.EMPTY,
              Arrays.asList(rs.getString("restrictedChannel"))
        ));
    }

}