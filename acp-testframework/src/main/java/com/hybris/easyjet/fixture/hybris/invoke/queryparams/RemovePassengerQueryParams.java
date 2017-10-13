package com.hybris.easyjet.fixture.hybris.invoke.queryparams;

import com.hybris.easyjet.fixture.IQueryParams;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by robertadigiorgio on 13/04/2017.
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class RemovePassengerQueryParams extends QueryParameters implements IQueryParams {

    private String all_related_flights;

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> queryParams = new HashMap<>();
        if (isPopulated(all_related_flights)) {
            queryParams.put("all-related-flights", all_related_flights);
        }
        return queryParams;
    }
}