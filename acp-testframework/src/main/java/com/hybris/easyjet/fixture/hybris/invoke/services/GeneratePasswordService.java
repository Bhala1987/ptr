package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.GeneratePasswordAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation.GeneratePasswordResponse;

/**
 * Created by robertadigiorgio on 13/02/2017.
 */
public class GeneratePasswordService extends HybrisService implements IService {
    private GeneratePasswordResponse generatePasswordResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    public GeneratePasswordService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(generatePasswordResponse.getGeneratePasswordConfirmation());
    }

    @Override
    protected void mapResponse() {
        generatePasswordResponse = restResponse.as(GeneratePasswordResponse.class);
    }

    @Override
    public GeneratePasswordResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return generatePasswordResponse;
    }

    @Override
    public GeneratePasswordAssertion assertThat() {
        return new GeneratePasswordAssertion(generatePasswordResponse);
    }

}
