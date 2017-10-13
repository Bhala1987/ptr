package com.hybris.easyjet.fixture.hybris.invoke.queryparams;

import com.hybris.easyjet.fixture.IQueryParams;
import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by giuseppecioce on 13/02/2017.
 */
@Builder
public class SSRDataQueryParams extends QueryParameters implements IQueryParams {
    private String sector;

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("sector", sector);
        return queryParams;
    }
}
