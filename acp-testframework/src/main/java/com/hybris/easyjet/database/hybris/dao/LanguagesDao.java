package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.TenantBeanFactoryPostProcessor;
import com.hybris.easyjet.database.QueryParser;
import com.hybris.easyjet.fixture.hybris.asserters.models.ExpectedLanguage;
import com.jolbox.bonecp.BoneCPDataSource;
import org.apache.commons.lang.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by daniel on 23/11/2016.
 * provides readonly access to languages reference data in hybris
 */
@Repository
public class LanguagesDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public LanguagesDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public static LanguagesDao getLanguagesDaoFromSpring() {
        return (LanguagesDao) TenantBeanFactoryPostProcessor.getFactory().getBean("languagesDao");
    }

    /**
     * @param active the desired active status of languages
     * @return a list of languages matching the desired active status
     */
    public List<ExpectedLanguage> getLanguages(boolean active) {

        SqlParameterSource params = new MapSqlParameterSource().addValue("p_active", active);
        String query =
                "SELECT\n" +
                        "[p_isocode]\n" +
                        ",[p_active]\n" +
                        "FROM [dbo].[languages]\n" +
                        "WHERE p_bcp47code IS NOT NULL\n" +
                        "AND p_active = :p_active;";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> ExpectedLanguage.builder()
                .code(rs.getString("p_isocode"))
                .isActive(rs.getBoolean("p_active"))
                .isoCode(getLocale(rs.getString("p_isocode")))
                .build());
    }

    private String getLocale(String isoCode) {

        return LocaleUtils.toLocale(isoCode).toString().replace("_", "-");
    }

    public List<String> getAvailableLanguageCodes() {
        SqlParameterSource params = new MapSqlParameterSource();

        String query =
                "SELECT\n" +
                        "l.p_isocode AS code\n" +
                        "FROM languages AS l\n" +
                        "WHERE l.p_bcp47code IS NOT NULL";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> rs.getString("code"));

    }
}




