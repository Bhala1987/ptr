package com.hybris.easyjet.fixture.hybris.invoke.queryparams;

import com.hybris.easyjet.fixture.IQueryParams;
import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by giuseppedimartino on 31/01/17.
 */
@Builder
public class SectorQueryParams extends QueryParameters implements IQueryParams {

    private String originAirportCode;

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("origin-airport-type", originAirportCode);
        return queryParams;
    }

}
