package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IResponse;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.BookingDocumentsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.BookingDocumentsResponse;

/**
 * Created by Alberto
 */
public class BookingDocumentsService extends HybrisService implements IService {

    private BookingDocumentsResponse bookingDocumentsResponse;

    /**
     * a service comprises a request, a client and an endpoint
     *
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    protected BookingDocumentsService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public IResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return bookingDocumentsResponse;
    }

    @Override
    public BookingDocumentsAssertion assertThat() {
        return new BookingDocumentsAssertion(bookingDocumentsResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(bookingDocumentsResponse);
    }

    @Override
    protected void mapResponse() {  bookingDocumentsResponse = restResponse.as(BookingDocumentsResponse.class);  }
}
