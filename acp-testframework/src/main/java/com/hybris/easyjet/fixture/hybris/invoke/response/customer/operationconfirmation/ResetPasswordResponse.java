package com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractConfirmation;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by giuseppecioce on 01/03/2017.
 */
public class ResetPasswordResponse extends AbstractConfirmation<ResetPasswordResponse.OperationConfirmation> {

    @Getter
    @Setter
    public static class OperationConfirmation extends UpdateConfirmationResponse.OperationConfirmation {
        private String passwordResetEmail;
    }

}