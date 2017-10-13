package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.database.hybris.dao.CustomerDao;
import com.hybris.easyjet.database.hybris.models.CustomerRecentSearchModel;
import com.hybris.easyjet.fixture.hybris.helpers.CustomerHelper;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.FlightQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.CustomerRecentSearchesResponse;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by ptr-kvijayapal on 1/18/2017.
 */

@Component
public class CustomerRecentSearchAssertion extends Assertion<CustomerRecentSearchAssertion, CustomerRecentSearchesResponse> {

    private static Logger LOG = LogManager.getLogger(CustomerRecentSearchAssertion.class);

    private CustomerDao customerRecentSearchDao;
    private CustomerHelper customerHelper;
    private List<CustomerRecentSearchModel> searches, searchesAfterSort;
    private CustomerRecentSearchModel oldestSearch;
    private CustomerRecentSearchModel latestSearch;
    private int totalSearches;

    @Autowired
    public CustomerRecentSearchAssertion(CustomerDao customerRecentSearchDao) {

        this.customerRecentSearchDao = customerRecentSearchDao;
    }

    public void readRecentSearchesFor(String customerId) {

        this.searches = customerRecentSearchDao.getAllRecentSearchesFor(customerId);
        this.totalSearches = searches.size();
        if (totalSearches > 1) {
            this.oldestSearch = searches.get(0);
            this.latestSearch = searches.get(totalSearches - 1);
        } else if (totalSearches == 1) {
            this.oldestSearch = searches.get(0);
            this.latestSearch = searches.get(0);
        }
    }

    public CustomerRecentSearchAssertion totalRecentSearchesShouldBe(int expectedNumberOfSearches) {

        assertTrue(searches.size() == expectedNumberOfSearches);
        return this;
    }

    public CustomerRecentSearchAssertion oldestSearchShouldBeRemoved(FlightQueryParams oldestParams) {

        assertFalse(verifyAllTheSearchesToMatch(oldestParams));
        return this;
    }

    private Boolean verifyAllTheSearchesToMatch(FlightQueryParams oldestParams) {

        for (CustomerRecentSearchModel search : searches) {
            if (searchMatches(search, oldestParams)) return true;
        }
        return false;
    }

    public CustomerRecentSearchAssertion recentSearchShouldContains(FlightQueryParams queryParams) {

        assertTrue(latestSearchContains(queryParams));
        return this;
    }

    private boolean latestSearchContains(FlightQueryParams queryParams) {

        return searchMatches(latestSearch, queryParams);
    }

    private boolean searchMatches(CustomerRecentSearchModel search, FlightQueryParams queryParams) {

        return search.getSource().equals(queryParams.getOrigin()) &&
                search.getDestination().equals(queryParams.getDestination()) &&
                search.getNumberOfAdults().equals(queryParams.getAdult()) &&
                search.getNumberOfChildren().equals(queryParams.getChild()) &&
                search.getNumberOfInfants().equals(queryParams.getInfant());
    }

    private List<CustomerRecentSearchModel> sortByCreatedDate(List<CustomerRecentSearchModel> custRecentSearches) {

        custRecentSearches.sort(Collections.reverseOrder((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate())));
        return custRecentSearches;
    }
}

