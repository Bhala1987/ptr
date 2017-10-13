package com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractConfirmation;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by giuseppecioce on 29/03/2017.
 */
public class PriceOverrideBasketResponse extends AbstractConfirmation<PriceOverrideBasketResponse.OperationConfirmation> {

    @Getter
    @Setter
    public static class OperationConfirmation extends BasketConfirmationResponse.OperationConfirmation {
        private String discountCode;
    }

}