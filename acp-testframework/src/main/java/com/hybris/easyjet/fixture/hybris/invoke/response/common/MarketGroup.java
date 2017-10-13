package com.hybris.easyjet.fixture.hybris.invoke.response.common;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MarketGroup {
    private String code;
    private String type;
    private String status;
    private List<LocalizedValue> localizedNames = new ArrayList<>();
    private List<LocalizedValue> localizedDescriptions = new ArrayList<>();
    private List<String> airports = new ArrayList<>();
    private String name;
}