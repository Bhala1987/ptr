package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class RemoveFlightFromBasketAssertion extends Assertion<RemoveFlightFromBasketAssertion, BasketConfirmationResponse> {

    public RemoveFlightFromBasketAssertion(BasketConfirmationResponse removeFlightFromBasketResponse) {

        this.response = removeFlightFromBasketResponse;
    }

    public RemoveFlightFromBasketAssertion basketOperationConfirmation(String basketCode) {
        assertThat(response.getOperationConfirmation().getBasketCode().equalsIgnoreCase(basketCode));
        return this;
    }
}
