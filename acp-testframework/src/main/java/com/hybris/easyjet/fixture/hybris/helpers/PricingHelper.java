package com.hybris.easyjet.fixture.hybris.helpers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by giuseppecioce on 15/03/2017.
 */
@Getter
@Setter
@ToString
public class PricingHelper {
    private double totalAmountWithCreditCard;
    private double totalAmountWithDebitCard;
    private double subtotalAmountWithCreditCard;
    private double subtotalAmountWithDebitCard;

    public PricingHelper() {
        totalAmountWithCreditCard = 0;
        totalAmountWithDebitCard = 0;
        subtotalAmountWithCreditCard = 0;
        subtotalAmountWithDebitCard = 0;
    }

    public PricingHelper(double totalAmountWithCreditCard, double totalAmountWithDebitCard, double subtotalAmountWithCreditCard, double subtotalAmountWithDebitCard) {
        this.totalAmountWithCreditCard = totalAmountWithCreditCard;
        this.totalAmountWithDebitCard = totalAmountWithDebitCard;
        this.subtotalAmountWithCreditCard = subtotalAmountWithCreditCard;
        this.subtotalAmountWithDebitCard = subtotalAmountWithDebitCard;
    }
}
