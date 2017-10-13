package com.hybris.easyjet.database.hybris.models;

import lombok.Data;

/**
 * Created by lbasile on 15/02/17.
 */

@Data
public class KeyDateModel {

    private String type;
    private String month;
    private String day;

    public KeyDateModel(String type, String month, String day) {

        this.type = type;
        this.month = month;
        this.day = day;

    }
}
