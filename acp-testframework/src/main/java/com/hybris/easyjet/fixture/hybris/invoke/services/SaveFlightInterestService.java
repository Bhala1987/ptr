package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.FlightInterestErrorAssertion;
import com.hybris.easyjet.fixture.hybris.asserters.SaveFlightInterestServiceAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ManageFlightInterestRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation.CustomerFlightInterestResponse;

/**
 * Created by ptr-kvijayapal on 1/23/2017.
 */
public class SaveFlightInterestService extends HybrisService implements IService {

    private CustomerFlightInterestResponse customerFlightInterestResponse;

    public SaveFlightInterestService(ManageFlightInterestRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public FlightInterestErrorAssertion assertThatErrors() {
        assertThatServiceCallWasNotSuccessful();
        return new FlightInterestErrorAssertion(getErrors());
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(customerFlightInterestResponse);
    }

    @Override
    protected void mapResponse() {
        customerFlightInterestResponse = restResponse.as(CustomerFlightInterestResponse.class);
    }

    @Override
    public CustomerFlightInterestResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return customerFlightInterestResponse;
    }

    @Override
    public SaveFlightInterestServiceAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new SaveFlightInterestServiceAssertion(customerFlightInterestResponse);
    }

    public SaveFlightInterestServiceAssertion wasSuccessful() {
        assertThatServiceCallWasSuccessful();
        return new SaveFlightInterestServiceAssertion(customerFlightInterestResponse);
    }
}
