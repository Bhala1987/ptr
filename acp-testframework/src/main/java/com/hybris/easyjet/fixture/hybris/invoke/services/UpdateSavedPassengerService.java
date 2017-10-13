package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.UpdateSavedPassengerAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.savedpassenger.operationconfirmation.UpdateSavedPassengerResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by giuseppecioce on 16/02/2017.
 */
@Getter
@Setter
public class UpdateSavedPassengerService extends HybrisService implements IService {

    private UpdateSavedPassengerResponse updateSavedPassengerResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    protected UpdateSavedPassengerService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public UpdateSavedPassengerAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new UpdateSavedPassengerAssertion(updateSavedPassengerResponse);
    }


    @Override
    public UpdateSavedPassengerResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return updateSavedPassengerResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(updateSavedPassengerResponse.getUpdateConfirmation());
    }

    @Override
    protected void mapResponse() {
        updateSavedPassengerResponse = restResponse.as(UpdateSavedPassengerResponse.class);
    }

}
