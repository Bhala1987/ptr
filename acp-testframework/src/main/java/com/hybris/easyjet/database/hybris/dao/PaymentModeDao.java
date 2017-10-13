package com.hybris.easyjet.database.hybris.dao;

import com.hybris.easyjet.database.QueryParser;
import com.hybris.easyjet.database.hybris.models.PaymentModeModel;
import com.jolbox.bonecp.BoneCPDataSource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by marco on 20/04/17.
 */
@Repository
public class PaymentModeDao {
    private static final Logger LOG = LogManager.getLogger(PaymentModeDao.class);

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public PaymentModeDao(@Qualifier("hybrisDataSource") BoneCPDataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<PaymentModeModel> getBackOfficePaymentModes(boolean isActive) {
        List<String> paymentTypesList;
        List<PaymentModeModel> code;
        List<PaymentModeModel> paymentCodes = new ArrayList<>();

        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("active", isActive);

        String query = "SELECT [p_paymenttypes] FROM [dbo].[paymentmodes]";

        List<Map<String, Object>> mapList = this.jdbcTemplate.queryForList(
            QueryParser.parse(query),
            params
        );

        if (mapList == null) {
            return new ArrayList<>();
        }

        for (Map row : mapList) {
            if (row.get("p_paymenttypes") != null) {
                paymentTypesList = Arrays.asList(
                    row.get("p_paymenttypes").toString().substring(4).split(",")
                );

                params = new MapSqlParameterSource("Code", paymentTypesList);

                query = "SELECT [Code] FROM [dbo].[enumerationvalues] WHERE PK IN (:Code);";

                code = this.jdbcTemplate.query(
                    QueryParser.parse(query),
                    params,
                    (rs, rowNum) -> new PaymentModeModel(rs.getString("Code"))
                );

                paymentCodes.addAll(code);
            }
        }

        return paymentCodes;
    }

    public List<String> getPaymentEntityForCustomer(String uID) {
        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("p_uid", uID);

        String query = "SELECT paymentinfos.p_savedpaymentmethodreference \n" +
            "FROM paymentinfos JOIN users ON paymentinfos.p_user = users.PK\n" +
            "WHERE users.p_uid = :p_uid";

        return this.jdbcTemplate.query(
            QueryParser.parse(query),
            params,
            (rs, rowNum) -> rs.getString("p_savedpaymentmethodreference")
        );
    }

    public String getDefaultPaymentorCustomer(String uID) {
        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("p_uid", uID);

        String query = "SELECT paymentinfos.p_savedpaymentmethodreference \n" +
            "FROM paymentinfos JOIN users ON paymentinfos.p_user = users.PK\n" +
            "WHERE users.p_uid = :p_uid\n" +
            "AND paymentinfos.PK = users.p_defaultpaymentinfo;";

        return this.jdbcTemplate.query(
            QueryParser.parse(query),
            params,
            (rs, rowNum) -> rs.getString("p_savedpaymentmethodreference")
        ).get(0);
    }

    public List<String> getChannelsForPaymentMethod(String paymentMethod) {
        String paymentModeChannelQuery = "SELECT p_code, p_channels FROM paymentmodes WHERE p_code = :p_code;";

        List<String> channelKeys = Arrays.asList(
            this.jdbcTemplate.queryForObject(
                QueryParser.parse(paymentModeChannelQuery),
                new MapSqlParameterSource().addValue("p_code", paymentMethod),
                (resultSet, rowNum) -> resultSet.getString("p_channels")
            ).split(",")
        );

        List<String> channelNames = channelKeys.stream()
            .map(key -> {
                try {
                    Integer intKey = Integer.valueOf(key);

                    return this.jdbcTemplate.queryForObject(
                        QueryParser.parse("SELECT Code FROM enumerationvalues WHERE PK = :key;"),
                        new MapSqlParameterSource().addValue("key", intKey),
                        (resultSet, rowNum) -> resultSet.getString("Code")
                    );
                } catch (EmptyResultDataAccessException | NumberFormatException e) {
                    LOG.info(e);
                    return null;
                }
            }).collect(Collectors.toList());

        channelNames.removeIf(Objects::isNull);

        return channelNames;
    }

    public List<String> getRefundPaymentMethods() {
        String query = "select p_code from refundtype";

        return this.jdbcTemplate.query(
            QueryParser.parse(query),
            (rs, rowNum) -> rs.getString("p_code")
        );
    }

    public List<String> getRefundPaymentTransactionStatus(String bookingRef){

        String query = "select p_transactionstatus from paymnttrnsctentries where p_code LIKE '%"+bookingRef+"%';";

        return this.jdbcTemplate.query(
                QueryParser.parse(query),
                   (rs, rowNum) -> rs.getString("p_transactionstatus")
        );
    }

    /**
     * getPaymentTransactionStatusByCode, it returns the transaction status by the code
     * @param code
     * @return String
     */
    public String getPaymentTransactionStatusByCode(String code) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("p_code", code);
        StringBuffer query = new StringBuffer();
        query.append("SELECT pte.p_transactionstatus as status FROM paymenttransactions AS pt, paymnttrnsctentries AS pte WHERE pt.pk = pte.p_paymenttransaction AND pt.p_code=:p_code ");
        query.append("AND pte.p_type in (SELECT PK FROM enumerationvalues AS ev WHERE code='REFUND_FOLLOW_ON');");
        return this.jdbcTemplate.queryForObject(QueryParser.parse(query.toString()), parameters, (rs, rowNum) -> rs.getString("status"));
    }
}
