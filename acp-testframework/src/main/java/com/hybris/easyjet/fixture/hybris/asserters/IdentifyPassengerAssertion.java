package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.IdentifyPassengerResponse;
import lombok.NoArgsConstructor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by rajakm on 11/10/2017.
 */
@NoArgsConstructor
public class IdentifyPassengerAssertion extends Assertion<IdentifyPassengerAssertion, IdentifyPassengerResponse> {
    public IdentifyPassengerAssertion(IdentifyPassengerResponse identifyPassengerResponse) {
        this.response = identifyPassengerResponse;
    }

    public void setResponse(IdentifyPassengerResponse identifyPassengerResponse) {
        this.response = identifyPassengerResponse;
    }

    public IdentifyPassengerAssertion theSearchWasSuccesful() {

        assertThat(response.getAuthentication().getAccessToken()).isNotNull();
        assertThat(response.getAuthentication().getExpiresIn()).isNotNull();
        assertThat(response.getAuthentication().getRefreshToken()).isNotEmpty();
        assertThat(response.getAuthentication().getScope()).isNotEmpty();
        assertThat(response.getAuthentication().getTokenType()).isEqualTo("bearer");
        return this;
    }

    public IdentifyPassengerAssertion theResultListContainsValidSearch(String expectedSurName){
        assertThat(response.getMatchingPassengers())
                .withFailMessage("The expected surname is not in the response")
                .allMatch(p -> p.getLastName().equals(expectedSurName));
        return this;
    }
}
