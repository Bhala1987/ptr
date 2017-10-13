package com.hybris.easyjet.fixture.hybris.invoke.queryparams;

import com.hybris.easyjet.fixture.IQueryParams;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dwebb on 11/9/2016.
 * this class offers builder or standard getter/setter construction for query parameters for the getPaymentMethods service
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class PaymentMethodsQueryParams extends QueryParameters implements IQueryParams {

    private String customerId;
    private String basketId;
    private String bookingTypeCode;
    private String bookingType;


    /**
     * get a list of parameters set
     *
     * @return a map of parameters which can be used by the jersey client
     */
    public Map<String, String> getParameters() {

        Map<String, String> queryParams = new HashMap<>();
        if (isPopulated(customerId)) {
            queryParams.put("customer-id", customerId);
        }
        if (isPopulated(basketId)) {
            queryParams.put("basket-id", basketId);
        }
        if (isPopulated(bookingTypeCode)) {
            queryParams.put("booking-type", bookingTypeCode);
        }
        if (isPopulated(bookingType)) {
            queryParams.put("booking-type", bookingType);
        }


        return queryParams;
    }

}


