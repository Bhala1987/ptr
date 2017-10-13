package com.hybris.easyjet.database.hybris.models;

import lombok.Getter;

@Getter
public class BulkTransferReasonModel {

    private final String name;
    private final String language;
    private final String code;

    public BulkTransferReasonModel(String name, String language, String code) {
        this.name = name;
        this.language = language;
        this.code = code;
    }
}
