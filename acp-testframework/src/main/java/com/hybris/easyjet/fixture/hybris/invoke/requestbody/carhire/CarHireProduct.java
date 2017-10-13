package com.hybris.easyjet.fixture.hybris.invoke.requestbody.carhire;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.carhire.common.Car;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.carhire.common.Insurance;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
public class CarHireProduct implements IRequestBody {
    private String rateID;
    private String carCategoryCode;
    private String carCategoryName;
    private String sampleCar;
    private int numberDoors;
    private int numberSeats;
    private int numberBags;
    private boolean airCon;
    private boolean automatic;
    private int co2Quantity;
    private double totalPrice;
    private double dailyPrice;
    private String dailyPriceCreditCard;
    private double totalPriceInBookingCurrency;
    private double dailyPriceInBookingCurrency;
    private String currency;
    private String imageRef;
    private int minimumAgeForCountry;
    private int minimumAgeForCategory;
    private int minimumAgeForDriver;
    private String includedKM;
    private List<CarHireProduct.CarHireTax> taxes;
    private List<CarHireProduct.CarHireSurcharge> surcharges;
    private List<CarHireProduct.CarHireInsurance> insuranceList;

    @Getter
    @Setter
    public class CarHireTax {
        private String taxCode;
        private String taxRate;
    }

    @Getter
    @Setter
    public class CarHireSurcharge {
        private String surchargeCode;
        private String surchargeDescription;
    }

    @Getter
    @Setter
    public class CarHireInsurance {
        private String insuranceCode;
        private String insuranceDescription;
        private double insuranceUnitPrice;
        private double insuranceUnitPriceInBookingCurrency;
        private double rentalPrice;
        private double rentalPriceInBookingCurrency;
        private double excessWithInsurance;
        private double excessWithInsuranceInBookingCurrency;
        private String insuranceType;
    }
}