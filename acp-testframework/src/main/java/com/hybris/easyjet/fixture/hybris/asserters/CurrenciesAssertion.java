package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.database.hybris.models.CurrencyModel;
import com.hybris.easyjet.fixture.hybris.invoke.response.CurrenciesResponse;
import org.assertj.core.api.Assertions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Created by daniel on 26/11/2016.
 * assertion wrapper for currencies response object, provides reusable assertions to all tests
 */
public class CurrenciesAssertion extends Assertion<CurrenciesAssertion, CurrenciesResponse> {

    public CurrenciesAssertion(CurrenciesResponse currenciesResponse) {

        this.response = currenciesResponse;
    }

    public CurrenciesAssertion onlyTheseCurrenciesWereReturned(List<CurrencyModel> expectedCurrencies) {

        assertThat(expectedCurrencies.size()).isEqualTo(response.getCurrencies().size());
        for (CurrencyModel expectedCurrency : expectedCurrencies) {
            Assertions.assertThat(response.getCurrencies()).extracting(
                    "code",
                    "displaySymbol",
                    "decimalPlaces",
                    "isBaseCurrency")
                    .contains(tuple(
                            expectedCurrency.getCode(),
                            expectedCurrency.getDisplaySymbol(),
                            expectedCurrency.getDecimalPlaces(),
                            expectedCurrency.isBaseCurrency()
                    ));
        }
        return this;
    }

    public void theseCurrenciesWereNotReturned(List<CurrencyModel> inactiveCurrencies) {

        for (CurrencyModel inactiveCurrency : inactiveCurrencies) {
            assertThat(response.getCurrencies()).flatExtracting("code").doesNotContain(inactiveCurrency.getCode());
        }
    }
}
