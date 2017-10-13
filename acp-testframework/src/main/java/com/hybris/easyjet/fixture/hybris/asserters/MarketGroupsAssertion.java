package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.asserters.models.ExpectedMarketGroup;
import com.hybris.easyjet.fixture.hybris.invoke.response.MarketGroupsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.LocalizedValue;
import org.assertj.core.api.Assertions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Created by daniel on 26/11/2016.
 * assertion wrapper for market groups response object, provides reusable assertions to all tests
 */
public class MarketGroupsAssertion extends Assertion<MarketGroupsAssertion, MarketGroupsResponse> {

    public MarketGroupsAssertion(MarketGroupsResponse marketGroupsResponse) {

        this.response = marketGroupsResponse;
    }

    public MarketGroupsAssertion theseMarketGroupsWereReturned(List<ExpectedMarketGroup> expectedMarketGroups) {

        for (ExpectedMarketGroup expectedMarketGroup : expectedMarketGroups) {
            assertThat(response.getMarketGroups())
                    .extracting(
                            "code",
                            "type",
                            "status")
                    .contains(tuple(
                            expectedMarketGroup.getCode(),
                            expectedMarketGroup.getType(),
                            expectedMarketGroup.getStatus()));
        }
        return this;
    }

    public MarketGroupsAssertion thisMarketGroupHasTheseLocalizedDescriptions(String marketGroupToCheck, List<LocalizedValue> expectedLocalizedDescriptions) {

        for (LocalizedValue expectedLocalizedDescription : expectedLocalizedDescriptions) {
            Assertions.assertThat(response.getMarketGroups())
                    .filteredOn("code", marketGroupToCheck).flatExtracting("localizedDescriptions")
                    .extracting("value", "name")
                    .contains(tuple(
                            expectedLocalizedDescription.getValue(),
                            expectedLocalizedDescription.getLocale()));
        }
        return this;
    }
}
