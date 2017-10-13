package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.DeleteCustomerProfileAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation.DeleteCustomerProfileResponse;

/**
 * Created by robertadigiorgio on 28/02/2017.
 */
public class DeleteCustomerProfileService extends HybrisService implements IService {


    private DeleteCustomerProfileResponse deleteCustomerProfileResponse;

    /**
     * a service comprises a request, a client and an endpoint
     *
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    protected DeleteCustomerProfileService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public DeleteCustomerProfileResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return deleteCustomerProfileResponse;
    }

    @Override
    public DeleteCustomerProfileAssertion assertThat() {
        return new DeleteCustomerProfileAssertion(deleteCustomerProfileResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(deleteCustomerProfileResponse.getUpdateConfirmation());
    }

    @Override
    protected void mapResponse() {
        deleteCustomerProfileResponse = restResponse.as(DeleteCustomerProfileResponse.class);
    }
}
