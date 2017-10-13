package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.LocalizedName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CurrenciesResponse extends Response {
    private List<Currency> currencies = new ArrayList<>();

    @Getter
    @Setter
    public static class Currency {
        private String code;
        private List<LocalizedName> localizedNames = new ArrayList<>();
        private String displaySymbol;
        private Integer decimalPlaces;
        private Boolean isBaseCurrency;
        private Boolean isActive;
    }

}