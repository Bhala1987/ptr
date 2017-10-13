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
public class BasketContent {
    private String code;
    private String basketLanguage;
    private String defaultCardType;
    private com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.Currency currency;
    private CustomerContext customerContext;
    private List<UniquePassenger> uniquePassengerList;
    private List<Journey> outbounds;
    private List<Journey> inbounds;
    private List<CarHire> carHires;
    private List<TravelInsurance> travelInsurances;
    private List<Hotel> hotels;
    private List<String> comments;
    private Taxes taxes;
    private Fees fees;
    private Discounts discounts;
    private Double subtotalAmountWithCreditCard;
    private Double subtotalAmountWithDebitCard;
    private Double totalAmountWithCreditCard;
    private Double totalAmountWithDebitCard;

}