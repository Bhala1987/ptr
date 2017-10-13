package com.hybris.easyjet.database.hybris.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * CreditFileFundModel, it holds CreditFileFundModel object
 */
@Getter
@Setter
public class CreditFileFundModel {
    private String code;
    private String name;
    private String currency;
    private Double startBalance;
    private Double currentBalance;
    private Date startDate;
    private Date expiryDate;

    /**
     * default constructor
     */
    public CreditFileFundModel () {
        code = null;
        name = null;
        currency = null;
        startBalance = 0.0;
        currentBalance = 0.0;
        expiryDate = null;
        startDate = null;
    }

    /**
     * parameterized constructor
     * @param startBalance
     * @param currentBalance
     */
    public CreditFileFundModel (Double startBalance, Double currentBalance) {
        this.startBalance = startBalance;
        this.currentBalance = currentBalance;
        code = null;
        name = null;
        currency = null;
        expiryDate = null;
        startDate = null;
    }
}
