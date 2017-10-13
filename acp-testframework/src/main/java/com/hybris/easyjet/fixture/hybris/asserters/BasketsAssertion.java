package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.database.hybris.dao.*;
import com.hybris.easyjet.database.hybris.models.CurrencyModel;
import com.hybris.easyjet.database.hybris.models.FeesAndTaxesModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.asserters.helpers.BasketCalculator;
import com.hybris.easyjet.fixture.hybris.asserters.helpers.BasketCurrency;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.PurchasedSeatHelper;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Name;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Passenger;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Passengers;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.AddFlightRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.purchasedseats.AddPurchasedSeatsRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.purchasedseats.AdditionalSeat;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.purchasedseats.PassengerAndSeat;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.GetSeatMapResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractFlights;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AugmentedPriceItem;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.Pricing;
import lombok.NoArgsConstructor;
import net.thucydides.core.annotations.Step;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.math3.util.Precision;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.springframework.dao.EmptyResultDataAccessException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by daniel on 01/12/2016.
 * assertion wrapper for baskets response object, provides reusable assertions to all tests
 */
@NoArgsConstructor
public class BasketsAssertion extends Assertion<BasketsAssertion, BasketsResponse> {

    private static final String INBOUND = "inbound";
    private static final String OUTBOUND = "outbound";
    private static final String ADULT = "adult";
    private static final String INFANT = "infant";
    private static final String ADMINISTRATION_FEE = "Administration Fee";
    private static final String CR_CARD_FEE = "CRCardFee";
    private static final String ADMIN_FEE = "AdminFee";
    private static final String STANDARD = "Standard";
    private static final String FLEXI = "Flexi";
    private static final String TOTALWITHDEBITCARDFEE = "totWithDebitCardFee";
    private static final String TOTALWITHCREDITCARDFEE = "totWithCreditCardFee";
    private static final String THEPASSENGER = "The passenger";
    private static final String GROUP_BOOKING_INTERNET_DISCOUNT = "group booking internet discount";
    private static final String GROUP_BOOKING_FEE = "group booking fee";
    private static final String GROUP_BUNDLE_CODE = "Group";

    protected static Logger LOG = LogManager.getLogger(BasketsAssertion.class);

    private FlightsDao flightsDao = FlightsDao.getFlightsDaoFromSpring();
    private CartDao cartDao = CartDao.getCartDaoFromSpring();
    private CurrenciesDao currenciesDao = CurrenciesDao.getCurrenciesDaoFromSpring();

    /**
     * @param basketsResponse
     */
    public BasketsAssertion(BasketsResponse basketsResponse) {

        this.response = basketsResponse;
    }

    private static Map<String, Double> computeTotalPerPassenger(Basket.Passenger passenger, int decimalPlaces) {

        BigDecimal basePrice = new BigDecimal(passenger.getFareProduct().getPricing().getBasePrice().toString());
        BigDecimal totDiscount = passenger.getFareProduct().getPricing().getDiscounts().stream()
                .filter(Objects::nonNull)
                .map(discount -> new BigDecimal(discount.getAmount().toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(decimalPlaces, RoundingMode.HALF_UP);
        BigDecimal totFees = passenger.getFareProduct().getPricing().getFees().stream()
                .filter(Objects::nonNull)
                .map(fee -> new BigDecimal(fee.getAmount().toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(decimalPlaces, RoundingMode.HALF_UP);
        BigDecimal totTaxes = passenger.getFareProduct().getPricing().getTaxes().stream()
                .filter(Objects::nonNull)
                .map(tax -> new BigDecimal(tax.getAmount().toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(decimalPlaces, RoundingMode.HALF_UP);

        BigDecimal crFeeValue = new BigDecimal("0");
        Optional<AugmentedPriceItem> optionalCRFee = passenger.getFareProduct()
                .getPricing()
                .getFees()
                .stream()
                .filter(fee -> fee.getCode().equals(CR_CARD_FEE))
                .findFirst();
        if (optionalCRFee.isPresent()) {
            crFeeValue = new BigDecimal(optionalCRFee.get().getAmount().toString());
        }

        BigDecimal totForPassengers =
                basePrice
                        .subtract(totDiscount)
                        .add(totFees)
                        .subtract(crFeeValue)
                        .add(totTaxes);

        HashMap<String, Double> tot = new HashMap<>();
        tot.put(TOTALWITHDEBITCARDFEE, totForPassengers.doubleValue());
        tot.put(TOTALWITHCREDITCARDFEE, totForPassengers.add(crFeeValue).doubleValue());

        return tot;
    }

    private static long getPassengerCount(List<Basket.Flights> bounds, String passengerCode) {
        return bounds.stream()
                .flatMap(bound -> bound.getFlights().stream())
                .flatMap(flight -> flight.getPassengers().stream())
                .filter(passenger -> passenger.getCode().equals(passengerCode))
                .count();
    }

    /**
     * Set respone to use the assertion class with @Steps annotation
     *
     * @param basketsResponse the response received after invoking the service
     */
    public void setResponse(BasketsResponse basketsResponse) {
        this.response = basketsResponse;
    }

    /**
     * @return this
     */
    public BasketsAssertion gotAValidResponse() {

        assertThat(response.getBasket()).isNotNull();
        return this;
    }

    public BasketsAssertion isEmptied(String basketId, CartDao cartdao) {
        assertThat(cartdao.getBasket(basketId)).isEmpty();
        return this;
    }

    public BasketsAssertion verifyTheBasketNotExist(String basketId) {
        pollingLoop().untilAsserted(() ->
                assertThat(cartDao.isBasketExists(basketId)).isFalse());
        return this;
    }

    public BasketsAssertion flightBasePriceAndFeesAreTheSameForASinglePassenger(FindFlightsResponse.Flight flight) throws EasyjetCompromisedException {

        int indexElem = 0;
        String fareType = response.getBasket()
                .getOutbounds()
                .get(indexElem)
                .getFlights()
                .get(indexElem)
                .getPassengers()
                .get(indexElem)
                .getFareType();

        if (flight.getFareTypes()
                .stream()
                .filter(f -> f.getFareTypeCode().equalsIgnoreCase(fareType))
                .flatMap(g -> g.getPassengers().stream())
                .findFirst()
                .map(h -> h.getBasePrice().doubleValue())
                .isPresent()) {
            assertThat(response.getBasket()
                    .getOutbounds()
                    .get(indexElem)
                    .getFlights()
                    .get(indexElem)
                    .getPassengers()
                    .get(indexElem)
                    .getFareProduct()
                    .getPricing()
                    .getBasePrice()
                    .doubleValue()).isEqualTo(flight.getFareTypes()
                    .stream()
                    .filter(f -> f.getFareTypeCode().equalsIgnoreCase(fareType))
                    .flatMap(g -> g.getPassengers().stream())
                    .findFirst()
                    .map(h -> h.getBasePrice().doubleValue())
                    .get());
        } else {
            throw new EasyjetCompromisedException("The added seatmap to the basket does not match any seatmap returned from find seatmap");
        }

        return this;
    }

    /**
     * @param addFlights the flights to check
     * @return this
     */
    public BasketsAssertion theCurrencyOfTheBasketIsDefinedAsTheFirstFlight(List<AddFlightRequestBody> addFlights) {
        assertThat(response.getBasket().getCurrency()).isEqualTo(addFlights.get(0).getCurrency());
        return this;
    }

    /**
     * @return this
     */
    public BasketsAssertion theBasePriceIsReturnedForEachPassenger() {

        List<Basket.Passenger> passengers = response.getBasket().getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream())
                .flatMap(p -> p.getPassengers().stream())
                .collect(Collectors.toList());
        for (Basket.Passenger traveller : passengers) {
            assertThat(traveller.getFareProduct().getPricing().getBasePrice()).isNotZero();
        }
        return this;
    }

    /**
     * @param bundle
     * @return
     */
    public BasketsAssertion theFareBundleIsAddedToEachPassenger(String bundle) {

        List<Basket.Passenger> passengers = response.getBasket().getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream())
                .flatMap(p -> p.getPassengers().stream())
                .collect(Collectors.toList());
        for (Basket.Passenger traveller : passengers) {
            assertThat(traveller.getFareProduct().getType()).isNotEmpty();
            assertThat(traveller.getFareProduct().getBundleCode()).isEqualTo(bundle);
        }
        return this;
    }

    /**
     * @param flight
     * @return
     */
    public BasketsAssertion theBasketContainsTheFlight(FindFlightsResponse.Flight flight) {

        List<Basket.Flight> basketFlight = response.getBasket()
                .getOutbounds()
                .stream()
                .flatMap(f -> f.getFlights().stream())
                .filter(t -> t.getFlightKey().equals(flight.getFlightKey()))
                .collect(Collectors.toList());

        assertThat(basketFlight.size()).isEqualTo(1);
        return this;
    }

    /**
     * @param flight
     * @return
     */
    public BasketsAssertion theBasketContainsTheInboundFlight(FindFlightsResponse.Flight flight) {

        List<Basket.Flight> basketFlight = response.getBasket()
                .getInbounds()
                .stream()
                .flatMap(f -> f.getFlights().stream())
                .filter(t -> t.getFlightKey().equals(flight.getFlightKey()))
                .collect(Collectors.toList());

        assertThat(basketFlight.size()).isEqualTo(1);
        return this;
    }

    /**
     * @param flight
     * @return
     */
    public BasketsAssertion theBasketDoesntContainsTheFlight(FindFlightsResponse.Flight flight) {

        List<Basket.Flight> basketFlight = response.getBasket()
                .getOutbounds()
                .stream()
                .flatMap(f -> f.getFlights().stream())
                .filter(t -> t.getFlightKey().equals(flight.getFlightKey()))
                .collect(Collectors.toList());

        assertThat(basketFlight.size()).isEqualTo(0);
        return this;
    }

    /**
     * @param type
     * @return
     */
    public BasketsAssertion thereIsNoSeatOfType(String type) {

        List<Basket.Passenger> basketTravellers = response.getBasket().getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .filter(h -> h.getPassengerDetails().getPassengerType().equals(type))
                .collect(Collectors.toList());
        assertThat(basketTravellers).size().isEqualTo(0).as("Child seat was not removed!");
        return this;
    }

     /**
     * @return
     */
    public BasketsAssertion infantIsNowOnLapOfFirstAdult() {

        List<Basket.Passenger> basketTravellers = response.getBasket().getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .collect(Collectors.toList());
        Basket.Passenger adult = basketTravellers.stream()
                .filter(p -> p.getPassengerDetails().getPassengerType().equals(ADULT))
                .findFirst()
                .orElse(null);
        Basket.Passenger infant = basketTravellers.stream()
                .filter(p -> p.getPassengerDetails().getPassengerType().equals(INFANT))
                .findFirst()
                .orElse(null);
        assertThat(adult.getInfantsOnLap()).contains(infant.getCode());
        return this;
    }

    public BasketsAssertion infantIsNowOnTheirOwnSeat(Basket originalBasket, Basket updatedBasket) {

        Basket.Passenger adultWithInfant = originalBasket.getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .filter(h -> h.getInfantsOnLap().size() >= 0 && h.getPassengerDetails().getPassengerType().equals(ADULT))
                .findFirst()
                .orElse(null);

        Basket.Passenger updatedAdult = updatedBasket.getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .filter(h -> h.getCode().equals(adultWithInfant.getCode()))
                .findFirst()
                .orElse(null);

        assertThat(updatedAdult.getInfantsOnLap()).isNullOrEmpty();

        /*
        //TODO - throwing null pointer exception
        com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse.AbstractPassenger updatedInfant = basketTravellers.stream()
                .filter(p -> p.getType().equals(infant.getType()))
                .findFirst()
                .orElse(null);
        */
        return this;
    }

    /**
     * @param basket
     * @param passengers
     * @return
     */
    public BasketsAssertion theBasketContainsTheUpdatedPassengerDetails(Basket basket, Passengers passengers) {

        for (com.hybris.easyjet.fixture.hybris.helpers.traveller.Passenger passenger : passengers.getPassengers()) {
            List<Basket.Passenger> basketTravellers = basket.getOutbounds().stream()
                    .flatMap(f -> f.getFlights().stream())
                    .flatMap(g -> g.getPassengers().stream())
                    .filter(t -> t.getCode().equals(passenger.getCode()))
                    .collect(Collectors.toList());
            for (Basket.Passenger t : basketTravellers) {
                assertThat(t.getPassengerDetails().getName().getFirstName()).isEqualTo(passenger.getPassengerDetails()
                        .getName()
                        .getFirstName());

                assertThat(t.getPassengerDetails().getPassengerType().toLowerCase())
                        .as("AbstractPassenger type has not been updated as expected.")
                        .isEqualTo(passenger.getPassengerDetails().getPassengerType().toLowerCase());

                assertThat(t.getAge()).as("Age is not as expected").isEqualTo(passenger.getAge());

                // Done due because of the piece meal way that APIs is being introduced.
                if (t.getPassengerAPIS() != null) {
                    assertThat(t.getPassengerAPIS().getDocumentNumber()).as("Document Number should not be blank").isNotEmpty();
                }
                assertThat(t.getPassengerDetails().getName().getFirstName()).isEqualTo(passenger.getPassengerDetails().getName().getFirstName());
                assertThat(t.getPassengerDetails().getName().getTitle()).isEqualTo(passenger.getPassengerDetails().getName().getTitle());
                assertThat(t.getPassengerDetails().getEjPlusCardNumber()).isEqualTo(passenger.getPassengerDetails().getEjPlusCardNumber());
            }
        }
        return this;
    }

    /**
     * @param basket
     * @param passengers
     * @return
     */
    public BasketsAssertion theBasketHasNotBeenUpdatedPassengerDetails(Basket basket, Passengers passengers) {

        for (Passenger passenger : passengers.getPassengers()) {
            List<Basket.Passenger> basketTravellers = basket.getOutbounds().stream()
                    .flatMap(f -> f.getFlights().stream())
                    .flatMap(g -> g.getPassengers().stream())
                    .filter(t -> t.getCode().equals(passenger.getCode()))
                    .collect(Collectors.toList());
            for (Basket.Passenger t : basketTravellers) {
                assertThat(t.getPassengerDetails().getName().getFirstName()).isNotEqualTo(passenger.getPassengerDetails()
                        .getName()
                        .getFirstName());
                assertThat(t.getPassengerDetails().getName().getLastName()).isNotEqualTo(passenger.getPassengerDetails()
                        .getName()
                        .getLastName());
                assertThat(t.getPassengerDetails().getName().getTitle()).isNotEqualTo(passenger.getPassengerDetails()
                        .getName()
                        .getTitle());
                assertThat(t.getPassengerDetails().getEmail()).isNotEqualTo(passenger.getPassengerDetails()
                        .getEmail());
            }
        }
        return this;
    }

    /**
     * @param basket
     * @param passengers
     * @param expectedPaxType
     * @return
     */
    public BasketsAssertion theBasketContainsTheUpdatedPassengerDetails(Basket basket, Passengers passengers, String expectedPaxType) {

        for (com.hybris.easyjet.fixture.hybris.helpers.traveller.Passenger passenger : passengers.getPassengers()) {
            List<Basket.Passenger> basketPassengers = basket.getOutbounds().stream()
                    .flatMap(f -> f.getFlights().stream())
                    .flatMap(g -> g.getPassengers().stream())
                    .filter(t -> t.getCode().equals(passenger.getCode()))
                    .collect(Collectors.toList());
            for (Basket.Passenger t : basketPassengers) {
                assertThat(t.getPassengerDetails().getName().getFirstName()).isEqualTo(passenger.getPassengerDetails()
                        .getName()
                        .getFirstName());
                assertThat(t.getPassengerDetails().getPassengerType().toLowerCase())
                        .as("AbstractPassenger type has not been updated as expected.")
                        .isEqualTo(expectedPaxType.toLowerCase());
            }
        }
        return this;
    }

    /**
     * @param
     * @return
     */
    public BasketsAssertion theCreditCardFeeForEachPassengerIsCorrect(FeesAndTaxesDao feesAndTaxesDao) {

        String creditCardFeeCode = CR_CARD_FEE;
        double creditCardFee = 0;

        for (FeesAndTaxesModel feesModel : feesAndTaxesDao.getCardFees()) {
            if (feesModel.getFeeCode().contains("CardFee")) {
                creditCardFee = feesModel.getFeeValue();
            }
        }

        List<Basket.Passenger> passengers = response.getBasket().getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream())
                .flatMap(p -> p.getPassengers().stream())
                .collect(Collectors.toList());
        for (Basket.Passenger traveller : passengers) {
            double totalAmountWithDebitCard = traveller.getPassengerTotalWithDebitCard();
            // I do not agree that chopping off to two decimal places is good approach but that what I've been told that system is required to do  - AG 03-02-2017
            double passengerTaxes = ((double) (int) (traveller.getFareProduct()
                    .getPricing()
                    .getTaxes()
                    .stream()
                    .mapToDouble(AugmentedPriceItem::getAmount)
                    .sum() * 100.0)) / 100.0;
            double expectedCreditCardAmount = ((double) (int) ((totalAmountWithDebitCard + (totalAmountWithDebitCard * creditCardFee) / 100) * 100.0)) / 100.0;
            double expectedCCFeeAmount = ((double) (int) (((traveller.getFareProduct()
                    .getPricing()
                    .getBasePrice() + passengerTaxes) * creditCardFee / 100) * 100.0)) / 100.0;
            // This is to show different calculation - will nedd to be removed once rounding debate is sorted.
            double expectedCCFeeAmountHalfUp = Precision.round((traveller.getFareProduct()
                    .getPricing()
                    .getBasePrice() + passengerTaxes) * creditCardFee / 100, 2, BigDecimal.ROUND_HALF_UP);
            double expectedCCFeeAmountHalfDown = Precision.round((traveller.getFareProduct()
                    .getPricing()
                    .getBasePrice() + passengerTaxes) * creditCardFee / 100, 2, BigDecimal.ROUND_HALF_DOWN);
            double expectedCCFeeAmountDown = Precision.round((traveller.getFareProduct()
                    .getPricing()
                    .getBasePrice() + passengerTaxes) * creditCardFee / 100, 2, BigDecimal.ROUND_DOWN);

            // Check that Credit Card fee is applied at the traveller level for booking made by Digital channel
            assertThat(traveller.getFareProduct().getPricing().getFees()).isNotEmpty();
            assertThat(traveller.getFareProduct().getPricing().getFees()).extracting("code", "amount", "percentage")
                    .overridingErrorMessage("Current chopped card fee <%s>, half-up <%s>, half-down <%s>, down <%s> and the one presented in json <%s>", expectedCCFeeAmount, expectedCCFeeAmountHalfUp, expectedCCFeeAmountHalfDown, expectedCCFeeAmountDown, traveller
                            .getFareProduct()
                            .getPricing()
                            .getFees()
                            .get(0)
                            .getAmount())
                    .containsSequence(Tuple.tuple(creditCardFeeCode, expectedCCFeeAmount, creditCardFee));
            assertThat(traveller.getPassengerTotalWithCreditCard()).isEqualTo(expectedCreditCardAmount);
        }
        return this;

    }

    /**
     * @return
     */
    public BasketsAssertion theAdministrationTaxIsAtBookingLevel(FeesAndTaxesDao feesAndTaxesDao) {

        Double adminFee = null;
        String adminFeeCode = ADMIN_FEE;
        for (FeesAndTaxesModel feesModel : feesAndTaxesDao.getAdminFees(response.getBasket()
                .getCurrency()
                .getCode())) {
            adminFee = feesModel.getFeeValue();
        }
        List<Basket.Passenger> passengers = response.getBasket().getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream())
                .flatMap(p -> p.getPassengers().stream())
                .collect(Collectors.toList());
        //Check that no Admin fees are applied at the traveler level
        for (Basket.Passenger traveller : passengers) {
            assertThat(traveller.getFareProduct().getPricing().getTaxes().isEmpty());
        }
        //Admin Fee is applied at the booking level
        assertThat(response.getBasket().getFees().getItems()).extracting("code", "amount")
                .contains(Tuple.tuple(adminFeeCode, adminFee));
        return this;
    }

    /**
     * @param feesAndTaxesDao
     * @return
     */
    public BasketsAssertion theAdministrationFeeDividedAcrossPassengersandOfTheFirstFlightOnly(FeesAndTaxesDao feesAndTaxesDao) {

        double adminFee = 0;
        double addedAdminFee = 0;
        int flightOrder = 0;
        double firstFlightAdminFee = 0;
        double subsequentFlightsAdminFee = 0; //this should be reset to 0 in the logic
        for (FeesAndTaxesModel feesModel : feesAndTaxesDao.getAdminFees(response.getBasket()
                .getCurrency()
                .getCode())) {
            adminFee = feesModel.getFeeValue();
        }

        List<Basket.Flight> basketFlights = response.getBasket()
                .getOutbounds()
                .stream()
                .flatMap(f -> f.getFlights().stream())
                .collect(Collectors.toList());

        //Validate That Admin fees is split only per passenger of the First Flight Only

        for (Basket.Flight flight : basketFlights) {
            List<Basket.Passenger> flightPassengers = new ArrayList<>(flight.getPassengers());
            for (Basket.Passenger traveller : flightPassengers) {
                for (AugmentedPriceItem fee : traveller.getFareProduct().getPricing().getFees()) {
                    if (fee.getName().contains(ADMINISTRATION_FEE)) {
                        addedAdminFee = addedAdminFee + fee.getAmount();
                    }
                }
            }

            if (flightOrder == 0) {
                firstFlightAdminFee = addedAdminFee;
                addedAdminFee = 0;
                flightOrder++;
                subsequentFlightsAdminFee = 0;
            } else {
                subsequentFlightsAdminFee = addedAdminFee;
            }
        }

        //Admin Fee split across Flights and Passengers is the same as total

        double subsequentFlightsAdminFeeRounded = Precision.round(subsequentFlightsAdminFee, 0, BigDecimal.ROUND_UP);
        double firstFlightAdminFeeRounded = Precision.round(firstFlightAdminFee, 0, BigDecimal.ROUND_UP);
        assertThat(firstFlightAdminFeeRounded).isNotZero();
        assertThat(subsequentFlightsAdminFeeRounded).isZero();
        assertThat(firstFlightAdminFeeRounded).isEqualTo(adminFee);
        return this;
    }

    public BasketsAssertion theAdminFeeShouldBeApportionedPerPassengerAndRoundedToTheNearestPenceForTheFirstTwoSectors(FeesAndTaxesDao feesAndTaxesDao) {

        double adminFee = 0;
        double addedOutboundAdminFee = 0;
        double addedInboundAdminFee = 0;
        double firstOutboundFlightAdminFee = 0;
        double subsequentOutboundFlightsAdminFee = 0;
        double firstInboundFlightAdminFee = 0;
        double subsequentInboundFlightsAdminFee = 0;
        int outboundFlightOrder = 0;
        int inboundFlightOrder = 0;

        for (FeesAndTaxesModel feesModel : feesAndTaxesDao.getAdminFees(response.getBasket()
                .getCurrency()
                .getCode())) {
            adminFee = feesModel.getFeeValue();
        }

        List<Basket.Flight> basketOutboundFlights = response.getBasket()
                .getOutbounds()
                .stream()
                .flatMap(f -> f.getFlights().stream())
                .collect(Collectors.toList());

        List<Basket.Flight> basketInboundFlights = response.getBasket()
                .getInbounds()
                .stream()
                .flatMap(f -> f.getFlights().stream())
                .collect(Collectors.toList());

        //Validate That Admin fees is split only per passenger of the First Flight Only

        for (Basket.Flight flight : basketOutboundFlights) {

            List<Basket.Passenger> flightPassengers = new ArrayList<>(flight.getPassengers());

            for (Basket.Passenger traveller : flightPassengers) {

                for (AugmentedPriceItem fee : traveller.getFareProduct().getPricing().getFees()) {
                    if (fee.getName().contains(ADMINISTRATION_FEE)) {
                        addedOutboundAdminFee = addedOutboundAdminFee + fee.getAmount();
                    }
                }
            }

            if (outboundFlightOrder == 0) {
                firstOutboundFlightAdminFee = addedOutboundAdminFee;
                addedOutboundAdminFee = 0;
                outboundFlightOrder++;
            } else {
                subsequentOutboundFlightsAdminFee = addedOutboundAdminFee;
            }
        }

        for (Basket.Flight flight : basketInboundFlights) {

            List<Basket.Passenger> flightPassengers = new ArrayList<>(flight.getPassengers());

            for (Basket.Passenger traveller : flightPassengers) {

                for (AugmentedPriceItem fee : traveller.getFareProduct().getPricing().getFees()) {
                    if (fee.getName().contains(ADMINISTRATION_FEE)) {
                        addedInboundAdminFee = addedInboundAdminFee + fee.getAmount();
                    }
                }
            }

            if (inboundFlightOrder == 0) {
                firstInboundFlightAdminFee = addedInboundAdminFee;
                addedInboundAdminFee = 0;
                inboundFlightOrder++;
            } else {
                subsequentInboundFlightsAdminFee = addedInboundAdminFee;
            }
        }

        //Admin Fee split across  Passengers of First Flights and Flight Flights is the same as total

        double firstOutboundFlightAdminFeeRounded = Precision.round(firstOutboundFlightAdminFee, 0, BigDecimal.ROUND_HALF_UP);
        double subsequentOutboundFlightsAdminFeeRounded = Precision.round(subsequentOutboundFlightsAdminFee, 0, BigDecimal.ROUND_HALF_UP);
        double firstInboundFlightAdminFeeRounded = Precision.round(firstInboundFlightAdminFee, 0, BigDecimal.ROUND_HALF_UP);
        double subsequentInboundFlightsAdminFeeRounded = Precision.round(subsequentInboundFlightsAdminFee, 0, BigDecimal.ROUND_HALF_UP);

        double addedTotalAdminFee = firstOutboundFlightAdminFeeRounded + firstInboundFlightAdminFeeRounded;

        assertThat(adminFee).isEqualTo(addedTotalAdminFee);
        assertThat(adminFee).overridingErrorMessage("First Flights seatmap added apportioned Admin Fee <%s> should be only half of Total Admin Fee <%s>", firstOutboundFlightAdminFeeRounded, adminFee)
                .isNotEqualTo(firstOutboundFlightAdminFeeRounded);
        assertThat(firstInboundFlightAdminFeeRounded).overridingErrorMessage("Expecting half of Admin Fee <%s> apportioned between all Travelers on Flight Flight but it was: <%s>", adminFee, firstInboundFlightAdminFeeRounded)
                .isNotZero();
        assertThat(firstOutboundFlightAdminFeeRounded).overridingErrorMessage("Expecting half of Admin Fee <%s> apportioned between all Travelers on Flights Flight but it was: <%s> ", adminFee, firstOutboundFlightAdminFeeRounded)
                .isNotZero();
        assertThat(subsequentOutboundFlightsAdminFeeRounded).isZero();
        assertThat(subsequentInboundFlightsAdminFeeRounded).isZero();

        return this;

    }

    public BasketsAssertion basketType(String basketType) {

        assertThat(response.getBasket().getBasketType().toLowerCase()).isEqualTo(basketType.toLowerCase());
        return this;
    }

    public BasketsAssertion theFlightTaxIsAtPassengerLevel(FeesAndTaxesDao feesAndTaxesDao) {

        List<Basket.Passenger> passengers = response.getBasket().getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream())
                .flatMap(p -> p.getPassengers().stream())
                .collect(Collectors.toList());

        //Check that no Admin fees are applied at the traveler level
        for (Basket.Passenger traveller : passengers) {
            List<AugmentedPriceItem> passengerTaxes = traveller.getFareProduct()
                    .getPricing()
                    .getTaxes();

            for (AugmentedPriceItem tax : passengerTaxes) {
                for (FeesAndTaxesModel feesModel : feesAndTaxesDao.getFeeOrTaxValue(tax.getName(), response.getBasket()
                        .getCurrency()
                        .getCode(), traveller.getPassengerDetails().getPassengerType())) {
                    assertThat(tax.getAmount()).isEqualTo(feesModel.getFeeValue());
                    assertThat(traveller.getFareProduct()
                            .getPricing()
                            .getTotalAmountWithDebitCard()
                            .equals(traveller.getFareProduct().getPricing().getBasePrice() + tax.getAmount()));
                }
            }

            assertThat(traveller.getFareProduct().getPricing().getTaxes().size()).isNotZero();
        }
        return this;
    }

    public BasketsAssertion discountAndPOSFeeAppliedAtPassengerLevel() {

        List<Basket.Passenger> passengers = response.getBasket()
                .getOutbounds()
                .get(0)
                .getFlights()
                .get(0)
                .getPassengers();
        assertThat(passengers.size()).isGreaterThan(0);
        for (Basket.Passenger passenger : passengers) {
            List<AugmentedPriceItem.Discount> discounts = passenger.getFareProduct().getPricing().getDiscounts();

            assertThat(discounts).isNotEmpty();
            List<AugmentedPriceItem> fees = passenger.getFareProduct().getPricing().getFees();
            assertThat(fees).isNotEmpty();
        }
        return this;
    }

    public BasketsAssertion appliedDiscountAndPOS() {

        double basePricePerPassenger;
        double discPerPassenger;
        double feePerPassenger;
        double taxPerPassenger;
        double actualTotalAmountCreditCard;
        double totCabinItem;
        double totHoldItem;
        DecimalFormat df = new DecimalFormat("#.##");
        List<Basket.Passenger> passengers = response.getBasket()
                .getOutbounds()
                .get(0)
                .getFlights()
                .get(0)
                .getPassengers();
        for (Basket.Passenger passenger : passengers) {
            basePricePerPassenger = passenger.getFareProduct().getPricing().getBasePrice();
            discPerPassenger = 0.0;
            feePerPassenger = 0.0;
            taxPerPassenger = 0.0;
            totCabinItem = 0.0;
            totHoldItem = 0.0;

            List<AugmentedPriceItem.Discount> discounts = passenger.getFareProduct().getPricing().getDiscounts();

            for (AugmentedPriceItem discount : discounts) {
                discPerPassenger = discPerPassenger + discount.getAmount();
            }

            List<AugmentedPriceItem> fees = passenger.getFareProduct().getPricing().getFees();

            for (AugmentedPriceItem fee : fees) {
                feePerPassenger = feePerPassenger + fee.getAmount();
            }

            List<AugmentedPriceItem> taxes = passenger.getFareProduct().getPricing().getTaxes();

            for (AugmentedPriceItem tax : taxes) {
                taxPerPassenger = taxPerPassenger + tax.getAmount();
            }

            List<AbstractPassenger.CabinItem> cabinItems = passenger.getCabinItems();

            for (AbstractPassenger.CabinItem cabinItem : cabinItems) {
                totCabinItem = totCabinItem + cabinItem.getPricing().getTotalAmountWithCreditCard();
            }

            List<AbstractPassenger.HoldItem> holdItems = passenger.getHoldItems();

            for (AbstractPassenger.HoldItem holdItem : holdItems) {
                totHoldItem = totHoldItem + holdItem.getPricing().getTotalAmountWithCreditCard();
            }

            actualTotalAmountCreditCard = passenger.getPassengerTotalWithCreditCard();
            Double expectedTotalAmountCreditCard = Double.valueOf(df.format((basePricePerPassenger + feePerPassenger + taxPerPassenger + totCabinItem + totHoldItem) - discPerPassenger));
            assertThat(expectedTotalAmountCreditCard.compareTo(actualTotalAmountCreditCard)).isEqualTo(0);
        }
        return this;
    }

    public BasketsAssertion basketTypeAsBusiness(String dealType) {

        assertThat(response.getBasket().getBasketType().equals(dealType));
        return this;
    }

    public BasketsAssertion bookingReasonAsBusiness(String dealType) {

        assertThat(response.getBasket().getBookingReason().equals(dealType));
        return this;
    }

    public BasketsAssertion theBasketContainsTheUpdatedPassengerName(Name aChosenName) {

        AbstractPassenger.PassengerDetails actualDetails = response.getBasket()
                .getOutbounds()
                .get(0)
                .getFlights()
                .get(0)
                .getPassengers()
                .get(0)
                .getPassengerDetails();
        String actualFirstName = actualDetails.getName().getFirstName();
        String actualLastName = actualDetails.getName().getLastName();

        assertThat(actualFirstName).isEqualTo(aChosenName.getFirstName());
        assertThat(actualLastName).isEqualTo(aChosenName.getLastName());

        return this;
    }

    public BasketsAssertion theCreditCardFeeForEachPassengerIsCorrect(int decimalPosition) {

        response.getBasket().getOutbounds().forEach(
                journey -> journey.getFlights().forEach(
                        flight -> flight.getPassengers().stream().filter(
                                passengers -> passengers.getFareProduct().getPricing().getFees().stream().anyMatch(
                                        fee -> fee.getCode().equals(CR_CARD_FEE))).forEach(
                                passenger -> {
                                    AugmentedPriceItem crFee = passenger.getFareProduct().getPricing().getFees()
                                            .stream()
                                            .filter(fee -> fee.getCode().equals(CR_CARD_FEE))
                                            .findFirst().get();
                                    Double crFeeValue = new BigDecimal(crFee.getAmount().toString()).doubleValue();
                                    BigDecimal crFeePercentage = new BigDecimal(
                                            crFee.getPercentage().toString())
                                            .multiply(new BigDecimal("0.01"));
                                    BigDecimal adminFee = new BigDecimal("0");
                                    Optional<AugmentedPriceItem> optionalAdminFee = passenger.getFareProduct().getPricing().getFees()
                                            .stream().filter(fee -> fee.getCode().equals(ADMIN_FEE))
                                            .findFirst();
                                    if (optionalAdminFee.isPresent()) {
                                        adminFee = new BigDecimal(optionalAdminFee.get().getAmount().toString());
                                    }

                                    assertThat(crFeeValue).isEqualTo(
                                            new BigDecimal(
                                                    computeTotalPerPassenger(passenger, Integer.valueOf(response.getBasket().getCurrency().getDecimalPlaces()))
                                                            .get(TOTALWITHDEBITCARDFEE).toString())
                                                    .subtract(adminFee)
                                                    .multiply(crFeePercentage).setScale(Integer.valueOf(response.getBasket().getCurrency().getDecimalPlaces()), RoundingMode.HALF_UP)
                                                    .add(adminFee.multiply(crFeePercentage).setScale(Integer.valueOf(response.getBasket().getCurrency().getDecimalPlaces()), RoundingMode.UP))
                                                    .doubleValue()
                                    );
                                }
                        )
                )
        );

        if (response.getBasket().getInbounds() != null) {
            response.getBasket().getInbounds().forEach(
                    journey -> journey.getFlights().forEach(
                            flight -> flight.getPassengers().stream().filter(
                                    passengers -> passengers.getFareProduct().getPricing().getFees().stream().anyMatch(
                                            fee -> fee.getCode().equals(CR_CARD_FEE))).forEach(
                                    passenger -> {
                                        AugmentedPriceItem crFee = passenger.getFareProduct().getPricing().getFees()
                                                .stream()
                                                .filter(fee -> fee.getCode().equals(CR_CARD_FEE))
                                                .findFirst().get();
                                        Double crFeeValue = new BigDecimal(crFee.getAmount().toString()).doubleValue();
                                        BigDecimal crFeePercentage = new BigDecimal(
                                                crFee.getPercentage().toString())
                                                .multiply(new BigDecimal("0.01"));
                                        BigDecimal adminFee = new BigDecimal("0");
                                        Optional<AugmentedPriceItem> optionalAdminFee = passenger.getFareProduct().getPricing().getFees()
                                                .stream().filter(fee -> fee.getCode().equals(ADMIN_FEE))
                                                .findFirst();
                                        if (optionalAdminFee.isPresent()) {
                                            adminFee = new BigDecimal(optionalAdminFee.get().getAmount().toString());
                                        }

                                        assertThat(crFeeValue).isEqualTo(
                                                new BigDecimal(
                                                        computeTotalPerPassenger(passenger, Integer.valueOf(response.getBasket().getCurrency().getDecimalPlaces()))
                                                                .get(TOTALWITHDEBITCARDFEE).toString())
                                                        .subtract(adminFee)
                                                        .multiply(crFeePercentage).setScale(Integer.valueOf(response.getBasket().getCurrency().getDecimalPlaces()), RoundingMode.HALF_UP)
                                                        .add(adminFee.multiply(crFeePercentage).setScale(Integer.valueOf(response.getBasket().getCurrency().getDecimalPlaces()), RoundingMode.UP))
                                                        .doubleValue()
                                        );
                                    }
                            )
                    )
            );
        }

        return this;
    }

    public BasketsAssertion theAdminFeeForEachPassengerIsCorrect(int decimalPosition, BigDecimal expectedAdminFee) {

        BigDecimal outboundPassengers = new BigDecimal(response.getBasket().getOutbounds().stream()
                .map(Basket.Flights::getFlights).limit(1).flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers).flatMap(Collection::stream)
                .map(Basket.Passenger::getPassengerDetails)
                .map(AbstractPassenger.PassengerDetails::getPassengerType)
                .filter(type -> !type.equals(INFANT))
                .count());

        double outboundFee = expectedAdminFee.divide(outboundPassengers, Integer.valueOf(response.getBasket().getCurrency().getDecimalPlaces()), RoundingMode.UP).doubleValue();

        response.getBasket().getOutbounds().stream()
                .map(Basket.Flights::getFlights).flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers).flatMap(Collection::stream)
                .map(Basket.Passenger::getFareProduct)
                .map(AbstractPassenger.FareProduct::getPricing)
                .map(Pricing::getFees).flatMap(Collection::stream)
                .filter(fee -> fee.getCode().equals(ADMIN_FEE))
                .forEach(
                        fee ->
                                assertThat(fee.getAmount())
                                        .withFailMessage("The admin fee have not been apportioned: expected was " + outboundFee + "; actual is " + fee.getAmount())
                                        .isEqualTo(outboundFee)
                );

        if (response.getBasket().getInbounds().size() > 0 ) {
            BigDecimal inboundPassengers = new BigDecimal(response.getBasket().getInbounds().stream()
                    .map(Basket.Flights::getFlights).limit(1).flatMap(Collection::stream)
                    .map(Basket.Flight::getPassengers).flatMap(Collection::stream)
                    .map(Basket.Passenger::getPassengerDetails)
                    .map(AbstractPassenger.PassengerDetails::getPassengerType)
                    .filter(type -> !type.equals(INFANT))
                    .count());

            double inboundFee = expectedAdminFee.divide(inboundPassengers, Integer.valueOf(response.getBasket().getCurrency().getDecimalPlaces()), RoundingMode.UP).doubleValue();

            response.getBasket().getInbounds().stream()
                    .map(Basket.Flights::getFlights).flatMap(Collection::stream)
                    .map(Basket.Flight::getPassengers).flatMap(Collection::stream)
                    .map(Basket.Passenger::getFareProduct)
                    .map(AbstractPassenger.FareProduct::getPricing)
                    .map(Pricing::getFees).flatMap(Collection::stream)
                    .filter(fee -> fee.getCode().equals(ADMIN_FEE))
                    .forEach(
                            fee ->
                                    assertThat(fee.getAmount())
                                            .withFailMessage("The admin fee have not been apportioned: expected was " + inboundFee + "; actual is " + fee.getAmount())
                                            .isEqualTo(inboundFee)
                    );
        }

        return this;
    }

    public BasketsAssertion theTotAmountForEachPassengerIsCorrect(int decimalPosition) {

        response.getBasket().getOutbounds().forEach(
                journey -> journey.getFlights().forEach(
                        flight -> flight.getPassengers().forEach(
                                passenger -> {

                                    Map<String, Double> expectedValues = computeTotalPerPassenger(passenger, decimalPosition);
                                    assertThat(passenger.getFareProduct().getPricing().getTotalAmountWithDebitCard())
                                            .isEqualTo(expectedValues.get(TOTALWITHDEBITCARDFEE));
                                    assertThat(passenger.getFareProduct().getPricing().getTotalAmountWithCreditCard())
                                            .isEqualTo(expectedValues.get(TOTALWITHCREDITCARDFEE));

                                }
                        )
                )
        );

        if (!response.getBasket().getInbounds().isEmpty()) {
            response.getBasket().getInbounds().forEach(
                    journey -> journey.getFlights().forEach(
                            flight -> flight.getPassengers().forEach(
                                    passenger -> {

                                        Map<String, Double> expectedValues = computeTotalPerPassenger(passenger, decimalPosition);
                                        assertThat(passenger.getFareProduct().getPricing().getTotalAmountWithDebitCard())
                                                .isEqualTo(expectedValues.get(TOTALWITHDEBITCARDFEE));
                                        assertThat(passenger.getFareProduct().getPricing().getTotalAmountWithCreditCard())
                                                .isEqualTo(expectedValues.get(TOTALWITHCREDITCARDFEE));

                                    }
                            )
                    )
            );
        }

        return this;
    }

    public BasketsAssertion verifyStaffBundleForEachPassenger() {

        String bundle = "Staff";
        List<Basket.Passenger> flightPassenger = response.getBasket()
                .getOutbounds()
                .stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .collect(Collectors.toList());

        for (Basket.Passenger passenger : flightPassenger) {
            assertThat(passenger.getFareProduct().getBundleCode().equals(bundle)).isEqualTo(true);
        }

        flightPassenger = response.getBasket().getInbounds().stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .collect(Collectors.toList());

        for (Basket.Passenger passenger : flightPassenger) {
            assertThat(passenger.getFareProduct().getBundleCode().equals(bundle)).isEqualTo(true);
        }

        return this;
    }

    public BasketsAssertion noCCRFeeAndAdminFeeAreApliedForStaffBundle() {

        String taxesCRCardFee = CR_CARD_FEE;
        String taxesAdminFee = "AdminFee";

        List<Basket.Passenger> flightPassenger = response.getBasket()
                .getOutbounds()
                .stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .collect(Collectors.toList());

        for (Basket.Passenger passenger : flightPassenger) {

            List<AugmentedPriceItem> fees = passenger.getFareProduct().getPricing().getFees().stream()
                    .filter(Objects::nonNull)
                    .filter(h -> h.getCode().equals(taxesCRCardFee) || h.getCode().equals(taxesAdminFee))
                    .collect(Collectors.toList());

            assertThat(fees.size()).isEqualTo(0);
        }

        flightPassenger = response.getBasket().getInbounds().stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .collect(Collectors.toList());

        for (Basket.Passenger passenger : flightPassenger) {
            List<AugmentedPriceItem> fees = passenger.getFareProduct().getPricing().getFees().stream()
                    .filter(Objects::nonNull)
                    .filter(h -> h.getCode().equals(taxesCRCardFee) || h.getCode().equals(taxesAdminFee))
                    .collect(Collectors.toList());

            assertThat(fees.size()).isEqualTo(0);
        }

        return this;
    }

    public BasketsAssertion taxesAreApplied(String tax) {

        List<Basket.Passenger> flightPassenger = response.getBasket()
                .getOutbounds()
                .stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .collect(Collectors.toList());

        for (Basket.Passenger passenger : flightPassenger) {
            List<AugmentedPriceItem> fees = passenger.getFareProduct().getPricing().getTaxes().stream()
                    .filter(Objects::nonNull)
                    .filter(h -> h.getCode().contains(tax))
                    .collect(Collectors.toList());

            assertThat(fees.size()).isNotEqualTo(0);
            break;
        }

        flightPassenger = response.getBasket().getInbounds().stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .collect(Collectors.toList());

        for (Basket.Passenger passenger : flightPassenger) {
            List<AugmentedPriceItem> fees = passenger.getFareProduct().getPricing().getTaxes().stream()
                    .filter(Objects::nonNull)
                    .filter(h -> h.getCode().contains(tax))
                    .collect(Collectors.toList());

            assertThat(fees.size()).isNotEqualTo(0);
            break;
        }

        return this;
    }

    public BasketsAssertion taxesAreAppliedForPassenger(String passengerCode, String passengerType, String channel, String tax, String currency, FeesAndTaxesDao feesAndTaxesDao) throws EasyjetCompromisedException {
        return taxesAreAppliedForPassenger(response.getBasket(), passengerCode, passengerType, channel, tax, currency, feesAndTaxesDao);
    }

    @Step("Taxes are applied for flight {1}: Basket {0}, Passenger {2}")
    public BasketsAssertion taxesAreAppliedForPassenger(Basket basket, String flightKey, String passengerCode, String passengerType, String currency, FeesAndTaxesDao feesAndTaxesDao) throws EasyjetCompromisedException {
        Basket.Passenger taxedPassenger = Stream.concat(
                basket.getOutbounds().stream()
                        .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                        .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream),
                basket.getInbounds().stream()
                        .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                        .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
        ).filter(passenger -> passenger.getCode().equals(passengerCode))
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("The passenger is not present in the basket"));

        HashMap<String, Double> availableTaxes = feesAndTaxesDao.getTaxesForPassenger(passengerCode.split("_")[1].substring(8, 14), currency, passengerType.toLowerCase());

        if (!availableTaxes.isEmpty()) {
            for (Map.Entry<String, Double> entry : availableTaxes.entrySet()) {
                assertThat(taxedPassenger.getFareProduct().getPricing().getTaxes())
                        .withFailMessage("The tax " + entry.getKey() + " is not applied to the passenger")
                        .extracting("code")
                        .contains(entry.getKey());
            }
        } else {
            assertThat(taxedPassenger.getFareProduct().getPricing().getTaxes().size())
                    .withFailMessage("Unexpected taxes is applied to the passenger")
                    .isEqualTo(0);
        }

        return this;
    }

    public BasketsAssertion taxesAreAppliedForPassenger(Basket basket, String passengerCode, String passengerType, String channel, String tax, String currency, FeesAndTaxesDao feesAndTaxesDao) throws EasyjetCompromisedException {

        List<Basket.Passenger> flightPassenger = basket
                .getOutbounds()
                .stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .collect(Collectors.toList());

        flightPassenger.addAll(basket
                .getInbounds()
                .stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .collect(Collectors.toList()));

        String sector = passengerCode.split("_")[1].substring(8, 14);

        List<FeesAndTaxesModel> availableTaxes = feesAndTaxesDao.getFees(tax, currency, passengerType.toLowerCase(), channel, sector);

        Basket.Passenger taxedPassenger = flightPassenger.stream()
                .filter(passenger -> passenger.getCode().equals(passengerCode))
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("Thepassengeris not present in the basket"));

        if (!availableTaxes.isEmpty()) {
            assertThat(taxedPassenger.getFareProduct().getPricing().getTaxes().size() == availableTaxes.size()).isTrue();
        } else {
            assertThat(taxedPassenger.getFareProduct().getPricing().getTaxes().stream().noneMatch(t -> t.getName().contains(tax))).isTrue();
        }

        return this;
    }

    public BasketsAssertion theNumberOfCabinBagsAddedToThePassenger(String numberOfCabinBags) {

        List<Basket.Passenger> flightPassenger = response.getBasket()
                .getOutbounds()
                .stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .collect(Collectors.toList());
        int numberOfBags = Integer.parseInt(numberOfCabinBags);
        for (Basket.Passenger passenger : flightPassenger) {
            assertThat(passenger.getCabinItems().size()).isEqualTo(numberOfBags);
        }
        return this;
    }

    public BasketsAssertion holdBagAddedAtPassengerLevel(BasketsResponse basketsResponse) {

        List<Basket.Passenger> flightPassenger = basketsResponse.getBasket().getOutbounds().stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .collect(Collectors.toList());
        for (Basket.Passenger passenger : flightPassenger) {
            assertThat(passenger.getHoldItems().get(0).getQuantity() > 0);
            break;
        }
        return this;
    }

    public BasketsAssertion additionalSeatAddedAtPassengerLevel(BasketsResponse basketsResponse, String basketPassengerId) {

        List<Basket.Passenger> outboundFlightPassenger = basketsResponse.getBasket().getOutbounds().stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .collect(Collectors.toList());
        for (Basket.Passenger passenger : outboundFlightPassenger) {
            if (passenger.getCode().equals(basketPassengerId)) {
                assertThat(passenger.getAdditionalSeats()).withFailMessage("No additional seat has been added to this passenger " + basketPassengerId).isNotEmpty();
                break;
            }
        }

        List<Basket.Passenger> inboundFlightPassenger = basketsResponse.getBasket().getInbounds().stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .collect(Collectors.toList());
        for (Basket.Passenger passenger : inboundFlightPassenger) {
            if (passenger.getCode().equals(basketPassengerId)) {
                assertThat(passenger.getAdditionalSeats()).withFailMessage("No additional seat has been added to this passenger " + basketPassengerId).isNotEmpty();
                break;
            }
        }

        return this;
    }

    public BasketsAssertion additionalSeatAddedForEachPassenger(Basket basket) {

        List<Basket.Passenger> outboundFlightPassenger = basket.getOutbounds().stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .collect(Collectors.toList());
        for (Basket.Passenger passenger : outboundFlightPassenger) {
            assertThat(passenger.getAdditionalSeats()).withFailMessage("No additional seat has been added to this passenger " + passenger.getCode()).isNotEmpty();
        }

        List<Basket.Passenger> inboundFlightPassenger = basket.getInbounds().stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .collect(Collectors.toList());
        for (Basket.Passenger passenger : inboundFlightPassenger) {
            assertThat(passenger.getAdditionalSeats()).withFailMessage("No additional seat has been added to this passenger " + passenger.getCode()).isNotEmpty();
        }

        return this;
    }

    public BasketsAssertion priceCalculationAreRight(int decimalPlaces, Double expectedAdminFee) {
        BasketCalculator calculator = new BasketCalculator(this, decimalPlaces, expectedAdminFee, response.getBasket());
        return calculator.priceCalculationAreRight();
    }

    @Step("Price calculation are right: Basket {2}, Admin fee {1}, Decimal places {0}")
    public BasketsAssertion priceCalculationAreRight(int decimalPlaces, Double expectedAdminFee, Basket basket) {
        BasketCalculator calculator = new BasketCalculator(this, decimalPlaces, expectedAdminFee, basket);
        return calculator.priceCalculationAreRight();
    }

    public BasketsAssertion theBasketDoesntContainsHoldItem() {

        response.getBasket().getOutbounds().forEach(
                outbound -> outbound.getFlights().forEach(
                        flight -> flight.getPassengers().forEach(
                                passenger -> assertThat(passenger.getHoldItems().size()).isEqualTo(0)
                        )
                )
        );
        return this;
    }

    public BasketsAssertion totalHasBeenReducedByHoldBagPrice(Map<String, BigDecimal> totals, Map<String, BigDecimal> holdBagPrices) {

        assertThat(response.getBasket().getTotalAmountWithDebitCard()).isEqualTo(totals.get("totalDebit").subtract(holdBagPrices.get("totalDebit")).doubleValue());
        assertThat(response.getBasket().getTotalAmountWithCreditCard()).isEqualTo(totals.get("totalCredit").subtract(holdBagPrices.get("totalCredit")).doubleValue());

        return this;
    }

    public BasketsAssertion passengerDontHaveHoldItem(String passengerCode) {

        response.getBasket().getOutbounds().stream().anyMatch(
                outbound -> outbound.getFlights().stream().anyMatch(
                        flight -> flight.getPassengers().stream().anyMatch(
                                passenger -> passenger.getCode().equals(passengerCode)
                        )
                )
        );

        return this;
    }

    public BasketsAssertion theBasketNotIsEmpty() {

        assertThat(response.getBasket().getOutbounds().get(0)).isNotNull();
        assertThat(response.getBasket().getOutbounds().size()).isGreaterThan(0);

        return this;
    }

    public BasketsAssertion theAdditionalSeatsIsAdded() {

        assertThat(response.getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getAdditionalSeats().get(0)).isNotNull();
        assertThat(response.getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getAdditionalSeats().size()).isGreaterThan(0);

        return this;
    }

    public BasketsAssertion theBasketDoesNotContainsTheFlight(String flightKey) {

        assertThat(
                response.getBasket().getOutbounds().stream()
                        .map(Basket.Flights::getFlights).flatMap(Collection::stream)
                        .anyMatch(fl -> fl.getFlightKey().equals(flightKey)))
                .withFailMessage("The basket still contains the removed flight: " + flightKey)
                .isFalse();

        assertThat(
                response.getBasket().getInbounds().stream()
                        .map(Basket.Flights::getFlights).flatMap(Collection::stream)
                        .anyMatch(fl -> fl.getFlightKey().equals(flightKey)))
                .withFailMessage("The basket still contains the removed flight: " + flightKey)
                .isFalse();

        return this;
    }

    public BasketsAssertion passengerIsInTheBasket(String passengerId, String journey) {

        switch (journey) {
            case OUTBOUND:
                assertThat(
                        response.getBasket().getOutbounds().stream()
                                .map(Basket.Flights::getFlights).flatMap(Collection::stream)
                                .map(Basket.Flight::getPassengers).flatMap(Collection::stream)
                                .anyMatch(basketPassenger -> basketPassenger.getCode().equals(passengerId))
                ).withFailMessage(THEPASSENGER + passengerId + "is not present in the basket")
                        .isTrue();
                break;
            case INBOUND:
                assertThat(
                        response.getBasket().getInbounds().stream()
                                .map(Basket.Flights::getFlights).flatMap(Collection::stream)
                                .map(Basket.Flight::getPassengers).flatMap(Collection::stream)
                                .anyMatch(basketPassenger -> basketPassenger.getCode().equals(passengerId))
                ).withFailMessage(THEPASSENGER + passengerId + "is not present in the basket")
                        .isTrue();
                break;
            default:
                break;
        }
        return this;
    }

    public BasketsAssertion passengerIsnotInTheBasket(String passengerId, String journey) {

        switch (journey) {
            case OUTBOUND:
                assertThat(
                        response.getBasket().getOutbounds().stream()
                                .map(Basket.Flights::getFlights).flatMap(Collection::stream)
                                .map(Basket.Flight::getPassengers).flatMap(Collection::stream)
                                .noneMatch(basketPassenger -> basketPassenger.getCode().equals(passengerId))
                ).withFailMessage(THEPASSENGER + passengerId + "is present in the basket")
                        .isTrue();
                break;
            case INBOUND:
                assertThat(
                        response.getBasket().getInbounds().stream()
                                .map(Basket.Flights::getFlights).flatMap(Collection::stream)
                                .map(Basket.Flight::getPassengers).flatMap(Collection::stream)
                                .noneMatch(basketPassenger -> basketPassenger.getCode().equals(passengerId))
                ).withFailMessage(THEPASSENGER + passengerId + "is present in the basket")
                        .isTrue();
                break;
            default:
                break;
        }
        return this;
    }

    public BasketsAssertion thePassengerInformationHasNotBeenUpdated(List<String> passengerCodes, String ejPlus) {
        List<Basket.Passenger> basketPassengers = response.getBasket().getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .filter(h -> passengerCodes.contains(h.getCode())
                ).collect(Collectors.toList());

        for (Basket.Passenger basketPassenger : basketPassengers) {
            assertThat(basketPassenger.getPassengerDetails().getEjPlusCardNumber().equals(ejPlus))
                    .withFailMessage("The value for ejPlus should not be populated with value " + ejPlus)
                    .isFalse();
        }
        return this;
    }

    public BasketsAssertion theMembershipHasBeenStored(List<String> passengerCodes, String ejPlus, BasketHelper basketHelper, String channelUsed) throws EasyjetCompromisedException, InterruptedException {

        final ArrayList<List<Basket.Passenger>> myBasketPassengers = new ArrayList<>();

        pollingLoop().until(() -> {
            myBasketPassengers.add(basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), channelUsed)
                    .getOutbounds()
                    .stream()
                    .flatMap(f ->
                            f.getFlights().stream()).flatMap(g ->
                            g.getPassengers().stream()).filter(h ->
                            passengerCodes.contains(h.getCode())).collect(Collectors.toList()
                    ));

            return myBasketPassengers.get(0).get(0).getPassengerDetails().getEjPlusCardNumber().equals(ejPlus);

        });


        for (Basket.Passenger basketPassenger : myBasketPassengers.get(0)) {
            assertThat(basketPassenger.getPassengerDetails().getEjPlusCardNumber().equals(ejPlus))
                    .withFailMessage("The value for ejPlus " + basketPassenger.getPassengerDetails().getEjPlusCardNumber() + " does not match the expected value " + ejPlus)
                    .isTrue();
        }
        return this;
    }

    public BasketsAssertion thePassengerInformationHasBeenStored(ArrayList<String> passengerCodes) {
        List<Basket.Passenger> basketPassengers = response.getBasket().getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .filter(h -> passengerCodes.contains(h.getCode()))
                .collect(Collectors.toList());

        for (Basket.Passenger basketPassenger : basketPassengers) {
            assertThat(Objects.isNull(basketPassenger.getPassengerDetails().getName().getFirstName()))
                    .withFailMessage("The basket does not contains updated information for the firstname of passenger id " + basketPassenger.getCode())
                    .isFalse();

            assertThat(Objects.isNull(basketPassenger.getPassengerDetails().getName().getLastName()))
                    .withFailMessage("The basket does not contains updated information for the lastname of passenger id " + basketPassenger.getCode())
                    .isFalse();

            assertThat(Objects.isNull(basketPassenger.getPassengerDetails().getEmail()))
                    .withFailMessage("The basket does not contains updated information for the email of passenger id " + basketPassenger.getCode())
                    .isFalse();
        }
        return this;
    }

    public BasketsAssertion seatsArePurchasedForEachPassenger(List<AddPurchasedSeatsRequestBody> purchsedSeatRequestBodyList) {
        seatsArePurchasedForEachPassengerInFlightList(response.getBasket().getOutbounds(), purchsedSeatRequestBodyList)
                .seatsArePurchasedForEachPassengerInFlightList(response.getBasket().getInbounds(), purchsedSeatRequestBodyList)
        ;
        return this;
    }

    public BasketsAssertion additionalSeatsArePurchasedForEachPassenger(List<AddPurchasedSeatsRequestBody> purchsedSeatRequestBodyList) {
        additionalSeatsArePurchasedForEachPassengerInFlightList(response.getBasket().getOutbounds(), purchsedSeatRequestBodyList)
                .additionalSeatsArePurchasedForEachPassengerInFlightList(response.getBasket().getInbounds(), purchsedSeatRequestBodyList)
        ;
        return this;
    }

    public BasketsAssertion checkTheCabinBagForProductSeat(List<AddPurchasedSeatsRequestBody> purchsedSeatRequestBodyList, String priceStandardSeat) {

        switch (priceStandardSeat) {
            case STANDARD:
                checkCabin(purchsedSeatRequestBodyList, true, "The cabin bag doesn't exist");
                break;
            case FLEXI:
                checkCabin(purchsedSeatRequestBodyList, false, "The cabin bag exist");
                break;
            default:
                break;
        }
        return this;
    }

    private void checkCabin(List<AddPurchasedSeatsRequestBody> purchasedSeatRequestBodyList, boolean condition, String message) {
        List<Basket.Flights> flights = response.getBasket().getOutbounds();
        Integer codeSeat = getSeatProductCode(testData.getSeatProductInBasket());

        assertThat(flights.stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .map(Basket.Passenger::getCabinItems)
                .flatMap(Collection::stream)
                .anyMatch(
                        getCabinItems -> getCabinItems.getBundleCode().equalsIgnoreCase(codeSeat.toString())
                ))
                .withFailMessage(message)
                .isEqualTo(condition);
    }

    /**
     * Get seat name from enum type
     *
     * @param aSeatProduct
     * @return
     */
    private Integer getSeatProductCode(PurchasedSeatHelper.SEATPRODUCTS aSeatProduct) {
        Integer code;
        switch (aSeatProduct) {
            case EXTRA_LEGROOM:
                code = 1;
                break;
            case STANDARD:
                code = 2;
                break;
            case UPFRONT:
                code = 3;
                break;
            default:
                code = 0;
                break;
        }
        return code;
    }

    public BasketsAssertion checkPriseSeatProduct(List<AddPurchasedSeatsRequestBody> purchsedSeatRequestBodyList) {


        List<Basket.Flights> flights = response.getBasket().getOutbounds();

        Double basketPrice = flights.stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .map(Basket.Passenger::getSeat).map(seat -> seat.getPricing().getBasePrice()).findFirst().get();

        Double mapSeatPrice = Double.valueOf(purchsedSeatRequestBodyList.stream()
                .flatMap(f -> f.getPassengerAndSeats().stream())
                .map(PassengerAndSeat::getSeat)
                .findFirst()
                .orElse(null)
                .getPrice());


        assertThat(basketPrice).isEqualTo(mapSeatPrice);
        return this;
    }

    private BasketsAssertion seatsArePurchasedForEachPassengerInFlightList(List<Basket.Flights> flights, List<AddPurchasedSeatsRequestBody> purchsedSeatRequestBodyList) {
        flights
                .forEach(journey ->
                        journey.getFlights()
                                .forEach(flight ->
                                        flight.getPassengers()
                                                .forEach(passenger ->
                                                        checkPassengerSeatIsCorrect(passenger, purchsedSeatRequestBodyList))));
        return this;
    }

    private void checkPassengerSeatIsCorrect(Basket.Passenger aPassenger, List<AddPurchasedSeatsRequestBody> addPurchasedSeatsRequestBodyList) {
        final Boolean[] compareComplete = {false};

        addPurchasedSeatsRequestBodyList.forEach(request ->
                request.getPassengerAndSeats().forEach(passengerAndSeat -> {
                    if (passengerAndSeat.getPassengerId().equals(aPassenger.getCode())) {
                        assertThat(passengerAndSeat.getSeat()).isEqualToIgnoringGivenFields(aPassenger.getSeat(), "type", "price");
                        compareComplete[0] = true;
                    }
                }));

        //Default trap, passenger should have no seat populated
        if (!(compareComplete[0])) {
            assertThat(aPassenger.getSeat().getCode()).isEmpty();
        }
    }

    private BasketsAssertion additionalSeatsArePurchasedForEachPassengerInFlightList(List<Basket.Flights> flights, List<AddPurchasedSeatsRequestBody> purchsedSeatRequestBodyList) {
        flights
                .forEach(journey ->
                        journey.getFlights()
                                .forEach(flight ->
                                        flight.getPassengers()
                                                .forEach(passenger ->
                                                        checkPassengerAdditionalSeatIsCorrect(passenger, purchsedSeatRequestBodyList))));
        return this;
    }

    private void checkPassengerAdditionalSeatIsCorrect(Basket.Passenger aPassenger, List<AddPurchasedSeatsRequestBody> addPurchasedSeatsRequestBodyList) {
        final Boolean[] compareComplete = {false};

        addPurchasedSeatsRequestBodyList.forEach(request ->
                request.getPassengerAndSeats().forEach(passengerAndSeat -> {
                    if (passengerAndSeat.getPassengerId().equals(aPassenger.getCode())) {
                        passengerAndSeat.getAdditionalSeats().forEach(additionalSeat -> {
                            assertThat(additionalSeat.getSeatNumber()).isEqualTo(aPassenger.getAdditionalSeats().stream().findFirst().get().getSeat().getSeatNumber());
                        });
                        compareComplete[0] = true;
                    }
                }));

        if (!(compareComplete[0])) {
            Optional<AbstractPassenger.AdditionalSeat> addlSeat = aPassenger.getAdditionalSeats().stream().findFirst();
            if (addlSeat.isPresent()) {
                assertThat(addlSeat.get().getSeat().getCode()).isEmpty();
            }
        }
    }

    public BasketsAssertion noSeatsArePurchasedForAnyPassengerInTheBasket() {
        assertThat(response.getBasket().getOutbounds().stream()
                .flatMap(bounds -> bounds.getFlights().stream())
                .flatMap(flight -> flight.getPassengers().stream())
                .filter(passenger -> passenger.getSeat() != null)
                .collect(Collectors.toList())
                .size()
        )
                .isEqualTo(0);
        return this;
    }

    public BasketsAssertion theBasketDoesNotContainsEntriesForTheFlight(CartDao cartDao, String flightKey) {

        assertThat(cartDao.getNumberOfProductsInTheCart(response.getBasket().getCode(), flightKey))
                .withFailMessage("The cart still contains products associated with " + flightKey)
                .isEqualTo(0);

        return this;
    }

    public BasketsAssertion theSeatIsAssignedToThePassenger(String seat) {
        String code;
        String name;
        switch (seat) {
            case "EXTRA_LEGROOM": {
                code = "1";
                name = "Extra legroom";
                break;
            }
            case "UPFRONT": {
                code = "2";
                name = "Up front";
                break;
            }
            case STANDARD: {
                code = "3";
                name = STANDARD;
                break;
            }
            default: {
                code = "3";
                name = STANDARD;
                break;
            }
        }

        List<Basket.Passenger> basketPassengers = response.getBasket().getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream())
                .flatMap(p -> p.getPassengers().stream())
                .collect(Collectors.toList());

        assertThat(basketPassengers.get(0).getSeat().getCode().equals(code));
        assertThat(basketPassengers.get(0).getSeat().getName().equals(name));
        return this;
    }

    public BasketsAssertion verifyThePriceForTheSeat(String seatPrice) {
        Double expectedSeatPrice = Double.parseDouble(seatPrice);
        List<Basket.Passenger> basketPassengers = response.getBasket().getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream())
                .flatMap(p -> p.getPassengers().stream())
                .collect(Collectors.toList());
        assertThat(basketPassengers.get(0).getSeat().getPricing().getBasePrice().doubleValue()).isEqualTo(expectedSeatPrice);
        return this;
    }

    public BasketsAssertion thePassengerIsNotAssociatedWithTheRemovedPassengers(CartDao cartDao, String basketId, String passenger, List<String> removedPassengers) {

        cartDao.getAssociatedPassenger(basketId, passenger).forEach(
                linkedPassenger ->
                        assertThat(removedPassengers)
                                .withFailMessage("The removed passenger " + linkedPassenger + " is still linked to " + passenger)
                                .doesNotContain(linkedPassenger));

        return this;

    }

    public BasketsAssertion theFlightAssociatedWithTheRemovedFlightIsSingle(CartDao cartDao, String basketId, List<String> flights) {

        flights.forEach(
                flight ->
                        assertThat(cartDao.getJourneyType(basketId, flight))
                                .withFailMessage("The flight " + flight + " has " + cartDao.getJourneyType(basketId, flight) + " as journeyType, not single")
                                .isEqualToIgnoringCase("single")
        );

        return this;
    }

    public BasketsAssertion thePassengerHaveNoInformationAssociated(PassengerInformationDao passengerInformationDao, List<String> removedPassengers) {

        removedPassengers.forEach(
                passenger -> assertThat(passengerInformationDao.getPassengerInformationNumber(passenger))
                        .withFailMessage(THEPASSENGER + passenger + " still have information associated.")
                        .isEqualTo(0)
        );

        return this;
    }

    public BasketsAssertion currencyIsRight(String currency) {

        assertThat(response.getBasket().getCurrency().getCode()).isEqualTo(currency);

        return this;
    }

    public BasketsAssertion priceAreUpdatedWithNewCurrency(String channel, Basket originalBasket, CurrencyModel oldCurrency, CurrencyModel newCurrency, CurrencyModel baseCurrency, FeesAndTaxesDao feesAndTaxesDao, HoldItemsDao holdItemsDao, BundleTemplateDao bundleDao, GetSeatMapResponse seatMap, BigDecimal margin) throws EasyjetCompromisedException {

        BasketCurrency basketCurrency = new BasketCurrency(this, response.getBasket(), channel, originalBasket, oldCurrency, newCurrency, baseCurrency, feesAndTaxesDao, holdItemsDao, bundleDao, seatMap, margin);
        return basketCurrency.priceAreUpdatedWithNewCurrency();

    }

    public BasketsAssertion taxPricesAreUpdatedWithNewCurrency(String channel, Basket originalBasket, CurrencyModel oldCurrency, CurrencyModel newCurrency, CurrencyModel baseCurrency, FeesAndTaxesDao feesAndTaxesDao, BigDecimal margin) throws EasyjetCompromisedException {
        BasketCurrency basketCurrency = new BasketCurrency(this, response.getBasket(), channel, originalBasket, oldCurrency, newCurrency, baseCurrency, feesAndTaxesDao, null, null, null, margin);
        return basketCurrency.taxPricesAreUpdatedWithNewCurrency();
    }

    public BasketsAssertion discountPricesAreUpdatedWithNewCurrency(String channel, Basket originalBasket, CurrencyModel oldCurrency, CurrencyModel newCurrency, CurrencyModel baseCurrency, FeesAndTaxesDao feesAndTaxesDao, BigDecimal margin) throws EasyjetCompromisedException {
        BasketCurrency basketCurrency = new BasketCurrency(this, response.getBasket(), channel, originalBasket, oldCurrency, newCurrency, baseCurrency, feesAndTaxesDao, null, null, null, margin);
        return basketCurrency.discountPricesAreUpdatedWithNewCurrency();
    }

    public BasketsAssertion priceAreRevertedToOriginalCurrency(Basket originalBasket) {

        assertThat(response.getBasket())
                .withFailMessage("The basket is different from the original one")
                .isEqualToComparingFieldByFieldRecursively(originalBasket);
                //.isEqualToComparingOnlyGivenFields(originalBasket,"totalAmountWithDebitCard","totalAmountWithCreditCard","subtotalAmountWithCreditCard","subtotalAmountWithDebitCard","journeyTotalWithCreditCard","journeyTotalWithDebitCard");

        return this;
    }

    public BasketsAssertion originalCurrencyIsStored(CartDao cartDao, String oldCurrency) {

        HashMap<String, String> currencies = new HashMap<>(cartDao.getCartCurrencies(response.getBasket().getCode()));

        assertThat(currencies.get("actual")).isEqualTo(response.getBasket().getCurrency().getCode());
        assertThat(currencies.get("original")).isEqualTo(oldCurrency);
        return this;
    }

    public BasketsAssertion basketComparison(Basket basket1, Basket basket2) {
        assertThat(basket1).isEqualToComparingFieldByFieldRecursively(basket2);
        return this;
    }

    public BasketsAssertion flightBasePriceUpdated(final String dataValue, final String flightKey) {
        if (response != null && response.getBasket() != null && CollectionUtils.isNotEmpty(response.getBasket().getOutbounds())) {
            for (Basket.Flights outbound : response.getBasket().getOutbounds()) {
                List<Basket.Flight> flights = outbound.getFlights().stream()
                        .filter(flight -> flight.getFlightKey().equalsIgnoreCase(flightKey))
                        .collect(Collectors.toList());
                flights.stream().forEach(
                        flight -> flight.getPassengers().stream().forEach(
                                passenger -> assertThat(passenger.getFareProduct().getPricing().getBasePrice().equals(Double.valueOf(dataValue)))
                                        .withFailMessage("Price missmatch between error message " + dataValue + " of commit booking response and the basket price " + passenger.getFareProduct().getPricing().getBasePrice())
                                        .isTrue()
                        ));
            }
        }
        return this;
    }

    public BasketsAssertion checkThatThepassengerHaveTheSameSeat(String oldSeat, String passengerId) {


        List<Basket.Flights> flights = response.getBasket().getOutbounds();


        assertThat(flights.stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .filter(passengers -> passengers.getCode().equals(passengerId))
                .map(Basket.Passenger::getSeat)
                .anyMatch(
                        getSeat -> getSeat.getSeatNumber().equals(oldSeat)
                ))
                .withFailMessage("The seat for this passengers is different from before")
                .isTrue();
        return this;
    }

    public BasketsAssertion flightPriceIsInNewCurrency(FindFlightsResponse.Flight expectedFlight, String fare, String passengerType) {

        Basket.Flight addedFlight = response.getBasket().getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .filter(flight -> flight.getFlightKey().equals(expectedFlight.getFlightKey()))
                .findFirst().get();

        Double expectedPrice = expectedFlight.getFareTypes().stream()
                .filter(fareType -> fareType.getFareTypeCode().equals(fare))
                .findFirst().get()
                .getPassengers().stream()
                .filter(passenger -> passenger.getType().equals(passengerType))
                .findFirst().get()
                .getBasePrice();

        addedFlight.getPassengers().forEach(
                passenger -> {
                    AbstractPassenger.FareProduct fareProduct = passenger.getFareProduct();
                    Double actualPrice = fareProduct.getPricing().getBasePrice();

                    assertThat(actualPrice)
                            .withFailMessage("The base price with new currency is not right: expected was: " + expectedPrice + ", actual is: " + actualPrice)
                            .isEqualTo(expectedPrice);
                }
        );

        return this;
    }

    public BasketsAssertion hasCabinBagBundleAddedToThePassenger(String passengerCode) {
        assertThat(response.getBasket().getOutbounds().stream()
                .flatMap(bound -> bound.getFlights().stream())
                .flatMap(flight -> flight.getPassengers().stream())
                .filter(passenger -> passenger.getCode().equals(passengerCode) && passenger.getCabinItems().size() == 1)
                .count()).isEqualTo(1);
        return this;
    }

    public BasketsAssertion infantOnLapBundleAdded(String passengerCode) {
        assertThat(response.getBasket().getOutbounds().stream()
                .flatMap(bound -> bound.getFlights().stream())
                .flatMap(flight -> flight.getPassengers().stream())
                .filter(passenger -> passenger.getCode().equals(passengerCode) && "InfantOnLap".equalsIgnoreCase(passenger.getFareProduct().getBundleCode()))
                .count()).isEqualTo(1);
        return this;
    }

    public BasketsAssertion passengerExists(String passengerCode) {
        assertThat(
                getPassengerCount(response.getBasket().getOutbounds(), passengerCode) +
                        getPassengerCount(response.getBasket().getInbounds(), passengerCode)
        )
                .isEqualTo(1);
        return this;
    }

    @Step("Infant type is changed for flight {1}: Basket {0}, Infant {2}, Passenger type {3}")
    public BasketsAssertion passengerTypeIsChanged(Basket basket, String flightKey, String passengerId, String passengerType, String fareProductType) throws EasyjetCompromisedException {

        Basket.Passenger passenger = basket.getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                .filter(passengerCode -> passengerCode.getCode().equals(passengerId))
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("The passenger is not present in the basket"));

        assertThat(passenger.getPassengerDetails().getPassengerType()).isEqualTo(passengerType);
        assertThat(passenger.getFareProduct().getType()).isEqualTo(fareProductType);
        return this;
    }

    public BasketsAssertion checkAddSeatForAdditionalSeat(Basket basket, List<AddPurchasedSeatsRequestBody> purchsedSeatRequestBodyList) throws EasyjetCompromisedException {


        List<String> seat = purchsedSeatRequestBodyList.stream()
                .map(AddPurchasedSeatsRequestBody::getPassengerAndSeats)
                .flatMap(Collection::stream)
                .map(PassengerAndSeat::getAdditionalSeats)
                .flatMap(Collection::stream)
                .map(AdditionalSeat::getSeatNumber).collect(Collectors.toList());

        List<String> seatAdditionalSeat = basket.getOutbounds().stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                .flatMap(Collection::stream)
                .map(Basket.Passenger::getAdditionalSeats)
                .flatMap(Collection::stream)
                .map(AbstractPassenger.AdditionalSeat::getSeat)
                .map(AbstractPassenger.Seat::getSeatNumber)
                .collect(Collectors.toList());

        Collections.sort(seat);
        Collections.sort(seatAdditionalSeat);

        assertThat(seat).containsExactlyElementsOf(seatAdditionalSeat);

        return this;
    }

    public BasketsAssertion checkAddSSRInBasket(Basket basket) throws EasyjetCompromisedException {

        List<Basket.Flights> flights = basket.getOutbounds();

        assertThat(flights.stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .anyMatch(passengers -> "WHC".equals(passengers.getSpecialRequests().getSsrs().get(0).getCode())))
                .withFailMessage("The seat for this passengers is different from before")
                .isTrue();

        return this;
    }

    public BasketsAssertion checkThatThePassengerNotHaveSeat(Basket basket) throws EasyjetCompromisedException {

        List<Basket.Flights> flights = basket.getOutbounds();

        assertThat(flights.stream()
                .map(Basket.Flights::getFlights).flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers).flatMap(Collection::stream)
                .filter(pass -> pass.getCode().equalsIgnoreCase(testData.getPassengerIdFromChange()) && Objects.isNull(pass.getSeat()))
                .collect(Collectors.toList()))
                .withFailMessage("No passenger with desired code and without seat")
                .isNotEmpty();
        return this;
    }

    public BasketsAssertion checkBasketContainsBookingTypeAndReason(Basket basket) {

        assertThat(basket.getBasketType().contains("STANDARD_CUSTOMER")).isTrue()
                .withFailMessage("Incorrect basket type");
        assertThat(basket.getBookingReason().contains("LEISURE")).isTrue()
                .withFailMessage("Incorrect booking reason");
        return this;
    }

    public BasketsAssertion flightIsAddedToTheBasket(String flightKey) {
        assertThat(Stream.concat(response.getBasket().getOutbounds().stream(), response.getBasket().getInbounds().stream())
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .anyMatch(flight -> flight.getFlightKey().equals(flightKey)))
                .withFailMessage("The flight " + flightKey + " has not been added to the basket")
                .isTrue();

        return this;
    }

    public BasketsAssertion standbyStockLevelIsReserved(String flightKey) {
        assertThat(flightsDao.getReservedStockLevelForFlight(flightKey, "STANDBY"))
                .withFailMessage("The standby stock level have not been reserved")
                .isGreaterThan(0);
        return this;
    }

    public BasketsAssertion theFlightIsNotPresentInTheBasket(String flightKey) {
        assertThat(Stream.concat(response.getBasket().getOutbounds().stream(), response.getBasket().getInbounds().stream())
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .noneMatch(flight -> flight.getFlightKey().equals(flightKey)))
                .withFailMessage("The flight " + flightKey + " is still in the basket")
                .isTrue();
        return this;
    }

    public BasketsAssertion checkThatInfantOnSeatIsAssociatedToFirstAdult(Basket basket) {

        List<Basket.Flights> flights = basket.getOutbounds();

        List<Basket.Passenger> passenger = flights.stream()
                .flatMap(f -> f.getFlights().stream())
                .flatMap(p -> p.getPassengers().stream())
                .filter(p -> p.getFareProduct().getOrderEntryNumber().contains("0"))
                .collect(Collectors.toList());

        assertThat(!passenger.get(0).getInfantsOnSeat().isEmpty())
                .isTrue()
                .withFailMessage("No infant on lap attached to the first passenger");

        return this;
    }

    public BasketsAssertion checkThatRatioInfantToAdultsDoesNotExceedAllowed(Basket basket, String passengerPos) {
        List<Basket.Flights> flights = basket.getOutbounds();

        if ("Second".equalsIgnoreCase(passengerPos)) {

            List<Basket.Passenger> passenger = flights.stream()
                    .flatMap(f -> f.getFlights().stream())
                    .flatMap(p -> p.getPassengers().stream())
                    .filter(p -> "adult".equalsIgnoreCase(p.getPassengerDetails().getPassengerType()))
                    .collect(Collectors.toList());

            passenger.forEach(eachPassenger -> {
                assertThat(eachPassenger.getInfantsOnSeat().size() == 2)
                        .isTrue()
                        .withFailMessage("Infant on Seat to Adult ratio not acheived or exceeded : ");
            });
        } else if ("first".equalsIgnoreCase(passengerPos)) {

            List<Basket.Passenger> passenger = flights.stream()
                    .flatMap(f -> f.getFlights().stream())
                    .flatMap(p -> p.getPassengers().stream())
                    .filter(p -> p.getFareProduct().getOrderEntryNumber().contains("0"))
                    .collect(Collectors.toList());

            assertThat(passenger.get(0).getInfantsOnSeat().size() == 2)
                    .isTrue()
                    .withFailMessage("Infant on Seat to Adult ratio not acheived or exceeded : " + "No of infants : " + passenger.get(0).getInfantsOnSeat().size());

        } else {
            List<Basket.Passenger> passenger = flights.stream()
                    .flatMap(f -> f.getFlights().stream())
                    .flatMap(p -> p.getPassengers().stream())
                    .filter(p -> p.getFareProduct().getOrderEntryNumber().contains("0"))
                    .collect(Collectors.toList());

            assertThat(passenger.get(0).getInfantsOnSeat().size() == 1)
                    .isTrue()
                    .withFailMessage("Infant on Seat to Adult ratio not acheived or exceeded : " + "No of infants : " + passenger.get(0).getInfantsOnSeat().size());
        }

        return this;
    }

    public BasketsAssertion standbyStockLevelIsReleased(String flightKey) {
        assertThat(flightsDao.getReservedStockLevelForFlight(flightKey, "STANDBY"))
                .withFailMessage("The standby stock level have not been released")
                .isEqualTo(0);
        return this;
    }

    public BasketsAssertion thePassengerIsNotPresentInTheBasket(String passengerId) {
        assertThat(Stream.concat(response.getBasket().getOutbounds().stream(), response.getBasket().getInbounds().stream())
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                .noneMatch(passenger -> passenger.getCode().equals(passengerId)))
                .withFailMessage("The passenger " + passengerId + " is still in the basket")
                .isTrue();
        return this;
    }

    public BasketsAssertion checkPassengerStatus(String passengerStatus) {
        response.getBasket().getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream())
                .flatMap(p -> p.getPassengers().stream())
                .forEach(passenger ->
                        assertThat(passenger.getPassengerStatus().equals(passengerStatus))
                                .withFailMessage("passengerstatus not set to Booked")
                                .isTrue());

        return this;
    }

    public BasketsAssertion newPriceIsApplied(String flightKey, String passengerType, String newPrice) throws EasyjetCompromisedException {
        Double price;
        try {
            List<CurrencyModel> currencies = currenciesDao.getCurrencies(true);
            BigDecimal margin = currenciesDao.getCurrencyConversionMargin();
            HashMap<String, String> basketCurrencies = new HashMap<>(cartDao.getCartCurrencies(testData.getData(SerenityFacade.DataKeys.BASKET_ID)));

            CurrencyModel oldCurrency = currencies.stream().filter(
                    currency -> currency.getCode()
                            .equals(basketCurrencies.get("original")))
                    .findFirst().get();

            CurrencyModel newCurrency = currencies.stream().filter(
                    currency -> currency.getCode()
                            .equals(basketCurrencies.get("actual")))
                    .findFirst().get();

            CurrencyModel baseCurrency = currencies.stream().filter(
                    CurrencyModel::isBaseCurrency)
                    .findFirst().get();

            price = new BigDecimal(newPrice)
                    .divide(new BigDecimal(oldCurrency.getConversion()), baseCurrency.getDecimalPlaces(), RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(newCurrency.getConversion())).setScale(newCurrency.getDecimalPlaces(), RoundingMode.HALF_UP)
                    .multiply(margin).setScale(newCurrency.getDecimalPlaces(), RoundingMode.HALF_UP)
                    .doubleValue();

        } catch (EmptyResultDataAccessException ignored) {
            price = Double.valueOf(newPrice);
        }

        Double finalPrice = price;
        response.getBasket().getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .filter(flight -> flight.getFlightKey().equals(flightKey))
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("Flight " + flightKey + " is not present in the basket"))
                .getPassengers().stream()
                .filter(passenger -> passenger.getPassengerDetails().getPassengerType().equals(passengerType))
                .forEach(passenger ->
                        assertThat(passenger.getFareProduct().getPricing().getBasePrice())
                                .withFailMessage("The price has not been updated for passenger " + passenger.getCode() + "; expected was " + finalPrice + ", actual is " + passenger.getFareProduct().getPricing().getBasePrice())
                                .isEqualTo(finalPrice)
                );

        return this;
    }

    public BasketsAssertion basketTypeIsRight(String expectedBasketType) {
        String actualBasketType = response.getBasket().getBasketType();
        assertThat(actualBasketType)
                .withFailMessage("The basket type is wrong: expected " + expectedBasketType + "; actual " + actualBasketType)
                .isEqualTo(expectedBasketType);
        return this;
    }

    public BasketsAssertion verifyAdminFeeIsNotAdded() {
        assertThat(response.getBasket().getFees().getItems().stream()
                .noneMatch(t -> t.getCode().equals(ADMIN_FEE)))
                .withFailMessage("The basket contains admin fee").isTrue();
        return this;
    }

    private List<Basket.Passenger> groupBookingPassengersInTheBasket() {
        return Stream.concat(response.getBasket().getOutbounds().stream(),
                response.getBasket().getInbounds().stream())
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .filter(f -> f.getFareProduct().getBundleCode().equalsIgnoreCase(GROUP_BUNDLE_CODE))
                .collect(Collectors.toList());
    }

    public BasketsAssertion verifyGroupBookingFeesDiscountsAdded(String type) {
        List<Basket.Passenger> passengers = groupBookingPassengersInTheBasket();

        if (type.equalsIgnoreCase(GROUP_BOOKING_FEE)) {
            for (Basket.Passenger passenger : passengers) {
                assertThat(passenger.getFareProduct()
                        .getPricing().getFees().stream()
                        .anyMatch(f -> f.getName().equalsIgnoreCase(GROUP_BOOKING_FEE)))
                        .withFailMessage("The group booking fee is not applie")
                        .isTrue();
            }
        } else if (type.equalsIgnoreCase(GROUP_BOOKING_INTERNET_DISCOUNT)) {
            for (Basket.Passenger passenger : passengers) {
                assertThat(passenger.getFareProduct().getPricing().getDiscounts().stream()
                        .anyMatch(f -> f.getName().equalsIgnoreCase(GROUP_BOOKING_INTERNET_DISCOUNT)))
                        .withFailMessage("The group booking internet discount is not applied")
                        .isTrue();
            }
        }
        return this;
    }


    public BasketsAssertion theInfantIsOnTheLapOfTheSecondAdult(Basket originalBasket) {
        //get original adult who had no infant on their lap
        List<Basket.Passenger> adultsWithNoInfant = originalBasket.getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .filter(h -> (h.getInfantsOnLap().isEmpty() || h.getInfantsOnLap().isEmpty()) && h.getPassengerDetails().getPassengerType().equals(CommonConstants.ADULT))
                .collect(Collectors.toList());
        List<Basket.Passenger> adultsWithInfant = response.getBasket().getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream()).filter(h -> (!h.getInfantsOnLap().isEmpty() || !h.getInfantsOnSeat().isEmpty()) && h.getPassengerDetails().getPassengerType().equals(CommonConstants.ADULT))
                .collect(Collectors.toList());

        for (Basket.Passenger adult : adultsWithNoInfant) {
            if (adultsWithInfant.contains(adult))
                assertThat(true);
        }
        return this;
    }


    public BasketsAssertion seatEntitlementBasedOnFareType() {

        String fareType = testData.getData(FARE_TYPE).toString();
        String stdSeatBasePrice = testData.getData(STANDARD_BASEPRICE).toString();
        String upfrontSeatBasePrice = testData.getData(UPFRONT_BASEPRICE).toString();
        String extraLegroomSeatBasePrice = testData.getData(EXTRA_LEGROOM_BASEPRICE).toString();
        Double finalPrice = null;
        Double actualPrice = response.getBasket().getPriceDifference().getAmountWithDebitCard();

        if (fareType.equalsIgnoreCase("Standard")) {
            if (testData.getSeatProductInBasket().toString().equalsIgnoreCase("STANDARD")) {
                Assertions.assertThat(actualPrice).withFailMessage("Basket total is incorrect. Expected debit price difference is "+testData.getData(STANDARD_BASEPRICE)+". But the actual is "+actualPrice).isEqualTo(testData.getData(STANDARD_BASEPRICE));
            } else if (testData.getSeatProductInBasket().toString().equalsIgnoreCase("UPFRONT")) {
                Assertions.assertThat(actualPrice).withFailMessage("Basket total is incorrect. Expected debit price difference is "+testData.getData(UPFRONT_BASEPRICE)+". But the actual is "+actualPrice).isEqualTo(testData.getData(UPFRONT_BASEPRICE));
            } else if (testData.getSeatProductInBasket().toString().equalsIgnoreCase("EXTRA_LEGROOM")) {
                Assertions.assertThat(actualPrice).withFailMessage("Basket total is incorrect. Expected price difference is "+testData.getData(EXTRA_LEGROOM_BASEPRICE)+". But the actual is "+actualPrice).isEqualTo(testData.getData(EXTRA_LEGROOM_BASEPRICE));
            }
        }

        else if (fareType.equalsIgnoreCase("Flexi")) {
            if (testData.getSeatProductInBasket().toString().equalsIgnoreCase("STANDARD")) {
                Assertions.assertThat(actualPrice).withFailMessage("Basket total is incorrect. Expected price difference is ZERO. But the actual is "+actualPrice).isZero();
            } else if (testData.getSeatProductInBasket().toString().equalsIgnoreCase("UPFRONT")) {
                finalPrice = Double.valueOf(upfrontSeatBasePrice) - Double.valueOf(stdSeatBasePrice);
                Assertions.assertThat(actualPrice).withFailMessage("Basket total is incorrect. Expected price difference is "+finalPrice+". But the actual is "+actualPrice).isEqualTo(finalPrice);
            } else if (testData.getSeatProductInBasket().toString().equalsIgnoreCase("EXTRA_LEGROOM")) {
                finalPrice = Double.valueOf(extraLegroomSeatBasePrice) - Double.valueOf(stdSeatBasePrice);
                Assertions.assertThat(actualPrice).withFailMessage("Basket total is incorrect. Expected price difference is "+finalPrice+". But the actual is "+actualPrice).isEqualTo(finalPrice);
            }
        }

        return this;
    }

    public BasketsAssertion theBasketContainsUpdatedPassengerDetails(Basket basket, Map<String, Object> passengers)
    {
        List<Basket.Passenger> basketTravellers = basket.getOutbounds().stream()
              .flatMap(f -> f.getFlights().stream())
              .flatMap(g -> g.getPassengers().stream())
              .filter(t -> t.getCode().contains(passengers.get("passengerCode1").toString()))
              .collect(Collectors.toList());
        for (Basket.Passenger t : basketTravellers) {
            assertThat(t.getPassengerDetails().getName().getFirstName()).isEqualTo(passengers.get("firstName"));
            assertThat(t.getPassengerDetails().getName().getLastName()).isEqualTo(passengers.get("lastName"));
            assertThat(t.getPassengerDetails().getPhoneNumber()).isEqualTo(passengers.get("phonenumber"));
        }
        return this;

    }

    public BasketsAssertion checkAddSSRInBasket(Basket basket,String ssrCode) throws EasyjetCompromisedException {

        List<Basket.Flights> flights = basket.getOutbounds();

        assertThat(flights.stream()
              .map(Basket.Flights::getFlights)
              .flatMap(Collection::stream)
              .map(Basket.Flight::getPassengers)
              .flatMap(Collection::stream)
              .anyMatch(passengers -> ssrCode.equals(passengers.getSpecialRequests().getSsrs().get(0).getCode())))
              .withFailMessage("The seat for this passengers is different from before")
              .isTrue();

        return this;
    }

}
