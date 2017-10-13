package com.hybris.easyjet.fixture.hybris.invoke.response.common;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by g.dimartino on 22/04/17.
 */
@Getter
@Setter
@EqualsAndHashCode
public class Pricing {
    private Double basePrice;
    private List<AugmentedPriceItem.Discount> discounts = new ArrayList<>();
    private List<AugmentedPriceItem> fees = new ArrayList<>();
    private List<AugmentedPriceItem> taxes = new ArrayList<>();
    private Double totalAmountWithCreditCard;
    private Double totalAmountWithDebitCard;
    private Double totalAmount;
    private PriceDifference priceDifference;

    @Getter
    @Setter
    public static class PriceDifference {
        private Double amountWithCreditCard;
        private Double amountWithDebitCard;
        private String toeiCode;
    }
}