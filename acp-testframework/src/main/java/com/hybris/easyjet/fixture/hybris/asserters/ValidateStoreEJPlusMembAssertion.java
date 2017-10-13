package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation.RegisterCustomerResponse;

/**
 * Created by Giuseppe Cioce on 19/12/2016.
 */
public class ValidateStoreEJPlusMembAssertion extends Assertion<ValidateStoreEJPlusMembAssertion, RegisterCustomerResponse> {

    public ValidateStoreEJPlusMembAssertion(RegisterCustomerResponse registerCustomerResponse) {

        this.response = registerCustomerResponse;
    }

}
