package com.hybris.easyjet.fixture.hybris.asserters.helpers;

import com.hybris.easyjet.fixture.hybris.asserters.BasketsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static com.hybris.easyjet.fixture.hybris.asserters.helpers.BasketCalculator.TOTALS.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by giuseppedimartino on 24/05/17.
 * This class is meant to handle all the calculation to be done in the basket to get the proper totals
 */
public final class BasketCalculator {

    private static final String CR_CARD_FEE = "CRCardFee";
    private static final String ADMIN_FEE = "AdminFee";
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private final EnumMap<TOTALS, BigDecimal> totals = new EnumMap<>(TOTALS.class);

    private final BasketsAssertion assertion;
    private final int decimalPlaces;
    private final BigDecimal expectedAdminFee;
    private final Basket basket;
    private final BigDecimal crFeePercentage;

    /**
     * BasketCalculator provide a method to validate the calculation in the basket response
     *
     * @param assertion        the assertion class used for the assertions
     * @param decimalPlaces    the number of decimal places of the basket currency
     * @param expectedAdminFee the total admin fee that should be applied to the basket
     * @param basket           the basket object from the service response
     */
    public BasketCalculator(BasketsAssertion assertion, int decimalPlaces, Double expectedAdminFee, Basket basket) {
        this.assertion = assertion;
        this.decimalPlaces = decimalPlaces;
        this.expectedAdminFee = new BigDecimal(expectedAdminFee.toString());
        this.basket = basket;
        totals.put(DISCOUNTS, ZERO);
        totals.put(CC_FEES, ZERO);
        totals.put(TAXES, ZERO);
        totals.put(SUBTOTAL_DEBIT, ZERO);
        totals.put(SUBTOTAL_CREDIT, ZERO);
        totals.put(TOTAL_DEBIT, ZERO);
        totals.put(TOTAL_CREDIT, ZERO);
        crFeePercentage = getCreditCardFeePercentage();
    }

    private static String expectation(Double expected, Double actual) {
        return "expected was " + expected + "; actual is " + actual;
    }

    /**
     * priceCalculationAreRight validate all the element in the basket, summing up every list and calculating separately the totals of the basket
     *
     * @return the basketAssertion class defined in the constructor
     */
    public BasketsAssertion priceCalculationAreRight() {

        checkJourney(basket.getOutbounds().stream());
        checkJourney(basket.getInbounds().stream());

        if (!basket.getDiscounts().getItems().isEmpty()) {
            assertThat(basket.getDiscounts().getTotalAmount())
                    .withFailMessage("The booking level discount total is not right: " + expectation(totals.get(DISCOUNTS).doubleValue(), basket.getDiscounts().getTotalAmount()))
                    .isEqualTo(totals.get(DISCOUNTS).doubleValue());
        }

        BigDecimal otherFee = BigDecimal.ZERO;
        if (!basket.getFees().getItems().isEmpty()) {
            BigDecimal totalFee = totals.get(CC_FEES).add(expectedAdminFee.multiply(crFeePercentage).setScale(decimalPlaces, RoundingMode.UP));
            otherFee = basket.getFees().getItems().stream()
                    .filter(fee -> !fee.getCode().equals(ADMIN_FEE))
                    .filter(fee -> !fee.getCode().equals(CR_CARD_FEE))
                    .map(fee -> new BigDecimal(fee.getAmount().toString()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalFee = totalFee.add(otherFee);

            assertThat(basket.getFees().getTotalAmount())
                    .withFailMessage("The booking level fee total is not right: " + expectation(totalFee.doubleValue(), basket.getFees().getTotalAmount()))
                    .isEqualTo(totalFee.doubleValue());
        }

        if (!basket.getTaxes().getItems().isEmpty()) {
            assertThat(totals.get(TAXES).doubleValue())
                    .withFailMessage("The booking level tax total is not right: " + expectation(totals.get(TAXES).doubleValue(), basket.getTaxes().getTotalAmount()))
                    .isEqualTo(basket.getTaxes().getTotalAmount());
        }

        assertThat(basket.getSubtotalAmountWithDebitCard())
                .withFailMessage("The sub total with debit card is not right: " + expectation(totals.get(SUBTOTAL_DEBIT).doubleValue(), basket.getSubtotalAmountWithDebitCard()))
                .isEqualTo(totals.get(SUBTOTAL_DEBIT).doubleValue());

        assertThat(basket.getSubtotalAmountWithCreditCard())
                .withFailMessage("The sub total with credit card is not right: " + expectation(totals.get(SUBTOTAL_CREDIT).doubleValue(), basket.getSubtotalAmountWithCreditCard()))
                .isEqualTo(totals.get(SUBTOTAL_CREDIT).doubleValue());

        double totalDebit = totals.get(TOTAL_DEBIT)
                .add(expectedAdminFee)
                .add(otherFee)
                .doubleValue();
        assertThat(basket.getTotalAmountWithDebitCard())
                .withFailMessage("The  total with debit card is not right: " + expectation(totalDebit, basket.getTotalAmountWithDebitCard()))
                .isEqualTo(totalDebit);

        double totalCredit = totals.get(TOTAL_CREDIT)
                .add(otherFee)
                .add(expectedAdminFee
                        .multiply(crFeePercentage)
                        .setScale(decimalPlaces, RoundingMode.UP)
                ).doubleValue();
        assertThat(basket.getTotalAmountWithCreditCard())
                .withFailMessage("The  total with credit card is not right: " + expectation(totalCredit, basket.getTotalAmountWithCreditCard()))
                .isEqualTo(totalCredit);

        return assertion;
    }

    private BigDecimal getCreditCardFeePercentage() {
        Optional<AugmentedPriceItem> optionalCRFee;
        if (!basket.getFees().getItems().isEmpty()) {
            optionalCRFee = basket.getFees().getItems()
                    .stream()
                    .filter(fee -> fee.getCode().equals(CR_CARD_FEE))
                    .findFirst();
        } else {
            optionalCRFee = basket.getOutbounds().stream()
                    .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                    .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                    .map(AbstractPassenger::getFareProduct).limit(1)
                    .map(AbstractProductItem::getPricing)
                    .map(Pricing::getFees).flatMap(Collection::stream)
                    .filter(fee -> fee.getCode().equals(CR_CARD_FEE))
                    .findFirst();
        }
        return optionalCRFee
                .map(augmentedPriceItem ->
                        new BigDecimal(augmentedPriceItem.getPercentage())
                                .multiply(new BigDecimal("0.01"))
                                .setScale(2, RoundingMode.HALF_UP)
                                .add(BigDecimal.ONE))
                .orElse(BigDecimal.ONE);
    }

    private void checkJourney(Stream<Basket.Flights> flights) {
        flights.forEach(
                journey -> {
                    totals.put(JOURNEY_DEBIT, BigDecimal.ZERO);
                    totals.put(JOURNEY_CREDIT, BigDecimal.ZERO);
                    journey.getFlights().forEach(
                            flight -> flight.getPassengers().forEach(
                                    this::checkPassenger
                            )
                    );

                    assertThat(journey.getJourneyTotalWithDebitCard())
                            .withFailMessage("The  journey total with debit card is not right: " + expectation(totals.get(JOURNEY_DEBIT).doubleValue(), journey.getJourneyTotalWithDebitCard()))
                            .isEqualTo(totals.get(JOURNEY_DEBIT).doubleValue());

                    assertThat(journey.getJourneyTotalWithCreditCard())
                            .withFailMessage("The  journey total with debit card is not right: " + expectation(totals.get(JOURNEY_CREDIT).doubleValue(), journey.getJourneyTotalWithCreditCard()))
                            .isEqualTo(totals.get(JOURNEY_CREDIT).doubleValue());

                }
        );
    }

    private void checkPassenger(Basket.Passenger passenger) {
        totals.put(PASSENGER_DEBIT, BigDecimal.ZERO);
        totals.put(PASSENGER_CREDIT, BigDecimal.ZERO);

        BigDecimal adminFee = passenger.getFareProduct().getPricing().getFees().stream()
                .filter(fee -> fee.getCode().equals(ADMIN_FEE))
                .findFirst()
                .map(augmentedPriceItem -> new BigDecimal(augmentedPriceItem.getAmount().toString()))
                .orElse(BigDecimal.ZERO);

        calculateTotalPerPassenger(passenger, adminFee);

        double totWithDebitCardFee = totals.get(FARE_DEBIT)
                .add(adminFee)
                .doubleValue();
        assertThat(passenger.getFareProduct().getPricing().getTotalAmountWithDebitCard())
                .withFailMessage("The  passenger fare total with debit card is not right: " + expectation(totWithDebitCardFee, passenger.getFareProduct().getPricing().getTotalAmountWithDebitCard()))
                .isEqualTo(totWithDebitCardFee);

        double totWithCreditCardFee = totals.get(FARE_CREDIT)
                .add(adminFee
                        .multiply(crFeePercentage)
                        .setScale(decimalPlaces, RoundingMode.UP)).doubleValue();
        assertThat(passenger.getFareProduct().getPricing().getTotalAmountWithCreditCard())
                .withFailMessage("The  passenger fare total with credit card is not right: " + expectation(totWithCreditCardFee, passenger.getFareProduct().getPricing().getTotalAmountWithCreditCard()))
                .isEqualTo(totWithCreditCardFee);

        if (passenger.getActive()) updateTotalsForPassenger(passenger.getFareProduct(), adminFee);

        passenger.getHoldItems().forEach(
                item -> {
                    EnumMap<TOTALS, BigDecimal> extraWeightTotals = new EnumMap<>(TOTALS.class);
                    extraWeightTotals.put(EXTRA_WEIGHT_DEBIT, BigDecimal.ZERO);
                    extraWeightTotals.put(EXTRA_WEIGHT_CREDIT, BigDecimal.ZERO);
                    item.getExtraWeight().forEach(
                            extraWeight -> {
                                calculateTotalsPerProductItem(extraWeight);

                                assertThat(extraWeight.getPricing().getTotalAmountWithDebitCard())
                                        .withFailMessage("The  extra weight item total with debit card is not right: " + expectation(totals.get(PRODUCT_DEBIT).doubleValue(), extraWeight.getPricing().getTotalAmountWithDebitCard()))
                                        .isEqualTo(totals.get(PRODUCT_DEBIT).doubleValue());

                                assertThat(extraWeight.getPricing().getTotalAmountWithCreditCard())
                                        .withFailMessage("The  extra weight total with credit card is not right: " + expectation(totals.get(PRODUCT_CREDIT).doubleValue(), extraWeight.getPricing().getTotalAmountWithCreditCard()))
                                        .isEqualTo(totals.get(PRODUCT_CREDIT).doubleValue());

                                if (Objects.nonNull(extraWeight.getActive()))
                                    updateTotalsForItem();

                                extraWeightTotals.merge(EXTRA_WEIGHT_DEBIT,
                                        totals.get(PRODUCT_DEBIT),
                                        BigDecimal::add
                                );
                                extraWeightTotals.merge(EXTRA_WEIGHT_CREDIT,
                                        totals.get(PRODUCT_CREDIT),
                                        BigDecimal::add
                                );
                            }
                    );

                    calculateTotalsPerProductItem(item);

                    assertThat(item.getPricing().getTotalAmountWithDebitCard())
                            .withFailMessage("The  hold item total with debit card is not right: " + expectation(totals.get(PRODUCT_DEBIT).doubleValue(), item.getPricing().getTotalAmountWithDebitCard()))
                            .isEqualTo(totals.get(PRODUCT_DEBIT).add(extraWeightTotals.get(EXTRA_WEIGHT_DEBIT)).doubleValue());

                    assertThat(item.getPricing().getTotalAmountWithCreditCard())
                            .withFailMessage("The  hold item total with credit card is not right: " + expectation(totals.get(PRODUCT_CREDIT).doubleValue(), item.getPricing().getTotalAmountWithCreditCard()))
                            .isEqualTo(totals.get(PRODUCT_CREDIT).add(extraWeightTotals.get(EXTRA_WEIGHT_CREDIT)).doubleValue());

                    if (item.getActive()) updateTotalsForItem();
                }
        );

        passenger.getCabinItems().forEach(
                item -> {
                    calculateTotalsPerProductItem(item);

                    assertThat(item.getPricing().getTotalAmountWithDebitCard())
                            .withFailMessage("The  cabin item total with debit card is not right: " + expectation(totals.get(PRODUCT_DEBIT).doubleValue(), item.getPricing().getTotalAmountWithDebitCard()))
                            .isEqualTo(totals.get(PRODUCT_DEBIT).doubleValue());

                    assertThat(item.getPricing().getTotalAmountWithCreditCard())
                            .withFailMessage("The  cabin item total with credit card is not right: " + expectation(totals.get(PRODUCT_CREDIT).doubleValue(), item.getPricing().getTotalAmountWithCreditCard()))
                            .isEqualTo(totals.get(PRODUCT_CREDIT).doubleValue());

                    if (item.getActive()) updateTotalsForItem();
                }
        );

        passenger.getAdditionalItems().forEach(
                item -> {
                    calculateTotalsPerProductItem(item);

                    assertThat(item.getPricing().getTotalAmountWithDebitCard())
                            .withFailMessage("The  additional item total with debit card is not right: " + expectation(totals.get(PRODUCT_DEBIT).doubleValue(), item.getPricing().getTotalAmountWithDebitCard()))
                            .isEqualTo(totals.get(PRODUCT_DEBIT).doubleValue());

                    assertThat(item.getPricing().getTotalAmountWithCreditCard())
                            .withFailMessage("The  additional item total with credit card is not right: " + expectation(totals.get(PRODUCT_CREDIT).doubleValue(), item.getPricing().getTotalAmountWithCreditCard()))
                            .isEqualTo(totals.get(PRODUCT_CREDIT).doubleValue());

                    if (item.getActive()) updateTotalsForItem();
                }
        );

        if (passenger.getSeat() != null) {
            AbstractPassenger.Seat item = passenger.getSeat();
            calculateTotalsPerProductItem(item);

            assertThat(item.getPricing().getTotalAmountWithDebitCard())
                    .withFailMessage("The  seat total with debit card is not right: " + expectation(totals.get(PRODUCT_DEBIT).doubleValue(), item.getPricing().getTotalAmountWithDebitCard()))
                    .isEqualTo(totals.get(PRODUCT_DEBIT).doubleValue());

            assertThat(item.getPricing().getTotalAmountWithCreditCard())
                    .withFailMessage("The  seat total with credit card is not right: " + expectation(totals.get(PRODUCT_CREDIT).doubleValue(), item.getPricing().getTotalAmountWithCreditCard()))
                    .isEqualTo(totals.get(PRODUCT_CREDIT).doubleValue());

            if (item.getActive()) updateTotalsForItem();

        }

        passenger.getAdditionalSeats().forEach(
                item -> {
                    if (item.getSeat() != null) {
                        AbstractPassenger.Seat seat = item.getSeat();
                        calculateTotalsPerProductItem(seat);

                        assertThat(seat.getPricing().getTotalAmountWithDebitCard())
                                .withFailMessage("The  seat total with debit card is not right: " + expectation(totals.get(PRODUCT_DEBIT).doubleValue(), seat.getPricing().getTotalAmountWithDebitCard()))
                                .isEqualTo(totals.get(PRODUCT_DEBIT).doubleValue());

                        assertThat(seat.getPricing().getTotalAmountWithCreditCard())
                                .withFailMessage("The  seat total with credit card is not right: " + expectation(totals.get(PRODUCT_CREDIT).doubleValue(), seat.getPricing().getTotalAmountWithCreditCard()))
                                .isEqualTo(totals.get(PRODUCT_CREDIT).doubleValue());

                        if (seat.getActive()) updateTotalsForItem();
                    }

                    calculateTotalsPerProductItem(item.getFareProduct());

                    assertThat(item.getFareProduct().getPricing().getTotalAmountWithDebitCard())
                            .withFailMessage("The  additional seat total with debit card is not right: " + expectation(totals.get(PRODUCT_DEBIT).doubleValue(), item.getFareProduct().getPricing().getTotalAmountWithDebitCard()))
                            .isEqualTo(totals.get(PRODUCT_DEBIT).doubleValue());

                    assertThat(item.getFareProduct().getPricing().getTotalAmountWithCreditCard())
                            .withFailMessage("The  additional seat total with credit card is not right: " + expectation(totals.get(PRODUCT_CREDIT).doubleValue(), item.getFareProduct().getPricing().getTotalAmountWithCreditCard()))
                            .isEqualTo(totals.get(PRODUCT_CREDIT).doubleValue());

                    if (item.getFareProduct().getActive()) updateTotalsForItem();

                }
        );

        assertThat(passenger.getPassengerTotalWithDebitCard())
                .withFailMessage("The  passenger total with debit card is not right: " + expectation(totals.get(PASSENGER_DEBIT).doubleValue(), passenger.getPassengerTotalWithDebitCard()))
                .isEqualTo(totals.get(PASSENGER_DEBIT).doubleValue());

        assertThat(passenger.getPassengerTotalWithCreditCard())
                .withFailMessage("The  passenger total with credit card is not right: " + expectation(totals.get(PASSENGER_CREDIT).doubleValue(), passenger.getPassengerTotalWithCreditCard()))
                .isEqualTo(totals.get(PASSENGER_CREDIT).doubleValue());

        totals.merge(JOURNEY_DEBIT,
                totals.get(PASSENGER_DEBIT),
                BigDecimal::add
        );

        totals.merge(JOURNEY_CREDIT,
                totals.get(PASSENGER_CREDIT),
                BigDecimal::add
        );
    }

    private void calculateTotalPerPassenger(Basket.Passenger passenger, BigDecimal adminFee) {

        BigDecimal basePrice = new BigDecimal(passenger.getFareProduct().getPricing().getBasePrice().toString());
        BigDecimal totDiscount = calculateTotalPerAugmentedPriceItem(
                passenger.getFareProduct()
                        .getPricing()
                        .getDiscounts()
                        .stream());
        BigDecimal totFees = calculateTotalPerAugmentedPriceItem(
                passenger.getFareProduct()
                        .getPricing()
                        .getFees()
                        .stream());
        BigDecimal totTaxes = calculateTotalPerAugmentedPriceItem(
                passenger.getFareProduct()
                        .getPricing()
                        .getTaxes()
                        .stream());

        BigDecimal crFeeValue = passenger.getFareProduct().getPricing().getFees()
                .stream()
                .filter(fee -> fee.getCode().equals(CR_CARD_FEE))
                .findFirst()
                .map(augmentedPriceItem -> new BigDecimal(augmentedPriceItem.getAmount().toString()))
                .orElse(BigDecimal.ZERO);

        BigDecimal totForPassengers = basePrice
                .subtract(totDiscount)
                .add(totFees)
                .subtract(adminFee)
                .subtract(crFeeValue)
                .add(totTaxes);

        totals.put(FARE_DEBIT, totForPassengers);
        totals.put(FARE_CREDIT,
                totForPassengers
                        .multiply(crFeePercentage)
                        .setScale(decimalPlaces, RoundingMode.HALF_UP));

    }

    private BigDecimal calculateTotalPerAugmentedPriceItem(Stream<? extends AugmentedPriceItem> items) {

        return items.filter(Objects::nonNull)
                .map(item -> new BigDecimal(item.getAmount().toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(decimalPlaces, RoundingMode.HALF_UP);

    }

    private void calculateTotalsPerProductItem(AbstractProductItem item) {

        BigDecimal basePrice = new BigDecimal(item.getPricing()
                .getBasePrice()
                .toString());
        BigDecimal totDiscount = calculateTotalPerAugmentedPriceItem(item.getPricing()
                .getDiscounts()
                .stream());
        BigDecimal totFees = calculateTotalPerAugmentedPriceItem(item.getPricing()
                .getFees()
                .stream());
        BigDecimal totTaxes = calculateTotalPerAugmentedPriceItem(item.getPricing().getTaxes()
                .stream());

        totals.put(PRODUCT_PRICE, basePrice);
        totals.put(PRODUCT_DISCOUNTS, totDiscount);
        totals.put(PRODUCT_CC_FEES, calculateTotalPerAugmentedPriceItem(
                item.getPricing().getFees().stream()
                        .filter(fee -> fee.getCode().equals(CR_CARD_FEE)))
        );
        totals.put(PRODUCT_TAXES, totTaxes);

        BigDecimal crFeeValue = item.getPricing().getFees()
                .stream()
                .filter(fee -> fee.getCode().equals(CR_CARD_FEE))
                .findFirst()
                .map(augmentedPriceItem -> new BigDecimal(augmentedPriceItem.getAmount().toString()))
                .orElse(BigDecimal.ZERO);

        BigDecimal totWithDebitCard = basePrice
                .subtract(totDiscount)
                .add(totFees)
                .subtract(crFeeValue)
                .add(totTaxes);

        BigDecimal totWithCreditCard =
                totWithDebitCard
                        .multiply(crFeePercentage)
                        .setScale(decimalPlaces, RoundingMode.HALF_UP);

        totals.put(PRODUCT_DEBIT, totWithDebitCard);
        totals.put(PRODUCT_CREDIT, totWithCreditCard);

    }

    private void updateTotalsForItem() {
        totals.merge(PASSENGER_DEBIT,
                totals.get(PRODUCT_DEBIT),
                BigDecimal::add
        );

        totals.merge(PASSENGER_CREDIT,
                totals.get(PRODUCT_CREDIT),
                BigDecimal::add
        );

        totals.merge(SUBTOTAL_DEBIT,
                totals.get(PRODUCT_PRICE),
                BigDecimal::add
        );

        totals.merge(SUBTOTAL_CREDIT,
                totals.get(PRODUCT_PRICE)
                        .multiply(crFeePercentage).setScale(decimalPlaces, RoundingMode.HALF_UP),
                BigDecimal::add
        );

        totals.merge(TOTAL_DEBIT,
                totals.get(PRODUCT_DEBIT),
                BigDecimal::add
        );

        totals.merge(TOTAL_CREDIT,
                totals.get(PRODUCT_CREDIT),
                BigDecimal::add
        );

        totals.merge(
                DISCOUNTS,
                totals.get(PRODUCT_DISCOUNTS),
                BigDecimal::add
        );
        totals.merge(
                CC_FEES,
                totals.get(PRODUCT_CC_FEES),
                BigDecimal::add
        );
        totals.merge(
                TAXES,
                totals.get(PRODUCT_TAXES),
                BigDecimal::add
        );
    }

    private void updateTotalsForPassenger(AbstractPassenger.FareProduct fareProduct, BigDecimal adminFee) {
        totals.merge(SUBTOTAL_DEBIT,
                new BigDecimal(fareProduct.getPricing().getBasePrice().toString()),
                BigDecimal::add
        );

        totals.merge(SUBTOTAL_CREDIT,
                new BigDecimal(fareProduct.getPricing().getBasePrice().toString())
                        .multiply(crFeePercentage).setScale(decimalPlaces, RoundingMode.HALF_UP),
                BigDecimal::add
        );

        totals.merge(
                DISCOUNTS,
                calculateTotalPerAugmentedPriceItem(fareProduct.getPricing().getDiscounts().stream()),
                BigDecimal::add
        );
        totals.merge(
                CC_FEES,
                calculateTotalPerAugmentedPriceItem(
                        fareProduct.getPricing().getFees().stream()
                                .filter(fee -> fee.getCode().equals(CR_CARD_FEE))
                ),
                BigDecimal::add
        );
        totals.merge(
                TAXES,
                calculateTotalPerAugmentedPriceItem(fareProduct.getPricing().getTaxes().stream()),
                BigDecimal::add
        );

        totals.put(
                PASSENGER_DEBIT,
                totals.get(FARE_DEBIT)
                        .add(adminFee)
                        .multiply(new BigDecimal(fareProduct.getQuantity()))
        );
        totals.put(
                PASSENGER_CREDIT,
                totals.get(FARE_CREDIT)
                        .add(adminFee
                                .multiply(crFeePercentage)
                                .setScale(decimalPlaces, RoundingMode.UP)
                        )
                        .multiply(new BigDecimal(fareProduct.getQuantity()))
        );

        totals.merge(TOTAL_DEBIT,
                totals.get(FARE_DEBIT)
                        .multiply(new BigDecimal(fareProduct.getQuantity())),
                BigDecimal::add
        );

        totals.merge(TOTAL_CREDIT,
                totals.get(FARE_CREDIT)
                        .multiply(new BigDecimal(fareProduct.getQuantity())),
                BigDecimal::add
        );
    }

    protected enum TOTALS {
        DISCOUNTS, CC_FEES, TAXES, TOTAL_DEBIT, TOTAL_CREDIT, SUBTOTAL_DEBIT, SUBTOTAL_CREDIT, JOURNEY_DEBIT, JOURNEY_CREDIT, PASSENGER_DEBIT, PASSENGER_CREDIT, FARE_DEBIT, FARE_CREDIT, PRODUCT_DEBIT, PRODUCT_CREDIT, PRODUCT_DISCOUNTS, PRODUCT_CC_FEES, PRODUCT_TAXES, PRODUCT_PRICE, EXTRA_WEIGHT_DEBIT, EXTRA_WEIGHT_CREDIT, SEAT_DEBIT, SEAT_CREDIT
    }
}