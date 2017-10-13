package com.hybris.easyjet.fixture.hybris.invoke.queryparams;

import com.hybris.easyjet.fixture.IQueryParams;
import lombok.Builder;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by marco on 23/02/17.
 */
@Builder
public class FareTypeQueryParams extends QueryParameters implements IQueryParams {

    private String gdsFareClass;

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("gds-fare-class", gdsFareClass);
        return params;
    }
}
