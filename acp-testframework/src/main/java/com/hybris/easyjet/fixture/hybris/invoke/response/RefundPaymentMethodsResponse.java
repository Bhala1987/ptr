package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPayment;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.LocalizedName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class RefundPaymentMethodsResponse extends Response {
    private List<RefundPaymentMethod> refundPaymentMethods = new ArrayList<>();

    @Getter
    @Setter
    public static class RefundPaymentMethod extends AbstractPayment {
        private List<LocalizedName> localizedDescriptions = new ArrayList<>();
    }

}
