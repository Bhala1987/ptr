package com.hybris.easyjet.fixture.hybris.invoke.response.booking.carhire.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by sudhir on 15/08/2017.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Equipment {
    private String equipmentCode;
    private String equipmentDescription;
    private double equipmentRentalPriceCreditCard;
    private double equipmentRentalPriceCreditCardInBookingCurrency;
    private double equipmentRentalPriceInBookingCurrency;
    private double equipmentUnitPrice;
    private double equipmentRentalPrice;
    private double equipmentUnitPriceCreditCard;
    private double equipmentUnitPriceInBookingCurrency;
    private double equipmentUnitPriceCreditCardInBookingCurrency;
    private String mandatoryItem;
    private double rentalMax;
}