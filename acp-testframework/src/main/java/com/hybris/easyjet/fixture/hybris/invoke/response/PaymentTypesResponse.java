package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPayment;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PaymentTypesResponse extends Response {
    private PaymentMethods paymentMethods;

    @Getter
    @Setter
    public static class PaymentMethods {
        private List<PaymentType> paymentType = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class PaymentType extends AbstractPayment{
        private AllowedCurrencies allowedCurrencies;
        private String allowedDaysTillDeparture;
        private String allowedMarketCountryCode;
    }

    @Getter
    @Setter
    public static class AllowedCurrencies {
        private List<Currency> currency = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class Currency {
        private String code;
    }

}