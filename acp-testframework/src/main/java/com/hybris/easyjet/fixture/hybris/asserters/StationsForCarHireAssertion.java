package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.StationsForCarHireResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by bhala.
 */
public class StationsForCarHireAssertion extends Assertion<StationsForCarHireAssertion, StationsForCarHireResponse> {

    public StationsForCarHireAssertion(StationsForCarHireResponse stationsForCarHireResponse) {

        this.response = stationsForCarHireResponse;
    }

    public StationsForCarHireAssertion stationsForCarHireReturned() {

        assertThat(response.getStations()).size().isGreaterThan(0);
        return this;
    }
}