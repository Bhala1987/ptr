package com.hybris.easyjet.fixture.hybris.invoke.services.refdata;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.PassengerTypesAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.PassengerTypesResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by daniel on 26/11/2016.
 */
public class PassengerTypesService extends HybrisService implements IService {

    private PassengerTypesResponse passengerTypesResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public PassengerTypesService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public PassengerTypesAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new PassengerTypesAssertion(passengerTypesResponse);
    }

    @Override
    public PassengerTypesResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return passengerTypesResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(passengerTypesResponse.getPassengerTypes());
    }

    @Override
    protected void mapResponse() {
        passengerTypesResponse = restResponse.as(PassengerTypesResponse.class);
    }
}
