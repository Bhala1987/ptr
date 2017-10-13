package com.hybris.easyjet.fixture.hybris.invoke.queryparams;

import com.hybris.easyjet.fixture.IQueryParams;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lbasile on 20/02/17.
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class CustomerProfileQueryParams extends QueryParameters implements IQueryParams {

    private String sections;
    private String excludeExpired;
    private String registrationtype;

    @Override
    public Map<String, String> getParameters() {

        Map<String, String> queryParams = new HashMap<>();
        if (isPopulated(sections)) {
            queryParams.put("sections", sections);
        }
        if (isPopulated(excludeExpired)) {
            queryParams.put("exclude-expired", excludeExpired);
        }
        if (isPopulated(registrationtype)) {
            queryParams.put("registration-type", registrationtype);
        }
        return queryParams;
    }
}
