package com.hybris.easyjet.database.hybris.models;

import lombok.Getter;

/**
 * Created by giuseppedimartino on 27/04/17.
 */
@Getter
public class DeallocateFareModel {
    private String fareClass;
    private int numberRequired;

    public DeallocateFareModel(String fareClass, int numberRequired) {
        this.fareClass = fareClass;
        this.numberRequired = numberRequired;
    }
}