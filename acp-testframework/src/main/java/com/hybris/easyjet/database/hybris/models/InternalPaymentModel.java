package com.hybris.easyjet.database.hybris.models;

import lombok.Getter;

/**
 * Created by markphipps on 21/04/2017.
 */
@Getter
public class InternalPaymentModel {
    private String code;
    private String currencyCode;
    private double originalBalance;
    private double currentBalance;
    private String created;
    private String expiry;

    public InternalPaymentModel(String p_code, String p_currency, double p_startBalance, double p_currentBalance, String p_startdate, String p_expirydate) {
        this.code = p_code;
        this.currencyCode = p_currency;
        this.originalBalance = p_currentBalance;
        this.currentBalance = p_currentBalance;
        this.created = p_startdate;
        this.expiry = p_expirydate;
    }
}
