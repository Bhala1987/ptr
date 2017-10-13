package com.hybris.easyjet.fixture.hybris.invoke.response.booking.operationconfirmation;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractConfirmation;
import lombok.Getter;
import lombok.Setter;

/**
 * Response object for when a comment is deleted.
 *
 * @author Joshua Curtis <j.curtis@reply.com>
 */
public class DeleteCommentOnBookingResponse extends AbstractConfirmation<DeleteCommentOnBookingResponse.OperationConfirmation> {
    @Getter
    @Setter
    public class OperationConfirmation extends AbstractConfirmation.OperationConfirmation {
        public String commentCode;
    }
}