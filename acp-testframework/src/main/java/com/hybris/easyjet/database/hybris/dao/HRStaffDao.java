package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.hybris.easyjet.database.hybris.models.HRStaffModel;
import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * Created by dwebb on 12/2/2016.
 * provides readonly access to customer data in hybris
 */
@Repository
public class HRStaffDao {

    private NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public HRStaffDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * @return list of valid customers details
     */
    public List<HRStaffModel> returnValidHRStaffFromHRStaffTable() {

        return returnHRStaffMember(null);
    }

    public List<HRStaffModel> returnUnassociatedHRStaffMember() {

        return returnHRStaffMember(false);
    }

    public List<HRStaffModel> returnAssociatedHRStaffMember() {

        return returnHRStaffMember(true);
    }

    private List<HRStaffModel> returnHRStaffMember(Boolean associated) {

        String query =
                "SELECT DISTINCT hr.hjmpTS\n" +
                        ",hr.createdTS\n" +
                        ",hr.modifiedTS\n" +
                        ",hr.TypePkString\n" +
                        ",hr.OwnerPkString\n" +
                        ",hr.PK\n" +
                        ",hr.p_employeeid\n" +
                        ",hr.p_email\n" +
                        ",hr.p_active\n" +
                        ",hr.p_worktype\n" +
                        ",hr.aCLTS\n" +
                        ",hr.propTS\n" +
                        "FROM hrstaff AS hr";

        if (associated != null) {
            query += "\nLEFT JOIN users AS u ON u.p_hrstaff = hr.pk\n";
            if (associated) {
                query += "WHERE u.p_uid IS NOT NULL;";
            } else {
                query += "WHERE u.p_uid IS NULL;";
            }
        } else {
            query += ";";
        }

        return this.jdbcTemplate.query(QueryParser.parse(query), (rs, rowNum) -> new HRStaffModel(
                rs.getString("hjmpTS"),
                rs.getString("createdTS"),
                rs.getString("modifiedTS"),
                rs.getString("TypePkString"),
                rs.getString("OwnerPkString"),
                rs.getString("PK"),
                rs.getString("p_employeeid"),
                rs.getString("p_email"),
                rs.getString("p_active"),
                rs.getString("p_worktype"),
                rs.getString("aCLTS"),
                rs.getString("propTS")
        ));
    }
}