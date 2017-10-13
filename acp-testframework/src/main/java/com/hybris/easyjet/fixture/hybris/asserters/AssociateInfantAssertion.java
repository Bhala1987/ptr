package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.database.hybris.models.PassengerStatus;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;

import java.util.Collection;
import java.util.IllformedLocaleException;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by robertadigiorgio on 12/05/2017.
 */
public class AssociateInfantAssertion extends Assertion<AssociateInfantAssertion, BasketConfirmationResponse> {

    public AssociateInfantAssertion(BasketConfirmationResponse associateInfantResponse) {

        this.response = associateInfantResponse;
    }

    public AssociateInfantAssertion confirmationAssociatedInfant(Basket basket, String passengerId, String passengerInfantId) {

        List<Basket.Flights> flights = basket.getOutbounds();

        assertThat(flights.stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .filter(basketPassenger -> basketPassenger.getCode().equals(passengerId))
                .map(Basket.Passenger::getInfantsOnLap).flatMap(Collection::stream)
                .anyMatch(infant -> infant.equals(passengerInfantId)))
                .withFailMessage("The Adult " + passengerId + "don't have the infant" + passengerInfantId)
                .isTrue();

        assertThat(flights.stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .filter(basketPassenger -> !basketPassenger.getCode().equals(passengerId))
                .map(Basket.Passenger::getInfantsOnLap).flatMap(Collection::stream)
                .anyMatch(infant -> infant.equals(passengerInfantId)))
                .withFailMessage("The another Adult have the infant" + passengerInfantId)
                .isFalse();

        return this;
    }

    public AssociateInfantAssertion associationInfantNotChange(Basket basket, String passengerId, String passengerInfantId) {

        List<Basket.Flights> flights = basket.getOutbounds();

        assertThat(flights.stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .filter(basketPassenger -> basketPassenger.getCode().equals(passengerId))
                .map(Basket.Passenger::getInfantsOnLap).flatMap(Collection::stream)
                .anyMatch(infant -> infant.equals(passengerInfantId)))
                .withFailMessage("The Adult " + passengerId + "have the infant" + passengerInfantId + " associated")
                .isFalse();
        return this;
    }

    public AssociateInfantAssertion checkAddExtraCabinBag(Basket basket, String passengerIdInfant, String fareType, String channel) { //NOSONAR

        List<Basket.Flights> flights = basket.getOutbounds();

        if ("Standard".equalsIgnoreCase(fareType)) {

            assertThat(flights.stream()
                    .map(Basket.Flights::getFlights)
                    .flatMap(Collection::stream)
                    .map(Basket.Flight::getPassengers)
                    .flatMap(Collection::stream)
                    .filter(basketPassenger -> basketPassenger.getCode().equals(passengerIdInfant))
                    .map(Basket.Passenger::getCabinItems)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList()).size() > 0)
                    .withFailMessage("The cabin bag doesn't exist")
                    .isTrue();
        } else if ("ADCustomerService".equalsIgnoreCase(channel)) {
            assertThat(flights.stream()
                    .map(Basket.Flights::getFlights)
                    .flatMap(Collection::stream)
                    .map(Basket.Flight::getPassengers)
                    .flatMap(Collection::stream)
                    .filter(basketPassenger -> basketPassenger.getCode().equals(passengerIdInfant))
                    .map(Basket.Passenger::getCabinItems)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList()).size() > 0)
                    .withFailMessage("The cabin bag doesn't exist")
                    .isTrue();
        } else {
            assertThat(flights.stream()
                    .map(Basket.Flights::getFlights)
                    .flatMap(Collection::stream)
                    .map(Basket.Flight::getPassengers)
                    .flatMap(Collection::stream)
                    .filter(basketPassenger -> basketPassenger.getCode().equals(passengerIdInfant))
                    .map(Basket.Passenger::getCabinItems)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList()).size() > 0)
                    .withFailMessage("The cabin bag should not be there")
                    .isFalse();
        }

        return this;
    }

    public AssociateInfantAssertion checkPassengerStatus(PassengerStatus originalPassengerStatus, PassengerStatus actualPassengerStatus) {
        assertThat(originalPassengerStatus)
                .withFailMessage("The passenger status info have been changed")
                .isEqualToComparingFieldByField(actualPassengerStatus);
        return this;
    }

    public AssociateInfantAssertion verifyAssociationInfantHasBeenUpdated(Basket basket, String newAssociationPassengerWitInfant, String oldAssociationPassengerWitInfant) {


        assertThat(basket.getOutbounds().stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .filter(basketPassenger -> basketPassenger.getCode().equals(newAssociationPassengerWitInfant))
                .findFirst()
                .orElseThrow(() -> new IllformedLocaleException("No passenger in basket with code " + newAssociationPassengerWitInfant))
                .getInfantsOnLap())
                .withFailMessage("The adult " + newAssociationPassengerWitInfant + " does not have the infant")
                .isNotEmpty();

        assertThat(basket.getOutbounds().stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .filter(basketPassenger -> basketPassenger.getCode().equals(oldAssociationPassengerWitInfant))
                .findFirst()
                .orElseThrow(() -> new IllformedLocaleException("No passenger in basket with code " + oldAssociationPassengerWitInfant))
                .getInfantsOnLap())
                .withFailMessage("The adult " + oldAssociationPassengerWitInfant + " does still have the infant")
                .isEmpty();

        return this;
    }

    public AssociateInfantAssertion verifyAssociationInfantHasBeenUpdatedBooking(GetBookingResponse.Booking bookingDetails, String newAssociationPassengerWitInfant, String oldAssociationPassengerWitInfant) {


        assertThat(bookingDetails.getOutbounds().stream()
                .map(GetBookingResponse.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(GetBookingResponse.Flight::getPassengers)
                .flatMap(Collection::stream)
                .filter(basketPassenger -> basketPassenger.getCode().equals(newAssociationPassengerWitInfant))
                .findFirst()
                .orElseThrow(() -> new IllformedLocaleException("No passenger in basket with code " + newAssociationPassengerWitInfant))
                .getInfantsOnLap())
                .withFailMessage("The adult " + newAssociationPassengerWitInfant + " does not have the infant")
                .isNotEmpty();

        assertThat(bookingDetails.getOutbounds().stream()
                .map(GetBookingResponse.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(GetBookingResponse.Flight::getPassengers)
                .flatMap(Collection::stream)
                .filter(basketPassenger -> basketPassenger.getCode().equals(oldAssociationPassengerWitInfant))
                .findFirst()
                .orElseThrow(() -> new IllformedLocaleException("No passenger in basket with code " + oldAssociationPassengerWitInfant))
                .getInfantsOnLap())
                .withFailMessage("The adult " + oldAssociationPassengerWitInfant + " does still have the infant")
                .isEmpty();

        return this;
    }

    public AssociateInfantAssertion confirmationAssociatedInfantOnSeat(Basket basket, String passengerId, String passengerInfantId) {

        List<Basket.Flights> flights = basket.getOutbounds();

        assertThat(flights.stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .filter(basketPassenger -> basketPassenger.getCode().equals(passengerId))
                .map(Basket.Passenger::getInfantsOnSeat).flatMap(Collection::stream)
                .anyMatch(infant -> infant.equals(passengerInfantId)))
                .withFailMessage("The Adult " + passengerId + "don't have the infant" + passengerInfantId)
                .isTrue();

        assertThat(flights.stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .filter(basketPassenger -> !basketPassenger.getCode().equals(passengerId))
                .map(Basket.Passenger::getInfantsOnSeat).flatMap(Collection::stream)
                .anyMatch(infant -> infant.equals(passengerInfantId)))
                .withFailMessage("The another Adult have the infant" + passengerInfantId)
                .isFalse();

        return this;
    }


}
