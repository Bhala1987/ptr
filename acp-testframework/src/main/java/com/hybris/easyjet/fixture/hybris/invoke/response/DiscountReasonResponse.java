package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.LocalizedName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by giuseppecioce on 31/03/2017.
 */
@Getter
@Setter
public class DiscountReasonResponse extends Response {
    private List<DiscountReason> discountReasons;

    @Getter
    @Setter
    public static class DiscountReason {
        private List<DiscountAmount> discountAmounts;
        private String discountCode;
        private List<LocalizedName> localizedName;
    }

    @Getter
    @Setter
    public static class DiscountAmount {
        protected String currencyCode;
        private Integer discountAmount;
    }

}