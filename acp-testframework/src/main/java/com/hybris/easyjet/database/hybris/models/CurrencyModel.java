package com.hybris.easyjet.database.hybris.models;

import lombok.Builder;
import lombok.Getter;

/**
 * Created by giuseppedimartino on 27/03/17.
 */
@Builder
@Getter
public class CurrencyModel {
    private String code;
    private String displaySymbol;
    private int decimalPlaces;
    private Double conversion;
    private boolean isBaseCurrency;
}