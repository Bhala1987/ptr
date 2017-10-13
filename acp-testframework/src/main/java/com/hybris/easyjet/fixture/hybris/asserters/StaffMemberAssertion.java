package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation.StaffMemberResponse;

/**
 * Created by dwebb on 12/15/2016.
 * assertion wrapper for register customer response object, provides reusable assertions to all tests
 */
public class StaffMemberAssertion extends Assertion<StaffMemberAssertion, StaffMemberResponse> {

    public StaffMemberAssertion(StaffMemberResponse staffMemberResponse) {

        this.response = staffMemberResponse;
    }

}
