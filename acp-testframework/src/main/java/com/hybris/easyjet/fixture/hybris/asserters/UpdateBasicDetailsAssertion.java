package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.database.hybris.dao.CartDao;
import com.hybris.easyjet.database.hybris.models.FeesAndTaxesModel;
import com.hybris.easyjet.database.hybris.models.PassengerStatus;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractFlights;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AugmentedPriceItem;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.Name;
import lombok.NoArgsConstructor;
import net.thucydides.core.annotations.Step;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.PASSENGER_ID;
import static com.hybris.easyjet.config.SerenityFacade.getTestDataFromSpring;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by giuseppedimartino on 26/06/17.
 */
@NoArgsConstructor
public class UpdateBasicDetailsAssertion extends Assertion<UpdateBasicDetailsAssertion, BasketConfirmationResponse> {

    public UpdateBasicDetailsAssertion(BasketConfirmationResponse basketConfirmationResponse) {
        this.response = basketConfirmationResponse;
    }

    @Step("Title is updated: Passengers {1}, Title {2}")
    public UpdateBasicDetailsAssertion titleIsChangedForThePassenger(Basket basket, List<String> passengerIds, String expectedTitle) {
        basket.getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                .filter(passenger -> passengerIds.contains(passenger.getCode()))
                .map(AbstractPassenger::getPassengerDetails)
                .map(AbstractPassenger.PassengerDetails::getName)
                .map(Name::getTitle)
                .forEach(
                        actualTitle -> assertThat(actualTitle)
                                .withFailMessage("The title has not been updated")
                                .isEqualTo(expectedTitle)
                );

        return this;
    }

    @Step("Name is updated: Passengers {1}, Name {2}")
    public UpdateBasicDetailsAssertion nameIsChangedForThePassenger(Basket basket, List<String> passengerIds, String expectedName) {
        basket.getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                .filter(passenger -> passengerIds.contains(passenger.getCode()))
                .map(AbstractPassenger::getPassengerDetails)
                .map(AbstractPassenger.PassengerDetails::getName)
                .map(Name::getFirstName)
                .forEach(
                        actualName -> assertThat(actualName)
                                .withFailMessage("The name has not been updated")
                                .isEqualTo(expectedName)
                );

        return this;
    }

    @Step("Fee is applied: Price {2}")
    public UpdateBasicDetailsAssertion feePriceIsRight(Basket basket, List<String> passengerIds, FeesAndTaxesModel expectedFee) {
        basket.getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                .filter(passenger -> passengerIds.contains(passenger.getCode()))
                .forEach(
                        passenger -> {
                            AugmentedPriceItem nameFee = passenger.getFareProduct().getPricing().getFees().stream()
                                    .filter(fee -> fee.getCode().equals(expectedFee.getFeeCode() + "_" + passenger.getFareProduct().getOrderEntryNumber()))
                                    .findFirst().orElse(null);
                            assertThat(nameFee)
                                    .withFailMessage(expectedFee.getFeeName() + " has not been applied")
                                    .isNotNull();
                            assertThat(nameFee.getAmount())
                                    .withFailMessage(expectedFee.getFeeName() + " price is wrong: expected was " + expectedFee.getFeeValue() + "; actual is " + nameFee.getAmount())
                                    .isEqualTo(expectedFee.getFeeValue());
                        }
                );
        return this;
    }

    @Step("Fee is not applied")
    public UpdateBasicDetailsAssertion feeIsNotPresent(Basket basket, List<String> passengerIds, FeesAndTaxesModel expectedFee) {
        basket.getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                .filter(passenger -> passengerIds.contains(passenger.getCode()))
                .forEach(
                        passenger -> {
                            AugmentedPriceItem nameFee = passenger.getFareProduct().getPricing().getFees().stream()
                                    .filter(fee -> fee.getCode().equals(expectedFee.getFeeCode() + "_" + passenger.getFareProduct().getOrderEntryNumber()))
                                    .findFirst().orElse(null);
                            assertThat(nameFee)
                                    .withFailMessage(expectedFee.getFeeName() + " has been applied")
                                    .isNull();
                        }
                );
        return this;
    }

    @Step("Original passenger is inactive for flight {1}")
    public UpdateBasicDetailsAssertion originalPassengerIsSetToInactive(Basket basket, String flightKey, String passengerId) throws EasyjetCompromisedException {
        Basket.Passenger updatedPassenger = basket.getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                .filter(passenger -> passenger.getCode().equals(passengerId))
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("Passenger is not present in the basket"));

        assertThat(updatedPassenger.getActive())
                .withFailMessage("Passenger is still active")
                .isFalse();
        assertThat(updatedPassenger.getFareProduct().getActive())
                .withFailMessage("Passenger fare product is still active")
                .isFalse();
        updatedPassenger.getHoldItems().forEach(
                item -> {
                    assertThat(item.getActive())
                            .withFailMessage("Passenger hold item is still active")
                            .isFalse();
                    item.getExtraWeight().forEach(
                            extraWeight -> assertThat(extraWeight.getActive())
                                    .withFailMessage("Passenger extra weight is still active")
                                    .isFalse()
                    );
                }
        );
        updatedPassenger.getCabinItems().forEach(
                item -> assertThat(item.getActive())
                        .withFailMessage("Passenger cabin item is still active")
                        .isFalse()
        );
        updatedPassenger.getAdditionalItems().forEach(
                item -> assertThat(item.getActive())
                        .withFailMessage("Passenger additional item is still active")
                        .isFalse()
        );
        if (!Objects.isNull(updatedPassenger.getSeat())) {
            assertThat(updatedPassenger.getSeat().getActive())
                    .withFailMessage("Passenger seat is still active")
                    .isFalse();
        }
        updatedPassenger.getAdditionalSeats().forEach(
                item -> {
                    assertThat(item.getFareProduct().getActive())
                            .withFailMessage("Passenger additional seat is still active")
                            .isFalse();
                    if (!Objects.isNull(item.getSeat())) {
                        assertThat(item.getSeat().getActive())
                                .withFailMessage("Passenger additional seat seat is still active")
                                .isFalse();
                    }
                }
        );
        return this;
    }

    @Step("New infant on lap is added to the basket and assigned to first free adult for flight {1}")
    public UpdateBasicDetailsAssertion newInfantOnLapIsAddedToTheFlight(Basket basket, String flightKey) throws EasyjetCompromisedException {
        List<Basket.Passenger> infant = basket.getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .filter(flight -> flight.getFlightKey().equals(flightKey))
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("Flight " + flightKey + " is not present in the basket"))
                .getPassengers().stream()
                .filter(passenger -> passenger.getEntryStatus().equals("NEW"))
                .filter(passenger -> passenger.getPassengerDetails().getPassengerType().equals("infant"))
                .collect(Collectors.toList());

        assertThat(infant.size())
                .withFailMessage("No infant has been added to the basket")
                .isEqualTo(1);

        getTestDataFromSpring().setData(PASSENGER_ID, infant.get(0).getCode());

        assertThat(basket.getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .filter(flight -> flight.getFlightKey().equals(flightKey))
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("The flight is not present in the basket"))
                .getPassengers().stream()
                .filter(passenger -> passenger.getPassengerDetails().getPassengerType().equals("adult"))
                .filter(passenger -> passenger.getInfantsOnLap().contains(infant.get(0).getCode()))
                .collect(Collectors.toList()).size())
                .withFailMessage("The new infant is not assigned to any adult in the basket")
                .isEqualTo(1);
        return this;
    }

    @Step("{0} status is correct for flight {1}: Expected status {2}, Actual status {3}")
    public UpdateBasicDetailsAssertion passengerStatusIsCorrect(String statusPosition, String flightKey, PassengerStatus expectedPassengerStatus, PassengerStatus actualPassengerStatus) {
        assertThat(actualPassengerStatus.getConsignmentStatus())
                .withFailMessage("The consignment status is wrong")
                .isEqualTo(expectedPassengerStatus.getConsignmentStatus());

        assertThat(actualPassengerStatus.getApisStatus())
                .withFailMessage("The APIS status is wrong")
                .isEqualTo(expectedPassengerStatus.getApisStatus());

        assertThat(actualPassengerStatus.getIctsStatus())
                .withFailMessage("The ICTS status is wrong")
                .isEqualTo(expectedPassengerStatus.getIctsStatus());
        return this;
    }

    // This method need newInfantOnLapIsAddedToTheFlight to be executed to set the correct infantCode
    @Step("New infant status for flight {1} is correct")
    public UpdateBasicDetailsAssertion newInfantHasTheSameStatusHasTheOriginalPassenger(Basket basket, String flightKey, PassengerStatus expectedPassengerStatus, CartDao cartDao) {
        String infantCode = getTestDataFromSpring().getData(PASSENGER_ID);
        PassengerStatus infantPassengerStatus = cartDao.getCartPassengerStatus(basket.getCode(), infantCode);
        expectedPassengerStatus.setConsignmentStatus("BOOKED");

        passengerStatusIsCorrect("Cart", flightKey, expectedPassengerStatus, infantPassengerStatus);

        return this;
    }

}