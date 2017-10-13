package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by giuseppedimartino on 30/03/17.
 */
public class RemoveHoldBagProductAssertion extends Assertion<RemoveHoldBagProductAssertion, BasketConfirmationResponse> {

    public RemoveHoldBagProductAssertion(BasketConfirmationResponse removeHoldBagProductResponse) {
        this.response = removeHoldBagProductResponse;
    }

    public RemoveHoldBagProductAssertion theOperationIsConfirmed(String code) {

        assertThat(response.getOperationConfirmation().getBasketCode()).isEqualTo(code);

        return this;
    }

    public RemoveHoldBagProductAssertion stockLevelIsReduced(int oldStockLevel, int newStockLevel) {

        assertThat(oldStockLevel).isEqualTo(newStockLevel);

        return this;
    }
}