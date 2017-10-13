package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.hybris.asserters.ValidateMembershipAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ValidateMembershipRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.validationconfirmation.ValidateMembershipResponse;

/**
 * Created by rajakm on 21/08/2017.
 */
public class ValidateMembershipNumberService  extends HybrisService {
    private ValidateMembershipResponse validateMembershipResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public ValidateMembershipNumberService(ValidateMembershipRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public ValidateMembershipAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new ValidateMembershipAssertion(validateMembershipResponse);
    }

    @Override
    public ValidateMembershipResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return validateMembershipResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(validateMembershipResponse.getValidationConfirmation());
    }

    @Override
    protected void mapResponse() {
        validateMembershipResponse = restResponse.as(ValidateMembershipResponse.class);
    }
}
