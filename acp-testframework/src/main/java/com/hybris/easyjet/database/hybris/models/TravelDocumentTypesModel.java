package com.hybris.easyjet.database.hybris.models;

import lombok.Data;

/**
 * Created by giuseppecioce on 09/02/2017.
 */
@Data
public class TravelDocumentTypesModel {

    private String code;
    private boolean active;

    public TravelDocumentTypesModel(String code, boolean active) {

        this.code = code;
        this.active = active;
    }
}
