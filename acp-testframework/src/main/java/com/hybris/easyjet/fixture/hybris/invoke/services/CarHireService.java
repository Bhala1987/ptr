package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IResponse;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.CarHireAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.carhire.CarHireResponse;

/**
 * Created by stalluri on 26/07/2017.
 */
public class CarHireService extends HybrisService implements IService {

    private CarHireResponse carHireResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    public CarHireService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(carHireResponse);
    }
    @Override
    public CarHireResponse getResponse() {
        return carHireResponse;
    }
    @Override
    public CarHireAssertion assertThat() {
        return new CarHireAssertion(carHireResponse);
    }
    @Override
    protected void mapResponse() {
        carHireResponse= restResponse.as(CarHireResponse.class);
    }

    public int getStatusCode() {
        return super.restResponse.getStatusCode();
    }
    @Override
    protected void assertThatServiceCallWasSuccessful() {
        if (restResponse.getStatusCode() == 200) {
            successful = true;
        }
    }
}