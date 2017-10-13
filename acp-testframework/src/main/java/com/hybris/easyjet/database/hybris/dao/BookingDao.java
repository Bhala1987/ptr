package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.TenantBeanFactoryPostProcessor;
import com.hybris.easyjet.config.constants.DBConstants;
import com.hybris.easyjet.database.QueryParser;
import com.hybris.easyjet.database.hybris.models.PassengerStatus;
import com.jolbox.bonecp.BoneCPDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created by daniel on 23/11/2016.
 * provides read access to booking data in Hybris
 */
@Repository
public class BookingDao {

    public static final String PASSENGER_ID = "passengerId";
    public static final String FARE_TYPE = "fareType";
    private static final String BOOKING_ID = "bookingId";
    private static final Random rand = new Random(System.currentTimeMillis());
    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public BookingDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public static BookingDao getTestDataFromSpring() {
        return (BookingDao) TenantBeanFactoryPostProcessor.getFactory().getBean("bookingDao");
    }

    private static <T> T getRandomItem(List<T> list) {

        try {
            return list.get(rand.nextInt(list.size()));
        } catch (Exception ex) {
            return null;
        }
    }

    public String getBookingOriginalCurrency(String bookingReference) {
        SqlParameterSource params = new MapSqlParameterSource("bookingReference", bookingReference);

        String query =
                "SELECT c.p_isocode AS originalCurrency\n" +
                        "FROM orders AS o\n" +
                        "   INNER JOIN currencies AS c ON c.pk = o.p_originalcurrency\n" +
                        "WHERE o.p_bookingreference = :bookingReference";

        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, (rs, rn) -> rs.getString("originalCurrency"));
    }

    public List<String> getMarginValueFromOrderEntry (String bookingReference)  {

        SqlParameterSource params = new MapSqlParameterSource("bookingReference", bookingReference);

        String query =
                "SELECT oe.p_marginpercentage AS marginPercentage\n" +
                        "FROM orders AS o\n" +
                        "INNER JOIN orderentries AS oe ON o.PK=oe.p_order\n" +
                        "WHERE o.p_bookingreference = :bookingReference";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> rs.getString("marginPercentage"));
    }

    public String getBookingVersionId(String bookingReference) {
        SqlParameterSource params = new MapSqlParameterSource("bookingReference", bookingReference);

        String query =
                "SELECT o.p_versionid AS versionId\n" +
                        "FROM orders AS o\n" +
                        "WHERE o.p_bookingreference = :bookingReference\n" +
                        "AND o.p_versionid IS NOT NULL";

        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, (rs, rn) -> rs.getString("versionId"));

    }

    /**
     * @return a random booking with any status
     */
    public String getRandomBooking() {
        return getRandomItem(getBookings());
    }

    /**
     * @return list of all existing booking references
     */
    public List<String> getBookings() {

        String query =
                "SELECT TOP (300)\n" +
                        "[p_bookingreference]\n" +
                        "FROM [dbo].[orders]\n" +
                        "INNER JOIN orderentries oe ON oe.p_order = orders.PK\n" +
                        "INNER JOIN warehouses w ON w.p_code = oe.p_flightcode\n" +
                        "WHERE orders.createdTS < NOW() \n" +
                        "ORDER BY w.p_departuretime ASC;";

        return this.jdbcTemplate.query(QueryParser.parse(query), (rs, rowNum) -> rs.getString("p_bookingreference"));

    }

    public List<String> getParamNotAvailableForChannel(String channel) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("code", channel).addValue("p_propertyname", "requestParamNotAvailableForChannel");

        try {
            String bookingQuery =
                    "SELECT propvalueconfig.p_propertyvalue\n" +
                            "        FROM ej_core.propvalueconfig INNER JOIN enumerationvalues ON propvalueconfig.p_channel = enumerationvalues.PK\n" +
                            "        WHERE enumerationvalues.Code = :type\n" +
                            "        AND propvalueconfig.p_propertyname = :p_propertyname;";
            return Arrays.asList(this.jdbcTemplate.query(QueryParser.parse(bookingQuery), namedParameters, (rs, rowNum) -> rs.getString("p_propertyvalue")).get(0).split(","));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public PassengerStatus getBookingPassengerStatus(String bookingId, String passengerId) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue(BOOKING_ID, bookingId)
                .addValue(PASSENGER_ID, passengerId)
                .addValue(FARE_TYPE, DBConstants.FARE_TYPE);

        String query =
                "SELECT\n" +
                        "evapi.Code AS icts\n" +
                        ", evt.Code AS apis\n" +
                        ", evco.Code AS consignment\n" +
                        "FROM orders AS o \n" +
                        "   INNER JOIN orderentries AS oe ON oe.p_order = o.PK \n" +
                        "       AND oe.p_product IN ( \n" +
                        "           SELECT PK \n" +
                        "           FROM products \n" +
                        "           WHERE p_code IN (:fareType)\n" +
                        "       )\n" +
                        "   INNER JOIN travelorderentryinfo AS toei ON toei.PK = oe.p_travelorderentryinfo\n" +
                        "   INNER JOIN orderentrytraveller AS oet ON oet.SourcePK = toei.PK\n" +
                        "   INNER JOIN traveller AS t ON t.PK = oet.TargetPK\n" +
                        "   LEFT JOIN enumerationvalues AS evt ON evt.PK = t.p_apisstatus\n" +
                        "   LEFT JOIN advancepassengerinfo AS api ON api.p_traveller = t.pk\n" +
                        "   LEFT JOIN enumerationvalues AS evapi ON evapi.PK = api.p_ictsstatus\n" +
                        "   INNER JOIN consignments AS co ON co.p_traveller = t.pk\n" +
                        "   LEFT JOIN enumerationvalues AS evco ON evco.PK = co.p_status\n" +
                        "WHERE o.p_bookingreference = :bookingId\n" +
                        "   AND toei.p_code = :passengerId;";

        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, (rs, rn) -> new PassengerStatus(
                rs.getString("apis"),
                rs.getString("icts"),
                rs.getString("consignment")
        ));
    }

    public List<HashMap<String, String>> getSearchBookingDetail() {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("bookingStatus",
                        Arrays.asList("COMPLETED", "PENDING_CANCELLATION", "CANCELLED_CUSTOMER", "CANCELLED_REVENUE_PROTECTION", "PART_REVENUE_PROTECTION", "CHARGEBACK_POLICY_REVENUE_PROTECTION", "CHARGEBACK_FRAUD_REVENUE_PROTECTION", "PAST", "CONSIGNMENT_CREATED"))
                .addValue(FARE_TYPE, DBConstants.FARE_TYPE);

        String query =
                "SELECT\n" +
                        "	o.bookingReference\n" +
                        "	, o.bookingDate\n" +
                        "	, sev.bookingStatus\n" +
                        "	, bt.bookingType\n" +
                        "	, cev.channel\n" +
                        "	, dc.ipAddress\n" +
                        "	, c.currencyCode\n" +
                        "	, c.currencyDigits\n" +
                        "	, fi.flightNumber\n" +
                        "	, fi.originAirport\n" +
                        "	, fi.destinationAirport\n" +
                        "	, td.travelFromDate\n" +
                        "	, td.travelToDate\n" +
                        "	, pt.transactionDate\n" +
                        "	, pt.paymentAmount\n" +
                        "   , o.p_deliveryaddress\n" +
                        "	, u.customerTitle\n" +
                        "	, u.customerFirstName\n" +
                        "	, u.customerLastName\n" +
                        "	, u.customerEmail\n" +
                        "	, u.customerPostcode\n" +
                        "	, u.customerContactNumber\n" +
                        "	, p.passengerTitle\n" +
                        "	, p.passengerFirstName\n" +
                        "	, p.passengerLastName\n" +
                        "	, p.passengerContactNumber\n" +
                        "	, p.passengerEmail\n" +
                        "	, p.passengerDob\n" +
                        "	, p.advancePassengerFirstName\n" +
                        "	, p.advancePassengerLastName\n" +
                        "	, p.advancePassengerDob\n" +
                        "	, p.passengerEjPlusNumber\n" +
                        "	, p.passengerTravelDocumentType\n" +
                        "	, p.passengerTravelDocumentNumber\n" +
                        "	, p.passengerSsr\n" +
                        "	, p.passengerSequenceNumber\n" +
                        "FROM (\n" +
                        "		SELECT pk\n" +
                        "			, p_bookingreference AS bookingReference\n" +
                        "			, createdTS AS bookingDate\n" +
                        "			, p_status\n" +
                        "			, p_bookingtype\n" +
                        "			, p_salesapplication\n" +
                        "			, p_devicecontext\n" +
                        "			, p_currency\n" +
                        "			, p_deliveryaddress\n" +
                        "			, p_versionid\n" +
                        "		FROM orders\n" +
                        "	) AS o\n" +
                        //  Booking status
                        "	INNER JOIN (\n" +
                        "		SELECT pk\n" +
                        "			, Code AS bookingStatus\n" +
                        "		FROM enumerationvalues\n" +
                        "		WHERE Code IN (:bookingStatus)\n" +
                        "	) AS sev ON sev.pk = o.p_status\n" +
                        //  Booking type
                        "	INNER JOIN (\n" +
                        "		SELECT pk\n" +
                        "			, p_code AS bookingType\n" +
                        "		FROM ejbookingtype\n" +
                        "	) AS bt ON bt.pk = o.p_bookingtype\n" +
                        //  Channel
                        "	INNER JOIN (\n" +
                        "		SELECT pk\n" +
                        "			, Code AS channel\n" +
                        "		FROM enumerationvalues\n" +
                        "	) AS cev ON cev.pk = o.p_salesapplication\n" +
                        //  IP address
                        "	INNER JOIN (\n" +
                        "		SELECT pk\n" +
                        "			, p_ipaddress AS ipAddress\n" +
                        "		FROM devicecontext\n" +
                        "	) AS dc ON dc.pk = o.p_devicecontext\n" +
                        //  Currency info
                        "	INNER JOIN (\n" +
                        "		SELECT pk\n" +
                        "			, p_isocode AS currencyCode\n" +
                        "			, p_digits AS currencyDigits\n" +
                        "		FROM currencies\n" +
                        "	) AS c ON c.pk = o.p_currency\n" +
                        //  Flight info: origin airports, destination airports
                        "	INNER JOIN (\n" +
                        "		SELECT oe.p_order\n" +
                        "			, w.p_code\n" +
                        "			, w.p_number AS flightNumber\n" +
                        "			, ts.*\n" +
                        "		FROM warehouses AS w\n" +
                        "			INNER JOIN (\n" +
                        "				SELECT pk AS tsPK\n" +
                        "					, tfoa.p_code AS originAirport\n" +
                        "					, tfda.p_code AS destinationAirport\n" +
                        "				FROM travelsector AS ts\n" +
                        "			INNER JOIN (\n" +
                        "				SELECT pk AS tfPK\n" +
                        "					, p_code\n" +
                        "				FROM transportfacility\n" +
                        "			) AS tfoa ON tfoa.tfPK = ts.p_origin\n" +
                        "			INNER JOIN (\n" +
                        "				SELECT pk AS tfPK\n" +
                        "					, p_code\n" +
                        "				FROM transportfacility\n" +
                        "			) AS tfda ON tfda.tfPK = ts.p_destination\n" +
                        "		) AS ts ON ts.tsPK = w.p_travelsector\n" +
                        "		INNER JOIN (\n" +
                        "			SELECT p_order\n" +
                        "				, p_flightcode\n" +
                        "			FROM orderentries\n" +
                        "			GROUP BY p_order, p_flightcode\n" +
                        "		) AS oe ON oe.p_flightcode = w.p_code\n" +
                        "	) AS fi ON fi.p_order = o.pk\n" +
                        //  Travel dates
                        "	INNER JOIN (\n" +
                        "		SELECT oe.p_order\n" +
                        "			, MIN(w.p_departuretime) AS travelFromDate\n" +
                        "			, MAX(w.p_arrivaltime) AS travelToDate\n" +
                        "		FROM orderentries AS oe\n" +
                        "			INNER JOIN (\n" +
                        "				SELECT p_code\n" +
                        "					, p_departuretime\n" +
                        "					, p_arrivaltime\n" +
                        "				FROM warehouses\n" +
                        "			) AS w ON w.p_code = oe.p_flightcode\n" +
                        "		GROUP BY oe.p_order\n" +
                        "	) AS td ON o.pk = td.p_order\n" +
                        //  Payment transaction
                        "	INNER JOIN (\n" +
                        "		SELECT p_order\n" +
                        "			, createdTS AS transactionDate\n" +
                        "			, SUM(pte.paymentAmount) AS paymentAmount\n" +
                        "		FROM paymenttransactions AS pt\n" +
                        "			INNER JOIN (\n" +
                        "				SELECT p_paymenttransaction\n" +
                        "					,SUM(p_amount) AS paymentAmount\n" +
                        "				FROM paymnttrnsctentries\n" +
                        "				WHERE p_transactionstatus = 'SENTFORSETTLEMENT'\n" +
                        "				GROUP BY p_paymenttransaction\n" +
                        "			) AS pte ON pte.p_paymenttransaction = pt.pk\n" +
                        "		GROUP BY p_order, createdTS\n" +
                        "	) AS pt ON pt.p_order = o.pk\n" +
                        //  Customer details
                        "	LEFT JOIN (\n" +
                        "		SELECT\n" +
                        "			a.pk\n" +
                        "			, t.p_code AS customerTitle\n" +
                        "			, p_firstname AS customerFirstName\n" +
                        "			, p_lastname AS customerLastName\n" +
                        "			, p_email AS customerEmail\n" +
                        "			, p_postalcode AS customerPostcode\n" +
                        "			, p_phone1 AS customerContactNumber\n" +
                        "		FROM addresses AS a\n" +
                        "			INNER JOIN (\n" +
                        "				SELECT pk\n" +
                        "					, p_code\n" +
                        "				FROM titles\n" +
                        "			) AS t ON t.pk = p_title\n" +
                        "	) AS u ON u.pk = o.p_deliveryaddress\n" +
                        //  Passenger details with Sequence number
                        "	INNER JOIN (\n" +
                        "		SELECT ti.p_order\n" +
                        "			, pi.*\n" +
                        "			, api.*\n" +
                        "			, p_ejmembership AS passengerEjPlusNumber\n" +
                        "			, ssrifp.passengerSsr\n" +
                        "			, cs.passengerSequenceNumber\n" +
                        "		FROM (\n" +
                        "			SELECT pk\n" +
                        "				, p_info\n" +
                        "				, p_ejmembership\n" +
                        "				, p_specialrequestdetail\n" +
                        "			FROM traveller\n" +
                        "		) AS t\n" +
                        "		INNER JOIN (\n" +
                        "			SELECT oe.p_order\n" +
                        "				, SourcePK\n" +
                        "				, TargetPK\n" +
                        "			FROM orderentrytraveller AS oet\n" +
                        "			INNER JOIN (\n" +
                        "				SELECT p_order\n" +
                        "					, p_travelorderentryinfo\n" +
                        "				FROM orderentries\n" +
                        "					INNER JOIN (\n" +
                        "						SELECT pk\n" +
                        "						FROM products\n" +
                        "						WHERE p_code IN (:fareType)\n" +
                        "					) AS p ON p.pk = p_product\n" +
                        "			) AS oe ON oe.p_travelorderentryinfo = oet.SourcePK\n" +
                        "		) AS ti ON t.pk = ti.TargetPK\n" +
                        "		INNER JOIN (\n" +
                        "			SELECT pi.pk\n" +
                        "				, t.p_code AS passengerTitle\n" +
                        "				, pi.p_firstname AS passengerFirstName\n" +
                        "				, pi.p_surname AS passengerLastName\n" +
                        "				, pi.p_phonenumber AS passengerContactNumber\n" +
                        "				, pi.p_emailaddress AS passengerEmail\n" +
                        "				, pi.p_dateofbirth AS passengerDob\n" +
                        "			FROM passengerinformation AS pi\n" +
                        "			INNER JOIN (\n" +
                        "				SELECT pk\n" +
                        "					, p_code\n" +
                        "				FROM titles\n" +
                        "			) AS t ON t.pk = pi.p_title\n" +
                        "		) AS pi ON pi.pk = t.p_info\n" +
                        "		LEFT JOIN (\n" +
                        "			SELECT p_traveller\n" +
                        "				, p_firstname AS advancePassengerFirstName\n" +
                        "				, p_lastname AS advancePassengerLastName\n" +
                        "				, p_dateofbirth AS advancePassengerDob\n" +
                        "				, tdt.p_code AS passengerTravelDocumentType\n" +
                        "				, p_documentnumber AS passengerTravelDocumentNumber\n" +
                        "			FROM advancepassengerinfo\n" +
                        "			INNER JOIN (\n" +
                        "				SELECT pk\n" +
                        "					, p_code\n" +
                        "				FROM traveldocumenttype\n" +
                        "			) AS tdt ON tdt.pk = p_apistype\n" +
                        "		) AS api ON api.p_traveller = t.pk\n" +
                        "		LEFT JOIN (\n" +
                        "			SELECT p_specialrequestdetail\n" +
                        "				, p_ssrcode AS passengerSsr\n" +
                        "			FROM ssrinstanceforpassenger\n" +
                        "		) AS ssrifp ON ssrifp.p_specialrequestdetail = t.p_specialrequestdetail\n" +
                        "		INNER JOIN (\n" +
                        "			SELECT p_traveller\n" +
                        "				, p_sequencenumber AS passengerSequenceNumber\n" +
                        "			FROM consignments\n" +
                        "		) AS cs ON cs.p_traveller = t.pk\n" +
                        "	) AS p ON p.p_order = o.pk\n" +
                        "WHERE o.p_versionid IS NULL;";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> {
            HashMap<String, String> result = new HashMap<>();
            result.put("bookingReference", rs.getString("bookingReference"));
            result.put("bookingDate", rs.getString("bookingDate").split(" ")[0]);
            result.put("bookingStatus", rs.getString("bookingStatus"));
            result.put("bookingType", rs.getString("bookingType"));
            result.put("channel", rs.getString("channel"));
            result.put("ipAddress", rs.getString("ipAddress"));
            result.put("currencyIsoCode", rs.getString("currencyCode"));
            result.put("flightNumber", rs.getString("flightNumber"));
            result.put("originAirport", rs.getString("originAirport"));
            result.put("destinationAirport", rs.getString("destinationAirport"));
            result.put("travelFromDate", rs.getString("travelFromDate").split(" ")[0]);
            result.put("travelToDate", rs.getString("travelToDate").split(" ")[0]);
            result.put("transactionDate", rs.getString("transactionDate").split(" ")[0]);
            result.put("paymentAmount",
                    new BigDecimal(rs.getString("paymentAmount"))
                            .setScale(Integer.parseInt(rs.getString("currencyDigits")), RoundingMode.HALF_UP)
                            .toString());
            // Customer details
            if (StringUtils.isNotBlank(rs.getString("p_deliveryaddress"))) {
                result.put("customerTitle", rs.getString("customerTitle"));
                result.put("customerFirstName", rs.getString("customerFirstName"));
                result.put("customerLastName", rs.getString("customerLastName"));
                result.put("customerEmail", rs.getString("customerEmail"));
                result.put("postcode", rs.getString("customerPostcode"));
                result.put("customerContactNumber", rs.getString("customerContactNumber"));
            } else {
                result.put("customerTitle", StringUtils.EMPTY);
                result.put("customerFirstName", StringUtils.EMPTY);
                result.put("customerLastName", StringUtils.EMPTY);
                result.put("customerEmail", StringUtils.EMPTY);
                result.put("postcode", StringUtils.EMPTY);
                result.put("customerContactNumber", StringUtils.EMPTY);
            }
            // Passenger details
            result.put("passengerTitle", rs.getString("passengerTitle"));
            if (StringUtils.isNotBlank(rs.getString("advancePassengerFirstName"))) {
                result.put("passengerFirstName", rs.getString("advancePassengerFirstName"));
            } else {
                result.put("passengerFirstName", rs.getString("passengerFirstName"));
            }
            if (StringUtils.isNotBlank(rs.getString("advancePassengerLastName"))) {
                result.put("passengerLastName", rs.getString("advancePassengerLastName"));
            } else {
                result.put("passengerLastName", rs.getString("passengerLastName"));
            }
            result.put("passengerContactNumber", rs.getString("passengerContactNumber"));
            result.put("passengerEmail", rs.getString("passengerEmail"));
            if (StringUtils.isNotBlank(rs.getString("passengerDob"))) {
                result.put("dob", rs.getString("passengerDob").split(" ")[0]);
            } else if (StringUtils.isNotBlank(rs.getString("advancePassengerDob"))) {
                result.put("dob", rs.getString("advancePassengerDob").split(" ")[0]);
            } else {
                result.put("dob", null);
            }
            result.put("travelDocumentType", rs.getString("passengerTravelDocumentType"));
            result.put("travelDocumentNumber", rs.getString("passengerTravelDocumentNumber"));
            result.put("ejPlusNumber", rs.getString("passengerEjPlusNumber"));
            result.put("ssrCode", rs.getString("passengerSsr"));
            result.put("sequenceNumber", rs.getString("passengerSequenceNumber"));
            return result;
        });
    }

}
