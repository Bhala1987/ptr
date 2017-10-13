package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.CustomerLoginAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.CustomerLoginResponse;

/**
 * Created by daniel on 26/11/2016.
 */
public class LoginDetailsService extends HybrisService implements IService {

    private CustomerLoginResponse customerLoginResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    protected LoginDetailsService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public CustomerLoginAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new CustomerLoginAssertion(customerLoginResponse);
    }


    @Override
    public CustomerLoginResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        customerLoginResponse.getAuthenticationConfirmation().getAuthentication().getAccessToken();
        return customerLoginResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(customerLoginResponse.getAuthenticationConfirmation());
    }

    @Override
    protected void mapResponse() {
        customerLoginResponse = restResponse.as(CustomerLoginResponse.class);
    }
}
