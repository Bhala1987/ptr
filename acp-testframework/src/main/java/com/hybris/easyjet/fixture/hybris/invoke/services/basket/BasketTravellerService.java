package com.hybris.easyjet.fixture.hybris.invoke.services.basket;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.ManageTravellerAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by daniel on 26/11/2016.
 */
public class BasketTravellerService extends HybrisService implements IService {

    private Response travellerUpdateResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public BasketTravellerService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public ManageTravellerAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new ManageTravellerAssertion(travellerUpdateResponse);
    }

    @Override
    public Response getResponse() {
        assertThatServiceCallWasSuccessful();
        return travellerUpdateResponse;
    }

    @Override
    protected void mapResponse() {
        travellerUpdateResponse = restResponse.as(Response.class);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(travellerUpdateResponse.getAdditionalInformations());
    }
}
