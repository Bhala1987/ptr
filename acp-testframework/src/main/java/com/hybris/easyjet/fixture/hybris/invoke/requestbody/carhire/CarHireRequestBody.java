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
public class CarHireRequestBody implements IRequestBody {
    private String carCategory;
    private String locale;
    private String pickUpAirport;
    private String pickUpStation;
    private String pickUpDate;
    private String pickUpTime;
    private String dropOffAirport;
    private String dropOffStation;
    private String dropOffDate;
    private String dropOffTime;
    private String driverCountryResidence;
    private Integer ageOfDriver;
    private String target;
    private List<Insurance> insuranceList;
    private List<Equipment> equipmentList;
}