package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.LocalizedValue;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giuseppedimartino on 31/01/17.
 */
@Getter
@Setter
public class SectorResponse extends Response {
    private List<Sector> sectors = new ArrayList<>();

    @Getter
    @Setter
    public static class Sector {
        private String arrivalAirport;
        private List<Terminal> arrivalTerminal = new ArrayList<>();
        private String code;
        private String departureAirport;
        private List<Terminal> departureTerminal = new ArrayList<>();
        private Double distance;
        private Boolean isAPIS;
        private List<String> services = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class Terminal {
        private String code;
        private List<LocalizedValue> localizedTerminalNames = new ArrayList<>();
    }
}