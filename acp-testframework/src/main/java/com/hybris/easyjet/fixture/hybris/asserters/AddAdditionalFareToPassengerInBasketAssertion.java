package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.Errors;

/**
 * Created by Niyi Falade  20/06/17.
 */
public class AddAdditionalFareToPassengerInBasketAssertion extends Assertion<AddAdditionalFareToPassengerInBasketAssertion, Errors> {


    public AddAdditionalFareToPassengerInBasketAssertion(Errors errorResponse) {

        this.response = errorResponse;
    }
}
