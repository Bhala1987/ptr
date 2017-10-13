package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.TenantBeanFactoryPostProcessor;
import com.hybris.easyjet.database.QueryParser;
import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BulkTransferReasonDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public BulkTransferReasonDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public static BulkTransferReasonDao getBulkTransferReasonDaoFromSpring() {
        return (BulkTransferReasonDao) TenantBeanFactoryPostProcessor.getFactory().getBean("bulkTransferReasonDao");
    }

    public int getCountOfAllLanguages() {
        String query =
                "SELECT count(*) AS count\n" +
                        "   FROM languages;";

        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), Integer.class);
    }
}