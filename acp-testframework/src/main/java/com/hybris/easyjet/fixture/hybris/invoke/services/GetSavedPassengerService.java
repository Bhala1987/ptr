package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.GetSavedPassengerAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.savedpassenger.GetSavedPassengerResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by giuseppecioce on 16/02/2017.
 */
@Getter
@Setter
public class GetSavedPassengerService extends HybrisService implements IService {

    private GetSavedPassengerResponse getSavedPassengerResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    protected GetSavedPassengerService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public GetSavedPassengerAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new GetSavedPassengerAssertion(getSavedPassengerResponse);
    }


    @Override
    public GetSavedPassengerResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return getSavedPassengerResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(getSavedPassengerResponse.getSavedPassengers());
    }

    @Override
    protected void mapResponse() {
        getSavedPassengerResponse = restResponse.as(GetSavedPassengerResponse.class);
    }

}