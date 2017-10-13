package com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdditionalSeat {

    private String bundleCode;
    private String code;
    private String name;
    private String description;
    private Integer quantity;
    private Pricing pricing;
    private String seatNumber;
    private String seatBand;
    private List<String> seatCharacteristics = null;

}