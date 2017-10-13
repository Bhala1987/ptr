package com.hybris.easyjet.fixture.hybris.invoke.queryparams;

import com.hybris.easyjet.fixture.IQueryParams;
import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by giuseppedimartino on 13/04/17.
 */
@Builder
public class RemoveFlightQueryParam extends QueryParameters implements IQueryParams {

    private String fromSearchResults;

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("from-search-results", fromSearchResults);
        return queryParams;
    }
}
