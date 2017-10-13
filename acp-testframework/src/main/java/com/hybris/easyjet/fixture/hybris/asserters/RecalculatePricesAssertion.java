package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.basket.RecalculatePricesResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AdditionalInformation;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 08/05/2017.
 */
public class RecalculatePricesAssertion extends Assertion<RecalculatePricesAssertion, RecalculatePricesResponse> {

    public RecalculatePricesAssertion(RecalculatePricesResponse recalculatePricesResponse) {

        this.response = recalculatePricesResponse;
    }

    public RecalculatePricesAssertion basketOperationConfirmation(String basketCode) {

        if (basketCode != null) {
            assertThat(response.getOperationConfirmation().getBasketCode().equalsIgnoreCase(basketCode));

        } else {
            assertThat(response.getBasket()).isNotNull();
        }
        return this;
    }

    public RecalculatePricesAssertion OperationConfirmation() {

        assertThat(response.getBasket()).isNotNull();
        return this;
    }

    public RecalculatePricesAssertion compairBasketContent(Basket oldBasket, Basket newBasket) {

        assertThat(oldBasket.getTotalAmountWithCreditCard().equals(newBasket.getTotalAmountWithCreditCard())).isTrue();
        assertThat(oldBasket.getTotalAmountWithDebitCard().equals(newBasket.getTotalAmountWithDebitCard())).isTrue();
        verifyUpdatedBasket(oldBasket, newBasket);

        return this;
    }

    public RecalculatePricesAssertion verifyUpdatedBasket(Basket oldBasket, Basket newBasket) {

        assertThat(oldBasket.getTotalAmountWithCreditCard().equals(newBasket.getTotalAmountWithCreditCard())).isTrue();
        assertThat(oldBasket.getTotalAmountWithDebitCard().equals(newBasket.getTotalAmountWithDebitCard())).isTrue();
        List<Basket.Passenger> basketPassenger = oldBasket.getOutbounds().stream().flatMap(outbound -> outbound.getFlights().stream().flatMap(flight -> flight.getPassengers().stream())).collect(Collectors.toList());
        List<Basket.Passenger> basketPassengerNew = newBasket.getOutbounds().stream().flatMap(outbound -> outbound.getFlights().stream().flatMap(flight -> flight.getPassengers().stream())).collect(Collectors.toList());

        for (int index = 0; index < basketPassenger.size(); index++) {
            assertThat(basketPassenger.get(index).getFareProduct().getPricing().getBasePrice().
                    equals(basketPassengerNew.get(index).getFareProduct().getPricing().getBasePrice())).isTrue();
        }

        return this;
    }

    public void verifyFlightIsRemoved(String flightKey, String journey) {
        if ("outbound".equalsIgnoreCase(journey)) {
            response.getBasket().getOutbounds().stream()
                    .flatMap(outbound -> outbound.getFlights().stream())
                    .forEach(flight ->
                                assertThat(flight.getFlightKey().equals(flightKey)).isFalse()
                    );
        } else {
            response.getBasket().getInbounds().stream()
                    .flatMap(outbound -> outbound.getFlights().stream())
                    .forEach(flight ->
                        assertThat(flight.getFlightKey().equals(flightKey)).isFalse());
        }
    }

    public void verifyInformationInAffectedData(List<String> productTypes) {
        final List<AdditionalInformation.AffectedData> affectedData =
                response.getAdditionalInformations().stream().flatMap(additionalInformation -> additionalInformation.getAffectedData().stream()).collect(Collectors.toList());
       assertThat(affectedData.size()).isNotZero();
        for (String type:productTypes
             ) {
            switch (type)
            {
                case "HoldBagProduct" :
                    boolean holdItems = affectedData.stream().anyMatch(affectedData1 -> affectedData1.getDataName().contains("holdItems")
                    );
                    assertThat(holdItems).isTrue();
                    break;
                case "ExcessWeightProduct" :
                    boolean excessWeight = affectedData.stream().anyMatch(affectedData1 -> affectedData1.getDataName().contains("excessWeights")
                    );
                    assertThat(excessWeight).isTrue();
                    break;
                case "SmallSportsProduct" :
                case "LargeSportsProduct" :
                    boolean sportEquipments = affectedData.stream().anyMatch(affectedData1 -> affectedData1.getDataName().contains("sportEquipments")
                    );
                    assertThat(sportEquipments).isTrue();
                    break;
                case "seat" :
                    affectedData.forEach(affectedData1 -> assertThat(affectedData1.getDataName().contains("seats")).isTrue());
                    break;
                default:break;

            }

        }
    }
}