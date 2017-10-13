package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPayment;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.LocalizedName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PaymentMethodsResponse extends Response {
    private List<PaymentMethod> paymentMethods = new ArrayList<>();

    @Getter
    @Setter
    public static class PaymentMethod extends AbstractPayment {
        private List<LocalizedName> localizedNames = new ArrayList<>();
    }
}