package com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractConfirmation;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by robertadigiorgio on 13/02/2017.
 */
public class GeneratePasswordResponse extends AbstractConfirmation<GeneratePasswordResponse.OperationConfirmation> {

    @Getter
    @Setter
    public static class OperationConfirmation extends UpdateConfirmationResponse.OperationConfirmation {
        private String password;
    }

}