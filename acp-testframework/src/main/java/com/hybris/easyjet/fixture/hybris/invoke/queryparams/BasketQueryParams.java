package com.hybris.easyjet.fixture.hybris.invoke.queryparams;

import com.hybris.easyjet.fixture.IQueryParams;
import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ptr-tdudhale on 1/19/2017.
 */
@Builder
public class BasketQueryParams extends QueryParameters implements IQueryParams {

    private String actionType;
    private String numberOfFare;
    private String basketId;

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> queryParams = new HashMap<>();
        if (isPopulated(actionType)) {
            queryParams.put("action-type", actionType);
        }
        if (isPopulated(basketId)) {
            queryParams.put("basket-id", basketId);
        }
        if (isPopulated(numberOfFare)) {
            queryParams.put("number-of-fares", numberOfFare);
        }
        return queryParams;
    }
}
