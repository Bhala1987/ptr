package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.hybris.easyjet.fixture.hybris.invoke.response.booking.createamendablebooking.OperationConfirmation;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AdditionalInformation;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Niyi Falade on 18/09/17.
 */
@Getter
@Setter
public class CreateCompensationResponse extends Response {

    private List<AdditionalInformation> additionalInformation;
    private OperationConfirmation operationConfirmation;
}
