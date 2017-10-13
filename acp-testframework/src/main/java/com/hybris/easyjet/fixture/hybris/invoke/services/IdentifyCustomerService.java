package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.IdentifyCustomerAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.IdentifyCustomerResponse;

/**
 * Created by dwebb on 12/5/2016.
 */
public class IdentifyCustomerService extends HybrisService implements IService {

    private IdentifyCustomerResponse identifyCustomerResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    protected IdentifyCustomerService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public IdentifyCustomerAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new IdentifyCustomerAssertion(identifyCustomerResponse);
    }

    @Override
    public IdentifyCustomerResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return identifyCustomerResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(identifyCustomerResponse.getCustomers());
    }

    @Override
    protected void mapResponse() {
        identifyCustomerResponse = restResponse.as(IdentifyCustomerResponse.class);
    }

}
