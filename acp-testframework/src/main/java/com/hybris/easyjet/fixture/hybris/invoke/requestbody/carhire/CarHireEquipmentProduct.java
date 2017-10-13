package com.hybris.easyjet.fixture.hybris.invoke.requestbody.carhire;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
public class CarHireEquipmentProduct implements IRequestBody {
    private String equipmentCode;
    private String equipmentDescription;
    private int quantity;
    private int equipmentUnitPrice;
    private double equipmentUnitPriceInBookingCurrency;
    private double equipmentRentalPrice;
    private double equipmentRentalPriceInBookingCurrency;
    private int rentalMax;
    private boolean isMandatory;
}