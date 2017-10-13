package com.hybris.easyjet.fixture.hybris.invoke.services.booking;

import com.hybris.easyjet.fixture.hybris.asserters.BookingSummaryAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.GetBookingSummaryRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingSummaryResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by rajakm on 02/08/2017.
 */
public class GetBookingSummaryService extends HybrisService {
    private GetBookingSummaryResponse getBookingSummaryResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public GetBookingSummaryService(GetBookingSummaryRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public BookingSummaryAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new BookingSummaryAssertion(getBookingSummaryResponse);
    }

    @Override
    public GetBookingSummaryResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return getBookingSummaryResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(getBookingSummaryResponse.getBookingSummaries());
    }

    @Override
    protected void mapResponse() {
        getBookingSummaryResponse = restResponse.as(GetBookingSummaryResponse.class);
    }
}
