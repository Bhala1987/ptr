package com.hybris.easyjet.fixture.hybris.invoke.queryparams;

import com.hybris.easyjet.fixture.IQueryParams;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by markphipps on 12/04/2017.
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class InternalPaymentFundsQueryParams extends QueryParameters implements IQueryParams {
    private String fundtype;
    private String filterby;
    private String invalid; // used to test case where an invalid parameter is added to the URI

    /**
     * get a list of parameters set
     *
     * @return a map of parameters which can be used by the jersey client
     */
    public Map<String, String> getParameters () {

        Map<String, String> queryParams = new HashMap<>();
        if (isPopulated(fundtype)) {
            queryParams.put("fund-type", fundtype);
        }
        if (isPopulated(filterby)) {
            queryParams.put("filter-by", filterby);
        }
        if (isPopulated(invalid)) {
            queryParams.put("foo", invalid);
        }
        return queryParams;
    }
}
