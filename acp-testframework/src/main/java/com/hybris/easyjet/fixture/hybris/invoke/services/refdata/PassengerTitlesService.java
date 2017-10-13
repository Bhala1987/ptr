package com.hybris.easyjet.fixture.hybris.invoke.services.refdata;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.PassengerTitlesAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.PassengerTitlesResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by daniel on 26/11/2016.
 */
public class PassengerTitlesService extends HybrisService implements IService {

    private PassengerTitlesResponse passengerTitlesResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public PassengerTitlesService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public PassengerTitlesAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new PassengerTitlesAssertion(passengerTitlesResponse);
    }

    @Override
    public PassengerTitlesResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return passengerTitlesResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(passengerTitlesResponse.getPassengerTitles());
    }

    @Override
    protected void mapResponse() {
        passengerTitlesResponse = restResponse.as(PassengerTitlesResponse.class);
    }
}
