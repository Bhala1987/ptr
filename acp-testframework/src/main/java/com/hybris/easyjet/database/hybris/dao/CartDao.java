package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.TenantBeanFactoryPostProcessor;
import com.hybris.easyjet.config.constants.DBConstants;
import com.hybris.easyjet.database.QueryParser;
import com.hybris.easyjet.database.hybris.models.BasketPassengerBasicInfoModel;
import com.hybris.easyjet.database.hybris.models.PassengerStatus;
import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Created by giuseppedimartino on 27/04/17.
 */
@Repository
public class CartDao {

    public static final String PASSENGER_ID = "passengerId";
    public static final String FARE_TYPE = "fareType";
    private static final String BASKET_ID = "basketId";
    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public CartDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public static CartDao getCartDaoFromSpring() {
        return (CartDao) TenantBeanFactoryPostProcessor.getFactory().getBean("cartDao");
    }

    public Map<String, String> getCartCurrencies(String basketId) {

        SqlParameterSource params = new MapSqlParameterSource(BASKET_ID, basketId);

        String query =
                "SELECT\n" +
                        "ca.p_isocode AS actual\n" +
                        ",co.p_isocode AS original\n" +
                        "FROM carts AS b\n" +
                        "	JOIN currencies AS ca ON ca.PK = b.p_currency\n" +
                        "	JOIN currencies AS co ON co.PK = b.p_originalcurrency\n" +
                        "WHERE b.p_code = :basketId;";

        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, (nrs, nrn) -> new HashMap<String, String>() {{
            put("actual", nrs.getString("actual"));
            put("original", nrs.getString("original"));
        }});
    }

    public int getNumberOfProductsInTheCart(String basketId, String flightKey) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue(BASKET_ID, basketId)
                .addValue("flightKey", flightKey);

        String query =
                "SELECT COUNT(*)\n" +
                        "FROM carts AS c\n" +
                        "   INNER JOIN cartentries AS ce ON ce.p_order = c.PK\n" +
                        "WHERE c.PK = :basketId\n" +
                        "   AND ce.p_flightcode = :flightKey;";

        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, Integer.class);
    }

    public List<String> getAssociatedPassenger(String basketId, String passenger) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue(BASKET_ID, basketId)
                .addValue("passenger", passenger)
                .addValue(FARE_TYPE, DBConstants.FARE_TYPE);

        String query =
                "SELECT t.p_sameperson AS passengers\n" +
                        "FROM carts AS c\n" +
                        "   INNER JOIN cartentries AS ce ON ce.p_order = c.PK\n" +
                        "       AND ce.p_product IN (\n" +
                        "           SELECT PK\n" +
                        "           FROM products\n" +
                        "           WHERE p_code IN (:fareType)\n" +
                        "       )\n" +
                        "   INNER JOIN travelorderentryinfo AS toei ON toei.PK = ce.p_travelorderentryinfo\n" +
                        "   INNER JOIN orderentrytraveller AS oet ON oet.SourcePK = toei.PK\n" +
                        "   INNER JOIN traveller AS t ON t.PK = oet.TargetPK\n" +
                        "WHERE c.p_code = :basketId\n" +
                        "   AND toei.p_code = :passenger;";

        String linkedPassengersList = this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, (rs, numRow) -> rs.getString("passengers"));

        if (linkedPassengersList == null)
            return new ArrayList<>();

        List<String> linkedPassengers = Arrays.asList(linkedPassengersList.substring(4).split(","));

        params = new MapSqlParameterSource("passengers", linkedPassengers);
        query =
                "SELECT toei.p_code AS passenger\n" +
                        "FROM traveller AS t\n" +
                        "   INNER JOIN orderentrytraveller AS oet ON oet.TargetPK = t.PK\n" +
                        "   INNER JOIN travelorderentryinfo AS toei ON toei.PK = oet.SourcePK\n" +
                        "WHERE t.PK IN (:passengers);";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, numRow) -> rs.getString("passenger"));
    }

    public String getOriginalPassenger(String basketId, String replacedPassenger) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue(BASKET_ID, basketId)
                .addValue("passenger", replacedPassenger)
                .addValue(FARE_TYPE, DBConstants.FARE_TYPE);

        String query =
                "SELECT toei.p_code AS passenger\n" +
                        "FROM travelorderentryinfo AS toei\n" +
                        "   INNER JOIN cartentries AS ce ON ce.p_travelorderentryinfo = toei.PK\n" +
                        "WHERE ce.PK = (\n" +
                        "   SELECT ce.p_originalentry AS originalEntry\n" +
                        "   FROM carts AS c\n" +
                        "      INNER JOIN cartentries AS ce ON ce.p_order = c.PK\n" +
                        "          AND ce.p_product IN (\n" +
                        "              SELECT PK\n" +
                        "              FROM products\n" +
                        "              WHERE p_code IN (:fareType)\n" +
                        "          )\n" +
                        "      INNER JOIN travelorderentryinfo AS toei ON toei.PK = ce.p_travelorderentryinfo\n" +
                        "   WHERE c.p_code = :basketId\n" +
                        "      AND toei.p_code = :passenger\n" +
                        ");";

        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, (rs, numRow) -> rs.getString("passenger"));
    }

    public List<Boolean> getCartEntriesStatusForPassenger(String basketId, String passenger) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue(BASKET_ID, basketId)
                .addValue("passenger", passenger)
                .addValue(FARE_TYPE, DBConstants.FARE_TYPE);

        String query =
                "SELECT ce.p_active AS status\n" +
                        "FROM carts AS c\n" +
                        "   INNER JOIN cartentries AS ce ON ce.p_order = c.PK\n" +
                        "   INNER JOIN travelorderentryinfo AS toei ON toei.PK = ce.p_travelorderentryinfo\n" +
                        "WHERE c.p_code = :basketId\n" +
                        "   AND toei.p_code = :passenger;";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, numRow) -> rs.getBoolean("status"));
    }

    public List<String> getAssociatedFlight(String basketId, String flightKey) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("basketId", basketId)
                .addValue("flightKey", flightKey)
                .addValue(FARE_TYPE, DBConstants.FARE_TYPE);

        String query =
                "SELECT ce.p_fareproductsiblings AS flights\n" +
                        "FROM carts AS c\n" +
                        "   INNER JOIN cartentries AS ce ON ce.p_order = c.PK\n" +
                        "       AND ce.p_product IN (\n" +
                        "           SELECT PK\n" +
                        "           FROM products\n" +
                        "           WHERE p_code IN (:fareType)\n" +
                        "       )\n" +
                        "WHERE c.p_code = :basketId\n" +
                        "   AND ce.p_flightcode = :flightKey;";

        String linkedFlightsList = this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, String.class);

        if (linkedFlightsList == null)
            return new ArrayList<>();

        String[] linkedFlights = linkedFlightsList.substring(4).split(",");

        params = new MapSqlParameterSource("flights", linkedFlights);

        query =
                "SELECT ce.p_flightcode AS flight\n" +
                        "FROM cartentries AS ce\n" +
                        "WHERE PK IN (:flights);";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, numRow) -> rs.getString("flight"));
    }

    public String getJourneyType(String basketId, String flightKey) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue(BASKET_ID, basketId)
                .addValue("flightKey", flightKey)
                .addValue(FARE_TYPE, DBConstants.FARE_TYPE);

        String query =
                "SELECT TOP (1) ce.p_journeytype\n" +
                        "FROM carts AS c\n" +
                        "   INNER JOIN cartentries AS ce ON ce.p_order = c.PK\n" +
                        "       AND ce.p_product IN (\n" +
                        "           SELECT PK\n" +
                        "           FROM products\n" +
                        "           WHERE p_code IN (:fareType)\n" +
                        "       )\n" +
                        "WHERE c.p_code = :basketId\n" +
                        "   AND ce.p_flightcode = :flightKey;";

        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, String.class);
    }

    /**
     * isBasketExists, it checks whether the basket exists or not and returns boolean
     *
     * @param basketId
     * @return
     */
    public boolean isBasketExists(String basketId) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue(BASKET_ID, basketId);

        String query =
                "SELECT COUNT(*) FROM carts AS c INNER JOIN cartentries AS ce ON c.pk = ce.p_order " +
                        "WHERE c.p_code= :basketId;";
        Integer result = this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, Integer.class);
        return result == null || result.intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
    }

    /**
     * getCustomerForBasket, returns the customer associated to the basket
     *
     * @param basketId
     * @return Associated customer to the requested basket
     */
    public String getCustomerForBasket(String basketId) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue(BASKET_ID, basketId);

        String query =
                "SELECT [p_uid] FROM carts AS c " +
                        "INNER JOIN users AS u " +
                        "ON c.p_user = u.pk " +
                        "WHERE c.p_code= :basketId;";
        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, String.class);
    }

    /**
     * getCustomerForBasket, returns the customer associated to the basket
     *
     * @param basketId
     * @return Associated customer to the requested basket
     */
    public List<String> getBasket(String basketId) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue(BASKET_ID, basketId);

        String query =
                "SELECT [p_code] FROM carts AS c " +
                        "WHERE c.p_code= :basketId;";
        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> rs.getString("p_code"));
    }

    /**
     * getDocumentCountForBasket, it return total number of documents associated to the basket
     *
     * @param basketId
     * @return document count that is associated to the requested basket
     */
    public int getDocumentCountForBasket(String basketId) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue(BASKET_ID, basketId);

        String query =
                "SELECT COUNT(1) " +
                        "from AdvancePassengerInfo api, traveller t,  orderentrytraveller oet, travelorderentryinfo toei " +
                        "WHERE " +
                        "api.p_traveller = t.pk " +
                        "AND t.pk = oet.targetPK " +
                        "AND oet.SourcePK = toei.pk " +
                        "AND toei.pk in (" +
                        "SELECT DISTINCT p_travelorderentryinfo FROM cartentries ce, carts c " +
                        " WHERE ce.p_order = c.pk " +
                        "AND c.p_code = :basketId);";
        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, Integer.class);
    }

    /**
     * getPassengerInfoForAllPassengersInBasket, returns the basic info for a passenger
     *
     * @param basketId
     * @return returns all the passenger information associated to the basket
     */
    public List<BasketPassengerBasicInfoModel> getPassengerInfoForAllPassengersInBasket(String basketId) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue(BASKET_ID, basketId);

        String query =
                "SELECT p_title, p_firstname, p_surname, p_age, p_emailaddress, p_phonenumber, p_membershipnumber" +
                        " FROM passengerinformation WHERE pk in (" +
                        "SELECT p_info FROM traveller WHERE pk in (" +
                        "SELECT targetPK FROM orderentrytraveller WHERE SourcePK in (\n" +
                        "SELECT pk FROM travelorderentryinfo WHERE pk in (" +
                        "SELECT DISTINCT p_travelorderentryinfo FROM cartentries WHERE p_order in (" +
                        "SELECT pk FROM carts WHERE p_code = :basketId)))));";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> new BasketPassengerBasicInfoModel(
                rs.getString("p_title"),
                rs.getString("p_firstname"),
                rs.getString("p_surname"),
                rs.getInt("p_age"),
                rs.getString("p_emailaddress"),
                rs.getString("p_phonenumber"),
                rs.getString("p_membershipnumber")
        ));
    }

    /**
     * getEJPlusMembershipExistsForAnyPassengerInBasket, returns all ejPlus membership numbers for all passengers
     *
     * @param basketId
     * @return all ejPlus membership numbers for all passengers
     */
    public List<String> getEJPlusMembershipExistsForAnyPassengerInBasket(String basketId) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue(BASKET_ID, basketId);

        String query =
                "SELECT t.p_ejmembership " +
                        "from traveller t,  orderentrytraveller oet, travelorderentryinfo toei " +
                        "WHERE t.pk = oet.targetPK " +
                        "AND oet.SourcePK = toei.pk " +
                        "AND toei.pk in (" +
                        "SELECT DISTINCT p_travelorderentryinfo from cartentries ce, carts c " +
                        "WHERE ce.p_order = c.pk " +
                        "AND c.p_code = :basketId);";
        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> rs.getString("p_ejmembership"));
    }

    public PassengerStatus getCartPassengerStatus(String basketId, String passengerId) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue(BASKET_ID, basketId)
                .addValue(PASSENGER_ID, passengerId)
                .addValue(FARE_TYPE, DBConstants.FARE_TYPE);

        String query =
                "SELECT\n" +
                        "	evco.Code AS consignment\n" +
                        "	, evt.Code AS apis\n" +
                        "	, evapi.Code AS icts\n" +
                        "FROM carts AS c \n" +
                        "	INNER JOIN cartentries AS ce ON ce.p_order = c.PK \n" +
                        "			 AND ce.p_product IN ( \n" +
                        "					 SELECT PK \n" +
                        "					 FROM products \n" +
                        "					 WHERE p_code IN (:fareType)\n" +
                        "			 )\n" +
                        "	INNER JOIN travelorderentryinfo AS toei ON toei.PK = ce.p_travelorderentryinfo\n" +
                        "	INNER JOIN orderentrytraveller AS oet ON oet.SourcePK = toei.PK\n" +
                        "	INNER JOIN traveller AS t ON t.PK = oet.TargetPK\n" +
                        "	LEFT JOIN advancepassengerinfo AS api ON api.p_traveller = t.pk\n" +
                        "	LEFT JOIN enumerationvalues AS evco ON evco.PK = t.p_consignmentstatus\n" +
                        "	LEFT JOIN enumerationvalues AS evt ON evt.PK = t.p_apisstatus\n" +
                        "	LEFT JOIN enumerationvalues AS evapi ON evapi.PK = api.p_ictsstatus\n" +
                        "WHERE c.p_code = :basketId\n" +
                        "		AND toei.p_code = :passengerId;";

        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, (rs, rn) -> new PassengerStatus(
                rs.getString("apis"),
                rs.getString("icts"),
                rs.getString("consignment")
        ));
    }
}
