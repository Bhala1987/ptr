package com.hybris.easyjet.fixture.hybris.invoke.response.customer.common;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by g.dimartino on 14/05/17.
 */
@Getter
@Setter
public class RecentSearch<F extends RecentSearch.Flight> {
    private F outbound;
    private F inbound;
    private List<PassengerMix> passengerMix = new ArrayList<>();

    @Getter
    @Setter
    public static class Flight {
        private String code;
        private String name;
        private String terminal;
    }

    @Getter
    @Setter
    public static class PassengerMix {
        private String code;
        private String quantity;
    }

}