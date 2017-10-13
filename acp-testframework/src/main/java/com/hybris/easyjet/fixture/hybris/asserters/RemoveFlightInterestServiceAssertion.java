package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation.UpdateConfirmationResponse;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;


/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 8/9/2017.
 */

public class RemoveFlightInterestServiceAssertion extends Assertion<RemoveFlightInterestServiceAssertion, UpdateConfirmationResponse> {

    public RemoveFlightInterestServiceAssertion(UpdateConfirmationResponse updateConfirmationResponse) {
        this.response = updateConfirmationResponse;
    }

    public RemoveFlightInterestServiceAssertion customerIdIsEqualTo(String expectedCustomerId) {

        assertNotNull(response.getFlightInterestConfirmation().getCustomerId());
        assertEquals(expectedCustomerId, response.getFlightInterestConfirmation().getCustomerId());
        return this;
    }

}
