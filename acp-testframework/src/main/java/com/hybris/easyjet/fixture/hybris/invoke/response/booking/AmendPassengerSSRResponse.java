package com.hybris.easyjet.fixture.hybris.invoke.response.booking;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hybris.easyjet.fixture.IResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.createamendablebooking.OperationConfirmation;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * AmendPassengerSSRResponse class
 */
@Setter
@Getter
@ToString
public class AmendPassengerSSRResponse extends Response implements IResponse {
    @JsonProperty("operationConfirmation")
    private OperationConfirmation operationConfirmation;
}
