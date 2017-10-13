package com.hybris.easyjet;

import com.hybris.easyjet.config.EasyjetCommonConfig;
import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.*;

/**
 * Created by daniel on 23/11/2016.
 */
@Configuration
@ComponentScan
@PropertySources({
        @PropertySource("classpath:environments/${environment}.properties"),
        @PropertySource("classpath:environments/common.properties")
})

public class TestApplication {

    @Autowired
    private EasyjetCommonConfig config;


    /**
     * autowires the datasource needed to connect to the hybris database, the specific details of configuration are
     * taken from the spring managed EasyjetHybrisConfig class
     *
     * @return a bean of DriverManagerDataSource with the correct configuration to connect to hybris
     */
    @Bean(name = "hybrisDataSource")
    public BoneCPDataSource hybrisDataSource() {
        addMicrosoftJdbcDriverToClasspath();

        BoneCPDataSource dataSource = new BoneCPDataSource();
        dataSource.setDriverClass(config.getHybrisDbDriver());
        dataSource.setJdbcUrl(config.getHybrisDbConnectionUrl());
        dataSource.setUsername(config.getHybrisDbUserName());
        dataSource.setPassword(config.getHybrisDbPassword());
        return dataSource;
    }

    /**
     * autowires the datasource needed to connect to the eres database, the specific details of configuration are taken
     * taken from the spring managed EasyjetHybrisConfig class
     *
     * @return a bean of DriverManagerDataSource with the correct configuration to connect to eres
     */
    @Bean(name = "eresDataSource")
    public BoneCPDataSource eResDataSource() {
        addMicrosoftJdbcDriverToClasspath();
        BoneCPDataSource dataSource = new BoneCPDataSource();
        dataSource.setDriverClass("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSource.setJdbcUrl(config.getEResDbConnectionUrl());
        dataSource.setUsername(config.getEResDbUserName());
        dataSource.setPassword(config.getEResDbPassword());
        return dataSource;
    }

    /**
     * autowires the datasource needed to connect to the Seating database in Hybris Stable environment, the specific details of configuration are taken
     * taken from the spring managed EasyjetHybrisConfig class
     *
     * @return a bean of DriverManagerDataSource with the correct configuration to connect to Seating database in Hybris Stable environment
     */
    @Bean(name = "seatingDataSource")
    public BoneCPDataSource seatingDataSource() {
        addMicrosoftJdbcDriverToClasspath();
        BoneCPDataSource dataSource = new BoneCPDataSource();
        dataSource.setDriverClass("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSource.setJdbcUrl(config.getSeatingDbConnectionUrl());
        dataSource.setUsername(config.getSeatingDbUserName());
        dataSource.setPassword(config.getSeatingDbPassword());
        return dataSource;
    }

    private void addMicrosoftJdbcDriverToClasspath() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Bean
    public static BeanFactoryPostProcessor beanFactoryPostProcessor() {
        return new TenantBeanFactoryPostProcessor();
    }

}
