package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.ResetPasswordAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation.ResetPasswordResponse;

/**
 * Created by giuseppecioce on 01/03/2017.
 */
public class ResetPasswordService extends HybrisService implements IService {
    private ResetPasswordResponse resetPasswordResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    public ResetPasswordService(IRequest request, String endPoint) {
        super(request, endPoint);
    }


    @Override
    public ResetPasswordResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return resetPasswordResponse;
    }

    @Override
    public ResetPasswordAssertion assertThat() {
        return new ResetPasswordAssertion(resetPasswordResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(resetPasswordResponse.getResetPasswordConfirmation());
    }

    @Override
    protected void mapResponse() {
        resetPasswordResponse = restResponse.as(ResetPasswordResponse.class);
    }
}
