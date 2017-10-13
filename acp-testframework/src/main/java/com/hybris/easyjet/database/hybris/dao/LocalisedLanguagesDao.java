package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.jolbox.bonecp.BoneCPDataSource;
import org.apache.commons.lang.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by daniel on 23/11/2016.
 * provides readonly access to localised language reference data in hybris
 */
@Repository
public class LocalisedLanguagesDao {

    private NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    @Qualifier("hybrisDataSource")
    public void LocalisedLanguagesDao(BoneCPDataSource dataSource) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * @return list of iso codes for languages
     */
    public List<String> get() {

        String query =
                "SELECT\n" +
                        "p_isocode\n" +
                        "FROM [dbo].[basestore2languagerel]\n" +
                        "INNER JOIN languages ON languages.PK = basestore2languagerel.TargetPK;";

        return this.jdbcTemplate.query(QueryParser.parse(query), (rs, rowNum) -> rs.getString("p_isocode"));
    }

    /**
     * @param language the desired language to search for
     * @return a list of Locales for the desired language
     */
    public List<String> getLocales(String language) {

        List<String> isoCodes = get();
        List<String> locales = new ArrayList<>();
        for (String isoCode : isoCodes) {

            if (language == null) {
                locales.add(LocaleUtils.toLocale(isoCode).toString().replace("_", "-"));
            } else {
                if (isoCode.equals(language)) {
                    locales.add(LocaleUtils.toLocale(isoCode).toString().replace("_", "-"));
                }
            }
        }
        return locales;
    }
}