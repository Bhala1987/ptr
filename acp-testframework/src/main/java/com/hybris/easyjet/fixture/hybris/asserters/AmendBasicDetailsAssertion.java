package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.database.hybris.models.FeesAndTaxesModel;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.Name;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AugmentedPriceItem;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.assertj.core.api.Assertions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.hybris.easyjet.config.constants.CommonConstants.*;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class AmendBasicDetailsAssertion extends Assertion<GetAmendableBookingAssertion, BasketConfirmationResponse> {

    /**
     * AmendBasicDetailsAssertion, parameterize constructor
     *
     * @param response
     */
    public AmendBasicDetailsAssertion(BasketConfirmationResponse response) {
        this.response = response;
    }

    /**
     * basketIsUpdated, it checks whether the basket has been updated or not
     *
     * @return
     */
    public AmendBasicDetailsAssertion basketIsUpdated() {
        assertThat(response.getOperationConfirmation().getBasketCode()).isNotEmpty();
        assertThat(response.getOperationConfirmation().getBasketCode()).isNotNull();
        return this;
    }

    public AmendBasicDetailsAssertion passengerDetailsAreUpdatedWith(List<Basket.Flights> basketFlights, int passengerIndex, String key, String value) {
        // Pass all flights here as they should be updated across the board.
        basketFlights.stream()
                .flatMap(flights -> flights.getFlights().stream())
                .forEach(flight -> {
                    try {
                        Basket.Passenger passenger = flight.getPassengers().get(passengerIndex);

                        assertThat(
                                MethodUtils.invokeExactMethod(
                                        passenger.getPassengerDetails(),
                                        "get" + StringUtils.capitalize(key)
                                )
                        ).isEqualTo(value);
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException(e);//NOSONAR
                    }
                });

        return this;
    }

    public AmendBasicDetailsAssertion verifyEJPlusHasBeenUpdated(Basket basket, String expectedMembership, String passengerCode) {
        String actualMembership = basket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null).getPassengerDetails().getEjPlusCardNumber();
        assertThat(Objects.isNull(actualMembership))
                .withFailMessage("EJPlus on passenger is currently null")
                .isFalse();
        assertThat(actualMembership.isEmpty())
                .withFailMessage("EJPlus on passenger is currently empty")
                .isFalse();
        assertThat(actualMembership)
                .withFailMessage("The expected value for EJPlus ".concat(actualMembership).concat(" does not match the expected value ".concat(expectedMembership)))
                .isEqualTo(expectedMembership);
        return this;
    }

    public AmendBasicDetailsAssertion verifyEJPlusHasBeenRemoved(Basket basket, String passengerCode) {
        String actualMembership = basket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null).getPassengerDetails().getEjPlusCardNumber();
        boolean isMembershipNull = Objects.isNull(actualMembership);
        boolean isMembershipEmpty = actualMembership.isEmpty();
        assertThat(isMembershipNull || isMembershipEmpty)
                .withFailMessage("EJPlus on passenger has not been removed")
                .isTrue();
        return this;
    }

    public AmendBasicDetailsAssertion verifyProductBundleHasBeenUpdate(Basket basket, String updateProduct, String fare, String passengerCode, int cabinItemSize) {
        if ("Standard".equalsIgnoreCase(fare)) {
            int actualCabinItemSize = basket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null).getCabinItems().size();
            int previousCabinItemSize = cabinItemSize;

            switch (updateProduct) {
                case "added":
                    assertThat(actualCabinItemSize)
                            .withFailMessage("The number of cabin item ".concat(String.valueOf(actualCabinItemSize)).concat(" are less then expected value ").concat(String.valueOf(previousCabinItemSize)))
                            .isGreaterThanOrEqualTo(previousCabinItemSize);
                    break;
                case "removed":
                    assertThat(actualCabinItemSize)
                            .withFailMessage("The number of cabin item ".concat(String.valueOf(actualCabinItemSize)).concat(" are greater then expected value ").concat(String.valueOf(previousCabinItemSize)))
                            .isLessThanOrEqualTo(previousCabinItemSize);
                    break;
                default:
                    break;
            }
        }
        return this;
    }

    public AmendBasicDetailsAssertion verifyProductPriceAfterUpdate(Basket basket, double price, double discount, String passengerCode) {
        AbstractPassenger.Seat actualSeat = basket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null).getSeat();
        double updatedPrice = price - discount;
        assertThat(actualSeat.getPricing().getBasePrice())
                .withFailMessage("Actual price of seat ".concat(String.valueOf(actualSeat.getPricing().getBasePrice())).concat(" is not the expected value ").concat(String.valueOf(price)))
                .isEqualTo(updatedPrice);
        return this;
    }

    public AmendBasicDetailsAssertion verifyWarningMessage(String warning, boolean present) {

        assertThat(
                this.response.getAdditionalInformations().stream().anyMatch(
                        warnings -> warnings.getCode().equalsIgnoreCase(warning)
                )
        ).withFailMessage(
                "EXPECTED : " + warning
        ).isEqualTo(present);

        return this;
    }

    public AmendBasicDetailsAssertion verifyTotalBasketHasBeenUpdated(Basket actualBasket, Basket previousBasket, String updateProduct) {
        switch (updateProduct) {
            case "added":
                assertThat(actualBasket.getTotalAmountWithCreditCard())
                        .withFailMessage("The expected total amount credit for basket ".concat(String.valueOf(actualBasket.getTotalAmountWithCreditCard())).concat(" is more than expected value ").concat(String.valueOf(previousBasket.getTotalAmountWithCreditCard())))
                        .isLessThan(previousBasket.getTotalAmountWithCreditCard());
                assertThat(actualBasket.getTotalAmountWithDebitCard())
                        .withFailMessage("The expected total amount debit for basket ".concat(String.valueOf(actualBasket.getTotalAmountWithCreditCard())).concat(" is more than expected value ").concat(String.valueOf(previousBasket.getTotalAmountWithCreditCard())))
                        .isLessThan(previousBasket.getTotalAmountWithDebitCard());
                break;
            case "removed":
                assertThat(actualBasket.getTotalAmountWithCreditCard())
                        .withFailMessage("The expected total amount credit for basket ".concat(String.valueOf(actualBasket.getTotalAmountWithCreditCard())).concat(" is less than expected value ").concat(String.valueOf(previousBasket.getTotalAmountWithCreditCard())))
                        .isGreaterThanOrEqualTo(previousBasket.getTotalAmountWithCreditCard());
                assertThat(actualBasket.getTotalAmountWithDebitCard())
                        .withFailMessage("The expected total amount debit for basket ".concat(String.valueOf(actualBasket.getTotalAmountWithCreditCard())).concat(" is less than expected value ").concat(String.valueOf(previousBasket.getTotalAmountWithCreditCard())))
                        .isGreaterThanOrEqualTo(previousBasket.getTotalAmountWithDebitCard());
                break;
            default:
                break;
        }
        return this;
    }

    public AmendBasicDetailsAssertion passengerNameAreUpdated(List<Basket.Passenger> passengers, Name name, Map<String, String> updatedFields) {
        passengers.forEach(passenger -> {
            for (Map.Entry<String, String> entry : updatedFields.entrySet()) {
                switch (entry.getKey()) {
                    case FIRSTNAME:
                        assertThat(passenger.getPassengerDetails().getName().getFirstName()).isEqualToIgnoringCase(name.getFirstName());
                        break;
                    case LASTNAME:
                        assertThat(passenger.getPassengerDetails().getName().getLastName()).isEqualToIgnoringCase(name.getLastName());
                        break;
                    case TITLE:
                        name.setTitle(entry.getValue());
                        assertThat(passenger.getPassengerDetails().getName().getTitle()).isEqualToIgnoringCase(name.getTitle());
                        break;
                    default:
                        break;
                }
            }

            assertThat(passenger.getApisStatus()).isEqualToIgnoringCase("Green");
        });

        return this;
    }

    public void passengerHasInfantOnLap(List<Basket.Flights> basketFlights) {
        boolean anyPassengerWithInfant = basketFlights.stream()
                .flatMap(flights -> flights.getFlights().stream())
                .flatMap(
                        flight -> flight.getPassengers().stream()
                ).anyMatch(
                        passenger -> !passenger.getInfantsOnLap().isEmpty()
                );

        assertThat(anyPassengerWithInfant).isTrue();
    }

    public AmendBasicDetailsAssertion verifyFeesAdded(Basket.Passenger passenger, FeesAndTaxesModel nameChangeFees, String condition) {
        AugmentedPriceItem priceItem = passenger.getFareProduct().getPricing().getFees().stream()
                .filter(fee -> fee.getCode().equalsIgnoreCase(nameChangeFees.getFeeCode() + "_" + passenger.getFareProduct().getOrderEntryNumber()))
                .findFirst().orElse(null);

        if (condition.equals(SHOULD)) {
            assertThat(priceItem)
                    .withFailMessage("The " + nameChangeFees.getFeeName() + " fee was not applied to the passenger")
                    .isNotNull();
            assertThat(priceItem.getAmount())
                    .withFailMessage(nameChangeFees.getFeeName() + " price is different than expected")
                    .isEqualTo(nameChangeFees.getFeeValue());
        } else {
            assertThat(priceItem)
                    .withFailMessage("The " + nameChangeFees.getFeeName() + " fee was applied to the passenger")
                    .isNull();
        }

        return this;
    }

    public AmendBasicDetailsAssertion verifyFeesAdded(Basket.Passenger passenger, FeesAndTaxesModel nameChangeFees, int times) {
        AugmentedPriceItem priceItem = passenger.getFareProduct().getPricing().getFees().stream()
                .filter(fee -> fee.getCode().equalsIgnoreCase(nameChangeFees.getFeeCode() + "_" + passenger.getFareProduct().getOrderEntryNumber()))
                .findFirst().orElse(null);

        assertThat(priceItem)
                .withFailMessage("The " + nameChangeFees.getFeeName() + " fee was not applied to the passenger")
                .isNotNull();

        assertThat(priceItem.getAmount())
                .withFailMessage(nameChangeFees.getFeeName() + " price is different than expected")
                .isEqualTo(new BigDecimal(nameChangeFees.getFeeValue().toString()).multiply(new BigDecimal(times)).doubleValue());

        return this;
    }
    public void assertTrue(boolean condition) {
        Assertions.assertThat(condition).isTrue();
    }
}
