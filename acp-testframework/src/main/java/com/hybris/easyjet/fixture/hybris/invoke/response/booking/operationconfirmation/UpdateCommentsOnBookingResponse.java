package com.hybris.easyjet.fixture.hybris.invoke.response.booking.operationconfirmation;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractConfirmation;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by rajakm on 03/08/2017.
 */
public class UpdateCommentsOnBookingResponse extends AbstractConfirmation<UpdateCommentsOnBookingResponse.OperationConfirmation> {

    @Getter
    @Setter
    public class OperationConfirmation extends AbstractConfirmation.OperationConfirmation {
        private String commentCode;
    }
}
