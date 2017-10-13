package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.GetRecentSearchServiceAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.CustomerRecentSearchesResponse;

/**
 * Created by ptr-kvijayapal on 1/23/2017.
 */
public class GetRecentSearchesService extends HybrisService implements IService {

    private CustomerRecentSearchesResponse customerRecentSearchesResponse;

    /**
     * a service comprises a request, a client and an endpoint
     *
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    protected GetRecentSearchesService(IRequest request, String endPoint) {
        super(request, endPoint);
    }


    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(customerRecentSearchesResponse);
    }

    @Override
    protected void mapResponse() {
        customerRecentSearchesResponse = restResponse.as(CustomerRecentSearchesResponse.class);
    }

    @Override
    public CustomerRecentSearchesResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return customerRecentSearchesResponse;
    }

    @Override
    public GetRecentSearchServiceAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new GetRecentSearchServiceAssertion(customerRecentSearchesResponse);
    }
}
