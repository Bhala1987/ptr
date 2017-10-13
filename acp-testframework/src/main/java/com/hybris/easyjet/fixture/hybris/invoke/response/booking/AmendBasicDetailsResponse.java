package com.hybris.easyjet.fixture.hybris.invoke.response.booking;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hybris.easyjet.fixture.IResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.createamendablebooking.OperationConfirmation;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AdditionalInformation;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * AmendBasicDetailsResponse class, it holds third party response
 */
@Setter
@Getter
@ToString
public class AmendBasicDetailsResponse extends Response implements IResponse {
    @JsonProperty("operationConfirmation")
    private OperationConfirmation operationConfirmation;
    private List<AdditionalInformation> additionalInformation = new ArrayList<>();
}