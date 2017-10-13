package com.hybris.easyjet.fixture.hybris.invoke.services.booking;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.BookingAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by daniel on 26/11/2016.
 */
public class GetBookingService extends HybrisService implements IService {

    private GetBookingResponse getBookingResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public GetBookingService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public BookingAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new BookingAssertion(getBookingResponse);
    }

    @Override
    public GetBookingResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return getBookingResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(getBookingResponse.getBookingContext());
    }

    @Override
    protected void mapResponse() {
        getBookingResponse = restResponse.as(GetBookingResponse.class);
    }
    public int getStatusCode(){
        return restResponse.getStatusCode();
    }
}
