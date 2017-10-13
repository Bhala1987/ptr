package com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by giuseppecioce on 17/03/2017.
 */
@Builder
@Getter
@Setter
public class AddSportEquipmentRequestBody implements IRequestBody {

    private String productCode;
    private Integer quantity;
    private String passengerCode;
    private String flightKey;
    private Boolean override;
}
