package com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractConfirmation;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by robertadigiorgio on 28/02/2017.
 */
public class DeleteCustomerProfileResponse extends AbstractConfirmation<DeleteCustomerProfileResponse.OperationConfirmation> {

    @Getter
    @Setter
    public class OperationConfirmation extends UpdateConfirmationResponse.OperationConfirmation {
        public String message;
    }

}