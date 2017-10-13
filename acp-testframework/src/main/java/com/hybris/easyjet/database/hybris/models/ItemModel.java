package com.hybris.easyjet.database.hybris.models;

import lombok.Data;

/**
 * Created by giuseppedimartino on 27/02/17.
 */
@Data
public class ItemModel {

    private String code;
    private String value;

    public ItemModel(String code, String value) {

        this.code = code;
        this.value = value;
    }
}
