package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.AdditionalSeatReasonsResponse;
import static org.assertj.core.api.Assertions.assertThat;

public class AdditionalSeatReasonsAssertion extends Assertion<AdditionalSeatReasonsAssertion, AdditionalSeatReasonsResponse> {

    private AdditionalSeatReasonsResponse additionalSeatReasonsResponse;

    public AdditionalSeatReasonsAssertion(AdditionalSeatReasonsResponse additionalSeatReasonsResponse) {
        this.response = additionalSeatReasonsResponse;
    }

    public AdditionalSeatReasonsAssertion theResponseNotIsEmpty() {
        assertThat(response.getAdditionalSeatReasons()).withFailMessage("The response of getAdditionalSeatReasons is empty").isNotEmpty();
        return this;
    }
}
