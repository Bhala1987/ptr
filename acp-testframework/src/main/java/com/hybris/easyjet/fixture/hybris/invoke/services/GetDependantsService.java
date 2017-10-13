package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.GetDependantsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.dependants.DependantsResponse;

/**
 * Created by markphipps on 29/03/2017.
 */
public class GetDependantsService extends HybrisService implements IService {
    private DependantsResponse getDependantsResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    protected GetDependantsService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public DependantsResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return getDependantsResponse;
    }

    @Override
    public GetDependantsAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new GetDependantsAssertion(getDependantsResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(getDependantsResponse.getDependant());
    }

    @Override
    protected void mapResponse() {
        getDependantsResponse = restResponse.as(DependantsResponse.class);
    }
}
