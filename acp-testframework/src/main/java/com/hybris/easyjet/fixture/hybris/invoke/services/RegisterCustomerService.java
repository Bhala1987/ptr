package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.RegisterCustomerAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation.RegisterCustomerResponse;

/**
 * Created by dwebb on 12/15/2016.
 */
public class RegisterCustomerService extends HybrisService implements IService {

    private RegisterCustomerResponse registerCustomerResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    protected RegisterCustomerService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public RegisterCustomerAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new RegisterCustomerAssertion(registerCustomerResponse);
    }

    @Override
    public RegisterCustomerResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return registerCustomerResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(registerCustomerResponse.getRegistrationConfirmation());
    }

    @Override
    protected void mapResponse() {
        registerCustomerResponse = restResponse.as(RegisterCustomerResponse.class);
    }

}
