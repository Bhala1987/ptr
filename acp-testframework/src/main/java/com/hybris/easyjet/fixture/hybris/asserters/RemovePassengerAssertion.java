package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by robertadigiorgio on 13/04/2017.
 */
public class RemovePassengerAssertion extends Assertion<ResetPasswordAssertion, BasketConfirmationResponse> {

    public RemovePassengerAssertion(BasketConfirmationResponse removePassengerResponse) {

        this.response = removePassengerResponse;
    }

    public RemovePassengerAssertion confirmation(String basketId) {

        String basketCode = response.getOperationConfirmation().getBasketCode();
        assertThat(basketId).isEqualTo(basketCode);
        return this;
    }

    public RemovePassengerAssertion fieldIsEmpty(Integer row) {

        assertThat(row).isEqualTo(0);
        return this;
    }

    public RemovePassengerAssertion checkThatPassengerIsRemoved(Basket basket, String passengerIdRemoved, String newPassengerId, String infantId) {
        List<Basket.Flights> flights = basket.getOutbounds();

        assertThat(flights.stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .anyMatch(passenger -> passenger.getCode().equals(passengerIdRemoved)))
                .withFailMessage("The passenger is still present in the Basket ")
                .isFalse();

        assertThat(flights.stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .filter(basketPassenger -> basketPassenger.getCode().equals(newPassengerId))
                .map(Basket.Passenger::getInfantsOnLap).flatMap(Collection::stream)
                .anyMatch(infant -> infant.equals(infantId)))
                .withFailMessage("The Adult don't have the infant")
                .isTrue();

        return this;
    }
}
