package com.hybris.easyjet.fixture.hybris.invoke.queryparams;

import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bhala.
 */

@Builder
public class StationsForCarHireQueryParams extends QueryParameters {
    private String countryCode;

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> queryParams = new HashMap<>();
        if (isPopulated(countryCode)) {
            queryParams.put("country-code", countryCode);
        }
        return queryParams;
    }
}
