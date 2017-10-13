package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.RegisterStaffFaresAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.registerStaffFares.RegisterStaffFaresResponse;

public class RegisterStaffFaresService extends HybrisService implements IService {

    private RegisterStaffFaresResponse registerStaffFaresResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    protected RegisterStaffFaresService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public RegisterStaffFaresAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new RegisterStaffFaresAssertion(registerStaffFaresResponse);
    }

    @Override
    public RegisterStaffFaresResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return registerStaffFaresResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(registerStaffFaresResponse.getRegistrationConfirmation());
    }

    @Override
    protected void mapResponse() {
        registerStaffFaresResponse = restResponse.as(RegisterStaffFaresResponse.class);
    }

}


