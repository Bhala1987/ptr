package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.dependants.DependantsResponse;

/**
 * Created by markphipps on 30/03/2017.
 */
public class GetDependantsAssertion extends Assertion<GetDependantsAssertion, DependantsResponse> {
    private DependantsResponse getDependantsResponse;

    public GetDependantsAssertion(DependantsResponse getDependantsResponse) {
        this.getDependantsResponse = getDependantsResponse;
    }
}
