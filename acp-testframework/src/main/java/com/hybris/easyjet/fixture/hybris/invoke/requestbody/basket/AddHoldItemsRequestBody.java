package com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by giuseppecioce on 01/03/2017.
 */

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddHoldItemsRequestBody implements IRequestBody {
    private String productCode;
    private Integer quantity;
    private String passengerCode;
    private String flightKey;
    private String excessWeightProductCode;
    private Double price;
    private Double excessWeightPrice;
    private Integer excessWeightQuantity;
    private Boolean override;
}