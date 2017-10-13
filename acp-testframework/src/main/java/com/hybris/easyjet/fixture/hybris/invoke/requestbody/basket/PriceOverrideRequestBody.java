package com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by giuseppecioce on 29/03/2017.
 */
@Builder
@Getter
@Setter
public class PriceOverrideRequestBody implements IRequestBody {
    private String passengerCode;
    private boolean isApplicableToAllPassengers;
    private String productCode;
    private String reasonCode;
    private String comment;
    private double overrideTotalAmount;
    private String feeChargeCode;
}
