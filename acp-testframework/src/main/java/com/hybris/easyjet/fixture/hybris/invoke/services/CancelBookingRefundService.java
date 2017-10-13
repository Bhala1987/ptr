package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.asserters.CancelBookingRefundAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.CancelBookingRefundResponse;

/**
 * Created by Niyi Falade on 24/07/17.
 */
public class CancelBookingRefundService extends HybrisService {

    private CancelBookingRefundResponse cancelBookingRefundResponse;

    protected CancelBookingRefundService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public CancelBookingRefundResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return cancelBookingRefundResponse;
    }

    @Override
    public CancelBookingRefundAssertion assertThat() {
        return new CancelBookingRefundAssertion(cancelBookingRefundResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(cancelBookingRefundResponse);
    }

    @Override
    protected void mapResponse() {
        cancelBookingRefundResponse = restResponse.as(CancelBookingRefundResponse.class);
    }
    public int getStatusCode(){
        return restResponse.getStatusCode();
    }
}
