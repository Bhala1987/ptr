package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.UpdatePasswordAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation.UpdatePasswordResponse;

/**
 * Created by robertadigiorgio on 09/02/2017.
 */
public class UpdatePasswordService extends HybrisService implements IService {

    private UpdatePasswordResponse updatePasswordResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    public UpdatePasswordService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(updatePasswordResponse.getUpdateConfirmation());
    }

    @Override
    protected void mapResponse() {
        updatePasswordResponse = restResponse.as(UpdatePasswordResponse.class);
    }

    @Override
    public UpdatePasswordResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return updatePasswordResponse;
    }

    @Override
    public UpdatePasswordAssertion assertThat() {
        return new UpdatePasswordAssertion(updatePasswordResponse);
    }
}
