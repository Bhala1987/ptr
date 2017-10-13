package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.PassengerTitlesResponse;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by daniel on 26/11/2016.
 * assertion wrapper for passenger titles response object, provides reusable assertions to all tests
 */
public class PassengerTitlesAssertion extends Assertion<PassengerTitlesAssertion, PassengerTitlesResponse> {

    public PassengerTitlesAssertion(PassengerTitlesResponse passengerTitlesResponse) {

        this.response = passengerTitlesResponse;
    }

    public PassengerTitlesAssertion theNumberOfPassengerTitlesReturnedWas(int expectedNumberOfPassengerTitles) {

        assertThat(response.getPassengerTitles().size()).isEqualTo(expectedNumberOfPassengerTitles);
        return this;
    }

    public PassengerTitlesAssertion titlesAreAsExpected(List<String> expectedTitles) {

        Object[] expectedTitlesArray = expectedTitles.toArray(new String[expectedTitles.size()]);
        assertThat(response.getPassengerTitles()).extracting("code")
                .containsExactlyInAnyOrder(expectedTitlesArray);
        return this;
    }

    public PassengerTitlesAssertion allLocalisationDataIsPresent(List<String> expectedLocales) {

        Object[] expectedLocalesArray = expectedLocales.toArray(new String[expectedLocales.size()]);
        for (PassengerTitlesResponse.PassengerTitle actualPassengerTitle : response.getPassengerTitles()) {
            assertThat(actualPassengerTitle.getLocalizedNames()).extracting("locale")
                    .containsExactlyInAnyOrder(expectedLocalesArray);
        }
        return this;
    }

    public PassengerTitlesAssertion allLocalisationDataIsPresentForLanguage(String languageToCheck, List<String> expectedLocales) {

        Object[] expectedLocalesArray = expectedLocales.toArray(new String[expectedLocales.size()]);
        for (PassengerTitlesResponse.PassengerTitle actualPassengerTitle : response.getPassengerTitles()) {
            assertThat(actualPassengerTitle.getLocalizedNames()).filteredOn("locale", languageToCheck)
                    .extracting("locale").containsExactlyInAnyOrder(expectedLocalesArray);
        }
        return this;
    }
}
