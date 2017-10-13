package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.database.hybris.dao.CurrenciesDao;
import com.hybris.easyjet.database.hybris.models.CurrencyModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.response.CurrencyConversionResponse;
import lombok.NoArgsConstructor;
import net.thucydides.core.annotations.Step;
import org.apache.commons.math3.util.Precision;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by vijayapalkayyam on 15/05/2017.
 */
@NoArgsConstructor
public class CurrencyConversionAssertion extends Assertion<CurrencyConversionAssertion, CurrencyConversionResponse> {

    public CurrencyConversionAssertion(CurrencyConversionResponse currencyConversionResponse) {
        this.response = currencyConversionResponse;
    }

    public void setResponse(CurrencyConversionResponse currencyConversionResponse) {
        this.response = currencyConversionResponse;
    }

    @Step("Amount calculated is right: original currency {0}, target currency {1}, amount {2}, goodwill percentage {3}")
    public CurrencyConversionAssertion amountCalculatedIsRight(String fromCurrencyCode, String toCurrencyCode, BigDecimal amountToConvert, BigDecimal margin, CurrenciesDao currenciesDao) throws EasyjetCompromisedException {
        //TODO check why ACP use two different conversion policy between this service and basket currency conversion
        List<CurrencyModel> activeCurrencies = currenciesDao.getCurrencies(true);

//        CurrencyModel baseCurrency = activeCurrencies.stream()
//                .filter(CurrencyModel::isBaseCurrency)
//                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("No base currency defined in Hybris"));

        CurrencyModel originalCurrency = activeCurrencies.stream()
                .filter(currencyModel -> currencyModel.getCode().equals(fromCurrencyCode))
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException(fromCurrencyCode + "  is not supported by Hybris"));

        CurrencyModel targetCurrency = activeCurrencies.stream()
                .filter(currencyModel -> currencyModel.getCode().equals(toCurrencyCode))
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException(toCurrencyCode + "  is not supported by Hybris"));

        Double convertedAmount =
                amountToConvert.doubleValue() / originalCurrency.getConversion() * targetCurrency.getConversion();
//                amountToConvert
//                .divide(new BigDecimal(originalCurrency.getConversion().toString()), baseCurrency.getDecimalPlaces(), RoundingMode.HALF_UP)
//                .multiply(new BigDecimal(targetCurrency.getConversion().toString())).setScale(targetCurrency.getDecimalPlaces(), RoundingMode.HALF_UP);

        double expectedAmount = Precision.round(convertedAmount - (convertedAmount * margin.doubleValue()), targetCurrency.getDecimalPlaces());
//                .subtract(convertedAmount.multiply(margin)
//                        //.setScale(targetCurrency.getDecimalPlaces(), RoundingMode.HALF_UP))
//                ).setScale(targetCurrency.getDecimalPlaces(), RoundingMode.HALF_UP).doubleValue();

        assertThat(response.getResult().getAmount())
                .withFailMessage("Expected amount is: " + expectedAmount + "; but service returned: " + response.getResult().getAmount())
                .isEqualTo(expectedAmount);

        return this;
    }
}
