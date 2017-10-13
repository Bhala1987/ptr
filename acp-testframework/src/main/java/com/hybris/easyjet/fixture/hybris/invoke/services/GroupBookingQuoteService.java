package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.hybris.asserters.GroupBookingQuoteAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.requests.GroupBookingQuoteRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.GroupBookingQuoteResponse;

public class GroupBookingQuoteService extends HybrisService {

    private GroupBookingQuoteResponse groupBookingQuoteResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public GroupBookingQuoteService(GroupBookingQuoteRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public GroupBookingQuoteAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new GroupBookingQuoteAssertion(groupBookingQuoteResponse);
    }

    @Override
    public GroupBookingQuoteResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return groupBookingQuoteResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(groupBookingQuoteResponse.getQuoteGenerationConfirmation());
    }

    @Override
    protected void mapResponse() {
        groupBookingQuoteResponse = restResponse.as(GroupBookingQuoteResponse.class);
    }

}
