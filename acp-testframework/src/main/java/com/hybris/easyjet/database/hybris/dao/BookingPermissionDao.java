package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.hybris.easyjet.database.hybris.models.BookingPermissionModel;
import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by daniel on 23/11/2016.
 * provides read access to booking data in Hybris
 */
@Repository
public class BookingPermissionDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public BookingPermissionDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<BookingPermissionModel> getBookingPermissions(String channel, String bookingType, String accessType) {
        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("channel", channel)
            .addValue("bookingType", bookingType)
            .addValue("accessType", accessType);

        String query =
                "SELECT\n" +
                "   category.Code AS category,\n" +
                "   capability.p_code AS capability,\n" +
                "   channel.Code AS channel,\n" +
                "   accesstype.Code AS accesstype,\n" +
                "   bookingtype.p_code AS bookingtype,\n" +
                "   booking_permission.p_permitted\n" +
                "FROM\n" +
                "   bookingpermission AS booking_permission\n" +
                "   JOIN enumerationvalues AS category ON category.PK = booking_permission.p_category\n" +
                "   JOIN functionalcapability AS capability ON capability.PK = booking_permission.p_capabilitycode\n" +
                "   JOIN enumerationvalues AS channel ON channel.PK = booking_permission.p_channel\n" +
                "   JOIN enumerationvalues AS accesstype ON accesstype.PK = booking_permission.p_accesstype\n" +
                "   LEFT JOIN ejbookingtype as bookingtype on bookingtype.PK = booking_permission.p_bookingtype\n" +
                "WHERE\n" +
                "   CHANNEL.Code = :channel AND\n" +
                "   accesstype.Code = :accessType AND\n" +
                "   booking_permission.p_permitted = 1 AND\n" +
                "   (bookingtype.PK = (SELECT PK from EJBookingType where p_code=:bookingType) \n" +
                "       OR bookingtype.PK IS NULL);";

        return this.jdbcTemplate.query(
            QueryParser.parse(query),
                params,
            (rs, rowNum) -> new BookingPermissionModel(
                    rs.getString("category"),
                    rs.getString("capability"),
                    rs.getString("channel"),
                    rs.getString("accessType"),
                    rs.getString("bookingType"),
                    rs.getBoolean("p_permitted")

            )
        );
    }
}