package com.hybris.easyjet.fixture.hybris.invoke.response.booking.createamendablebooking;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hybris.easyjet.fixture.IResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by vijayapalkayyam on 15/05/2017.
 */
@ToString
@Getter
@Setter
public class GetAmendableBookingResponse extends Response implements IResponse{

    @JsonProperty("operationConfirmation")
    private OperationConfirmation operationConfirmation;
}
