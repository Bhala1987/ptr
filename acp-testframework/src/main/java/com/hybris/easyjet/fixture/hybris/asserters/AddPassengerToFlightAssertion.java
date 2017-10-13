package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.basket.AddPassengerToFlightResponse;

public class AddPassengerToFlightAssertion extends Assertion<AddPassengerToFlightAssertion, AddPassengerToFlightResponse> {


    public AddPassengerToFlightAssertion(AddPassengerToFlightResponse addPassengerToFlightResponse) {

        this.response = addPassengerToFlightResponse;
    }
}
