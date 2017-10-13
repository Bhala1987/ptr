package com.hybris.easyjet.database.hybris.models;

import lombok.Data;

/**
 * Created by daniel on 23/11/2016.
 */
@Data
public class PassengerTypeDbModel {

    private String pCode;
    private int minAge;
    private int maxAge;
    private String passengerTypeCode;
    private boolean active;

    public PassengerTypeDbModel(String pCode, int minAge, int maxAge, String passengerTypeCode, boolean active) {
        this.pCode = pCode;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.passengerTypeCode = passengerTypeCode;
        this.active = active;
    }
}
