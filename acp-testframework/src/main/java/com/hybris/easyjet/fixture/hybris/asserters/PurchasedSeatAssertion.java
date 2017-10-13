package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.PricingHelper;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.purchasedseats.PassengerSeatChangeRequests;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.purchasedseats.Seat;
import com.hybris.easyjet.fixture.hybris.invoke.response.GetSeatMapResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import lombok.NoArgsConstructor;
import org.assertj.core.api.Assertions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by giuseppecioce on 12/04/2017.
 */
@NoArgsConstructor
public class PurchasedSeatAssertion extends Assertion<PurchasedSeatAssertion, BasketConfirmationResponse> {


    public PurchasedSeatAssertion(BasketConfirmationResponse purchasedSeatResponse) {
        this.response = purchasedSeatResponse;
    }

    public PurchasedSeatAssertion verifySuccessfullyResponseAfterRemoveSeat(String basketCode) {
        assertThat(
                response.getOperationConfirmation().getBasketCode().isEmpty())
                .withFailMessage("The basket type for operation confirmation is empty!")
                .isFalse();

        assertThat(
                response.getOperationConfirmation().getBasketCode().equals(basketCode))
                .withFailMessage("The operation confirmation does not contain the desired basket type: " + basketCode)
                .isTrue();

        return this;
    }

    public PurchasedSeatAssertion basketTotalsAreUpdatedAfterPurchasingSeatProduct(Basket oldBasket, Basket newBasket, GetSeatMapResponse.Product seatProduct) {
        basketDebitTotalsAreUpdatedAfterPurchasingSeatProduct(oldBasket, newBasket, seatProduct);
        basketCreditTotalsAreUpdatedAfterPurchasingSeatProduct(oldBasket, newBasket, seatProduct);
        return this;
    }

    private PurchasedSeatAssertion basketDebitTotalsAreUpdatedAfterPurchasingSeatProduct(Basket oldBasket, Basket newBasket, GetSeatMapResponse.Product seatProduct) {
        Double myOriginalTotalDebit = oldBasket.getTotalAmountWithDebitCard();
        Double mySeatProductPriceDebit = seatProduct.getOfferPrices().getWithDebitCardFee();
        Double myExpectedNewTotalDebit = myOriginalTotalDebit + mySeatProductPriceDebit;

        Double myActualNewTotalDebit = newBasket.getTotalAmountWithDebitCard();

        assertThat(myActualNewTotalDebit).isEqualTo(myExpectedNewTotalDebit).withFailMessage("Basket debit card total not updated with purchased seat price");

        return this;
    }

    private PurchasedSeatAssertion basketCreditTotalsAreUpdatedAfterPurchasingSeatProduct(Basket oldBasket, Basket newBasket, GetSeatMapResponse.Product seatProduct) {
        Double myOriginalTotalCredit = oldBasket.getTotalAmountWithCreditCard();
        Double mySeatProductPriceCredit = seatProduct.getOfferPrices().getWithCreditCardFee();
        Double myExpectedNewTotalCredit = myOriginalTotalCredit + mySeatProductPriceCredit;

        Double myActualNewTotalCredit = newBasket.getTotalAmountWithCreditCard();

        assertThat(myActualNewTotalCredit).isEqualTo(myExpectedNewTotalCredit).withFailMessage("Basket credit card total not updated with purchased seat price");

        return this;
    }

    public PurchasedSeatAssertion verifyAllSeatHasBeenRemovedFromPassengers(BasketsResponse basketsResponse, String passengerCode) {
        List<Basket.Passenger> basketPassengers = basketsResponse.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream())
                .filter(passenger -> passenger.getCode().equalsIgnoreCase(passengerCode))
                .collect(Collectors.toList());
        for (Basket.Passenger passenger : basketPassengers) {
            assertThat(Objects.isNull(passenger.getSeat()))
                    .withFailMessage("Unexpected seat on passenger " + passenger.getCode())
                    .isTrue();
        }
        return this;
    }

    public PurchasedSeatAssertion verifySeatHasBeenRemovedFromPassenger(Basket basket, String passengerCode) {
        Basket.Passenger basketPassenger = basket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null);
        assertThat(Objects.nonNull(basketPassenger))
                .withFailMessage("No passenger in basket with desired code " + passengerCode)
                .isTrue();
        assertThat(Objects.isNull(basketPassenger.getSeat()))
                .withFailMessage("Unexpected seat on passenger " + basketPassenger.getCode())
                .isTrue();

        return this;
    }

    public PurchasedSeatAssertion verifyPriceForPassengerHasBeenUpdateAfterRemoving(BasketsResponse oldBasket, BasketsResponse newBasket, PricingHelper priceForSeat, String passengerCode) {
        BigDecimal oldPassengerPriceTotCredit = BigDecimal.valueOf(oldBasket.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null).getPassengerTotalWithCreditCard()).setScale(2);
        BigDecimal oldPassengerPriceTotDebit = BigDecimal.valueOf(oldBasket.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null).getPassengerTotalWithDebitCard()).setScale(2);

        BigDecimal newPassengerPriceTotCredit = BigDecimal.valueOf(newBasket.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null).getPassengerTotalWithCreditCard()).setScale(2);
        BigDecimal newPassengerPriceTotDebit = BigDecimal.valueOf(newBasket.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null).getPassengerTotalWithDebitCard()).setScale(2);

        BigDecimal seatPriceTotCredit = BigDecimal.valueOf(priceForSeat.getTotalAmountWithCreditCard()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal seatPriceTotDebit = BigDecimal.valueOf(priceForSeat.getTotalAmountWithDebitCard()).setScale(2, RoundingMode.HALF_UP);

        assertThat(newPassengerPriceTotCredit).isEqualTo(oldPassengerPriceTotCredit.subtract(seatPriceTotCredit));
        assertThat(newPassengerPriceTotDebit).isEqualTo(oldPassengerPriceTotDebit.subtract(seatPriceTotDebit));

        return this;
    }

    public PurchasedSeatAssertion verifyPriceForBasketHasBeenUpdateAfterRemoving(Basket oldBasket, Basket newBasket, PricingHelper priceForSeat) {
        BigDecimal oldBasketPriceTotCredit = BigDecimal.valueOf(oldBasket.getTotalAmountWithCreditCard()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal oldBasketPriceTotDebit = BigDecimal.valueOf(oldBasket.getTotalAmountWithDebitCard()).setScale(2, RoundingMode.HALF_UP);

        BigDecimal newBasketPriceTotCredit = BigDecimal.valueOf(newBasket.getTotalAmountWithCreditCard()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal newBasketPriceTotDebit = BigDecimal.valueOf(newBasket.getTotalAmountWithDebitCard()).setScale(2, RoundingMode.HALF_UP);

        BigDecimal seatPriceTotCredit = BigDecimal.valueOf(priceForSeat.getTotalAmountWithCreditCard()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal seatPriceTotDebit = BigDecimal.valueOf(priceForSeat.getTotalAmountWithDebitCard()).setScale(2, RoundingMode.HALF_UP);

        assertThat(newBasketPriceTotCredit).isEqualTo(oldBasketPriceTotCredit.subtract(seatPriceTotCredit));
        assertThat(newBasketPriceTotDebit).isEqualTo(oldBasketPriceTotDebit.subtract(seatPriceTotDebit));

        return this;
    }

    public PurchasedSeatAssertion verfiyNewConfigurationHasBeenStored(Basket basket, Map associationPassengerSeat, Double discountToApply) {

        basket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).collect(Collectors.toList()).stream().forEach(passenger -> {
            PassengerSeatChangeRequests.Seat expectedToPassenger = (PassengerSeatChangeRequests.Seat) associationPassengerSeat.get(passenger.getCode());

            assertThat(Objects.nonNull(passenger.getSeat()))
                    .withFailMessage("No seat are found for passenger " + passenger.getCode())
                    .isEqualTo(true);

            AbstractPassenger.Seat actualToPassenger = passenger.getSeat();

            BigDecimal expectedPriceSeat = BigDecimal.valueOf(expectedToPassenger.getPrice()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal actualPriceSeat = BigDecimal.valueOf(actualToPassenger.getPricing().getBasePrice()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal eventualDiscount = BigDecimal.valueOf(discountToApply).setScale(2, RoundingMode.HALF_UP);

            assertThat((expectedPriceSeat.subtract(eventualDiscount)).equals(actualPriceSeat))
                    .withFailMessage("The actual price " + (expectedPriceSeat.subtract(eventualDiscount)) + " does not match the expected value " + actualPriceSeat)
                    .isEqualTo(true);
            assertThat(expectedToPassenger.getSeatNumber().equals(actualToPassenger.getSeatNumber()))
                    .withFailMessage("The actual seat number " + expectedToPassenger.getSeatNumber() + " does not match the expected value " + actualToPassenger.getSeatNumber())
                    .isEqualTo(true);
        });
        return this;
    }

    public PurchasedSeatAssertion verifyPriceForPassengerHasBeenUpdateAfterChanging(BasketsResponse oldBasket, Basket newBasket, PricingHelper priceForSeat, String passengerCode) {
        BigDecimal oldPassengerPriceTotCredit = BigDecimal.valueOf(oldBasket.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null).getPassengerTotalWithCreditCard()).setScale(2);
        BigDecimal oldPassengerPriceTotDebit = BigDecimal.valueOf(oldBasket.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null).getPassengerTotalWithDebitCard()).setScale(2);

        BigDecimal newPassengerPriceTotCredit = BigDecimal.valueOf(newBasket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null).getPassengerTotalWithCreditCard()).setScale(2);
        BigDecimal newPassengerPriceTotDebit = BigDecimal.valueOf(newBasket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null).getPassengerTotalWithDebitCard()).setScale(2);

        BigDecimal seatPriceTotCredit = BigDecimal.valueOf(priceForSeat.getTotalAmountWithCreditCard()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal seatPriceTotDebit = BigDecimal.valueOf(priceForSeat.getTotalAmountWithDebitCard()).setScale(2, RoundingMode.HALF_UP);

        assertThat(newPassengerPriceTotCredit).isEqualTo(oldPassengerPriceTotCredit.add(seatPriceTotCredit));
        assertThat(newPassengerPriceTotDebit).isEqualTo(oldPassengerPriceTotDebit.add(seatPriceTotDebit));

        return this;
    }

    public PurchasedSeatAssertion verifyPriceForFlightHasBeenUpdateAfterChanging(BasketsResponse oldBasket, Basket newBasket, PricingHelper priceForSeat) {
        BigDecimal oldBasketPriceTotCredit = BigDecimal.valueOf(oldBasket.getBasket().getOutbounds().get(0).getJourneyTotalWithCreditCard()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal oldBasketPriceTotDebit = BigDecimal.valueOf(oldBasket.getBasket().getOutbounds().get(0).getJourneyTotalWithDebitCard()).setScale(2, RoundingMode.HALF_UP);

        BigDecimal newBasketPriceTotCredit = BigDecimal.valueOf(newBasket.getOutbounds().get(0).getJourneyTotalWithCreditCard()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal newBasketPriceTotDebit = BigDecimal.valueOf(newBasket.getOutbounds().get(0).getJourneyTotalWithDebitCard()).setScale(2, RoundingMode.HALF_UP);

        BigDecimal seatPriceTotCredit = BigDecimal.valueOf(priceForSeat.getTotalAmountWithCreditCard()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal seatPriceTotDebit = BigDecimal.valueOf(priceForSeat.getTotalAmountWithDebitCard()).setScale(2, RoundingMode.HALF_UP);

        assertThat(newBasketPriceTotCredit).isEqualTo(oldBasketPriceTotCredit.add(seatPriceTotCredit));
        assertThat(newBasketPriceTotDebit).isEqualTo(oldBasketPriceTotDebit.add(seatPriceTotDebit));

        return this;
    }

    public PurchasedSeatAssertion verifyPriceForBasketHasBeenUpdateAfterChanging(BasketsResponse oldBasket, Basket newBasket, PricingHelper priceForSeat) {
        BigDecimal oldBasketPriceTotCredit = BigDecimal.valueOf(oldBasket.getBasket().getTotalAmountWithCreditCard()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal oldBasketPriceTotDebit = BigDecimal.valueOf(oldBasket.getBasket().getTotalAmountWithDebitCard()).setScale(2, RoundingMode.HALF_UP);

        BigDecimal newBasketPriceTotCredit = BigDecimal.valueOf(newBasket.getTotalAmountWithCreditCard()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal newBasketPriceTotDebit = BigDecimal.valueOf(newBasket.getTotalAmountWithDebitCard()).setScale(2, RoundingMode.HALF_UP);

        BigDecimal seatPriceTotCredit = BigDecimal.valueOf(priceForSeat.getTotalAmountWithCreditCard()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal seatPriceTotDebit = BigDecimal.valueOf(priceForSeat.getTotalAmountWithDebitCard()).setScale(2, RoundingMode.HALF_UP);

        assertThat(newBasketPriceTotCredit).isEqualTo(oldBasketPriceTotCredit.add(seatPriceTotCredit));
        assertThat(newBasketPriceTotDebit).isEqualTo(oldBasketPriceTotDebit.add(seatPriceTotDebit));

        return this;
    }

    public PurchasedSeatAssertion verifyTheNewProductHasBeenStored(Basket oldBasket, Basket newBasket, Integer additionalQuantity) {
        newBasket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).collect(Collectors.toList()).forEach(passenger -> {

            Integer actualNumCabinItem = passenger.getCabinItems().size();
            Integer previousCabinItem = oldBasket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passenger.getCode())).findFirst().orElse(null).getCabinItems().size();
            assertThat(actualNumCabinItem >= (previousCabinItem + additionalQuantity))
                    .withFailMessage("No valid configuration for fare product related product bundle")
                    .isEqualTo(true);
        });
        return this;
    }

    public PurchasedSeatAssertion theSeatHasBeenAllocated(List<String> available, String seat) {
        assertThat(available.contains(seat))
                .withFailMessage("The seat " + seat + " has not been allocate properly")
                .isFalse();
        return this;
    }

    public PurchasedSeatAssertion allTheSeatHasBeenAllocated(List<String> available, List<String> seats) {
        for (String seat : seats) {
            assertThat(available.contains(seat))
                    .withFailMessage("The seat " + seat + " has not been allocate properly")
                    .isFalse();
        }
        return this;
    }

    public PurchasedSeatAssertion theSeatHasNotBeenAllocated(List<String> available, String seat) {
        assertThat(available.contains(seat))
                .withFailMessage("The seat " + seat + " has been allocate incorrectly")
                .isTrue();
        return this;
    }

    public PurchasedSeatAssertion theSeatHasBeenAddedForPassenger(Basket newBasket, String passengerCode, String expectedSeat) {
        Basket.Passenger basketPassenger = newBasket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(g -> g.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null);
        assertThat(Objects.nonNull(basketPassenger))
                .withFailMessage("Desired passenger " + passengerCode + " does not present in the current basket")
                .isTrue();

        assertThat(Objects.nonNull(basketPassenger.getSeat()))
                .withFailMessage("The desired passenger " + passengerCode + " does not have any purchased seat")
                .isTrue();

        assertThat(basketPassenger.getSeat().getSeatNumber().equalsIgnoreCase(expectedSeat))
                .withFailMessage("The desired passenger " + passengerCode + " does not have the desired purchased seat + " + expectedSeat);

        return this;
    }

    public PurchasedSeatAssertion verifyPriceChangeForMoveSeat(Map oldAssociationPassengerSeat, Map newAssociationPassengerSeat, String typeOfChange) {
        oldAssociationPassengerSeat.forEach(
                (k, v) -> {
                    String pCode = (String) k;
                    Seat oldSeat = (Seat) v;

                    PassengerSeatChangeRequests.Seat newSeat = (PassengerSeatChangeRequests.Seat) newAssociationPassengerSeat.get(pCode);

                    BigDecimal priceOldSeat = new BigDecimal(oldSeat.getPrice()).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal priceNewSeat = BigDecimal.valueOf(newSeat.getPrice()).setScale(2, RoundingMode.HALF_UP);
                    verifyPriceSeat(priceOldSeat, priceNewSeat, typeOfChange);
                }
        );
        return this;
    }

    private void verifyPriceSeat(BigDecimal priceOldSeat, BigDecimal priceNewSeat, String typeOfChange) {

        BigDecimal val = priceOldSeat.subtract(priceNewSeat).stripTrailingZeros();
        switch (typeOfChange) {
            case "same":
                assertThat(val)
                        .withFailMessage("The price of the seat after change is not correct (should be equal 0)")
                        .isEqualTo(BigDecimal.ZERO);
                break;
            case "lower":
                assertThat(val)
                        .withFailMessage("The price of the seat after change is not correct (should be greater than 0)")
                        .isGreaterThan(BigDecimal.ZERO);
                break;
            case "higher":
                assertThat(val)
                        .withFailMessage("The price of the seat after change is not correct (should be less than 0")
                        .isLessThan(BigDecimal.ZERO);
                break;
            default:
                break;
        }
    }

    public PurchasedSeatAssertion verifySeatRemoveAfterChange(Map oldAssociationPassengerSeat, Map newAssociationPassengerSeat) {
        oldAssociationPassengerSeat.forEach(
                (k, v) -> {
                    String pCode = (String) k;
                    Seat oldSeat = (Seat) v;

                    PassengerSeatChangeRequests.Seat newSeat = (PassengerSeatChangeRequests.Seat) newAssociationPassengerSeat.get(pCode);
                    assertThat(oldSeat.getSeatNumber())
                            .withFailMessage("The number of the seat after move seat is the same")
                            .isNotEqualTo(newSeat.getSeatNumber());
                }
        );
        return this;
    }


    public PurchasedSeatAssertion checkCabinBagAfterRemoveSeat(BasketsResponse oldBasket, BasketsResponse newBasket) {

        String codeSeat = oldBasket.getBasket().getOutbounds().stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .map(Basket.Passenger::getSeat).map(seat -> seat.getCode()).findFirst().get();


        assertThat(newBasket.getBasket().getOutbounds().stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .map(Basket.Passenger::getCabinItems)
                .flatMap(Collection::stream)
                .anyMatch(
                        getCabinItems -> getCabinItems.getBundleCode().equals(codeSeat)
                ))
                .withFailMessage("The cabin bag exist")
                .isFalse();

        return this;
    }

    public void assertFalse(boolean condition) {
        Assertions.assertThat(condition).isFalse();
    }
    public void assertNotNull(String condition) {
        Assertions.assertThat(condition).isNotNull();
    }

    public PurchasedSeatAssertion verifyPrimaryAndAdditionalSeatHasBeenRemovedFromPassenger(Basket basket, String passengerCode) {
        Basket.Passenger basketPassenger = basket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).findFirst().orElse(null);
        assertThat(Objects.nonNull(basketPassenger))
                .withFailMessage("No passenger in basket with desired code " + passengerCode)
                .isTrue();
        assertThat(Objects.isNull(basketPassenger.getSeat()))
                .withFailMessage("Unexpected seat on passenger " + basketPassenger.getCode())
                .isTrue();

        AbstractPassenger.AdditionalSeat additionalSeat = basket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(passengerCode)).flatMap(addlSeat -> addlSeat.getAdditionalSeats().stream()).findFirst().orElseThrow(() -> new IllegalArgumentException("No additional seats available for the desired passenger " + passengerCode));

        assertThat(Objects.isNull(additionalSeat.getSeat()))
                .withFailMessage("Additional seat on desired passenger " + basketPassenger.getCode() + " has not been removed")
                .isTrue();

        return this;
    }

    public void removalOfPurchasedSeat(Basket basket, String newPassengerType) throws EasyjetCompromisedException {

        if ("infantonlap".equalsIgnoreCase(newPassengerType)){
            AbstractPassenger infantOnLapPassenger = basket.getOutbounds().stream().flatMap(flights -> flights.getFlights().stream()).flatMap(passengers -> passengers.getPassengers().stream()).filter(passenger -> passenger.getFareProduct().getCode().equalsIgnoreCase(newPassengerType)).findFirst().orElseThrow(() -> new EasyjetCompromisedException("The passenger infant on lap doesn't exist"));
            assertThat(Objects.isNull(infantOnLapPassenger.getSeat())).withFailMessage("The new passenger type " + newPassengerType + " seat has not been removed in the amendable basket").isEqualTo(true);
        } else {
            AbstractPassenger passenger = basket.getOutbounds().stream().flatMap(flights -> flights.getFlights().stream()).flatMap(passengers -> passengers.getPassengers().stream()).filter(passenger1 -> passenger1.getPassengerDetails().getPassengerType().equalsIgnoreCase(newPassengerType)).findFirst().orElseThrow(() -> new EasyjetCompromisedException("The passenger doesn't exist"));
            assertThat(Objects.nonNull(passenger.getSeat())).withFailMessage("The new passenger type " + newPassengerType + " seat has been removed in the amendable basket").isEqualTo(true);
        }
    }

}
