package com.hybris.easyjet.fixture.hybris.invoke.services.booking;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.GetCommentTypesAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetCommentTypesResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 05/07/2017.
 */
public class GetCommentTypesService extends HybrisService implements IService {

    private GetCommentTypesResponse getCommentTypesResponse;

    /**
     * a service comprises a request, a client and an endpoint
     *
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    public GetCommentTypesService(IRequest request, String endPoint) {
        super(request, endPoint);
    }


    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(getCommentTypesResponse.getCommentContexts());
    }

    @Override
    protected void mapResponse() {
        getCommentTypesResponse = restResponse.as(GetCommentTypesResponse.class);
    }

    @Override
    public GetCommentTypesResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return getCommentTypesResponse;
    }

    @Override
    public GetCommentTypesAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new GetCommentTypesAssertion(getCommentTypesResponse);
    }
}