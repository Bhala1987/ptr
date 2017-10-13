package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.helpers.PricingHelper;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;

import java.math.BigDecimal;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 12/04/2017.
 */
public class ManageAdditionalFareToPassengerInBasketAssertion extends Assertion<ManageAdditionalFareToPassengerInBasketAssertion, BasketConfirmationResponse> {

    public ManageAdditionalFareToPassengerInBasketAssertion(BasketConfirmationResponse manageAdditionalFareToPassengerInBasketResponse) {

        this.response = manageAdditionalFareToPassengerInBasketResponse;
    }

    public ManageAdditionalFareToPassengerInBasketAssertion basketOperationConfirmation(String basketCode) {
        assertThat(Objects.nonNull(response))
                .withFailMessage("The service was not called successful")
                .isEqualTo(true);
        assertThat(response.getOperationConfirmation().getBasketCode().equalsIgnoreCase(basketCode));
        return this;
    }

    public ManageAdditionalFareToPassengerInBasketAssertion verifySeatHasBeenRemovedFromPassenger(Basket basket, String passengerCode) {
        assertThat(Objects.isNull(basket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null).getAdditionalSeats()) || basket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null).getAdditionalSeats().isEmpty())
                .withFailMessage("The has not been removed from passenger " + passengerCode)
                .isEqualTo(true);
        return null;
    }

    public ManageAdditionalFareToPassengerInBasketAssertion verifyPriceForPassengerHasBeenUpdateAfterRemoving(BasketsResponse oldBasket, BasketsResponse newBasket, PricingHelper priceForSeat, String passengerCode) {
        BigDecimal oldPassengerPriceTotCredit = BigDecimal.valueOf(oldBasket.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null).getPassengerTotalWithCreditCard()).setScale(2);
        BigDecimal oldPassengerPriceTotDebit = BigDecimal.valueOf(oldBasket.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null).getPassengerTotalWithDebitCard()).setScale(2);

        BigDecimal newPassengerPriceTotCredit = BigDecimal.valueOf(newBasket.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null).getPassengerTotalWithCreditCard()).setScale(2);
        BigDecimal newPassengerPriceTotDebit = BigDecimal.valueOf(newBasket.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null).getPassengerTotalWithDebitCard()).setScale(2);

        BigDecimal seatPriceTotCredit = BigDecimal.valueOf(priceForSeat.getTotalAmountWithCreditCard()).setScale(2);
        BigDecimal seatPriceTotDebit = BigDecimal.valueOf(priceForSeat.getTotalAmountWithDebitCard()).setScale(2);

        assertThat(newPassengerPriceTotCredit).isEqualTo(oldPassengerPriceTotCredit.subtract(seatPriceTotCredit));
        assertThat(newPassengerPriceTotDebit).isEqualTo(oldPassengerPriceTotDebit.subtract(seatPriceTotDebit));

        return this;
    }

    public ManageAdditionalFareToPassengerInBasketAssertion verifyPriceForBasketHasBeenUpdateAfterRemoving(BasketsResponse oldBasket, BasketsResponse newBasket, PricingHelper priceForSeat) {
        BigDecimal oldBasketPriceTotCredit = BigDecimal.valueOf(oldBasket.getBasket().getTotalAmountWithCreditCard()).setScale(2);
        BigDecimal oldBasketPriceTotDebit = BigDecimal.valueOf(oldBasket.getBasket().getTotalAmountWithDebitCard()).setScale(2);

        BigDecimal newBasketPriceTotCredit = BigDecimal.valueOf(newBasket.getBasket().getTotalAmountWithCreditCard()).setScale(2);
        BigDecimal newBasketPriceTotDebit = BigDecimal.valueOf(newBasket.getBasket().getTotalAmountWithDebitCard()).setScale(2);

        BigDecimal seatPriceTotCredit = BigDecimal.valueOf(priceForSeat.getTotalAmountWithCreditCard()).setScale(2);
        BigDecimal seatPriceTotDebit = BigDecimal.valueOf(priceForSeat.getTotalAmountWithDebitCard()).setScale(2);

        assertThat(newBasketPriceTotCredit).isEqualTo(oldBasketPriceTotCredit.subtract(seatPriceTotCredit));
        assertThat(newBasketPriceTotDebit).isEqualTo(oldBasketPriceTotDebit.subtract(seatPriceTotDebit));

        return this;
    }
}