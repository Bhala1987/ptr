package com.hybris.easyjet.database.hybris.models;

import lombok.Data;

/**
 * Created by giuseppecioce on 09/02/2017.
 */
@Data
public class SSRDataModel {

    private String code;
    private boolean active;

    public SSRDataModel(String code, boolean active) {

        this.code = code;
        this.active = active;
    }
}
