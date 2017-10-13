package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.hybris.easyjet.database.hybris.models.MemberShipModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Random;

import static com.hybris.easyjet.fixture.hybris.helpers.SavedPassengerHelper.COMPLETED;

/**
 * Created by Giuseppe Cioce on 09/01/2017.
 */
@Repository
public class MembershipDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private static final Random rand = new Random(System.currentTimeMillis());

    @Autowired
    public MembershipDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    private List<MemberShipModel> getEJPlusMembershipOtherThanStatus(String ejPlusmembershipStatus) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("status", ejPlusmembershipStatus);
        String query =
                "SELECT\n" +
                        "p_firstname\n" +
                        ",p_lastname\n" +
                        ",p_expirydate\n" +
                        ",p_number\n" +
                        ",Code\n" +
                        "FROM membershipvalidation AS m\n" +
                        "INNER JOIN enumerationvalues AS ev ON ev.PK=m.p_status\n" +
                        "WHERE Code != :status AND p_expirydate > GETDATE()\n" +
                        "AND p_number NOT LIKE 'S%';";

        return this.jdbcTemplate.query(QueryParser.parse(query), namedParameters, (rs, rowNum) -> MemberShipModel.builder()
                .ejMemberShipNumber(rs.getString("p_number"))
                .expiryDate(rs.getString("p_expirydate"))
                .firstname(rs.getString("p_firstname"))
                .lastname(rs.getString("p_lastname"))
                .status(rs.getString("Code"))
                .build());
    }

    private List<MemberShipModel> getEJPlusMembershipWithStatus(String ejPlusmembershipStatus) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("status", ejPlusmembershipStatus);
        String query =
                "SELECT\n" +
                        "p_firstname\n" +
                        ",p_lastname\n" +
                        ",p_expirydate\n" +
                        ",p_number\n" +
                        ",Code\n" +
                        "FROM membershipvalidation AS m\n" +
                        "INNER JOIN enumerationvalues AS ev ON ev.PK=m.p_status\n" +
                        "WHERE Code = :status AND p_expirydate > GETDATE()\n" +
                        "AND p_number NOT LIKE 'S%';";

        return this.jdbcTemplate.query(QueryParser.parse(query), namedParameters, (rs, rowNum) -> MemberShipModel.builder()
                .ejMemberShipNumber(rs.getString("p_number"))
                .expiryDate(rs.getString("p_expirydate"))
                .firstname(rs.getString("p_firstname"))
                .lastname(rs.getString("p_lastname"))
                .status(rs.getString("Code"))
                .build());
    }

    private List<MemberShipModel> getExpiredEJPlusMembershipWithStatus(String ejPlusmembershipStatus) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("status", ejPlusmembershipStatus);
        String query =
                "SELECT\n" +
                        "p_firstname\n" +
                        ",p_lastname\n" +
                        ",p_expirydate\n" +
                        ",p_number\n" +
                        ",Code\n" +
                        "FROM membershipvalidation AS m\n" +
                        "INNER JOIN enumerationvalues AS ev ON ev.PK=m.p_status\n" +
                        "WHERE Code = :status AND p_expirydate < GETDATE()\n" +
                        "AND p_number NOT LIKE 'S%';";

        return this.jdbcTemplate.query(QueryParser.parse(query), namedParameters, (rs, rowNum) -> MemberShipModel.builder()
                .ejMemberShipNumber(rs.getString("p_number"))
                .expiryDate(rs.getString("p_expirydate"))
                .firstname(rs.getString("p_firstname"))
                .lastname(rs.getString("p_lastname"))
                .status(rs.getString("Code"))
                .build());
    }

    public List<MemberShipModel> getAllEJPlusMembershipsWithStatus(String ejPlusmembershipStatus) {
        return getEJPlusMembershipWithStatus(ejPlusmembershipStatus);
    }

    public MemberShipModel getEJPlusMemberOtherThanStatus(String status) throws EasyjetCompromisedException {
        return getEJMemberShipModel(getEJPlusMembershipOtherThanStatus(status));
    }

    public MemberShipModel getEJPlusMemberBasedOnStatus(String status) throws EasyjetCompromisedException {
        return getEJMemberShipModel(getEJPlusMembershipWithStatus(status));
    }

    public MemberShipModel getValidEJPlusMembershipForStaffOtherThanStatus(String status) throws EasyjetCompromisedException {
        return getEJMemberShipModel(getValidEJPlusMembershipStaffWithOtherThanStatus(status));
    }

    public MemberShipModel getValidEJPlusMembershipForStaffWithStatus(String status) {
        return getRandomItem(getValidEJPlusMembershipStaff(status));
    }

    public MemberShipModel getExpiredEJPlusMembership(String status) {
        return getRandomItem(getExpiredEJPlusMembershipWithStatus(status));
    }

    private static MemberShipModel getEJMemberShipModel(List<MemberShipModel> list) throws EasyjetCompromisedException {
        final MemberShipModel[] model = {null};
            list.stream().forEach(
                    item->
                    {
                        if(item.getEjMemberShipNumber().length() >= 8)
                        {
                            model[0] =item;
                            return;
                        }
                    }
            );
            if(model[0]==null)
                throw new EasyjetCompromisedException("There are no customers");
            else
            return model[0];
    }

    public MemberShipModel getRandomValueForValidEJPlus() {
        return getRandomItem(getAllEJPlusMembershipsWithStatus(COMPLETED));
    }

    private static <T> T getRandomItem(List<T> list) {

        try {
            return list.get(rand.nextInt(list.size()));
        } catch (Exception ex) {
            return null;
        }
    }


    private List<MemberShipModel> getValidEJPlusMembershipStaffWithOtherThanStatus(String status) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("status", status);
        String query =
                "SELECT\n" +
                        "p_firstname\n" +
                        ",p_lastname\n" +
                        ",p_expirydate\n" +
                        ",p_number\n" +
                        ",Code\n" +
                        "FROM membershipvalidation AS m\n" +
                        "INNER JOIN enumerationvalues AS ev ON ev.PK=m.p_status\n" +
                        "WHERE Code != :status AND p_expirydate > GETDATE()\n" +
                        "AND p_number LIKE 'S%'";
        return this.jdbcTemplate.query(QueryParser.parse(query), namedParameters, (rs, rowNum) -> MemberShipModel.builder()
                .ejMemberShipNumber(rs.getString("p_number"))
                .expiryDate(rs.getString("p_expirydate"))
                .firstname(rs.getString("p_firstname"))
                .lastname(rs.getString("p_lastname"))
                .status(rs.getString("Code"))
                .build());
    }

    private List<MemberShipModel> getValidEJPlusMembershipStaff(String ejPlusmembershipStatus) {

        SqlParameterSource namedParameters = new MapSqlParameterSource("status", ejPlusmembershipStatus);
        String query =
                "SELECT\n" +
                        "p_firstname\n" +
                        ",p_lastname\n" +
                        ",p_expirydate\n" +
                        ",p_number\n" +
                        ",Code\n" +
                        "FROM membershipvalidation AS m\n" +
                        "INNER JOIN enumerationvalues AS ev ON ev.PK=m.p_status\n" +
                        "WHERE Code = :status AND p_expirydate > GETDATE()\n" +
                        "AND p_number LIKE 'S%'";

        return this.jdbcTemplate.query(QueryParser.parse(query), namedParameters, (rs, rowNum) -> MemberShipModel.builder()
                .ejMemberShipNumber(rs.getString("p_number"))
                .expiryDate(rs.getString("p_expirydate"))
                .firstname(rs.getString("p_firstname"))
                .lastname(rs.getString("p_lastname"))
                .status(rs.getString("Code"))
                .build());
    }
}