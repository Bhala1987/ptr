package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation.DeleteCustomerSSRResponse;

/**
 * Created by robertadigiorgio on 28/02/2017.
 */
public class DeleteCustomerSSRAssertion extends Assertion<DeleteCustomerSSRAssertion, DeleteCustomerSSRResponse> {

    public DeleteCustomerSSRAssertion(DeleteCustomerSSRResponse deleteCustomerSSRResponse) {

        this.response = deleteCustomerSSRResponse;
    }
}

