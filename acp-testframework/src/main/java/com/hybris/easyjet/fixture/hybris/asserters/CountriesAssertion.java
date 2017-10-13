package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.asserters.models.ExpectedCountry;
import com.hybris.easyjet.fixture.hybris.asserters.models.ExpectedLocalizedName;
import com.hybris.easyjet.fixture.hybris.invoke.response.CountriesResponse;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Created by daniel on 26/11/2016.
 * assertion wrapper for countries response object, provides reusable assertions to all tests
 */
public class CountriesAssertion extends Assertion<CountriesAssertion, CountriesResponse> {

    public CountriesAssertion(CountriesResponse countriesResponse) {

        this.response = countriesResponse;
    }

    public CountriesAssertion thisCountryHasTheseLocalizedNames(String countryToCheck, List<ExpectedLocalizedName> expectedLocalizedNames) {

        for (ExpectedLocalizedName expectedLocalizedName : expectedLocalizedNames) {
            assertThat(response.getCountries()).filteredOn("code", countryToCheck).flatExtracting("localizedNames")
                    .extracting("name", "locale")
                    .contains(tuple(
                            expectedLocalizedName.getName(),
                            expectedLocalizedName.getLocale()));
        }
        return this;
    }

    public CountriesAssertion allCountriesAreIdentifiedAsBeingActiveOrInactive() {

        assertThat(response.getCountries()).extracting("isActive").hasOnlyElementsOfType(Boolean.class);
        return this;
    }

    public CountriesAssertion thereWereCountriesReturned() {

        assertThat(response.getCountries()).size().isGreaterThan(0);
        return this;
    }

    public CountriesAssertion theseCountriesWereAllReturned(List<String> expectedCountries) {

        Object[] expectedCountriesArray = expectedCountries.toArray(new String[expectedCountries.size()]);
        assertThat(response.getCountries())
                .extracting("code")
                .containsExactlyInAnyOrder(expectedCountriesArray);
        return this;
    }

    public CountriesAssertion theseCountriesWereReturned(List<ExpectedCountry> expectedCountries) {

        for (ExpectedCountry expectedCountry : expectedCountries) {
            //TODO more work here to check other contents from the expected object
            assertThat(response.getCountries())
                    .extracting("code", "isActive")
                    .contains(tuple(
                            expectedCountry.getCountryCode(),
                            expectedCountry.isActive()));
        }
        return this;
    }

    public CountriesAssertion theseCountriesWereNotReturned(List<String> inactiveCountriesInTheHybrisDatabase) {

        assertThat(response.getCountries())
                .extracting("code")
                .doesNotContain(inactiveCountriesInTheHybrisDatabase);
        return this;
    }

    public CountriesAssertion theCountryContainsTheInternationalDiallingCodeIfStoredInDb(final Map<String, String> diallingCodeForCountryCode) {

        response.getCountries().stream().forEach(country -> {

            if (StringUtils.isNotBlank(diallingCodeForCountryCode.get(country.getCode()))) {


                assertThat(StringUtils.isNotBlank(country.getDiallingCode()) && country.getDiallingCode().equalsIgnoreCase(diallingCodeForCountryCode.get(country.getCode())))
                        .withFailMessage("The dialling type is not populated in the getCountries response")
                        .isTrue();

            }

        });
        return this;
    }

}
