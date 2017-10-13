package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.hybris.easyjet.database.hybris.models.CustomerModel;
import com.hybris.easyjet.database.hybris.models.CustomerRecentSearchModel;
import com.hybris.easyjet.database.hybris.models.DbCustomerModel;
import com.hybris.easyjet.database.hybris.models.KeyDateModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.jolbox.bonecp.BoneCPDataSource;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by marco on 09/02/17.
 */
@Repository
@ToString
public class CustomerDao {

    private NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param dataSource autowired datasource which allows connectivity to the Hybris database
     */
    @Autowired
    public CustomerDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public DbCustomerModel getActiveCustomer(boolean isStaff) throws EasyjetCompromisedException {
        List<DbCustomerModel> customers = getActiveCustomers(isStaff);
        if (customers.isEmpty()) {
            throw new EasyjetCompromisedException("No customer in the database");
        }
        return customers.get(new Random().nextInt(customers.size()));
    }

    private List<DbCustomerModel> getActiveCustomers(boolean isStaff) {

        SqlParameterSource params = new MapSqlParameterSource();

        String query;
        if (isStaff) {
            query = "SELECT DISTINCT u.p_uid AS id\n" +
                    ", hrs.p_employeeid AS employeeId\n" +
                    "FROM users AS u\n" +
                    "   INNER JOIN enumerationvalues AS ev ON ev.PK = u.p_status\n" +
                    "   INNER JOIN hrstaff AS hrs ON hrs.pk = u.p_hrstaff\n" +
                    "WHERE u.p_customerid IS NOT NULL\n" +
                    "   AND ev.Code = 'ACTIVE'\n" +
                    "   AND u.p_logindisabled = 0\n" +
                    "   AND hrs.p_active = 1\n" +
                    "   AND hrs.p_employeeid IS NOT NULL;";
        } else {
            query = "SELECT DISTINCT u.p_uid AS id\n" +
                    "FROM users AS u\n" +
                    "   INNER JOIN enumerationvalues AS ev ON ev.PK = u.p_status\n" +
                    "WHERE u.p_customerid IS NOT NULL\n" +
                    "   AND ev.Code = 'ACTIVE'\n" +
                    "   AND u.p_logindisabled = 0;";
        }

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) ->
                DbCustomerModel.builder()
                        .id(rs.getString("id"))
                        .build());
    }

    public DbCustomerModel getActiveCustomerWithAPIs(boolean isStaff) throws EasyjetCompromisedException {
        List<DbCustomerModel> customers = getActiveCustomersWithAPIs(isStaff);
        if (customers.isEmpty()) {
            throw new EasyjetCompromisedException("No customer in the database");
        }
        return customers.get(new Random().nextInt(customers.size()));
    }

    private List<DbCustomerModel> getActiveCustomersWithAPIs(boolean isStaff) {

        SqlParameterSource params = new MapSqlParameterSource();

        String query;
        if (isStaff) {
            query = "SELECT DISTINCT u.p_uid AS id\n" +
                    ", hrs.p_employeeid AS employeeId\n" +
                    "FROM users AS u\n" +
                    "   INNER JOIN enumerationvalues AS ev ON ev.PK = u.p_status\n" +
                    "   INNER JOIN hrstaff AS hrs ON hrs.pk = u.p_hrstaff\n" +
                    "   INNER JOIN advancepassengerinfo AS api ON api.p_customer = u.pk\n" +
                    "WHERE u.p_customerid IS NOT NULL\n" +
                    "   AND ev.Code = 'ACTIVE'\n" +
                    "   AND u.p_logindisabled = 0\n" +
                    "   AND hrs.p_active = 1\n" +
                    "   AND hrs.p_employeeid IS NOT NULL;";
        } else {
            query = "SELECT DISTINCT u.p_uid AS id\n" +
                    "FROM users AS u\n" +
                    "   INNER JOIN enumerationvalues AS ev ON ev.PK = u.p_status\n" +
                    "   INNER JOIN advancepassengerinfo AS api ON api.p_customer = u.pk\n" +
                    "WHERE u.p_customerid IS NOT NULL\n" +
                    "   AND ev.Code = 'ACTIVE'\n" +
                    "   AND u.p_logindisabled = 0;";
        }

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) ->
                DbCustomerModel.builder()
                        .id(rs.getString("id"))
                        .build());
    }

    public DbCustomerModel getActiveCustomerWithSignificantOthers(boolean isStaff) throws EasyjetCompromisedException {
        List<DbCustomerModel> customers = getActiveCustomersWithSignificantOthers(isStaff);
        if (customers.isEmpty()) {
            throw new EasyjetCompromisedException("No customer in the database");
        }
        return customers.get(new Random().nextInt(customers.size()));
    }

    private List<DbCustomerModel> getActiveCustomersWithSignificantOthers(boolean isStaff) {

        SqlParameterSource params = new MapSqlParameterSource();

        String query = "SELECT DISTINCT u.p_uid AS id\n" +
                ", hrs.p_employeeid AS employeeId\n" +
                "FROM significantother AS so\n" +
                "   INNER JOIN users AS u ON u.PK = so.p_significantothercustomer\n" +
                "   INNER JOIN enumerationvalues AS ev ON ev.PK = u.p_status\n" +
                "   INNER JOIN hrstaff AS hrs ON hrs.pk = u.p_hrstaff\n" +
                "WHERE u.p_customerid IS NOT NULL\n" +
                "   AND ev.Code = 'ACTIVE'\n" +
                "   AND u.p_logindisabled = 0\n" +
                "   AND hrs.p_active = 1\n" +
                "   AND hrs.p_employeeid IS NOT NULL;";

        List<DbCustomerModel> customers = this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) ->
                DbCustomerModel.builder()
                        .id(rs.getString("id"))
                        .build());

        for (DbCustomerModel customer : customers) {
            params = new MapSqlParameterSource("customer", customer.getId());

            query = "SELECT t.p_info AS id\n" +
                    "FROM significantother AS so\n" +
                    "   INNER JOIN users AS u ON u.PK = so.p_significantothercustomer\n" +
                    "   INNER JOIN traveller AS t ON t.pk = so.p_nominatedtraveller\n" +
                    "WHERE u.p_uid = :customer;";

            customer.setSignificantOthers(this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) ->
                    DbCustomerModel.SignificantOther.builder()
                            .id(rs.getString("id"))
                            .build()));
        }

        return customers;
    }

    public DbCustomerModel getActiveCustomerWithDependents() throws EasyjetCompromisedException {
        List<DbCustomerModel> customers = getActiveCustomersWithDependents();
        if (customers.isEmpty()) {
            throw new EasyjetCompromisedException("No customer in the database");
        }
        return customers.get(new Random().nextInt(customers.size()));
    }

    private List<DbCustomerModel> getActiveCustomersWithDependents() {

        SqlParameterSource params = new MapSqlParameterSource();

        String query = "SELECT DISTINCT u.p_uid AS id\n" +
                ", hrs.p_employeeid AS employeeId\n" +
                "FROM hrstaffdependant AS hrsd\n" +
                "   INNER JOIN hrstaff AS hrs ON hrs.PK = hrsd.p_hrstaff\n" +
                "   INNER JOIN users AS u ON u.p_hrstaff = hrs.pk\n" +
                "   INNER JOIN enumerationvalues AS ev ON ev.PK = u.p_status\n" +
                "WHERE u.p_customerid IS NOT NULL\n" +
                "   AND ev.Code = 'ACTIVE'\n" +
                "   AND u.p_logindisabled = 0\n" +
                "   AND hrs.p_active = 1\n" +
                "   AND hrs.p_employeeid IS NOT NULL;";

        List<DbCustomerModel> customers = this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) ->
                DbCustomerModel.builder()
                        .id(rs.getString("id"))
                        .employeeId(rs.getString("employeeId"))
                        .build());

        for (DbCustomerModel customer : customers) {
            params = new MapSqlParameterSource("hrStaffMember", customer.getEmployeeId());

            query = "SELECT hrsd.p_dependantid AS id\n" +
                    "FROM hrstaffdependant AS hrsd\n" +
                    "   INNER JOIN hrstaff AS hrs ON hrs.PK = hrsd.p_hrstaff\n" +
                    "WHERE hrs.p_employeeid = :hrStaffMember;";

            customer.setDependents(this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) ->
                    DbCustomerModel.Dependent.builder()
                            .id(rs.getString("id"))
                            .build()));
        }

        return customers;
    }

    public List<CustomerModel> getAllCustomers() {

        String query =
                "SELECT\n" +
                        "u.[p_uid]\n" +
                        ",u.[p_customerid]\n" +
                        ",u.[passwd]\n" +
                        "FROM [dbo].[users] as u\n" +
                        "INNER JOIN [dbo].[enumerationvalues] AS ev ON ev.PK=u.p_status\n" +
                        "WHERE p_customerid IS NOT NULL\n" +
                        "AND ev.Code='ACTIVE'\n" +
                        "AND u.p_logindisabled = 0";


        return this.jdbcTemplate.query(QueryParser.parse(query), (rs, rowNum) -> new CustomerModel(
                rs.getString("p_uid"),
                rs.getString("p_customerid"),
                rs.getString("passwd")));
    }

    public int getCustomersPaymentInfoWithId(String customerId) {

        SqlParameterSource params = new MapSqlParameterSource("p_uid", customerId);
        String query =
                "SELECT COUNT(1)\n" +
                        "FROM users as u join paymentinfos as api\n" +
                        "ON api.p_user = u.PK\n" +
                        "WHERE u.p_uid = :p_uid\n" +
                        "AND api.p_duplicate = 0;";

        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, Integer.class);
    }

    public int getCustomersTravellerWithId(String customerId) {

        SqlParameterSource params = new MapSqlParameterSource("p_uid", customerId);
        String query =
                "SELECT COUNT(1)\n" +
                        "FROM users AS u\n" +
                        "JOIN traveller AS api ON api.p_customer = u.PK\n" +
                        "WHERE u.p_uid = :p_uid;";

        return this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, Integer.class);
    }

    public int getCustomerAPISDetailWithId(String customerId) {

        SqlParameterSource params = new MapSqlParameterSource("p_uid", customerId);
        String query =
                "SELECT COUNT(1)\n" +
                        "FROM users AS u\n" +
                        "JOIN advancepassengerinfo AS api ON api.p_customer = u.PK\n" +
                        "WHERE u.p_uid = :p_uid;";

        return this.jdbcTemplate.queryForObject(query, params, Integer.class);
    }

    public List<String> getCustomerSSRWithId(String customerId) {

        SqlParameterSource params = new MapSqlParameterSource("p_uid", customerId);
        String query =
                "SELECT p_specialrequestdetail\n" +
                        "FROM [dbo].[users]\n" +
                        "WHERE p_uid = :p_uid;";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> rs.getString("p_specialrequestdetail"));
    }

    public List<String> getCustomerTokenWithId(String customerId) {

        SqlParameterSource params = new MapSqlParameterSource("p_uid", customerId);
        String query =
                "SELECT p_token\n" +
                        "FROM [dbo].[users]\n" +
                        "WHERE p_uid = :p_uid;";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> rs.getString("p_token"));
    }

    /**
     * @return list of valid customers with a shipping address
     */
    public List<CustomerRecentSearchModel> getAllRecentSearchesFor(String customerId) {

        SqlParameterSource params = new MapSqlParameterSource("p_uid", customerId);
        String query =
                "SELECT\n" +
                        "crs.[createdTS]\n" +
                        ",crs.[p_origin]\n" +
                        ",crs.[p_destination]\n" +
                        ",crs.[p_departuredate]\n" +
                        ",crs.[p_returndate]\n" +
                        ",crs.[p_adult]\n" +
                        ",crs.[p_child]\n" +
                        ",crs.[p_infant]\n" +
                        "FROM [dbo].[customerrecentsearch] AS crs\n" +
                        "JOIN [dbo].[users] AS u ON crs.p_customer = u.PK\n" +
                        "WHERE u.p_uid = :p_uid;";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> new CustomerRecentSearchModel(
                rs.getTimestamp("createdTS"),
                rs.getString("p_origin"),
                rs.getString("p_destination"),
                rs.getString("p_departuredate"),
                rs.getString("p_returndate"),
                rs.getString("p_adult"),
                rs.getString("p_child"),
                rs.getString("p_infant")));
    }

    public String getStatusOfcustomer(String customerId) {

        SqlParameterSource namedParameters = new MapSqlParameterSource("p_uid", customerId);
        String GET_CUSTOMER_SEARCHES =
                "SELECT  ev.type AS status\n" +
                        "FROM users as u join enumerationvalues AS ev \n" +
                        "ON u.p_status = ev.pk\n" +
                        "WHERE p_uid = :p_uid;";

        return this.jdbcTemplate.queryForObject(QueryParser.parse(GET_CUSTOMER_SEARCHES), namedParameters, String.class);
    }


    /**
     * @return list of valid customers with a shipping address
     */
    public List<CustomerModel> returnValidCustomerWithShippingAddress() {

        String query =
                "SELECT\n" +
                        "DISTINCT users.[p_uid]\n" +
                        ",users.[p_customerid]\n" +
                        ",users.[p_status]\n" +
                        ",titles.p_code\n" +
                        ",users.[p_firstname]\n" +
                        ",users.[p_lastname]\n" +
                        ",users.[p_ejMembership]\n" +
                        ",users.[p_flightclubnumber]\n" +
                        ",addresses.[p_postalcode]\n" +
                        ",addresses.[p_town]\n" +
                        ",countrieslp.[p_name]\n" +
                        "FROM [dbo].[users] as users\n" +
                        "INNER JOIN [dbo].[addresses] ON users.p_defaultshipmentaddress=addresses.PK\n" +
                        "INNER JOIN [dbo].[titles] as titles ON users.p_title=titles.PK\n" +
                        "INNER JOIN [dbo].[countries] ON countries.PK=addresses.p_country\n" +
                        "INNER JOIN [dbo].[enumerationvalues] ON enumerationvalues.PK=users.p_status\n" +
                        "INNER JOIN [dbo].[countrieslp] ON countrieslp.ITEMPK=countries.PK\n" +
                        "WHERE enumerationvalues.Code='ACTIVE' \n" +
                        "AND users.p_logindisabled != 1";

        return this.jdbcTemplate.query(QueryParser.parse(query), (rs, rowNum) -> new CustomerModel(
                rs.getString("p_uid"),
                rs.getString("p_customerid"),
                rs.getString("p_status"),
                rs.getString("p_code"),
                rs.getString("p_firstname"),
                rs.getString("p_lastname"),
                rs.getString("p_ejMembership"),
                rs.getString("p_flightclubnumber"),
                rs.getString("p_postalcode"),
                rs.getString("p_town"),
                rs.getString("p_name")));
    }

    /**
     * @return list of valid customers with a shipping address
     */
    public List<CustomerModel> returnValidCustomerByStatus(String status) {

        String query =
                "SELECT\n" +
                        "DISTINCT users.p_uid\n" +
                        ",users.p_customerid\n" +
                        ",users.p_status\n" +
                        ",users.p_firstname\n" +
                        ",users.p_lastname\n" +
                        ",users.p_ejMembership\n" +
                        ",users.p_flightclubnumber\n" +
                        "FROM users as users\n" +
                        "INNER JOIN titles as titles ON users.p_title=titles.PK\n" +
                        "INNER JOIN enumerationvalues ON enumerationvalues.PK=users.p_status\n" +
                        "WHERE enumerationvalues.Code='" + status + "'";

        return this.jdbcTemplate.query(QueryParser.parse(query), (rs, rowNum) -> new CustomerModel(
                rs.getString("p_uid"),
                rs.getString("p_customerid"),
                rs.getString("p_status"),
                rs.getString("p_firstname"),
                rs.getString("p_lastname"),
                rs.getString("p_ejMembership"),
                rs.getString("p_flightclubnumber")));
    }


    /**
     * @return list of valid customers details
     */
    public List<CustomerModel> returnValidCustomerDetailsFromUsersTable(String empoyeeId) {

        SqlParameterSource params = new MapSqlParameterSource("p_employeeid", empoyeeId);

        String query =
                "SELECT distinct \n" +
                        "users.[p_uid]\n" +
                        ",users.[p_customerid]\n" +
                        ",users.[p_status]\n" +
                        ",hrstaff.[p_employeeid]\n" +
                        ",users.[p_firstname]\n" +
                        ",users.[p_lastname]\n" +
                        ",users.[p_ejMembership]\n" +
                        ",users.[p_flightclubnumber]\n" +
                        "FROM [dbo].[users] AS users\n" +
                        "LEFT JOIN [dbo].[hrstaff] AS hrstaff ON users.p_hrstaff=hrstaff.PK \n" +
                        "WHERE hrstaff.[p_employeeid]= :p_employeeid;";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> new CustomerModel(
                rs.getString("p_customerid"),
                rs.getString("p_status"),
                rs.getString("p_uid"),
                rs.getString("p_firstname"),
                rs.getString("p_lastname"),
                rs.getString("p_ejMembership"),
                rs.getString("p_flightclubnumber")));
    }

    public List<CustomerModel> returnValidFullCustomerProfileForUid(String userUid) {

        SqlParameterSource params = new MapSqlParameterSource("p_uid", userUid);

        String query =
                "SELECT DISTINCT\n" +
                        "users.[p_uid]\n" +
                        ",users.[p_customerid]\n" +
                        ",users.[p_status]\n" +
                        ",titles.[p_code]\n" +
                        ",users.[p_firstname]\n" +
                        ",users.[p_lastname]\n" +
                        ",users.[p_flightclubnumber]\n" +
                        ",addresses.[p_postalcode]\n" +
                        ",addresses.[p_town]\n" +
                        ",countrieslp.[p_name]\n" +
                        ",users.[p_keydates]\n" +
                        ",staff.[p_employeeid]\n" +
                        ",staff.[p_email]\n" +
                        ",users.[p_type]\n" +
                        ",users.[p_nifnumber]\n" +
                        ",addresses.[p_phone1]\n" +
                        ",addresses.[p_phone2]\n" +
                        "FROM [dbo].[users] AS users\n" +
                        "INNER JOIN [dbo].[addresses] ON users.p_defaultshipmentaddress=addresses.PK\n" +
                        "INNER JOIN [dbo].[titles] AS titles ON users.p_title=titles.PK\n" +
                        "INNER JOIN [dbo].[countries] ON countries.PK=addresses.p_country\n" +
                        "INNER JOIN [dbo].[countrieslp] ON countrieslp.ITEMPK=countries.PK\n" +
                        "INNER JOIN [dbo].[hrstaff] AS staff ON staff.PK=users.p_hrStaff\n" +
                        "Where users.p_uid = :p_uid;";

        return this.jdbcTemplate.query(QueryParser.parse(query), params, (rs, rowNum) -> {
            CustomerModel dbCustomerModel = new CustomerModel(
                    rs.getString("p_uid"),
                    rs.getString("p_customerid"),
                    rs.getString("p_status"),
                    rs.getString("p_code"),
                    rs.getString("p_firstname"),
                    rs.getString("p_lastname"),
                    null,
                    rs.getString("p_flightclubnumber"),
                    rs.getString("p_postalcode"),
                    rs.getString("p_town"),
                    rs.getString("p_name"),
                    rs.getString("p_employeeid"),
                    rs.getString("p_type"),
                    null,
                    rs.getString("p_nifnumber"),
                    rs.getString("p_phone1"),
                    rs.getString("p_phone2"),
                    rs.getString("p_email"),
                    null
            );
            if (rs.getString("p_keydates") != null) {
                dbCustomerModel.setKeydates(getModels(rs.getString("p_keydates").split(",")));
            }

            return dbCustomerModel;
        });
    }

    private ArrayList<KeyDateModel> getModels(String[] keydatesPKS) {

        ArrayList<KeyDateModel> models = new ArrayList<KeyDateModel>();

        String query =
                "SELECT *\n" +
                        "FROM keydate AS k\n" +
                        "WHERE PK = :pk";

        for (String pk : Arrays.asList(keydatesPKS)) {
            if (!StringUtils.isEmpty(pk) && StringUtils.isNumeric(pk)) {

                SqlParameterSource params = new MapSqlParameterSource("pk", pk);

                models.add(
                        this.jdbcTemplate.queryForObject(QueryParser.parse(query), params, (nrs, nrn) -> new KeyDateModel(
                                nrs.getString("p_type"),
                                nrs.getString("p_date").split("-")[1],
                                nrs.getString("p_date").split("-")[0]
                        ))
                );
            }
        }

        return models;
    }

}