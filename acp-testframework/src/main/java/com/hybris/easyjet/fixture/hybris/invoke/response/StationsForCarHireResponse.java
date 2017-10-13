package com.hybris.easyjet.fixture.hybris.invoke.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class StationsForCarHireResponse extends Response {

    private List<Stations> stations = new ArrayList<>();

    @Getter
    @Setter
    public static class Stations {
        private String stationCode;
        private String stationName;
    }

}