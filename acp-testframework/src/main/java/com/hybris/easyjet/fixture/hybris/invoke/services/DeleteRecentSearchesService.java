package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IResponse;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.DeleteRecentSearchesAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.DeleteRecentSearchesResponse;

/**
 * Created by albertowork on 7/7/17.
 */
public class DeleteRecentSearchesService extends HybrisService implements IService {
    private DeleteRecentSearchesResponse deleteRecentSearchesResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    public DeleteRecentSearchesService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public IResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return deleteRecentSearchesResponse;
    }

    @Override
    public DeleteRecentSearchesAssertion assertThat() {
        return new DeleteRecentSearchesAssertion(deleteRecentSearchesResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(deleteRecentSearchesResponse);
    }

    @Override
    protected void mapResponse() {
        deleteRecentSearchesResponse = restResponse.as(DeleteRecentSearchesResponse.class);
    }
}
