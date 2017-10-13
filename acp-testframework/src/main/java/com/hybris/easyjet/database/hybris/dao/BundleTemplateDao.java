package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.hybris.easyjet.database.hybris.models.BundleTemplateModel;
import com.jolbox.bonecp.BoneCPDataSource;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by marco on 23/02/17.
 */
@Repository
public class BundleTemplateDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public BundleTemplateDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * It returns N rows for a single bundle template, depending on how many
     * localizations are present
     *
     * @return list of all staged bundle templates.
     */
    public List<BundleTemplateModel> getStagedBundleTemplates() {

        return getStagedBundleTemplatesWithGdsFareClass(null);
    }

    /**
     * It returns N rows for a single bundle template, depending on how many
     * localizations are present
     *
     * @param gdsFareClass
     * @return
     */
    public List<BundleTemplateModel> getStagedBundleTemplatesWithGdsFareClass(String gdsFareClass) {

        SqlParameterSource params = null;
        if (StringUtils.isNotEmpty(gdsFareClass)) {
            params = new MapSqlParameterSource("gdsFareClass", gdsFareClass);
        }

        String query =
                "SELECT\n" +
                        "bt.p_id\n" +
                        ", bt_lan.p_name\n" +
                        ", bt.p_gdsfareclass\n" +
                        ", lan.p_isocode\n" +
                        ",  bt_lan.p_description\n" +
                        ", bt_lan.p_fareconditions\n" +
                        "FROM bundletemplates AS bt\n" +
                        "JOIN bundletemplateslp AS bt_lan ON bt.PK = bt_lan.ITEMPK\n" +
                        "JOIN catalogversions AS cv ON cv.PK = bt.p_catalogversion\n" +
                        "JOIN languages AS lan ON lan.PK = bt_lan.LANGPK\n" +
                        "WHERE cv.p_version = 'Staged'";
        if (StringUtils.isNotEmpty(gdsFareClass)) {
            query += "\nAND bt.p_gdsfareclass = :gdsFareClass;";
        } else {
            query += ";";
        }

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> new BundleTemplateModel(
                rs.getString("p_id"),
                rs.getString("p_name"),
                rs.getString("p_description"),
                rs.getString("p_fareconditions"),
                rs.getString("p_gdsfareclass"),
                rs.getString("p_isocode")
        ));
    }

    public String getSeatIncluded (String bundle) {
        SqlParameterSource params = new MapSqlParameterSource("bundle", bundle);

        String query =
                "SELECT\n" +
                        "	p.p_code\n" +
                        "FROM products AS p\n" +
                        "	JOIN prod2bundletemplrel AS p2bt ON p2bt.SourcePK = p.pk\n" +
                        "	JOIN bundletemplates AS bt ON bt.pk = p2bt.TargetPK\n" +
                        "WHERE\n" +
                        "	bt.p_catalogversion IN (\n" +
                        "		SELECT pk\n" +
                        "		FROM catalogversions\n" +
                        "		WHERE p_version = 'Online'\n" +
                        "	)\n" +
                        "	AND bt.p_id = :bundle\n" +
                        "	AND p.p_seattier IS NOT NULL;";

        try {
            return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, String.class);
        } catch (EmptyResultDataAccessException ignored) {//NOSONAR
            return null;
        }
    }

}