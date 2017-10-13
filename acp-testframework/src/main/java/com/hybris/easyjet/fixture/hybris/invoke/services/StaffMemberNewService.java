package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.StaffMemberAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation.StaffMemberResponse;


/**
 * Created by Siva on 01/12/2017.
 */


public class StaffMemberNewService extends HybrisService implements IService {

    private StaffMemberResponse staffMemberResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    protected StaffMemberNewService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public StaffMemberAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new StaffMemberAssertion(staffMemberResponse);
    }

    @Override
    public StaffMemberResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return staffMemberResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(staffMemberResponse.getEligibilityConfirmation());
    }

    @Override
    protected void mapResponse() {
        staffMemberResponse = restResponse.as(StaffMemberResponse.class);
    }

}


