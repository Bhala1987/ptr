package com.hybris.easyjet.fixture.hybris.invoke.response.customer;

import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.MarketGroup;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.common.RecentSearch;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ptr-kvijayapal on 1/25/2017.
 */
@Getter
@Setter
public class CustomerRecentSearchesResponse extends Response {
    private List<RecentSearch<Flight>> recentSearches = new ArrayList<>();

    @Getter
    @Setter
    public static class Flight extends RecentSearch.Flight {
        private String date;
        private String airportCode;
        private MarketGroup marketGroup;
    }

}