package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.database.hybris.models.PassengerTypeDbModel;
import com.hybris.easyjet.fixture.hybris.asserters.models.ExpectedLocalizedName;
import com.hybris.easyjet.fixture.hybris.asserters.models.ExpectedPassenger;
import com.hybris.easyjet.fixture.hybris.invoke.response.PassengerTypesResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Created by daniel on 26/11/2016.
 * assertion wrapper for passenger types response object, provides reusable assertions to all tests
 */
public class PassengerTypesAssertion extends Assertion<PassengerTypesAssertion, PassengerTypesResponse> {

    public PassengerTypesAssertion(PassengerTypesResponse passengerTypesResponse) {

        this.response = passengerTypesResponse;
    }

    public PassengerTypesAssertion thePassengerTypesWereReturned(List<PassengerTypeDbModel> dbPassengers) {

        for (PassengerTypeDbModel expectedPassenger : dbPassengers) {
            assertThat(response.getPassengerTypes()).extracting(
                    "code",
                    "minAge",
                    "maxAge")
                    .contains(tuple(
                            expectedPassenger.getPassengerTypeCode(),
                            expectedPassenger.getMinAge(),
                            expectedPassenger.getMaxAge()
                    ));
        }
        return this;
    }

    public PassengerTypesAssertion thesePassengerTypesWereReturned(List<ExpectedPassenger> expectedPassengers) {

        for (ExpectedPassenger expectedPassenger : expectedPassengers) {
            assertThat(response.getPassengerTypes()).extracting(
                    "code",
                    "minAge",
                    "maxAge",
                    "maxPermitted")
                    .contains(tuple(
                            expectedPassenger.getPassengerCode(),
                            expectedPassenger.getMinAge(),
                            expectedPassenger.getMaxAge(),
                            expectedPassenger.getMaxPermitted()
                    ));
        }
        return this;
    }

    public void thisPassengerTypeHasTheseLocalizedNames(String passengerCodeToCheck, List<ExpectedLocalizedName> expectedLocalizedNames) {

        for (ExpectedLocalizedName expectedLocalizedName : expectedLocalizedNames) {
            assertThat(response.getPassengerTypes()).filteredOn("code", passengerCodeToCheck)
                    .flatExtracting("localizedNames")
                    .extracting("name", "locale")
                    .contains(tuple(
                            expectedLocalizedName.getName(),
                            expectedLocalizedName.getLocale()));
        }
    }

    public PassengerTypesAssertion thesePassengerRulesAreReturned(){

        assertThat(response.getPassengerRules().getAdultInfantOnSeatRatio().contains("1:2"))
                .withFailMessage("Rules have changed expected a rule which equals to 1:2")
                .isTrue();

        return this;


    }
}
