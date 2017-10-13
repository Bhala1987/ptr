package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.helpers.PricingHelper;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by giuseppecioce on 01/03/2017.
 */
@NoArgsConstructor
public class AddSportEquipmentToBasketAssertion extends Assertion<AddSportEquipmentToBasketAssertion, BasketConfirmationResponse> {

    /**
     * @param addSportEquipmentResponse
     */
    public AddSportEquipmentToBasketAssertion(BasketConfirmationResponse addSportEquipmentResponse) {

        this.response = addSportEquipmentResponse;
    }

    public void setResponse(BasketConfirmationResponse response){
        this.response = response;
    }

    public AddSportEquipmentToBasketAssertion verifyBasketNotContainProductCode(BasketsResponse basketsResponse, String flightKey, String productCode) {

        List<Basket.Flight> flightAvailableInBasket = basketsResponse.getBasket()
                .getOutbounds()
                .stream()
                .flatMap(e -> e.getFlights().stream())
                .filter(h -> h.getFlightKey().equals(flightKey))
                .collect(Collectors.toList());
        for (Basket.Flight flight : flightAvailableInBasket) {
            List<Basket.Passenger> passengers = flight.getPassengers()
                    .stream()
                    .collect(Collectors.toList());
            for (Basket.Passenger passenger : passengers) {
                List<AbstractPassenger.HoldItem> items = passenger.getHoldItems()
                        .stream()
                        .collect(Collectors.toList());
                for (AbstractPassenger.HoldItem item : items) {
                    assertThat(item.getCode().equals(productCode) && (item.getQuantity() > 1)).isEqualTo(false);
                }
            }
        }
        flightAvailableInBasket = basketsResponse.getBasket()
                .getInbounds()
                .stream()
                .flatMap(e -> e.getFlights().stream())
                .filter(h -> h.getFlightKey().equals(flightKey))
                .collect(Collectors.toList());
        for (Basket.Flight flight : flightAvailableInBasket) {
            List<Basket.Passenger> passengers = flight.getPassengers()
                    .stream()
                    .collect(Collectors.toList());
            for (Basket.Passenger passenger : passengers) {
                List<AbstractPassenger.HoldItem> items = passenger.getHoldItems()
                        .stream()
                        .collect(Collectors.toList());
                for (AbstractPassenger.HoldItem item : items) {
                    assertThat(item.getCode().equals(productCode) && (item.getQuantity() > 1)).isEqualTo(false);
                }
            }
        }
        return this;
    }

    public AddSportEquipmentToBasketAssertion verifySportItemInTheBasket(String basketCode, BasketsResponse basketsResponse) {

        assertThat(response.getOperationConfirmation().getBasketCode().equals(basketCode)).isEqualTo(true);
        List<AbstractPassenger.HoldItem> cabinItems = basketsResponse.getBasket()
                .getOutbounds()
                .stream()
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .flatMap(h -> h.getHoldItems().stream())
                .collect(Collectors.toList());
        for (AbstractPassenger.HoldItem item : cabinItems) {
            assertThat(Objects.nonNull(item.getPricing().getBasePrice())).isEqualTo(true);
            assertThat(Objects.nonNull(item.getPricing().getTotalAmountWithDebitCard())).isEqualTo(true);
            assertThat(Objects.nonNull(item.getPricing().getTotalAmountWithCreditCard())).isEqualTo(true);
        }
        return this;
    }

    public AddSportEquipmentToBasketAssertion verifyOrderIsCreatedForEachPassenger(BasketsResponse basketsResponse, String passengerCode, String productCode) {

        List<Basket.Passenger> basketPassenger = basketsResponse.getBasket()
                .getOutbounds()
                .stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .filter(h -> h.getPassengerDetails().equals(passengerCode))
                .collect(Collectors.toList());
        for (Basket.Passenger passenger : basketPassenger) {
            List<AbstractPassenger.HoldItem> items = passenger.getHoldItems()
                    .stream()
                    .collect(Collectors.toList());
            for (AbstractPassenger.HoldItem item : items) {
                assertThat(item.getCode().equals(productCode)).isEqualTo(true);
            }
        }
        return this;
    }

    public AddSportEquipmentToBasketAssertion verifyBasketPriceIsUpdate(BasketsResponse basketsResponse, PricingHelper pricingHelper, String passengerCode, String productCode) {

        double prevValForTotDebit = pricingHelper.getTotalAmountWithDebitCard();
        double prevValForToCredit = pricingHelper.getTotalAmountWithCreditCard();
        List<Basket.Passenger> basketPassenger = basketsResponse.getBasket()
                .getOutbounds()
                .stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .filter(h -> h.getCode().equals(passengerCode))
                .collect(Collectors.toList());
        for (Basket.Passenger passenger : basketPassenger) {
            List<AbstractPassenger.HoldItem> items = passenger.getHoldItems()
                    .stream()
                    .filter(f -> f.getCode().equals(productCode))
                    .collect(Collectors.toList());
            for (AbstractPassenger.HoldItem item : items) {
                double productTotDebit = item.getPricing().getTotalAmountWithDebitCard();
                double productTotCredit = item.getPricing().getTotalAmountWithCreditCard();

                double finalValForTotDebit = basketsResponse.getBasket().getTotalAmountWithDebitCard();
                double finalValForTotCredit = basketsResponse.getBasket().getTotalAmountWithCreditCard();

                BigDecimal sumDebit = BigDecimal.valueOf(prevValForTotDebit + productTotDebit).setScale(2, RoundingMode.HALF_UP);
                BigDecimal sumCredit = BigDecimal.valueOf(prevValForToCredit + productTotCredit).setScale(2, RoundingMode.HALF_UP);

                assertThat(finalValForTotDebit == sumDebit.doubleValue()).isEqualTo(true);//NOSONAR
                assertThat(finalValForTotCredit == sumCredit.doubleValue()).isEqualTo(true);//nosonar
            }
        }
        return this;
    }

    public AddSportEquipmentToBasketAssertion verifyStockLevelDecrease(int actual, int previous, int quantity) {

        assertThat(actual == (previous + quantity)).isEqualTo(true);
        return this;
    }

    public AddSportEquipmentToBasketAssertion verifyStockLevelIsTheSame(int actual, int previous) {

        assertThat(actual == previous).isEqualTo(true);
        return this;
    }
}
