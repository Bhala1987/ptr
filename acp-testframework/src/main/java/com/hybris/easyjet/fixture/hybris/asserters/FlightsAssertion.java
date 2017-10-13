package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.database.hybris.dao.FeesAndTaxesDao;
import com.hybris.easyjet.database.hybris.models.CurrencyModel;
import com.hybris.easyjet.database.hybris.models.ItemModel;
import com.hybris.easyjet.exceptions.HybrisCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.DateFormat;
import com.hybris.easyjet.fixture.hybris.helpers.FlightPassengers;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractFlights;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AugmentedPriceItem;
import lombok.NoArgsConstructor;
import net.thucydides.core.annotations.Step;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.assertj.core.groups.Tuple;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by daniel on 26/11/2016.
 * assertion wrapper for flights response object, provides reusable assertions to all tests
 */
@NoArgsConstructor
public class FlightsAssertion extends Assertion<FlightsAssertion, FindFlightsResponse> {
    public static final String ADMIN_FEE = "AdminFee";
    public static final String ADULT = "adult";
    public static final String CHILD = "child";
    public static final String INFANT = "infant";
    public static final String STAFF = "Staff";
    public static final String STAFF_STANDARD = "StaffStandard";
    public static final String STANDBY = "Standby";
    public static final String TOT_WITH_CREDIT_CARD_FEE = "totWithCreditCardFee";
    public static final String TOT_WITH_DEBIT_CARD_FEE = "totWithDebitCardFee";
    public static final String CR_CARD_FEE = "CRCardFee";
    public static final String SHOULD = "should";
    public static final String SHOULD_NOT = "should not";
    public static final String INFANT_ON_SEAT = "infantOnSeat";
    public static final String INFANT_ON_LAP = "infantOnLap";
    private static final Logger LOG = LogManager.getLogger(FlightsAssertion.class);
    java.text.DateFormat fromFormat = new SimpleDateFormat("E dd-MMM-yyyy hh:mm:ss");

    public FlightsAssertion(FindFlightsResponse flightsResponse) {
        this.response = flightsResponse;
    }

    public void setResponse(FindFlightsResponse flightsResponse) {
        this.response = flightsResponse;
    }

    public FlightsAssertion atLeastOneOutboundFlightWasReturned() {
        assertThat(response.getOutbound().getJourneys()).isNotEmpty();
        assertThat(response.getOutbound().getJourneys().get(0).getFlights().size()).isGreaterThan(0);
        assertThat(response.getOutbound().getJourneys().get(0).getFlights().get(0)).isNotNull();
        assertThat(response.getOutbound().getJourneys()).isNotEmpty();
        assertThat(response.getOutbound().getJourneys().get(0).getFlights().size()).isGreaterThan(0);
        assertThat(response.getOutbound().getJourneys().get(0).getFlights().get(0)).isNotNull();
        return this;
    }

    public FlightsAssertion unavailableStatus(long hours) {
        int journeySize = response.getOutbound().getJourneys().size();
        for (int i = 0; i < journeySize; i++) {
            int flightSize = response.getOutbound().getJourneys().get(i).getFlights().size();
            for (int j = 0; j < flightSize; j++) {
                String strDpartureDate = response.getOutbound()
                        .getJourneys()
                        .get(i)
                        .getFlights()
                        .get(j)
                        .getDeparture()
                        .getDate();

                SimpleDateFormat sdf = new SimpleDateFormat("EEE dd-MMM-yyyy hh:mm:ss");
                Date currentdate = new Date();

                try {
                    Date departureDate = sdf.parse(strDpartureDate);

                    if ((Math.abs(departureDate.getTime() - currentdate.getTime()) / (60 * 60 * 1000)) <= hours) {
                        assertThat(response.getOutbound()
                                .getJourneys()
                                .get(i)
                                .getFlights()
                                .get(j)
                                .getAvailableStatus()
                                .equalsIgnoreCase("UNAVAILABLE") || !response.getOutbound()//NOSONAR
                                .getJourneys()
                                .get(i)
                                .getFlights()
                                .get(j)
                                .getAvailableStatus()
                                .equalsIgnoreCase("AVAILABLE")); //SOLDOUT status is also coming, hence this assertion
                    }

                } catch (ParseException e) {
                    LOG.error(e);
                }
            }
        }
        return this;
    }

    public FlightsAssertion theFlightHasAFlightKey() {

        assertThat(response.getOutbound()
                .getJourneys()
                .get(0)
                .getFlights()
                .get(0)
                .getFlightKey()).isNotEmpty();
        return this;
    }

    public FlightsAssertion discountsreturned(String shouldOrShouldnot, ItemModel expectedDiscount) {

        List<FindFlightsResponse.AugmentedPriceItem> discounts = response.getOutbound()
                .getJourneys()
                .get(0)
                .getFlights()
                .get(0)
                .getFareTypes()
                .get(0)
                .getPassengers()
                .get(0)
                .getDiscounts();
        if (shouldOrShouldnot.equals(SHOULD)) {
            for (int i = 0; i < discounts.size(); i++) {
                Integer percentageDiscount = response.getOutbound()
                        .getJourneys()
                        .get(0)
                        .getFlights()
                        .get(0)
                        .getFareTypes()
                        .get(0)
                        .getPassengers()
                        .get(0)
                        .getDiscounts()
                        .get(i)
                        .getPercentageValue();
                Double discountValue = discounts
                        .get(i)
                        .getValue();
                if (discounts.get(i).getCode().equals(expectedDiscount.getCode())) {
                    if (percentageDiscount == 0) {
                        assertThat(discountValue).isEqualTo(Double.parseDouble(expectedDiscount.getValue()));
                    } else {
                        assertThat(percentageDiscount).isEqualTo((int) Double.parseDouble(expectedDiscount.getValue()));
                    }
                }
            }
        } else if (shouldOrShouldnot.equals(SHOULD_NOT)) {
            discounts.stream().forEach(
                    (discount -> assertThat(discount.getCode()).doesNotContain(expectedDiscount.getCode())));
        }
        return this;
    }

    public FlightsAssertion posFeereturned(String shouldOrShouldnot, ItemModel expectedFee) {

        List<FindFlightsResponse.AugmentedPriceItem> fees = response.getOutbound()
                .getJourneys()
                .get(0)
                .getFlights()
                .get(0)
                .getFareTypes()
                .get(0)
                .getPassengers()
                .get(0)
                .getFees();
        if (shouldOrShouldnot.equals(SHOULD)) {
            for (int i = 0; i < fees.size(); i++) {
                Integer percentageFee = response.getOutbound()
                        .getJourneys()
                        .get(0)
                        .getFlights()
                        .get(0)
                        .getFareTypes()
                        .get(0)
                        .getPassengers()
                        .get(0)
                        .getFees()
                        .get(i)
                        .getPercentageValue();
                Double feeValue = response.getOutbound()
                        .getJourneys()
                        .get(0)
                        .getFlights()
                        .get(0)
                        .getFareTypes()
                        .get(0)
                        .getPassengers()
                        .get(0)
                        .getFees()
                        .get(i)
                        .getValue();
                if (fees.get(i).getCode().equals(expectedFee.getCode())) {
                    if (percentageFee == 0) {
                        assertThat(feeValue).isEqualTo(Double.parseDouble(expectedFee.getValue()));
                    } else {
                        assertThat(percentageFee).isEqualTo((int) Double.parseDouble(expectedFee.getValue()));
                    }
                }
            }
        } else if (shouldOrShouldnot.equals(SHOULD_NOT)) {
            for (int i = 0; i < fees.size(); i++) {
                assertThat(response.getOutbound()
                        .getJourneys()
                        .get(0)
                        .getFlights()
                        .get(0)
                        .getFareTypes()
                        .get(0)
                        .getPassengers()
                        .get(i)
                        .getFees()
                        .size()).isZero();
            }
        }

        return this;
    }

    public FlightsAssertion totalDiscounts(String shouldOrShouldnot, ItemModel expectedDiscount) {
        List<FindFlightsResponse.AugmentedPriceItem> discounts = response.getOutbound()
                .getJourneys()
                .get(0)
                .getFlights()
                .get(0)
                .getFareTypes()
                .get(0)
                .getPassengers()
                .get(0)
                .getDiscounts();

        if (shouldOrShouldnot.equals(SHOULD)) {
            Double totalDiscount = 0.00;
            for (int i = 0; i < discounts.size(); i++) {
                totalDiscount += discounts
                        .get(i)
                        .getValue();
            }

            assertThat(response.getOutbound()
                    .getJourneys()
                    .get(0)
                    .getFlights()
                    .get(0)
                    .getFareTypes()
                    .get(0)
                    .getPassengers()
                    .get(0)
                    .getTotalDiscounts()).isEqualTo((totalDiscount));
        }
        if (shouldOrShouldnot.equals(SHOULD_NOT)) {
            discounts.stream().forEach(
                    (discount -> assertThat(discount.getCode()).doesNotContain(expectedDiscount.getCode())));
            assertThat(discounts.get(0).getValue()).isNotEqualTo(discounts.get(0).getValue() + Double.parseDouble(expectedDiscount.getValue()));
        }

        return this;
    }

    public FlightsAssertion augmentedprice() {

        double ibaseprice = response.getOutbound()
                .getJourneys()
                .get(0)
                .getFlights()
                .get(0)
                .getFareTypes()
                .get(0)
                .getPassengers()
                .get(0)
                .getBasePrice();
        double itaxes = response.getOutbound()
                .getJourneys()
                .get(0)
                .getFlights()
                .get(0)
                .getFareTypes()
                .get(0)
                .getPassengers()
                .get(0)
                .getTotalTaxes();
        double idiscounts = response.getOutbound()
                .getJourneys()
                .get(0)
                .getFlights()
                .get(0)
                .getFareTypes()
                .get(0)
                .getPassengers()
                .get(0)
                .getTotalDiscounts();
        double ifees = response.getOutbound()
                .getJourneys()
                .get(0)
                .getFlights()
                .get(0)
                .getFareTypes()
                .get(0)
                .getPassengers()
                .get(0)
                .getTotalFees();
        double iAugmentedPrice = response.getOutbound()
                .getJourneys()
                .get(0)
                .getFlights()
                .get(0)
                .getFareTypes()
                .get(0)
                .getPassengers()
                .get(0)
                .getTotalPassengerFare()
                .getWithDebitCardFee();
        double actAugmentedPrice = ibaseprice + itaxes - idiscounts + ifees;
        assertThat(iAugmentedPrice).isEqualTo(actAugmentedPrice);
        return this;
    }

    public FlightsAssertion infantIsOnOwnSeat(String passengerMix) {

        FlightPassengers pax = new FlightPassengers(passengerMix);
        List<com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.Passenger> infantOnSeatPax = pax.getPassengers()
                .stream()
                .filter(com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.Passenger::getInfantOnSeat)
                .collect(Collectors.toList());

        assertThat(response.getOutbound()
                .getJourneys()
                .get(0)
                .getFlights()
                .get(0)
                .getFareTypes()).isNotNull();

        for (com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.Passenger p : infantOnSeatPax) {
            List<FindFlightsResponse.Passenger> matchedPaxTypes = response
                    .getOutbound()
                    .getJourneys()
                    .stream()
                    .flatMap(a -> a.getFlights().stream())
                    .flatMap(b -> b.getFareTypes().stream())
                    .flatMap(c -> c.getPassengers().stream())
                    .filter(d -> d.getType().equalsIgnoreCase(p.getPassengerType()) && d.getInfantOnSeat())
                    .collect(Collectors.toList());

            for (FindFlightsResponse.Passenger mPax : matchedPaxTypes) {
                assertThat(mPax.getInfantOnSeat()).isEqualTo(p.getInfantOnSeat());
            }

        }

        return this;
    }

    public FlightsAssertion theCreditCardFeeForEachPassengerIsCorrect(int decimalPosition) {

        response.getOutbound().getJourneys().forEach(
                journey -> journey.getFlights().forEach(
                        flight -> flight.getFareTypes().forEach(
                                fareType -> fareType.getPassengers().stream().filter(
                                        passengers -> passengers.getFees().stream().anyMatch(
                                                fee -> fee.getCode().equals(CR_CARD_FEE))).forEach(
                                        passenger -> {
                                            FindFlightsResponse.AugmentedPriceItem crFee = passenger.getFees()
                                                    .stream()
                                                    .filter(fee -> fee.getCode().equals(CR_CARD_FEE))
                                                    .findFirst().get();
                                            Double crFeeValue = new BigDecimal(crFee.getValue().toString()).doubleValue();
                                            BigDecimal crFeePercentage = new BigDecimal(
                                                    crFee.getPercentageValue().toString())
                                                    .multiply(new BigDecimal("0.01"));
                                            BigDecimal adminFee = new BigDecimal("0");
                                            Optional<FindFlightsResponse.AugmentedPriceItem> optionalAdminFee = passenger.getFees()
                                                    .stream().filter(fee -> fee.getCode().equals(ADMIN_FEE))
                                                    .findFirst();
                                            if (optionalAdminFee.isPresent()) {
                                                adminFee = new BigDecimal(optionalAdminFee.get().getValue().toString());
                                            }

                                            assertThat(crFeeValue).isEqualTo(
                                                    new BigDecimal(
                                                            computeTotalPerPassenger(passenger, decimalPosition)
                                                                    .get(TOT_WITH_DEBIT_CARD_FEE).toString())
                                                            .subtract(adminFee)
                                                            .multiply(crFeePercentage).setScale(decimalPosition, RoundingMode.HALF_UP)
                                                            .add(adminFee.multiply(crFeePercentage).setScale(decimalPosition, RoundingMode.UP))
                                                            .doubleValue()
                                            );
                                        }
                                )
                        )
                )
        );

        if (response.getInbound() != null) {
            response.getOutbound().getJourneys().forEach(
                    journey -> journey.getFlights().forEach(
                            flight -> flight.getFareTypes().forEach(
                                    fareType -> fareType.getPassengers().stream().filter(
                                            passengers -> passengers.getFees().stream().anyMatch(
                                                    fee -> fee.getCode().equals(CR_CARD_FEE))).forEach(
                                            passenger -> {
                                                FindFlightsResponse.AugmentedPriceItem crFee = passenger.getFees()
                                                        .stream()
                                                        .filter(fee -> fee.getCode().equals(CR_CARD_FEE))
                                                        .findFirst().get();
                                                Double crFeeValue = new BigDecimal(
                                                        crFee.getValue()
                                                                .toString()).doubleValue();
                                                BigDecimal crFeePercentage = new BigDecimal(
                                                        crFee.getPercentageValue().toString())
                                                        .multiply(new BigDecimal("0.01"));
                                                BigDecimal adminFee = new BigDecimal("0");
                                                Optional<FindFlightsResponse.AugmentedPriceItem> optionalAdminFee = passenger.getFees()
                                                        .stream()
                                                        .filter(fee -> fee.getCode().equals(ADMIN_FEE))
                                                        .findFirst();
                                                if (optionalAdminFee.isPresent()) {
                                                    adminFee = new BigDecimal(optionalAdminFee.get().getValue().toString());
                                                }

                                                assertThat(crFeeValue).isEqualTo(
                                                        new BigDecimal(
                                                                computeTotalPerPassenger(passenger, decimalPosition)
                                                                        .get(TOT_WITH_DEBIT_CARD_FEE).toString())
                                                                .subtract(adminFee)
                                                                .multiply(crFeePercentage).setScale(decimalPosition, RoundingMode.HALF_UP)
                                                                .add(adminFee.multiply(crFeePercentage)
                                                                        .setScale(decimalPosition, RoundingMode.UP))
                                                                .doubleValue()
                                                );
                                            }
                                    )
                            )
                    )
            );
        }

        return this;
    }

    private HashMap<String, Double> computeTotalPerPassenger(FindFlightsResponse.Passenger passenger, int decimalPlaces) {

        BigDecimal basePrice = new BigDecimal(passenger.getBasePrice().toString());
        BigDecimal totDiscount = passenger.getDiscounts().stream()
                .filter(Objects::nonNull)
                .map(discount -> new BigDecimal(discount.getValue().toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(decimalPlaces, RoundingMode.HALF_UP);
        BigDecimal totFees = passenger.getFees().stream()
                .filter(Objects::nonNull)
                .map(fee -> new BigDecimal(fee.getValue().toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(decimalPlaces, RoundingMode.HALF_UP);
        BigDecimal totTaxes = passenger.getTaxes().stream()
                .filter(Objects::nonNull)
                .map(tax -> new BigDecimal(tax.getValue().toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(decimalPlaces, RoundingMode.HALF_UP);

        BigDecimal crFeeValue = new BigDecimal("0");
        Optional<FindFlightsResponse.AugmentedPriceItem> optionalCRFee = passenger.getFees()
                .stream()
                .filter(fee -> fee.getCode().equals(CR_CARD_FEE))
                .findFirst();
        if (optionalCRFee.isPresent()) {
            crFeeValue = new BigDecimal(optionalCRFee.get().getValue().toString());
        }

        BigDecimal totForPassengers =
                basePrice
                        .subtract(totDiscount)
                        .add(totFees)
                        .subtract(crFeeValue)
                        .add(totTaxes);

        HashMap<String, Double> tot = new HashMap<>();
        tot.put(TOT_WITH_DEBIT_CARD_FEE, totForPassengers.doubleValue());
        tot.put(TOT_WITH_CREDIT_CARD_FEE, totForPassengers.add(crFeeValue).doubleValue());

        return tot;
    }

    public FlightsAssertion theAdminFeeForEachPassengerIsCorrect(int decimalPosition, BigDecimal totalPassengers, BigDecimal expectedAdminFee) {

        response.getOutbound().getJourneys().forEach(
                journey -> journey.getFlights().forEach(
                        flight -> flight.getFareTypes().forEach(
                                fareType -> fareType.getPassengers().stream().filter(
                                        passengers -> passengers.getFees().stream().anyMatch(
                                                fee -> fee.getCode().equals(ADMIN_FEE))).forEach(
                                        passenger -> {
                                            Double adminFee = passenger.getFees().stream()
                                                    .filter(fee -> fee.getCode().equals(ADMIN_FEE))
                                                    .findFirst().get().getValue();

                                            assertThat(adminFee).isEqualTo(expectedAdminFee.divide(totalPassengers, decimalPosition, RoundingMode.UP)
                                                    .doubleValue());
                                        }
                                )
                        )
                )
        );

        if (response.getInbound() != null) {
            response.getInbound().getJourneys().forEach(
                    journey -> journey.getFlights().forEach(
                            flight -> flight.getFareTypes().forEach(
                                    fareType -> fareType.getPassengers().stream().filter(
                                            passengers -> passengers.getFees().stream().anyMatch(
                                                    fee -> fee.getCode().equals(ADMIN_FEE))).forEach(
                                            passenger -> {
                                                Double adminFee = passenger.getFees().stream()
                                                        .filter(fee -> fee.getCode().equals(ADMIN_FEE))
                                                        .findFirst().get().getValue();

                                                assertThat(adminFee).isEqualTo(expectedAdminFee.divide(totalPassengers, decimalPosition, RoundingMode.UP)
                                                        .doubleValue());
                                            }
                                    )
                            )
                    )
            );
        }

        return this;
    }

    public FlightsAssertion theTotAmountForEachPassengerIsCorrect(int decimalPosition) {

        response.getOutbound().getJourneys().forEach(
                journey -> journey.getFlights().forEach(
                        flight -> flight.getFareTypes().forEach(
                                fareType -> fareType.getPassengers().forEach(
                                        passenger -> {

                                            HashMap<String, Double> expectedValues = computeTotalPerPassenger(passenger, decimalPosition);
                                            assertThat(passenger.getTotalPassengerFare().getWithDebitCardFee())
                                                    .isEqualTo(expectedValues.get(TOT_WITH_DEBIT_CARD_FEE));
                                            assertThat(passenger.getTotalPassengerFare().getWithCreditCardFee())
                                                    .isEqualTo(expectedValues.get(TOT_WITH_CREDIT_CARD_FEE));

                                        }
                                )
                        )
                )
        );

        if (response.getInbound() != null) {
            response.getInbound().getJourneys().forEach(
                    journey -> journey.getFlights().forEach(
                            flight -> flight.getFareTypes().forEach(
                                    fareType -> fareType.getPassengers().forEach(
                                            passenger -> {

                                                HashMap<String, Double> expectedValues = computeTotalPerPassenger(passenger, decimalPosition);
                                                assertThat(passenger.getTotalPassengerFare().getWithDebitCardFee())
                                                        .isEqualTo(expectedValues.get(TOT_WITH_DEBIT_CARD_FEE));
                                                assertThat(passenger.getTotalPassengerFare().getWithCreditCardFee())
                                                        .isEqualTo(expectedValues.get(TOT_WITH_CREDIT_CARD_FEE));

                                            }
                                    )
                            )
                    )
            );
        }

        return this;
    }

    public FlightsAssertion allFlightsReturnedAreInDirect() {

        allOutboundFlightsReturnedAreInDirect();
//        allInboundFlightsReturnedAreInDirect();
        return this;
    }

    public FlightsAssertion allOutboundFlightsReturnedAreInDirect() {

        List<FindFlightsResponse.Journey> outboundJourneys = response.getOutbound().getJourneys();
        for (FindFlightsResponse.Journey outboundJourney : outboundJourneys) {
            assertThat(isInDirectAndHasAtLeastOneStop(outboundJourney));
        }
        return this;
    }

    public FlightsAssertion allFlightsReturnWithinRange(String outBoundDate, String inBoundDate, String flexiDays) throws ParseException {
        List<FindFlightsResponse.Flight> flights;
        flights = response.getOutbound().getJourneys().stream().flatMap(flight -> flight.getFlights().stream()).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(flights)) {
            allFlightsReturnWithinRange(flights, outBoundDate, flexiDays);
        }
        flights = response.getInbound().getJourneys().stream().flatMap(flight -> flight.getFlights().stream()).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(flights)) {
            allFlightsReturnWithinRange(flights, inBoundDate, flexiDays);
        }
        return this;
    }

    public FlightsAssertion allFlightsReturnOutSideRange(String outBoundDate, String inBoundDate, String flexiDays) throws ParseException {
        List<FindFlightsResponse.Flight> flights;
        flights = response.getOutbound().getJourneys().stream().flatMap(flight -> flight.getFlights().stream()).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(flights)) {
            allFlightsAreNotReturnOutsideRange(flights, outBoundDate, flexiDays);
        }
        flights = response.getInbound().getJourneys().stream().flatMap(flight -> flight.getFlights().stream()).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(flights)) {
            allFlightsAreNotReturnOutsideRange(flights, inBoundDate, flexiDays);
        }
        return this;
    }

    public FlightsAssertion allFlightsReturnWithinRange(List<FindFlightsResponse.Flight> flights, String date, String flexiDays) throws ParseException {
        List<String> departureDate = new ArrayList<>();
        flights.forEach(
                flight -> {
                    try {
                        departureDate.add(DateFormat.getDateFromSpecificFormat(flight.getDeparture().getDate(), fromFormat));
                    } catch (ParseException e) {
                        LOG.error(e);
                    }
                }
        );
        List<String> supportedDates = getSupportedTravelDateWithinRange(date, flexiDays);
        Collections.sort(supportedDates);
        //check that the flight search result based on expected range
        assertThat(supportedDates).containsAll(departureDate);
        return this;
    }

    public FlightsAssertion allFlightsAreNotReturnOutsideRange(List<FindFlightsResponse.Flight> flights, String date, String flexiDays) throws ParseException {
        List<String> departureDate = new ArrayList<>();
        flights.forEach(
                flight -> {
                    try {
                        departureDate.add(DateFormat.getDateFromSpecificFormat(flight.getDeparture().getDate(), fromFormat));
                    } catch (ParseException e) {
                        LOG.error(HybrisCompromisedException.HybrisCompromisedExceptionMessages.INCORRECT_DATE);
                    }
                }
        );
        assertThat(departureDate).doesNotContain(new DateFormat().addDay(Integer.valueOf(flexiDays) + 1, date));
        assertThat(departureDate).doesNotContain(new DateFormat().addDay(-Integer.valueOf(flexiDays) - 1, date));
        return this;
    }

    public List<String> getSupportedTravelDateWithinRange(String date, String flexiDays) throws ParseException {
        List<String> expectedDates = new ArrayList<>();
        int flexiDay = Integer.parseInt(flexiDays);
        expectedDates.add(date);
        for (int index = 0; index < Integer.valueOf(flexiDays); index++) {
            String addDay = new DateFormat().addDay(flexiDay, date);
            expectedDates.add(addDay);
            addDay = new DateFormat().addDay(-flexiDay, date);
            expectedDates.add(addDay);
            flexiDay--;
        }
        return expectedDates;
    }

    private boolean isInDirectAndHasAtLeastOneStop(FindFlightsResponse.Journey journey) {

        return journey.getStops() > 0 && !journey.getIsDirect();
    }

    public FlightsAssertion allInboundFlightsReturnedAreInDirect() {

        List<FindFlightsResponse.Journey> inboundJourneys = response.getInbound().getJourneys();
        for (FindFlightsResponse.Journey inboundJourney : inboundJourneys) {
            assertThat(isInDirectAndHasAtLeastOneStop(inboundJourney));
        }
        return this;
    }

    public FlightsAssertion allOutboundJourneysAreValid(String expectedOrigin, String expectedDestination, List<String> outboundConnectingAirports, List<String> alternateOutboundDepartureAirports) {

        List<FindFlightsResponse.Journey> outboundJourneys = response.getOutbound().getJourneys();
        assertThat(outboundJourneys.size()).isGreaterThan(0);

        for (FindFlightsResponse.Journey outboundJourney : outboundJourneys) {
            assertThat(areAllValidConnectionsFor(outboundJourney, expectedOrigin, expectedDestination, outboundConnectingAirports, alternateOutboundDepartureAirports))
                    .isTrue();
        }
        return this;

    }

    /**
     * Returns true if flight1's arrival airport is equal to flight2's departure airport
     * and flight1's departure is the origin and flight2's arrival is the destination
     * and connecting airport is one of the expecting connecting airports
     **/
    private boolean areAllValidConnectionsFor(FindFlightsResponse.Journey journey, String expectedOrigin, String expectedDestination, List<String> expectedConnectingAirports, List<String> expectedAlternateAirports) {

        FindFlightsResponse.Flight flight1 = journey.getFlights().get(0);
        FindFlightsResponse.Flight flight2 = journey.getFlights().get(1);
        return isOriginWithinAlternateAriports(expectedAlternateAirports, expectedOrigin) &&
                getArrivalAirportCode(flight1).equals(getDepartureAirportCode(flight2)) &&
                getArrivalAirportCode(flight2).equals(expectedDestination) &&
                expectedConnectingAirports.contains(flight1.getArrival().getAirportCode());
    }

    private boolean isOriginWithinAlternateAriports(List<String> expectedAlternateAirports, String expectedOrigin) {

        if (expectedAlternateAirports.isEmpty()) {
            return true;
        } else {
            return expectedAlternateAirports.contains(expectedOrigin);
        }
    }

    private String getDepartureAirportCode(FindFlightsResponse.Flight flight) {

        return flight.getDeparture().getAirportCode();
    }

    private String getArrivalAirportCode(FindFlightsResponse.Flight flight) {

        return flight.getArrival().getAirportCode();
    }

    //need to check the logic
    public FlightsAssertion allOutboundJourneysAreAddedToBasket(List<FindFlightsResponse.Flight> actualFlights, BasketsResponse basket) {

        List<Basket.Flight> basketOutbound = basket.getBasket()
                .getOutbounds()
                .stream()
                .flatMap(f -> f.getFlights().stream())
                .collect(Collectors.toList());

        for (FindFlightsResponse.Flight flight : actualFlights
                ) {
            assertThat(basketOutbound.stream()
                    .filter(f -> Objects.equals(f.getFlightKey(), flight.getFlightKey()))).isNotNull();
            assertThat(basketOutbound.stream()
                    .filter(f -> Objects.equals(f.getDepartureDateTime(), flight.getDeparture().getDate()))).isNotNull();
        }
        assertThat(actualFlights.size()).isEqualTo(basket.getBasket().getOutbounds().size());
        return this;
    }

    public FlightsAssertion allInBoundJourneysAreAddedToBasket(List<FindFlightsResponse.Flight> actualFlights, BasketsResponse basket) {

        List<Basket.Flight> basketInbound = basket.getBasket()
                .getInbounds()
                .stream()
                .flatMap(f -> f.getFlights().stream())
                .collect(Collectors.toList());

        for (FindFlightsResponse.Flight flight : actualFlights
                ) {
            assertThat(basketInbound.stream()
                    .filter(f -> Objects.equals(f.getFlightKey(), flight.getFlightKey()))).isNotNull();
            assertThat(basketInbound.stream()
                    .filter(f -> Objects.equals(f.getDepartureDateTime(), flight.getDeparture().getDate()))).isNotNull();
        }
        assertThat(actualFlights.size()).isEqualTo(basket.getBasket().getInbounds().size());
        return this;
    }

    public FlightsAssertion allInboundJourneysAreValid(String expectedOrigin, String expectedDestination, List<String> inboundConnectingAirports, List<String> alternateInboundDepartureAirports) {

        List<FindFlightsResponse.Journey> inboundJourneys = response.getInbound().getJourneys();
        assertThat(inboundJourneys.size()).isGreaterThan(0);

        for (FindFlightsResponse.Journey inboundJourney : inboundJourneys) {
            assertThat(areAllValidConnectionsFor(inboundJourney, expectedOrigin, expectedDestination, inboundConnectingAirports, alternateInboundDepartureAirports))
                    .isTrue();
        }
        return this;
    }

    public FlightsAssertion allOutboundJourneysHasDepartureDateAs(String outboundDate) {

        List<FindFlightsResponse.Journey> outboundJourneys = response.getOutbound().getJourneys();
        for (FindFlightsResponse.Journey outboundJourney : outboundJourneys) {
            assertThat(outboundJourney.getFlights().get(0).getDeparture().getDate().equals(outboundDate));
        }
        return this;
    }

    public FlightsAssertion resultsAreInAscendingOrderOfTheirTotalDuration() {

        List<FindFlightsResponse.Journey> outboundJourneys = response.getOutbound().getJourneys();
        if (outboundJourneys.size() > 1) {
            for (int i = 0; i < outboundJourneys.size() - 1; i++) {
                assertThat(duration(outboundJourneys.get(i).getTotalDuration()) <= duration(outboundJourneys.get(i + 1)
                        .getTotalDuration()));
            }
        }
        return this;
    }

    private int duration(String totalDuration) {

        int hours = Integer.parseInt(totalDuration.split("h")[0]);
        String temp = totalDuration.split("h")[1];
        int minutes = Integer.parseInt(temp.split("m")[0]);
        return hours * 60 + minutes;
    }

    public FlightsAssertion noFlightsAreReturnedWithLessThanMinimumConnectionTime(long connectionTime) {

        assertMinimumConnectionTimeForJourneys(response.getOutbound().getJourneys(), connectionTime);
        assertMinimumConnectionTimeForJourneys(response.getInbound().getJourneys(), connectionTime);
        return this;
    }

    private void assertMinimumConnectionTimeForJourneys(List<FindFlightsResponse.Journey> aJourneys, long connectionTime) {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("E dd-MMM-yyyy HH:mm:ss");

        for (FindFlightsResponse.Journey journey : aJourneys) {
            FindFlightsResponse.Flight flight1 = journey.getFlights().get(0);
            FindFlightsResponse.Flight flight2 = journey.getFlights().get(1);
            LocalDateTime flight1Arrive = LocalDateTime.parse(flight1.getArrival().getDate(), formatter);
            LocalDateTime flight2Depart = LocalDateTime.parse(flight2.getDeparture().getDate(), formatter);

            assertThat(Duration.between(flight1Arrive, flight2Depart).toMinutes()).isGreaterThan(connectionTime);
        }
    }

    public FlightsAssertion noFlightsAreReturnedWithDurationGreaterThanMaximumAllowed(long maximumDuration) {

        assertMaximumDuration(response.getOutbound().getJourneys(), maximumDuration);
        return this;
    }

    private void assertMaximumDuration(List<FindFlightsResponse.Journey> aJourneys, long maximumDuration) {

        Duration maxDuration = Duration.ofMinutes(maximumDuration);

        for (FindFlightsResponse.Journey journey : aJourneys) {
            String hours = journey.getTotalDuration().split("h")[0];
            String minutes = journey.getTotalDuration().split("h")[1].split("m")[0];
            String seconds = journey.getTotalDuration().split("m")[1].replace("s", "");

            Duration actualDuration = Duration.ofHours(Long.parseLong(hours));
            actualDuration = actualDuration.plusMinutes(Long.parseLong(minutes));
            actualDuration = actualDuration.plusSeconds(Long.parseLong(seconds));
            assertThat(actualDuration).isLessThan(maxDuration);

        }

    }

    public FlightsAssertion passengerMixIsAddedWithAdditionalSeat(String passengerMix) {

        String[] passengers = passengerMix.split("\\s+;\\s+");
        HashMap<String, String> passengersList = new HashMap<>();
        for (String passenger : passengers) {
            passengersList.put(passenger.split("\\s+")[1], passenger.split("\\s+")[0]);
        }

        response.getOutbound().getJourneys().forEach(
                journey -> journey.getFlights().forEach(
                        flight -> flight.getFareTypes().forEach(
                                fareType -> fareType.getPassengers().forEach(
                                        passenger -> {
                                            assertThat(passenger.getAdditionalSeats()).isEqualTo(Integer.valueOf(passengersList.get(passenger
                                                    .getType()).split(",")[1]));
                                            assertThat(passenger.getQuantity()).isEqualTo(Integer.valueOf(passengersList.get(passenger.getType())
                                                    .split(",")[0]));
                                        }
                                )
                        )
                ));

        if (response.getInbound() != null) {
            response.getInbound().getJourneys().forEach(
                    journey -> journey.getFlights().forEach(
                            flight -> flight.getFareTypes().forEach(
                                    fareType -> fareType.getPassengers().forEach(
                                            passenger -> {
                                                assertThat(passenger.getAdditionalSeats()).isEqualTo(Integer.valueOf(passengersList.get(passenger
                                                        .getType()).split(",")[1]));
                                                assertThat(passenger.getQuantity()).isEqualTo(Integer.valueOf(passengersList.get(passenger
                                                        .getType()).split(",")[0]));
                                            }
                                    )
                            )
                    ));
        }

        return this;
    }

    public FlightsAssertion feesAndTaxesAreAppliedToTheAdditionalSeat(String passengerMix) {

        String[] passengers = passengerMix.split("\\s+;\\s+");
        HashMap<String, String> passengersList = new HashMap<>();
        for (String passenger : passengers) {
            passengersList.put(passenger.split("\\s+")[1], passenger.split("\\s+")[0]);
        }

        response.getOutbound().getJourneys().forEach(
                journey -> journey.getFlights().forEach(
                        flight -> flight.getFareTypes().forEach(
                                fareType -> fareType.getPassengers().forEach(
                                        passenger -> {
                                            HashMap<String, Double> tot = computeTotalPerFareType(passenger);
                                            assertThat(fareType.getTotalFare()
                                                    .getWithDebitCardFee()).isEqualTo(tot.get(TOT_WITH_DEBIT_CARD_FEE));
                                            assertThat(fareType.getTotalFare()
                                                    .getWithCreditCardFee()).isEqualTo(tot.get(TOT_WITH_CREDIT_CARD_FEE));
                                        }
                                )
                        )
                )
        );

        if (response.getInbound() != null) {
            response.getInbound().getJourneys().forEach(
                    journey -> journey.getFlights().forEach(
                            flight -> flight.getFareTypes().forEach(
                                    fareType -> fareType.getPassengers().forEach(
                                            passenger -> {
                                                HashMap<String, Double> tot = computeTotalPerFareType(passenger);
                                                assertThat(fareType.getTotalFare()
                                                        .getWithDebitCardFee()).isEqualTo(tot.get(TOT_WITH_DEBIT_CARD_FEE));
                                                assertThat(fareType.getTotalFare()
                                                        .getWithCreditCardFee()).isEqualTo(tot.get(TOT_WITH_CREDIT_CARD_FEE));
                                            }
                                    )
                            )
                    )
            );
        }

        return this;
    }

    private HashMap<String, Double> computeTotalPerFareType(FindFlightsResponse.Passenger passenger) {

        return computeTotalPerFareType(passenger, 2);
    }

    private HashMap<String, Double> computeTotalPerFareType(FindFlightsResponse.Passenger passenger, int decimalPlaces) {

        BigDecimal quantity = new BigDecimal(passenger.getQuantity());
        BigDecimal additionalSeat = new BigDecimal(passenger.getAdditionalSeats());
        BigDecimal basePrice = new BigDecimal(passenger.getBasePrice().toString());
        BigDecimal totDiscount = passenger.getDiscounts().stream()
                .filter(Objects::nonNull)
                .map(discount -> new BigDecimal(discount.getValue().toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(decimalPlaces, RoundingMode.HALF_UP);
        BigDecimal totFees = passenger.getFees().stream()
                .filter(Objects::nonNull)
                .map(fee -> new BigDecimal(fee.getValue().toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(decimalPlaces, RoundingMode.HALF_UP);
        BigDecimal totTaxes = passenger.getTaxes().stream()
                .filter(Objects::nonNull)
                .map(tax -> new BigDecimal(tax.getValue().toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(decimalPlaces, RoundingMode.HALF_UP);
        BigDecimal adminFee = new BigDecimal("0");
        Optional<FindFlightsResponse.AugmentedPriceItem> optionalAdminFee = passenger.getFees()
                .stream()
                .filter(fee -> fee.getCode().equals(ADMIN_FEE))
                .findFirst();
        if (optionalAdminFee.isPresent()) {
            adminFee = new BigDecimal(optionalAdminFee.get().getValue().toString());
        }
        BigDecimal crFeeValue = new BigDecimal("0");
        BigDecimal crFeePercentage = new BigDecimal("1");
        Optional<FindFlightsResponse.AugmentedPriceItem> optionalCRFee = passenger.getFees()
                .stream()
                .filter(fee -> fee.getCode().equals(CR_CARD_FEE))
                .findFirst();
        if (optionalCRFee.isPresent()) {
            crFeeValue = new BigDecimal(optionalCRFee.get().getValue().toString());
            crFeePercentage = new BigDecimal(optionalCRFee.get()
                    .getPercentageValue()
                    .toString()).multiply(new BigDecimal("0.01")).add(new BigDecimal("1"));
        }

        BigDecimal totForPassengers = basePrice.subtract(totDiscount)
                .add(totFees)
                .subtract(crFeeValue)
                .add(totTaxes)
                .multiply(quantity)
                .setScale(decimalPlaces, RoundingMode.HALF_UP);
        BigDecimal totForAdditionalSeats = basePrice.subtract(totDiscount)
                .add(totFees)
                .subtract(adminFee)
                .subtract(crFeeValue)
                .add(totTaxes)
                .multiply(additionalSeat)
                .setScale(decimalPlaces, RoundingMode.HALF_UP);

        HashMap<String, Double> tot = new HashMap<>();
        tot.put(TOT_WITH_DEBIT_CARD_FEE,
                totForPassengers.add(totForAdditionalSeats)
                        .doubleValue());
        tot.put(TOT_WITH_CREDIT_CARD_FEE,
                totForPassengers.subtract(adminFee).multiply(crFeePercentage)
                        .setScale(decimalPlaces, RoundingMode.HALF_UP)
                        .add(adminFee.multiply(crFeePercentage).setScale(decimalPlaces, RoundingMode.UP))
                        .add(totForAdditionalSeats.multiply(crFeePercentage).setScale(decimalPlaces, RoundingMode.HALF_UP))
                        .doubleValue());

        return tot;

    }

    public FlightsAssertion standbyBundlesAreNotDisplayed() {

        response.getOutbound().getJourneys().forEach(
                journey -> journey.getFlights().forEach(
                        flight -> flight.getFareTypes().forEach(
                                fareType -> assertThat(fareType.getFareTypeCode()).isNotEqualTo(STANDBY)
                        )
                )
        );

        if (response.getInbound() != null) {
            response.getInbound().getJourneys().forEach(
                    journey -> journey.getFlights().forEach(
                            flight -> flight.getFareTypes().forEach(
                                    fareType -> assertThat(fareType.getFareTypeCode()).isNotEqualTo(STANDBY)
                            )
                    )
            );
        }

        return this;
    }

    public FlightsAssertion staffBundlesAreReturned() {

        List<FindFlightsResponse.FareType> fareTypes = response.getOutbound().getJourneys().stream()
                .flatMap(journey -> journey.getFlights().stream())
                .flatMap(flight -> flight.getFareTypes().stream())
                .filter(fareType ->
                        fareType.getFareTypeCode().equals(STAFF)
                                || fareType.getFareTypeCode().equals(STAFF_STANDARD)
                                || fareType.getFareTypeCode().equals(STANDBY))
                .collect(Collectors.toList());

        assertThat(fareTypes).isNotEmpty();

        if (response.getInbound() != null) {
            fareTypes = response.getInbound().getJourneys().stream()
                    .flatMap(journey -> journey.getFlights().stream())
                    .flatMap(flight -> flight.getFareTypes().stream())
                    .filter(fareType ->
                            fareType.getFareTypeCode().equals(STAFF)
                                    || fareType.getFareTypeCode().equals(STAFF_STANDARD)
                                    || fareType.getFareTypeCode().equals(STANDBY))
                    .collect(Collectors.toList());

            assertThat(fareTypes).isNotEmpty();
        }

        return this;

    }

    public FlightsAssertion staffBundlesDontHaveAdminFeeOrCRFee() {

        List<FindFlightsResponse.FareType> fareTypes = response.getOutbound().getJourneys().stream()
                .flatMap(journey -> journey.getFlights().stream())
                .flatMap(flight -> flight.getFareTypes().stream())
                .filter(fareType ->
                        fareType.getFareTypeCode().equals(STAFF)
                                || fareType.getFareTypeCode().equals(STAFF_STANDARD)
                                || fareType.getFareTypeCode().equals(STANDBY))
                .collect(Collectors.toList());

        fareTypes.forEach(
                fareType -> fareType.getPassengers().forEach(
                        passenger ->
                                assertThat(passenger.getFees()
                                        .stream()
                                        .filter(fee -> fee.getCode().equals(ADMIN_FEE) || fee.getCode().equals(CR_CARD_FEE))
                                        .collect(Collectors.toList())).isEmpty()
                )
        );

        if (response.getInbound() != null) {
            fareTypes = response.getInbound().getJourneys().stream()
                    .flatMap(journey -> journey.getFlights().stream())
                    .flatMap(flight -> flight.getFareTypes().stream())
                    .filter(fareType ->
                            fareType.getFareTypeCode().equals(STAFF)
                                    || fareType.getFareTypeCode().equals(STAFF_STANDARD)
                                    || fareType.getFareTypeCode().equals(STANDBY))
                    .collect(Collectors.toList());

            fareTypes.forEach(
                    fareType -> fareType.getPassengers().forEach(
                            passenger ->
                                    assertThat(passenger.getFees()
                                            .stream()
                                            .filter(fee -> fee.getCode().equals(ADMIN_FEE) || fee.getCode().equals(CR_CARD_FEE))
                                            .collect(Collectors.toList())).isEmpty()
                    )
            );
        }

        return this;

    }

    public FlightsAssertion staffBundlesIncludeTaxes(Map<String, Double> taxes) {

        List<FindFlightsResponse.FareType> fareTypes = response.getOutbound().getJourneys().stream()
                .flatMap(journey -> journey.getFlights().stream())
                .flatMap(flight -> flight.getFareTypes().stream())
                .filter(fareType ->
                        fareType.getFareTypeCode().equals(STAFF)
                                || fareType.getFareTypeCode().equals(STAFF_STANDARD)
                                || fareType.getFareTypeCode().equals(STANDBY))
                .collect(Collectors.toList());

        fareTypes.forEach(
                fareType -> fareType.getPassengers().forEach(
                        passenger -> {
                            for (Map.Entry<String, Double> tax : taxes.entrySet()) {
                                assertThat(passenger.getTaxes()).extracting("code", "value").contains(
                                        Tuple.tuple(
                                                tax.getKey(),
                                                tax.getValue()
                                        )
                                );
                            }
                        }
                )
        );

        if (response.getInbound() != null) {
            fareTypes = response.getInbound().getJourneys().stream()
                    .flatMap(journey -> journey.getFlights().stream())
                    .flatMap(flight -> flight.getFareTypes().stream())
                    .filter(fareType ->
                            fareType.getFareTypeCode().equals(STAFF)
                                    || fareType.getFareTypeCode().equals(STAFF_STANDARD)
                                    || fareType.getFareTypeCode().equals(STANDBY))
                    .collect(Collectors.toList());

            fareTypes.forEach(
                    fareType -> fareType.getPassengers().forEach(
                            passenger -> {
                                for (Map.Entry<String, Double> tax : taxes.entrySet()) {
                                    assertThat(passenger.getTaxes()).extracting("code", "value").contains(
                                            Tuple.tuple(
                                                    tax.getKey(),
                                                    tax.getValue()
                                            )
                                    );
                                }
                            }
                    )
            );
        }

        return this;

    }

    public FlightsAssertion priceCalculationAreRight(int decimalPlaces) {

        response.getOutbound().getJourneys().forEach(
                journey -> journey.getFlights().forEach(
                        flight -> flight.getFareTypes().forEach(
                                fareType -> {
                                    HashMap<String, BigDecimal> totalsPerFareType = new HashMap<>();
                                    totalsPerFareType.put(TOT_WITH_DEBIT_CARD_FEE, BigDecimal.ZERO);
                                    totalsPerFareType.put(TOT_WITH_CREDIT_CARD_FEE, BigDecimal.ZERO);
                                    fareType.getPassengers().forEach(
                                            passenger -> {

                                                BigDecimal totDiscount = calculateTotalPerItem(passenger.getDiscounts()
                                                        .stream(), decimalPlaces);
                                                assertThat(passenger.getTotalDiscounts()).isEqualTo(totDiscount.doubleValue());

                                                BigDecimal crFeeValue = new BigDecimal("0");
                                                Optional<FindFlightsResponse.AugmentedPriceItem> optionalCRFee = passenger.getFees()
                                                        .stream()
                                                        .filter(fee -> fee.getCode().equals(CR_CARD_FEE))
                                                        .findFirst();
                                                if (optionalCRFee.isPresent()) {
                                                    crFeeValue = new BigDecimal(optionalCRFee.get().getValue().toString());
                                                }
                                                BigDecimal totFees = calculateTotalPerItem(passenger.getFees().stream(), decimalPlaces).subtract(crFeeValue);
                                                assertThat(passenger.getTotalFees()).isEqualTo(totFees.doubleValue());

                                                BigDecimal totTaxes = calculateTotalPerItem(passenger.getTaxes()
                                                        .stream(), decimalPlaces);
                                                assertThat(passenger.getTotalTaxes()).isEqualTo(totTaxes.doubleValue());

                                                HashMap<String, BigDecimal> totalsPerPassenger = calculateTotalPerPassenger(passenger, decimalPlaces);

                                                assertThat(passenger.getTotalPassengerFare()
                                                        .getWithDebitCardFee()).isEqualTo(totalsPerPassenger.get(TOT_WITH_DEBIT_CARD_FEE)
                                                        .doubleValue());
                                                assertThat(passenger.getTotalPassengerFare()
                                                        .getWithCreditCardFee()).isEqualTo(totalsPerPassenger.get(TOT_WITH_CREDIT_CARD_FEE)
                                                        .doubleValue());

                                                BigDecimal additionalSeat = new BigDecimal(passenger.getAdditionalSeats().toString());
                                                BigDecimal quantity = new BigDecimal(passenger.getQuantity().toString());

                                                HashMap<String, BigDecimal> totalsPerAdditionalSeat = calculateTotalPerAdditionalSeat(passenger, decimalPlaces);

                                                totalsPerFareType.merge(TOT_WITH_DEBIT_CARD_FEE,
                                                        totalsPerPassenger.get(TOT_WITH_DEBIT_CARD_FEE)
                                                                .multiply(quantity),
                                                        BigDecimal::add);
                                                totalsPerFareType.merge(TOT_WITH_DEBIT_CARD_FEE,
                                                        totalsPerAdditionalSeat.get(TOT_WITH_DEBIT_CARD_FEE)
                                                                .multiply(additionalSeat),
                                                        BigDecimal::add);

                                                totalsPerFareType.merge(TOT_WITH_CREDIT_CARD_FEE,
                                                        totalsPerPassenger.get(TOT_WITH_CREDIT_CARD_FEE)
                                                                .multiply(quantity),
                                                        BigDecimal::add);
                                                totalsPerFareType.merge(TOT_WITH_CREDIT_CARD_FEE,
                                                        totalsPerAdditionalSeat.get(TOT_WITH_CREDIT_CARD_FEE)
                                                                .multiply(additionalSeat),
                                                        BigDecimal::add);

                                            }
                                    );

                                    assertThat(fareType.getTotalFare()
                                            .getWithDebitCardFee()).isEqualTo(totalsPerFareType.get(TOT_WITH_DEBIT_CARD_FEE).doubleValue());
                                    assertThat(fareType.getTotalFare()
                                            .getWithCreditCardFee()).isEqualTo(totalsPerFareType.get(TOT_WITH_CREDIT_CARD_FEE).doubleValue());
                                }
                        )
                )
        );

        return this;
    }

    private BigDecimal calculateTotalPerItem(Stream<FindFlightsResponse.AugmentedPriceItem> items, int decimalPlaces) {

        return items.filter(Objects::nonNull)
                .map(augmentePriceItem -> (new BigDecimal((augmentePriceItem.getValue().toString()))))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(decimalPlaces, RoundingMode.HALF_UP);

    }

    private HashMap<String, BigDecimal> calculateTotalPerPassenger(FindFlightsResponse.Passenger passenger, int decimalPlaces) {

        BigDecimal basePrice = new BigDecimal(passenger.getBasePrice().toString());
        BigDecimal totDiscount = calculateTotalPerItem(passenger.getDiscounts().stream(), decimalPlaces);
        BigDecimal totFees = calculateTotalPerItem(passenger.getFees().stream(), decimalPlaces);
        BigDecimal totTaxes = calculateTotalPerItem(passenger.getTaxes().stream(), decimalPlaces);

        BigDecimal adminFee = new BigDecimal("0");
        Optional<FindFlightsResponse.AugmentedPriceItem> optionalAdminFee = passenger.getFees()
                .stream()
                .filter(fee -> fee.getCode().equals(ADMIN_FEE))
                .findFirst();
        if (optionalAdminFee.isPresent()) {
            adminFee = new BigDecimal(optionalAdminFee.get().getValue().toString());
        }

        BigDecimal crFeeValue = new BigDecimal("0");
        BigDecimal crFeePercentage = new BigDecimal("1");
        Optional<FindFlightsResponse.AugmentedPriceItem> optionalCRFee = passenger.getFees()
                .stream()
                .filter(fee -> fee.getCode().equals(CR_CARD_FEE))
                .findFirst();
        if (optionalCRFee.isPresent()) {
            crFeeValue = new BigDecimal(optionalCRFee.get().getValue().toString());
            crFeePercentage = new BigDecimal(optionalCRFee.get()
                    .getPercentageValue()
                    .toString()).multiply(new BigDecimal("0.01")).add(new BigDecimal("1"));
        }

        BigDecimal totForPassengers = basePrice.subtract(totDiscount)
                .add(totFees)
                .subtract(adminFee)
                .subtract(crFeeValue)
                .add(totTaxes)
                .setScale(decimalPlaces, RoundingMode.HALF_UP);

        HashMap<String, BigDecimal> tot = new HashMap<>();

        tot.put(TOT_WITH_DEBIT_CARD_FEE,
                totForPassengers.add(adminFee));
        tot.put(TOT_WITH_CREDIT_CARD_FEE,
                totForPassengers.multiply(crFeePercentage)
                        .setScale(decimalPlaces, RoundingMode.HALF_UP)
                        .add(adminFee.multiply(crFeePercentage).setScale(decimalPlaces, RoundingMode.UP)));

        return tot;
    }

    private HashMap<String, BigDecimal> calculateTotalPerAdditionalSeat(FindFlightsResponse.Passenger passenger, int decimalPlaces) {

        BigDecimal basePrice = new BigDecimal(passenger.getBasePrice().toString());
        BigDecimal totDiscount = calculateTotalPerItem(passenger.getDiscounts().stream(), decimalPlaces);
        BigDecimal totFees = calculateTotalPerItem(passenger.getFees().stream(), decimalPlaces);
        BigDecimal totTaxes = calculateTotalPerItem(passenger.getTaxes().stream(), decimalPlaces);

        BigDecimal adminFee = new BigDecimal("0");
        Optional<FindFlightsResponse.AugmentedPriceItem> optionalAdminFee = passenger.getFees()
                .stream()
                .filter(fee -> fee.getCode().equals(ADMIN_FEE))
                .findFirst();
        if (optionalAdminFee.isPresent()) {
            adminFee = new BigDecimal(optionalAdminFee.get().getValue().toString());
        }

        BigDecimal crFeeValue = new BigDecimal("0");
        BigDecimal crFeePercentage = new BigDecimal("1");
        Optional<FindFlightsResponse.AugmentedPriceItem> optionalCRFee = passenger.getFees()
                .stream()
                .filter(fee -> fee.getCode().equals(CR_CARD_FEE))
                .findFirst();
        if (optionalCRFee.isPresent()) {
            crFeeValue = new BigDecimal(optionalCRFee.get().getValue().toString());
            crFeePercentage = new BigDecimal(optionalCRFee.get()
                    .getPercentageValue()
                    .toString()).multiply(new BigDecimal("0.01")).add(new BigDecimal("1"));
        }

        BigDecimal totForPassengers = basePrice.subtract(totDiscount)
                .add(totFees)
                .subtract(adminFee)
                .subtract(crFeeValue)
                .add(totTaxes)
                .setScale(decimalPlaces, RoundingMode.HALF_UP);

        HashMap<String, BigDecimal> tot = new HashMap<>();
        tot.put(TOT_WITH_DEBIT_CARD_FEE,
                totForPassengers);
        tot.put(TOT_WITH_CREDIT_CARD_FEE,
                totForPassengers.multiply(crFeePercentage)
                        .setScale(decimalPlaces, RoundingMode.HALF_UP));

        return tot;
    }

    public FlightsAssertion fareTypeIsAvailable(String bundle) {

        assertThat(response.getOutbound().getJourneys().stream()
                .flatMap(f -> f.getFlights().stream())
                .filter(g -> g.getFareTypes().stream().anyMatch(u -> u.getFareTypeCode().equals(bundle)))
                .findFirst()
                .orElse(null)
        ).isNotNull();

        return this;

    }

    public FlightsAssertion fareTypeIsNotAvailable(String bundle) {

        assertThat(response.getOutbound().getJourneys().stream()
                .flatMap(f -> f.getFlights().stream())
                .filter(g -> g.getFareTypes().stream().anyMatch(u -> u.getFareTypeCode().equals(bundle)))
                .findFirst()
                .orElse(null)
        ).isNull();

        return this;

    }

    public FlightsAssertion priceAreUpdatedWithBasketCurrency(List<Double> newPrices, CurrencyModel oldCurrency, CurrencyModel newCurrency, CurrencyModel baseCurrency, BigDecimal margin) {
        List<Double> originalPrices = response.getOutbound().getJourneys().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(FindFlightsResponse.Flight::getFareTypes).flatMap(Collection::stream)
                .map(FindFlightsResponse.FareType::getPassengers).flatMap(Collection::stream)
                .map(FindFlightsResponse.Passenger::getBasePrice)
                .collect(Collectors.toList());

        assertThat(newPrices.size()).isEqualTo(originalPrices.size());

        for (int i = 0; i < newPrices.size(); i++) {
            double expectedPrice = new BigDecimal(originalPrices.get(i).toString())
                    .divide(new BigDecimal(oldCurrency.getConversion()), baseCurrency.getDecimalPlaces(), RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(newCurrency.getConversion())).setScale(newCurrency.getDecimalPlaces(), RoundingMode.HALF_UP)
                    .multiply(margin).setScale(newCurrency.getDecimalPlaces(), RoundingMode.HALF_UP)
                    .doubleValue();
            assertThat(newPrices.get(i))
                    .withFailMessage("The price returned by find flight is not right: expected was " + expectedPrice + "; actual is " + newPrices.get(i))
                    .isEqualTo(expectedPrice);
        }

        return this;
    }

    @Step("{0} {1} displayed")
    public FlightsAssertion feeIsDisplayed(String feeCode, String display) {
        Stream<FindFlightsResponse.AugmentedPriceItem> feeStream = response.getOutbound().getJourneys().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(FindFlightsResponse.Flight::getFareTypes).flatMap(Collection::stream)
                .map(FindFlightsResponse.FareType::getPassengers).flatMap(Collection::stream)
                .map(FindFlightsResponse.Passenger::getFees).flatMap(Collection::stream);
        boolean check;
        if ("is".equals(display)) {
            check = feeStream
                    .anyMatch(fee -> fee.getCode().equals(feeCode));
        } else {
            check = feeStream
                    .noneMatch(fee -> fee.getCode().equals(feeCode));
        }
        assertThat(check)
                .withFailMessage(feeCode + " " + display + " displayed")
                .isTrue();

        return this;
    }

    @Step("{0} {1} displayed")
    public FlightsAssertion discountIsDisplayed(String discountCode, String display) {
        Stream<FindFlightsResponse.AugmentedPriceItem> feeStream = response.getOutbound().getJourneys().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(FindFlightsResponse.Flight::getFareTypes).flatMap(Collection::stream)
                .map(FindFlightsResponse.FareType::getPassengers).flatMap(Collection::stream)
                .map(FindFlightsResponse.Passenger::getDiscounts).flatMap(Collection::stream);
        boolean check;
        if (display.equals("is")) {
            check = feeStream
                    .anyMatch(fee -> fee.getCode().equals(discountCode));
        } else {
            check = feeStream
                    .noneMatch(fee -> fee.getCode().equals(discountCode));
        }
        assertThat(check)
                .withFailMessage(discountCode + " " + display + " displayed")
                .isTrue();

        return this;
    }

    @Step("Flights bundle is {0}")
    public FlightsAssertion flightsBundleIs(List<String> bundles) {
        Stream<FindFlightsResponse.Journey> journeys = response.getOutbound().getJourneys().stream();
        if (response.getInbound() != null) {
            journeys = Stream.concat(journeys, response.getInbound().getJourneys().stream());
        }
        assertThat(journeys
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(FindFlightsResponse.Flight::getFareTypes).flatMap(Collection::stream)
                .map(FindFlightsResponse.FareType::getFareTypeCode)
                .collect(Collectors.toList()))
                .withFailMessage("Not all the flights have the specified bundle")
                .containsOnlyElementsOf(bundles);
        return this;
    }

    @Step("Flight price is the delta with original price")
    public FlightsAssertion flightsPriceIsTheDeltaWithChangedFlight(Basket basket) {

        HashMap<String, BigDecimal> prices = new HashMap<>();
        prices.put(ADULT, calculateTotalPerBasketPassengerType(basket, ADULT));
        prices.put(CHILD, calculateTotalPerBasketPassengerType(basket, CHILD));
        prices.put(INFANT_ON_SEAT, calculateTotalPerBasketPassengerType(basket, INFANT_ON_SEAT));
        prices.put(INFANT_ON_LAP, calculateTotalPerBasketPassengerType(basket, INFANT_ON_LAP));

        Stream<FindFlightsResponse.Journey> journeys = response.getOutbound().getJourneys().stream();
        if (response.getInbound() != null) {
            journeys = Stream.concat(journeys, response.getInbound().getJourneys().stream());
        }

        int decimalPlaces = Integer.parseInt(basket.getCurrency().getDecimalPlaces()); //NOSONAR sonar identify it as unused

        journeys
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(FindFlightsResponse.Flight::getFareTypes).flatMap(Collection::stream)
                .map(FindFlightsResponse.FareType::getPassengers).flatMap(Collection::stream)
                .forEach(
                        passenger -> {
                            BigDecimal actualDebitPrice = new BigDecimal(passenger.getTotalPassengerFare().getWithDebitCardFee().toString()); //NOSONAR sonar identify it as unused
                            Double actualDebitDifference = passenger.getFarePriceDifference().getWithDebitCardFee(); //NOSONAR sonar identify it as unused
                            Double actualCreditDifference = passenger.getFarePriceDifference().getWithCreditCardFee(); //NOSONAR sonar identify it as unused

                            Double expectedDebitDifference; //NOSONAR sonar identify it as unused
                            if (passenger.getType().equals(INFANT)) {
                                if (passenger.getInfantOnSeat()) {
                                    expectedDebitDifference = Math.max(actualDebitPrice.subtract(prices.get(INFANT_ON_SEAT)).setScale(decimalPlaces, RoundingMode.HALF_UP).doubleValue(), 0.0); //NOSONAR sonar identify it as unused
                                } else {
                                    expectedDebitDifference = Math.max(actualDebitPrice.subtract(prices.get(INFANT_ON_LAP)).setScale(decimalPlaces, RoundingMode.HALF_UP).doubleValue(), 0.0); //NOSONAR sonar identify it as unused
                                }
                            } else {
                                expectedDebitDifference = Math.max(actualDebitPrice.subtract(prices.get(passenger.getType())).setScale(decimalPlaces, RoundingMode.HALF_UP).doubleValue(), 0.0); //NOSONAR sonar identify it as unused
                            }

                            BigDecimal crFeePercentage = new BigDecimal(passenger.getFees().stream() //NOSONAR sonar identify it as unused
                                    .filter(fee -> fee.getCode().equals(CR_CARD_FEE))
                                    .findFirst()
                                    .map(FindFlightsResponse.AugmentedPriceItem::getPercentageValue)
                                    .orElse(0).toString())
                                    .multiply(new BigDecimal("0.01")).setScale(2, RoundingMode.UP)
                                    .add(BigDecimal.ONE);

                            Double expectedCreditDifference = new BigDecimal(expectedDebitDifference.toString()).multiply(crFeePercentage).setScale(decimalPlaces, RoundingMode.HALF_UP).doubleValue(); //NOSONAR sonar identify it as unused

                            assertThat(actualDebitDifference)
                                    .withFailMessage(wrongPriceDifference("debit", passenger.getType(), expectedDebitDifference, actualDebitDifference))
                                    .isEqualTo(expectedDebitDifference);

                            assertThat(actualCreditDifference)
                                    .withFailMessage(wrongPriceDifference("credit", passenger.getType(), expectedCreditDifference, actualCreditDifference))
                                    .isEqualTo(expectedCreditDifference);
                        }
                );

        return this;
    }

    private BigDecimal calculateTotalPerBasketPassengerType(Basket basket, String passengerType) {
        int decimalPlaces = Integer.parseInt(basket.getCurrency().getDecimalPlaces());

        Optional<Basket.Passenger> foundPassenger;
        switch (passengerType) {
            case INFANT_ON_LAP:
                foundPassenger = basket.getOutbounds().stream()
                        .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                        .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                        .filter(passenger -> passenger.getPassengerDetails().getPassengerType().equals(INFANT))
                        .filter(passenger -> passenger.getFareProduct().getBundleCode().equals("InfantOnLap"))
                        .findFirst();
                break;
            case INFANT_ON_SEAT:
                foundPassenger = basket.getOutbounds().stream()
                        .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                        .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                        .filter(passenger -> passenger.getPassengerDetails().getPassengerType().equals(INFANT))
                        .filter(passenger -> !passenger.getFareProduct().getBundleCode().equals("InfantOnLap"))
                        .findFirst();
                break;
            default:
                foundPassenger = basket.getOutbounds().stream()
                        .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                        .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                        .filter(passenger -> passenger.getPassengerDetails().getPassengerType().equals(passengerType))
                        .findFirst();
                break;
        }

        BigDecimal totals = BigDecimal.ZERO;

        if (foundPassenger.isPresent()) {
            Basket.Passenger passenger = foundPassenger.get();
            BigDecimal basePrice = new BigDecimal(passenger.getFareProduct().getPricing().getBasePrice().toString());
            BigDecimal totDiscount = calculateTotalPerAugmentedPriceItem(
                    passenger.getFareProduct()
                            .getPricing()
                            .getDiscounts()
                            .stream()
                    , decimalPlaces);
            BigDecimal totFees = calculateTotalPerAugmentedPriceItem(
                    passenger.getFareProduct()
                            .getPricing()
                            .getFees()
                            .stream()
                    , decimalPlaces);
            BigDecimal totTaxes = calculateTotalPerAugmentedPriceItem(
                    passenger.getFareProduct()
                            .getPricing()
                            .getTaxes()
                            .stream()
                    , decimalPlaces);

            BigDecimal adminFee = new BigDecimal(foundPassenger.get().getFareProduct().getPricing().getFees().stream()
                    .filter(fee -> fee.getCode().equals(ADMIN_FEE))
                    .findFirst()
                    .map(AugmentedPriceItem::getAmount)
                    .orElse(0.0).toString());

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

            totals = totForPassengers;
        }

        return totals;
    }

    private BigDecimal calculateTotalPerAugmentedPriceItem(Stream<? extends AugmentedPriceItem> items, int decimalPlaces) {
        return items.filter(Objects::nonNull)
                .map(item -> new BigDecimal(item.getAmount().toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(decimalPlaces, RoundingMode.HALF_UP);
    }

    private String wrongPriceDifference(String cardType, String passengerType, Double expectedPrice, Double actualPrice) {
        return "The price difference with " + cardType + " card for " + passengerType + " is wrong: expected was " + expectedPrice + "; actual is " + actualPrice;
    }

    @Step("Taxes are applied")
    public FlightsAssertion taxesAreAppliedToTheFlights(FeesAndTaxesDao feesAndTaxesDao) {
        // All the flights have the same sector
        String sector = response.getOutbound().getJourneys().get(0).getFlights().get(0).getFlightKey().substring(8, 14);

        String currency = response.getCurrency();

        HashMap<String, Double> adultTaxes = feesAndTaxesDao.getTaxesForPassenger(sector, currency, "adult"); //NOSONAR sonar identify it as unused
        HashMap<String, Double> childTaxes = feesAndTaxesDao.getTaxesForPassenger(sector, currency, "child"); //NOSONAR sonar identify it as unused
        HashMap<String, Double> infantTaxes = feesAndTaxesDao.getTaxesForPassenger(sector, currency, "infant"); //NOSONAR sonar identify it as unused

        Stream<FindFlightsResponse.Journey> journeys = response.getOutbound().getJourneys().stream();
        if (response.getInbound() != null) {
            journeys = Stream.concat(journeys, response.getInbound().getJourneys().stream());
        }

        journeys
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(FindFlightsResponse.Flight::getFareTypes).flatMap(Collection::stream)
                .map(FindFlightsResponse.FareType::getPassengers).flatMap(Collection::stream)
                .forEach(
                        passenger -> {
                            HashMap<String, Double> applicableTaxes = new HashMap<>(); //NOSONAR sonar identify it as unused
                            switch (passenger.getType()) {
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
                                assertThat(passenger.getTaxes())
                                        .withFailMessage("The tax " + entry.getKey() + " is not applied to the passenger")
                                        .extracting("code")
                                        .contains(entry.getKey());
                            }
                        }
                );
        return this;
    }

    public FlightsAssertion flightsHaveDealApplied(Map<String, ItemModel> deal) { //NOSONAR sonar identify deal as unused
        Stream<FindFlightsResponse.Journey> journeys = response.getOutbound().getJourneys().stream();
        if (response.getInbound() != null) {
            journeys = Stream.concat(journeys, response.getInbound().getJourneys().stream());
        }

        assertThat(journeys
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(FindFlightsResponse.Flight::getFareTypes).flatMap(Collection::stream)
                .map(FindFlightsResponse.FareType::getPassengers).flatMap(Collection::stream)
                .allMatch(passenger -> passenger.getDiscounts().stream()
                        .anyMatch(discount -> discount.getCode().equals(deal.get("discount").getCode()))
                        && passenger.getFees().stream()
                        .anyMatch(fee -> fee.getCode().equals(deal.get("fee").getCode()))
                ))
                .withFailMessage("The deal wasn't applied to all the flights")
                .isTrue();

        return this;
    }

    public FlightsAssertion flightsHaveNoDealApplied(List<HashMap<String, ItemModel>> dealList) {

        Stream<FindFlightsResponse.Journey> journeys;
        if (response.getInbound() != null) {
            journeys = Stream.concat(response.getOutbound().getJourneys().stream(), response.getInbound().getJourneys().stream());
        } else {
            journeys = response.getOutbound().getJourneys().stream();
        }

        dealList.forEach(
                deal -> assertThat(journeys
                        .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                        .map(FindFlightsResponse.Flight::getFareTypes).flatMap(Collection::stream)
                        .map(FindFlightsResponse.FareType::getPassengers).flatMap(Collection::stream)
                        .noneMatch(passenger -> passenger.getDiscounts().stream()
                                .anyMatch(discount -> discount.getCode().equals(deal.get("discount").getCode()))
                                && passenger.getFees().stream()
                                .anyMatch(fee -> fee.getCode().equals(deal.get("fee").getCode()))
                        ))
                        .withFailMessage("The deal was applied to a the flight")
                        .isTrue()
        );

        return this;
    }

    public FlightsAssertion verifyInfantsConsumedOnAFlight(int actualValue, int expectedValue) {
        assertThat(actualValue)
                .withFailMessage("Infants/InfantsOwnSeat consumed value is not as expected")
                .isEqualTo(expectedValue);
        return this;
    }

    public FlightsAssertion fareIsReturned(String fareType, boolean isReturned) {
        Stream<FindFlightsResponse.Journey> journeys;
        if (response.getInbound() != null) {
            journeys = Stream.concat(response.getOutbound().getJourneys().stream(), response.getInbound().getJourneys().stream());
        } else {
            journeys = response.getOutbound().getJourneys().stream();
        }
        assertThat(journeys
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(FindFlightsResponse.Flight::getFareTypes).flatMap(Collection::stream)
                .map(FindFlightsResponse.FareType::getFareTypeCode)
                .anyMatch(fareTypeCode -> fareTypeCode.equals(fareType)))
                .withFailMessage(fareType + " fare " + (isReturned ? "is not" : "is") + " returned")
                .isEqualTo(isReturned);
        return this;
    }

}
