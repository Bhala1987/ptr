package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IResponse;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.GenerateBoardingPassAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.GenerateBoardingPassResponse;


/**
 * Created by albertowork on 5/24/17.
 */
public class GenerateBoardingPassService extends HybrisService implements IService {
    private GenerateBoardingPassResponse boardingPassResponseResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    public GenerateBoardingPassService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public IResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return boardingPassResponseResponse;
    }

    @Override
    public GenerateBoardingPassAssertion assertThat() {
        return new GenerateBoardingPassAssertion(boardingPassResponseResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(boardingPassResponseResponse);
    }

    @Override
    protected void mapResponse() {
        boardingPassResponseResponse = restResponse.as(GenerateBoardingPassResponse.class);
    }
}
