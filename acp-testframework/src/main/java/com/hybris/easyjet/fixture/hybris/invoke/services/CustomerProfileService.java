package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.CustomerProfileAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.CustomerProfileResponse;

/**
 * Created by dwebb on 12/2/2016.
 */

public class CustomerProfileService extends HybrisService implements IService {

    private CustomerProfileResponse customerProfileResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    protected CustomerProfileService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public CustomerProfileAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new CustomerProfileAssertion(customerProfileResponse);
    }

    @Override
    public CustomerProfileResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return customerProfileResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(customerProfileResponse.getCustomer());
    }

    @Override
    protected void mapResponse() {
        customerProfileResponse = restResponse.as(CustomerProfileResponse.class);
    }

}
