package com.hybris.easyjet.fixture.hybris.invoke.response.booking.carhire.common;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
@Getter
@Setter
public class Car {
    private String rateID;
    private String carCategoryCode;
    private String carCategoryName;
    private String sampleCar;
    private Integer numberDoors;
    private Integer numberSeats;
    private String numberBags;
    private Boolean airCon;
    private Boolean automatic;
    private Integer co2Quantity;
    private Double totalPrice;
    private Double totalPriceCreditCard;
    private Double dailyPrice;
    private String dailyPriceCreditCard;
    private Double totalPriceInBookingCurrency;
    private Double totalPriceCreditCardInBookingCurrency;
    private Double dailyPriceInBookingCurrency;
    private Double dailyPriceCreditCardInBookingCurrency;
    private String currency;
    private String imageRef;
    private Integer minimumAgeForCountry;
    private Integer minimumAgeForCategory;
    private Integer minimumAgeForDriver;
    private Integer extraKMPrice;
    private String includedKM;
    private List<Taxes> taxes;
    private List<Surcharges> surcharges;
    private List<Insurance> insuranceList;
@Getter
@Setter
    private static class Surcharges {
        private String surchargeCode;
    }
@Getter
@Setter
    private static class Taxes {
        private String taxCode;
        private String taxRate;
    }
}
