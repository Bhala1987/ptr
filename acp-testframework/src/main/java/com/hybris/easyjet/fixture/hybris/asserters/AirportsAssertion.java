package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.GetAirportsResponse;
import lombok.NoArgsConstructor;
import net.thucydides.core.annotations.Step;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by daniel on 26/11/2016.
 * assertion wrapper for airports response object, provides reusable assertions to all tests
 */
@NoArgsConstructor
public class AirportsAssertion extends Assertion<AirportsAssertion, GetAirportsResponse> {

    /**
     * @param getAirportsResponse the response object from the get Airports service
     */
    public AirportsAssertion(GetAirportsResponse getAirportsResponse) {
        this.response = getAirportsResponse;
    }

    public void setResponse(GetAirportsResponse getAirportsResponse) {
        this.response = getAirportsResponse;
    }

    @Step("All active airports are returned")
    public AirportsAssertion allTheActiveAirportsAreReturned(List<String> activeAirports) {
        assertThat(response.getAirports().stream()
                .map(GetAirportsResponse.Airport::getCode)
                .collect(Collectors.toList()))
                .withFailMessage("Some of the active airports have not been returned")
                .containsExactlyInAnyOrder(activeAirports.toArray(new String[activeAirports.size()]));
        return this;
    }

    @Step("None of the inactive airports is returned")
    public AirportsAssertion noneInactiveAirportsAreReturned(List<String> inactiveAirports) {
        assertThat(response.getAirports().stream()
                .map(GetAirportsResponse.Airport::getCode)
                .collect(Collectors.toList()))
                .withFailMessage("Some of the active airports have not been returned")
                .doesNotContainAnyElementsOf(inactiveAirports);
        return this;
    }

    @Step("All the airports returned have a country specified")
    public AirportsAssertion allTheAirportsHaveACountry() {
        assertThat(response.getAirports().stream()
                .filter(airport -> !Objects.isNull(airport.getCountry()))
                .collect(Collectors.toList()).size())
                .withFailMessage("Not all the airports have a country specified")
                .isEqualTo(response.getAirports().size());
        return this;
    }

    @Step("All the airports returned have a currency specified")
    public AirportsAssertion allTheAirportsHaveACurrency() {
        assertThat(response.getAirports().stream()
                .filter(airport -> !Objects.isNull(airport.getDefaultCurrency()))
                .collect(Collectors.toList()).size())
                .withFailMessage("Not all the airports have a currency specified")
                .isEqualTo(response.getAirports().size());
        return this;
    }

    @Step("All the airports returned have a online check in availability specified")
    public AirportsAssertion allTheAirportsHaveAOnlineCheckInAvailability() {
        assertThat(response.getAirports().stream()
                .filter(airport -> !Objects.isNull(airport.getIsOnlineCheckInAvailable()))
                .collect(Collectors.toList()).size())
                .withFailMessage("Not all the airports have a online check in specified")
                .isEqualTo(response.getAirports().size());
        return this;
    }

    @Step("All the airports returned have a mobile check in availability specified")
    public AirportsAssertion allTheAirportsHaveAMobileCheckInAvailability() {
        assertThat(response.getAirports().stream()
                .filter(airport -> !Objects.isNull(airport.getIsMobileCheckInAvailable()))
                .collect(Collectors.toList()).size())
                .withFailMessage("Not all the airports have a mobile check in specified")
                .isEqualTo(response.getAirports().size());
        return this;
    }

    @Step("All the airports returned have a localized name specified")
    public AirportsAssertion allTheAirportsHaveALocalizedName() {
        assertThat(response.getAirports().stream()
                .filter(airport -> !Objects.isNull(airport.getLocalizedNames()))
                .collect(Collectors.toList()).size())
                .withFailMessage("Not all the airports have a localized name specified")
                .isEqualTo(response.getAirports().size());
        return this;
    }

    public AirportsAssertion allTheAirportsHaveATimeZone() {
        assertThat(response.getAirports().stream()
                .filter(airport -> !Objects.isNull(airport.getTimeZone()))
                .collect(Collectors.toList()).size())
                .withFailMessage("Not all the airports have a timezone specified")
                .isEqualTo(response.getAirports().size());
        return this;
    }
}