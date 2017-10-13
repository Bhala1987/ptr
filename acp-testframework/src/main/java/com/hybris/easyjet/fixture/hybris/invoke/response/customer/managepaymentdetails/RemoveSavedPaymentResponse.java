package com.hybris.easyjet.fixture.hybris.invoke.response.customer.managepaymentdetails;

import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Sudhir Talluri on 04/07/2017.
 */
@Getter
@Setter
public class RemoveSavedPaymentResponse extends Response {
    private OperationConfirmation operationConfirmation;
}
