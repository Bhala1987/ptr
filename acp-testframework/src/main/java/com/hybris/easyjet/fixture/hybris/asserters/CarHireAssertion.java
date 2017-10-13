package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.booking.carhire.CarHireResponse;
import java.util.List;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Sudhir on 03/08/2017.
 */
public class CarHireAssertion extends Assertion<CarHireAssertion, CarHireResponse> {

    /**
     * @param carHireResponse
     */
    public CarHireAssertion(CarHireResponse carHireResponse) {
       this.response = carHireResponse;
    }

    public void assertCurrenciesDisplayedCorrectly(String expectedCurrency) {
        List<String> collect = response.result.getCars().stream().map(car -> car.getCurrency()).collect(Collectors.toList());
        assertThat(collect.stream().allMatch(item -> expectedCurrency.contains(item))).withFailMessage(" Every item in list" + collect + " Contains" + expectedCurrency).isTrue();
    }
    public void assertProductIncludesDebitAndCreditCardPrices() {
        List<Double> totalPricesWithDebitCard = response.result.getCars().stream().map(car -> car.getTotalPrice()).collect(Collectors.toList());
        List<Double> totalPricesWithCreditCard = response.result.getCars().stream().map(car -> car.getTotalPriceCreditCard()).collect(Collectors.toList());
        assertThat(totalPricesWithDebitCard.stream().allMatch(item -> item > 0)).withFailMessage("Total prices are not greater than 0, actual price list items "+ totalPricesWithDebitCard).isTrue();
        assertThat(totalPricesWithCreditCard.stream().allMatch(item -> item > 0)).withFailMessage("Total prices with credit card are not greater than 0, actual price list items "+ totalPricesWithDebitCard).isTrue();
    }
}
