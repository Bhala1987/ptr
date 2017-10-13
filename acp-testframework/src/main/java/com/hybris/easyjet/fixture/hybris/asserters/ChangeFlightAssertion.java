package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.database.hybris.dao.CartDao;
import com.hybris.easyjet.database.hybris.dao.FeesAndTaxesDao;
import com.hybris.easyjet.database.hybris.models.FeesAndTaxesModel;
import com.hybris.easyjet.database.hybris.models.PassengerStatus;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractFlights;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AugmentedPriceItem;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.Pricing;
import lombok.NoArgsConstructor;
import net.thucydides.core.annotations.Step;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Created by robertadigiorgio on 11/07/2017.
 */
@NoArgsConstructor
public class ChangeFlightAssertion extends Assertion<ChangeFlightAssertion, BasketConfirmationResponse> {

    private static final String STANDARD_FLIGHT_FEE_BEFORE_THRESHOLD = "FlightFee<59";
    private static final String STANDARD_FLIGHT_FEE_AFTER_THRESHOLD = "FlightFee>60";
    private static final String FLEXI_FLIGHT_FEE_BEFORE_THRESHOLD = "FlightFlexiFee<59";
    private static final String FLEXI_FLIGHT_FEE_AFTER_THRESHOLD = "FlightFlexiFee>60";
    private static final Logger LOG = LogManager.getLogger(ChangeFlightAssertion.class);
    private static final String ADULT = "adult";
    private static final String CHILD = "child";
    private static final String INFANT = "infant";
    private static final String CR_CARD_FEE = "CRCardFee";
    private static final String ADMIN_FEE = "AdminFee";

    private CartDao cartDao = CartDao.getCartDaoFromSpring();

    public ChangeFlightAssertion(BasketConfirmationResponse changeFlightResponse) {
        this.response = changeFlightResponse;
    }

    public ChangeFlightAssertion verifyFlightPriceHasBeenUpdated(Basket basket, String flightKey, Double expectedBasePrice) {
        Basket.Flight flight = basket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).filter(g -> g.getFlightKey().equalsIgnoreCase(flightKey)).findFirst().orElse(null);
        assertThat(Objects.nonNull(flight))
                .withFailMessage("No flight are found in the basket with the key " + flightKey)
                .isTrue();

        Double actualPrice = flight.getPassengers().get(0).getFareProduct().getPricing().getBasePrice();
        assertThat(actualPrice)
                .withFailMessage("The two prices do not match. Expected base price " + expectedBasePrice + ", but was " + actualPrice)
                .isEqualTo(expectedBasePrice);

        return this;
    }

    public ChangeFlightAssertion verifyFeeAndTaxes(Basket oldBasket, String oldFlightKey, Basket newBasket, String newFlightKey) {
        Basket.Flight oldFlight = oldBasket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).filter(g -> g.getFlightKey().equalsIgnoreCase(oldFlightKey)).findFirst().orElseThrow(() -> new IllegalArgumentException("No flight are found in the old basket with the key " + oldFlightKey));
        Basket.Flight newFlight = newBasket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).filter(g -> g.getFlightKey().equalsIgnoreCase(newFlightKey)).findFirst().orElseThrow(() -> new IllegalArgumentException("No flight are found in the new basket with the key " + newFlightKey));

        Pricing oldFlightPricing = oldFlight.getPassengers().get(0).getFareProduct().getPricing();
        Pricing newFlightPricing = newFlight.getPassengers().get(0).getFareProduct().getPricing();

        assertThat(oldFlightPricing.getFees().size())
                .withFailMessage("The applied fee has been changed unexpected")
                .isEqualTo(newFlightPricing.getFees().size());

        assertThat(oldFlightPricing.getTaxes().size())
                .withFailMessage("The applied taxes has been changed unexpected")
                .isEqualTo(newFlightPricing.getTaxes().size());

        return this;
    }

    public ChangeFlightAssertion verifyProductAssociatedToPassenger(Basket oldBasket, String oldFlightKey, Basket newBasket, String newFlightKey) {
        Basket.Flight oldFlight = oldBasket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).filter(g -> g.getFlightKey().equalsIgnoreCase(oldFlightKey)).findFirst().orElseThrow(() -> new IllegalArgumentException("No flight are found in the old basket with the key " + oldFlightKey));
        Basket.Flight newFlight = newBasket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).filter(g -> g.getFlightKey().equalsIgnoreCase(newFlightKey)).findFirst().orElseThrow(() -> new IllegalArgumentException("No flight are found in the new basket with the key " + newFlightKey));

        Basket.Passenger passengerOldFlight = oldFlight.getPassengers().get(0);
        Basket.Passenger passengerNewFlight = newFlight.getPassengers().get(0);

        assertThat(passengerOldFlight.getCabinItems().size())
                .withFailMessage("The number of cabin item is different. Expected number cabin item " + passengerOldFlight + ", but was " + passengerNewFlight)
                .isEqualTo(passengerNewFlight.getCabinItems().size());

        return this;
    }

    public ChangeFlightAssertion verifyOldFlightKeyHasBeenRemoved(Basket newBasket, String oldFlightKey) {
        Basket.Flight newFlight = newBasket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).filter(g -> g.getFlightKey().equalsIgnoreCase(oldFlightKey)).findFirst().orElse(null);

        assertThat(Objects.isNull(newFlight))
                .withFailMessage("The flight with key " + oldFlightKey + " is still present in the basket")
                .isTrue();

        return this;
    }

    public ChangeFlightAssertion verifyApportionedAdminFee(Basket oldBasket, String oldFlightKey, Basket newBasket, String newFlightKey) {

        Basket.Flight oldFlight = oldBasket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).filter(g -> g.getFlightKey().equalsIgnoreCase(oldFlightKey)).findFirst().orElseThrow(() -> new IllegalArgumentException("No flight are found in the old basket with the key " + oldFlightKey));
        Basket.Flight newFlight = newBasket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).filter(g -> g.getFlightKey().equalsIgnoreCase(newFlightKey)).findFirst().orElseThrow(() -> new IllegalArgumentException("No flight are found in the new basket with the key " + newFlightKey));

        AugmentedPriceItem oldFlightAdminFee = oldFlight.getPassengers().get(0).getFareProduct().getPricing().getFees().stream().filter(f -> f.getCode().equalsIgnoreCase(ADMIN_FEE)).findFirst().orElseThrow(() -> new IllegalArgumentException("No Admin fee found on the old flight"));
        AugmentedPriceItem newFlightAdminFee = newFlight.getPassengers().get(0).getFareProduct().getPricing().getFees().stream().filter(f -> f.getCode().equalsIgnoreCase(ADMIN_FEE)).findFirst().orElseThrow(() -> new IllegalArgumentException("No Admin fee found on the new flight"));

        assertThat(oldFlightAdminFee.getAmount())
                .withFailMessage("The amount of the admin fee has been changed. Expected admin fee amount " + oldFlightAdminFee.getAmount() + " but was " + newFlightAdminFee.getAmount())
                .isEqualTo(newFlightAdminFee.getAmount());

        return this;
    }

    @Step("New flight {1} is in the basket")
    public ChangeFlightAssertion theNewFlightIsAddedToTheBasket(Basket basket, String flightKey) {

        assertThat(basket.getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .filter(flight -> flight.getFlightKey().equals(flightKey)))
                .withFailMessage("The flight " + flightKey + " is not present in the basket")
                .hasSize(1);
        return this;
    }

    @Step("New flight {2} price is right")
    public ChangeFlightAssertion thePriceOfTheNewFlightIsRight(Basket basket, String originalFlightKey, String replacedFlightKey, CartDao cartDao) {

        int decimalPlaces = Integer.parseInt(basket.getCurrency().getDecimalPlaces()); //NOSONAR sonar identify it as unused
        checkPassenger(basket, replacedFlightKey,
                passenger -> {
                    Double replacedPassengerTotal = calculateTotalWithDebitForPassenger(decimalPlaces, passenger.getFareProduct().getPricing()); //NOSONAR sonar identify it as unused

                    Double actualTotal = passenger.getFareProduct().getPricing().getTotalAmountWithDebitCard(); //NOSONAR sonar identify it as unused
                    assertThat(actualTotal)
                            .withFailMessage("The passenger total for " + passenger.getCode() + " is wrong")
                            .isEqualTo(replacedPassengerTotal);
                }
        );
        return this;
    }

    private Double calculateTotalWithDebitForPassenger(int decimalPlaces, Pricing passengerPricing) {
        BigDecimal originalBasePrice = new BigDecimal(passengerPricing.getBasePrice().toString());
        BigDecimal originalTotDiscount = passengerPricing.getDiscounts().stream()
                .filter(Objects::nonNull)
                .map(item -> new BigDecimal(item.getAmount().toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(decimalPlaces, RoundingMode.HALF_UP);
        BigDecimal originalTotFees = passengerPricing.getFees().stream()
                .filter(Objects::nonNull)
                .map(item -> new BigDecimal(item.getAmount().toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(decimalPlaces, RoundingMode.HALF_UP);
        BigDecimal originalTotTaxes = passengerPricing.getTaxes().stream()
                .filter(Objects::nonNull)
                .map(item -> new BigDecimal(item.getAmount().toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(decimalPlaces, RoundingMode.HALF_UP);

        BigDecimal originalAdminFee = passengerPricing.getFees().stream()
                .filter(fee -> fee.getCode().equals(ADMIN_FEE))
                .findFirst()
                .map(augmentedPriceItem -> new BigDecimal(augmentedPriceItem.getAmount().toString()))
                .orElse(BigDecimal.ZERO);

        BigDecimal originalCrFeeValue = passengerPricing.getFees()
                .stream()
                .filter(fee -> fee.getCode().equals(CR_CARD_FEE))
                .findFirst()
                .map(augmentedPriceItem -> new BigDecimal(augmentedPriceItem.getAmount().toString()))
                .orElse(BigDecimal.ZERO);

        return originalBasePrice
                .subtract(originalTotDiscount)
                .add(originalTotFees)
                .subtract(originalAdminFee)
                .subtract(originalCrFeeValue)
                .add(originalTotTaxes)
                .doubleValue();
    }

    @Step("Taxes are applied to the new flight {1}")
    public ChangeFlightAssertion taxesAndFeesAreAppliedToTheNewFlight(Basket basket, String flightKey, FeesAndTaxesDao feesAndTaxesDao) {
        String sector = flightKey.substring(8, 14);

        String currency = basket.getCurrency().getCode();

        HashMap<String, Double> adultTaxes = feesAndTaxesDao.getTaxesForPassenger(sector, currency, "adult"); //NOSONAR sonar identify it as unused
        HashMap<String, Double> childTaxes = feesAndTaxesDao.getTaxesForPassenger(sector, currency, "child"); //NOSONAR sonar identify it as unused
        HashMap<String, Double> infantTaxes = feesAndTaxesDao.getTaxesForPassenger(sector, currency, "infant"); //NOSONAR sonar identify it as unused

        checkPassenger(basket, flightKey,
                passenger -> {
                    HashMap<String, Double> applicableTaxes = new HashMap<>(); //NOSONAR sonar identify it as unused
                    switch (passenger.getPassengerDetails().getPassengerType()) {
                        case ADULT:
                            applicableTaxes = adultTaxes;
                            break;
                        case CHILD:
                            applicableTaxes = childTaxes;
                            break;
                        case INFANT:
                            applicableTaxes = infantTaxes;
                            break;
                        default:
                            LOG.error("Unrecognized passenger type");
                    }
                    for (Map.Entry<String, Double> entry : applicableTaxes.entrySet()) { //NOSONAR sonar identify it as unused
                        assertThat(passenger.getFareProduct().getPricing().getTaxes())
                                .withFailMessage("The tax " + entry.getKey() + " is not applied to the passenger")
                                .extracting("code")
                                .contains(entry.getKey() + "_" + passenger.getFareProduct().getOrderEntryNumber());
                    }
                }
        );
        return this;
    }

    @Step("Passengers details are copied to the new flight {2}")
    public ChangeFlightAssertion passengerDetailsAreCopiedToTheNewFlight(Basket basket, String originalFlightKey, String replacedFlightKey, CartDao cartDao) {
        checkPassenger(basket, replacedFlightKey,
                passenger -> {
                    try {

                        Basket.Passenger originalPassengerEntry = basket.getOutbounds().stream() //NOSONAR sonar identify it as unused
                                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                                .filter(flight -> flight.getFlightKey().equals(originalFlightKey))
                                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                                .filter(originalPassenger -> originalPassenger.getCode().equals(cartDao.getOriginalPassenger(basket.getCode(), passenger.getCode())))
                                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("Original passenger is not present in the cart"));

                        checkCopiedPassengerDetails(originalPassengerEntry, passenger);
                    } catch (EasyjetCompromisedException e) {
                        fail(e.getMessage());
                    }
                }
        );
        return this;
    }

    @Step("Passenger details of {0} copied to {1}")
    public void checkCopiedPassengerDetails(Basket.Passenger originalPassenger, Basket.Passenger replacedPassenger) {
        assertThat(replacedPassenger.getPassengerDetails())
                .withFailMessage("The passenger details are wrong")
                .isEqualToComparingFieldByFieldRecursively(originalPassenger.getPassengerDetails());
    }

    @Step("Passenger products of {0} copied to {1}")
    public ChangeFlightAssertion passengerProductsAreAddedToTheNewFlight(Basket basket, String originalFlightKey, String replacedFlightKey, CartDao cartDao) {
        checkPassenger(basket, replacedFlightKey,
                passenger -> {
                    try {

                        Basket.Passenger originalPassengerEntry = basket.getOutbounds().stream() //NOSONAR sonar identify it as unused
                                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                                .filter(flight -> flight.getFlightKey().equals(originalFlightKey))
                                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                                .filter(originalPassenger -> originalPassenger.getCode().equals(cartDao.getOriginalPassenger(basket.getCode(), passenger.getCode())))
                                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("Original passenger is not present in the cart"));

                        checkCopiedPassengerCabinItems(originalPassengerEntry, passenger);
                        checkCopiedPassengerHoldItems(originalPassengerEntry, passenger);
                        checkCopiedPassengerAdditionalItems(originalPassengerEntry, passenger);
                        checkCopiedPassengerAdditionalSeats(originalPassengerEntry, passenger);
                    } catch (EasyjetCompromisedException e) {
                        fail(e.getMessage());
                    }
                }
        );
        return this;
    }

    @Step("Cabin items of {0} copied to {1}")
    public void checkCopiedPassengerCabinItems(Basket.Passenger originalPassenger, Basket.Passenger replacedPassenger) {
        assertThat(replacedPassenger.getCabinItems().size())
                .withFailMessage("Not all the cabin items have been copied")
                .isEqualToComparingFieldByFieldRecursively(originalPassenger.getCabinItems().size());
    }

    @Step("Hold items of {0} copied to {1}")
    public void checkCopiedPassengerHoldItems(Basket.Passenger originalPassenger, Basket.Passenger replacedPassenger) {
        assertThat(replacedPassenger.getHoldItems().size())
                .withFailMessage("Not all the hold items have been copied")
                .isEqualToComparingFieldByFieldRecursively(originalPassenger.getHoldItems().size());
    }

    @Step("Additional items of {0} copied to {1}")
    public void checkCopiedPassengerAdditionalItems(Basket.Passenger originalPassenger, Basket.Passenger replacedPassenger) {
        assertThat(replacedPassenger.getAdditionalItems().size())
                .withFailMessage("Not all the additional items have been copied")
                .isEqualToComparingFieldByFieldRecursively(originalPassenger.getAdditionalItems().size());
    }

    @Step("Additional seats of {0} copied to {1}")
    public void checkCopiedPassengerAdditionalSeats(Basket.Passenger originalPassenger, Basket.Passenger replacedPassenger) {
        assertThat(replacedPassenger.getAdditionalSeats().size())
                .withFailMessage("Not all the additional seats have been copied")
                .isEqualToComparingFieldByFieldRecursively(originalPassenger.getAdditionalSeats().size());
    }

    @Step("Old flight {1} is deactivated")
    public ChangeFlightAssertion theOldFlightIsDeactivated(Basket basket, String originalFlightKey, String replacedFlightKey, CartDao cartDao) {
        checkPassenger(basket, replacedFlightKey,
                passenger -> {
                    try {
                        Basket.Passenger originalPassengerEntry = basket.getOutbounds().stream() //NOSONAR sonar identify it as unused
                                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                                .filter(flight -> flight.getFlightKey().equals(originalFlightKey))
                                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                                .filter(originalPassenger -> originalPassenger.getCode().equals(cartDao.getOriginalPassenger(basket.getCode(), passenger.getCode())))
                                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("Original passenger is not present in the cart"));

                        assertThat(originalPassengerEntry.getActive())
                                .withFailMessage("The passenger is still active")
                                .isFalse();
                        assertThat(originalPassengerEntry.getFareProduct().getActive())
                                .withFailMessage("Passenger fareProduct is still active")
                                .isFalse();
                        originalPassengerEntry.getCabinItems().forEach(
                                item -> assertThat(item.getActive())
                                        .withFailMessage("A cabin item is still active")
                                        .isFalse()
                        );
                        originalPassengerEntry.getHoldItems().forEach(
                                item -> {
                                    assertThat(item.getActive())
                                            .withFailMessage("An hold item is still active")
                                            .isFalse();
                                    item.getExtraWeight().forEach(
                                            extraWeight -> assertThat(extraWeight.getActive())
                                                    .withFailMessage("An extra weight is still active")
                                                    .isFalse()
                                    );
                                }
                        );
                        originalPassengerEntry.getAdditionalItems().forEach(
                                item -> assertThat(item.getActive())
                                        .withFailMessage("An additional item is still active")
                                        .isFalse()
                        );
                        originalPassengerEntry.getAdditionalSeats().forEach(
                                item -> {
                                    assertThat(item.getFareProduct().getActive())
                                            .withFailMessage("An additional seat is still active")
                                            .isFalse();

                                    if (!Objects.isNull(item.getSeat())) {
                                        assertThat(item.getSeat().getActive())
                                                .withFailMessage("The seat of an additional seat is still active")
                                                .isFalse();
                                    }
                                }
                        );
                        if (!Objects.isNull(originalPassengerEntry.getSeat())) {
                            assertThat(originalPassengerEntry.getSeat().getActive())
                                    .withFailMessage("The seat of the passenger is still active")
                                    .isFalse();
                        }
                    } catch (EasyjetCompromisedException e) {
                        new EasyjetCompromisedException(e.getMessage());
                    }
                }
        );
        return this;
    }

    private void checkPassenger(Basket basket, String flightKey, Consumer<Basket.Passenger> passengerCheck) {//NOSONAR sonar identify flightKey as unused
        basket.getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .filter(flight -> flight.getFlightKey().equals(flightKey))
                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                .forEach(passengerCheck);
    }

    public ChangeFlightAssertion noChangeFlightFeeIsAdded(Basket basket, String flightKey, FeesAndTaxesDao feesAndTaxesDao, String fare) {

        FeesAndTaxesModel changeFlightFeeBeforeThreshold;
        FeesAndTaxesModel changeFlightFeeAfterThreshold;

        if ("Standard".equals(fare)) {
            changeFlightFeeBeforeThreshold = feesAndTaxesDao.getFeesBasedOnType(basket.getCurrency().getCode(), STANDARD_FLIGHT_FEE_BEFORE_THRESHOLD, testData.getChannel()).get(0);
            changeFlightFeeAfterThreshold = feesAndTaxesDao.getFeesBasedOnType(basket.getCurrency().getCode(), STANDARD_FLIGHT_FEE_AFTER_THRESHOLD, testData.getChannel()).get(0);
        } else {
            changeFlightFeeBeforeThreshold = feesAndTaxesDao.getFeesBasedOnType(basket.getCurrency().getCode(), FLEXI_FLIGHT_FEE_BEFORE_THRESHOLD, testData.getChannel()).get(0);
            changeFlightFeeAfterThreshold = feesAndTaxesDao.getFeesBasedOnType(basket.getCurrency().getCode(), FLEXI_FLIGHT_FEE_AFTER_THRESHOLD, testData.getChannel()).get(0);
        }

        checkPassenger(basket, flightKey,
                passenger -> {
                    assertThat(passenger.getFareProduct().getPricing().getFees().stream()
                            .noneMatch(fee -> fee.getCode().split("_")[0].equals(changeFlightFeeBeforeThreshold.getFeeCode())))
                            .withFailMessage("The change flight fee before threshold has been added")
                            .isTrue();
                    assertThat(passenger.getFareProduct().getPricing().getFees().stream()
                            .noneMatch(fee -> fee.getCode().split("_")[0].equals(changeFlightFeeAfterThreshold.getFeeCode())))
                            .withFailMessage("The change flight fee after threshold has been added")
                            .isTrue();
                });
        return this;
    }

    public ChangeFlightAssertion feeIsApplied(Basket basket, String flightKey, String feesCode) {
        checkPassenger(basket, flightKey,
                passenger ->
                        assertThat(passenger.getFareProduct().getPricing().getFees().stream()
                                .anyMatch(fee -> fee.getCode().split("_")[0].equals(feesCode)))
                                .withFailMessage("The change flight fee " + feesCode + " has not been added")
                                .isTrue());
        return this;
    }

    public ChangeFlightAssertion passengerStatusIsCorrect(PassengerStatus actualPassengerStatus, PassengerStatus expectedPassengerStatus) {
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

    public ChangeFlightAssertion isAPISExistForNewlyAddedPassenger(Basket basket) throws EasyjetCompromisedException {
        assertThat(basket.getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                .filter(passenger -> "NEW".equals(passenger.getEntryStatus()))
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("No new passenger present in the basket"))
                .getPassengerAPIS()).isNotNull();
        return this;
    }

    public ChangeFlightAssertion passengerIsLinkedToOriginalLinkedFlights(Basket basket, List<String> passengerList) throws EasyjetCompromisedException {
        for (String originalPassengerCode : passengerList) {
            String newPassengerCode = Stream.concat(basket.getOutbounds().stream(), basket.getInbounds().stream())
                    .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                    .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                    .filter(passenger -> "NEW".equals(passenger.getEntryStatus()))
                    .filter(passenger -> passenger.getFareProduct().getPricing().getPriceDifference().getToeiCode().equals(originalPassengerCode))
                    .findFirst().orElseThrow(() -> new EasyjetCompromisedException("No new passenger present in the basket"))
                    .getCode();

            List<String> originalLinkedPassengers = Stream.concat(basket.getOutbounds().stream(), basket.getInbounds().stream())
                    .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                    .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                    .filter(passenger -> !passenger.getActive())
                    .filter(passenger -> passenger.getPassengerMap().contains(originalPassengerCode))
                    .map(Basket.Passenger::getPassengerMap).flatMap(Collection::stream)
                    .filter(code -> !code.equals(originalPassengerCode))
                    .collect(Collectors.toList());

            List<String> newLinkedPassengers = cartDao.getAssociatedPassenger(basket.getCode(), newPassengerCode);

            assertThat(Stream.concat(basket.getOutbounds().stream(), basket.getInbounds().stream())
                    .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                    .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                    .filter(passenger -> newLinkedPassengers.contains(passenger.getCode()))
                    .map(AbstractPassenger::getCode)
                    .collect(Collectors.toList()))
                    .containsAll(originalLinkedPassengers);
        }

        return this;
    }
}