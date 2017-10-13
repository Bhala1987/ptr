package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by daniel on 26/11/2016.
 * assertion wrapper for manage traveller response object, provides reusable assertions to all tests
 */
public class ManageTravellerAssertion extends Assertion<ManageTravellerAssertion, Response> {

    public ManageTravellerAssertion(Response travellerUpdateResponse) {

        this.response = travellerUpdateResponse;
    }

    public ManageTravellerAssertion verifyWarningMessage(String warning, boolean present) {
        assertThat(
                this.response.getAdditionalInformations().stream().anyMatch(
                        warnings -> warnings.getCode().equalsIgnoreCase(warning)
                )
        ).withFailMessage(
                "EXPECTED : " + warning
        ).isEqualTo(present);

        return this;
    }

}
