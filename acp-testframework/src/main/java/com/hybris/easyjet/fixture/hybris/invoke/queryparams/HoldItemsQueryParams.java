package com.hybris.easyjet.fixture.hybris.invoke.queryparams;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hybris.easyjet.fixture.IQueryParams;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by giuseppedimartino on 15/03/17.
 */
@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HoldItemsQueryParams extends QueryParameters implements IQueryParams {

    private String currency;
    private String bundleId;
    private String sector;
    private String flightKey;

    @Override
    public Map<String, String> getParameters() {

        Map<String, String> queryParams = new HashMap<>();
        if(isPopulated(currency)){
        queryParams.put("currency", currency);
        }
        if(isPopulated(bundleId)) {
            queryParams.put("bundle-id", bundleId);
        }
        if(isPopulated(sector)) {
            queryParams.put("sector", sector);
        }
        if(isPopulated(flightKey)) {
            queryParams.put("flight-key", flightKey);
        }
        return queryParams;
    }

}
