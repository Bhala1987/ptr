package com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractConfirmation;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by robertadigiorgio on 28/02/2017.
 */
public class DeleteCustomerSSRResponse extends AbstractConfirmation<DeleteCustomerSSRResponse.OperationConfirmation> {

    @Getter
    @Setter
    public class OperationConfirmation extends UpdateConfirmationResponse.OperationConfirmation {
        public String message;
    }

}