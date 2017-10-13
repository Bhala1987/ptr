package com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractConfirmation;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by robertadigiorgio on 09/02/2017.
 */
public class UpdatePasswordResponse extends AbstractConfirmation<UpdatePasswordResponse.OperationConfirmation> {

    @Getter
    @Setter
    public static class OperationConfirmation extends UpdateConfirmationResponse.OperationConfirmation {
        private String passwordStrength;
    }

}