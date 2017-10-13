package com.hybris.easyjet.fixture.hybris.invoke.queryparams;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by webbd on 10/31/2016.
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class SaveFlightInterestParams extends QueryParameters {

    private String flightKey;
    private String fareType;

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> queryParams = new HashMap<>();
        if (isPopulated(flightKey)) {
            queryParams.put("flightKey", flightKey);
        }
        if (isPopulated(fareType)) {
            queryParams.put("fareType", fareType);
        }

        return queryParams;
    }


}


