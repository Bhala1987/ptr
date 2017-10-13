package com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class PremiumAmountPaid {

    @JsonProperty("basePrice")
    private String basePrice;
    @JsonProperty("taxes")
    private List<Item> taxes = null;
    @JsonProperty("fees")
    private List<Item> fees = null;
    @JsonProperty("discounts")
    private List<Item> discounts = null;
    @JsonProperty("totalAmountWithCreditCard")
    private String totalAmountWithCreditCard;
    @JsonProperty("totalAmountWithDebitCard")
    private String totalAmountWithDebitCard;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

}
