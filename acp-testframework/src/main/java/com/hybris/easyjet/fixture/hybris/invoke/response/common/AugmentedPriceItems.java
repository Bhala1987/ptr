package com.hybris.easyjet.fixture.hybris.invoke.response.common;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AugmentedPriceItems {
    private List<AugmentedPriceItem> items = new ArrayList<>();
    private Double totalAmount;

    @Getter
    @Setter
    public static class Discounts extends AugmentedPriceItem {
        private List<AugmentedPriceItem.Discount> items = new ArrayList<>();
        private Double totalAmount;
    }
}