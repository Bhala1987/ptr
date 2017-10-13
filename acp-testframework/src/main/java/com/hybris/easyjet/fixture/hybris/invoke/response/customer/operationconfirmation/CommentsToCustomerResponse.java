package com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractConfirmation;
import lombok.Getter;
import lombok.Setter;

public class CommentsToCustomerResponse extends AbstractConfirmation.OperationConfirmation{
    @Getter
    @Setter
    public String commentCode;
}