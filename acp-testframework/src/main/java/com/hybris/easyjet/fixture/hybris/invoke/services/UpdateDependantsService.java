package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.UpdateDependantsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.dependants.operationconfirmation.UpdateDependantsResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by markphipps on 24/03/2017.
 */
@Getter
@Setter
public class UpdateDependantsService extends HybrisService implements IService {
    private UpdateDependantsResponse updateDependantsResponse;


    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    protected UpdateDependantsService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public UpdateDependantsResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return updateDependantsResponse;
    }

    @Override
    public UpdateDependantsAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new UpdateDependantsAssertion(updateDependantsResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(updateDependantsResponse.getUpdateConfirmation());
    }

    @Override
    protected void mapResponse() {
        updateDependantsResponse = restResponse.as(UpdateDependantsResponse.class);
    }
}
