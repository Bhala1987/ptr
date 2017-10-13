package com.hybris.easyjet.fixture.hybris.invoke.response.common;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class AugmentedPriceItem {
    private String code;
    private String name;
    private Double amount;
    private Double percentage;
    private String comment;
    private String discountReasonCode;
    private String passengerCode;
    private String firedPromoMessage;

    @Getter
    @Setter
    public static class Discount extends AugmentedPriceItem {
        private Boolean isPriceOverrideDiscount;
        private String agentId;
        private String dateTime;
        private String channel;
        private String commentType;
    }
}
