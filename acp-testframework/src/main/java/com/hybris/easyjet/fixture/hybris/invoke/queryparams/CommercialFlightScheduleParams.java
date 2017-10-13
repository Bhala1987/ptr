package com.hybris.easyjet.fixture.hybris.invoke.queryparams;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jamie on 13/02/2017.
 */
@Getter
@Setter
@Builder
public class CommercialFlightScheduleParams extends QueryParameters {
    private String fromDate;
    private String toDate;
    private String origin;
    private String destination;

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> queryParams = new HashMap<>();

        if (isPopulated(fromDate)) queryParams.put("from-date", fromDate);
        if (isPopulated(toDate)) queryParams.put("to-date", toDate);
        if (isPopulated(origin)) queryParams.put("origin", origin);
        if (isPopulated(destination)) queryParams.put("destination", destination);

        return queryParams;
    }

}
