package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.ValidateStoreEJPlusMembAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation.RegisterCustomerResponse;

/**
 * Created by Giuseppe Cioce on 19/12/2016.
 */
public class ValidateStoreEJPlusMembService extends HybrisService implements IService {

    private RegisterCustomerResponse registerCustomerResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    protected ValidateStoreEJPlusMembService(IRequest request, String endPoint) {
        super(request, endPoint);
    }


    @Override
    public ValidateStoreEJPlusMembAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new ValidateStoreEJPlusMembAssertion(registerCustomerResponse);
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
