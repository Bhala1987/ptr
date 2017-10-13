package com.hybris.easyjet.fixture.hybris.invoke.queryparams;

import com.hybris.easyjet.fixture.IQueryParams;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by giuseppedimartino on 03/04/17.
 */
@Builder
@Getter
public class RemoveProductQueryParams extends QueryParameters implements IQueryParams {

    private String passengerCode;
    private String flightKey;
    private String excessWeightProductCode;
    private String excessWeightQuantity;
    private String orderEntryNumber;
    private String productCode;
    private boolean excessWeightOnly;

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> queryParams = new HashMap<>();
        if (StringUtils.isNotEmpty(passengerCode))
            queryParams.put("passengerCode", passengerCode);
        if (StringUtils.isNotBlank(flightKey))
            queryParams.put("flightKey", flightKey);
        if (StringUtils.isNotBlank(excessWeightProductCode))
            queryParams.put("excessWeightProductCode", excessWeightProductCode);
        if (excessWeightQuantity != null)
            queryParams.put("excessWeightQuantity", excessWeightQuantity);
        if (orderEntryNumber != null)
            queryParams.put("orderEntryNumber", orderEntryNumber);
        if (productCode != null)
            queryParams.put("productCode", productCode);
        return queryParams;
    }
}
