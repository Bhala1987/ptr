package com.hybris.easyjet.fixture.hybris.invoke.services.booking;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.AmendPassengerSSRAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.AmendPassengerSSRResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * AmendPassengerSSRService class, it updates basket the basic details
 */
public class AmendPassengerSSRService extends HybrisService implements IService {

    private AmendPassengerSSRResponse amendPassengerSSRResponse;

    /**
     * AmendPassengerSSRService, parameterize constructor
     * @param request
     * @param endPoint
     */
    public AmendPassengerSSRService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public AmendPassengerSSRResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return amendPassengerSSRResponse;
    }

    @Override
    public AmendPassengerSSRAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new AmendPassengerSSRAssertion(amendPassengerSSRResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(amendPassengerSSRResponse.getOperationConfirmation());
    }

    @Override
    protected void mapResponse() {
        amendPassengerSSRResponse = restResponse.as(AmendPassengerSSRResponse.class);
    }
}
