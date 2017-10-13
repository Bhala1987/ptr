package com.hybris.easyjet.fixture.hybris.invoke.response.booking.carhire.common;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
@Getter
@Setter
public class Result {
    private String checkInStationID;
    private String checkOutStationID;
    private String ecPromotionCode;
    private List<Car> cars;
    private List<Equipment> equipmentList;
    private List<Insurance> insuranceList;
}
