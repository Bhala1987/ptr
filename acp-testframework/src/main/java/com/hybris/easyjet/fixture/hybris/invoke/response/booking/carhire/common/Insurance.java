package com.hybris.easyjet.fixture.hybris.invoke.response.booking.carhire.common;

import cucumber.api.java.eo.Do;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by sudhir on 15/08/2017.
 */
@Getter
@Setter
public class Insurance {
    private String insuranceCode;
    private String insuranceDescription;
    private Double insuranceUnitPrice;
    private String insuranceUnitPriceCreditCard;
    private Double insuranceUnitPriceInBookingCurrency;
    private Double insuranceUnitPriceCreditCardInBookingCurrency;
    private int rentalPrice;
    private Double rentalPriceCreditCard;
    private Double rentalPriceInBookingCurrency;
    private Double rentalPriceCreditCardInBookingCurrency;
    private Double excessWithInsurance;
    private Double excessWithInsuranceInBookingCurrency;
    private String insuranceType;
}
