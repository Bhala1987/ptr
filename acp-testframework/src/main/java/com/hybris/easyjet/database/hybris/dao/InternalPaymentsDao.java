package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.QueryParser;
import com.hybris.easyjet.database.hybris.models.InternalPaymentModel;
import com.jolbox.bonecp.BoneCPDataSource;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * Created by markphipps on 21/04/2017.
 */
@Repository
@ToString
public class InternalPaymentsDao {
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    private SerenityFacade testData;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public InternalPaymentsDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<InternalPaymentModel> getAllCreditFileFunds() {
        String query =
                "SELECT * \n" +
                        "FROM creditfilefund;";

        return this.jdbcTemplate.query(QueryParser.parse(query), (rs, rowNum) -> new InternalPaymentModel(
                rs.getString("p_code"),
                rs.getString("p_currency"),
                rs.getFloat("p_startBalance"),
                rs.getFloat("p_currentBalance"),
                rs.getString("p_startdate"),
                rs.getString("p_expirydate")));
    }

    public List<InternalPaymentModel> getActiveCreditFiles() {

        String query =
                "SELECT *  FROM creditfilefund item_t0 \n" +
                        "JOIN cffchannelrelation item_t1 ON  item_t0.PK = item_t1.SourcePK  \n" +
                        "JOIN enumerationvalues item_t2 ON  item_t2.PK = item_t1.TargetPK  \n" +
                        "JOIN cffusergrouprelation item_t5 ON  item_t5.SourcePK  =  item_t0.PK  \n" +
                        "WHERE ( item_t2.Code ='"+testData.getChannel()+"' \n" +
                        "AND item_t0.p_startdate <= NOW() \n" +
                        "AND item_t0.p_expirydate >= NOW() \n" +
                        "AND item_t5.TargetPK in (SELECT  PK  FROM usergroups WHERE p_uid  = 'customergroup'));";
        
//        String query = "SELECT distinct item_t0.PK  FROM creditfilefund item_t0 \n" +
//        String query = "SELECT * FROM creditfilefund WHERE creditfilefund.p_startdate <= NOW() AND creditfilefund.p_expirydate >= NOW();";

        return this.jdbcTemplate.query(QueryParser.parse(query), (rs, rowNum) -> new InternalPaymentModel(
                rs.getString("p_code"),
                rs.getString("p_currency"),
                rs.getDouble("p_startBalance"),
                rs.getDouble("p_currentBalance"),
                rs.getString("p_startdate"),
                rs.getString("p_expirydate")));
    }

}
