package com.hybris.easyjet.fixture.hybris.invoke.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CountriesResponse extends Response {

    private List<Country> countries = new ArrayList<>();

    @Getter
    @Setter
    public static class Country {
        private String code;
        private List<CountriesResponse.LocalizedValue> localizedNames = new ArrayList<>();
        private Boolean isActive;
        private String diallingCode;
    }

    @Getter
    @Setter
    public static class LocalizedValue {
        private String name;
        private String locale;
    }

}