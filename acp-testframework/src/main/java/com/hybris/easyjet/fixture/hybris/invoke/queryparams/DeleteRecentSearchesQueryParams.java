package com.hybris.easyjet.fixture.hybris.invoke.queryparams;

import com.hybris.easyjet.fixture.IQueryParams;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by albertowork on 7/10/17.
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class DeleteRecentSearchesQueryParams extends QueryParameters implements IQueryParams {
    private String searchIndexList;

    @Override
    public Map<String, String> getParameters() {

        Map<String, String> queryParams = new HashMap<>();
        if (isPopulated(searchIndexList)) {
            queryParams.put("search-index-list", searchIndexList);
        }

        return queryParams;
    }
}
