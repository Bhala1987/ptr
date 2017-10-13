package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.helpers.PricingHelper;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.AddHoldItemsRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.response.HoldItemsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import lombok.NoArgsConstructor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.constants.CommonConstants.DIGITAL_CHANNEL;
import static com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger.HoldItem;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by rajakm on 07/03/2017.
 */
@NoArgsConstructor
public class AddHoldBagToBasketAssertion extends Assertion<AddHoldBagToBasketAssertion, BasketConfirmationResponse> {

    protected static Logger LOG = LogManager.getLogger(BasketsAssertion.class);
    private BasketsResponse basketsResponse;
    private List<HoldItem>[] holdItems;

    /**
     * @param addHoldBagResponse
     */
    public AddHoldBagToBasketAssertion(BasketsResponse basketsResponse, BasketConfirmationResponse addHoldBagResponse) {

        this.response = addHoldBagResponse;
        this.basketsResponse = basketsResponse;
    }

    public void setResponse(BasketConfirmationResponse response){
        this.response = response;
    }

    public AddHoldBagToBasketAssertion theBasketHasHoldBagItem() {

        assertThat(basketsResponse.getBasket()
                .getOutbounds()
                .get(0)
                .getFlights()
                .get(0)
                .getPassengers()
                .get(0)
                .getHoldItems()
                .get(0)
                .getQuantity()).isEqualTo(0);
        return this;
    }

    public AddHoldBagToBasketAssertion theNumberOfHoldBagsAddedToThePassenger(String numberOfHoldBags) {

        List<Basket.Passenger> passengers = basketsResponse.getBasket()
                .getOutbounds()
                .get(0)
                .getFlights()
                .get(0)
                .getPassengers();
        int numberOfBags = Integer.parseInt(numberOfHoldBags);
        int paxNo = 0;
        for (Basket.Passenger passenger : passengers) {
            assertThat(numberOfBags == passenger.getHoldItems().get(paxNo).getQuantity());
            paxNo = paxNo + 1;
        }
        return this;
    }

    public AddHoldBagToBasketAssertion holdBagAddedForEachPassenger(BasketsResponse basketsResponse) {

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


    public AddHoldBagToBasketAssertion holdBagFeesAppliedAtPassengerLevel(BasketsResponse basketsResponse) {
        List<Basket.Passenger> passengers = basketsResponse.getBasket()
                .getOutbounds()
                .get(0)
                .getFlights()
                .get(0)
                .getPassengers();
        assertThat(passengers.size()).isGreaterThan(0);
        for (Basket.Passenger passenger : passengers) {
            for (HoldItem item : passenger.getHoldItems()) {
                BigDecimal holdBagFees = new BigDecimal(item.getPricing().getBasePrice());
                assertThat(holdBagFees).isNotZero();
            }
        }
        return this;
    }

    public AddHoldBagToBasketAssertion creditCardFeesAreApplied(BasketsResponse basketsResponse, String channel) {
        List<Basket.Passenger> passengers = basketsResponse.getBasket()
                .getOutbounds()
                .get(0)
                .getFlights()
                .get(0)
                .getPassengers();
        assertThat(passengers.size()).isGreaterThan(0);
        for (Basket.Passenger passenger : passengers) {
            for (HoldItem item : passenger.getHoldItems()) {
                if (DIGITAL_CHANNEL.equalsIgnoreCase(channel)) {
                    assertThat(item.getPricing().getFees().size()).isNotZero();
                    if (!item.getExtraWeight().isEmpty()) {
                        item.getExtraWeight().forEach(
                                extraWeight -> assertThat(extraWeight.getPricing().getBasePrice()).isNotZero()
                        );
                    }
                }


            }
        }
        return this;
    }

    public AddHoldBagToBasketAssertion holdBagAddedAtPassengerLevelToSpecificFlight(BasketsResponse basketsResponse, String flightKey, String productCode) {
        List<Basket.Passenger> flightPassenger = basketsResponse.getBasket().getOutbounds().stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .filter(f -> f.getFlightKey().equals(flightKey))
                .flatMap(g -> g.getPassengers().stream())
                .collect(Collectors.toList());

        List<AbstractPassenger.HoldItem> holdItems = flightPassenger.stream()
                .flatMap(basketPassenger -> basketPassenger.getHoldItems().stream())
                //.filter(holdItem->holdItem.getType().equals(addedholdItem.getProductType()))
                .collect(Collectors.toList());
        assertThat(!holdItems.isEmpty());
        for (HoldItem item : holdItems
                ) {
            if (!"EXCESS_WEIGHT".equals(item.getType()))
                assertThat(item.getCode()).isEqualTo(productCode);
        }
        List<Basket.Flight> flights = basketsResponse.getBasket().getOutbounds().stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .collect(Collectors.toList());
        for (Basket.Flight flight : flights
                ) {
            if (!flight.getFlightKey().equals(flightKey)) {
                for (Basket.Passenger passenger : flight.getPassengers()) {
                    assertThat(passenger.getHoldItems().size()).isEqualTo(0);
                }
            }
        }

        return this;
    }
    public AddHoldBagToBasketAssertion holdBagAddedAtPassengerLevelToSpecificFlight(BasketsResponse basketsResponse, AddHoldItemsRequestBody requestBody, HoldItemsResponse.HoldItems  holdItemsResponse) {
        List<Basket.Passenger> flightPassenger = basketsResponse.getBasket().getOutbounds().stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .filter(f -> f.getFlightKey().equals(requestBody.getFlightKey()))
                .flatMap(g -> g.getPassengers().stream())
                .collect(Collectors.toList());
        flightPassenger.stream().forEach(
                passenger -> {
                    List<HoldItem> holdItems = passenger.getHoldItems().stream()
                            .filter(item -> item.getCode().equalsIgnoreCase(holdItemsResponse.getProductCode())).collect(Collectors.toList());
                        assertThat(holdItems.size()).isEqualTo(requestBody.getQuantity());
                        holdItems.stream().forEach(
                                item -> {
                                    assertThat(item.getCode()).isEqualToIgnoringCase(holdItemsResponse.getProductCode());
                                    assertThat(item.getPricing().getBasePrice()).isEqualTo(holdItemsResponse.getPrices().get(0).getBasePrice());
                                    assertThat(item.getQuantity()).isEqualTo(1);
                                }

                        );
                    }
        );

        return this;
    }
    public AddHoldBagToBasketAssertion holdBagAddedSpecificPassengerToSpecificFlight(BasketsResponse basketsResponse, AddHoldItemsRequestBody requestBody, HoldItemsResponse.HoldItems  holdItemsResponse) {
        List<Basket.Passenger> flightPassenger = basketsResponse.getBasket().getOutbounds().stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .filter(f -> f.getFlightKey().equals(requestBody.getFlightKey()))
                .flatMap(g -> g.getPassengers().stream())
                .collect(Collectors.toList());
        List<Basket.Passenger> passengers = flightPassenger.stream().filter(
                passenger -> passenger.getCode().equalsIgnoreCase(requestBody.getPassengerCode())).collect(Collectors.toList());
        passengers.stream().forEach(
                passenger -> {
                    List<HoldItem> holdItems = passenger.getHoldItems().stream()
                            .filter(item -> item.getCode().equalsIgnoreCase(holdItemsResponse.getProductCode())).collect(Collectors.toList());
                    assertThat(holdItems.size()).isEqualTo(requestBody.getQuantity());
                    holdItems.stream().forEach(
                            item -> {
                                assertThat(item.getCode()).isEqualToIgnoringCase(holdItemsResponse.getProductCode());
                                assertThat(item.getPricing().getBasePrice()).isEqualTo(holdItemsResponse.getPrices().get(0).getBasePrice());
                                assertThat(item.getQuantity()).isEqualTo(1);
                            }

                    );
                }
        );

        return this;
    }

    public AddHoldBagToBasketAssertion excessWeightAddedSuccessfullyToSpecificHoldBag(BasketsResponse basketsResponse, AddHoldItemsRequestBody requestBody, String holdItemIndex) {
        List<Basket.Passenger> flightPassenger = basketsResponse.getBasket().getOutbounds().stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .filter(f -> f.getFlightKey().equals(requestBody.getFlightKey()))
                .flatMap(g -> g.getPassengers().stream())
                .filter(passenger -> passenger.getCode().equals(requestBody.getPassengerCode()))
                .collect(Collectors.toList());


        List<AbstractPassenger.HoldItem> holdItems = flightPassenger.stream()
                .flatMap(basketPassenger -> basketPassenger.getHoldItems().stream())
                .filter(holdItem -> holdItem.getOrderEntryNumber().equals(holdItemIndex))
                .collect(Collectors.toList());

        assertThat(!holdItems.isEmpty());
        for (HoldItem item : holdItems
                ) {
            assertThat(!item.getExtraWeight().isEmpty());
            item.getExtraWeight().forEach(
                    extraWeight ->
                    {
                        assertThat(extraWeight.getQuantity()).isNotZero();
                        assertThat(extraWeight.getCode().equalsIgnoreCase(requestBody.getExcessWeightProductCode())).isTrue();
                    }
            );
        }
        List<Basket.Flight> flights = basketsResponse.getBasket().getOutbounds().stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .collect(Collectors.toList());
        for (Basket.Flight flight : flights) {
            if (!flight.getFlightKey().equals(requestBody.getFlightKey())) {
                for (Basket.Passenger passenger : flight.getPassengers()) {
                    assertThat(passenger.getHoldItems().size()).isEqualTo(0);
                }
            }
        }

        return this;
    }

    public AddHoldBagToBasketAssertion holdItemAddedForEachFlightForSpecificPassenger(BasketsResponse basketsResponse, String passengerCode, String productCode) {

        List<Basket.Passenger> flightPassenger = basketsResponse.getBasket().getOutbounds().stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .filter(g -> g.getCode().equals(passengerCode))
                .collect(Collectors.toList());

        for (Basket.Passenger passenger : flightPassenger) {
            List<AbstractPassenger.HoldItem> items = passenger.getHoldItems()
                    .stream()
                    .collect(Collectors.toList());
            assertThat(!items.isEmpty()).isTrue();
            for (HoldItem item : items) {
                if (!"EXCESS_WEIGHT".equals(item.getType())) {
                    assertThat(item.getCode().equals(productCode)).isEqualTo(true);
                    assertThat(item.getQuantity()).isEqualTo(1);
                }
            }
        }
        List<Basket.Flight> flights = basketsResponse.getBasket().getOutbounds().stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .collect(Collectors.toList());
        for (Basket.Flight flight : flights
                ) {
            for (Basket.Passenger passenger : flight.getPassengers()) {
                if (!passenger.getCode().equals(passengerCode))
                    assertThat(passenger.getHoldItems().size()).isEqualTo(0);
            }
        }
        return this;
    }

    public AddHoldBagToBasketAssertion holdItemAddedForEachPassenger(BasketsResponse basketsResponse) {

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
    public AddHoldBagToBasketAssertion allHoldItemsIncrementedAfterCancelBooking(Map<String,Integer> basketHoldItemsBeforeCancel,Map<String,Integer> basketHoldItemsAfterCancel) {
        basketHoldItemsBeforeCancel.keySet().forEach(key->{
            assertThat(basketHoldItemsBeforeCancel.get(key)>basketHoldItemsAfterCancel.get(key)).isTrue()
                    .withFailMessage("Items not removed form inventory after cancel booking for the item : "+key +" Expected is less than 0 but is " + basketHoldItemsAfterCancel.get(key));
        });
        return this;
    }

    public AddHoldBagToBasketAssertion excessWeightAddedToSpecificPassengerForAllFlights(BasketsResponse basketsResponse, String passengerCode, String expectedCode, int excessWeightQuantity) {
        List<Basket.Passenger> flightPassenger = basketsResponse.getBasket().getOutbounds().stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .filter(g -> g.getCode().equals(passengerCode))
                .collect(Collectors.toList());
        for (Basket.Passenger passenger : flightPassenger) {
            for (HoldItem item : passenger.getHoldItems()) {
                if (!"EXCESS_WEIGHT".equals(item.getType())) {
                    assertThat(item.getQuantity()).isEqualTo(excessWeightQuantity);
                    assertThat(item.getCode()).isEqualTo(expectedCode);
                    break;
                }
            }
        }
        return this;
    }


    public AddHoldBagToBasketAssertion excessWeightAddedAtPassengerLevel(BasketsResponse basketsResponse, String flightKey, String expectedCode, int excessWeightQuantity) {
        final int[] actualHoldBagCount = {0};
        List<Basket.Passenger> flightPassenger = basketsResponse.getBasket().getOutbounds().stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .filter(f -> f.getFlightKey().equals(flightKey))
                .flatMap(g -> g.getPassengers().stream())
                .collect(Collectors.toList());
        for (Basket.Passenger passenger : flightPassenger) {
            for (HoldItem item : passenger.getHoldItems()) {
                if (!item.getExtraWeight().isEmpty()) {
                    item.getExtraWeight().forEach(
                            extraWeight -> {
                                actualHoldBagCount[0] = extraWeight.getQuantity();
                                assertThat(extraWeight.getCode()).isEqualTo(expectedCode);
                            }
                    );
                }
                break;
            }
        }
        assertThat(actualHoldBagCount[0]).isEqualTo(excessWeightQuantity);
        return this;
    }


    public AddHoldBagToBasketAssertion quantityOfHoldBagAddedAsExpected(BasketsResponse basketsResponse, int quantity) {
        List<Basket.Passenger> passengers = basketsResponse.getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers();
        assertThat(passengers.size()).isGreaterThan(0);
        for (Basket.Passenger passenger : passengers) {
            for (HoldItem item : passenger.getHoldItems()) {
                if ("HOLD_BAG".equals(item.getType()))
                    assertThat(item.getQuantity()).isEqualTo(quantity);
            }
        }
        return this;
    }

    public AddHoldBagToBasketAssertion verifyBasketPriceIsUpdate(BasketsResponse basketsResponse, PricingHelper pricingHelper, String passengerCode, String productCode) {
        double totalPrice = 0;
        double prevValForTotDebit = pricingHelper.getTotalAmountWithDebitCard();
        double prevValForToCredit = pricingHelper.getTotalAmountWithCreditCard();
        List<Basket.Passenger> basketPassenger = basketsResponse.getBasket()
                .getOutbounds()
                .stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .filter(h -> h.getCode().equals(passengerCode))
                .collect(Collectors.toList());
        for (Basket.Passenger passenger : basketPassenger) {
            List<AbstractPassenger.HoldItem> items = passenger.getHoldItems()
                    .stream()
                    .filter(f -> f.getCode().equals(productCode))
                    .collect(Collectors.toList());
            for (HoldItem item : items) {
                double productTotDebit = item.getPricing().getTotalAmountWithDebitCard();
                double productTotCredit = item.getPricing().getTotalAmountWithCreditCard();

                double finalValForTotDebit = basketsResponse.getBasket().getTotalAmountWithDebitCard();
                double finalValForTotCredit = basketsResponse.getBasket().getTotalAmountWithCreditCard();
                totalPrice = totalPrice + item.getPricing().getTotalAmountWithDebitCard();
                Assert.assertEquals(finalValForTotDebit, (prevValForTotDebit + productTotDebit), 0.01);
                Assert.assertEquals(finalValForTotCredit, (prevValForToCredit + productTotCredit), 0.01);
            }
        }
        return this;
    }

    public AddHoldBagToBasketAssertion verifyBasketPriceIsUpdateForAllPassengers(BasketsResponse basketsResponse, PricingHelper pricingHelper) {
        BigDecimal totalDebitCardPrice = BigDecimal.ZERO;
        BigDecimal totalCreditCardPrice = BigDecimal.ZERO;
        BigDecimal prevValForTotDebit = new BigDecimal(String.valueOf(pricingHelper.getTotalAmountWithDebitCard()));
        BigDecimal prevValForToCredit = new BigDecimal(String.valueOf(pricingHelper.getTotalAmountWithCreditCard()));
        List<Basket.Passenger> basketPassenger = basketsResponse.getBasket()
                .getOutbounds()
                .stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .collect(Collectors.toList());
        for (Basket.Passenger passenger : basketPassenger) {
            List<AbstractPassenger.HoldItem> items = passenger.getHoldItems()
                    .stream()
                    //		.filter(f -> f.getType().equals(productCode))
                    .collect(Collectors.toList());
            for (HoldItem item : items) {
                BigDecimal productTotDebit = new BigDecimal(String.valueOf(item.getPricing().getTotalAmountWithDebitCard()));
                BigDecimal productTotCredit = new BigDecimal(String.valueOf(item.getPricing().getTotalAmountWithCreditCard()));
                totalDebitCardPrice = totalDebitCardPrice.add(productTotDebit);
                totalCreditCardPrice = totalCreditCardPrice.add(productTotCredit);
            }
        }
        double finalValForTotDebit = basketsResponse.getBasket().getTotalAmountWithDebitCard();
        double finalValForTotCredit = basketsResponse.getBasket().getTotalAmountWithCreditCard();
        assertThat(finalValForTotDebit).isEqualTo(prevValForTotDebit.add(totalDebitCardPrice).doubleValue());
        assertThat(finalValForTotCredit).isEqualTo(prevValForToCredit.add(totalCreditCardPrice).doubleValue());
        return this;
    }

    public AddHoldBagToBasketAssertion verifyStockLevelIsTheSame(int actual, int previous) {

        assertThat(actual == previous).isEqualTo(true);
        return this;
    }

    public AddHoldBagToBasketAssertion verifyStockLevelDecrease(int actual, int previous, int quantity) {
        assertThat(actual).isEqualTo(previous + quantity);
        return this;
    }

    public AddHoldBagToBasketAssertion verifySportItemInTheBasket(String basketCode, BasketsResponse basketsResponse) {

        assertThat(response.getOperationConfirmation().getBasketCode().equals(basketCode)).isEqualTo(true);
        List<AbstractPassenger.HoldItem> holdItems = getAllHoldItemsForOutboundPassengers(basketsResponse);
        verifyPricingPopulatedForHoldItems(holdItems);
        return this;
    }

    private List<AbstractPassenger.HoldItem> getAllHoldItemsForOutboundPassengers(BasketsResponse basketsResponse) {
        return basketsResponse
                .getBasket()
                .getOutbounds()
                .stream()
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .flatMap(h -> h.getHoldItems().stream())
                .collect(Collectors.toList());
    }

    public AddHoldBagToBasketAssertion verifyHoldItemAddedOnlyToTheExpectedPassenger(String productType, String passengerCodeForWhichHoldItemAdding, String amendableBasketCode, BasketsResponse basketResponse) {
        assertThat(response.getOperationConfirmation().getBasketCode().equals(amendableBasketCode)).isEqualTo(true);
        AbstractPassenger matchingPassenger = getMatchingPassenger(passengerCodeForWhichHoldItemAdding, basketResponse);
        List<AbstractPassenger> otherPassengers = getNonMatchingPassengers(passengerCodeForWhichHoldItemAdding, basketResponse);

        assertThat(matchingPassenger.getHoldItems().size()).isGreaterThan(0);

        verifyPricingPopulatedForHoldItems(matchingPassenger.getHoldItems());
        verifyThatHoldItemsAdded(productType, matchingPassenger.getHoldItems());
        verifyThatNoHoldItemsFor(otherPassengers);
        return this;
    }

    private AbstractPassenger getMatchingPassenger(String passengerCodeForWhichHoldItemAdding, BasketsResponse basketResponse) {
        AbstractPassenger matchedPassenger = getMatchingPassengerFromOneBound(passengerCodeForWhichHoldItemAdding, basketResponse.getBasket().getOutbounds());
        if (matchedPassenger == null) {
            matchedPassenger = getMatchingPassengerFromOneBound(passengerCodeForWhichHoldItemAdding, basketResponse.getBasket().getInbounds());
        }
        return matchedPassenger;
    }

    private List<AbstractPassenger> getNonMatchingPassengers(String passengerCodeForWhichHoldItemAdding, BasketsResponse basketResponse) {
        List<AbstractPassenger> nonMatchedPassengers = getNonMatchingPassengersOneSide(passengerCodeForWhichHoldItemAdding, basketResponse.getBasket().getOutbounds());
        nonMatchedPassengers.addAll(getNonMatchingPassengersOneSide(passengerCodeForWhichHoldItemAdding, basketResponse.getBasket().getInbounds()));
        return nonMatchedPassengers;
    }

    private AbstractPassenger getMatchingPassengerFromOneBound(String passengerCodeForWhichHoldItemAdding, List<Basket.Flights> bounds) {
        return bounds
                .stream()
                .flatMap(bound -> bound.getFlights().stream())
                .flatMap(flight -> flight.getPassengers().stream())
                .filter(passenger -> passenger.getCode().equals(passengerCodeForWhichHoldItemAdding))
                .findFirst()
                .orElse(null);
    }

    private List<AbstractPassenger> getNonMatchingPassengersOneSide(String passengerCodeForWhichHoldItemAdding, List<Basket.Flights> bounds) {
        return bounds
                .stream()
                .flatMap(bound -> bound.getFlights().stream())
                .flatMap(flight -> flight.getPassengers().stream())
                .filter(passenger -> !(passenger.getCode().equals(passengerCodeForWhichHoldItemAdding)))
                .collect(Collectors.toList());
    }

    private void verifyThatHoldItemsAdded(String productType, List<AbstractPassenger.HoldItem> holdItems) {
        holdItems.forEach(item -> {
            assertThat(item.getActive()).isTrue();
            assertThat(item.getEntryStatus()).isEqualTo("NEW");
        });
        if (containsIgnoreCase(productType, "hold bag")) {
            holdItems.forEach(item -> assertThat("HoldBagProduct".equals(item.getType())).isTrue());
        } else {
            holdItems.forEach(item -> assertThat("LargeSportsProduct".equals(item.getType())));
        }
    }

    private void verifyThatNoHoldItemsFor(List<AbstractPassenger> otherPassengers) {
        otherPassengers.forEach(passenger -> assertThat(passenger.getHoldItems().size()).isEqualTo(0));
    }

    private void verifyPricingPopulatedForHoldItems(List<AbstractPassenger.HoldItem> holdItems) {
        holdItems
                .forEach(
                        item -> {
                            assertThat(Objects.nonNull(item.getPricing().getBasePrice())).isEqualTo(true);
                            assertThat(Objects.nonNull(item.getPricing().getTotalAmountWithDebitCard())).isEqualTo(true);
                            assertThat(Objects.nonNull(item.getPricing().getTotalAmountWithCreditCard())).isEqualTo(true);
                        });
    }
}
