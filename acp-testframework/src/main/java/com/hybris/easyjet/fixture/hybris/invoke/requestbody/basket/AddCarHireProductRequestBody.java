package com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.carhire.CarHireEquipmentProduct;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.carhire.CarHireLocation;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.carhire.CarHireProduct;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.carhire.DriverContext;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
public class AddCarHireProductRequestBody implements IRequestBody {
    private CarHireLocation location;
    private DriverContext driver;
    private CarHireProduct carHireProduct;
    private List<CarHireEquipmentProduct> equipmentList;
}
