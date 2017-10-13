package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.queryparams.FlightQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.CustomerRecentSearchesResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.common.RecentSearch;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by ptr-kvijayapal on 1/31/2017.
 */

public class GetRecentSearchServiceAssertion extends Assertion<GetRecentSearchServiceAssertion, CustomerRecentSearchesResponse> {

    private int numberOfRecentSearches;
    private List<RecentSearch<CustomerRecentSearchesResponse.Flight>> recentSearches;

    public GetRecentSearchServiceAssertion(CustomerRecentSearchesResponse customerRecentSearchesResponse) {

        this.response = customerRecentSearchesResponse;
        this.recentSearches = customerRecentSearchesResponse.getRecentSearches();
        this.numberOfRecentSearches = recentSearches.size();
    }

    public GetRecentSearchServiceAssertion totalRecentSearchesShouldBe(int expectedNumberOfSearches) {

        assertTrue(numberOfRecentSearches == expectedNumberOfSearches);
        return this;
    }

    public GetRecentSearchServiceAssertion recentSearchShouldContains(FlightQueryParams queryParams) {

        assertTrue(atLeastOneOfTheRecentSearchesShouldHave(queryParams));
        return this;
    }

    private boolean atLeastOneOfTheRecentSearchesShouldHave(FlightQueryParams queryParams) {

        for (RecentSearch recentSearch : recentSearches) {
            if (recentSearchMatches(recentSearch, queryParams)) {
                return true;
            }
        }
        return false;
    }

    private boolean recentSearchMatches(RecentSearch<CustomerRecentSearchesResponse.Flight> recentSearch, FlightQueryParams queryParams) {

        return verifyPassengerMix(recentSearch.getPassengerMix(), queryParams.getAdult(), queryParams.getChild(), queryParams
                .getInfant()) &&
                verifyInBoundDate(recentSearch.getInbound().getDate(), queryParams.getInboundDate()) &&
                verifyAirportDetailsAndOutBoundDate(recentSearch, queryParams);
    }

    private boolean verifyInBoundDate(String actualInboundDate, String expectedInBoundDate) {

        return expectedInBoundDate == null || expectedInBoundDate.equals(actualInboundDate);
    }

    private boolean verifyAirportDetailsAndOutBoundDate(RecentSearch<CustomerRecentSearchesResponse.Flight> recentSearch, FlightQueryParams queryParams) {

        return recentSearch.getOutbound().getAirportCode().equals(queryParams.getOrigin()) &&
                recentSearch.getInbound().getAirportCode().equals(queryParams.getDestination()) &&
                recentSearch.getOutbound().getDate().equals(queryParams.getOutboundDate());
    }

    private boolean verifyPassengerMix(List<RecentSearch.PassengerMix> passengerMixList, String expectedNumberOfAdults, String expectedNumberOfChild, String expectedNumberOfInfants) {

        int actualNumberOfAdults = 0, actualNumberOfChild = 0, actualNumberOfInfant = 0;
        String[] pax;
        for (RecentSearch.PassengerMix passengermix : passengerMixList) {
            if (passengermix.getCode().equals("adult")) {
                if(passengermix.getQuantity().contains(","))
                {
                    pax=passengermix.getQuantity().split(";");
                    actualNumberOfAdults=Integer.valueOf(pax[0]);
                }
                else
                actualNumberOfAdults = Integer.valueOf(passengermix.getQuantity());
            } else if (passengermix.getCode().equals("child")) {
                if(passengermix.getQuantity().contains(","))
                {
                    pax=passengermix.getQuantity().split(";");
                    actualNumberOfAdults=Integer.valueOf(pax[0]);
                }
                else
                actualNumberOfChild = Integer.valueOf(passengermix.getQuantity());
            } else if (passengermix.getCode().equals("infant")) {
                if(passengermix.getQuantity().contains(","))
                {
                    pax=passengermix.getQuantity().split(";");
                    actualNumberOfAdults=Integer.valueOf(pax[0]);
                }
                else
                actualNumberOfInfant =Integer.valueOf( passengermix.getQuantity());
            }
        }
        return actualNumberOfAdults == Integer.parseInt(expectedNumberOfAdults) &&
                actualNumberOfChild == Integer.parseInt(expectedNumberOfChild) &&
                actualNumberOfInfant == Integer.parseInt(expectedNumberOfInfants);
    }
}
