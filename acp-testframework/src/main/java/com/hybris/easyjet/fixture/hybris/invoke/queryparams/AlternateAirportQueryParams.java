package com.hybris.easyjet.fixture.hybris.invoke.queryparams;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by robertadigiorgio on 13/03/2017.
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class AlternateAirportQueryParams extends QueryParameters {

    private String departureCode;
    private String destinationCode;
    private String maxDistance;


    @Override
    public Map<String, String> getParameters() {
        Map<String, String> queryParams = new HashMap<>();
        if (isPopulated(departureCode)) {
            queryParams.put("departure-code", departureCode);
        }
        if (isPopulated(destinationCode)) {
            queryParams.put("destination-code", destinationCode);
        }
        if (isPopulated(maxDistance)) {
            queryParams.put("max-distance", maxDistance);
        }
        return queryParams;
    }

}
