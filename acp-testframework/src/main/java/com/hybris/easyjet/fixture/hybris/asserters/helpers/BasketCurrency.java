package com.hybris.easyjet.fixture.hybris.asserters.helpers;

import com.hybris.easyjet.database.hybris.dao.BundleTemplateDao;
import com.hybris.easyjet.database.hybris.dao.FeesAndTaxesDao;
import com.hybris.easyjet.database.hybris.dao.HoldItemsDao;
import com.hybris.easyjet.database.hybris.models.CurrencyModel;
import com.hybris.easyjet.database.hybris.models.FeesAndTaxesModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.asserters.BasketsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.GetSeatMapResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractProductItem;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AugmentedPriceItem;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by giuseppedimartino on 30/05/17.
 */
public class BasketCurrency {

    private static final String DOUBLE_ZERO = "0.0";
    private final BasketsAssertion assertion;
    private final Basket basket;
    private final String channel;
    private final Basket originalBasket;
    private final CurrencyModel oldCurrency;
    private final CurrencyModel newCurrency;
    private final CurrencyModel baseCurrency;
    private final FeesAndTaxesDao feesAndTaxesDao;
    private final HoldItemsDao holdItemsDao;
    private final BundleTemplateDao bundleDao;
    private final GetSeatMapResponse seatMap;
    private final BigDecimal margin;
    private Basket.Passenger actualPassenger = null;
    private Basket.Passenger originalPassenger = null;


    public BasketCurrency(BasketsAssertion assertion, Basket basket, String channel, Basket originalBasket, CurrencyModel oldCurrency, CurrencyModel newCurrency, CurrencyModel baseCurrency, FeesAndTaxesDao feesAndTaxesDao, HoldItemsDao holdItemsDao, BundleTemplateDao bundleDao, GetSeatMapResponse seatMap, BigDecimal margin) { //NOSONAR
        this.assertion = assertion;
        this.basket = basket;
        this.channel = channel;
        this.originalBasket = originalBasket;
        this.oldCurrency = oldCurrency;
        this.newCurrency = newCurrency;
        this.feesAndTaxesDao = feesAndTaxesDao;
        this.holdItemsDao = holdItemsDao;
        this.bundleDao = bundleDao;
        this.seatMap = seatMap;
        this.margin = margin;
        this.baseCurrency = baseCurrency;
    }

    public BasketsAssertion priceAreUpdatedWithNewCurrency() throws EasyjetCompromisedException {

        List<Basket.Flights> actualOutbounds = basket.getOutbounds();
        List<Basket.Flights> originalOutbounds = originalBasket.getOutbounds();
        checkJourneysUpdatedCurrency(actualOutbounds, originalOutbounds);

        List<Basket.Flights> actualInbounds = basket.getInbounds();
        List<Basket.Flights> originalInbounds = originalBasket.getInbounds();
        if (actualInbounds != null && originalInbounds != null) {
            checkJourneysUpdatedCurrency(actualInbounds, originalInbounds);
        }

        return assertion;
    }

    public BasketsAssertion taxPricesAreUpdatedWithNewCurrency() throws EasyjetCompromisedException {
        priceAreUpdatedWithNewCurrency();
        checkTaxPrices();
        return assertion;
    }

    public BasketsAssertion discountPricesAreUpdatedWithNewCurrency() throws EasyjetCompromisedException {
        priceAreUpdatedWithNewCurrency();
        checkDiscountPrices();
        return assertion;
    }

    private void checkJourneysUpdatedCurrency(List<Basket.Flights> actualJourneys, List<Basket.Flights> originalJourneys) throws EasyjetCompromisedException {
        assertThat(actualJourneys.size()).isEqualTo(originalJourneys.size());
        for (int i = 0; i < actualJourneys.size(); i++) {
            Basket.Flights actualJourney = actualJourneys.get(i);
            Basket.Flights originalJourney = originalJourneys.get(i);
            checkFlightsUpdatedCurrency(actualJourney, originalJourney);
        }
    }

    private void checkFlightsUpdatedCurrency(Basket.Flights actualJourney, Basket.Flights originalJourney) throws EasyjetCompromisedException {
        assertThat(actualJourney.getFlights().size()).isEqualTo(originalJourney.getFlights().size());
        for (int i = 0; i < actualJourney.getFlights().size(); i++) {
            Basket.Flight actualFlight = actualJourney.getFlights().get(i);
            Basket.Flight originalFlight = originalJourney.getFlights().get(i);
            checkPassengerUpdatedCurrency(actualFlight, originalFlight);
        }
    }

    private void checkPassengerUpdatedCurrency(Basket.Flight actualFlight, Basket.Flight originalFlight) throws EasyjetCompromisedException {
        assertThat(actualFlight.getPassengers().size()).isEqualTo(originalFlight.getPassengers().size());
        for (int i = 0; i < actualFlight.getPassengers().size(); i++) {
            actualPassenger = actualFlight.getPassengers().get(i);
            originalPassenger = originalFlight.getPassengers().get(i);

            AbstractPassenger.FareProduct fareProduct = actualPassenger.getFareProduct();
            Double actualPrice = fareProduct.getPricing().getBasePrice();
            Double expectedPrice = convertPrice(originalPassenger.getFareProduct().getPricing().getBasePrice())
                    .multiply(margin)
                    .setScale(newCurrency.getDecimalPlaces(), RoundingMode.HALF_UP).doubleValue();
            assertThat(actualPrice)
                    .withFailMessage("The base price with new currency is not right: expected was: " + expectedPrice + ", actual is: " + actualPrice)
                    .isEqualTo(expectedPrice);

            if(!actualPassenger.getHoldItems().isEmpty()) {
                checkAugmentedPriceItemUpdatedCurrency(fareProduct, actualPassenger);
                List<AbstractPassenger.HoldItem> holdItems = actualPassenger.getHoldItems();
                checkProducts(actualFlight, actualPassenger, originalPassenger, fareProduct, holdItems);
            }

            if(!actualPassenger.getAdditionalItems().isEmpty()) {
                List<AbstractPassenger.AdditionalItem> additionalItems = actualPassenger.getAdditionalItems();
                checkProducts(actualFlight, actualPassenger, originalPassenger, fareProduct, additionalItems);
            }

            if(actualPassenger.getSeat() != null) {
                AbstractPassenger.Seat seat = actualPassenger.getSeat();
                checkSeat(actualPassenger, seat);
            }
        }
    }

    private void checkProducts(Basket.Flight actualFlight, Basket.Passenger actualPassenger, Basket.Passenger originalPassenger, AbstractPassenger.FareProduct fareProduct, List<? extends AbstractProductItem> productItems) {
        Double originalPrice;
        for (int l = 0; l < productItems.size(); l++) {
            originalPrice = originalPassenger
                    .getHoldItems().get(l)
                    .getPricing()
                    .getBasePrice();
            if (originalPrice.toString().equals(DOUBLE_ZERO)) {
                assertThat(productItems.get(l).getPricing().getBasePrice())
                        .withFailMessage("Product price for item included in the bundle is not 0")
                        .isEqualTo(0.0);
            } else {
                checkProductUpdatedCurrency(productItems.get(l), actualFlight, actualPassenger, fareProduct);
            }
        }
    }

    private void checkSeat(Basket.Passenger actualPassenger, AbstractPassenger.Seat seat) throws EasyjetCompromisedException {
        Double expectedPrice;
        if (seat != null) {
            GetSeatMapResponse.Product seatPrice = seatMap.getProducts().stream()
                    .filter(seatProduct -> seatProduct.getId().equals(seat.getCode()))
                    .findFirst()
                    .orElseThrow(() -> new EasyjetCompromisedException("Seating service doesn't returned the seat present in the basket"));

            String includedSeat = bundleDao.getSeatIncluded(actualPassenger.getFareProduct().getBundleCode());
            GetSeatMapResponse.Product includedSeatPrice = null;
            if (includedSeat != null) {
                includedSeatPrice = seatMap.getProducts().stream()
                        .filter(seatProduct -> seatProduct.getId().equals(includedSeat))
                        .findFirst().orElse(null);
            }

            if (includedSeatPrice != null) {
                expectedPrice = new BigDecimal(
                        ((Double) (Double.valueOf(seatPrice.getBasePrice()) - Double.valueOf(includedSeatPrice.getBasePrice()))).toString())
                        .setScale(newCurrency.getDecimalPlaces(), RoundingMode.HALF_UP)
                        .doubleValue();
            } else {
                expectedPrice = Double.valueOf(seatPrice.getBasePrice());
            }

            assertThat(seatPrice.getOfferPrices().getWithDebitCardFee())
                    .withFailMessage("The response of the seating service contains an unexpected offer price: expected " + seatPrice.getName() + " price was " + expectedPrice + ", actual is " + seatPrice.getOfferPrices().getWithDebitCardFee())
                    .isEqualTo(expectedPrice);

            assertThat(seat.getPricing().getBasePrice())
                    .withFailMessage("The seat price with new currency is not right: expected was: " + expectedPrice + ", actual is: " + seat.getPricing().getBasePrice())
                    .isEqualTo(expectedPrice);

            checkAugmentedPriceItemUpdatedCurrency(seat, actualPassenger);
        }
    }

    private void checkProductUpdatedCurrency(AbstractProductItem holdItem, Basket.Flight flight, Basket.Passenger passenger, AbstractPassenger.FareProduct fareProduct) {
        Double expectedPrice = holdItemsDao.getProductPrice(
                channel,
                newCurrency.getCode(),
                flight.getFlightKey(),
                flight.getFlightKey().substring(9, 15),
                fareProduct.getName(),
                holdItem.getCode(),
                holdItem.getQuantity()
        );
        assertThat(expectedPrice)
                .withFailMessage("The hold item \"" + holdItem.getName() + "\" is still present in the basket even if the price for '" + newCurrency.getCode() + "' is not defined")
                .isNotNull();
        Double actualPrice = holdItem.getPricing().getBasePrice();
        assertThat(actualPrice)
                .withFailMessage("The price of the hold item have not been updated: expected was " + expectedPrice + ", actual is " + actualPrice)
                .isEqualTo(expectedPrice);

        checkAugmentedPriceItemUpdatedCurrency(holdItem, passenger);
    }

    private void checkAugmentedPriceItemUpdatedCurrency(AbstractProductItem holdItem, Basket.Passenger passenger) {
        List<AugmentedPriceItem> taxes = holdItem.getPricing().getTaxes();
        for (AugmentedPriceItem taxe : taxes) {
            assertThat(taxe.getAmount()).isEqualTo(
                    feesAndTaxesDao.getFees(taxe.getCode().split("_")[0], null, newCurrency.getCode(), passenger.getPassengerDetails().getPassengerType())
                            .get(0).getFeeValue()
            );
        }
        List<AugmentedPriceItem> fees = holdItem.getPricing().getFees();
        for (AugmentedPriceItem fee : fees) {
            FeesAndTaxesModel expectedFee = feesAndTaxesDao.getFees(fee.getCode(), null, newCurrency.getCode(), passenger.getPassengerDetails().getPassengerType())
                    .get(0);
            Double actualValue;
            if (StringUtils.isBlank(expectedFee.getFeeCurrency())) {
                actualValue = fee.getPercentage();
            } else {
                actualValue = fee.getAmount();
            }
            assertThat(actualValue).isEqualTo(expectedFee.getFeeValue());
        }
        List<AugmentedPriceItem.Discount> discount = holdItem.getPricing().getDiscounts();
        for (AugmentedPriceItem aDiscount : discount) {
            assertThat(aDiscount.getAmount()).isEqualTo(
                    feesAndTaxesDao.getDiscount(aDiscount.getCode().split("_")[0], null, newCurrency.getCode(), channel)
                            .get(0).getFeeValue()
            );
        }
    }

    private void checkTaxPrices(){
        Double actualPrice;
        Double expectedPrice;
        AbstractPassenger.FareProduct actualBasketFareProduct = actualPassenger.getFareProduct();
        List<AugmentedPriceItem> actualBasketTaxes = actualBasketFareProduct.getPricing().getTaxes();
        AbstractPassenger.FareProduct originalBasketFareProduct = originalPassenger.getFareProduct();
        List<AugmentedPriceItem> originalBasketTaxes = originalBasketFareProduct.getPricing().getTaxes();
        for(int i=0 ; i<actualBasketTaxes.size(); i++){
            actualPrice = actualBasketTaxes.get(i).getAmount();
            expectedPrice = convertPrice(originalBasketTaxes.get(i).getAmount())
                    .multiply(margin)
                    .setScale(newCurrency.getDecimalPlaces(), RoundingMode.HALF_UP).doubleValue();
            assertThat(actualPrice)
                    .withFailMessage("The tax price with new currency is not right: expected was: " + expectedPrice + ", actual is: " + actualPrice)
                    .isEqualTo(expectedPrice);
        }
    }

    private void checkDiscountPrices(){
        Double actualPrice;
        Double expectedPrice;
        AbstractPassenger.FareProduct actualBasketFareProduct = actualPassenger.getFareProduct();
        List<AugmentedPriceItem.Discount> actualBasketDiscounts = actualBasketFareProduct.getPricing().getDiscounts();
        AbstractPassenger.FareProduct originalBasketFareProduct = originalPassenger.getFareProduct();
        List<AugmentedPriceItem.Discount> originalBasketDiscounts = originalBasketFareProduct.getPricing().getDiscounts();
        for(int i=0 ; i<actualBasketDiscounts.size(); i++){
            actualPrice = actualBasketDiscounts.get(i).getAmount();
            expectedPrice = convertPrice(originalBasketDiscounts.get(i).getAmount())
                    .multiply(margin)
                    .setScale(newCurrency.getDecimalPlaces(), RoundingMode.HALF_UP).doubleValue();
            assertThat(actualPrice)
                    .withFailMessage("The discount price with new currency is not right: expected was: " + expectedPrice + ", actual is: " + actualPrice)
                    .isEqualTo(expectedPrice);
        }
    }


    private BigDecimal convertPrice(Double price) {
        return new BigDecimal(price.toString())
                .divide(new BigDecimal(oldCurrency.getConversion()), baseCurrency.getDecimalPlaces(), RoundingMode.HALF_UP)
                .multiply(new BigDecimal(newCurrency.getConversion())).setScale(newCurrency.getDecimalPlaces(), RoundingMode.HALF_UP);
    }

}