package com.hybris.easyjet.fixture.hybris.invoke.services.basket;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.AddInfantOnLapAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.operationconfirmation.AddInfantOnLapResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by vijayapalkayyam on 27/06/2017.
 */
public class AddInfantOnLapService extends HybrisService implements IService {
    AddInfantOnLapResponse addInfantOnLapResponse;

    public AddInfantOnLapService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public AddInfantOnLapResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return addInfantOnLapResponse;
    }

    @Override
    public AddInfantOnLapAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new AddInfantOnLapAssertion(addInfantOnLapResponse);
    }


    @Override
    protected void mapResponse() {
        addInfantOnLapResponse = restResponse.as(AddInfantOnLapResponse.class);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(addInfantOnLapResponse.getOperationConfirmation());
    }
}
