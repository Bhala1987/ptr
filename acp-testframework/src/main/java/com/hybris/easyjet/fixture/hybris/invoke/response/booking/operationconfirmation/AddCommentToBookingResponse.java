package com.hybris.easyjet.fixture.hybris.invoke.response.booking.operationconfirmation;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractConfirmation;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by rajakm on 05/05/2017.
 */
@Getter
@Setter
public class AddCommentToBookingResponse extends AbstractConfirmation<AddCommentToBookingResponse.OperationConfirmation> {

    @Getter
    @Setter
    public class OperationConfirmation extends AbstractConfirmation.OperationConfirmation {
        private String commentCode;
    }

}