package com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractConfirmation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCommentsToCustomerResponse extends AbstractConfirmation<AddCommentsToCustomerResponse.OperationConfirmation>{

    @Getter
    @Setter
    public static class OperationConfirmation extends AbstractConfirmation.OperationConfirmation {
        public String commentCode;
    }


}