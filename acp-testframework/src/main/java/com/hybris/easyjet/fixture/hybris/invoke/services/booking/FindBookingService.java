package com.hybris.easyjet.fixture.hybris.invoke.services.booking;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.FindBookingAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.FindBookingsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by daniel on 26/11/2016.
 */
public class FindBookingService extends HybrisService implements IService {

    private FindBookingsResponse findBookingResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public FindBookingService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public FindBookingAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new FindBookingAssertion(findBookingResponse);
    }


    @Override
    public FindBookingsResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return findBookingResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(findBookingResponse.getBookings());
    }

    @Override
    protected void mapResponse() {
        findBookingResponse = restResponse.as(FindBookingsResponse.class);
    }

}
