package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IResponse;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.GetCustomerApisAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.GetCustomerAPIsResponse;

/**
 * Created by vijayapalkayyam on 29/03/2017.
 */
public class GetCustomerAPIsService extends HybrisService implements IService {

    private GetCustomerAPIsResponse getAPIResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    public GetCustomerAPIsService(IRequest request, String endPoint) {
        super(request, endPoint);
    }


    @Override
    public IResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return getAPIResponse;
    }

    @Override
    public GetCustomerApisAssertion assertThat() {
        return new GetCustomerApisAssertion(getAPIResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(getAPIResponse.getIdentityDocuments());
    }

    @Override
    protected void mapResponse() {
        getAPIResponse = restResponse.as(GetCustomerAPIsResponse.class);
    }

    public int getStatus() {
        return restResponse.getStatusCode();
    }
}