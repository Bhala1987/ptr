package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.CreateCompensationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.Response;

/**
 * Created by Niyi Falade on 18/09/17.
 */
public class CreateCompensationAssertion extends Assertion<CreateCompensationAssertion, CreateCompensationResponse> {

    public CreateCompensationAssertion(CreateCompensationResponse CreateCompensationResponse ){
        this.response = CreateCompensationResponse;
    }
}
