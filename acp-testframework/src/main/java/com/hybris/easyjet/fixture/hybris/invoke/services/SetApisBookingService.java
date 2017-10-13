package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.*;
import com.hybris.easyjet.fixture.hybris.asserters.SetApisBookingAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.SetApisBookingResponse;

/**
 * Created by robertadigiorgio on 08/06/2017.
 */
public class SetApisBookingService extends HybrisService implements IService {

    private SetApisBookingResponse setApisBookingResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    public SetApisBookingService(IRequest request, String endPoint) {
        super(request, endPoint);
    }


    @Override
    public SetApisBookingResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return setApisBookingResponse;
    }

    @Override
    public SetApisBookingAssertion assertThat() {
        return new SetApisBookingAssertion(setApisBookingResponse) ;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(setApisBookingResponse.getOperationConfirmation().getBookingReference());
    }

    @Override
    protected void mapResponse() {
        setApisBookingResponse = restResponse.as(SetApisBookingResponse.class);
    }

    public int getStatusCode() {
        return super.restResponse.getStatusCode();
    }
}
