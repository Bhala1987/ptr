package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.DeleteCustomerSSRAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation.DeleteCustomerSSRResponse;

/**
 * Created by robertadigiorgio on 28/02/2017.
 */
public class DeleteCustomerSSRService extends HybrisService implements IService {

    private DeleteCustomerSSRResponse deleteCustomerSSRResponse;

    /**
     * a service comprises a request, a client and an endpoint
     *
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    protected DeleteCustomerSSRService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public DeleteCustomerSSRResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return deleteCustomerSSRResponse;
    }

    @Override
    public DeleteCustomerSSRAssertion assertThat() {
        return new DeleteCustomerSSRAssertion(deleteCustomerSSRResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(deleteCustomerSSRResponse.getUpdateConfirmation());
    }

    @Override
    protected void mapResponse() {
        deleteCustomerSSRResponse = restResponse.as(DeleteCustomerSSRResponse.class);
    }
}
