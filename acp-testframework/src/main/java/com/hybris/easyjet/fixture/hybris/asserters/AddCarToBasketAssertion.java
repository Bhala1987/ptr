package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;
import lombok.NoArgsConstructor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@NoArgsConstructor
public class AddCarToBasketAssertion extends Assertion<AddCarToBasketAssertion, BasketConfirmationResponse> {

    protected static Logger LOG = LogManager.getLogger(BasketsAssertion.class);

    public AddCarToBasketAssertion(BasketConfirmationResponse addCarToBasketResponse) {
        this.response = addCarToBasketResponse;
    }

    public AddCarToBasketAssertion theBasketHasAtleastOneCarProduct(BasketsResponse basketsResponse) {
        assertThat(basketsResponse.getBasket()
                .getCarHires()
                .size()
                ).isGreaterThan(0);
        return this;
    }

    public AddCarToBasketAssertion checkBasketTotals(Basket originalBasket, BasketsResponse basketsResponse) {
        double carHireTotalDebitCardAmount = basketsResponse.getBasket().getCarHires().stream().mapToDouble(carHirePrice -> carHirePrice.getPricing().getTotalAmountWithDebitCard()).sum();
        double carHireTotalCreditCardAmount = basketsResponse.getBasket().getCarHires().stream().mapToDouble(carHirePrice -> carHirePrice.getPricing().getTotalAmountWithCreditCard()).sum();
        assertThat(originalBasket.getTotalAmountWithDebitCard()).isEqualTo(carHireTotalDebitCardAmount + basketsResponse.getBasket().getTotalAmountWithDebitCard());
        assertThat(originalBasket.getTotalAmountWithCreditCard()).isEqualTo(carHireTotalCreditCardAmount + basketsResponse.getBasket().getTotalAmountWithCreditCard());
        return this;
    }
}
