package com.hybris.easyjet.fixture.hybris.invoke.queryparams;

import com.hybris.easyjet.fixture.IQueryParams;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jamie on 21/03/2017.
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class SeatMapQueryParams extends QueryParameters implements IQueryParams {

    private String basketId;
    private String bundleId;
    private String currency;

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> queryParams = new HashMap<>();

        if ( isPopulated( basketId ) ) {
            queryParams.put("basket-id", basketId);
        }

        if ( isPopulated( bundleId ) ) {
            queryParams.put("bundle-id", bundleId);
        }

        if ( isPopulated( currency ) ) {
            queryParams.put("currency", currency);
        }

        return queryParams;
    }
}
