package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.helpers.PricingHelper;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.PriceOverrideBasketResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AugmentedPriceItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by giuseppecioce on 29/03/2017.
 */
public class PriceOverrideAssertion extends Assertion<PriceOverrideAssertion, PriceOverrideBasketResponse> {

    public PriceOverrideAssertion(PriceOverrideBasketResponse priceOverrideBasketResponse) {
        this.response = priceOverrideBasketResponse;
    }

    public PriceOverrideAssertion verifyDiscountIsAppliedOnBasketLevel(BasketsResponse basketsResponse, String discountReason) {
        assertThat(basketsResponse.getBasket().getDiscounts().getItems().stream().map(f -> f.getDiscountReasonCode()).findFirst().orElse(null)).isEqualTo(discountReason);
        return this;
    }

    public PriceOverrideAssertion theTotalPriceOfBasketHasBeenUpdated(BasketsResponse basketsResponse, PricingHelper previousPrice, double amountDiscount) {
        BigDecimal actualTotDebitCard = new BigDecimal(basketsResponse.getBasket().getTotalAmountWithDebitCard()).setScale(2, RoundingMode.HALF_UP);

        BigDecimal previousTotDebitCard = new BigDecimal(previousPrice.getTotalAmountWithDebitCard()).setScale(2, RoundingMode.HALF_UP);

        assertThat(actualTotDebitCard).isEqualTo(previousTotDebitCard.add(BigDecimal.valueOf(amountDiscount)).setScale(2, RoundingMode.HALF_UP));

        return this;
    }

    public PriceOverrideAssertion theTotalPriceOfBasketHasBeenUpdatedOnProductLevel(BasketsResponse basketsResponse, PricingHelper previousPrice, double amountDiscount) {
        BigDecimal actualTotDebitCard = new BigDecimal(basketsResponse.getBasket().getTotalAmountWithDebitCard()).setScale(2, RoundingMode.HALF_UP);

        BigDecimal previousTotDebitCard = new BigDecimal(previousPrice.getTotalAmountWithDebitCard()).setScale(2, RoundingMode.HALF_UP);

        assertThat(actualTotDebitCard).isEqualTo(previousTotDebitCard.subtract(BigDecimal.valueOf(amountDiscount)).setScale(2, RoundingMode.HALF_UP));

        return this;
    }

    public PriceOverrideAssertion verifyDiscountIsAppliedOnPassengerLevel(BasketsResponse basketsResponse, String discountReason, List<Basket.Passenger> passengers) {
        List<AugmentedPriceItem> augmentedPriceItemDiscount = basketsResponse.getBasket().getDiscounts().getItems().stream().filter(f -> f.getDiscountReasonCode().equals(discountReason)).collect(Collectors.toList());
        assertThat(augmentedPriceItemDiscount.stream().map(f -> f.getPassengerCode()).collect(Collectors.toList()).containsAll(passengers.stream().map(g -> g.getCode()).collect(Collectors.toList()))).isEqualTo(true);
        return this;
    }

    public PriceOverrideAssertion verifyDiscountHasBeenRemovedOnBasketLevel(BasketsResponse basketsResponse, String discountReason) {
        assertThat(basketsResponse.getBasket().getDiscounts().getItems().stream().map(f -> f.getDiscountReasonCode()).findFirst().orElse(null)).isNotEqualTo(discountReason);
        return this;
    }

    public PriceOverrideAssertion verifyDiscountIsAppliedOnProductLevel(String discountReason, List<Basket.Passenger> passengers, Map passengerProductCode) {
        for (Basket.Passenger passenger : passengers) {
            String productCode = (String) passengerProductCode.get(passenger.getCode());
            List<AbstractPassenger.HoldItem> holdItem = passenger.getHoldItems().stream().filter(f -> f.getCode().equals(productCode)).collect(Collectors.toList());
            assertThat(holdItem.stream().flatMap(f -> f.getPricing().getDiscounts().stream()).filter(g -> g.getDiscountReasonCode().equals(discountReason)).findFirst().orElse(null)).isNotNull();
        }
        return this;
    }

    public PriceOverrideAssertion verifyDiscountIsRemovedOnProductLevel(String discountReason, List<Basket.Passenger> passengers, Map passengerProductCode) {
        for (Basket.Passenger passenger : passengers) {
            String productCode = (String) passengerProductCode.get(passenger.getCode());
            List<AbstractPassenger.HoldItem> holdItem = passenger.getHoldItems().stream().filter(f -> f.getCode().equals(productCode)).collect(Collectors.toList());
            assertThat(holdItem.stream().flatMap(f -> f.getPricing().getDiscounts().stream()).filter(g -> g.getDiscountReasonCode().equals(discountReason)).findFirst().orElse(null)).isNull();
        }
        return this;
    }

    public PriceOverrideAssertion verifyThePriceOfProductHasBeenUpdated(BasketsResponse basketsResponse, String productCode, double amount, Map<String, PricingHelper> passengerProductCode) {
        passengerProductCode.forEach((k, v) -> {
            BigDecimal previousValueDebit = BigDecimal.valueOf(v.getTotalAmountWithDebitCard()).setScale(2, RoundingMode.HALF_UP);

            AbstractPassenger.HoldItem item = basketsResponse.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equals(k)).flatMap(i -> i.getHoldItems().stream()).filter(l -> l.getCode().equals(productCode)).findFirst().orElse(null);
            BigDecimal actualValueDebit = new BigDecimal(item.getPricing().getTotalAmountWithDebitCard()).setScale(2, RoundingMode.HALF_UP);

            assertThat(actualValueDebit).isEqualTo(previousValueDebit.add(BigDecimal.valueOf(amount)).setScale(2, RoundingMode.HALF_UP));
        });
        return this;
    }

    public PriceOverrideAssertion verifyThePriceOfProductHasBeenDecreased(BasketsResponse basketsResponse, String productCode, double amount, Map<String, PricingHelper> passengerProductCode) {
        passengerProductCode.forEach((k, v) -> {
            BigDecimal previousValueDebit = BigDecimal.valueOf(v.getTotalAmountWithDebitCard()).setScale(2, RoundingMode.HALF_UP);

            AbstractPassenger.HoldItem item = basketsResponse.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equals(k)).flatMap(i -> i.getHoldItems().stream()).filter(l -> l.getCode().equals(productCode)).findFirst().orElse(null);
            BigDecimal actualValueDebit = new BigDecimal(item.getPricing().getTotalAmountWithDebitCard()).setScale(2, RoundingMode.HALF_UP);

            assertThat(actualValueDebit).isEqualTo(previousValueDebit.subtract(BigDecimal.valueOf(amount)).setScale(2, RoundingMode.HALF_UP));
        });
        return this;
    }

    public PriceOverrideAssertion verifyThePriceOfThePassengerHasBeenDecreased(BasketsResponse basketsResponse, double amount, Map<String, PricingHelper> passengerProductCode) {
        passengerProductCode.forEach((k, v) -> {

            Basket.Passenger passenger = basketsResponse.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equals(k)).findFirst().orElse(null);

            BigDecimal previousValueCredit = new BigDecimal(passengerProductCode.get(k).getTotalAmountWithCreditCard()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal previousValueDebit = new BigDecimal(passengerProductCode.get(k).getTotalAmountWithDebitCard()).setScale(2, RoundingMode.HALF_UP);

            BigDecimal actualValueCredit = new BigDecimal(passenger.getPassengerTotalWithCreditCard()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal actualValueDebit = new BigDecimal(passenger.getPassengerTotalWithDebitCard()).setScale(2, RoundingMode.HALF_UP);

            assertThat(actualValueCredit).isEqualTo(previousValueCredit.subtract(BigDecimal.valueOf(amount)).setScale(2, RoundingMode.HALF_UP));
            assertThat(actualValueDebit).isEqualTo(previousValueDebit.subtract(BigDecimal.valueOf(amount)).setScale(2, RoundingMode.HALF_UP));

        });
        return this;
    }
}