package com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Pricing {
    private Double basePrice;
    private List<Item> taxes;
    private List<Item> fees;
    private List<Item> discounts;
    private Double totalAmountWithCreditCard;
    private Double totalAmountWithDebitCard;
}
