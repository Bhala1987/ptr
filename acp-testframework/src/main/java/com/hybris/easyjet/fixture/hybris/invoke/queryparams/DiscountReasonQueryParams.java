package com.hybris.easyjet.fixture.hybris.invoke.queryparams;

import com.hybris.easyjet.fixture.IQueryParams;
import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by giuseppecioce on 31/03/2017.
 */
@Builder
public class DiscountReasonQueryParams extends QueryParameters implements IQueryParams {
    private String currency;

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("currency", currency);
        return queryParams;
    }
}
