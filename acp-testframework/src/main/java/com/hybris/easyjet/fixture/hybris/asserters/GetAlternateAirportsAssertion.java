package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.GetAlternateAirportsResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by robertadigiorgio on 10/03/2017.
 */
public class GetAlternateAirportsAssertion extends Assertion<GetAlternateAirportsAssertion, GetAlternateAirportsResponse> {

    public GetAlternateAirportsAssertion(GetAlternateAirportsResponse getAlternateAirportsResponse) {

        this.response = getAlternateAirportsResponse;
    }

    public GetAlternateAirportsAssertion theDistanceIsLessOfMaxDistance() {

        response.getAlternateAirports().getAirports().forEach(
                airport -> {
                    assertThat(airport.getDistance()).isLessThanOrEqualTo(response.getAlternateAirports().getMaxDistance());
                }
        );
        return this;
    }

    public GetAlternateAirportsAssertion theAiportMustBeEmpty() {

        assertThat(response.getAlternateAirports().getAirports().isEmpty());
        return this;
    }

    public GetAlternateAirportsAssertion theDistanceIsSorted() {

        assertThat(!response.getAlternateAirports().getAirports().isEmpty());

        List<GetAlternateAirportsResponse.Airport> airports = response.getAlternateAirports().getAirports();
        Integer firstDistance = response.getAlternateAirports()
                .getAirports()
                .get(0)
                .getDistance();

        for (GetAlternateAirportsResponse.Airport airport : airports) {
            assertThat(airport.getDistance() >= firstDistance);
            firstDistance = airport.getDistance();
        }
        return this;
    }

    public GetAlternateAirportsAssertion theAirportAreSortedAlphabetically() {

        assertThat(!response.getAlternateAirports().getAirports().isEmpty());

        List<GetAlternateAirportsResponse.Airport> airports = response.getAlternateAirports().getAirports();
        String firstAirport = response.getAlternateAirports()
                .getAirports()
                .get(0)
                .getAirportCode();

        for (GetAlternateAirportsResponse.Airport airport : airports) {
            assertThat(airport.getAirportCode().compareToIgnoreCase(firstAirport)).isGreaterThanOrEqualTo(0);
            firstAirport = airport.getAirportCode();
        }
        return this;
    }

}
