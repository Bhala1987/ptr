package com.hybris.easyjet.database.hybris.models;

import lombok.Data;

/**
 * Created by AndrewGr on 14/12/2016.
 */
@Data
public class FeesAndTaxesModel {

    private String feeCode;
    private String feeName;
    private String passengerType;
    private Double feeValue;
    private String feeCurrency;

    public FeesAndTaxesModel(String feeCode, String feeName, String passengerType, String feeValue, String feeCurrency) {

        this.feeCode = feeCode;
        this.feeName = feeName;
        this.passengerType = passengerType;
        this.feeValue = Double.valueOf(feeValue);
        this.feeCurrency = feeCurrency;
    }

}