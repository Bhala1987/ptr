package com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CarHire {
    private String bundleCode;
    private String code;
    private String name;
    private String description;
    private Integer quantity;
    private Pricing pricing;
    private String category;
    private String rateId;
    private CheckInStation checkInStation;
    private CheckOutStation checkOutStation;
    private String checkInDateTime;
    private String checkOutDateTime;
    private String customerEmail;
    private String customerPhone;
    private PrimaryDriver primaryDriver;
    private OtherDrivers otherDrivers;
    private List<CarExtra> carExtras = null;
}
