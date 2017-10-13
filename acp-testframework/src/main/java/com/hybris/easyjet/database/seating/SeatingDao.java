package com.hybris.easyjet.database.seating;

import com.jolbox.bonecp.BoneCPDataSource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;

/**
 * This class provides the ability to call stored procedure in Seating DB
 */
@Repository
public class SeatingDao {
    private static final Logger LOG = LogManager.getLogger(SeatingDao.class);

    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource for connecting to the Seating Database
     */
    @Autowired
    public SeatingDao(@Qualifier("seatingDataSource") BoneCPDataSource dataSource) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }


    public void clearSeats() throws SQLException {
        String job = "Truncate inventory tables";
        LOG.info("================ RELEASING THE SEATS STARTED ================ ");
        jdbcTemplate.update("EXEC msdb.dbo.sp_start_job N'" + job + "'", new MapSqlParameterSource());
        LOG.info("================ RELEASING THE SEATS COMPLETED ================ ");
    }
}